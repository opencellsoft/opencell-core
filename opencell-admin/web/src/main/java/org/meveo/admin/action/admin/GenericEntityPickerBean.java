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

package org.meveo.admin.action.admin;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.IEntity;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.LazyDataModel;

@ViewScoped
@Named
public class GenericEntityPickerBean extends BaseBean<IEntity> {

    private static final long serialVersionUID = 115130709397837651L;

    private Class<? extends IEntity> selectedEntityClass;

    @Inject
    private BaseEntityService baseEntityService;

    /**
     * Get a list of classes that contain the given annotation
     * 
     * @param annotation Annotation classname
     * @return A list of classes
     */
    @SuppressWarnings("unchecked")
    public List<Class<?>> getEntityClasses(String annotation) {
        try {

            List<Class<?>> classes = new ArrayList<>(ReflectionUtils.getClassesAnnotatedWith((Class<? extends Annotation>) Class.forName(annotation)));

            Collections.sort(classes, new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> clazz1, Class<?> clazz2) {
                    return clazz1.getName().compareTo(clazz2.getName());
                }
            });
            return classes;

        } catch (ClassNotFoundException e) {
            return new ArrayList<Class<?>>();
        }
    }

    public Class<? extends IEntity> getSelectedEntityClass() {
        return selectedEntityClass;
    }

    @SuppressWarnings("unchecked")
    public void setSelectedEntityClass(Class<? extends IEntity> selectedEntityClass) {
        this.selectedEntityClass = selectedEntityClass;
        setClazz((Class<IEntity>) selectedEntityClass);
        baseEntityService.setEntityClass((Class<IEntity>) selectedEntityClass);
    }

    @Override
    protected IPersistenceService<IEntity> getPersistenceService() {
        return baseEntityService;
    }

    @Override
    public LazyDataModel<IEntity> getLazyDataModel() {
        if (selectedEntityClass == null) {
            return null;
        } else {
            return super.getLazyDataModel();
        }
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }
}