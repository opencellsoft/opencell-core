package org.meveo.api.module;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.ApiService;
import org.meveo.api.ApiVersionedService;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.EntityCustomActionApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.catalog.ProductTemplateApi;
import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.ModuleItem;
import org.meveo.model.VersionedEntity;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.module.ModuleScriptInterface;
import org.meveo.service.script.module.ModuleScriptService;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 **/
@Stateless
public class MeveoModuleApi extends BaseCrudApi<MeveoModule, MeveoModuleDto> {

    @Inject
    private MeveoModuleService meveoModuleService;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private EntityCustomActionApi entityCustomActionApi;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

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

    @Inject
    private ProductTemplateApi productTemplateApi;

    @Inject
    private ProductTemplateService productTemplateService;

    private static JAXBContext jaxbCxt;
    static {
        try {
            jaxbCxt = JAXBContext.newInstance(MeveoModuleDto.class);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public MeveoModule create(MeveoModuleDto moduleDto) throws MeveoApiException, BusinessException {

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

        } else if (moduleDto instanceof BusinessServiceModelDto && (((BusinessServiceModelDto) moduleDto).getServiceTemplate() == null
                || StringUtils.isBlank(((BusinessServiceModelDto) moduleDto).getServiceTemplate().getCode()))) {
            missingParameters.add("serviceTemplate.code");

        } else if (moduleDto instanceof BusinessProductModelDto && (((BusinessProductModelDto) moduleDto).getProductTemplate() == null
                || StringUtils.isBlank(((BusinessProductModelDto) moduleDto).getProductTemplate().getCode()))) {
            missingParameters.add("productTemplate.code");

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

        if (meveoModuleService.findByCode(moduleDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(MeveoModule.class, moduleDto.getCode());
        }
        MeveoModule meveoModule = new MeveoModule();
        if (moduleDto instanceof BusinessOfferModelDto) {
            meveoModule = new BusinessOfferModel();
        } else if (moduleDto instanceof BusinessServiceModelDto) {
            meveoModule = new BusinessServiceModel();
        } else if (moduleDto instanceof BusinessAccountModelDto) {
            meveoModule = new BusinessAccountModel();
        } else if (moduleDto instanceof BusinessProductModelDto) {
            meveoModule = new BusinessProductModel();
        }

        parseModuleInfoOnlyFromDto(meveoModule, moduleDto);

        meveoModuleService.create(meveoModule);

        return meveoModule;
    }

    public MeveoModule update(MeveoModuleDto moduleDto) throws MeveoApiException, BusinessException {

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

        } else if (moduleDto instanceof BusinessServiceModelDto && (((BusinessServiceModelDto) moduleDto).getServiceTemplate() == null
                || StringUtils.isBlank(((BusinessServiceModelDto) moduleDto).getServiceTemplate().getCode()))) {
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

        MeveoModule meveoModule = meveoModuleService.findByCode(moduleDto.getCode());
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
        parseModuleInfoOnlyFromDto(meveoModule, moduleDto);
        meveoModule = meveoModuleService.update(meveoModule);
        return meveoModule;
    }

    public void delete(String code) throws EntityDoesNotExistsException, BusinessException {

        MeveoModule meveoModule = meveoModuleService.findByCode(code);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, code);
        }
        String logoPicture = meveoModule.getLogoPicture();
        meveoModuleService.remove(meveoModule);
        removeModulePicture(logoPicture);

    }

    public List<MeveoModuleDto> list(Class<? extends MeveoModule> clazz) throws MeveoApiException, BusinessException {

        List<MeveoModule> meveoModules = null;
        if (clazz == null) {
            meveoModules = meveoModuleService.list();

        } else {
            Map<String, Object> filters = new HashMap<>();
            filters.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, clazz);

            meveoModules = meveoModuleService.list(new PaginationConfiguration(filters));
        }

        List<MeveoModuleDto> result = new ArrayList<MeveoModuleDto>();
        MeveoModuleDto moduleDto = null;
        for (MeveoModule meveoModule : meveoModules) {
            try {
                moduleDto = moduleToDto(meveoModule);
                result.add(moduleDto);
            } catch (MeveoApiException e) {
                // Dont care, it was logged earlier in moduleToDto()
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public MeveoModuleDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code [BOM: businessOfferModelCode, BSM: businessServiceModelCode, BAM: businessAccountModelCode]");
            handleMissingParameters();
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, code);
        }
        MeveoModuleDto moduleDto = moduleToDto(meveoModule);
        return moduleDto;
    }

