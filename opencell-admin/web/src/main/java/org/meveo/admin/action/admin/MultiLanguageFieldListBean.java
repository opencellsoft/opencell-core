/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

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

    @Override
    public void preRenderView() {

        if (FacesContext.getCurrentInstance().isPostback()) {
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
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    public void updateEntity() throws BusinessException {
        persistenceService.update(selectedEntity);
    }
}