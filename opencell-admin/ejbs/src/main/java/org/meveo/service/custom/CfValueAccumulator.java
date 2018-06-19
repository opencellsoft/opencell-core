package org.meveo.service.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * Handles CF value accumulation rules
 * 
 * <pre>
Given the following CFT structure:

Entity:       CFTS
Provider      A,B
Customer
CA            B,C
BA            A,C
UA
Subscription  A,B,C


CF value change is propagated downwards:
Provider (A) > BA(A) >> Sub(A)
Provider (B) > CA (B) >> Sub(B)
CA (B) > Sub (B)
CA (C)> BA (C) >> Sub (C)
BA (A)> Sub (A)
BA (C) > Sub (C)

CF value is accumulated from:

CA (B) &lt; P (B)
BA (A) &lt; P (A)
BA (C) &lt; CA (C)
Sub (A) &lt; BA (A)
Sub (B) &lt; CA(B)
Sub (C) &lt; BA(C)

The propagation logic is determined from:
     Class reflection
     Same code CFT definitions

The logic for CF value accumulation is:

For each of CFTs defined on entity which CF value is being modified (any CRUD) Eg Provider:
    Reconstruct entity's own accumulative CF field value for CFT in question by following "accumulated from" scheme
    Loop until no entities to update going downward:
        Find all corresponding entities following the "propagated downwards" scheme one level at a time and:
           Clear accumulative CF Field value for CFT in question
           Reconstruct entity's accumulative CF field value for CFT in question by following "accumulated from" scheme

Accumulation logic:
For single value fields value from parent is set if no current value is present
For list, map, matrix value fields, parent value can be merged or ignored if current value is present - For  map, matrix value fields, parent value shall be merged if current value is present -  but need to verify current implementation.
For versioned fields:
    Non-calendar versioning:    
        Parent level values are added with higher priority. 
    Calendar versioning:
       Parent values are ignored for single value field if period already exists.  
       Matrix and map values are merged for same period.



