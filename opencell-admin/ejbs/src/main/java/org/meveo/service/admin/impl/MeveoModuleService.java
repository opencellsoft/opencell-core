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

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ApiService;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.script.module.ModuleScriptInterface;
import org.meveo.service.script.module.ModuleScriptService;

@Stateless
public class MeveoModuleService extends GenericModuleService<MeveoModule> {

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private ModuleScriptService moduleScriptService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private MeveoInstanceService meveoInstanceService;

    /**
     * import module from remote meveo instance.
     * 
     * @param meveoInstance meveo instance
     * @return list of meveo module
     * @throws BusinessException business exception.
     * @throws RemoteAuthenticationException remote authentication exception.
     */
    public List<MeveoModuleDto> downloadModulesFromMeveoInstance(MeveoInstance meveoInstance) throws BusinessException, RemoteAuthenticationException {
        List<MeveoModuleDto> result = null;
        try {
            String url = "api/rest/module/list";
            String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
            String username = meveoInstance.getAuthUsername() != null ? meveoInstance.getAuthUsername() : "";
            String password = meveoInstance.getAuthPassword() != null ? meveoInstance.getAuthPassword() : "";
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(baseurl + url);
            BasicAuthentication basicAuthentication = new BasicAuthentication(username, password);
            target.register(basicAuthentication);

            Response response = target.request().get();
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
                    throw new RemoteAuthenticationException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                } else {
                    throw new BusinessException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                }
            }

