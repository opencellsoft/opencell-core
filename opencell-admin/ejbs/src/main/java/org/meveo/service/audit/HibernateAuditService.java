package org.meveo.service.audit;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.FieldAudit;
import org.meveo.model.audit.hibernate.AuditTarget;
import org.meveo.model.audit.hibernate.HibernateAuditable;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Event service.
 *
 * @author Abdellatif BARI
 * @since 5.3
 */

@Stateless
public class HibernateAuditService {

    @Inject
    private Logger log;

    @Inject
    protected Event<Map<Object, List<FieldAudit>>> fieldsUpdatedEventProducer;


    /**
     * registre the dirtyable fields.
     *
     * @param dirtyableEntities the dirtyable fields and their entities.
     */
    public void registreDirtyableFields(Map<Object, List<FieldAudit>> dirtyableEntities) {
        if (dirtyableEntities != null && !dirtyableEntities.isEmpty()) {
            log.debug("fire statusUpdated event");
            fieldsUpdatedEventProducer.fire(dirtyableEntities);
        }
    }

    /**
     * Get all classes and their fields that are marked by a AuditTarget annotation.
     *
     * @return all classes and their fields that are marked by a AuditTarget annotation.
     */
    public Map<Class, List<Field>> getAuditableEntities() {

        Map<Class, List<Field>> auditableEntities = new HashMap();

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
            return null;
        }

        for (Class clazz : classes) {
            if (Proxy.isProxyClass(clazz) || clazz.getName().contains("$$")) {
                continue;
            }
            if (clazz.isAnnotationPresent(Entity.class) && HibernateAuditable.class.isAssignableFrom(clazz)) {
                List<Field> entityFields = new ArrayList<>();
                List<Field> fields = new ArrayList<>();
                fields = ReflectionUtils.getAllFields(fields, clazz);

                for (Field field : fields) {

                    if (!field.isAnnotationPresent(Transient.class) && field.isAnnotationPresent(AuditTarget.class)) {
                        entityFields.add(field);
                    }
                }

                if (!entityFields.isEmpty()) {
                    auditableEntities.put(clazz, entityFields);
                }
            }
        }

        return auditableEntities;
    }
}