The above script should be prepared dynamically whenever CFT definition is added, removed or changed (appliesTo value ??, maybe some other field). 
Script could optionally take care of appliesTo logic. On the other hand its probably not needed, as entity wont be able to use this CFT anyway, so value will never be calculated.
Script can be compiled as a Java class or as DB procedure/trigger (applies to in this case is complicated)
And if needed, accumulative values should be recalculated following the level downwards.
 * </pre>
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class CfValueAccumulator {

    /**
     * Where to cf values should be propagated to. Key is an entity class and value is a list of entity classes that should receive updated CF value
     */
    protected static Map<Class<?>, List<Class<?>>> treePropagateTo = new HashMap<>();

    /**
     * Entity classes that only propagate values downwards and do not accumulate custom field values from other entities
     */
    protected static List<Class<?>> topPropagateToClasses = new ArrayList<>();

    /**
     * Where CF values are being accumulated from. Key is an entity class and value is a list of fields and field type (entity clases) that entity should inherit CF values from.
     */
    protected static Map<Class<?>, Map<Class<?>, String>> treeAcumulateFrom = new HashMap<>();

    /**
     * Maps CFT appliesTo value to an entity class
     */
    protected static Map<String, Class<?>> appliesToMap = new HashMap<>();

    /**
     * Custom field value accumulation rules identified by a custom field template code and a provider code in: <provider code>_<cft code>
     */
    private static Map<String, CfValueAccumulatorRule> cfAccumulatorRules = new HashMap<>();

    private static int paginationSize = 200;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private Logger log;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @EJB
    private CfValueAccumulator cfValueAccumulator;

    /**
     * Load Custom field entity hierarchy constructing two lists/maps - where CF values are being accumulated from and where to cf values should be propagated to
     */
    static {

        Set<Class<?>> cfClasses = ReflectionUtils.getClassesAnnotatedWith(CustomFieldEntity.class);

        for (Class<?> cfClass : cfClasses) {
            CustomFieldEntity cfAnotation = cfClass.getAnnotation(CustomFieldEntity.class);
            if (cfAnotation.inheritCFValuesFrom().length == 0 && !cfAnotation.inheritFromProvider()) {
                continue;
            }
            Map<Class<?>, String> paths = new LinkedHashMap<>();
            for (String fieldName : cfAnotation.inheritCFValuesFrom()) {
                Class<?> targetClass = ReflectionUtils.getField(cfClass, fieldName).getType();
                paths.put(targetClass, fieldName);

                List<Class<?>> propagateTos = treePropagateTo.get(targetClass);
                if (propagateTos == null) {
                    propagateTos = new ArrayList<>();
                    treePropagateTo.put(targetClass, propagateTos);
                }
                propagateTos.add(cfClass);

            }

            if (cfAnotation.inheritFromProvider()) {
                paths.put(Provider.class, null);

                List<Class<?>> propagateTos = treePropagateTo.get(Provider.class);
                if (propagateTos == null) {
                    propagateTos = new ArrayList<>();
                    treePropagateTo.put(Provider.class, propagateTos);
                }
                propagateTos.add(cfClass);
            }
            treeAcumulateFrom.put(cfClass, paths);
        }

        // Construct a mapping between CFT appliesTo value and a entity class
        treePropagateTo.keySet().forEach(x -> {
            appliesToMap.put(((CustomFieldEntity) x.getAnnotation(CustomFieldEntity.class)).cftCodePrefix(), x);
        });

        treeAcumulateFrom.keySet().forEach(x -> appliesToMap.put(((CustomFieldEntity) x.getAnnotation(CustomFieldEntity.class)).cftCodePrefix(), x));

        // Find topmost accumulation classes
        topPropagateToClasses.addAll(treePropagateTo.keySet());
        topPropagateToClasses.removeAll(treeAcumulateFrom.keySet());

        // Move provider to be the first class in a list. ONLY to solve the issue with subscription's multiparent issue
        int providerPos = topPropagateToClasses.indexOf(Provider.class);
        if (providerPos > 0) {
            topPropagateToClasses.add(0, topPropagateToClasses.remove(providerPos));
        }
    }

    /**
     * Construct CF accumulation rules for a given Custom field templates.
     */
    public void loadCfAccumulationRules() {

        log.trace("Constructing CF accumulation rules");
        String providerPrefix = currentUser.getProviderCode() + "_";

        // Remove all rules for a current provider
        List<String> providerCfCodes = appliesToMap.keySet().stream().filter(x -> x.startsWith(providerPrefix)).collect(Collectors.toList());
        providerCfCodes.forEach(key -> cfAccumulatorRules.remove(key));

        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCustomFieldsForAcumulation(appliesToMap.keySet());

        Map<String, List<CustomFieldTemplate>> cftMap = cfts.stream().collect(Collectors.groupingBy(CustomFieldTemplate::getCode));

        for (Entry<String, List<CustomFieldTemplate>> groupedCfts : cftMap.entrySet()) {
            cfAccumulatorRules.put(providerPrefix + groupedCfts.getKey(), new CfValueAccumulatorRule(groupedCfts.getValue()));
        }

        log.info("CF accumulation rules constructed {}", cfAccumulatorRules);
    }

    /**
     * Refresh CF accumulation rules due to a change in a given Custom field template. Rules will be reconstructed only if CFT is for an entity that participates in custom field
     * entity hierarchy. No need to call this method if CFT was just updated.
     * 
     * @param cft CFT that was added or removed.
     * @return True if CFT accumulation rules have changed
     */
    public boolean refreshCfAccumulationRules(CustomFieldTemplate cft) {

        // Entity class for which CFT applies to does not participate in custom field entity hierarchy
        if (!appliesToMap.containsKey(cft.getAppliesTo())) {
            return false;
        }

        loadCfAccumulationRules();
        return true;
    }

    /**
     * Recalculate accumulated custom field values for all entities of an entity class that a given custom field template applies to
     * 
     * @param cft Custom field template
     */
    @SuppressWarnings("unchecked")
    @Asynchronous
    public void cftCreated(CustomFieldTemplate cft) {

        Class<?> entityClass = appliesToMap.get(cft.getAppliesTo());
        if (entityClass == null) {
            return;
        }

        String cfCode = cft.getCode();
        boolean isVersionable = cft.isVersionable();

        // Determine if there is a need to propagate CF changes down - e.g. when new CFT is the last in the inheritance chain - then not
        String providerPrefix = currentUser.getProviderCode() + "_";
        CfValueAccumulatorRule accumulationRule = cfAccumulatorRules.get(providerPrefix + cfCode);
        boolean canPropagateDown = false;
        if (accumulationRule != null) {
            List<Class<?>> entityClassesToPropagateTo = accumulationRule.getPropagateTo().get(entityClass);
            canPropagateDown = entityClassesToPropagateTo != null && !entityClassesToPropagateTo.isEmpty();
        }
        Object defaultValue = cft.getDefaultValueConverted();

        int from = 0;
        Query query = getEntityManager().createQuery("select e from " + entityClass.getSimpleName() + " e order by e.id").setMaxResults(paginationSize);

        List<ICustomFieldEntity> entitiesToAccumulateFor = query.getResultList();
        while (!entitiesToAccumulateFor.isEmpty()) {

            // StringBuffer sb = new StringBuffer();
            // long start = System.currentTimeMillis();
            // for (ICustomFieldEntity entity : entitiesToAccumulateFor) {
            // sb.append(((IEntity) entity).getId());
            // }
            // long end = System.currentTimeMillis();
            // log.error("AKK external loop {}", end - start);
            //
            // StringBuffer sb2 = new StringBuffer();
            // start = System.currentTimeMillis();
            // entitiesToAccumulateFor.stream().forEach(entity -> {
            // sb2.append(((IEntity) entity).getId());
            // });
            // end = System.currentTimeMillis();
            // log.error("AKK internal loop {}", end - start);

            List<ICustomFieldEntity> entitiesToPropagateDown = new ArrayList<>();

            for (ICustomFieldEntity entity : entitiesToAccumulateFor) {

                boolean isApplicable = true;
                if (cft.getApplicableOnEl() != null) {
                    isApplicable = ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity);
                }

                if (!isApplicable) {
                    continue;
                }
                // Set value with a default value if requested
                if (defaultValue != null) {
                    entity.setCfValue(cfCode, defaultValue);
                }

                // Populate accumulated custom field value field
                boolean propagateDown = accumulateCfValue(entity, cfCode, isVersionable);

                // Set value with a value from inherited value
                if (cft.isUseInheritedAsDefaultValue()) {
                    Object valueInherited = entity.getCfAccumulatedValue(cfCode);
                    if (valueInherited != null) {
                        entity.setCfValue(cfCode, valueInherited);
                    }
                }

                if (canPropagateDown && propagateDown) {
                    entitiesToPropagateDown.add(entity);
                }
            }

            // Propagate value accumulation to child entities in custom field inheritance hierarchy
            for (ICustomFieldEntity entity : entitiesToPropagateDown) {
                propagateCFValue(entity, cfCode, isVersionable);
            }

            from += paginationSize;
            entitiesToAccumulateFor = query.setFirstResult(from).getResultList();
        }
    }

    /**
     * Handle Custom field entity update. Set and calculate accumulated custom field value field and propagate CF changes to the lower entities in custom field inheriatnce
     * hierarchy.
     * 
     * @param entity Entity that was updated
     * @param cfsWithValueChanged Custom fields (codes) that were added, modified, or removed. Also includes any changes in periods.
     * @param cfsWithPeriodsChanged Custom fields (codes) periods that were added or removed. Same as dirtyCfValues minus the custom fields, which had change in value only.
     */
    public void entityUpdated(ICustomFieldEntity entity, Set<String> cfsWithValueChanged, Set<String> cfsWithPeriodsChanged) {

        if (cfsWithValueChanged == null || cfsWithValueChanged.isEmpty()) {
            return;
        }

        if (cfsWithPeriodsChanged == null) {
            cfsWithPeriodsChanged = new HashSet<>();
        }

        Map<String, Boolean> propagateDownCfs = new HashMap<>();

        for (String cfCode : cfsWithValueChanged) {
            boolean periodsChanged = cfsWithPeriodsChanged.contains(cfCode);

            boolean propagateDown = accumulateCfValue(entity, cfCode, periodsChanged);
            // Get a list of child entities and call propagateCFValues on each of them
            if (propagateDown) {
                propagateDownCfs.put(cfCode, periodsChanged);
            }
        }

        // Propagate CF changes to the child entities
        if (!propagateDownCfs.isEmpty()) {
            cfValueAccumulator.propagateCFValues(entity, propagateDownCfs);
        }
    }

    /**
     * Handle Custom field entity creation. Set and calculate accumulated custom field value field.
     * 
     * @param entity Entity that was created
     */
    public void entityCreated(ICustomFieldEntity entity) {

        Set<String> cfCodes = customFieldTemplateService.findByAppliesTo(entity).keySet();

        Map<String, Boolean> propagateDownCfs = new HashMap<>();

        for (String cfCode : cfCodes) {

            boolean propagateDown = accumulateCfValue(entity, cfCode, true);
            // Get a list of child entities and call propagateCFValues on each of them
            if (propagateDown) {
                propagateDownCfs.put(cfCode, true);
            }
        }

        // Propagate CF changes to the child entities
        if (!propagateDownCfs.isEmpty()) {
            cfValueAccumulator.propagateCFValues(entity, propagateDownCfs);
        }
    }

    /**
     * Propagate accumulated CF value to the child entities in custom field inheritance hierarchy. For each propagation path, find corresponding entities, update their CF
     * accumulated value and propagate it downwards the hierarchy.
     * 
     * @param entity Entity with source of CF values
     * @param cfCodes Custom field codes to propagate down where key is Custom field code and value is a boolean indicating if there were any changes in periods (new, removed or
     *        period dates changed). False if there was a change in value only.
     */
    @Asynchronous
    public void propagateCFValues(ICustomFieldEntity entity, Map<String, Boolean> cfCodes) {

        for (Entry<String, Boolean> cfCodeInfo : cfCodes.entrySet()) {
            String cfCode = cfCodeInfo.getKey();
            boolean periodsChanged = cfCodeInfo.getValue();
            propagateCFValue(entity, cfCode, periodsChanged);
        }
    }

    /**
     * Propagate accumulated CF value to the child entities in custom field inheritance hierarchy. For each propagation path, find corresponding entities, update their CF
     * accumulated value and propagate it downwards the hierarchy.
     * 
     * @param entity Entity with source of CF values
     * @param cfCode Custom field code to update
     * @param periodDatesChanged True if there were any changes in periods (new, removed or period dates changed). False if there was a change in value only.
     */
    @SuppressWarnings("unchecked")
    private void propagateCFValue(ICustomFieldEntity entity, String cfCode, boolean periodDatesChanged) {

        String providerPrefix = currentUser.getProviderCode() + "_";

        Class<?> entityClass = ReflectionUtils.getCleanClass(entity.getClass());
        CfValueAccumulatorRule accumulationRule = cfAccumulatorRules.get(providerPrefix + cfCode);
        List<Class<?>> entityClassesToPropagateTo = accumulationRule.getPropagateTo().get(entityClass);
        if (entityClassesToPropagateTo == null || entityClassesToPropagateTo.isEmpty()) {
            return;
        }

        log.trace("Will propagate CF value {} from entity {}/{}", cfCode, entityClass.getSimpleName(), ((IEntity) entity).getId());

        // For each propagation path, find corresponding entities, update their CF accumulated value and propagate it downwards the hierarchy
        EntityManager entityManager = getEntityManager();
        for (Class<?> classToPropagateTo : entityClassesToPropagateTo) {
            List<CfValueAccumulatorPath> paths = accumulationRule.getAcumulateFrom().get(classToPropagateTo);
            if (paths == null || paths.isEmpty()) {
                continue;
            }

            // If child class has only one way of accumulating CF values, OR
            // periods were not changed (only the values changed) and its not a Map type storage
            // append or override the ones from parent and propagate downwards if there were any changes
            if (paths.size() == 1 || (!periodDatesChanged && !accumulationRule.isStoredAsMap())) {

                for (CfValueAccumulatorPath cfValueAccumulatorPath : paths) {
                    if (!cfValueAccumulatorPath.getClazz().equals(entityClass)) {
                        continue;
                    }

                    int from = 0;
                    // log.trace("Will propagate CF value {} from entity {}/{} to {} by path {}", cfCode, entityClass.getSimpleName(), ((IEntity) entity).getId(),
                    // classToPropagateTo.getSimpleName(), cfValueAccumulatorPath.getPath());

                    // If path is null, then return all entities.
                    // If path is not null, ten return only those entities that are linked to a given entity
                    Query query = null;
                    if (cfValueAccumulatorPath.getPath() == null) {

                        // Seller is a special case that has recursive relationship to another seller, so propagation from provider should select only topmost sellers (no FK to
                        // another seller)
                        if (Seller.class.equals(classToPropagateTo)) {
                            query = entityManager.createQuery("select e from Seller e where e.seller is null order by e.id").setMaxResults(paginationSize);
                        } else {
                            query = entityManager.createQuery("select e from " + classToPropagateTo.getSimpleName() + " e order by e.id").setMaxResults(paginationSize);
                        }
                    } else {
                        query = entityManager
                            .createQuery("select e from " + classToPropagateTo.getSimpleName() + " e where e." + cfValueAccumulatorPath.getPath() + "=:entity order by e.id")
                            .setParameter("entity", entity).setMaxResults(paginationSize);
                    }

                    String sourceForCFValue = cfValueAccumulatorPath.getPath() == null ? entityClass.getSimpleName() : cfValueAccumulatorPath.getPath();

                    List<ICustomFieldEntity> entitiesToAccumulateFor = query.getResultList();
                    while (!entitiesToAccumulateFor.isEmpty()) {

                        List<ICustomFieldEntity> entitiesToPropagateDown = new ArrayList<>();

                        for (ICustomFieldEntity e : entitiesToAccumulateFor) {

                            // Clear values that came from the same path
                            boolean hasChanged = e.getCfAccumulatedValuesNullSafe().clearValues(cfCode, sourceForCFValue);

                            // Map/matrix is a special case as it deals with not value as a whole, but value's items. So clearing values that came from the same path would remove
                            // whole value altogether, so need to re-add value from the entity first, and only then - of its parents
                            if (hasChanged && accumulationRule.isStoredAsMap()) {
                                e.getCfAccumulatedValues().appendCfValues(cfCode, e.getCfValues(), null);
                            }
                            // Re-append values from parent again
                            boolean wasAppended = e.getCfAccumulatedValues().appendCfValues(cfCode, entity.getCfAccumulatedValues(), sourceForCFValue);
                            hasChanged = hasChanged || wasAppended;

                            // And propagate it downwards the hierarchy if there were any changes
                            if (hasChanged) {
                                // log.trace("CF {} accumulated value was updated for {}/{} entity and will propage the change down", cfCode, e.getClass().getSimpleName(),
                                // ((IEntity) e).getId());
                                entitiesToPropagateDown.add(e);
                            }
                        }

                        // Propagate value accumulation to child entities in custom field inheritance hierarchy
                        for (ICustomFieldEntity entityPropagateDown : entitiesToPropagateDown) {
                            propagateCFValue(entityPropagateDown, cfCode, periodDatesChanged);
                        }

                        from += paginationSize;
                        entitiesToAccumulateFor = query.setFirstResult(from).getResultList();
                    }
                }
                // Child entity accumulates values from multiple entities in parallel then reset target entities accumulated CF values completely and let it accumulate value for
                // itself.
                // This case applies to versioned or map type CF values in multi-hierarchy case as value is broken down further into periods (versioned case) or map keys (map and
                // matrix type fields) and there is no way to reconstruct accumulated value by propagation (especially in CF period/map key removal case)
            } else {

                CfValueAccumulatorPath cfValueAccumulatorPath = null;
                for (CfValueAccumulatorPath cfValueAccumulatorPathIter : paths) {
                    if (cfValueAccumulatorPathIter.getClazz().equals(entityClass)) {
                        cfValueAccumulatorPath = cfValueAccumulatorPathIter;
                        break;
                    }
                }

                int from = 0;
                // log.trace("Will propagate CF value {} from entity {}/{} to {} by path {}. Propagation will be by accumulation.", cfCode, entityClass.getSimpleName(),
                // ((IEntity) entity).getId(), classToPropagateTo.getSimpleName(), cfValueAccumulatorPath.getPath());

                // If path is null, then return all entities.
                // If path is not null, then return only those entities that are linked to a given entity
                Query query = null;
                if (cfValueAccumulatorPath.getPath() == null) {
                    query = entityManager.createQuery("select e from " + classToPropagateTo.getSimpleName() + " e order by e.id").setMaxResults(paginationSize);
                } else {
                    query = entityManager
                        .createQuery("select e from " + classToPropagateTo.getSimpleName() + " e where e." + cfValueAccumulatorPath.getPath() + "=:entity order by e.id")
                        .setParameter("entity", entity).setMaxResults(paginationSize);
                }
                List<ICustomFieldEntity> entitiesToAccumulateFor = query.getResultList();
                while (!entitiesToAccumulateFor.isEmpty()) {

                    List<ICustomFieldEntity> entitiesToPropagateDown = new ArrayList<>();

                    for (ICustomFieldEntity e : entitiesToAccumulateFor) {

                        // Force to accumulate CF values again as in case of multiple path, no way to respect the priority without reconstructing the field again
                        boolean hasChanged = accumulateCfValue(e, cfCode, true);
                        if (hasChanged) {
                            // log.trace("CF {} accumulated value was updated for {} entity and will propage the change down", cfCode, e.getClass().getSimpleName(),
                            // ((IEntity) e).getId());
                            entitiesToPropagateDown.add(e);
                        }
                    }

                    // Propagate value accumulation to child entities in custom field inheritance hierarchy
                    for (ICustomFieldEntity entityPropagateDown : entitiesToPropagateDown) {
                        propagateCFValue(entityPropagateDown, cfCode, true);
                    }

                    from += paginationSize;
                    entitiesToAccumulateFor = query.setFirstResult(from).getResultList();
                }
            }
        }
    }

    /**
     * Accumulate CF values. In case when entity has no CF value inheritance, or CFT is not participating in CF value inheritance (only a single entity class has this CFT) - simply
     * copy the value to the accumulated field
     * 
     * @param entity Entity to accumulate the values for
     * @param cfCode Custom field code
     * @param periodDatesChanged True if there were any changes in periods (new, removed or period dates changed) - will force to query and aaccumulate values from parent. False if
     *        there was a change in value only - no need to query and accumulate from parent.
     * @return Should value change be propagated down the inheritance hierarchy to other entities
     */
    private boolean accumulateCfValue(ICustomFieldEntity entity, String cfCode, boolean periodDatesChanged) {

        String providerPrefix = currentUser.getProviderCode() + "_";

        CfValueAccumulatorRule accumulationRule = cfAccumulatorRules.get(providerPrefix + cfCode);
        boolean hasCfValue = entity.hasCfValue(cfCode);

        // Just copy the value to accumulated values field when:
        // CFT does not participate in inheritance
        // When CFT does participate in inheritance, but field is not versionable and is not map type and value is present - no way field value could come from other entity
        if (accumulationRule == null || (hasCfValue && !accumulationRule.isVersionable() && !accumulationRule.isStoredAsMap())) {
            entity.getCfAccumulatedValuesNullSafe().copyCfValues(cfCode, entity.getCfValues());
            // Propagate when CFT participate in inheritance
            return accumulationRule != null;
        }

        Class<?> entityClass = ReflectionUtils.getCleanClass(entity.getClass());

        log.trace("Will accumulate values for {} field (periods changed = {}) of entity {}/{}", cfCode, periodDatesChanged, entityClass.getSimpleName(),
            ((IEntity) entity).getId());

        boolean appendAccumulatedValuesFromParent = true;

        // For versionable CFT, in case when only CF values has changed (no change in period dates, new periods, or periods removed), simply override the accumulated values with
        // new values. No need to merge with values from parent CF entity.
        if (accumulationRule.isVersionable() && !periodDatesChanged) {
            entity.getCfAccumulatedValuesNullSafe().overrideOrAppendCfValues(cfCode, entity.getCfValues());
            appendAccumulatedValuesFromParent = false;

            // In other cases, copy current value (can be null, in which case it will be removed) and merge it with values form parent CF entity.
        } else {
            entity.getCfAccumulatedValuesNullSafe().copyCfValues(cfCode, entity.getCfValues());
        }

        // Retrieve parent entities, get their accumulated values and append only missing periods

        // Merge current values with values from parent CF entity.
        if (appendAccumulatedValuesFromParent) {

            List<CfValueAccumulatorPath> accumulationPaths = accumulationRule.getAcumulateFrom().get(entityClass);

            if (accumulationPaths == null || accumulationPaths.isEmpty()) {
                return true;
            }

            EntityManager entityManager = getEntityManager();

            for (CfValueAccumulatorPath cfValueAccumulatorPath : accumulationPaths) {

                // Seller is a special case that has recursive relationship to another seller, so accumulation from provider should be for only topmost sellers (no FK to
                // another seller)
                if (Provider.class.equals(cfValueAccumulatorPath.getClazz()) && entityClass.equals(Seller.class) && ((Seller) entity).getSeller() != null) {
                    continue;
                }

                String sourceForCFValue = cfValueAccumulatorPath.getPath() == null ? cfValueAccumulatorPath.getClazz().getSimpleName() : cfValueAccumulatorPath.getPath();

                // log.trace("Will accumulate values from parent for {} field of entity {}/{}. Parent path {}", cfCode, entityClass.getSimpleName(), ((IEntity) entity).getId(),
                // sourceForCFValue);

                if (Provider.class.equals(cfValueAccumulatorPath.getClazz())) {

                    entity.getCfAccumulatedValues().appendCfValues(cfCode, appProvider.getCfValues(), sourceForCFValue);
                    
                    // Query query = entityManager.createQuery("select e.cfValues.valuesByCode from Provider e where id=:id").setParameter("id", appProvider.getId());
                    // Map<String, List<CustomFieldValue>> cfValuesFromParent = (Map<String, List<CustomFieldValue>>) query.getSingleResult();
                    // if (cfValuesFromParent != null) {
                    // entity.getCfAccumulatedValues().appendCfValues(cfCode, new CustomFieldValues(cfValuesFromParent), sourceForCFValue);
                    // }
                } else {
                    Query query = entityManager
                        .createQuery(
                            "select e." + cfValueAccumulatorPath.getPath() + ".cfAccumulatedValues.valuesByCode from " + entityClass.getSimpleName() + " e where e=:entity")
                        .setParameter("entity", entity);
                    try {
                        Map<String, List<CustomFieldValue>> cfValuesFromParent = (Map<String, List<CustomFieldValue>>) query.getSingleResult();
                        if (cfValuesFromParent != null) {
                            entity.getCfAccumulatedValues().appendCfValues(cfCode, new CustomFieldValues(cfValuesFromParent), sourceForCFValue);
                            // CustomFieldValues cfValuesFromParent = (CustomFieldValues) query.getSingleResult();
                            // entity.getCfAccumulatedValues().appendCfValues(cfCode, cfValuesFromParent, sourceForCFValue);
                        }

                    } catch (NoResultException e) {
                        // No worries here
                        continue;
                    }
                }
            }
        }
        return true;
    }

    private EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }
}