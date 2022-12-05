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

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.IEntity;
import org.meveo.service.base.MultiLanguageFieldService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;

@Named
@ConversationScoped
public class MultiLanguageFieldListBean extends BaseBean<IEntity> {

    private static final long serialVersionUID = 8501551399825487066L;

    @SuppressWarnings("rawtypes")
    private Map<Class, List<String>> multiLanguageFieldMapping;

    @SuppressWarnings("rawtypes")
    private Class entityClass;

    @SuppressWarnings("rawtypes")
    private PersistenceService persistenceService;

    private IEntity selectedEntity;

    @Inject
    private MultiLanguageFieldService multiLanguageFieldService;
    

    @Inject
    private FacesContext facesContext;

    @Override
    public void preRenderView() {

        if (facesContext.isPostback()) {
            return; // Skip postback/ajax requests.
        }

        super.preRenderView();
        multiLanguageFieldMapping = multiLanguageFieldService.getMultiLanguageFieldMapping();
        if (entityClass == null) {
            setEntityClass(multiLanguageFieldMapping.entrySet().iterator().next().getKey());
            changeEntityClass();
        }
    }

    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    @SuppressWarnings("rawtypes")
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void changeEntityClass() {
        setClazz(entityClass);
        persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass);
        dataModel = null;
        selectedEntity = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected IPersistenceService<IEntity> getPersistenceService() {
        return persistenceService;
    }

    @SuppressWarnings("rawtypes")
    public Set<Class> getMultiLanguageClasses() {
        return multiLanguageFieldMapping.keySet();
    }

    public List<String> getMultiLanguageFields() {
        return multiLanguageFieldMapping.get(entityClass);
    }

    public IEntity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(IEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
        this.entity = selectedEntity;
    }

    /**
     * Save changes to language fields
     * 
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("unchecked")
    public void updateEntity() throws BusinessException {
        persistenceService.update(selectedEntity);
    }
}