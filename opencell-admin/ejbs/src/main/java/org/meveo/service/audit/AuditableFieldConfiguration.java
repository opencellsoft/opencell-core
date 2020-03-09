/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.audit;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.audit.AuditTarget;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads Fields audit configuration.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class AuditableFieldConfiguration implements Serializable {


    private static final long serialVersionUID = -4077922735278134360L;

    @Inject
    private Logger log;

    private Map<String, List<Field>> auditableEntities = new HashMap();

    @PostConstruct
    public void init() {
        //get only all the entities that extends the AuditableEntity interface and that contain fields that are marked by the AuditTarget annotation
        auditableEntities = buildAuditableEntities();
    }

    /**
     * Get all classes and their fields that are marked by a AuditTarget annotation.
     *
     * @return all classes and their fields that are marked by a AuditTarget annotation.
     */
    private Map<String, List<Field>> buildAuditableEntities() {

        Map<String, List<Field>> auditableEntities = new HashMap();
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
            if (clazz.isAnnotationPresent(Entity.class) && AuditableEntity.class.isAssignableFrom(clazz)) {
                List<Field> entityFields = new ArrayList<>();
                List<Field> fields = new ArrayList<>();
                fields = ReflectionUtils.getAllFields(fields, clazz);

                for (Field field : fields) {

                    if (!field.isAnnotationPresent(Transient.class) && field.isAnnotationPresent(AuditTarget.class) &&
                            (field.getAnnotation(AuditTarget.class).history() || field.getAnnotation(AuditTarget.class).notif())) {
                        entityFields.add(field);
                    }
                }

                if (!entityFields.isEmpty()) {
                    auditableEntities.put(clazz.getName(), entityFields);
                }
            }
        }
        return auditableEntities;
    }

    /**
     * Gets the auditable entities
     *
     * @return the auditable entities
     */
    public Map<String, List<Field>> getAuditableEntities() {
        return auditableEntities;
    }
}