package org.meveo.api.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.api.BaseApi;
import org.meveo.api.CustomEntityApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.FilterApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.catalog.CounterTemplateApi;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
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
import org.meveo.api.job.JobInstanceApi;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.notification.EmailNotificationApi;
import org.meveo.api.notification.JobTriggerApi;
import org.meveo.api.notification.NotificationApi;
import org.meveo.api.notification.WebhookNotificationApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
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
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.notification.WebHook;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.PieChart;

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
    private CounterTemplateApi counterTemplateApi;
    
    @Inject
    private ChartApi chartApi;

    public void create(ModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(moduleDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(moduleDto.getDescription())) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(moduleDto.getLicense())) {
            missingParameters.add("license");
        }
        
        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();
        if (meveoModuleService.findByCode(moduleDto.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(MeveoModule.class, moduleDto.getCode());
        }
        MeveoModule meveoModule = new MeveoModule();
        meveoModule.setCode(moduleDto.getCode());
        meveoModule = parseModuleFromDto(meveoModule, moduleDto, currentUser);

        meveoModuleService.create(meveoModule, currentUser);
    }

    public void update(ModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(moduleDto.getCode())) {
            missingParameters.add("module code is null");
        }
        if (StringUtils.isBlank(moduleDto.getDescription())) {
            missingParameters.add("description");
        }
        if (StringUtils.isBlank(moduleDto.getLicense())) {
            missingParameters.add("module license is null");
        }
        
        handleMissingParameters();
        

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
            moduleDto = meveoModuleService.moduleToDto(meveoModule, currentUser.getProvider());
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
        ModuleDto moduleDto = meveoModuleService.moduleToDto(meveoModule, currentUser.getProvider());
        return moduleDto;
    }

    public void createOrUpdate(ModuleDto postData, User currentUser) throws MeveoApiException, BusinessException {
        MeveoModule meveoModule = meveoModuleService.findByCode(postData.getCode(), currentUser.getProvider());
        if (meveoModule == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }

    @SuppressWarnings("rawtypes")
    private MeveoModule parseModuleFromDto(MeveoModule meveoModule, ModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {
        meveoModule.setCode(moduleDto.getCode());
        meveoModule.setDescription(moduleDto.getDescription());
        meveoModule.setLicense(moduleDto.getLicense());
        meveoModule.setLogoPicture(moduleDto.getLogoPicture());
        if (!StringUtils.isBlank(moduleDto.getLogoPicture()) && moduleDto.getLogoPictureFile() != null) {
            writeModulePicture(currentUser, moduleDto.getLogoPicture(), moduleDto.getLogoPictureFile());
        }
        for (BaseDto dto : moduleDto.getModuleItems()) {
            if (dto instanceof CustomEntityTemplateDto) {
                customEntityApi.createOrUpdateEntityTemplate((CustomEntityTemplateDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((CustomEntityTemplateDto) dto).getCode(), CustomEntityTemplate.class.getName(), null));

            } else if (dto instanceof CustomFieldTemplateDto) {
                customFieldTemplateApi.createOrUpdate((CustomFieldTemplateDto) dto, null, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((CustomFieldTemplateDto) dto).getCode(), CustomFieldTemplate.class.getName(), ((CustomFieldTemplateDto) dto)
                    .getAppliesTo()));

            } else if (dto instanceof FilterDto) {
                filterApi.createOrUpdate((FilterDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((FilterDto) dto).getCode(), Filter.class.getName(), null));

            } else if (dto instanceof TimerEntityDto) {
                timerEntityApi.createOrUpdate((TimerEntityDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((TimerEntityDto) dto).getCode(), TimerEntity.class.getName(), null));

            } else if (dto instanceof JobInstanceDto) {
                jobInstanceApi.createOrUpdate((JobInstanceDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((JobInstanceDto) dto).getCode(), JobInstance.class.getName(), null));

            } else if (dto instanceof ScriptInstanceDto) {
                scriptInstanceApi.createOrUpdate((ScriptInstanceDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((ScriptInstanceDto) dto).getCode(), ScriptInstance.class.getName(), null));

            } else if (dto instanceof EmailNotificationDto) {
                emailNotificationApi.createOrUpdate((EmailNotificationDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((EmailNotificationDto) dto).getCode(), EmailNotification.class.getName(), null));

            } else if (dto instanceof JobTriggerDto) {
                jobTriggerApi.createOrUpdate((JobTriggerDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((JobTriggerDto) dto).getCode(), JobTrigger.class.getName(), null));

            } else if (dto instanceof WebhookNotificationDto) {
                webhookNotificationApi.createOrUpdate((WebhookNotificationDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((WebhookNotificationDto) dto).getCode(), WebHook.class.getName(), null));

            } else if (dto instanceof NotificationDto) {
                notificationApi.createOrUpdate((NotificationDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((NotificationDto) dto).getCode(), ScriptNotification.class.getName(), null));

            } else if (dto instanceof ModuleDto) {
                createOrUpdate((ModuleDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((ModuleDto) dto).getCode(), MeveoModule.class.getName(), null));

            } else if (dto instanceof MeasurableQuantityDto) {
                measurableQuantityApi.createOrUpdate((MeasurableQuantityDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((MeasurableQuantityDto) dto).getCode(), MeasurableQuantity.class.getName(), null));

            } else if (dto instanceof ChartDto) {
                chartApi.createOrUpdate((ChartDto) dto, currentUser);
                Class chartClass = dto instanceof BarChartDto ? BarChart.class : dto instanceof LineChartDto ? LineChart.class : PieChart.class;
                meveoModule.addModuleItem(new MeveoModuleItem(((ChartDto) dto).getCode(), chartClass.getName(), null));
            
            }  else if (dto instanceof CounterTemplateDto) {
                counterTemplateApi.createOrUpdate((CounterTemplateDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((CounterTemplateDto) dto).getCode(), CounterTemplate.class.getName(), null));
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
}