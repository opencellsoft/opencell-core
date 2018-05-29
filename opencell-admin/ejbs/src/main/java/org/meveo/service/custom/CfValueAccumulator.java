package org.meveo.service.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.util.MeveoJpaForMultiTenancy;
import org.meveo.util.MeveoJpaForMultiTenancyForJobs;
import org.slf4j.Logger;

/**
 * Handles CF value acumulation rules
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
    @MeveoJpaForMultiTenancy
    private EntityManager em;

    @Inject
    @MeveoJpaForMultiTenancyForJobs
    private EntityManager emfForJobs;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

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

        // Find topmost and bottom accumulation classes
        topPropagateToClasses.addAll(treePropagateTo.keySet());
        topPropagateToClasses.removeAll(treeAcumulateFrom.keySet());
    }

    /**
     * Construct CF accumulation rules for a given Custom field templates.
     */
    public void loadCfAccumulationRules() {

        String providerPrefix = currentUser.getProviderCode() + "_";

        // Remove all rules for a current provider
        List<String> providerCfCodes = appliesToMap.keySet().stream().filter(x -> x.startsWith(providerPrefix)).collect(Collectors.toList());
        providerCfCodes.forEach(key -> cfAccumulatorRules.remove(key));

        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCustomFieldsForAcumulation(appliesToMap.keySet());

        Map<String, List<CustomFieldTemplate>> cftMap = cfts.stream().collect(Collectors.groupingBy(CustomFieldTemplate::getCode));

        for (Entry<String, List<CustomFieldTemplate>> groupedCfts : cftMap.entrySet()) {

            cfAccumulatorRules.put(providerPrefix + groupedCfts.getKey(), new CfValueAccumulatorRule(groupedCfts.getValue()));
        }
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
    public void accumulateCfValues(CustomFieldTemplate cft) {

        Class<?> entityClass = appliesToMap.get(cft.getAppliesTo());
        if (entityClass == null) {
            return;
        }

        String cfCode = cft.getCode();
        
        int from = 0;
        Query query = getEntityManager().createQuery("select e from " + entityClass.getSimpleName() + " e order by e.id").setMaxResults(paginationSize);

        List<ICustomFieldEntity> entitiesToAccumulateFor = query.getResultList();
        while (!entitiesToAccumulateFor.isEmpty()) {

            entitiesToAccumulateFor.stream().forEach(entity -> {
                // Populate accumulated custom field value field
                accumulateCfValue(entity, cfCode);
            });

            from += paginationSize;
            entitiesToAccumulateFor = query.setFirstResult(from).getResultList();
        }

    }

    /**
     * Handle Custom field entity update. Set and calculate accumulated custom field value field and propagate CF changes to the lower entities in custom field inheriatnce
     * hierarchy.
     * 
     * @param entity Entity that was updated
     * @param cfsChanged Custom field codes that were changed
     */
    public void entityUpdated(ICustomFieldEntity entity, List<String> cfsChanged) {
        for (String cfCode : cfsChanged) {

            boolean propagateDown = accumulateCfValue(entity, cfCode);
            // Get a list of child entities and call propagateCFValues on each of them
            if (propagateDown) {
                propagateCFValue(entity, cfCode);
            }
        }
    }

    /**
     * Handle Custom field entity creation. Set and calculate accumulated custom field value field.
     * 
     * @param entity Entity that was created
     */
    public void entityCreated(ICustomFieldEntity entity) {

        Set<String> cfCodes = customFieldTemplateService.findByAppliesTo(entity).keySet();

        for (String cfCode : cfCodes) {

            boolean propagateDown = accumulateCfValue(entity, cfCode);
            // Get a list of child entities and call propagateCFValues on each of them
            if (propagateDown) {
                propagateCFValue(entity, cfCode);
            }
        }
    }

    /**
     * Propagate accumulated CF value to the child entities in custom field inheritance hierarchy. For each propagation path, find corresponding entities, update their CF
     * accumulated value and propagate it downwards the hierarchy.
     * 
     * @param entity Entity with source of CF values
     * @param cfCode Custom field code to update
     */
    @SuppressWarnings("unchecked")
    private void propagateCFValue(ICustomFieldEntity entity, String cfCode) {

        String providerPrefix = currentUser.getProviderCode() + "_";

        Class<?> entityClass = ReflectionUtils.getCleanClass(entity.getClass());
        CfValueAccumulatorRule accumulationRule = cfAccumulatorRules.get(providerPrefix + cfCode);
        List<Class<?>> entityClassesToPropagateTo = accumulationRule.getPropagateTo().get(entityClass);
        if (entityClassesToPropagateTo == null || entityClassesToPropagateTo.isEmpty()) {
            return;
        }

        // For each propagation path, find corresponding entities, update their CF accumulated value and propagate it downwards the hierarchy
        EntityManager entityManager = getEntityManager();
        for (Class<?> classToPropagateTo : entityClassesToPropagateTo) {
            List<CfValueAccumulatorPath> paths = accumulationRule.getAcumulateFrom().get(classToPropagateTo);
            if (paths == null || paths.isEmpty()) {
                continue;
            }
            for (CfValueAccumulatorPath cfValueAccumulatorPath : paths) {
                int from = 0;
                Query query = entityManager
                    .createQuery("select e from " + cfValueAccumulatorPath.getClazz().getSimpleName() + " e where e." + cfValueAccumulatorPath.getPath() + "=:entity order by e.id")
                    .setParameter("entity", entity).setMaxResults(paginationSize);

                List<ICustomFieldEntity> entitiesToAccumulateFor = query.getResultList();
                while (!entitiesToAccumulateFor.isEmpty()) {

                    entitiesToAccumulateFor.stream().forEach(e -> {
                        // Update their CF accumulated value and propagate it downwards the hierarchy
                        boolean hasChanged = e.getCfValuesNullSafe().appendCfValues(cfCode, entity.getCfValues());
                        if (hasChanged) {
                            propagateCFValue(e, cfCode);
                        }
                    });

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
     * @return Should value change be propagated down the inheritance hierarchy to other entities
     */
    private boolean accumulateCfValue(ICustomFieldEntity entity, String cfCode) {

        String providerPrefix = currentUser.getProviderCode() + "_";

        CfValueAccumulatorRule accumulationRule = cfAccumulatorRules.get(providerPrefix + cfCode);
        boolean hasCfValue = entity.hasCfValue(cfCode);

        // Just copy the value to accumulated values field when:
        // CFT does not participate in inheritance
        // When CFT is not versionable and value is present
        if (accumulationRule == null || (hasCfValue && !accumulationRule.isVersionable())) {
            entity.getCfAccumulatedValuesNullSafe().copyCfValues(cfCode, entity.getCfValues());
            return accumulationRule == null;
        }

        Class<?> entityClass = ReflectionUtils.getCleanClass(entity.getClass());

        boolean periodDatesChanged = true;
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

        // Merge current values with values from parent CF entity.
        if (appendAccumulatedValuesFromParent) {

            List<CfValueAccumulatorPath> accumulationPaths = accumulationRule.getAcumulateFrom().get(entityClass);

            if (accumulationPaths == null || accumulationPaths.isEmpty()) {
                return true;
            }
            // Retrieve parent entities, get their accumulated values and append only missing periods
            EntityManager entityManager = getEntityManager();
            for (CfValueAccumulatorPath cfValueAccumulatorPath : accumulationPaths) {

                Query query = entityManager
                    .createQuery("select e." + cfValueAccumulatorPath.getPath() + ".cfAccumulatedValues from " + entityClass.getSimpleName() + " e where e=:entity")
                    .setParameter("entity", entity);
                try {
                    CustomFieldValues cfValuesFromParent = (CustomFieldValues) ((Object[]) query.getSingleResult())[0];
                    entity.getCfAccumulatedValuesNullSafe().appendCfValues(cfCode, cfValuesFromParent);

                } catch (NoResultException e) {
                    // No worries here
                    continue;
                }
            }
        }
        return true;
    }

    private EntityManager getEntityManager() {

        if (FacesContext.getCurrentInstance() != null) {
            return em;
        }
        return emfForJobs;
    }
}