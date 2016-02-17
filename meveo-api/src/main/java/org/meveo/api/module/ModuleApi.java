package org.meveo.api.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.util.ModuleUtil;
import org.meveo.api.BaseApi;
import org.meveo.api.CustomEntityApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.FilterApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.dwh.ChartApi;
import org.meveo.api.dwh.MeasurableQuantityApi;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.job.JobInstanceApi;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.notification.EmailNotificationApi;
import org.meveo.api.notification.JobTriggerApi;
import org.meveo.api.notification.NotificationApi;
import org.meveo.api.notification.WebhookNotificationApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.MeveoModuleItem;
import org.meveo.model.admin.ModuleItemTypeEnum;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.notification.WebHook;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.notification.GenericNotificationService;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 **/
@Stateless
public class ModuleApi extends BaseApi {

    @Inject
    private MeveoModuleService meveoModuleService;

    @Inject
    private CustomEntityApi customEntityApi;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private FilterApi filterApi;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Inject
    private JobInstanceApi jobInstanceApi;

    @Inject
    private TimerEntityApi timerEntityApi;

    @Inject
    private GenericNotificationService genericNotificationService;

    @Inject
    private NotificationApi notificationApi;

    @Inject
    private EmailNotificationApi emailNotificationApi;

    @Inject
    private JobTriggerApi jobTriggerApi;

    @Inject
    private WebhookNotificationApi webhookNotificationApi;

    @Inject
    private MeasurableQuantityApi measurableQuantityApi;

    @Inject
    private ChartApi chartApi;

