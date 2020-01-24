package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.BSMConfigurationDto;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BpmProductDto;
import org.meveo.api.dto.catalog.BsmServiceDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.service.catalog.impl.BOMInstantiationParameters;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessProductModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@Stateless
public class BusinessOfferApi extends BaseApi {

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    @Inject
    private BusinessServiceModelService businessServiceModelService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private BusinessProductModelService businessProductModelService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ProductTemplateService productTemplateService;

    public Long instantiateBOM(BomOfferDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getBomCode())) {
            missingParameters.add("bomCode");
        }

        handleMissingParametersAndValidate(postData);

        // find bom
        BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getBomCode());
        if (businessOfferModel == null) {
            throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
        }

        // get the offer from bom
        OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
        if (bomOffer == null) {
            throw new MeveoApiException("No offer template attached");
        }

        if ((bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().isEmpty()) && (bomOffer.getOfferProductTemplates() == null || bomOffer
                .getOfferProductTemplates().isEmpty())) {
            log.warn("No service or product template attached");
        }

        // process bsm
        List<ServiceConfigurationDto> serviceConfigurationDtoFromBSM = getServiceConfiguration(postData.getBusinessServiceModels());
        if (!serviceConfigurationDtoFromBSM.isEmpty()) {
            postData.getServicesToActivate().addAll(serviceConfigurationDtoFromBSM);
        }

        // create newOfferTemplate
        OfferTemplate newOfferTemplate = createNewOfferTemplate(postData, businessOfferModel);

        if (CollectionUtils.isNotEmpty(postData.getServicesToActivate())) {
            postData.getServicesToActivate().stream().map(p -> {
                p.setMatch(false);
                return p;
            }).collect(Collectors.toList());
        }

        try {
            saveImage(newOfferTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        for (OfferServiceTemplate ost : newOfferTemplate.getOfferServiceTemplates()) {
            ServiceTemplate serviceTemplate = ost.getServiceTemplate();
            for (ServiceConfigurationDto serviceConfigurationDto : postData.getServicesToActivate()) {

                String serviceTemplateCode = constructServiceTemplateCode(newOfferTemplate, ost, serviceTemplate, serviceConfigurationDto);

                if (serviceTemplateCode.equals(serviceTemplate.getCode())) {
                    try {
                        saveImage(serviceTemplate, serviceConfigurationDto.getImagePath(), serviceConfigurationDto.getImageBase64());
                    } catch (IOException e1) {
                        log.error("Invalid image data={}", e1.getMessage());
                        throw new InvalidImageData();
                    }
                }
            }
        }

        // populate service custom fields
        for (OfferServiceTemplate ost : newOfferTemplate.getOfferServiceTemplates()) {
            ServiceTemplate serviceTemplate = ost.getServiceTemplate();

            for (ServiceConfigurationDto serviceConfigurationDto : postData.getServicesToActivate()) {

                // Caution the service code also must match that of BusinessOfferModelService.createOfferFromBOM
                String serviceTemplateCode = constructServiceTemplateCode(newOfferTemplate, ost, serviceTemplate, serviceConfigurationDto);

                // #4865 - [bom] Service CF values should be copied when creating offer from BOM
                if (serviceTemplateCode.equals(serviceTemplate.getCode())) {
                    ServiceTemplate oldService = serviceTemplateService.findByCode(serviceConfigurationDto.getCode());
                    CustomFieldValues customFieldValues = oldService.getCfValuesNullSafe();
                    Map<String, List<CustomFieldValue>> cfValues = customFieldValues.getValuesByCode();

                    if (!cfValues.isEmpty()) {
                        CustomFieldsDto cfs = entityToDtoConverter.getCustomFieldsDTO(oldService, cfValues, CustomFieldInheritanceEnum.INHERIT_NONE);
                        addNoExistingCFToNewList(serviceConfigurationDto, cfs);
                    }
                }

                if (serviceTemplateCode.equals(serviceTemplate.getCode()) && serviceConfigurationDto.getCustomFields() != null && !serviceConfigurationDto.isMatch()) {
                    try {
                        CustomFieldsDto cfsDto = new CustomFieldsDto();
                        cfsDto.setCustomField(serviceConfigurationDto.getCustomFields());
                        // to fix a case when we instantiate a BSM multiple times in the same offer with CF value override,
                        populateCFsWithClonedServiceTemplate(cfsDto, serviceTemplate, true);
                        serviceTemplate = serviceTemplateService.update(serviceTemplate);
                        ost.setServiceTemplate(serviceTemplate);

                    } catch (MissingParameterException | InvalidParameterException e) {
                        log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                        throw e;
                    } catch (Exception e) {
                        log.error("Failed to associate custom field instance to an entity", e);
                        throw new BusinessException(e);
                    }
                    serviceConfigurationDto.setMatch(true);
                    break;
                }
            }
        }

        // populate product custom fields
        for (OfferProductTemplate opt : newOfferTemplate.getOfferProductTemplates()) {
            ProductTemplate productTemplate = opt.getProductTemplate();

            for (ServiceConfigurationDto productCodeDto : postData.getProductsToActivate()) {
                // Caution the productCode building algo must match that of
                // BusinessOfferModelService.createOfferFromBOM
                String productCode = opt.getOfferTemplate().getId() + "_" + productCodeDto.getCode();

                // #4941 - [bom] Product CF with no override should be copied from offer template's product
                if (productCode.equals(productTemplate.getCode())) {
                    ProductTemplate oldProduct = productTemplateService.findByCode(productCodeDto.getCode());
                    CustomFieldValues customFieldValues = oldProduct.getCfValuesNullSafe();
                    Map<String, List<CustomFieldValue>> cfValues = customFieldValues.getValuesByCode();

                    if (!cfValues.isEmpty()) {
                        CustomFieldsDto cfs = entityToDtoConverter.getCustomFieldsDTO(oldProduct, cfValues, CustomFieldInheritanceEnum.INHERIT_NONE);
                        addNoExistingCFToNewList(productCodeDto, cfs);
                    }
                }
                if (productCode.equals(productTemplate.getCode())) {
                    if (productCodeDto.getCustomFields() != null) {
                        try {
                            CustomFieldsDto cfsDto = new CustomFieldsDto();
                            cfsDto.setCustomField(productCodeDto.getCustomFields());
                            // to fix a case when we instantiate a BSM multiple times in the same offer with CF value override,
                            populateCFsWithClonedProductTemplate(cfsDto, productTemplate, true);

                            productTemplate = productTemplateService.update(productTemplate);
                            opt.setProductTemplate(productTemplate);

                        } catch (MissingParameterException e) {
                            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                            throw e;
                        } catch (Exception e) {
                            log.error("Failed to associate custom field instance to an entity", e);
                            throw e;
                        }
                        break;
                    }
                }
            }
        }

        // populate offer custom fields
        if (newOfferTemplate != null && postData.getCustomFields() != null) {
            try {
                CustomFieldsDto cfsDto = new CustomFieldsDto();
                cfsDto.setCustomField(postData.getCustomFields());
                // to fix a case when we instantiate a BSM multiple times in the same offer with CF value override,
                populateCFsWithClonedOfferTemplate(cfsDto, newOfferTemplate, true);
            } catch (MissingParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new BusinessException("Failed to associate custom field instance to an entity", e);
            }
        }

        newOfferTemplate = offerTemplateService.update(newOfferTemplate);

        return newOfferTemplate.getId();
    }

    private String constructServiceTemplateCode(OfferTemplate newOfferTemplate, OfferServiceTemplate ost, ServiceTemplate serviceTemplate,
            ServiceConfigurationDto serviceConfigurationDto) {

        String serviceTemplateCode = ost.getOfferTemplate().getId() + "_" + serviceConfigurationDto.getCode();
        Integer serviceConfItemIndex = serviceConfigurationDto.getItemIndex();

        if (serviceConfigurationDto.isInstantiatedFromBSM()) {
            if (serviceConfItemIndex != null) {
                serviceTemplateCode = newOfferTemplate.getId() + "_" + serviceConfItemIndex + "_" + serviceConfigurationDto.getCode();
            } else {
                serviceTemplateCode = newOfferTemplate.getId() + "_" + serviceTemplate.getId() + "_" + serviceConfigurationDto.getCode();
            }
        }
        return serviceTemplateCode;
    }

    private OfferTemplate createNewOfferTemplate(BomOfferDto postData, BusinessOfferModel businessOfferModel) throws MeveoApiException {
        try {
            OfferTemplate newOfferTemplate;
            BOMInstantiationParameters bomParams = new BOMInstantiationParameters();
            bomParams.setBusinessOfferModel(businessOfferModel);
            bomParams.setCustomFields(postData.getCustomFields());
            bomParams.setCode(postData.getCode());
            bomParams.setName(postData.getName());
            bomParams.setOfferDescription(postData.getDescription());
            bomParams.setServiceCodes(postData.getServicesToActivate());
            bomParams.setProductCodes(postData.getProductsToActivate());
            bomParams.setLifeCycleStatusEnum(postData.getLifeCycleStatusEnum());
            if (postData.getOfferTemplateCategories() != null) {
                List<OfferTemplateCategory> offerTemplateCategories = new ArrayList<OfferTemplateCategory>();
                for (OfferTemplateCategoryDto offerTemplateCategoryDto : postData.getOfferTemplateCategories()) {
                    OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(offerTemplateCategoryDto.getCode());
                    if (offerTemplateCategory == null) {
                        throw new EntityDoesNotExistsException(OfferTemplateCategory.class, offerTemplateCategoryDto.getCode());
                    }
                    offerTemplateCategories.add(offerTemplateCategory);
                }
                bomParams.setOfferTemplateCategories(offerTemplateCategories);
            }
            newOfferTemplate = businessOfferModelService.instantiateFromBOM(bomParams);
            return newOfferTemplate;
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }

    private List<ServiceConfigurationDto> getServiceConfiguration(List<BSMConfigurationDto> bsmsConfig) throws MeveoApiException {
        List<ServiceConfigurationDto> result = new ArrayList<>();

        if (bsmsConfig != null && !bsmsConfig.isEmpty()) {
            int itemIndex = 1;
            for (BSMConfigurationDto bsmConfig : bsmsConfig) {
                BusinessServiceModel bsm = businessServiceModelService.findByCode(bsmConfig.getCode());
                if (bsm == null) {
                    throw new EntityDoesNotExistsException(BusinessServiceModel.class, bsmConfig.getCode());
                }
                ServiceConfigurationDto serviceConfigurationDto = bsmConfig.getServiceConfiguration();
                if (bsm.getServiceTemplate() == null || !bsm.getServiceTemplate().getCode().equals(serviceConfigurationDto.getCode())) {
                    throw new MeveoApiException("Service template with code=" + serviceConfigurationDto.getCode() + " is not linked to BSM with code=" + bsm.getCode());
                }
                serviceConfigurationDto.setInstantiatedFromBSM(true);
                serviceConfigurationDto.setItemIndex(itemIndex++);
                result.add(serviceConfigurationDto);
            }
        }

        return result;
    }

    /**
     * @param postData business service model service.
     * @return id of new service template
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Long instantiateBSM(BsmServiceDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getBsmCode())) {
            missingParameters.add("bsmCode");
        }
        if (StringUtils.isBlank(postData.getPrefix())) {
            missingParameters.add("prefix");
        }

        handleMissingParametersAndValidate(postData);

        BusinessServiceModel bsm = businessServiceModelService.findByCode(postData.getBsmCode());
        if (bsm == null) {
            throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getBsmCode());
        }
        ServiceTemplate newServiceTemplateCreated = businessServiceModelService.instantiateBSM(bsm, postData.getPrefix(), postData.getCustomFields());

        try {
            saveImage(newServiceTemplateCreated, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        try {
            CustomFieldsDto cfsDto = new CustomFieldsDto();
            cfsDto.setCustomField(postData.getCustomFields());
            populateCustomFields(cfsDto, newServiceTemplateCreated, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        return newServiceTemplateCreated.getId();
    }

    /**
     * Instantiates a product from a given BusinessProductModel.
     *
     * @param postData business product model product
     * @return product template's id
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Long instantiateBPM(BpmProductDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getBpmCode())) {
            missingParameters.add("bpmCode");
        }

        handleMissingParametersAndValidate(postData);

        BusinessProductModel bpm = businessProductModelService.findByCode(postData.getBpmCode());
        if (bpm == null) {
            throw new EntityDoesNotExistsException(BusinessProductModel.class, postData.getBpmCode());
        }

        ProductTemplate newProducTemplate = businessProductModelService.instantiateBPM(postData.getPrefix(), null, bpm, postData.getCustomFields());

        // sets the custom field values
        try {
            CustomFieldsDto cfsDto = new CustomFieldsDto();
            cfsDto.setCustomField(postData.getCustomFields());
            populateCustomFields(cfsDto, newProducTemplate, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        return newProducTemplate.getId();
    }

    /**
     * Special populate custom field values from service template DTO.
     * <p>
     * To avoid overridden Cf values. see #4865, #4928, #4929
     *
     * @param customFieldsDto Custom field values
     * @param entity          Entity
     * @param isNewEntity     Is entity a newly saved entity
     * @throws MeveoApiException meveo api exception.
     */
    private ServiceTemplate populateCFsWithClonedServiceTemplate(CustomFieldsDto customFieldsDto, ServiceTemplate entity, boolean isNewEntity) throws MeveoApiException {
        try {
            ServiceTemplate temp = (ServiceTemplate) BeanUtils.cloneBean(entity);
            temp.setCfValues(null);
            temp.setUuid(UUID.randomUUID().toString());
            temp.setCfAccumulatedValues(null);

            populateCustomFields(customFieldsDto, temp, isNewEntity);
            entity.setCfValues(temp.getCfValues());
            entity.setCfAccumulatedValues(temp.getCfValues());
        } catch (Exception e) {
            log.error("Error when cloning object:" + entity, e);
        }
        return entity;
    }

    /**
     * Special populate custom field values from offer template DTO.
     * <p>
     * To avoid overridden Cf values. see #4865, #4928, #4929
     *
     * @param customFieldsDto Custom field values
     * @param entity          Entity
     * @param isNewEntity     Is entity a newly saved entity
     * @throws MeveoApiException meveo api exception.
     */
    private void populateCFsWithClonedOfferTemplate(CustomFieldsDto customFieldsDto, OfferTemplate entity, boolean isNewEntity) throws MeveoApiException {
        try {
            OfferTemplate temp = (OfferTemplate) BeanUtils.cloneBean(entity);
            temp.setUuid(UUID.randomUUID().toString());
            temp.setCfValues(null);
            temp.setCfAccumulatedValues(null);

            populateCustomFields(customFieldsDto, temp, true);
            entity.setCfValues(temp.getCfValues());
            entity.setCfAccumulatedValues(temp.getCfValues());
        } catch (Exception e) {
            log.error("Error setting CF values to the cloning object:" + entity, e);
        }
    }

    /**
     * Special populate custom field values from product template DTO.
     * <p>
     * To avoid overridden Cf values. see #4865, #4929
     *
     * @param customFieldsDto Custom field values
     * @param entity          Entity
     * @param isNewEntity     Is entity a newly saved entity
     * @throws MeveoApiException meveo api exception.
     */
    private ProductTemplate populateCFsWithClonedProductTemplate(CustomFieldsDto customFieldsDto, ProductTemplate entity, boolean isNewEntity) throws MeveoApiException {
        try {
            ProductTemplate temp = (ProductTemplate) BeanUtils.cloneBean(entity);
            temp.setUuid(UUID.randomUUID().toString());
            temp.setCfValues(null);
            temp.setCfAccumulatedValues(null);

            populateCustomFields(customFieldsDto, temp, true);
            entity.setCfValues(temp.getCfValues());
            entity.setCfAccumulatedValues(temp.getCfValues());
        } catch (Exception e) {
            log.error("Error when cloning object" + entity, e);
        }
        return entity;
    }

    /***
     * skip copy Cfs from old service if it is overridden
     * @param serviceConfigurationDto a new service
     * @param cfs old cfs
     */
    private void addNoExistingCFToNewList(ServiceConfigurationDto serviceConfigurationDto, CustomFieldsDto cfs) {
        List<CustomFieldDto> newCustomFields = Optional.ofNullable(serviceConfigurationDto.getCustomFields()).orElse(new ArrayList<>());
        List<CustomFieldDto> oldCustomFields = Optional.ofNullable(cfs.getCustomField()).orElse(new ArrayList<>());
        List<CustomFieldDto> combinedList = new ArrayList<>(newCustomFields);
        // add CFs from old service
        for (CustomFieldDto customField : oldCustomFields) {
            boolean found = false;
            for (CustomFieldDto field : newCustomFields) {
                if (field.getCode().equalsIgnoreCase(customField.getCode())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                combinedList.add(customField);
            }
        }
        serviceConfigurationDto.setCustomFields(combinedList);
    }
}
