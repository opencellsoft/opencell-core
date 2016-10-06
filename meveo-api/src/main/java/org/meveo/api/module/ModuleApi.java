package org.meveo.api.module;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.CustomEntityApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.FilterApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.catalog.CounterTemplateApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
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
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
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
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
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
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.module.ModuleScriptService;
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

    @Inject
    private OfferTemplateApi offerTemplateApi;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private ServiceTemplateApi serviceTemplateApi;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ModuleScriptService moduleScriptService;

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

        if (moduleDto instanceof BusinessOfferModelDto
                && (((BusinessOfferModelDto) moduleDto).getOfferTemplate() == null || StringUtils.isBlank(((BusinessOfferModelDto) moduleDto).getOfferTemplate().getCode()))) {
            missingParameters.add("offerTemplate.code");

        } else if (moduleDto instanceof BusinessServiceModelDto
                && (((BusinessServiceModelDto) moduleDto).getServiceTemplate() == null || StringUtils.isBlank(((BusinessServiceModelDto) moduleDto).getServiceTemplate().getCode()))) {
            missingParameters.add("serviceTemplate.code");

        } else if (moduleDto instanceof BusinessAccountModelDto && ((BusinessAccountModelDto) moduleDto).getHierarchyType() == null) {
            missingParameters.add("hierarchyType");
        }

        if (moduleDto.getScript() != null) {
            // If script was passed code is needed if script source was not passed.
            if (StringUtils.isBlank(moduleDto.getScript().getCode()) && StringUtils.isBlank(moduleDto.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else if (!StringUtils.isBlank(moduleDto.getScript().getScript())) {
                String fullClassname = ScriptInstanceService.getFullClassname(moduleDto.getScript().getScript());
                if (!StringUtils.isBlank(moduleDto.getScript().getCode()) && !moduleDto.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                moduleDto.getScript().setCode(fullClassname);
            }
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();
        if (meveoModuleService.findByCode(moduleDto.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(MeveoModule.class, moduleDto.getCode());
        }
        MeveoModule meveoModule = new MeveoModule();
        if (moduleDto instanceof BusinessOfferModelDto) {
            meveoModule = new BusinessOfferModel();
        } else if (moduleDto instanceof BusinessServiceModelDto) {
            meveoModule = new BusinessServiceModel();
        } else if (moduleDto instanceof BusinessAccountModelDto) {
            meveoModule = new BusinessAccountModel();
        }

        parseModuleInfoOnlyFromDto(meveoModule, moduleDto, currentUser);

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

        if (moduleDto instanceof BusinessOfferModelDto
                && (((BusinessOfferModelDto) moduleDto).getOfferTemplate() == null || StringUtils.isBlank(((BusinessOfferModelDto) moduleDto).getOfferTemplate().getCode()))) {
            missingParameters.add("offerTemplate.code");

        } else if (moduleDto instanceof BusinessServiceModelDto
                && (((BusinessServiceModelDto) moduleDto).getServiceTemplate() == null || StringUtils.isBlank(((BusinessServiceModelDto) moduleDto).getServiceTemplate().getCode()))) {
            missingParameters.add("serviceTemplate.code");

        } else if (moduleDto instanceof BusinessAccountModelDto && ((BusinessAccountModelDto) moduleDto).getHierarchyType() == null) {
            missingParameters.add("type");
        }

        if (moduleDto.getScript() != null) {
            // If script was passed code is needed if script source was not passed.
            if (StringUtils.isBlank(moduleDto.getScript().getCode()) && StringUtils.isBlank(moduleDto.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else if (!StringUtils.isBlank(moduleDto.getScript().getScript())) {
                String fullClassname = ScriptInstanceService.getFullClassname(moduleDto.getScript().getScript());
                if (!StringUtils.isBlank(moduleDto.getScript().getCode()) && !moduleDto.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                moduleDto.getScript().setCode(fullClassname);
            }
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();
        MeveoModule meveoModule = meveoModuleService.findByCode(moduleDto.getCode(), provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, moduleDto.getCode());
        }

        if (!meveoModule.isDownloaded()) {
            throw new ActionForbiddenException(meveoModule.getClass(), moduleDto.getCode(), "install",
                "Module with the same code is being developped locally, can not overwrite it.");
        }

        if (meveoModule.getModuleItems() != null) {
            Iterator<MeveoModuleItem> itr = meveoModule.getModuleItems().iterator();
            while (itr.hasNext()) {
                MeveoModuleItem i = itr.next();
                i.setMeveoModule(null);
                itr.remove();
            }
        }
        parseModuleInfoOnlyFromDto(meveoModule, moduleDto, currentUser);
        meveoModuleService.update(meveoModule, currentUser);
    }

    public void delete(String code, User currentUser) throws EntityDoesNotExistsException, BusinessException {
        Provider provider = currentUser.getProvider();
        MeveoModule meveoModule = meveoModuleService.findByCode(code, currentUser.getProvider());
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, code);
        }
        String logoPicture = meveoModule.getLogoPicture();
        meveoModuleService.remove(meveoModule, currentUser);
        removeModulePicture(provider.getCode(), logoPicture);

    }

    public List<ModuleDto> list(Class<? extends MeveoModule> clazz, User currentUser) throws MeveoApiException, BusinessException {
        Provider provider = currentUser.getProvider();
        List<MeveoModule> meveoModules = null;
        if (clazz == null) {
            meveoModules = meveoModuleService.list(provider);

        } else {
            Map<String, Object> filters = new HashMap<>();
            filters.put(PersistenceService.SEARCH_CURRENT_PROVIDER, provider);
            filters.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, clazz);

            meveoModules = meveoModuleService.list(new PaginationConfiguration(filters));
        }

        List<ModuleDto> result = new ArrayList<ModuleDto>();
        ModuleDto moduleDto = null;
        for (MeveoModule meveoModule : meveoModules) {
            moduleDto = meveoModuleService.moduleToDto(meveoModule, currentUser.getProvider());
            result.add(moduleDto);
        }
        return result;
    }

    public ModuleDto get(String code, Class<? extends MeveoModule> moduleClass, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
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

    public MeveoModule install(ModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(moduleDto.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        MeveoModule meveoModule = meveoModuleService.findByCode(moduleDto.getCode(), currentUser.getProvider());
        if (meveoModule == null) {
            create(moduleDto, currentUser);
            meveoModule = meveoModuleService.findByCode(moduleDto.getCode(), currentUser.getProvider());
        } else {

            if (!meveoModule.isDownloaded()) {
                throw new ActionForbiddenException(meveoModule.getClass(), moduleDto.getCode(), "install",
                    "Module with the same code is being developped locally, can not overwrite it.");
            }

            if (meveoModule.isInstalled()) {
                throw new ActionForbiddenException(meveoModule.getClass(), moduleDto.getCode(), "install", "Module is already installed");
            }

            try {
                moduleDto = MeveoModuleService.moduleSourceToDto(meveoModule);
            } catch (JAXBException e) {
                log.error("Failed to parse module {} source", meveoModule.getCode(), e);
                throw new BusinessException("Failed to parse module source", e);
            }
        }

        if (meveoModule.getScript() != null) {
            moduleScriptService.preInstallModule(meveoModule.getScript().getCode(), meveoModule, currentUser);
        }

        unpackAndInstallModuleItems(meveoModule, moduleDto, currentUser);

        meveoModule.setInstalled(true);
        meveoModule = meveoModuleService.update(meveoModule, currentUser);

        if (meveoModule.getScript() != null) {
            moduleScriptService.postInstallModule(meveoModule.getScript().getCode(), meveoModule, currentUser);
        }
        return meveoModule;
    }

    public void uninstall(String code, Class<? extends MeveoModule> moduleClass, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
        }

        if (!meveoModule.isInstalled()) {
            throw new ActionForbiddenException(meveoModule.getClass(), code, "uninstall", "Module is not installed or already enabled");
        }
        meveoModuleService.uninstall(meveoModule, currentUser);
    }

    private void parseModuleInfoOnlyFromDtoBOM(BusinessOfferModel bom, BusinessOfferModelDto bomDto, User currentUser) throws MeveoApiException, BusinessException {
        // nothing to do for now
    }

    private void unpackAndInstallBOMItems(BusinessOfferModel bom, BusinessOfferModelDto bomDto, User currentUser) throws MeveoApiException, BusinessException {

        // Should create it or update offerTemplate only if it has full information only
        if (!bomDto.getOfferTemplate().isCodeOnly()) {
            offerTemplateApi.createOrUpdate(bomDto.getOfferTemplate(), currentUser);
        }
        OfferTemplate offerTemplate = offerTemplateService.findByCode(bomDto.getOfferTemplate().getCode(), currentUser.getProvider());
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, bomDto.getOfferTemplate().getCode());
        }

        bom.setOfferTemplate(offerTemplate);
    }

    private void parseModuleInfoOnlyFromDtoBSM(BusinessServiceModel bsm, BusinessServiceModelDto bsmDto, User currentUser) throws MeveoApiException, BusinessException {

        bsm.setDuplicatePricePlan(bsmDto.isDuplicatePricePlan());
        bsm.setDuplicateService(bsmDto.isDuplicateService());
    }

    private void unpackAndInstallBSMItems(BusinessServiceModel bsm, BusinessServiceModelDto bsmDto, User currentUser) throws MeveoApiException, BusinessException {

        // Should create it or update serviceTemplate only if it has full information only
        if (!bsmDto.getServiceTemplate().isCodeOnly()) {
            serviceTemplateApi.createOrUpdate(bsmDto.getServiceTemplate(), currentUser);
        }
        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(bsmDto.getServiceTemplate().getCode(), currentUser.getProvider());
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, bsmDto.getServiceTemplate().getCode());
        }

        bsm.setServiceTemplate(serviceTemplate);
    }

    private void parseModuleInfoOnlyFromDtoBAM(BusinessAccountModel bam, BusinessAccountModelDto bamDto, User currentUser) throws MeveoApiException, BusinessException {
        bam.setHierarchyType(bamDto.getHierarchyType());
    }

    private void unpackAndInstallBAMItems(BusinessAccountModel bam, BusinessAccountModelDto bamDto, User currentUser) throws MeveoApiException, BusinessException {

        // nothing to do for now
    }

    public void parseModuleInfoOnlyFromDto(MeveoModule meveoModule, ModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {
        meveoModule.setCode(moduleDto.getCode());
        meveoModule.setDescription(moduleDto.getDescription());
        meveoModule.setLicense(moduleDto.getLicense());
        meveoModule.setLogoPicture(moduleDto.getLogoPicture());
        if (!StringUtils.isBlank(moduleDto.getLogoPicture()) && moduleDto.getLogoPictureFile() != null) {
            writeModulePicture(currentUser, moduleDto.getLogoPicture(), moduleDto.getLogoPictureFile());
        }
        if (meveoModule.isTransient()) {
            meveoModule.setInstalled(false);
        }

        // Converting subclasses of MeveoModuleDto class
        if (moduleDto instanceof BusinessServiceModelDto) {
            parseModuleInfoOnlyFromDtoBSM((BusinessServiceModel) meveoModule, (BusinessServiceModelDto) moduleDto, currentUser);

        } else if (moduleDto instanceof BusinessOfferModelDto) {
            parseModuleInfoOnlyFromDtoBOM((BusinessOfferModel) meveoModule, (BusinessOfferModelDto) moduleDto, currentUser);

        } else if (moduleDto instanceof BusinessAccountModelDto) {
            parseModuleInfoOnlyFromDtoBAM((BusinessAccountModel) meveoModule, (BusinessAccountModelDto) moduleDto, currentUser);
        }

        // Extract module script used for installation and module activation
        ScriptInstance scriptInstance = null;
        // Should create it or update script only if it has full information only
        if (moduleDto.getScript() != null) {
            if (!moduleDto.getScript().isCodeOnly()) {
                scriptInstanceApi.createOrUpdate(moduleDto.getScript(), currentUser);
            }

            scriptInstance = scriptInstanceService.findByCode(moduleDto.getScript().getCode(), currentUser.getProvider());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, moduleDto.getScript().getCode());
            }
        }
        meveoModule.setScript(scriptInstance);

        // Store module DTO into DB to be used later for installation
        try {
            Marshaller m = JAXBContext.newInstance(ModuleDto.class).createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(moduleDto, w);
            meveoModule.setModuleSource(w.toString());

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private void unpackAndInstallModuleItems(MeveoModule meveoModule, ModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

        // Converting subclasses of MeveoModuleDto class
        if (moduleDto instanceof BusinessServiceModelDto) {
            unpackAndInstallBSMItems((BusinessServiceModel) meveoModule, (BusinessServiceModelDto) moduleDto, currentUser);

        } else if (moduleDto instanceof BusinessOfferModelDto) {
            unpackAndInstallBOMItems((BusinessOfferModel) meveoModule, (BusinessOfferModelDto) moduleDto, currentUser);

        } else if (moduleDto instanceof BusinessAccountModelDto) {
            unpackAndInstallBAMItems((BusinessAccountModel) meveoModule, (BusinessAccountModelDto) moduleDto, currentUser);
        }

        if (meveoModule.getModuleItems() != null) {
            meveoModule.getModuleItems().clear();
        }

        if (moduleDto.getModuleItems() == null) {
            return;
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
                install((ModuleDto) dto, currentUser);

                Class<? extends MeveoModule> moduleClazz = MeveoModule.class;
                if (dto instanceof BusinessOfferModelDto) {
                    moduleClazz = BusinessOfferModel.class;
                } else if (dto instanceof BusinessServiceModelDto) {
                    moduleClazz = BusinessServiceModel.class;
                } else if (dto instanceof BusinessAccountModelDto) {
                    moduleClazz = BusinessAccountModel.class;
                }
                meveoModule.addModuleItem(new MeveoModuleItem(((ModuleDto) dto).getCode(), moduleClazz.getName(), null));

            } else if (dto instanceof MeasurableQuantityDto) {
                measurableQuantityApi.createOrUpdate((MeasurableQuantityDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((MeasurableQuantityDto) dto).getCode(), MeasurableQuantity.class.getName(), null));

            } else if (dto instanceof ChartDto) {
                chartApi.createOrUpdate((ChartDto) dto, currentUser);
                Class chartClass = dto instanceof BarChartDto ? BarChart.class : dto instanceof LineChartDto ? LineChart.class : PieChart.class;
                meveoModule.addModuleItem(new MeveoModuleItem(((ChartDto) dto).getCode(), chartClass.getName(), null));

            } else if (dto instanceof CounterTemplateDto) {
                counterTemplateApi.createOrUpdate((CounterTemplateDto) dto, currentUser);
                meveoModule.addModuleItem(new MeveoModuleItem(((CounterTemplateDto) dto).getCode(), CounterTemplate.class.getName(), null));
            }
        }
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

    public void enable(String code, Class<? extends MeveoModule> moduleClass, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
        }

        if (!meveoModule.isInstalled() || meveoModule.isActive()) {
            throw new ActionForbiddenException(meveoModule.getClass(), code, "enable", "Module is not installed or already enabled");
        }
        meveoModuleService.enable(meveoModule, currentUser);
    }

    public void disable(String code, Class<? extends MeveoModule> moduleClass, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
        }

        if (!meveoModule.isInstalled() || meveoModule.isDisabled()) {
            throw new ActionForbiddenException(meveoModule.getClass(), code, "disable", "Module is not installed or already disabled");
        }

        meveoModuleService.disable(meveoModule, currentUser);
    }
}