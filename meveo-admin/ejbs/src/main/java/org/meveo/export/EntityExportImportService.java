package org.meveo.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.Auditable;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.IVersionedEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.MeveoJpaForTarget;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

@Lock(LockType.READ)
@Singleton
public class EntityExportImportService implements Serializable {

    private static final long serialVersionUID = 5141462881249084547L;

    @Inject
    @MeveoJpa
    private EntityManager em;

    @Inject
    @MeveoJpaForTarget
    private EntityManager emTarget;

    @Inject
    @MeveoJpaForJobs
    private EntityManager emfForJobs;

    @Inject
    private Conversation conversation;

    private ParamBean param = ParamBean.getInstance();

    @Inject
    private Logger log;

    private Map<Class<? extends IEntity>, String[]> exportIdMapping;
    private Map<String, Object[]> attributesToOmit;
    @SuppressWarnings("rawtypes")
    private Map<Class, List<Field>> nonCascadableFields;

    @PostConstruct
    private void init() {
        exportIdMapping = loadExportIdentifierMappings();
        attributesToOmit = loadAtributesToOmit();
        nonCascadableFields = loadNonCascadableFields();
    }

    private EntityManager getEntityManager() {
        EntityManager result = emfForJobs;
        if (conversation != null) {
            try {
                conversation.isTransient();
                result = em;
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     * Export entities matching given export templates
     * 
     * @param exportTemplates A list of export templates
     * @param parameters Entity export (select) criteria
     * @return Export statistics
     */
    public ExportImportStatistics exportEntities(Collection<ExportTemplate> exportTemplates, Map<String, Object> parameters) {
        ExportImportStatistics exportStats = new ExportImportStatistics();
        for (ExportTemplate exportTemplate : exportTemplates) {
            ExportImportStatistics exportStatsSingle = exportEntities(exportTemplate, parameters);
            exportStats.updateSummary(exportStatsSingle);
        }
        return exportStats;
    }

    /**
     * Export entities matching a given export template
     * 
     * @param exportTemplates Export template
     * @param parameters Entity export (select) criteria
     * @return Export statistics
     */
    public ExportImportStatistics exportEntities(ExportTemplate exportTemplate, Map<String, Object> parameters) {

        if (parameters == null) {
            parameters = new HashMap<String, Object>();
        }

        XStream xstream = new XStream();
        xstream.alias("exportInfo", ExportInfo.class);
        xstream.useAttributeFor(ExportTemplate.class, "name");
        xstream.useAttributeFor(ExportTemplate.class, "entityToExport");
        xstream.useAttributeFor(ExportTemplate.class, "canDeleteAfterExport");

        ExportImportStatistics exportStats = new ExportImportStatistics();
        List<ExportInfo> exportInfos = new LinkedList<ExportInfo>();
        if (exportTemplate.getGroupedTemplates() == null || exportTemplate.getGroupedTemplates().isEmpty()) {
            String xml = serializeEntities(exportTemplate, parameters, exportStats);
            exportInfos.add(new ExportInfo(exportTemplate, xml));
        } else {
            for (ExportTemplate groupedExportTemplate : exportTemplate.getGroupedTemplates()) {
                String xml = serializeEntities(groupedExportTemplate, parameters, exportStats);
                exportInfos.add(new ExportInfo(groupedExportTemplate, xml));
            }
        }

        FileOutputStream fos = null;
        String filename = param.getProperty("providers.rootDir", "/tmp/meveo/");
        if (!filename.endsWith(File.separator)) {
            filename = filename + File.separator;
        }
        if (parameters.get("provider") != null) {
            filename = filename + ((Provider) parameters.get("provider")).getCode() + File.separator;
        }
        filename = filename + "exports";
        try {
            FileUtils.forceMkdir(new File(filename));
            filename = filename + File.separator + exportTemplate.getName() + DateUtils.formatDateWithPattern(new Date(), "yyyy-MM-DD_HH_mm_ss") + ".xml";

            fos = new FileOutputStream(filename);
            xstream.toXML(exportInfos, fos);
        } catch (Exception e) {
            log.error("Failed to export data to a file {}", filename, e);

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("Failed to export data to a file {}", filename, e);
                }
            }
        }
        log.info("Entities for export template {} saved to a file {}", exportTemplate.getName(), filename);

        // Remove entities if was requested so
        if (parameters.containsKey("delete") && (Boolean) parameters.get("delete")) {
            removeEntitiesAfterExport(exportStats);
        }

        return exportStats;
    }

    /**
     * Remove entities after an export
     * 
     * @param exportStats Export statistics, including entities to remove
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void removeEntitiesAfterExport(ExportImportStatistics exportStats) {

        EntityManager emForRemove = getEntityManager();

        for (Entry<Class, List<Long>> removeInfo : exportStats.getEntitiesToRemove().entrySet()) {
            for (Long id : removeInfo.getValue()) {
                try {
                    emForRemove.remove(emForRemove.getReference(removeInfo.getKey(), id));
                    exportStats.updateDeleteSummary(removeInfo.getKey(), 1);
                    log.trace("Removed entity {} id {}", removeInfo.getKey().getName(), id);

                } catch (Exception e) {
                    log.error("Failed to remove entity {} id {}", removeInfo.getKey().getName(), id);
                }
            }
        }
        exportStats.getEntitiesToRemove().clear();

    }

    /**
     * Export entities matching a given export template
     * 
     * @param exportTemplate Export template
     * @param parameters Entity export (select) criteria
     * @param exportStats Export statistics
     * @return
     */
    @SuppressWarnings("unchecked")
    private String serializeEntities(ExportTemplate exportTemplate, Map<String, Object> parameters, ExportImportStatistics exportStats) {

        XStream xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new HibernateMapper(next);
            }
        };
        ExportImportConfig exportImportConfig = new ExportImportConfig(exportTemplate, exportIdMapping);