            MeveoModuleDtosResponse resultDto = response.readEntity(MeveoModuleDtosResponse.class);
            log.debug("response {}", resultDto);
            if (resultDto == null || ActionStatusEnum.SUCCESS != resultDto.getActionStatus().getStatus()) {
                throw new BusinessException("Code " + resultDto.getActionStatus().getErrorCode() + ", info " + resultDto.getActionStatus().getMessage());
            }
            result = resultDto.getModules();
            if (result != null) {
                Collections.sort(result, new Comparator<MeveoModuleDto>() {
                    @Override
                    public int compare(MeveoModuleDto dto1, MeveoModuleDto dto2) {
                        return dto1.getCode().compareTo(dto2.getCode());
                    }
                });
            }
            return result;

        } catch (Exception e) {
            log.error("Failed to communicate {}. Reason {}", meveoInstance.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
            throw new BusinessException("Fail to communicate " + meveoInstance.getCode() + ". Error " + (e == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
    }

    /**
     * Publish meveo module with DTO items to remote meveo instance.
     * 
     * @param module meveo module
     * @param meveoInstance meveo instance.
     * @throws BusinessException business exception.
     * @throws RemoteAuthenticationException remote exception.
     */
    @SuppressWarnings("unchecked")
    public void publishModule2MeveoInstance(MeveoModule module, MeveoInstance meveoInstance) throws BusinessException, RemoteAuthenticationException {
        log.debug("export module {} to {}", module, meveoInstance);
        final String url = "api/rest/module/createOrUpdate";

        try {
            ApiService<MeveoModule, MeveoModuleDto> moduleApi = (ApiService<MeveoModule, MeveoModuleDto>) EjbUtils.getServiceInterface("MeveoModuleApi");
            MeveoModuleDto moduleDto = moduleApi.find(module.getCode());

            log.debug("Export module dto {}", moduleDto);
            Response response = meveoInstanceService.publishDto2MeveoInstance(url, meveoInstance, moduleDto);
            ActionStatus actionStatus = response.readEntity(ActionStatus.class);
            log.debug("response {}", actionStatus);
            if (actionStatus == null || ActionStatusEnum.SUCCESS != actionStatus.getStatus()) {
                throw new BusinessException("Code " + actionStatus.getErrorCode() + ", info " + actionStatus.getMessage());
            }
        } catch (Exception e) {
            log.error("Error when export module {} to {}. Reason {}", module.getCode(), meveoInstance.getCode(),
                (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
            throw new BusinessException("Fail to communicate " + meveoInstance.getCode() + ". Error " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    public List<MeveoModuleItem> findByCodeAndItemType(String code, String className) {
        QueryBuilder qb = new QueryBuilder(MeveoModuleItem.class, "m");
        qb.addCriterion("itemCode", "=", code, true);
        qb.addCriterion("itemClass", "=", className, true);

        try {
            return (List<MeveoModuleItem>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static MeveoModuleDto moduleSourceToDto(MeveoModule module) throws JAXBException {
        Class<? extends MeveoModuleDto> dtoClass = (Class<? extends MeveoModuleDto>) ReflectionUtils.getClassBySimpleNameAndParentClass(module.getClass().getSimpleName() + "Dto",
            MeveoModuleDto.class);

        MeveoModuleDto moduleDto = (MeveoModuleDto) JAXBContext.newInstance(dtoClass).createUnmarshaller().unmarshal(new StringReader(module.getModuleSource()));

        return moduleDto;
    }

    public MeveoModule uninstall(MeveoModule module) throws BusinessException {
        return uninstall(module, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private MeveoModule uninstall(MeveoModule module, boolean childModule) throws BusinessException {

        if (!module.isInstalled()) {
            throw new BusinessException("Module is not installed");
        }

        ModuleScriptInterface moduleScript = null;
        if (module.getScript() != null) {
            moduleScript = moduleScriptService.preUninstallModule(module.getScript().getCode(), module);
        }

        if (module instanceof BusinessServiceModel) {
            serviceTemplateService.disable(((BusinessServiceModel) module).getServiceTemplate());
        } else if (module instanceof BusinessOfferModel) {
            offerTemplateService.disable(((BusinessOfferModel) module).getOfferTemplate());
        }

        for (MeveoModuleItem item : module.getModuleItems()) {

            // check if moduleItem is linked to other module
            if (isChildOfOtherModule(item.getItemClass(), item.getItemCode())) {
                continue;
            }

            loadModuleItem(item);
            BusinessEntity itemEntity = item.getItemEntity();
            if (itemEntity == null) {
                continue;
            }

            try {
                if (itemEntity instanceof MeveoModule) {
                    uninstall((MeveoModule) itemEntity, true);
                } else {

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

                    persistenceServiceForItem.disable(itemEntity);

                }
            } catch (Exception e) {
                log.error("Failed to uninstall/disable module item. Module item {}", item, e);
            }
        }

        if (moduleScript != null) {
            moduleScriptService.postUninstallModule(moduleScript, module);
        }

        // Remove if it is a child module
        if (childModule) {
            remove(module);
            return null;

            // Otherwise mark it uninstalled and clear module items
        } else {
            module.setInstalled(false);
            module.getModuleItems().clear();
            return update(module);
        }
    }

    /**
     * Determine if entity identified by a class and code is part of more than one module
     * 
     * @param moduleItemClass Entity class
     * @param moduleItemCode Entity code
     * @return True if entity is part of more than one module
     */
    public boolean isChildOfOtherModule(String moduleItemClass, String moduleItemCode) {
        QueryBuilder qb = new QueryBuilder(MeveoModuleItem.class, "i", null);
        qb.addCriterion("itemClass", "=", moduleItemClass, false);
        qb.addCriterion("itemCode", "=", moduleItemCode, true);
        return qb.count(getEntityManager()) > 1 ? true : false;
    }

    @SuppressWarnings("unchecked")
    public String getRelatedModulesAsString(String itemCode, String itemClazz, String appliesTo) {
        QueryBuilder qb = new QueryBuilder(MeveoModule.class, "m", Arrays.asList("moduleItems as i"));
        qb.addCriterion("i.itemCode", "=", itemCode, true);
        qb.addCriterion("i.itemClass", "=", itemClazz, false);
        qb.addCriterion("i.appliesTo", "=", appliesTo, false);
        List<MeveoModule> modules = qb.getQuery(getEntityManager()).getResultList();

        if (modules != null) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (MeveoModule module : modules) {
                if (i != 0) {
                    sb.append(";");
                }
                sb.append(module.getCode());
                i++;
            }
            return sb.toString();
        }
        return null;
    }
}