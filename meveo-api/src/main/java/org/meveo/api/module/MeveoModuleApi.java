package org.meveo.api.module;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.lang.reflect.FieldUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.ApiService;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.EntityCustomActionApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ModuleItem;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.module.ModuleScriptService;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
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

    public MeveoModule create(MeveoModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

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

        return meveoModule;
    }

    public MeveoModule update(MeveoModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

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
        meveoModule = meveoModuleService.update(meveoModule, currentUser);
        return meveoModule;
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

    public List<MeveoModuleDto> list(Class<? extends MeveoModule> clazz, User currentUser) throws MeveoApiException, BusinessException {
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

        List<MeveoModuleDto> result = new ArrayList<MeveoModuleDto>();
        MeveoModuleDto moduleDto = null;
        for (MeveoModule meveoModule : meveoModules) {
            try {
                moduleDto = moduleToDto(meveoModule, currentUser);
                result.add(moduleDto);
            } catch (MeveoApiException e) {
                // Dont care, it was logged earlier in moduleToDto()
            }
        }
        return result;
    }

    public MeveoModuleDto find(String code, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code [BOM: businessOfferModelCode, BSM: businessServiceModelCode, BAM: businessAccountModelCode]");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
        if (meveoModule == null) {
            throw new EntityDoesNotExistsException(MeveoModule.class, code);
        }
        MeveoModuleDto moduleDto = moduleToDto(meveoModule, currentUser);
        return moduleDto;
    }

    public MeveoModule createOrUpdate(MeveoModuleDto postData, User currentUser) throws MeveoApiException, BusinessException {
        MeveoModule meveoModule = meveoModuleService.findByCode(postData.getCode(), currentUser.getProvider());
        if (meveoModule == null) {
            // create
            return create(postData, currentUser);
        } else {
            // update
            return update(postData, currentUser);
        }
    }

    public MeveoModule install(MeveoModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

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

    public void parseModuleInfoOnlyFromDto(MeveoModule meveoModule, MeveoModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {
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
            Marshaller m = JAXBContext.newInstance(MeveoModuleDto.class).createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(moduleDto, w);
            meveoModule.setModuleSource(w.toString());

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void unpackAndInstallModuleItems(MeveoModule meveoModule, MeveoModuleDto moduleDto, User currentUser) throws MeveoApiException, BusinessException {

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

            try {

                if (dto instanceof MeveoModuleDto) {
                    install((MeveoModuleDto) dto, currentUser);

                    Class<? extends MeveoModule> moduleClazz = MeveoModule.class;
                    if (dto instanceof BusinessOfferModelDto) {
                        moduleClazz = BusinessOfferModel.class;
                    } else if (dto instanceof BusinessServiceModelDto) {
                        moduleClazz = BusinessServiceModel.class;
                    } else if (dto instanceof BusinessAccountModelDto) {
                        moduleClazz = BusinessAccountModel.class;
                    }
                    meveoModule.addModuleItem(new MeveoModuleItem(((MeveoModuleDto) dto).getCode(), moduleClazz.getName(), null));

                } else if (dto instanceof CustomFieldTemplateDto) {
                    customFieldTemplateApi.createOrUpdate((CustomFieldTemplateDto) dto, null, currentUser);
                    meveoModule.addModuleItem(new MeveoModuleItem(((CustomFieldTemplateDto) dto).getCode(), CustomFieldTemplate.class.getName(), ((CustomFieldTemplateDto) dto)
                        .getAppliesTo()));

                } else if (dto instanceof EntityCustomActionDto) {
                    entityCustomActionApi.createOrUpdate((EntityCustomActionDto) dto, null, currentUser);
                    meveoModule.addModuleItem(new MeveoModuleItem(((EntityCustomActionDto) dto).getCode(), EntityCustomAction.class.getName(), ((EntityCustomActionDto) dto)
                        .getAppliesTo()));

                } else {

                    String entityClassName = dto.getClass().getSimpleName().substring(0, dto.getClass().getSimpleName().lastIndexOf("Dto"));
                    Class<?> entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClassName, ModuleItem.class);
                    if (entityClass == null) {
                        throw new RuntimeException("No entity class or @ModuleItem annotation found for " + entityClassName);
                    }

                    ApiService apiService = getApiService(dto, true);
                    apiService.createOrUpdate(dto, currentUser);

                    if (ReflectionUtils.hasField(dto, "appliesTo")) {
                        meveoModule.addModuleItem(new MeveoModuleItem((String) FieldUtils.readField(dto, "code", true), entityClass.getName(), (String) FieldUtils.readField(dto,
                            "appliesTo", true)));
                    } else {
                        meveoModule.addModuleItem(new MeveoModuleItem((String) FieldUtils.readField(dto, "code", true), entityClass.getName(), null));
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

    /**
     * Convert MeveoModule or its subclass object to DTO representation
     * 
     * @param module Module object
     * @param provider Provider
     * @return MeveoModuleDto object
     */
    @SuppressWarnings("rawtypes")
    public MeveoModuleDto moduleToDto(MeveoModule module, User currentUser) throws MeveoApiException {

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
                moduleDto.setLogoPictureFile(ModuleUtil.readModulePicture(module.getProvider().getCode(), module.getLogoPicture()));
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
                        itemDto = customFieldTemplateApi.find(item.getItemCode(), item.getAppliesTo(), currentUser);

                    } else if (item.getItemClass().equals(EntityCustomAction.class.getName())) {
                        itemDto = entityCustomActionApi.find(item.getItemCode(), item.getAppliesTo(), currentUser);

                    } else {

                        ApiService apiService = getApiService(item.getItemClass(), true);
                        itemDto = apiService.find(item.getItemCode(), currentUser);

                    }
                    moduleDto.addModuleItem(itemDto);

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
        }

        return moduleDto;
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
            dto.setOfferTemplate(new OfferTemplateDto(bom.getOfferTemplate(), entityToDtoConverter.getCustomFieldsDTO(bom.getOfferTemplate())));
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
     * @return BusinessAccountModel object DTO representation
     */
    private void businessAccountModelToDto(BusinessAccountModel bom, BusinessAccountModelDto dto) {

        dto.setHierarchyType(bom.getHierarchyType());
    }
}