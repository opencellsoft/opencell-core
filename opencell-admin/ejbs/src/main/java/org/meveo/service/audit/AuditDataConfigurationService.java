package org.meveo.service.audit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.audit.AuditCrudActionEnum;
import org.meveo.model.audit.AuditDataConfiguration;
import org.meveo.service.base.PersistenceService;

/**
 * A service for managing AuditDataConfiguration entities
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class AuditDataConfigurationService extends PersistenceService<AuditDataConfiguration> {

    private static final Map<String, AuditDataHierarchy> auditDataHierarchies = new HashMap<>();

    /**
     * Find data audit configuration by entity class - a full classname including a package name
     * 
     * @param entityClass Entity class
     * @return Data audit configuration
     * @throws ClassNotFoundException
     */
    public AuditDataConfiguration findByEntityClass(String entityClass) {

        if (!entityClass.contains(".")) {
            try {
                entityClass = getClassFromShortName(entityClass).getName();
            } catch (ClassNotFoundException e) {
                throw new BusinessException(e);
            }
        }

        try {
            return getEntityManager().createNamedQuery("AuditDataConfiguration.findByEntityClass", AuditDataConfiguration.class).setParameter("entityClass", entityClass).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<AuditDataConfiguration> list(PaginationConfiguration config) {

        if (config != null && !config.getFilters().isEmpty() && config.getFilters().containsKey("entityClass")) {
            String entityClass = (String) config.getFilters().get("entityClass");

            if (!entityClass.contains(".")) {
                try {
                    config.getFilters().put("entityClass", getClassFromShortName(entityClass).getName());
                } catch (ClassNotFoundException e) {
                    throw new BusinessException(e);
                }
            }
        }

        return super.list(config);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void create(AuditDataConfiguration auditDataConfig) {

        // Validate that entity class is a valid class and convert a simple name into a full class name
        try {
            Class clazz = null;
            try {
                clazz = Class.forName(auditDataConfig.getEntityClass());
            } catch (ClassNotFoundException e) {
                clazz = getClassFromShortName(auditDataConfig.getEntityClass());
            }

            auditDataConfig.setEntityClass(clazz.getName());

        } catch (ClassNotFoundException e) {
            throw new BusinessException(e);
        }

        super.create(auditDataConfig);

        recreateAuditTriggerInDB(auditDataConfig);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public AuditDataConfiguration update(AuditDataConfiguration auditDataConfig) {

        // Validate that entity class is a valid class and convert a simple name into a full class name
        try {
            Class clazz = null;
            try {
                clazz = Class.forName(auditDataConfig.getEntityClass());
            } catch (ClassNotFoundException e) {
                clazz = getClassFromShortName(auditDataConfig.getEntityClass());
            }

            auditDataConfig.setEntityClass(clazz.getName());

        } catch (ClassNotFoundException e) {
            throw new BusinessException(e);
        }

        auditDataConfig = super.update(auditDataConfig);

        recreateAuditTriggerInDB(auditDataConfig);

        return auditDataConfig;
    }

    @Override
    public void remove(AuditDataConfiguration auditDataConfig) {
        super.remove(auditDataConfig);
        removeAuditTriggerInDB(auditDataConfig);
    }

    /**
     * Create a trigger for auditing data changes in DB
     * 
     * @param auditDataConfig Data audit configuration
     * @throws BusinessException Unable to determine auditing rules for a class or to create a trigger
     */
    @SuppressWarnings({ "rawtypes" })
    private void recreateAuditTriggerInDB(AuditDataConfiguration auditDataConfig) throws BusinessException {

        try {
            Class clazz = null;
            try {
                clazz = Class.forName(auditDataConfig.getEntityClass());
            } catch (ClassNotFoundException e) {
                clazz = getClassFromShortName(auditDataConfig.getEntityClass());
            }

            AuditDataHierarchy auditDataHierarchy = getAuditDataHierarchy(clazz);

            if (auditDataHierarchy == null) {
                return;
            }

            String dbFields = null;

            EntityManager em = getEntityManager();

            List<String> fieldNames = null;

            if (auditDataConfig.getFields() != null) {
                fieldNames = new ArrayList<>();
                for (String fieldName : auditDataConfig.getFields().split(",")) {
                    fieldName = fieldName.trim();
                    fieldNames.add(fieldName);
                    Field field = ReflectionUtils.getField(clazz, fieldName);
                    if (field.isAnnotationPresent(Column.class)) {
                        Column columnDefinition = field.getAnnotation(Column.class);
                        if (columnDefinition != null) {
                            dbFields = dbFields == null ? columnDefinition.name() : (dbFields + "," + columnDefinition.name());
                        }
                    } else if (field.isAnnotationPresent(JoinColumn.class)) {
                        JoinColumn columnDefinition = field.getAnnotation(JoinColumn.class);
                        if (columnDefinition != null) {
                            dbFields = dbFields == null ? columnDefinition.name() : (dbFields + "," + columnDefinition.name());
                        }
                    }
                }
            }

            boolean isCreate = auditDataConfig.getActions() == null || auditDataConfig.getActions().toUpperCase().contains(AuditCrudActionEnum.INSERT.name());
            boolean isUpdate = auditDataConfig.getActions() == null || auditDataConfig.getActions().toUpperCase().contains(AuditCrudActionEnum.UPDATE.name());
            boolean isDelete = auditDataConfig.getActions() == null || auditDataConfig.getActions().toUpperCase().contains(AuditCrudActionEnum.DELETE.name());

            em.createNamedStoredProcedureQuery("AuditDataConfiguration.recreateDataAuditTrigger").setParameter("tableName", auditDataHierarchy.getTableName()).setParameter("fields", dbFields)
                .setParameter("actions", auditDataConfig.getActions()).setParameter("preserveField", auditDataHierarchy.getParentIdDbColumn()).execute();

            // Create triggers for any @JoinTable and @OneToMany with Cascade=All/Persist/Merge
            for (AuditDataHierarchy fieldAuditDataHierarchy : auditDataHierarchy.getRelatedEntities()) {
                if (fieldNames != null && !fieldNames.contains(fieldAuditDataHierarchy.getFieldName())) {
                    continue;
                }

                String fieldActions = null;

                if (isCreate && fieldAuditDataHierarchy.isActionEnabled(AuditCrudActionEnum.INSERT)) {
                    fieldActions = (fieldActions == null ? "" : fieldActions + ",") + AuditCrudActionEnum.INSERT;
                }
                if (isUpdate && fieldAuditDataHierarchy.isActionEnabled(AuditCrudActionEnum.UPDATE)) {
                    fieldActions = (fieldActions == null ? "" : fieldActions + ",") + AuditCrudActionEnum.UPDATE;
                }
                if (isDelete && fieldAuditDataHierarchy.isActionEnabled(AuditCrudActionEnum.DELETE)) {
                    fieldActions = (fieldActions == null ? "" : fieldActions + ",") + AuditCrudActionEnum.DELETE;
                }

                if (fieldActions == null) {
                    continue;
                }

                if (fieldAuditDataHierarchy.getRelatedEntities().isEmpty()) {
                    em.createNamedStoredProcedureQuery("AuditDataConfiguration.recreateDataAuditTrigger").setParameter("tableName", fieldAuditDataHierarchy.getTableName()).setParameter("fields", null)
                        .setParameter("actions", fieldActions).setParameter("preserveField", fieldAuditDataHierarchy.getParentIdDbColumn()).execute();

                } else {

                    AuditDataConfiguration fieldAuditDataConfig = new AuditDataConfiguration(fieldAuditDataHierarchy.getEntityClass().getName(), null, fieldActions);

                    recreateAuditTriggerInDB(fieldAuditDataConfig);
                }
            }

        } catch (Exception e) {
            throw new BusinessException("Unable to create/update DB based audit trigger for " + auditDataConfig, e);
        }
    }

    /**
     * Remove a trigger in DB for auditing data changes
     * 
     * @param auditDataConfig Data audit configuration
     * @throws BusinessException Unable to determine auditing rules for a class or to create a trigger
     */
    @SuppressWarnings({ "rawtypes" })
    private void removeAuditTriggerInDB(AuditDataConfiguration auditDataConfig) throws BusinessException {

        try {

            Class clazz = null;
            try {
                clazz = Class.forName(auditDataConfig.getEntityClass());
            } catch (ClassNotFoundException e) {
                clazz = getClassFromShortName(auditDataConfig.getEntityClass());
            }

            AuditDataHierarchy auditDataHierarchy = getAuditDataHierarchy(clazz);

            if (auditDataHierarchy == null) {
                return;
            }

            EntityManager em = getEntityManager();

            List<String> fieldNames = null;

            if (auditDataConfig.getFields() != null) {
                fieldNames = new ArrayList<>();
                for (String fieldName : auditDataConfig.getFields().split(",")) {
                    fieldName = fieldName.trim();
                    fieldNames.add(fieldName);
                }
            }

            boolean isCreate = auditDataConfig.getActions() == null || auditDataConfig.getActions().toUpperCase().contains(AuditCrudActionEnum.INSERT.name());
            boolean isUpdate = auditDataConfig.getActions() == null || auditDataConfig.getActions().toUpperCase().contains(AuditCrudActionEnum.UPDATE.name());
            boolean isDelete = auditDataConfig.getActions() == null || auditDataConfig.getActions().toUpperCase().contains(AuditCrudActionEnum.DELETE.name());

            em.createNamedStoredProcedureQuery("AuditDataConfiguration.deleteDataAuditTrigger").setParameter("tableName", auditDataHierarchy.getTableName()).execute();

            // Delete triggers for any @JoinTable and @OneToMany with Cascade=All/Persist/Merge
            for (AuditDataHierarchy fieldAuditDataHierarchy : auditDataHierarchy.getRelatedEntities()) {
                if (fieldNames != null && !fieldNames.contains(fieldAuditDataHierarchy.getFieldName())) {
                    continue;
                }

                if (fieldAuditDataHierarchy.getRelatedEntities().isEmpty()) {
                    em.createNamedStoredProcedureQuery("AuditDataConfiguration.deleteDataAuditTrigger").setParameter("tableName", fieldAuditDataHierarchy.getTableName()).execute();

                } else {
                    String oneToManyActions = null;

                    if (isCreate && fieldAuditDataHierarchy.isActionEnabled(AuditCrudActionEnum.INSERT)) {
                        oneToManyActions = (oneToManyActions == null ? "" : oneToManyActions + ",") + AuditCrudActionEnum.INSERT;
                    }
                    if (isUpdate && fieldAuditDataHierarchy.isActionEnabled(AuditCrudActionEnum.UPDATE)) {
                        oneToManyActions = (oneToManyActions == null ? "" : oneToManyActions + ",") + AuditCrudActionEnum.UPDATE;
                    }
                    if (isDelete && fieldAuditDataHierarchy.isActionEnabled(AuditCrudActionEnum.DELETE)) {
                        oneToManyActions = (oneToManyActions == null ? "" : oneToManyActions + ",") + AuditCrudActionEnum.DELETE;
                    }
                    if (oneToManyActions != null) {
                        AuditDataConfiguration fieldAuditDataConfig = new AuditDataConfiguration(fieldAuditDataHierarchy.getEntityClass().getName(), null, oneToManyActions);
                        removeAuditTriggerInDB(fieldAuditDataConfig);
                    }
                }
            }

        } catch (Exception e) {
            throw new BusinessException("Unable to delete DB based audit trigger for " + auditDataConfig, e);
        }
    }

    /**
     * Get a class from a short entity class name
     * 
     * @param shortClassName Short class name
     * @return An entity class from org.meveo.model package
     * @throws ClassNotFoundException Unable to determine a class
     */
    @SuppressWarnings("rawtypes")
    public static Class getClassFromShortName(String shortClassName) throws ClassNotFoundException {

        return ReflectionUtils.getClassBySimpleNameAndAnnotation(shortClassName, Entity.class);
    }

    /**
     * Get a list of all, currently data auditable entities.
     * 
     * @return A list of currently data auditable entities in a form of AuditDataHierarchy with field and related entity information
     */
    public List<AuditDataHierarchy> getAuditDataHierarchies() {

        List<AuditDataHierarchy> hierachies = new ArrayList<AuditDataHierarchy>();
        List<String> entityClasses = getEntityManager().createNamedQuery("AuditDataConfiguration.getEntityClasses", String.class).getResultList();
        for (String entityClassName : entityClasses) {
            try {
                @SuppressWarnings("rawtypes")
                Class clazz = Class.forName(entityClassName);
                hierachies.add(AuditDataConfigurationService.getAuditDataHierarchy(clazz));

            } catch (ClassNotFoundException e) {
                // Ignore - continue
                log.error("Unable to determine audit data hierarchy for class {}", entityClassName, e);
            }
        }

        return hierachies;
    }

    /**
     * Construct a hierarchy of classes with field information that are of interest for auditing data. Considers regular fields, @ManyToMany/@JoinTable relationships and traverse recursively into "@OneToMany with
     * Cascade=All/Persist/Merge" relationships
     * 
     * @param clazz Class to inspect
     * @return An Audit data hierarchy containing field and related entity information
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static AuditDataHierarchy getAuditDataHierarchy(Class clazz) {

        if (auditDataHierarchies.containsKey(clazz.getName())) {
            return auditDataHierarchies.get(clazz.getName());
        }

        String tableName = null;

        // There is no table for Embeddable classes
        if (!clazz.isAnnotationPresent(Embeddable.class)) {
            Class tableClazz = clazz;
            while (tableClazz != null) {
                Table entityDefinition = (Table) tableClazz.getAnnotation(Table.class);

                if (entityDefinition != null) {
                    tableName = entityDefinition.name();
                    break;

                } else {
                    tableClazz = tableClazz.getSuperclass();
                }
            }
            if (tableName == null) {
                return null;
            }
        }
        AuditDataHierarchy hierarcyInfo = new AuditDataHierarchy(clazz, tableName);

        // Find tables for any @JoinTable and @OneToMany with Cascade=All/Persist/Merge
        List<Field> entityFields = ReflectionUtils.getAllFields(clazz);
        for (Field field : entityFields) {

            // Ignore transient fields
            if (field.isAnnotationPresent(Transient.class)) {
                continue;

                // Include fields from Embedded entity
            } else if (field.isAnnotationPresent(Embedded.class)) {

                AuditDataHierarchy fieldHierarchy = getAuditDataHierarchy(field.getType());
                if (fieldHierarchy != null) {
                    hierarcyInfo.getRelatedEntities().addAll(fieldHierarchy.getRelatedEntities());
                    for (Entry<String, String> dbColumnToField : fieldHierarchy.getDbColumnToFieldMap().entrySet()) {
                        hierarcyInfo.getDbColumnToFieldMap().put(dbColumnToField.getKey(), field.getName() + "." + dbColumnToField.getValue());
                    }
                }

                // A regular trackable field
            } else if (field.isAnnotationPresent(Column.class)) {
                Column columnDefinition = field.getAnnotation(Column.class);
                if (columnDefinition != null) {
                    hierarcyInfo.getDbColumnToFieldMap().put(columnDefinition.name(), field.getName());
                }

                // A @ManyToOne relationship field
            } else if (field.isAnnotationPresent(JoinColumn.class)) {
                JoinColumn columnDefinition = field.getAnnotation(JoinColumn.class);
                if (columnDefinition != null) {
                    hierarcyInfo.getDbColumnToFieldMap().put(columnDefinition.name(), field.getName());
                }

                // @ManyToMany/@JoinTable relationship
            } else if (field.isAnnotationPresent(JoinTable.class)) {
                JoinTable joinTableDefinition = field.getAnnotation(JoinTable.class);
                if (joinTableDefinition.name() != null) {
                    AuditDataHierarchy hierarchyInfo = new AuditDataHierarchy(field.getName(), null, joinTableDefinition.name(), null);
                    hierarchyInfo.setParentIdDbColumn(joinTableDefinition.joinColumns()[0].name());
                    hierarchyInfo.setParentIdField(AuditDataHierarchy.LIST_ENTITY_FIELD_PARENTID);
                    hierarchyInfo.getDbColumnToFieldMap().put(hierarchyInfo.getParentIdDbColumn(), hierarchyInfo.getParentIdField());
                    hierarchyInfo.getDbColumnToFieldMap().put(joinTableDefinition.inverseJoinColumns()[0].name(), AuditDataHierarchy.LIST_ENTITY_FIELD_ID);
                    hierarcyInfo.getRelatedEntities().add(hierarchyInfo);

                }

                // @OneToMany with Cascade=All/Persist/Merge
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToManyDefinition = field.getAnnotation(OneToMany.class);
                if (oneToManyDefinition.cascade() != null) {
                    String oneToManyActions = null;
                    for (int i = 0; i < oneToManyDefinition.cascade().length; i++) {

                        if (oneToManyDefinition.cascade()[i] == CascadeType.ALL || oneToManyDefinition.cascade()[i] == CascadeType.PERSIST) {
                            oneToManyActions = (oneToManyActions == null ? "" : oneToManyActions + ",") + AuditCrudActionEnum.INSERT;
                        }
                        if (oneToManyDefinition.cascade()[i] == CascadeType.ALL || oneToManyDefinition.cascade()[i] == CascadeType.MERGE) {
                            oneToManyActions = (oneToManyActions == null ? "" : oneToManyActions + ",") + AuditCrudActionEnum.UPDATE;
                        }
                        if (oneToManyDefinition.cascade()[i] == CascadeType.ALL || oneToManyDefinition.cascade()[i] == CascadeType.REMOVE) {
                            oneToManyActions = (oneToManyActions == null ? "" : oneToManyActions + ",") + AuditCrudActionEnum.DELETE;
                        }
                    }

                    if (oneToManyActions != null) {
                        try {
                            Class fieldClass = ReflectionUtils.getFieldGenericsType(field);
                            AuditDataHierarchy fieldHierarchy = getAuditDataHierarchy(fieldClass);
                            if (fieldHierarchy != null) {
                                fieldHierarchy.setFieldName(field.getName());
                                fieldHierarchy.setActions(oneToManyActions);
                                fieldHierarchy.setParentIdField(oneToManyDefinition.mappedBy());
                                fieldHierarchy.setParentIdDbColumn(fieldHierarchy.getDbColumByFieldname(fieldHierarchy.getParentIdField()));
                                hierarcyInfo.getRelatedEntities().add(fieldHierarchy);
                            }
                        } catch (Exception e) {
                            int i = 0;
                        }
                    }
                }
            }
        }
        auditDataHierarchies.put(clazz.getName(), hierarcyInfo);
        return hierarcyInfo;
    }
}