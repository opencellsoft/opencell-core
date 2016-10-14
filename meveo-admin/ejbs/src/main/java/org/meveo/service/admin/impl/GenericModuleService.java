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
package org.meveo.service.admin.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.module.ModuleScriptService;

@Stateless
public class GenericModuleService<T extends MeveoModule> extends BusinessService<T> {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private ModuleScriptService moduleScriptService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private OfferTemplateService offerTemplateService;

    public void loadModuleItem(MeveoModuleItem item, Provider provider) {

        BusinessEntity entity = null;
        if (CustomFieldTemplate.class.getName().equals(item.getItemClass())) {
            entity = customFieldTemplateService.findByCodeAndAppliesTo(item.getItemCode(), item.getAppliesTo(), provider);

        } else {

            String sql = "select mi from " + item.getItemClass() + " mi where mi.code=:code and mi.provider=:provider";
            TypedQuery<BusinessEntity> query = getEntityManager().createQuery(sql, BusinessEntity.class);
            query.setParameter("code", item.getItemCode());
            query.setParameter("provider", provider);
            try {
                entity = query.getSingleResult();

            } catch (NoResultException | NonUniqueResultException e) {
                log.error("Failed to find a module item {}. Reason: {}", item, e.getClass().getSimpleName());
                return;
            } catch (Exception e) {
                log.error("Failed to find a module item {}", item, e);
                return;
            }
        }
        item.setItemEntity(entity);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T disable(T module, User currentUser) throws BusinessException {

        // if module is local module (was not downloaded) just disable as any other entity without iterating module items
        if (!module.isDownloaded()) {
            return super.disable(module, currentUser);
        }

        if (!module.isInstalled()) {
            // throw new BusinessException("Module is not installed");
            return module;
        }

        if (module.getScript() != null) {
            moduleScriptService.preDisableModule(module.getScript().getCode(), module, currentUser);
        }

        if (module instanceof BusinessServiceModel) {
            serviceTemplateService.disable(((BusinessServiceModel) module).getServiceTemplate(), currentUser);
        } else if (module instanceof BusinessOfferModel) {
            offerTemplateService.disable(((BusinessOfferModel) module).getOfferTemplate(), currentUser);
        }

        for (MeveoModuleItem item : module.getModuleItems()) {
            loadModuleItem(item, currentUser.getProvider());
            BusinessEntity itemEntity = item.getItemEntity();
            if (itemEntity == null) {
                continue;
            }

            try {
                // Find API service class first trying with item's classname and then with its super class (a simplified version instead of trying various class
                // superclasses)
                Class clazz = Class.forName(item.getItemClass());
                PersistenceService persistenceServiceForItem = (PersistenceService) EjbUtils.getServiceInterface(clazz.getSimpleName() + "Service");
                if (persistenceServiceForItem == null) {
                    persistenceServiceForItem = (PersistenceService) EjbUtils.getServiceInterface(clazz.getSuperclass().getSimpleName() + "Service");
                }
                if (persistenceServiceForItem == null) {
                    log.error("Failed to find implementation of persistence service for class {}", item.getItemClass());
                    continue;
                }

                persistenceServiceForItem.disable(itemEntity, currentUser);

            } catch (Exception e) {
                log.error("Failed to disable module item. Module item {}", item, e);
            }
        }

        if (module.getScript() != null) {
            moduleScriptService.postDisableModule(module.getScript().getCode(), module, currentUser);
        }

        return super.disable(module, currentUser);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T enable(T module, User currentUser) throws BusinessException {

        // if module is local module (was not downloaded) just disable as any other entity without iterating module items
        if (!module.isDownloaded()) {
            return super.enable(module, currentUser);
        }

        if (!module.isInstalled()) {
            // throw new BusinessException("Module is not installed");
            return module;
        }

        if (module.getScript() != null) {
            moduleScriptService.preEnableModule(module.getScript().getCode(), module, currentUser);
        }

        if (module instanceof BusinessServiceModel) {
            serviceTemplateService.enable(((BusinessServiceModel) module).getServiceTemplate(), currentUser);
        } else if (module instanceof BusinessOfferModel) {
            offerTemplateService.enable(((BusinessOfferModel) module).getOfferTemplate(), currentUser);
        }

        for (MeveoModuleItem item : module.getModuleItems()) {
            loadModuleItem(item, currentUser.getProvider());
            BusinessEntity itemEntity = item.getItemEntity();
            if (itemEntity == null) {
                continue;
            }

            try {
                // Find API service class first trying with item's classname and then with its super class (a simplified version instead of trying various class
                // superclasses)
                Class clazz = Class.forName(item.getItemClass());
                PersistenceService persistenceServiceForItem = (PersistenceService) EjbUtils.getServiceInterface(clazz.getSimpleName() + "Service");
                if (persistenceServiceForItem == null) {
                    persistenceServiceForItem = (PersistenceService) EjbUtils.getServiceInterface(clazz.getSuperclass().getSimpleName() + "Service");
                }
                if (persistenceServiceForItem == null) {
                    log.error("Failed to find implementation of persistence service for class {}", item.getItemClass());
                    continue;
                }

                persistenceServiceForItem.enable(itemEntity, currentUser);

            } catch (Exception e) {
                log.error("Failed to enable module item. Module item {}", item, e);
            }
        }

        if (module.getScript() != null) {
            moduleScriptService.postEnableModule(module.getScript().getCode(), module, currentUser);
        }

        return super.enable(module, currentUser);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void remove(T module, User currentUser) throws BusinessException {

        // If module was downloaded, remove all submodules as well
        if (module.isDownloaded() && module.getModuleItems() != null) {

            for (MeveoModuleItem item : module.getModuleItems()) {
                try {
                    if (MeveoModule.class.isAssignableFrom(Class.forName(item.getItemClass()))) {
                        loadModuleItem(item, module.getProvider());
                        T itemModule = (T) item.getItemEntity();
                        remove(itemModule, currentUser);
                    }
                } catch (Exception e) {
                    log.error("Failed to delete a submodule", e);
                }
            }
        }

        super.remove(module, currentUser);
    }

}