    public void create(ModuleDto moduleDto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(moduleDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(moduleDto.getDescription())) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(moduleDto.getLicense())) {
            missingParameters.add("license");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        Provider provider = currentUser.getProvider();
        if (meveoModuleService.findByCode(moduleDto.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(MeveoModule.class, moduleDto.getCode());
        }
        MeveoModule meveoModule = new MeveoModule();
        meveoModule.setCode(moduleDto.getCode());
        meveoModule = parseModuleFromDto(meveoModule, moduleDto, currentUser);

        meveoModuleService.create(meveoModule, currentUser, provider);
    }

    public void update(ModuleDto moduleDto, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(moduleDto.getCode())) {
            missingParameters.add("module code is null");
        }
        if (StringUtils.isBlank(moduleDto.getDescription())) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(moduleDto.getLicense())) {
            missingParameters.add("module license is null");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        Provider provider = currentUser.getProvider();
        MeveoModule meveoModule = meveoModuleService.findByCode(moduleDto.getCode(), provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, moduleDto.getCode());
        }
        if (meveoModule.getModuleItems() != null) {
            Iterator<MeveoModuleItem> itr = meveoModule.getModuleItems().iterator();
            while (itr.hasNext()) {
                MeveoModuleItem i = itr.next();
                i.setMeveoModule(null);
                itr.remove();
            }
        }
        meveoModule = parseModuleFromDto(meveoModule, moduleDto, currentUser);
        meveoModuleService.update(meveoModule, currentUser);
    }

    public void delete(String code, User currentUser) throws EntityDoesNotExistsException {
        Provider provider = currentUser.getProvider();
        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, code);
        }
        String logoPicture = meveoModule.getLogoPicture();
        meveoModuleService.remove(meveoModule);
        removeModulePicture(provider.getCode(), logoPicture);

    }

    public List<ModuleDto> list(User currentUser) throws MeveoApiException {
        Provider provider = currentUser.getProvider();
        List<MeveoModule> meveoModules = meveoModuleService.list(provider);
        List<ModuleDto> result = new ArrayList<ModuleDto>();
        ModuleDto moduleDto = null;
        for (MeveoModule meveoModule : meveoModules) {
            moduleDto = new ModuleDto(meveoModule);
            moduleDto = parseModule2Dto(meveoModule, moduleDto, currentUser);
            result.add(moduleDto);
        }
        return result;
    }

    public ModuleDto get(String code, User currentUser) throws MeveoApiException {
        Provider provider = currentUser.getProvider();
        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, code);
        }
        ModuleDto moduleDto = new ModuleDto(meveoModule);
        moduleDto = parseModule2Dto(meveoModule, moduleDto, currentUser);
        return moduleDto;
    }

    public void createOrUpdate(ModuleDto postData, User currentUser) throws MeveoApiException {
        MeveoModule meveoModule = meveoModuleService.findByCode(postData.getCode(), currentUser.getProvider());
        if (meveoModule == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }

    private ModuleDto parseModule2Dto(MeveoModule meveoModule, ModuleDto moduleDto, User currentUser) throws MeveoApiException {
        if (!StringUtils.isBlank(meveoModule.getLogoPicture())) {
            byte[] pictureFileData = readModulePicture(meveoModule, meveoModule.getLogoPicture());
            moduleDto.setLogoPictureFile(pictureFileData);
        }
        List<MeveoModuleItem> items = meveoModule.getModuleItems();
        if (items != null && items.size() > 0) {
            Provider provider = currentUser.getProvider();
            for (MeveoModuleItem item : items) {
                switch (item.getItemType()) {
                case CET:
                    CustomEntityTemplateDto cetDto = customEntityApi.findEntityTemplate(item.getItemCode(), currentUser);
                    moduleDto.getCetDtos().add(cetDto);
                    break;
                case CFT:
                    CustomFieldTemplateDto cftDto = customFieldTemplateApi.find(item.getItemCode(), item.getAppliesTo(), provider);
                    moduleDto.getCftDtos().add(cftDto);
                    break;
                case FILTER:
                    FilterDto filterDto = filterApi.findFilter(item.getItemCode(), provider);
                    moduleDto.getFilterDtos().add(filterDto);
                    break;
                case SCRIPT:
                    ScriptInstanceDto scriptDto = scriptInstanceApi.findScriptInstance(item.getItemCode(), currentUser);
                    moduleDto.getScriptDtos().add(scriptDto);
                    break;
                case JOBINSTANCE:
                    moduleDto = getJobInstanceDto(item.getItemCode(), currentUser, moduleDto);
                    break;
                case NOTIFICATION:
                    Notification notification = genericNotificationService.findByCode(item.getItemCode(), provider);
                    if (notification != null) {
                        if (notification instanceof ScriptNotification) {
                            NotificationDto notificationDto = notificationApi.find(item.getItemCode(), provider);
                            moduleDto.getNotificationDtos().add(notificationDto);
                            if (!StringUtils.isBlank(notificationDto.getScriptInstanceCode())) {
                                ScriptInstanceDto scriptInstanceDto = scriptInstanceApi.findScriptInstance(notificationDto.getScriptInstanceCode(), currentUser);
                                moduleDto.getScriptDtos().add(scriptInstanceDto);
                            }
                        } else if (notification instanceof EmailNotification) {
                            EmailNotificationDto emailNotifiDto = emailNotificationApi.find(item.getItemCode(), provider);
                            moduleDto.getEmailNotifDtos().add(emailNotifiDto);
                        } else if (notification instanceof JobTrigger) {
                            JobTriggerDto jobTriggerDto = jobTriggerApi.find(item.getItemCode(), provider);
                            moduleDto.getJobTriggerDtos().add(jobTriggerDto);
                        } else if (notification instanceof WebHook) {
                            WebhookNotificationDto webhookNotifDto = webhookNotificationApi.find(item.getItemCode(), provider);
                            moduleDto.getWebhookNotifDtos().add(webhookNotifDto);
                        }
                        CounterTemplate counter = notification.getCounterTemplate();
                        if (!StringUtils.isBlank(counter)) {
                            CounterTemplateDto counterDto = new CounterTemplateDto(counter);
                            moduleDto.getCounterDtos().add(counterDto);
                        }
                    }
                    break;
                case SUBMODULE:
                    ModuleDto subModuleDto = get(item.getItemCode(), currentUser);
                    moduleDto.getSubModules().add(subModuleDto);
                    break;
                case MEASURABLEQUANTITIES:
                    MeasurableQuantityDto measurableQuantityDto = measurableQuantityApi.find(item.getItemCode(), currentUser);
                    moduleDto.getMeasurableQuantities().add(measurableQuantityDto);
                    break;
                case CHART:
                    ChartDto chartDto = chartApi.find(item.getItemCode(), currentUser);
                    moduleDto.getCharts().add(chartDto);
                    break;
                default:
                }
            }
        }
        return moduleDto;
    }

    private ModuleDto getJobInstanceDto(String jobInstanceCode, User currentUser, ModuleDto moduleDto) throws MeveoApiException {
        if (jobInstanceCode == null) {
            return moduleDto;
        }
        JobInstanceDto jobInstanceDto = jobInstanceApi.find(jobInstanceCode, currentUser.getProvider());
        moduleDto.getJobDtos().add(jobInstanceDto);
        if (!StringUtils.isBlank(jobInstanceDto.getTimerCode())) {
            TimerEntityDto timerDto = timerEntityApi.find(jobInstanceDto.getTimerCode(), currentUser);
            moduleDto.getTimerEntityDtos().add(timerDto);
        }
        String jobInstanceNextCode = jobInstanceDto.getFollowingJob();
        return getJobInstanceDto(jobInstanceNextCode, currentUser, moduleDto);
    }

    private MeveoModule parseModuleFromDto(MeveoModule meveoModule, ModuleDto moduleDto, User currentUser) throws MeveoApiException {
        meveoModule.setCode(moduleDto.getCode());
        meveoModule.setDescription(moduleDto.getDescription());
        meveoModule.setLicense(moduleDto.getLicense());
        meveoModule.setLogoPicture(moduleDto.getLogoPicture());
        if (!StringUtils.isBlank(moduleDto.getLogoPicture()) && moduleDto.getLogoPictureFile() != null) {
            writeModulePicture(currentUser, moduleDto.getLogoPicture(), moduleDto.getLogoPictureFile());
        }
        MeveoModuleItem item = null;
        if (moduleDto.getCetDtos() != null) {
            for (CustomEntityTemplateDto dto : moduleDto.getCetDtos()) {
                customEntityApi.createOrUpdateEntityTemplate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.CET);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getCftDtos() != null) {
            for (CustomFieldTemplateDto dto : moduleDto.getCftDtos()) {
                customFieldTemplateApi.createOrUpdate(dto, null, currentUser);
                item = new MeveoModuleItem(dto.getCode(), dto.getAppliesTo(), ModuleItemTypeEnum.CFT);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getFilterDtos() != null) {
            for (FilterDto dto : moduleDto.getFilterDtos()) {
                filterApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.FILTER);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getTimerEntityDtos() != null) {
            for (TimerEntityDto dto : moduleDto.getTimerEntityDtos()) {
                timerEntityApi.createOrUpdate(dto, currentUser);
            }
        }
        if (moduleDto.getJobNextDtos() != null) {
            for (JobInstanceDto dto : moduleDto.getJobNextDtos()) {
                jobInstanceApi.createOrUpdate(dto, currentUser);
            }
        }
        if (moduleDto.getJobDtos() != null) {
            for (JobInstanceDto dto : moduleDto.getJobDtos()) {
                jobInstanceApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.JOBINSTANCE);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getScriptDtos() != null) {
            for (ScriptInstanceDto dto : moduleDto.getScriptDtos()) {
                scriptInstanceApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.SCRIPT);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getNotificationDtos() != null) {
            for (NotificationDto dto : moduleDto.getNotificationDtos()) {
                notificationApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.NOTIFICATION);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getEmailNotifDtos() != null) {
            for (EmailNotificationDto dto : moduleDto.getEmailNotifDtos()) {
                emailNotificationApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.NOTIFICATION);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getJobTriggerDtos() != null) {
            for (JobTriggerDto dto : moduleDto.getJobTriggerDtos()) {
                jobTriggerApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.NOTIFICATION);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getWebhookNotifDtos() != null) {
            for (WebhookNotificationDto dto : moduleDto.getWebhookNotifDtos()) {
                webhookNotificationApi.createOrUpdate(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.NOTIFICATION);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getSubModules() != null) {
            for (ModuleDto dto : moduleDto.getSubModules()) {
                create(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.SUBMODULE);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getMeasurableQuantities() != null) {
            for (MeasurableQuantityDto dto : moduleDto.getMeasurableQuantities()) {
                measurableQuantityApi.create(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.MEASURABLEQUANTITIES);
                meveoModule.addModuleItem(item);
            }
        }
        if (moduleDto.getCharts() != null) {
            for (ChartDto dto : moduleDto.getCharts()) {
                chartApi.create(dto, currentUser);
                item = new MeveoModuleItem(dto.getCode(), ModuleItemTypeEnum.CHART);
                meveoModule.addModuleItem(item);
            }
        }
        return meveoModule;
    }

    private void writeModulePicture(User currentUser, String filename, byte[] fileData) {
        try {
            ModuleUtil.writeModulePicture(currentUser.getProvider().getCode(), filename, fileData);
        } catch (Exception e) {
            log.error("error when export module picture {}, info {}", filename, e.getMessage(), e);
        }
    }

    private void removeModulePicture(String provider, String filename) {
        try {
            ModuleUtil.removeModulePicture(provider, filename);
        } catch (Exception e) {
            log.error("error when delete module picture {} for provider {}, info {}", filename, provider, (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()),
                e);
        }
    }

    private byte[] readModulePicture(MeveoModule meveoModule, String filename) {
        try {
            return ModuleUtil.readModulePicture(meveoModule.getProvider().getCode(), filename);
        } catch (Exception e) {
            log.error("error when read module picture {}, info {}", filename, (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
        }
        return null;
    }
}
