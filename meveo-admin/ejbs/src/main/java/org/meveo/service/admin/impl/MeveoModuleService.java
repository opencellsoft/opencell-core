/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
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
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.MeveoModuleItem;
import org.meveo.model.admin.User;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.notification.WebHook;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.service.script.EntityActionScriptService;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class MeveoModuleService extends BusinessService<MeveoModule> {

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;
    @Inject
    private CustomFieldTemplateService customFieldTemplateService;
    @Inject
    private FilterService filterService;
    @Inject
    private JobInstanceService jobInstanceService;
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;
    @Inject
    private GenericNotificationService genericNotificationService;
    @Inject
    private ScriptInstanceService scriptInstanceService;
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
            result = resultDto.getModuleDtoList();
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
            log.error("Fail to communicate {}. Reason {}", meveoInstance.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
            throw new BusinessException("Fail to communicate " + meveoInstance.getCode() + ". Error " + (e == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
    }

    /**
     * export meveo module with DTO items to remote meveo instance
     * 
     * @param module
     * @param meveoInstance
     * @throws MeveoApiException
     * @throws RemoteAuthenticationException
     */
    public void exportModule2MeveoInstance(MeveoModule module, MeveoInstance meveoInstance, User currentUser) throws BusinessException, RemoteAuthenticationException {
        log.debug("export module {} to {}", module, meveoInstance);
        final String url = "api/rest/module/createOrUpdate";

        try {
            ModuleDto moduleDto = exportModuleDto(module, currentUser);
            log.debug("export module dto {}", moduleDto);
            Response response = exportDto2MeveoInstance(url, meveoInstance, moduleDto);
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

    private ModuleDto exportModuleDto(MeveoModule module, User currentUser) {
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
            switch (item.getItemType()) {
            case CET:
                CustomEntityTemplate customEntityTemplate = customEntityTemplateService.findByCode(item.getItemCode(), this.getCurrentProvider());
                if (customEntityTemplate != null) {
                    Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getCftPrefix(),
                        currentUser.getProvider());
                    Map<String, EntityActionScript> customActions = entityActionScriptService.findByAppliesTo(customEntityTemplate.getCftPrefix(), currentUser.getProvider());

                    CustomEntityTemplateDto dto = CustomEntityTemplateDto.toDTO(customEntityTemplate, customFieldTemplates != null ? customFieldTemplates.values() : null,
                        customActions != null ? customActions.values() : null);
                    moduleDto.getCetDtos().add(dto);
                }
                break;
            case CFT:
                CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCode(item.getItemCode(), this.getCurrentProvider());
                if (customFieldTemplate != null) {
                    CustomFieldTemplateDto dto = new CustomFieldTemplateDto(customFieldTemplate);
                    moduleDto.getCftDtos().add(dto);
                }
                break;
            case FILTER:
                Filter filter = filterService.findByCode(item.getItemCode(), this.getCurrentProvider());
                if (filter != null) {
                    FilterDto dto = FilterDto.parseDto(filter);
                    moduleDto.getFilterDtos().add(dto);
                }
                break;
            case JOBINSTANCE:
                JobInstance jobInstance = jobInstanceService.findByCode(item.getItemCode(), this.getCurrentProvider());
                if (jobInstance != null) {
                    moduleDto = exportJobInstance(jobInstance, moduleDto, false);
                }
                break;
            case NOTIFICATION:
                Notification notification = genericNotificationService.findByCode(item.getItemCode(), getCurrentProvider());
                if (notification == null) {
                    break;
                }
                if (notification.getScriptInstance() != null) {
                    ScriptInstance scriptInstance = notification.getScriptInstance();
                    ScriptInstanceDto dto = new ScriptInstanceDto(scriptInstance);
                    moduleDto.getScriptDtos().add(dto);
                }

                if (notification instanceof EmailNotification) {
                    EmailNotificationDto dto = new EmailNotificationDto((EmailNotification) notification);
                    moduleDto.getEmailNotifDtos().add(dto);
                } else if (notification instanceof JobTrigger) {
                    exportJobInstance(((JobTrigger) notification).getJobInstance(), moduleDto, true);
                    JobTriggerDto dto = new JobTriggerDto((JobTrigger) notification);
                    moduleDto.getJobTriggerDtos().add(dto);
                } else if (notification instanceof ScriptNotification) {
                    NotificationDto dto = new NotificationDto(notification);
                    moduleDto.getNotificationDtos().add(dto);
                } else if (notification instanceof WebHook) {
                    WebhookNotificationDto dto = new WebhookNotificationDto((WebHook) notification);
                    moduleDto.getWebhookNotifDtos().add(dto);
                }
                break;
            case SCRIPT:
                ScriptInstance scriptInstance = scriptInstanceService.findByCode(item.getItemCode(), getCurrentProvider());
                if (scriptInstance != null) {
                    ScriptInstanceDto dto = new ScriptInstanceDto(scriptInstance);
                    moduleDto.getScriptDtos().add(dto);
                }
                break;
            default:
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
    private ModuleDto exportJobInstance(JobInstance jobInstance, ModuleDto moduleDto, boolean nextJob) {
        if (jobInstance == null) {
            return moduleDto;
        }

        if (jobInstance.getTimerEntity() != null) {
            TimerEntity timerEntity = jobInstance.getTimerEntity();
            if (timerEntity != null) {
                TimerEntityDto timerDto = new TimerEntityDto(timerEntity);
                moduleDto.getTimerEntityDtos().add(timerDto);
            }
        }
        Map<String, List<CustomFieldInstance>> cfis = customFieldInstanceService.getCustomFieldInstances(jobInstance);
        JobInstanceDto dto = new JobInstanceDto(jobInstance, cfis);
        if (nextJob) {
            moduleDto.getJobNextDtos().addFirst(dto);
        } else {
            moduleDto.getJobDtos().add(dto);
        }

        JobInstance nextJobInstance = jobInstance.getFollowingJob();

        return exportJobInstance(nextJobInstance, moduleDto, true);
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
    private Response exportDto2MeveoInstance(String url, MeveoInstance meveoInstance, BaseDto dto) throws BusinessException {
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
}