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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.audit.AuditTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads Fields audit configuration.
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public class AuditableFieldConfiguration implements Serializable {

    private static final long serialVersionUID = -4077922735278134360L;

    private static Map<String, List<AuditFieldInfo>> auditableEntities = buildAuditableEntities();

    /**
     * Get all classes and their fields that are marked by a AuditTarget annotation.
     *
     * @return all classes and their fields that are marked by a AuditTarget annotation.
     */
    private static Map<String, List<AuditFieldInfo>> buildAuditableEntities() {

        Map<String, List<AuditFieldInfo>> auditableEntities = new HashMap<>();

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");

            for (Class clazz : classes) {
                if (Proxy.isProxyClass(clazz) || clazz.getName().contains("$$")) {
                    continue;
                }
                if (clazz.isAnnotationPresent(Entity.class) && AuditableEntity.class.isAssignableFrom(clazz)) {
                    List<AuditFieldInfo> entityFields = new ArrayList<>();
                    List<Field> fields = ReflectionUtils.getAllFields(new ArrayList<>(), clazz);

                    for (Field field : fields) {
                        AuditTarget atAnnotation = field.getAnnotation(AuditTarget.class);
                        if (!field.isAnnotationPresent(Transient.class) && atAnnotation != null && (atAnnotation.history() || atAnnotation.notif())) {
                            entityFields.add(new AuditFieldInfo(field.getName(), atAnnotation.type(), atAnnotation.history(), atAnnotation.notif(), field.isAnnotationPresent(Embedded.class)));
                        }
                    }

                    if (!entityFields.isEmpty()) {
                        auditableEntities.put(clazz.getName(), entityFields);
                    }
                }
            }
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(AuditableFieldConfiguration.class);
            log.error("Failed to get a list of classes for a model package", e);
        }
        return auditableEntities;
    }

    /**
     * Gets information about entities that track field value changes
     *
     * @return Information about field to audit. A map of field information with key being a full entity class name and a list of field information as a value
     */
    public static Map<String, List<AuditFieldInfo>> getAuditableEntities() {
        return auditableEntities;
    }
}