    public MeveoModule createOrUpdate(MeveoModuleDto postData) throws MeveoApiException, BusinessException {
        MeveoModule meveoModule = meveoModuleService.findByCode(postData.getCode());
        if (meveoModule == null) {
            // create
            return create(postData);
        } else {
            // update
            return update(postData);
        }
    }

    public MeveoModule install(MeveoModuleDto moduleDto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(moduleDto.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        MeveoModule meveoModule = meveoModuleService.findByCode(moduleDto.getCode());
        boolean installed = false;
        if (meveoModule == null) {
            create(moduleDto);
            meveoModule = meveoModuleService.findByCode(moduleDto.getCode());

        } else {
            if (!meveoModule.isDownloaded()) {
                throw new ActionForbiddenException(meveoModule.getClass(), moduleDto.getCode(), "install",
                    "Module with the same code is being developped locally, can not overwrite it.");
            }

            if (meveoModule.isInstalled()) {
                // throw new ActionForbiddenException(meveoModule.getClass(), moduleDto.getCode(), "install", "Module is already installed");
                installed = true;

            } else {
                try {
                    moduleDto = MeveoModuleService.moduleSourceToDto(meveoModule);
                } catch (JAXBException e) {
                    log.error("Failed to parse module {} source", meveoModule.getCode(), e);
                    throw new BusinessException("Failed to parse module source", e);
                }
            }
        }

        if (!installed) {
            ModuleScriptInterface moduleScript = null;
            if (meveoModule.getScript() != null) {
                moduleScript = moduleScriptService.preInstallModule(meveoModule.getScript().getCode(), meveoModule);
            }

            unpackAndInstallModuleItems(meveoModule, moduleDto);

            meveoModule.setInstalled(true);
            meveoModule.setDisabled(false);
            meveoModule = meveoModuleService.update(meveoModule);

            if (moduleScript != null) {
                moduleScriptService.postInstallModule(moduleScript, meveoModule);
            }
        }

        return meveoModule;
    }

    public void uninstall(String code, Class<? extends MeveoModule> moduleClass) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
        }