        // Add custom converters
        xstream.registerConverter(new IEntityHibernateProxyConverter(exportImportConfig), XStream.PRIORITY_VERY_HIGH);
        xstream.registerConverter(new IEntityExportIdentifierConverter(exportImportConfig), XStream.PRIORITY_NORMAL);
        xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));
        xstream.registerConverter(new IEntityClassConverter(xstream.getMapper(), xstream.getReflectionProvider(), true), XStream.PRIORITY_LOW);

        // Indicate XStream to omit certain attributes except ones matching the classes to be exported fully (except the root class)
        applyAttributesToOmit(xstream, exportTemplate.getClassesToExportAsFull());

        // xstream.setMode(XStream.NO_REFERENCES);

        // Construct a query to retrieve entities to export by selection criteria. OR examine selection criteria - could be that top export entity matches search criteria for
        // related entities (e.g. exporting provider and related info and some provider is search criteria, but also it matches the top entity)
        StringBuilder sql = new StringBuilder("select e from " + exportTemplate.getEntityToExport().getName() + " e  ");
        boolean firstWhere = true;
        Map<String, Object> parametersToApply = new HashMap<String, Object>();
        for (Entry<String, Object> param : parameters.entrySet()) {
            String paramName = param.getKey();
            Object paramValue = param.getValue();

            if (paramValue == null) {
                continue;

                // Handle the case when top export entity matches search criteria for related entities (e.g. exporting provider and related info. If search criteria is Provider -
                // for related entities it is just search criteria, but for top entity (provider) it has to match it)
            } else if (exportTemplate.getEntityToExport().isAssignableFrom(paramValue.getClass())) {
                sql.append(firstWhere ? " where " : " and ").append(" id=:id");
                firstWhere = false;
                parametersToApply.put("id", ((IEntity) paramValue).getId());

            } else {
                String fieldName = paramName;
                String fieldCondition = "=";
                if (fieldName.contains("_")) {
                    String[] paramInfo = fieldName.split("_");
                    fieldName = paramInfo[0];
                    fieldCondition = "from".equals(paramInfo[1]) ? ">" : "to".equals(paramInfo[1]) ? "<" : "=";
                }

                Field field = FieldUtils.getField(exportTemplate.getEntityToExport(), fieldName, true);
                if (field == null) {
                    continue;
                }

                sql.append(firstWhere ? " where " : " and ").append(String.format(" %s%s:%s", fieldName, fieldCondition, paramName));
                firstWhere = false;
                parametersToApply.put(paramName, paramValue);
            }
        }

        // Do a search

        Query query = getEntityManager().createQuery(sql.toString());
        for (Entry<String, Object> param : parametersToApply.entrySet()) {
            if (param.getValue() != null) {
                query.setParameter(param.getKey(), param.getValue());
            }
        }
        List<IEntity> entities = query.getResultList();

        String xml = null;
        if (!entities.isEmpty()) {
            xml = xstream.toXML(entities);
            exportStats.updateSummary(exportTemplate.getEntityToExport(), entities.size());
            if (parameters.containsKey("delete") && (Boolean) parameters.get("delete")) {
                exportStats.trackEntitiesToDelete(entities);
            }
        }
        log.info("Serialized {} entities from export template {}", entities.size(), exportTemplate.getName());
        log.debug("XML is {}", xml);
        return xml;

    }

    /**
     * Import entities from xml stream.
     * 
     * @param inputStreamXML stream contains a template that was used to export data and serialized data
     * @param preserveId Should Ids of entities be preserved when importing instead of using sequence values for ID generation (DOES NOT WORK)
     * @param ignoreNotFoundFK Should import fail if any FK was not found
     * @param forceToProvider Ignore provider specified in an entity and force provider value to this value
     * @return Import statistics
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ExportImportStatistics importEntities(InputStream inputStream, boolean preserveId, boolean ignoreNotFoundFK, Provider forceToProvider) {

        XStream xstream = new XStream();
        xstream.alias("exportInfo", ExportInfo.class);
        xstream.useAttributeFor(ExportTemplate.class, "name");
        xstream.useAttributeFor(ExportTemplate.class, "entityToExport");
        xstream.useAttributeFor(ExportTemplate.class, "canDeleteAfterExport");
        List<ExportInfo> exportInfos = (List<ExportInfo>) xstream.fromXML(inputStream);

        if (forceToProvider != null) {
            forceToProvider = emTarget.createQuery("select p from Provider p where p.code=:code", Provider.class).setParameter("code", forceToProvider.getCode()).getSingleResult();
        }

        ExportImportStatistics importStatsTotal = new ExportImportStatistics();
        for (ExportInfo exportInfo : exportInfos) {
            ExportImportStatistics importStats = importEntities(exportInfo.exportTemplate, exportInfo.serializedData, preserveId, ignoreNotFoundFK, forceToProvider);
            importStatsTotal.updateSummary(importStats);
        }
        return importStatsTotal;
    }

    /**
     * Import entities
     * 
     * @param exportTemplate Export template used to export data
     * @param serializedData Serialized data
     * @param preserveId Should Ids of entities be preserved when importing instead of using sequence values for ID generation (DOES NOT WORK)
     * @param ignoreNotFoundFK Should import fail if any FK was not found
     * @param forceToProvider Ignore provider specified in an entity and force provider value to this value
     * @return Import statistics
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ExportImportStatistics importEntities(ExportTemplate exportTemplate, String serializedData, boolean preserveId, boolean ignoreNotFoundFK, Provider forceToProvider) {

        if (serializedData == null) {
            log.info("No entities to import from {} export template ", exportTemplate.getName());
            return null;
        }
        log.info("Importing entities from template {} ignore not found FK={}, forcing import to a provider {}", exportTemplate.getName(), ignoreNotFoundFK, forceToProvider);
        log.trace("Importing entities from xml {}", serializedData);

        ExportImportStatistics importStats = null;
        XStream xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new HibernateMapper(next);
            }
        };

        ExportImportConfig exportImportConfig = new ExportImportConfig(exportTemplate, exportIdMapping);
        xstream.registerConverter(new IEntityExportIdentifierConverter(exportImportConfig, emTarget, preserveId, ignoreNotFoundFK, forceToProvider), XStream.PRIORITY_NORMAL);
        xstream.registerConverter(new IEntityClassConverter(xstream.getMapper(), xstream.getReflectionProvider(), preserveId), XStream.PRIORITY_LOW);

        List<? extends IEntity> entities = null;
        try {
            entities = (List<? extends IEntity>) xstream.fromXML(serializedData);
            importStats = saveEntitiesToTarget(entities, preserveId, forceToProvider);

        } catch (Exception e) {
            log.error("Failed to import XML contents. Template {}, serialized data {}: ", exportTemplate.getName(), serializedData, e);
            throw new RuntimeException("Failed to import XML contents. Template " + exportTemplate.getName() + ". " + e.getMessage(), e);
        }

        log.info("Imported {} entities from {} export template ", entities.size(), exportTemplate.getName());

        return importStats;

    }

    /**
     * Save entities to a target DB
     * 
     * @param entities Entities to save
     * @param lookupById Should a lookup of existing entity in DB be done by ID or by attributes
     * @param forceToProvider Ignore provider specified in an entity and force provider value to this value
     */
    private ExportImportStatistics saveEntitiesToTarget(List<? extends IEntity> entities, boolean lookupById, Provider forceToProvider) {

        ExportImportStatistics importStats = new ExportImportStatistics();

        for (IEntity entityToSave : entities) {

            // Check if entity to be saved is a provider entity but were told to force importing to a given provider - just replace a code
            if (forceToProvider != null && entityToSave instanceof Provider) {
                ((Provider) entityToSave).setCode(forceToProvider.getCode());
            }
            saveEntityToTarget(entityToSave, lookupById, importStats, false);
        }
        return importStats;
    }

    /**
     * Save entity to a target DB
     * 
     * @param entityToSave Entity to save
     * @param lookupById Should a lookup of existing entity in DB be done by ID or by attributes
     * @param importStats Import statistics
     * @param saveUpdateOnly Should only existing entity be saved
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private IEntity saveEntityToTarget(IEntity entityToSave, boolean lookupById, ExportImportStatistics importStats, boolean updateExistingOnly) {

        log.debug("Saving entity {} preserveId={}", entityToSave, lookupById);

        // Check that entity does not exist yet
        IEntity entityFound = null;

        // Check by id
        if (lookupById && entityToSave.getId() != null) {
            entityFound = emTarget.find(entityToSave.getClass(), entityToSave.getId());
        } else {
            entityFound = findEntityByAttributes(entityToSave);
        }

        if (entityFound == null && updateExistingOnly) {
            log.debug("No existing entity was found. Entity will be saved by other means.");
            return entityToSave;
        }

        if (entityFound == null) {
            // Clear version field
            if (IVersionedEntity.class.isAssignableFrom(entityToSave.getClass())) {
                ((IVersionedEntity) entityToSave).setVersion(null);
            }

            saveNotManagedFields(entityToSave, lookupById, importStats);
            emTarget.persist(entityToSave);

            log.debug("Entity {} saved", entityToSave);

        } else {
            log.debug("Existing entity found with ID {}. Entity will be updated.", entityFound.getId());
            updateEntityFromDB(entityFound, entityToSave, lookupById, importStats);

            log.debug("Entity {} saved", entityFound);
        }

        List extractedRelatedEntities = extractNonCascadedEntities(entityToSave);
        if (extractedRelatedEntities != null && !extractedRelatedEntities.isEmpty()) {
            ExportImportStatistics importStatsRelated = saveEntitiesToTarget(extractedRelatedEntities, lookupById, null);
            importStats.updateSummary(importStatsRelated);
        }

        // Update statistics
        importStats.updateSummary(entityToSave.getClass(), 1);

        return entityFound == null ? entityToSave : entityFound;
    }

    /**
     * Extract entities referred from a given entity that would not be persisted when a given entity is saved
     * 
     * @param entityToSave Entity to analyse
     * @return A list of entities to save
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List extractNonCascadedEntities(IEntity entityToSave) {

        List<Field> fields = nonCascadableFields.get(entityToSave.getClass());
        if (fields == null) {
            return null;
        }

        List nonCascadedEntities = new ArrayList<>();
        for (Field field : fields) {
            try {
                Object fieldValue = FieldUtils.readField(field, entityToSave, true);
                if (fieldValue == null) {
                    continue;
                }
                if (Map.class.isAssignableFrom(field.getType())) {
                    Map mapValue = (Map) fieldValue;
                    if (!mapValue.isEmpty()) {
                        nonCascadedEntities.addAll(mapValue.values());
                        log.trace("Extracted non-cascaded fields {} from {}", mapValue.values(), entityToSave.getClass().getName() + "." + field.getName());
                    }
                } else if (Set.class.isAssignableFrom(field.getType())) {
                    Set setValue = (Set) fieldValue;
                    if (!setValue.isEmpty()) {
                        nonCascadedEntities.addAll(setValue);
                        log.trace("Extracted non-cascaded fields {} from {}", setValue, entityToSave.getClass().getName() + "." + field.getName());
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    List listValue = (List) fieldValue;
                    if (!listValue.isEmpty()) {
                        nonCascadedEntities.addAll(listValue);
                        log.trace("Extracted non-cascaded fields {} from {}", listValue, entityToSave.getClass().getName() + "." + field.getName());
                    }
                }

            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("Failed to access field " + field.getName() + " in class " + entityToSave.getClass().getName(), e);
            }

        }
        return nonCascadedEntities;
    }

    /**
     * Copy data from deserialized entity to an entity from DB
     * 
     * @param entityFromDB Entity found in DB
     * @param entityDeserialized Entity deserialised
     * @param lookupById Should a lookup of existing entity in DB be done by ID or by attributes
     * @param importStats
     * @return A updated
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void updateEntityFromDB(IEntity entityFromDB, IEntity entityDeserialized, boolean lookupById, ExportImportStatistics importStats) {

        if (HibernateProxy.class.isAssignableFrom(entityFromDB.getClass())) {
            entityFromDB = (IEntity) ((HibernateProxy) entityFromDB).getHibernateLazyInitializer().getImplementation();
        }

        // Update id and version fields, so if entity was referred from other importing entities, it would be referring to a newly saved entity
        entityDeserialized.setId((Long) entityFromDB.getId());
        log.trace("Deserialized entity updated with id {}", entityFromDB.getId());

        if (IVersionedEntity.class.isAssignableFrom(entityDeserialized.getClass())) {
            ((IVersionedEntity) entityDeserialized).setVersion(((IVersionedEntity) entityFromDB).getVersion());
            log.trace("Deserialized entity updated with version {}", ((IVersionedEntity) entityFromDB).getVersion());
        }

        Class clazz = entityDeserialized.getClass();
        Class cls = clazz;
        while (!Object.class.equals(cls) && cls != null) {

            for (Field field : cls.getDeclaredFields()) {
                try {
                    // Do not overwrite id, version and static fields
                    if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Version.class) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    Object sourceValue = FieldUtils.readField(field, entityDeserialized, true);

                    // Do not overwrite fields that should have been omitted during export, unless they are not empty
                    if (sourceValue == null && attributesToOmit.containsKey(clazz.getName() + "." + field.getName())) {
                        continue;
                    }

                    // Do not overwrite @oneToMany and @oneToOne fields THAT DO NOT CASCASE as they wont be saved anyway - that is handled apart in saveEntityToTarget()
                    if (field.isAnnotationPresent(OneToMany.class)) {
                        OneToMany oneToManyAnotation = field.getAnnotation(OneToMany.class);
                        if (!(ArrayUtils.contains(oneToManyAnotation.cascade(), CascadeType.ALL) || ArrayUtils.contains(oneToManyAnotation.cascade(), CascadeType.MERGE) || ArrayUtils
                            .contains(oneToManyAnotation.cascade(), CascadeType.PERSIST))) {
                            continue;
                        }

                        // Extract @oneToOne fields that do not cascade
                    } else if (field.isAnnotationPresent(OneToOne.class)) {
                        OneToOne oneToOneAnotation = field.getAnnotation(OneToOne.class);
                        if (!(ArrayUtils.contains(oneToOneAnotation.cascade(), CascadeType.ALL) || ArrayUtils.contains(oneToOneAnotation.cascade(), CascadeType.MERGE) || ArrayUtils
                            .contains(oneToOneAnotation.cascade(), CascadeType.PERSIST))) {
                            continue;
                        }
                    }

                    // Save related entities that were not saved during main entity saving
                    sourceValue = saveNotManagedField(sourceValue, entityDeserialized, field, lookupById, importStats, clazz);

                    // Populate existing Map, List and Set type fields by modifying field contents instead of rewriting a whole field
                    if (Map.class.isAssignableFrom(field.getType())) {
                        Map targetValue = (Map) FieldUtils.readField(field, entityFromDB, true);
                        if (targetValue != null) {
                            targetValue.clear();
                            targetValue.putAll((Map) sourceValue);
                        } else {
                            FieldUtils.writeField(field, entityFromDB, sourceValue, true);
                        }
                        log.trace("Populating field " + field.getName() + " with " + sourceValue);

                    } else if (Set.class.isAssignableFrom(field.getType())) {
                        Set targetValue = (Set) FieldUtils.readField(field, entityFromDB, true);
                        if (targetValue != null) {
                            targetValue.clear();
                            targetValue.addAll((Set) sourceValue);
                        } else {
                            FieldUtils.writeField(field, entityFromDB, sourceValue, true);
                        }
                        log.trace("Populating field " + field.getName() + " with " + sourceValue);

                    } else if (List.class.isAssignableFrom(field.getType())) {
                        List targetValue = (List) FieldUtils.readField(field, entityFromDB, true);
                        if (targetValue != null) {
                            targetValue.clear();
                            targetValue.addAll((List) sourceValue);
                        } else {
                            FieldUtils.writeField(field, entityFromDB, sourceValue, true);
                        }

                        log.trace("Populating field " + field.getName() + " with " + sourceValue);

                    } else {

                        log.trace("Setting field " + field.getName() + " to " + sourceValue);
                        FieldUtils.writeField(field, entityFromDB, sourceValue, true);

                    }

                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException("Failed to access field " + field.getName() + " in class " + clazz.getName(), e);
                }
            }
            cls = cls.getSuperclass();
        }

        // entityFromDB = emTarget.merge(entityFromDB);

    }

    /**
     * Determine if fields that are entity type fields are managed, and if they are not managed yet - save them first
     * 
     * @param entityDeserialized Entity deserialised
     * @param lookupById Should a lookup of existing entity in DB be done by ID or by attributes
     * @param importStats Import statistics
     */
    @SuppressWarnings({ "rawtypes" })
    private void saveNotManagedFields(IEntity entityDeserialized, boolean lookupById, ExportImportStatistics importStats) {

        Class clazz = entityDeserialized.getClass();
        Class cls = clazz;
        while (!Object.class.equals(cls) && cls != null) {

            for (Field field : cls.getDeclaredFields()) {
                try {
                    // Do not overwrite id, version and static fields
                    if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Version.class) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    saveNotManagedField(null, entityDeserialized, field, lookupById, importStats, clazz);

                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException("Failed to access field " + clazz.getSimpleName() + "." + field.getName(), e);
                }
            }
            cls = cls.getSuperclass();
        }
    }

    /**
     * Determine if field is an entity type field, and if it is not managed yet - save it first. TODO fix here as when lookupById, id value would be filled already
     * 
     * @param fieldValue Field value
     * @param entity Entity to obtain field value from if one is not provided. Also used to update the value if it was saved (after merge)
     * @param field Field definition
     * @param lookupById Is a lookup for FK or entity duplication performed by ID or attributes
     * @param importStats Import statistics
     * @param clazz A class of an entity that this field belongs to
     * @throws IllegalAccessException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object saveNotManagedField(Object fieldValue, IEntity entity, Field field, boolean lookupById, ExportImportStatistics importStats, Class clazz)
            throws IllegalAccessException {

        if (fieldValue == null) {
            fieldValue = FieldUtils.readField(field, entity, true);
            if (fieldValue == null) {
                return fieldValue;
            }
        }

        // Do not care about @oneToMany and @OneToOne fields that do not cascade they will be ignored anyway and their saving is handled in saveEntityToTarget()
        boolean isCascadedField = false;
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToManyAnotation = field.getAnnotation(OneToMany.class);
            if (!(ArrayUtils.contains(oneToManyAnotation.cascade(), CascadeType.ALL) || ArrayUtils.contains(oneToManyAnotation.cascade(), CascadeType.MERGE) || ArrayUtils
                .contains(oneToManyAnotation.cascade(), CascadeType.PERSIST))) {
                return fieldValue;
            } else {
                isCascadedField = true;
            }

            // Extract @oneToOne fields that do not cascade
        } else if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne oneToOneAnotation = field.getAnnotation(OneToOne.class);
            if (!(ArrayUtils.contains(oneToOneAnotation.cascade(), CascadeType.ALL) || ArrayUtils.contains(oneToOneAnotation.cascade(), CascadeType.MERGE) || ArrayUtils.contains(
                oneToOneAnotation.cascade(), CascadeType.PERSIST))) {
                return fieldValue;
            } else {
                isCascadedField = true;
            }
        }

        // Do not care about non-entity type fields
        if (!checkIfFieldIsOfType(field, IEntity.class)) {
            return fieldValue;
        }

        // Ensure that field value is managed (or saved) before continuing
        boolean isManaged = false;

        // Examine Map, List and Set type fields to see if they are persisted, and if not - persist them
        if (Map.class.isAssignableFrom(field.getType())) {
            Map mapValue = (Map) fieldValue;
            for (Object entry : mapValue.entrySet()) {
                Object key = ((Entry) entry).getKey();
                Object singleValue = ((Entry) entry).getValue();
                if (singleValue == null) {
                    continue;
                }
                // If entity is managed, then continue on. Update value in a map with a new value. TODO fix here as when lookupById, id value would be filled already
                isManaged = ((IEntity) singleValue).getId() != null; // emTarget.contains(singleValue);
                if (!isManaged) {
                    log.debug("Persisting child field {}.{}'s (cascaded={}) value {}", clazz.getSimpleName(), field.getName(), isCascadedField, singleValue);
                    mapValue.put(key, saveEntityToTarget((IEntity) singleValue, lookupById, importStats, isCascadedField));
                    // } else {
                    // log.trace("Persisting field {}.{} is managed", clazz.getSimpleName(), field.getName());
                }
            }

        } else if (Collection.class.isAssignableFrom(field.getType())) {
            Collection collectionValue = (Collection) fieldValue;
            Object[] collectionValues = collectionValue.toArray();

            // Clear and construct collection again with updated values (if were not managed before)
            collectionValue.clear();
            for (Object singleValue : collectionValues) {
                if (singleValue == null) {
                    continue;
                }
                // If entity is managed, then save it. TODO fix here as when lookupById, id value would be filled already
                isManaged = ((IEntity) singleValue).getId() != null; // emTarget.contains(singleValue);
                if (!isManaged) {
                    log.debug("Persisting child field {}.{}'s (cascaded={}) value {}", clazz.getSimpleName(), field.getName(), isCascadedField, singleValue);
                    collectionValue.add(saveEntityToTarget((IEntity) singleValue, lookupById, importStats, isCascadedField));
                } else {
                    collectionValue.add(singleValue);
                    // log.trace("Persisting field {}.{} is managed", clazz.getSimpleName(), field.getName());
                }
            }

        } else {

            // If entity is managed, then save it. TODO fix here as when lookupById, id value would be filled already
            isManaged = ((IEntity) fieldValue).getId() != null; // emTarget.contains(fieldValue);
            if (!isManaged) {
                log.debug("Persisting child field {}.{}'s (cascaded={}) value {}", clazz.getSimpleName(), field.getName(), isCascadedField, fieldValue);
                fieldValue = saveEntityToTarget((IEntity) fieldValue, lookupById, importStats, isCascadedField);
                // Update field value in an entity with a new value
                FieldUtils.writeField(field, entity, fieldValue, true);
                // } else {
                // log.trace("Persisting field {}.{} is managed", clazz.getSimpleName(), field.getName());
            }
        }

        return fieldValue;
    }

    /**
     * Find an entity in target db by attributes
     * 
     * @param entityToSave Entity to match
     * @return Entity found in target DB
     */
    private IEntity findEntityByAttributes(IEntity entityToSave) {
        String[] attributes = exportIdMapping.get(entityToSave.getClass());
        if (attributes == null) {
            return null;
        }

        Map<String, Object> parameters = new HashMap<String, Object>();

        for (String attributeName : attributes) {

            Object attrValue;
            try {
                attrValue = getAttributeValue(entityToSave, attributeName);
                if (attrValue != null) {
                    parameters.put(attributeName, attrValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access " + entityToSave.getClass().getName() + "." + attributeName + "field", e);
            }
        }

        // Construct a query to retrieve an entity by the attributes
        StringBuilder sql = new StringBuilder("select o from " + entityToSave.getClass().getName() + " o where ");
        boolean firstWhere = true;
        for (Entry<String, Object> param : parameters.entrySet()) {
            if (!firstWhere) {
                sql.append(" and ");
            }
            sql.append(String.format(" %s=:%s", param.getKey(), param.getKey().replace('.', '_')));
            firstWhere = false;
        }
        Query query = emTarget.createQuery(sql.toString());
        for (Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey().replace('.', '_'), param.getValue());
        }
        try {
            IEntity entity = (IEntity) query.getSingleResult();
            log.trace("Found entity {} id={} with attributes {}. Entity will be updated.", entity.getClass().getName(), entity.getId(), parameters);
            return entity;

        } catch (NoResultException | NonUniqueResultException e) {
            log.debug("Entity {} not found matching attributes: {}. Entity will be inserted.", entityToSave.getClass().getName(), parameters);
            return null;

        } catch (Exception e) {
            log.error("Failed to search for entity {} with attributes: {} sql {}", entityToSave.getClass().getName(), parameters, sql, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get an attribute value. Handles composed attribute cases (e.g. provider.code)
     * 
     * @param object Object to get attribute value from
     * @param attributeName Attribute name. Can be a composed attribute name
     * @return Attribute value
     * @throws IllegalAccessException
     */
    private Object getAttributeValue(Object object, String attributeName) throws IllegalAccessException {

        Object value = object;
        StringTokenizer tokenizer = new StringTokenizer(attributeName, ".");
        while (tokenizer.hasMoreElements()) {
            String attrName = tokenizer.nextToken();
            value = FieldUtils.readField(value, attrName, true);
            if (value instanceof HibernateProxy) {
                value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
            } else if (value == null) {
                return null;
            }
        }
        return value;
    }

    /**
     * Determine what attributes are treated as identifiers for export for an entity. Such information is provided by @ExportIdentifier annotation on an entity.
     * 
     * @return A map with such format: <Entity class, an array of entity attribute names>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<Class<? extends IEntity>, String[]> loadExportIdentifierMappings() {
        Map<Class<? extends IEntity>, String[]> exportIdMap = new HashMap<Class<? extends IEntity>, String[]>();
        IEntity.class.getPackage();
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
        }

        for (Class clazz : classes) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || clazz.isAnnotation() || !IEntity.class.isAssignableFrom(clazz)) {
                continue;
            }

            if (clazz.isAnnotationPresent(ExportIdentifier.class)) {
                exportIdMap.put(clazz, ((ExportIdentifier) clazz.getAnnotation(ExportIdentifier.class)).value());
            }

        }
        return exportIdMap;
    }

    /**
     * Determine what attributes should be omitted for export for an entity. Attributes annotated with @OneToMany annotation should be omitted.
     * 
     * @return A map of <classname.fieldname,array of [Class, attribute name]>
     */
    @SuppressWarnings({ "rawtypes" })
    private Map<String, Object[]> loadAtributesToOmit() {
        Map<String, Object[]> attributesToOmit = new HashMap<String, Object[]>();
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
        }

        for (Class clazz : classes) {

            if (clazz.isInterface() || clazz.isAnnotation() || !IEntity.class.isAssignableFrom(clazz)) {
                continue;
            }

            Class cls = clazz;
            while (!Object.class.equals(cls) && cls != null) {

                for (Field field : cls.getDeclaredFields()) {

                    if (field.isAnnotationPresent(Transient.class)) {
                        attributesToOmit.put(clazz.getName() + "." + field.getName(), new Object[] { clazz, field });

                    } else if (field.isAnnotationPresent(OneToMany.class)) {

                        // Omit attribute only if backward relationship is set
                        // boolean hasBackwardRelationship = checkIfClassContainsFieldOfType(field.getGenericType(), clazz);
                        // if (hasBackwardRelationship) {
                        attributesToOmit.put(clazz.getName() + "." + field.getName(), new Object[] { clazz, field });
                        // } else {
                        // log.error("AKK field " + field.getName() + " of generic type " + field.getGenericType() + "will not be omited from " + clazz.getSimpleName());
                        // }
                    }
                }

                cls = cls.getSuperclass();
            }
        }
        return attributesToOmit;
    }

    /**
     * Identify fields in classes that contain a list of related entities (@OneToMany and @OneToOne), but are not cascaded
     * 
     * @return A map of <Class, List of non-cascaded fields>
     */
    @SuppressWarnings("rawtypes")
    private Map<Class, List<Field>> loadNonCascadableFields() {

        Map<Class, List<Field>> nonCascadableFields = new HashMap<Class, List<Field>>();
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
        }

        for (Class clazz : classes) {
            if (clazz.isInterface() || clazz.isAnnotation() || !IEntity.class.isAssignableFrom(clazz)) {
                continue;
            }
            List<Field> classNonCascadableFields = new ArrayList<Field>();

            Class cls = clazz;
            while (!Object.class.equals(cls) && cls != null) {
                for (Field field : cls.getDeclaredFields()) {

                    // Skip id, version and static fields
                    if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Version.class) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    // Extract @oneToMany fields that do not cascade
                    if (field.isAnnotationPresent(OneToMany.class)) {
                        OneToMany oneToManyAnotation = field.getAnnotation(OneToMany.class);
                        if (!(ArrayUtils.contains(oneToManyAnotation.cascade(), CascadeType.ALL) || ArrayUtils.contains(oneToManyAnotation.cascade(), CascadeType.MERGE) || ArrayUtils
                            .contains(oneToManyAnotation.cascade(), CascadeType.PERSIST))) {
                            classNonCascadableFields.add(field);
                        }

                        // Extract @oneToOne fields that do not cascade
                    } else if (field.isAnnotationPresent(OneToOne.class)) {
                        OneToOne oneToOneAnotation = field.getAnnotation(OneToOne.class);
                        if (!(ArrayUtils.contains(oneToOneAnotation.cascade(), CascadeType.ALL) || ArrayUtils.contains(oneToOneAnotation.cascade(), CascadeType.MERGE) || ArrayUtils
                            .contains(oneToOneAnotation.cascade(), CascadeType.PERSIST))) {
                            classNonCascadableFields.add(field);
                        }
                    }
                }

                cls = cls.getSuperclass();
            }
            if (!classNonCascadableFields.isEmpty()) {
                nonCascadableFields.put(clazz, classNonCascadableFields);
            }
        }
        return nonCascadableFields;
    }

    /**
     * Check if parameterized class contains a non-transient field of given type
     * 
     * @param type Parameterized type to examine
     * @param classToMatch Class type to match
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    private boolean checkIfClassContainsFieldOfType(Type type, Class classToMatch) {
        Class classToCheck = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) type;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                Class fieldArgClass = (Class) fieldArgType;
                if (IEntity.class.isAssignableFrom(fieldArgClass)) {
                    classToCheck = fieldArgClass;
                    break;
                }
            }
        }

        for (Field field : classToCheck.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Transient.class) && IEntity.class.isAssignableFrom(field.getDeclaringClass()) && field.getType().isAssignableFrom(classToMatch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if class field is of type - including List<>, Map<>, Set<> and potentially other parameterized classes
     * 
     * @param field Field to analyse
     * @param typesToCheck Class to match
     * @return True is field is of type classToMatch or is parameterized with classToMatch class (e.g. List<classToMatch>
     */
    private boolean checkIfFieldIsOfType(Field field, Collection<Class<? extends IEntity>> typesToCheck) {
        for (Class<? extends IEntity> typeToCheck : typesToCheck) {
            boolean isOfType = checkIfFieldIsOfType(field, typeToCheck);
            if (isOfType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if class field is of type - including List<>, Map<>, Set<> and potentially other parameterized classes
     * 
     * @param field Field to analyse
     * @param typeToCheck Class to match
     * @return True is field is of type classToMatch or is parameterized with classToMatch class (e.g. List<classToMatch>
     */
    @SuppressWarnings("rawtypes")
    private boolean checkIfFieldIsOfType(Field field, Class<? extends IEntity> typeToCheck) {
        if (typeToCheck.isAssignableFrom(field.getType())) {
            return true;
        } else if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) field.getGenericType();
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                Class fieldArgClass = (Class) fieldArgType;
                if (typeToCheck.isAssignableFrom(fieldArgClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Specify Xstream what attributes to omit.
     * 
     * @param xstream Instance to apply to
     * @param typesNotToOmit Types not to omit
     * @param typesNotToOmit
     */
    @SuppressWarnings("rawtypes")
    private void applyAttributesToOmit(XStream xstream, Collection<Class<? extends IEntity>> typesNotToOmit) {
        for (Object[] classFieldInfo : attributesToOmit.values()) {
            if (typesNotToOmit != null && checkIfFieldIsOfType((Field) classFieldInfo[1], typesNotToOmit)) {
                log.trace("Explicitly not omitting {}.{} attribute from export", classFieldInfo[0], ((Field) classFieldInfo[1]).getName());
                continue;
            }
            log.trace("Will ommit {}.{} attribute from export", classFieldInfo[0], ((Field) classFieldInfo[1]).getName());
            xstream.omitField((Class) classFieldInfo[0], ((Field) classFieldInfo[1]).getName());
        }
        xstream.omitField(Auditable.class, "creator");
        xstream.omitField(Auditable.class, "updater");
    }

    private static class ExportInfo {

        public ExportInfo(ExportTemplate exportTemplate, String serializedData) {
            this.exportTemplate = exportTemplate;
            this.serializedData = serializedData;
        }

        ExportTemplate exportTemplate;
        String serializedData;
    }
}