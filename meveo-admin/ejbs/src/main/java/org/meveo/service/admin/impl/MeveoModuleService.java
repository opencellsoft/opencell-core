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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
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
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
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
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.EntityActionScriptService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.PieChart;

@Stateless
public class MeveoModuleService extends BusinessService<MeveoModule> {

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;
    
    @Inject
    private EntityActionScriptService entityActionScriptService;

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
            ModuleDto moduleDto = moduleToDto(module, currentUser.getProvider());
            log.debug("export module dto {}", moduleDto);
            Response response = publishDto2MeveoInstance(url, meveoInstance, moduleDto);
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

    public ModuleDto moduleToDto(MeveoModule module, Provider provider) {
        ModuleDto moduleDto = new ModuleDto(module);
        if (StringUtils.isNotBlank(module.getLogoPicture())) {
            try {
                moduleDto.setLogoPictureFile(ModuleUtil.readModulePicture(module.getProvider().getCode(), module.getLogoPicture()));
            } catch (Exception e) {
                log.error("Failed to read module files {}, info {}", module.getLogoPicture(), e.getMessage(), e);
            }
        }
        List<MeveoModuleItem> moduleItems = module.getModuleItems();
        if (moduleItems == null || moduleItems.size() == 0) {
            return moduleDto;
        }
        for (MeveoModuleItem item : moduleItems) {
            loadModuleItem(item, provider);

            if (item.getItemEntity() == null) {
                continue;
            }
            if (item.getItemEntity() instanceof CustomEntityTemplate) {
                CustomEntityTemplate customEntityTemplate = (CustomEntityTemplate) item.getItemEntity();
                Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo(), module.getProvider());
                Map<String, EntityActionScript> customActions = entityActionScriptService.findByAppliesTo(customEntityTemplate.getAppliesTo(), module.getProvider());

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

                if (notification.getCounterTemplate() !=null) {
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
                
            } else if (item.getItemEntity() instanceof BusinessServiceModel) {
                moduleDto.addModuleItem(new BusinessServiceModelDto((BusinessServiceModel) item.getItemEntity()));
                
            } else if (item.getItemEntity() instanceof BusinessOfferModel) {
                moduleDto.addModuleItem(new BusinessOfferModelDto((BusinessOfferModel) item.getItemEntity()));
                
            }
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
     * export module dto to remote meveo instance
     * 
     * @param url
     * @param meveoInstance
     * @param dto
     * @return
     * @throws MeveoApiException
     */
    private Response publishDto2MeveoInstance(String url, MeveoInstance meveoInstance, BaseDto dto) throws BusinessException {
        String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
        String username = meveoInstance.getAuthUsername() != null ? meveoInstance.getAuthUsername() : "";
        String password = meveoInstance.getAuthPassword() != null ? meveoInstance.getAuthPassword() : "";
        try {
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(baseurl + url);
            BasicAuthentication basicAuthentication = new BasicAuthentication(username, password);
            target.register(basicAuthentication);

            Response response = target.request().post(Entity.entity(dto, MediaType.APPLICATION_XML));
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
                    throw new RemoteAuthenticationException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                } else {
                    throw new BusinessException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                }
            }
            return response;
        } catch (Exception e) {
            log.error("Failed to communicate {}. Reason {}", meveoInstance.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
            throw new BusinessException("Failed to communicate " + meveoInstance.getCode() + ". Error " + e.getMessage());
        }
    }

    public void loadModuleItem(MeveoModuleItem item, Provider provider) {

        BusinessEntity entity = null;
        if (CustomFieldTemplate.class.getName().equals(item.getItemClass())) {
            entity = customFieldTemplateService.findByCode(item.getItemCode(), provider);

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
}