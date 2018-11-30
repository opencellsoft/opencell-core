package org.meveo.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.FieldAudit;
import org.meveo.model.audit.hibernate.AuditTarget;
import org.meveo.model.audit.hibernate.HibernateAuditable;
import org.meveo.service.audit.HibernateAuditService;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tis interceptor allows to to intercept Hibernate persistent objects and inspect and/or manipulate their properties before it is saved,
 * updated, deleted or loaded.
 *
 * @author Abdellatif BARI
 * @since 5.3
 */
@Singleton
@Startup
public class HibernateAuditInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -1175387060899608632L;

    @Inject
    private Logger log;

    @Inject
    private HibernateAuditService auditService;

    private Map<Class, List<Field>> auditableEntities = new HashMap();
    private Map<Object, List<FieldAudit>> dirtyableEntities = new HashMap();

    @PostConstruct
    public void init() {
        //register itself with the StaticDelegateInterceptor
        HibernateDelegateInterceptor.setInterceptor(this);
        //get only all the entities that implement the HibernateAuditable interface and that contain fields that are marked by the AuditTarget annotation
        auditableEntities = auditService.getAuditableEntities();
    }


    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        //Called when an entity is detected to be dirty, during a flush.
        log.debug("Flush Dirty Entity {}", entity.getClass().getName());

        //check if the dirty entity is not already processed in the current transaction
        if (!dirtyableEntities.containsKey(entity)) {
            if (!(entity instanceof HibernateAuditable)) {
                return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
            }
            /**
             * find out if the current entity is part of the list of auditable entities (auditableEntities)
             * if yes, we store the entity and the modified field
             */

            //loop over entities that implement the HibernateAuditable interface
            for (Map.Entry<Class, List<Field>> entry : auditableEntities.entrySet()) {
                Class clazz = entry.getKey();
                log.debug("Auditable Entity {}", clazz);
                if (clazz.getName().equals(ReflectionUtils.getCleanClassName(entity.getClass().getName()))) {
                    List<Field> fields = entry.getValue();
                    //loop over the fields that are marked by the AuditTarget annotation
                    for (Field field : fields) {
                        //collect all auditable fields that are modified
                        setDirtyableFields(entity, field, currentState, previousState, propertyNames);
                    }
                }
            }
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        //Called before a transaction is committed or rolled back.
        super.beforeTransactionCompletion(tx);
        if (!dirtyableEntities.isEmpty()) {
            //delete the map by call resetDirtyableEntities() before following the treatment, otherwise any database update will replay the treatment.
            auditService.registreDirtyableFields(resetDirtyableEntities());
        }
    }

    /**
     * collect all auditable fields that are modified
     *
     * @param entity        the current auditable entity
     * @param field         the field to check
     * @param currentState  the current state of all fields of current auditable entity
     * @param previousState the previous state of all fields of current auditable entity
     * @param propertyNames the names of all fields in the current auditable entity
     */
    private void setDirtyableFields(Object entity, Field field, Object[] currentState, Object[] previousState, String[] propertyNames) {
        String fieldName = field.getName();
        //loop over the fields of dirty entity
        for (int i = 0; i < propertyNames.length; ++i) {
            if (propertyNames[i].equals(fieldName)) {
                if (isChanged(currentState[i], previousState[i])) {

                    List<FieldAudit> fieldAuditList = null;
                    if (!dirtyableEntities.containsKey(entity)) {
                        fieldAuditList = new ArrayList<>();
                        dirtyableEntities.put(entity, fieldAuditList);
                    } else {
                        fieldAuditList = dirtyableEntities.get(entity);
                    }

                    //store the dirty entity and the modified field
                    FieldAudit fieldAudit = new FieldAudit(entity, currentState[i], previousState[i], field.getAnnotation(AuditTarget.class).type());
                    fieldAuditList.add(fieldAudit);
                    dirtyableEntities.put(entity, fieldAuditList);
                    break;
                }
            }
        }
    }

    /**
     * Reset the dirtyable entities map
     *
     * @return a copy of the old values of the dirtyable entities map.
     */
    private Map<Object, List<FieldAudit>> resetDirtyableEntities() {
        Map<Object, List<FieldAudit>> temp = new HashMap();
        temp.putAll(dirtyableEntities);
        dirtyableEntities.clear();
        return temp;
    }

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
}