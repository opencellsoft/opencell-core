package org.meveo.service.audit;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.AuditableField;
import org.meveo.model.BaseEntity;
import org.meveo.model.audit.AuditChangeType;
import org.meveo.model.audit.AuditTarget;
import org.meveo.model.audit.AuditableFieldHistory;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Fields audit service.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */

@Stateless
public class AuditableFieldService extends PersistenceService<AuditableField> {

    @Inject
    private Logger log;

    @Inject
    private Event<Set<BaseEntity>> fieldsUpdatedEventProducer;

    @Inject
    private AuditableFieldChanges auditableFieldChanges;

    @Inject
    private AuditOrigin auditOrigin;

    /**
     * check if the field has been modified
     *
     * @param currentState  the current state of the field
     * @param previousState the previous state of the field
     * @return boolean, true if the field is changed.
     */
    private boolean isChanged(Object currentState, Object previousState) {
        return (previousState == null && currentState != null) // nothing to something
                || (previousState != null && currentState == null) // something to nothing
                || (previousState != null && !previousState.equals(currentState)); // something to something else
    }

    /**
     * Check if there is an entity that is not yet audited
     *
     * @return True is there is an entity that is not yet audited, false if not.
     */
    private boolean hasUnauditedEntities() {
        if (!auditableFieldChanges.isEmpty()) {
            for (BaseEntity baseEntity : auditableFieldChanges.getDirtyableEntities()) {
                AuditableEntity entity = (AuditableEntity) baseEntity;
                if (!entity.isHistorized() || !entity.isNotified()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Registre dirtyable field
     *
     * @param baseEntity    the current auditable entity
     * @param field         the field to check
     * @param currentState  the current state of all fields of current auditable entity
     * @param previousState the previous state of all fields of current auditable entity
     * @param propertyNames the names of all fields in the current auditable entity
     */
    private void setDirtyableField(BaseEntity baseEntity, Field field, Object[] currentState, Object[] previousState, String[] propertyNames) {

        AuditableEntity entity = (AuditableEntity) baseEntity;
        String fieldName = field.getName();

        //loop over the fields of dirty entity
        for (int i = 0; i < propertyNames.length; ++i) {
            if (propertyNames[i].equals(fieldName)) {
                if (isChanged(currentState[i], previousState[i])) {
                    AuditableFieldHistory auditableFieldHistory = new AuditableFieldHistory(field.getName(), previousState[i], currentState[i], field.getAnnotation(AuditTarget.class).type(),
                            field.getAnnotation(AuditTarget.class).history(), field.getAnnotation(AuditTarget.class).notif());
                    Set<AuditableFieldHistory> auditableFields = entity.getAuditableFields();
                    if (auditableFields == null || auditableFields.isEmpty() || !auditableFields.contains(auditableFieldHistory)) {
                        if (auditableFields == null) {
                            auditableFields = new HashSet<>();
                            entity.setAuditableFields(auditableFields);
                        }
                        auditableFields.add(auditableFieldHistory);
                        entity.setHistorized(false);
                        entity.setNotified(false);
                        auditableFieldChanges.addChange(entity);
                    }
                }
                break;
            }
        }
    }

    /**
     * Create fields history
     *
     * @param dirtyableEntities the dirtyable fields and their entities.
     * @throws BusinessException the business exception
     */
    private void createFieldsHistory(Set<BaseEntity> dirtyableEntities) throws BusinessException {

        if (dirtyableEntities != null && !dirtyableEntities.isEmpty()) {
            for (BaseEntity baseEntity : dirtyableEntities) {
                AuditableEntity entity = (AuditableEntity) baseEntity;
                Set<AuditableFieldHistory> auditableFields = entity.getAuditableFields();
                if (!entity.isHistorized() && auditableFields != null && !auditableFields.isEmpty()) {
                    for (AuditableFieldHistory field : auditableFields) {
                        if (field.isHistorable() && !field.isHistorized()) {
                            try {
                                String previousState = String.valueOf(field.getPreviousState());
                                String currentState = String.valueOf(field.getCurrentState());
                                if (field.getAuditType() == AuditChangeType.RENEWAL) {
                                    previousState = "";
                                    currentState = AuditChangeType.RENEWAL.toString();
                                }
                                createFieldHistory(entity, field.getFieldName(), field.getAuditType(), previousState, currentState);
                                field.setHistorized(true);
                            } catch (BusinessException e) {
                                field.setHistorized(false);
                                log.error("Failed to create field history");
                                throw e;
                            }
                        }
                    }
                    entity.setHistorized(true);
                }
            }
        }
    }

    /**
     * fireDirtyableFieldsEvents
     *
     * @param dirtyableEntities the dirtyable fields and their entities.
     * @throws BusinessException the business exception
     */
    private void fireDirtyableFieldsEvents(Set<BaseEntity> dirtyableEntities) throws BusinessException {
        if (dirtyableEntities != null && !dirtyableEntities.isEmpty()) {
            Set<BaseEntity> entities = new HashSet<>();
            for (BaseEntity baseEntity : dirtyableEntities) {
                AuditableEntity entity = (AuditableEntity) baseEntity;
                Set<AuditableFieldHistory> auditableFields = entity.getAuditableFields();
                if (!entity.isNotified() && auditableFields != null && !auditableFields.isEmpty()) {
                    for (AuditableFieldHistory field : auditableFields) {
                        if (field.isNotfiable() && !field.isNotified()) {
                            entities.add(entity);
                        }
                    }
                }
                if (!entities.contains(entity)) {
                    entity.setNotified(true);
                }
            }
            if (!entities.isEmpty()) {
                log.debug("fire the events of field changes");
                fieldsUpdatedEventProducer.fire(entities);
            }
        }

    }

    /**
     * Registre the dirtyable fields.
     *
     * @param dirtyableEntities the dirtyable fields and their entities.
     * @throws BusinessException the business exception
     */
    private void registreDirtyableFields(Set<BaseEntity> dirtyableEntities) throws BusinessException {
        if (dirtyableEntities != null && !dirtyableEntities.isEmpty()) {
            // Add fields change history
            createFieldsHistory(dirtyableEntities);

            // Fire fields updated events.
            fireDirtyableFieldsEvents(dirtyableEntities);
        }
    }

    /**
     * Sets the dirtyable fields
     *
     * @param entity        the dirtyable entity
     * @param fields        the fields of dirtyable entity
     * @param currentState  the current state of entity fields
     * @param previousState the previous state of entity fields
     * @param propertyNames the names of the entity fields
     */
    public void setDirtyableFields(BaseEntity entity, List<Field> fields, Object[] currentState, Object[] previousState, String[] propertyNames) {
        for (Field field : fields) {
            //collect all auditable fields that are modified
            setDirtyableField((BaseEntity) entity, field, currentState, previousState, propertyNames);
        }
    }

    /**
     * Registre the dirtyable fields.
     *
     * @throws BusinessException the business exception
     */
    public void registreDirtyableFields() throws BusinessException {
        if (!auditableFieldChanges.isHasTransactionInProgress() && hasUnauditedEntities()) {
            try {
                auditableFieldChanges.setHasTransactionInProgress(true);
                Set<BaseEntity> entities = new HashSet<>();
                entities.addAll(auditableFieldChanges.getDirtyableEntities());
                registreDirtyableFields(entities);
            } catch (BusinessException e) {
                log.error("exception in AuditableFieldService registreDirtyableFields()", e);
                throw e;
            } finally {
                auditableFieldChanges.setHasTransactionInProgress(false);
            }
        }
    }

    /**
     * Reset dirtyable entities map
     */
    public void resetDirtyableEntities() {
        if (!auditableFieldChanges.isHasTransactionInProgress() && !hasUnauditedEntities()) {
            auditableFieldChanges.clear();
        }
    }

    public List<AuditableField> list(BaseEntity entity) {
        Map<String, Object> filters = new HashMap();
        filters.put("entityClass", ReflectionUtils.getCleanClassName(entity.getClass().getName()));
        filters.put("entityId", entity.getId());
        PaginationConfiguration config = new PaginationConfiguration(filters);
        return list(config);
    }

    public void createFieldHistory(BaseEntity entity, String fieldName, AuditChangeType changeType, String previousState, String currentState) throws BusinessException {

        AuditableField auditableField = new AuditableField(currentUser);
        auditableField.setEntityClass(ReflectionUtils.getCleanClassName(entity.getClass().getName()));
        auditableField.setEntityId(entity.getId());
        auditableField.setName(fieldName);
        auditableField.setChangeType(changeType);
        auditableField.setChangeOrigin(auditOrigin.getAuditOrigin());
        auditableField.setOriginName(auditOrigin.getAuditOriginName());
        auditableField.setPreviousState(previousState);
        auditableField.setCurrentState(currentState);
        create(auditableField);
        getEntityManager().flush();
    }

}