        if (!meveoModule.isInstalled()) {
            throw new ActionForbiddenException(meveoModule.getClass(), code, "uninstall", "Module is not installed or already enabled");
        }
        meveoModuleService.uninstall(meveoModule);
    }

    private void parseModuleInfoOnlyFromDtoBOM(BusinessOfferModel bom, BusinessOfferModelDto bomDto) throws MeveoApiException, BusinessException {
        // nothing to do for now
    }

    private void unpackAndInstallBOMItems(BusinessOfferModel bom, BusinessOfferModelDto bomDto) throws MeveoApiException, BusinessException {

        // Should create it or update offerTemplate only if it has full information only
        if (!bomDto.getOfferTemplate().isCodeOnly()) {
            offerTemplateApi.createOrUpdate(bomDto.getOfferTemplate());
        }
        OfferTemplate offerTemplate = offerTemplateService.findByCode(bomDto.getOfferTemplate().getCode(), bomDto.getOfferTemplate().getValidFrom(),
            bomDto.getOfferTemplate().getValidTo());
        if (offerTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class,
                bomDto.getOfferTemplate().getCode() + " / " + DateUtils.formatDateWithPattern(bomDto.getOfferTemplate().getValidFrom(), datePattern) + " / "
                        + DateUtils.formatDateWithPattern(bomDto.getOfferTemplate().getValidTo(), datePattern));
        }

        bom.setOfferTemplate(offerTemplate);
    }

    private void parseModuleInfoOnlyFromDtoBSM(BusinessServiceModel bsm, BusinessServiceModelDto bsmDto) throws MeveoApiException, BusinessException {

        bsm.setDuplicatePricePlan(bsmDto.isDuplicatePricePlan());
        bsm.setDuplicateService(bsmDto.isDuplicateService());
    }

    private void unpackAndInstallBSMItems(BusinessServiceModel bsm, BusinessServiceModelDto bsmDto) throws MeveoApiException, BusinessException {

        // Should create it or update serviceTemplate only if it has full information only
        if (!bsmDto.getServiceTemplate().isCodeOnly()) {
            serviceTemplateApi.createOrUpdate(bsmDto.getServiceTemplate());
        }
        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(bsmDto.getServiceTemplate().getCode());
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, bsmDto.getServiceTemplate().getCode());
        }

        bsm.setServiceTemplate(serviceTemplate);
    }

    private void parseModuleInfoOnlyFromDtoBAM(BusinessAccountModel bam, BusinessAccountModelDto bamDto) throws MeveoApiException, BusinessException {
        bam.setHierarchyType(bamDto.getHierarchyType());
    }

    private void unpackAndInstallBAMItems(BusinessAccountModel bam, BusinessAccountModelDto bamDto) throws MeveoApiException, BusinessException {

        // nothing to do for now
    }

    private void parseModuleInfoOnlyFromDtoBPM(BusinessProductModel bm, BusinessProductModelDto dto) {
        // nothing to do for now
    }

    private void unpackAndInstallBPMItems(BusinessProductModel businessModel, BusinessProductModelDto dto) throws MeveoApiException, BusinessException {
        // Should create it or update productTemplate only if it has full information only
        if (!dto.getProductTemplate().isCodeOnly()) {
            productTemplateApi.createOrUpdate(dto.getProductTemplate());
        }
        ProductTemplate productTemplate = productTemplateService.findByCode(dto.getProductTemplate().getCode(), dto.getProductTemplate().getValidFrom(),
            dto.getProductTemplate().getValidTo());
        if (productTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class,
                dto.getProductTemplate().getCode() + " / " + DateUtils.formatDateWithPattern(dto.getProductTemplate().getValidFrom(), datePattern) + " / "
                        + DateUtils.formatDateWithPattern(dto.getProductTemplate().getValidTo(), datePattern));
        }

        businessModel.setProductTemplate(productTemplate);
    }

    public void parseModuleInfoOnlyFromDto(MeveoModule meveoModule, MeveoModuleDto moduleDto) throws MeveoApiException, BusinessException {
        meveoModule.setCode(StringUtils.isBlank(moduleDto.getUpdatedCode()) ? moduleDto.getCode() : moduleDto.getUpdatedCode());
        meveoModule.setDescription(moduleDto.getDescription());
        meveoModule.setLicense(moduleDto.getLicense());
        meveoModule.setLogoPicture(moduleDto.getLogoPicture());
        if (!StringUtils.isBlank(moduleDto.getLogoPicture()) && moduleDto.getLogoPictureFile() != null) {
            writeModulePicture(moduleDto.getLogoPicture(), moduleDto.getLogoPictureFile());
        }
        if (meveoModule.isTransient()) {
            meveoModule.setInstalled(false);
        }

        // Converting subclasses of MeveoModuleDto class
        if (moduleDto instanceof BusinessServiceModelDto) {
            parseModuleInfoOnlyFromDtoBSM((BusinessServiceModel) meveoModule, (BusinessServiceModelDto) moduleDto);

        } else if (moduleDto instanceof BusinessOfferModelDto) {
            parseModuleInfoOnlyFromDtoBOM((BusinessOfferModel) meveoModule, (BusinessOfferModelDto) moduleDto);

        } else if (moduleDto instanceof BusinessAccountModelDto) {
            parseModuleInfoOnlyFromDtoBAM((BusinessAccountModel) meveoModule, (BusinessAccountModelDto) moduleDto);

        } else if (moduleDto instanceof BusinessProductModelDto) {
            parseModuleInfoOnlyFromDtoBPM((BusinessProductModel) meveoModule, (BusinessProductModelDto) moduleDto);
        }

        // Extract module script used for installation and module activation
        ScriptInstance scriptInstance = null;
        // Should create it or update script only if it has full information only
        if (moduleDto.getScript() != null) {
            if (!moduleDto.getScript().isCodeOnly()) {
                scriptInstanceApi.createOrUpdate(moduleDto.getScript());
            }

            scriptInstance = scriptInstanceService.findByCode(moduleDto.getScript().getCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, moduleDto.getScript().getCode());
            }
        }
        meveoModule.setScript(scriptInstance);

        // Store module DTO into DB to be used later for installation
        try {
            Marshaller m = jaxbCxt.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(moduleDto, w);
            meveoModule.setModuleSource(w.toString());

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void unpackAndInstallModuleItems(MeveoModule meveoModule, MeveoModuleDto moduleDto) throws MeveoApiException, BusinessException {

        if (moduleDto.getModuleItems() != null) {

            meveoModule.getModuleItems().clear();

            for (BaseDto dto : moduleDto.getModuleItems()) {

                try {

                    if (dto instanceof MeveoModuleDto) {
                        install((MeveoModuleDto) dto);

                        Class<? extends MeveoModule> moduleClazz = MeveoModule.class;
                        if (dto instanceof BusinessOfferModelDto) {
                            moduleClazz = BusinessOfferModel.class;
                        } else if (dto instanceof BusinessServiceModelDto) {
                            moduleClazz = BusinessServiceModel.class;
                        } else if (dto instanceof BusinessAccountModelDto) {
                            moduleClazz = BusinessAccountModel.class;
                        } else if (dto instanceof BusinessProductModelDto) {
                            moduleClazz = BusinessProductModel.class;
                        }
                        meveoModule.addModuleItem(new MeveoModuleItem(((MeveoModuleDto) dto).getCode(), moduleClazz.getName(), null, null));

                    } else if (dto instanceof CustomFieldTemplateDto) {

                        customFieldTemplateApi.createOrUpdate((CustomFieldTemplateDto) dto, null);
                        customFieldTemplateApi.enableOrDisable(((CustomFieldTemplateDto) dto).getCode(), ((CustomFieldTemplateDto) dto).getAppliesTo(), true);
                        meveoModule.addModuleItem(new MeveoModuleItem(((CustomFieldTemplateDto) dto).getCode(), CustomFieldTemplate.class.getName(),
                            ((CustomFieldTemplateDto) dto).getAppliesTo(), null));

                    } else if (dto instanceof EntityCustomActionDto) {
                        entityCustomActionApi.createOrUpdate((EntityCustomActionDto) dto, null);
                        entityCustomActionApi.enableOrDisable(((EntityCustomActionDto) dto).getCode(), ((EntityCustomActionDto) dto).getAppliesTo(), true);
                        meveoModule.addModuleItem(
                            new MeveoModuleItem(((EntityCustomActionDto) dto).getCode(), EntityCustomAction.class.getName(), ((EntityCustomActionDto) dto).getAppliesTo(), null));

                    } else {

                        String entityClassName = dto.getClass().getSimpleName().substring(0, dto.getClass().getSimpleName().lastIndexOf("Dto"));
                        Class<?> entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClassName, ModuleItem.class);
                        if (entityClass == null) {
                            throw new RuntimeException("No entity class or @ModuleItem annotation found for " + entityClassName);
                        }

                        if (entityClass.isAnnotationPresent(VersionedEntity.class)) {
                            ApiVersionedService apiService = getApiVersionedService(entityClass, true);
                            apiService.createOrUpdate(dto);
                            apiService.enableOrDisable((String) FieldUtils.readField(dto, "code", true), (Date) FieldUtils.readField(dto, "validFrom", true),
                                (Date) FieldUtils.readField(dto, "validTo", true), true);
                        } else {
                            ApiService apiService = getApiService(entityClass, true);
                            apiService.createOrUpdate(dto);
                            apiService.enableOrDisable((String) FieldUtils.readField(dto, "code", true), true);
                        }

                        DatePeriod validity = null;
                        if (ReflectionUtils.hasField(dto, "validFrom")) {
                            validity = new DatePeriod((Date) FieldUtils.readField(dto, "validFrom", true), (Date) FieldUtils.readField(dto, "validTo", true));
                        }

                        if (ReflectionUtils.hasField(dto, "appliesTo")) {
                            meveoModule.addModuleItem(new MeveoModuleItem((String) FieldUtils.readField(dto, "code", true), entityClass.getName(),
                                (String) FieldUtils.readField(dto, "appliesTo", true), validity));
                        } else {
                            meveoModule.addModuleItem(new MeveoModuleItem((String) FieldUtils.readField(dto, "code", true), entityClass.getName(), null, validity));
                        }
                    }

                } catch (IllegalAccessException e) {
                    log.error("Failed to access field value in DTO {}", dto, e);
                    throw new MeveoApiException("Failed to access field value in DTO: " + e.getMessage());

                } catch (MeveoApiException | BusinessException e) {
                    log.error("Failed to transform DTO into a module item. DTO {}", dto, e);
                    throw e;
                }
            }

        }

        // Converting subclasses of MeveoModuleDto class
        if (moduleDto instanceof BusinessServiceModelDto) {
            unpackAndInstallBSMItems((BusinessServiceModel) meveoModule, (BusinessServiceModelDto) moduleDto);

        } else if (moduleDto instanceof BusinessOfferModelDto) {
            unpackAndInstallBOMItems((BusinessOfferModel) meveoModule, (BusinessOfferModelDto) moduleDto);

        } else if (moduleDto instanceof BusinessAccountModelDto) {
            unpackAndInstallBAMItems((BusinessAccountModel) meveoModule, (BusinessAccountModelDto) moduleDto);

        } else if (moduleDto instanceof BusinessProductModelDto) {
            unpackAndInstallBPMItems((BusinessProductModel) meveoModule, (BusinessProductModelDto) moduleDto);
        }
    }

    private void writeModulePicture(String filename, byte[] fileData) {
        try {
            ModuleUtil.writeModulePicture(currentUser.getProviderCode(), filename, fileData);
        } catch (Exception e) {
            log.error("error when export module picture {}, info {}", filename, e.getMessage(), e);
        }
    }

    private void removeModulePicture(String filename) {
        try {
            ModuleUtil.removeModulePicture(currentUser.getProviderCode(), filename);
        } catch (Exception e) {
            log.error("error when delete module picture {}, info {}", filename, (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
        }
    }

    public void enable(String code, Class<? extends MeveoModule> moduleClass) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
        }

        if (!meveoModule.isInstalled() || meveoModule.isActive()) {
            throw new ActionForbiddenException(meveoModule.getClass(), code, "enable", "Module is not installed or already enabled");
        }
        meveoModuleService.enable(meveoModule);
    }

    public void disable(String code, Class<? extends MeveoModule> moduleClass) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (moduleClass == null) {
            moduleClass = MeveoModule.class;
        }

        MeveoModule meveoModule = meveoModuleService.findByCode(code);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(moduleClass, code);
        }

        if (!meveoModule.isInstalled() || meveoModule.isDisabled()) {
            throw new ActionForbiddenException(meveoModule.getClass(), code, "disable", "Module is not installed or already disabled");
        }

        meveoModuleService.disable(meveoModule);
    }

    /**
     * Convert MeveoModule or its subclass object to DTO representation.
     * 
     * @param module Module object
     * @return MeveoModuleDto object
     * @throws MeveoApiException meveo api exception.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MeveoModuleDto moduleToDto(MeveoModule module) throws MeveoApiException {

        if (module.isDownloaded() && !module.isInstalled()) {
            try {
                return MeveoModuleService.moduleSourceToDto(module);
            } catch (Exception e) {
                log.error("Failed to load module source {}", module.getCode(), e);
                throw new MeveoApiException("Failed to load module source");
            }
        }

        Class<? extends MeveoModuleDto> dtoClass = MeveoModuleDto.class;
        if (module instanceof BusinessServiceModel) {
            dtoClass = BusinessServiceModelDto.class;
        } else if (module instanceof BusinessOfferModel) {
            dtoClass = BusinessOfferModelDto.class;
        } else if (module instanceof BusinessAccountModel) {
            dtoClass = BusinessAccountModelDto.class;
        } else if (module instanceof BusinessProductModel) {
            dtoClass = BusinessProductModelDto.class;
        }

        MeveoModuleDto moduleDto = null;
        try {
            moduleDto = dtoClass.getConstructor(MeveoModule.class).newInstance(module);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            log.error("Failed to instantiate Module Dto. No reason for it to happen. ", e);
            throw new RuntimeException("Failed to instantiate Module Dto. No reason for it to happen. ", e);
        }

        if (!StringUtils.isBlank(module.getLogoPicture())) {
            try {
                moduleDto.setLogoPictureFile(ModuleUtil.readModulePicture(currentUser.getProviderCode(), module.getLogoPicture()));
            } catch (Exception e) {
                log.error("Failed to read module files {}, info {}", module.getLogoPicture(), e.getMessage(), e);
            }
        }

        List<MeveoModuleItem> moduleItems = module.getModuleItems();
        if (moduleItems != null) {
            for (MeveoModuleItem item : moduleItems) {

                try {
                    BaseDto itemDto = null;

                    if (item.getItemClass().equals(CustomFieldTemplate.class.getName())) {
                        itemDto = customFieldTemplateApi.findIgnoreNotFound(item.getItemCode(), item.getAppliesTo());

                    } else if (item.getItemClass().equals(EntityCustomAction.class.getName())) {
                        itemDto = entityCustomActionApi.findIgnoreNotFound(item.getItemCode(), item.getAppliesTo());

                    } else {
                        Class clazz = Class.forName(item.getItemClass());
                        if (clazz.isAnnotationPresent(VersionedEntity.class)) {
                            ApiVersionedService apiService = getApiVersionedService(item.getItemClass(), true);
                            itemDto = apiService.findIgnoreNotFound(item.getItemCode(), item.getValidity() != null ? item.getValidity().getFrom() : null,
                                item.getValidity() != null ? item.getValidity().getTo() : null);

                        } else {
                            ApiService apiService = getApiService(clazz, true);
                            itemDto = apiService.findIgnoreNotFound(item.getItemCode());
                        }
                    }
                    if (itemDto != null) {
                        moduleDto.addModuleItem(itemDto);
                    } else {
                        log.warn("Failed to find a module item {}", item);
                    }

                } catch (ClassNotFoundException e) {
                    log.error("Failed to find a class", e);
                    throw new MeveoApiException("Failed to access field value in DTO: " + e.getMessage());

                } catch (MeveoApiException e) {
                    log.error("Failed to transform module item to DTO. Module item {}", item, e);
                    throw e;
                }
            }
        }

        // Finish converting subclasses of MeveoModule class
        if (module instanceof BusinessServiceModel) {
            businessServiceModelToDto((BusinessServiceModel) module, (BusinessServiceModelDto) moduleDto);

        } else if (module instanceof BusinessOfferModel) {
            businessOfferModelToDto((BusinessOfferModel) module, (BusinessOfferModelDto) moduleDto);

        } else if (module instanceof BusinessAccountModel) {
            businessAccountModelToDto((BusinessAccountModel) module, (BusinessAccountModelDto) moduleDto);

        } else if (module instanceof BusinessProductModel) {
            businessProductModelToDto((BusinessProductModel) module, (BusinessProductModelDto) moduleDto);
        }

        return moduleDto;
    }

    /**
     * Convert BusinessProductModel object to DTO representation
     * 
     * @param bpm BusinessProductModel object to convert
     * @param dto BusinessProductModel object DTO representation (as result of base MeveoModule object conversion)
     * @return BusinessProductModel object DTO representation
     */
    private void businessProductModelToDto(BusinessProductModel bpm, BusinessProductModelDto dto) {

        if (bpm.getProductTemplate() != null) {
            dto.setProductTemplate(new ProductTemplateDto(bpm.getProductTemplate(), entityToDtoConverter.getCustomFieldsDTO(bpm.getProductTemplate(), true), false));
        }
    }

    /**
     * Convert BusinessOfferModel object to DTO representation
     * 
     * @param bom BusinessOfferModel object to convert
     * @param dto BusinessOfferModel object DTO representation (as result of base MeveoModule object conversion)
     * @return BusinessOfferModel object DTO representation
     */
    private void businessOfferModelToDto(BusinessOfferModel bom, BusinessOfferModelDto dto) {

        if (bom.getOfferTemplate() != null) {
            dto.setOfferTemplate(offerTemplateApi.convertOfferTemplateToDto(bom.getOfferTemplate()));
        }
    }

    /**
     * Finish converting BusinessServiceModel object to DTO representation
     * 
     * @param bsm BusinessServiceModel object to convert
     * @param dto BusinessServiceModel object DTO representation (as result of base MeveoModule object conversion)
     */
    private void businessServiceModelToDto(BusinessServiceModel bsm, BusinessServiceModelDto dto) {

        if (bsm.getServiceTemplate() != null) {
            dto.setServiceTemplate(new ServiceTemplateDto(bsm.getServiceTemplate(), entityToDtoConverter.getCustomFieldsDTO(bsm.getServiceTemplate(), true)));
        }
        dto.setDuplicateService(bsm.isDuplicateService());
        dto.setDuplicatePricePlan(bsm.isDuplicatePricePlan());

    }

    /**
     * Convert BusinessAccountModel object to DTO representation
     * 
     * @param bom BusinessAccountModel object to convert
     * @param dto BusinessAccountModel object DTO representation (as result of base MeveoModule object conversion)
     * @return BusinessAccountModel object DTO representation
     */
    private void businessAccountModelToDto(BusinessAccountModel bom, BusinessAccountModelDto dto) {

        dto.setHierarchyType(bom.getHierarchyType());
    }
}