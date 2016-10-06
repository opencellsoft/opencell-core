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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.notification.WebHook;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.TimerEntityService;
import org.meveo.service.notification.EmailNotificationService;
import org.meveo.service.notification.JobTriggerService;
import org.meveo.service.notification.NotificationService;
import org.meveo.service.notification.WebHookService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.module.ModuleScriptService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.PieChart;
import org.meveocrm.services.dwh.ChartService;
import org.meveocrm.services.dwh.MeasurableQuantityService;

@Stateless
public class MeveoModuleService extends BusinessService<MeveoModule> {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private EntityCustomActionService entityActionScriptService;

    @Inject
    private ModuleScriptService moduleScriptService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private FilterService filterService;

    @Inject
    private TimerEntityService timerEntityService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EmailNotificationService emailNotificationService;

    @Inject
    private JobTriggerService jobTriggerService;

    @Inject
    private WebHookService webhookNotificationService;

    @Inject
    private NotificationService scriptNotificationService;

    @Inject
    private MeasurableQuantityService measurableQuantityService;

    @SuppressWarnings("rawtypes")
    @Inject
    private ChartService chartService;

    @SuppressWarnings("rawtypes")
    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private MeveoInstanceService meveoInstanceService;
    
    /**
     * import module from remote meveo instance
     * 
     * @param meveoInstance
     * @return
     * @throws MeveoApiException
     * @throws RemoteAuthenticationException
     */
    public List<ModuleDto> downloadModulesFromMeveoInstance(MeveoInstance meveoInstance) throws BusinessException, RemoteAuthenticationException {
        List<ModuleDto> result = null;
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
                Collections.sort(result, new Comparator<ModuleDto>() {
                    @Override
                    public int compare(ModuleDto dto1, ModuleDto dto2) {
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
     * Publish meveo module with DTO items to remote meveo instance
     * 
     * @param module
     * @param meveoInstance
     * @throws MeveoApiException
     * @throws RemoteAuthenticationException
     */
    public void publishModule2MeveoInstance(MeveoModule module, MeveoInstance meveoInstance, User currentUser) throws BusinessException, RemoteAuthenticationException {
        log.debug("export module {} to {}", module, meveoInstance);
        final String url = "api/rest/module/createOrUpdate";

        try {

            module = refreshOrRetrieve(module);
            ModuleDto moduleDto = moduleToDto(module, currentUser.getProvider());
            log.debug("export module dto {}", moduleDto);
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

    /**
     * Convert MeveoModule or its subclass object to DTO representation
     * 
     * @param module Module object
     * @param provider Provider
     * @return MeveoModuleDto object
     */
    public ModuleDto moduleToDto(MeveoModule module, Provider provider) throws BusinessException {

        if (module.isDownloaded() && !module.isInstalled()) {
            try {
                return MeveoModuleService.moduleSourceToDto(module);
            } catch (Exception e) {
                log.error("Failed to load module source {}", module.getCode(), e);
                throw new BusinessException("Failed to load module source", e);
            }
        }

        Class<? extends ModuleDto> dtoClass = ModuleDto.class;
        if (module instanceof BusinessServiceModel) {
            dtoClass = BusinessServiceModelDto.class;
        } else if (module instanceof BusinessOfferModel) {
            dtoClass = BusinessOfferModelDto.class;
        } else if (module instanceof BusinessAccountModel) {
            dtoClass = BusinessAccountModelDto.class;
        }

        ModuleDto moduleDto = null;
        try {
            moduleDto = dtoClass.getConstructor(MeveoModule.class).newInstance(module);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            log.error("Failed to instantiate Module Dto. No reason for it to happen. ", e);
            throw new RuntimeException("Failed to instantiate Module Dto. No reason for it to happen. ", e);
        }

        if (StringUtils.isNotBlank(module.getLogoPicture())) {
            try {
                moduleDto.setLogoPictureFile(ModuleUtil.readModulePicture(module.getProvider().getCode(), module.getLogoPicture()));
            } catch (Exception e) {
                log.error("Failed to read module files {}, info {}", module.getLogoPicture(), e.getMessage(), e);
            }
        }

        List<MeveoModuleItem> moduleItems = module.getModuleItems();
        if (moduleItems != null) {
            for (MeveoModuleItem item : moduleItems) {
                loadModuleItem(item, provider);

                if (item.getItemEntity() == null) {
                    continue;
                }
                if (item.getItemEntity() instanceof CustomEntityTemplate) {
                    CustomEntityTemplate customEntityTemplate = (CustomEntityTemplate) item.getItemEntity();
                    Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo(), module.getProvider());
                    Map<String, EntityCustomAction> customActions = entityActionScriptService.findByAppliesTo(customEntityTemplate.getAppliesTo(), module.getProvider());

                    CustomEntityTemplateDto dto = CustomEntityTemplateDto.toDTO(customEntityTemplate, customFieldTemplates != null ? customFieldTemplates.values() : null,
                        customActions != null ? customActions.values() : null);
                    moduleDto.addModuleItem(dto);

                } else if (item.getItemEntity() instanceof CustomFieldTemplate) {
                    moduleDto.addModuleItem(new CustomFieldTemplateDto((CustomFieldTemplate) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof Filter) {
                    moduleDto.addModuleItem(FilterDto.toDto((Filter) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof JobInstance) {
                    exportJobInstance((JobInstance) item.getItemEntity(), moduleDto);

                } else if (item.getItemEntity() instanceof Notification) {

                    Notification notification = (Notification) item.getItemEntity();
                    if (notification.getScriptInstance() != null) {
                        moduleDto.addModuleItem(new ScriptInstanceDto(notification.getScriptInstance()));
                    }

                    if (notification.getCounterTemplate() != null) {
                        moduleDto.addModuleItem(new CounterTemplateDto(notification.getCounterTemplate()));
                    }

                    if (notification instanceof EmailNotification) {
                        moduleDto.addModuleItem(new EmailNotificationDto((EmailNotification) notification));

                    } else if (notification instanceof JobTrigger) {
                        exportJobInstance(((JobTrigger) notification).getJobInstance(), moduleDto);
                        moduleDto.addModuleItem(new JobTriggerDto((JobTrigger) notification));

                    } else if (notification instanceof ScriptNotification) {
                        moduleDto.addModuleItem(new NotificationDto(notification));

                    } else if (notification instanceof WebHook) {
                        moduleDto.addModuleItem(new WebhookNotificationDto((WebHook) notification));
                    }

                } else if (item.getItemEntity() instanceof ScriptInstance) {
                    moduleDto.addModuleItem(new ScriptInstanceDto((ScriptInstance) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof PieChart) {
                    moduleDto.addModuleItem(new PieChartDto((PieChart) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof LineChart) {
                    moduleDto.addModuleItem(new LineChartDto((LineChart) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof BarChart) {
                    moduleDto.addModuleItem(new BarChartDto((BarChart) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof MeasurableQuantity) {
                    moduleDto.addModuleItem(new MeasurableQuantityDto((MeasurableQuantity) item.getItemEntity()));

                } else if (item.getItemEntity() instanceof MeveoModule) {
                    moduleDto.addModuleItem(moduleToDto((MeveoModule) item.getItemEntity(), provider));

                }
            }
        }

        // Finish converting subclasses of MeveoModule class
        if (module instanceof BusinessServiceModel) {
            businessServiceModelToDto((BusinessServiceModel) module, (BusinessServiceModelDto) moduleDto, provider);

        } else if (module instanceof BusinessOfferModel) {
            businessOfferModelToDto((BusinessOfferModel) module, (BusinessOfferModelDto) moduleDto, provider);

        } else if (module instanceof BusinessAccountModel) {
            businessAccountModelToDto((BusinessAccountModel) module, (BusinessAccountModelDto) moduleDto, provider);
        }

        return moduleDto;
    }

    /**
     * export jobInstance to remote meveo instance
     * 
     * @param meveoInstance
     * @param jobInstance
     * @return
     * @throws MeveoApiException
     */
    private void exportJobInstance(JobInstance jobInstance, ModuleDto moduleDto) {
        JobInstance nextJobInstance = jobInstance.getFollowingJob();
        if (nextJobInstance != null) {
            exportJobInstance(nextJobInstance, moduleDto);
        }

        if (jobInstance.getTimerEntity() != null) {
            TimerEntity timerEntity = jobInstance.getTimerEntity();
            if (timerEntity != null) {
                TimerEntityDto timerDto = new TimerEntityDto(timerEntity);
                moduleDto.addModuleItem(timerDto);
            }
        }
        JobInstanceDto dto = new JobInstanceDto(jobInstance, entityToDtoConverter.getCustomFieldsDTO(jobInstance));
        moduleDto.addModuleItem(dto);
    }

    /**
     * Convert BusinessOfferModel object to DTO representation
     * 
     * @param bom BusinessOfferModel object to convert
     * @param dto BusinessOfferModel object DTO representation (as result of base MeveoModule object conversion)
     * @param provider Provider
     * @return BusinessOfferModel object DTO representation
     */
    private void businessOfferModelToDto(BusinessOfferModel bom, BusinessOfferModelDto dto, Provider provider) {

        if (bom.getOfferTemplate() != null) {
            dto.setOfferTemplate(new OfferTemplateDto(bom.getOfferTemplate(), entityToDtoConverter.getCustomFieldsDTO(bom.getOfferTemplate())));
        }

    }

    /**
     * Finish converting BusinessServiceModel object to DTO representation
     * 
     * @param bsm BusinessServiceModel object to convert
     * @param dto BusinessServiceModel object DTO representation (as result of base MeveoModule object conversion)
     * @param provider Provider
     */
    public void businessServiceModelToDto(BusinessServiceModel bsm, BusinessServiceModelDto dto, Provider provider) {

        if (bsm.getServiceTemplate() != null) {
            dto.setServiceTemplate(new ServiceTemplateDto(bsm.getServiceTemplate(), entityToDtoConverter.getCustomFieldsDTO(bsm.getServiceTemplate())));
        }
        dto.setDuplicateService(bsm.isDuplicateService());
        dto.setDuplicatePricePlan(bsm.isDuplicatePricePlan());

    }

    /**
     * Convert BusinessAccountModel object to DTO representation
     * 
     * @param bom BusinessAccountModel object to convert
     * @param dto BusinessAccountModel object DTO representation (as result of base MeveoModule object conversion)
     * @param provider Provider
     * @return BusinessAccountModel object DTO representation
     */
    private void businessAccountModelToDto(BusinessAccountModel bom, BusinessAccountModelDto dto, Provider provider) {

        dto.setHierarchyType(bom.getHierarchyType());
    }

 
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

    public static ModuleDto moduleSourceToDto(MeveoModule module) throws JAXBException {
        Class<? extends ModuleDto> dtoClass = ModuleDto.class;
        if (module instanceof BusinessServiceModel) {
            dtoClass = BusinessServiceModelDto.class;
        } else if (module instanceof BusinessOfferModel) {
            dtoClass = BusinessOfferModelDto.class;
        } else if (module instanceof BusinessAccountModel) {
            dtoClass = BusinessAccountModelDto.class;
        }

        ModuleDto moduleDto = (ModuleDto) JAXBContext.newInstance(dtoClass).createUnmarshaller().unmarshal(new StringReader(module.getModuleSource()));

        return moduleDto;
    }

    public MeveoModule uninstall(MeveoModule module, User currentUser) throws BusinessException {
        return uninstall(module, currentUser, false);
    }

    @SuppressWarnings("unchecked")
    private MeveoModule uninstall(MeveoModule module, User currentUser, boolean childModule) throws BusinessException {

        if (!module.isInstalled()) {
            throw new BusinessException("Module is not installed");
        }

        if (module.getScript() != null) {
            moduleScriptService.preUninstallModule(module.getScript().getCode(), module, currentUser);
        }

        if (module instanceof BusinessServiceModel) {
            serviceTemplateService.disable(((BusinessServiceModel) module).getServiceTemplate(), currentUser);
        } else if (module instanceof BusinessOfferModel) {
            offerTemplateService.disable(((BusinessOfferModel) module).getOfferTemplate(), currentUser);
        }

        for (MeveoModuleItem item : module.getModuleItems()) {
            loadModuleItem(item, currentUser.getProvider());
            Object itemEntity = item.getItemEntity();
            if (itemEntity == null) {
                continue;
            }

            if (itemEntity instanceof CustomEntityTemplate) {
                // try {
                // customEntityTemplateService.remove((CustomEntityTemplate) itemEntity);
                // } catch (Exception e) {
                customEntityTemplateService.disable((CustomEntityTemplate) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof CustomFieldTemplate) {
                // try {
                // customFieldTemplateService.remove((CustomFieldTemplate) itemEntity);
                // } catch (Exception e) {
                customFieldTemplateService.disable((CustomFieldTemplate) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof Filter) {
                // try {
                // filterService.remove((Filter) itemEntity);
                // } catch (Exception e) {
                filterService.disable((Filter) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof TimerEntity) {
                // try {
                // timerEntityService.remove((TimerEntity) itemEntity);
                // } catch (Exception e) {
                timerEntityService.disable((TimerEntity) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof JobInstance) {
                // try {
                // jobInstanceService.remove((JobInstance) itemEntity);
                // } catch (Exception e) {
                jobInstanceService.disable((JobInstance) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof ScriptInstance) {
                // try {
                // scriptInstanceService.remove((ScriptInstance) itemEntity);
                // } catch (Exception e) {
                scriptInstanceService.disable((ScriptInstance) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof EmailNotification) {
                // try {
                // emailNotificationService.remove((EmailNotification) itemEntity);
                // } catch (Exception e) {
                emailNotificationService.disable((EmailNotification) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof JobTrigger) {
                // try {
                // jobTriggerService.remove((JobTrigger) itemEntity);
                // } catch (Exception e) {
                jobTriggerService.disable((JobTrigger) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof WebHook) {
                // try {
                // webhookNotificationService.remove((WebHook) itemEntity);
                // } catch (Exception e) {
                webhookNotificationService.disable((WebHook) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof ScriptNotification) {
                // try {
                // scriptNotificationService.remove((ScriptNotification) itemEntity);
                // } catch (Exception e) {
                scriptNotificationService.disable((ScriptNotification) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof MeveoModule) {
                uninstall((MeveoModule) itemEntity, currentUser, true);

            } else if (itemEntity instanceof MeasurableQuantity) {
                // try {
                // measurableQuantityService.remove((MeasurableQuantity) itemEntity);
                // } catch (Exception e) {
                measurableQuantityService.disable((MeasurableQuantity) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof Chart) {
                // try {
                // chartService.remove((Chart) itemEntity);
                // } catch (Exception e) {
                chartService.disable((Chart) itemEntity, currentUser);
                // }

            } else if (itemEntity instanceof CounterTemplate) {
                // try {
                // counterTemplateService.remove((CounterTemplate) itemEntity);
                // } catch (Exception e) {
                counterTemplateService.disable((CounterTemplate) itemEntity, currentUser);
                // }
            }
        }

        if (module.getScript() != null) {
            moduleScriptService.postUninstallModule(module.getScript().getCode(), module, currentUser);
        }

        // Remove if it is a child module
        if (childModule) {
            remove(module, currentUser);
            return null;

            // Otherwise mark it uninstalled and clear module items
        } else {
            module.setInstalled(false);
            module.getModuleItems().clear();
            return update(module, currentUser);
        }
    }

    @SuppressWarnings("unchecked")
    public MeveoModule disable(MeveoModule module, User currentUser) throws BusinessException {

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
            Object itemEntity = item.getItemEntity();
            if (itemEntity == null) {
                continue;
            }

            if (itemEntity instanceof CustomEntityTemplate) {
                customEntityTemplateService.disable((CustomEntityTemplate) itemEntity, currentUser);

            } else if (itemEntity instanceof CustomFieldTemplate) {
                customFieldTemplateService.disable((CustomFieldTemplate) itemEntity, currentUser);

            } else if (itemEntity instanceof Filter) {
                filterService.disable((Filter) itemEntity, currentUser);

            } else if (itemEntity instanceof TimerEntity) {
                timerEntityService.disable((TimerEntity) itemEntity, currentUser);

            } else if (itemEntity instanceof JobInstance) {
                jobInstanceService.disable((JobInstance) itemEntity, currentUser);

            } else if (itemEntity instanceof ScriptInstance) {
                scriptInstanceService.disable((ScriptInstance) itemEntity, currentUser);

            } else if (itemEntity instanceof EmailNotification) {
                emailNotificationService.disable((EmailNotification) itemEntity, currentUser);

            } else if (itemEntity instanceof JobTrigger) {
                jobTriggerService.disable((JobTrigger) itemEntity, currentUser);

            } else if (itemEntity instanceof WebHook) {
                webhookNotificationService.disable((WebHook) itemEntity, currentUser);

            } else if (itemEntity instanceof ScriptNotification) {
                scriptNotificationService.disable((ScriptNotification) itemEntity, currentUser);

            } else if (itemEntity instanceof MeveoModule) {
                disable((MeveoModule) itemEntity, currentUser);

            } else if (itemEntity instanceof MeasurableQuantity) {
                measurableQuantityService.disable((MeasurableQuantity) itemEntity, currentUser);

            } else if (itemEntity instanceof Chart) {
                chartService.disable((Chart) itemEntity, currentUser);

            } else if (itemEntity instanceof CounterTemplate) {
                counterTemplateService.disable((CounterTemplate) itemEntity, currentUser);
            }
        }

        if (module.getScript() != null) {
            moduleScriptService.postDisableModule(module.getScript().getCode(), module, currentUser);
        }

        return super.disable(module, currentUser);
    }

    @SuppressWarnings("unchecked")
    public MeveoModule enable(MeveoModule module, User currentUser) throws BusinessException {

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
            Object itemEntity = item.getItemEntity();
            if (itemEntity == null) {
                continue;
            }

            if (itemEntity instanceof CustomEntityTemplate) {
                customEntityTemplateService.enable((CustomEntityTemplate) itemEntity, currentUser);

            } else if (itemEntity instanceof CustomFieldTemplate) {
                customFieldTemplateService.enable((CustomFieldTemplate) itemEntity, currentUser);

            } else if (itemEntity instanceof Filter) {
                filterService.enable((Filter) itemEntity, currentUser);

            } else if (itemEntity instanceof TimerEntity) {
                timerEntityService.enable((TimerEntity) itemEntity, currentUser);

            } else if (itemEntity instanceof JobInstance) {
                jobInstanceService.enable((JobInstance) itemEntity, currentUser);

            } else if (itemEntity instanceof ScriptInstance) {
                scriptInstanceService.enable((ScriptInstance) itemEntity, currentUser);

            } else if (itemEntity instanceof EmailNotification) {
                emailNotificationService.enable((EmailNotification) itemEntity, currentUser);

            } else if (itemEntity instanceof JobTrigger) {
                jobTriggerService.enable((JobTrigger) itemEntity, currentUser);

            } else if (itemEntity instanceof WebHook) {
                webhookNotificationService.enable((WebHook) itemEntity, currentUser);

            } else if (itemEntity instanceof ScriptNotification) {
                scriptNotificationService.enable((ScriptNotification) itemEntity, currentUser);

            } else if (itemEntity instanceof MeveoModule) {
                enable((MeveoModule) itemEntity, currentUser);

            } else if (itemEntity instanceof MeasurableQuantity) {
                measurableQuantityService.enable((MeasurableQuantity) itemEntity, currentUser);

            } else if (itemEntity instanceof Chart) {
                chartService.enable((Chart) itemEntity, currentUser);

            } else if (itemEntity instanceof CounterTemplate) {
                counterTemplateService.enable((CounterTemplate) itemEntity, currentUser);
            }
        }

        if (module.getScript() != null) {
            moduleScriptService.postEnableModule(module.getScript().getCode(), module, currentUser);
        }

        return super.enable(module, currentUser);
    }

    @Override
    public void remove(MeveoModule module, User currentUser) throws BusinessException {

        // If module was downloaded, remove all submodules as well
        if (module.isDownloaded() && module.getModuleItems() != null) {

            for (MeveoModuleItem item : module.getModuleItems()) {
                try {
                    if (MeveoModule.class.isAssignableFrom(Class.forName(item.getItemClass()))) {
                        loadModuleItem(item, module.getProvider());
                        MeveoModule itemModule = (MeveoModule) item.getItemEntity();
                        remove(itemModule, currentUser);
                    }
                } catch (Exception e) {
                    log.error("Failed to delete a submodule", e);
                }
            }
        }

        super.remove(module, currentUser);
    }

    @SuppressWarnings("unchecked")
    public String getRelatedModulesAsString(String itemCode, String itemClazz, String appliesTo, Provider provider) {
        QueryBuilder qb = new QueryBuilder(MeveoModule.class, "m", Arrays.asList("moduleItems as i"), provider);
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