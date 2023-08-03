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

package org.meveo.admin.action.audit;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AuditableField;
import org.meveo.model.audit.AuditCrudActionEnum;
import org.meveo.model.audit.AuditDataConfiguration;
import org.meveo.model.audit.AuditDataLog;
import org.meveo.model.audit.AuditTarget;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.service.audit.AuditDataConfigurationService;
import org.primefaces.model.DualListModel;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class AuditDataConfigurationBean implements Serializable {

    private static final long serialVersionUID = -2288050777565855091L;

    @Inject
    private Messages messages;

    @Inject
    private AuditDataConfigurationService auditDataConfigurationService;

    private List<Class<?>> entityClasses;
    private Class<?> selectedClass;
    private DualListModel<String> fields = new DualListModel<>();
    List<AuditCrudActionEnum> actions = new ArrayList<AuditCrudActionEnum>();
    private List<AuditDataConfiguration> auditDataConfigurations = new ArrayList<>();
    private AuditDataConfiguration selectedAuditDataConfiguration;

    @PostConstruct
    private void init() {
        entityClasses = new ArrayList<>(ReflectionUtils.getClassesAnnotatedWith(Entity.class));
        for (int i = entityClasses.size() - 1; i >= 0; i--) {
            Class<?> clazz = entityClasses.get(i);
            if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isAssignableFrom(AuditLog.class) || clazz.isAssignableFrom(AuditableField.class) || clazz.isAssignableFrom(AuditDataLog.class)) {
                entityClasses.remove(i);
            }
        }

        entityClasses.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> lhs, Class<?> rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        auditDataConfigurations = auditDataConfigurationService.list();

        // Remove entity classes from a picklist that are already being audited
        for (AuditDataConfiguration auditDataConfiguration : auditDataConfigurations) {
            entityClasses.removeIf(clazz -> clazz.getName().equals(auditDataConfiguration.getEntityClass()));
        }
    }

    @ActionMethod
    public void addAuditDataConfiguration() {
        if (selectedClass != null) {
            AuditDataConfiguration auditDataConfiguration = new AuditDataConfiguration();
            auditDataConfiguration.setEntityClass(selectedClass.getName());

            if (fields.getTarget().isEmpty()) {
                auditDataConfiguration.setFields(null);
            } else {

                String fieldNames = null;
                for (String field : fields.getTarget()) {
                    fieldNames = (fieldNames == null ? "" : ",") + field;
                }

                auditDataConfiguration.setFields(StringUtils.concatenate(",", fields.getTarget()));
            }

            if (actions == null || actions.isEmpty()) {
                auditDataConfiguration.setActions(null);
            } else {
                auditDataConfiguration.setActions(StringUtils.concatenate(",", actions));
            }

            auditDataConfigurationService.create(auditDataConfiguration);

            // Refresh picklists and data panel
            auditDataConfigurations.add(auditDataConfiguration);
            entityClasses.remove(selectedClass);

            selectedClass = null;
            fields = new DualListModel<>();

            messages.info(new BundleKey("messages", "save.successful"));
        }
    }

    @ActionMethod
    public void removeAuditDataConfiguration() {
        if (selectedAuditDataConfiguration != null) {
            auditDataConfigurations.remove(selectedAuditDataConfiguration);
            auditDataConfigurationService.remove(selectedAuditDataConfiguration);
            try {
                entityClasses.add(Class.forName(selectedAuditDataConfiguration.getEntityClass()));
            } catch (ClassNotFoundException e) {
            }

            messages.info(new BundleKey("messages", "update.successful"));
        }
    }

    @ActionMethod
    public void onClassChange() {

        List<Field> classFields = ReflectionUtils.getAllFields(selectedClass);
        List<String> fieldNames = new ArrayList<String>();
        List<String> targetFieldNames = new ArrayList<String>();
        for (Field field : classFields) {
            if (field.isAnnotationPresent(AuditTarget.class)) {
                targetFieldNames.add(field.getName());

            } else if (!Modifier.isStatic(field.getModifiers()) && !field.isAnnotationPresent(Transient.class)) {
                fieldNames.add(field.getName());
            }
        }

        Collections.sort(fieldNames);
        Collections.sort(targetFieldNames);

        fields = new DualListModel<>(fieldNames, targetFieldNames);
    }

    public List<AuditCrudActionEnum> getActionsPicklist() {
        return Arrays.asList(AuditCrudActionEnum.values());
    }

    public List<AuditCrudActionEnum> getActions() {
        return actions;
    }

    public void setActions(List<AuditCrudActionEnum> actions) {
        this.actions = actions;
    }

    public List<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    public void setEntityClasses(List<Class<?>> entityClasses) {
        this.entityClasses = entityClasses;
    }

    public Class<?> getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(Class<?> selectedClass) {
        this.selectedClass = selectedClass;
    }

    public DualListModel<String> getFields() {
        return fields;
    }

    public void setFields(DualListModel<String> fields) {
        this.fields = fields;
    }

    public AuditDataConfiguration getSelectedAuditDataConfiguration() {
        return selectedAuditDataConfiguration;
    }

    public void setSelectedAuditDataConfiguration(AuditDataConfiguration selectedAuditDataConfiguration) {
        this.selectedAuditDataConfiguration = selectedAuditDataConfiguration;
    }

    public List<AuditDataConfiguration> getAuditDataConfigurations() {
        return auditDataConfigurations;
    }

    public void setAuditDataConfigurations(List<AuditDataConfiguration> auditDataConfigurations) {
        this.auditDataConfigurations = auditDataConfigurations;
    }
}