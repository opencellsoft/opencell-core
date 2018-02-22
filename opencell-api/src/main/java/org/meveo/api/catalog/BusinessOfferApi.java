package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.BSMConfigurationDto;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BpmProductDto;
import org.meveo.api.dto.catalog.BsmServiceDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
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
import org.meveo.service.catalog.impl.BOMInstantiationParameters;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessProductModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;

@Stateless
public class BusinessOfferApi extends BaseApi {

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    @Inject
    private BusinessServiceModelService businessServiceModelService;
    
    @Inject
    private OfferTemplateService offerTemplateService;
    
    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;
    
    @Inject
    private BusinessProductModelService businessProductModelService;

    public Long instantiateBOM(BomOfferDto postData) throws MeveoApiException {

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

        if ((bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().isEmpty())
                && (bomOffer.getOfferProductTemplates() == null || bomOffer.getOfferProductTemplates().isEmpty())) {
            throw new MeveoApiException("No service or product template attached");
        }
        
        // process bsm
        List<ServiceConfigurationDto> serviceConfigurationDtoFromBSM = getServiceConfiguration(postData.getBusinessServiceModels());
        if (!serviceConfigurationDtoFromBSM.isEmpty()) {
            postData.getServicesToActivate().addAll(serviceConfigurationDtoFromBSM);
        }

        OfferTemplate newOfferTemplate = null;
        try {
            BOMInstantiationParameters bomParams = new BOMInstantiationParameters();
            bomParams.setBusinessOfferModel(businessOfferModel);
            bomParams.setCustomFields(postData.getCustomFields());
            bomParams.setCode(postData.getCode());
            bomParams.setName(postData.getName());
            bomParams.setOfferDescription(postData.getDescription());
            bomParams.setServiceCodes(postData.getServicesToActivate());
            bomParams.setProductCodes(postData.getProductsToActivate());
            bomParams.setLifeCycleStatusEnum(postData.getLifeCycleStatusEnum());
            if(postData.getOfferTemplateCategories() != null) {
                List<OfferTemplateCategory> offerTemplateCategories = new ArrayList<OfferTemplateCategory>();
                for(OfferTemplateCategoryDto offerTemplateCategoryDto :  postData.getOfferTemplateCategories()) {
                    OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(offerTemplateCategoryDto.getCode());
                    if (offerTemplateCategory == null) {
                        throw new EntityDoesNotExistsException(OfferTemplateCategory.class, offerTemplateCategoryDto.getCode());
                    } 
                    offerTemplateCategories.add(offerTemplateCategory);
                }
                bomParams.setOfferTemplateCategories(offerTemplateCategories);
            }
            newOfferTemplate = businessOfferModelService.instantiateFromBOM(bomParams);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }

        if (postData.getServicesToActivate() != null && !postData.getServicesToActivate().isEmpty()) {
            postData.getServicesToActivate().stream().map(p -> {
                p.setMatch(false);
                return p;
            }).collect(Collectors.toList());
        }

        // populate service custom fields
        for (OfferServiceTemplate ost : newOfferTemplate.getOfferServiceTemplates()) {
            ServiceTemplate serviceTemplate = ost.getServiceTemplate();

            for (ServiceConfigurationDto serviceConfigurationDto : postData.getServicesToActivate()) {
                // Caution the servicode building algo must match that of
                // BusinessOfferModelService.createOfferFromBOM
                String serviceTemplateCode = ost.getOfferTemplate().getId() + "_" + serviceConfigurationDto.getCode();
                if (serviceConfigurationDto.isInstantiatedFromBSM()) {
                    serviceTemplateCode = newOfferTemplate.getId() + "_" + serviceTemplate.getId() + "_" + serviceConfigurationDto.getCode();
                }
                if (serviceTemplateCode.equals(serviceTemplate.getCode()) && serviceConfigurationDto.getCustomFields() != null && !serviceConfigurationDto.isMatch()) {
                    try {
                        CustomFieldsDto cfsDto = new CustomFieldsDto();
                        cfsDto.setCustomField(serviceConfigurationDto.getCustomFields());
                        populateCustomFields(cfsDto, serviceTemplate, true);
                    } catch (MissingParameterException e) {
                        log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                        throw e;
                    } catch (Exception e) {
                        log.error("Failed to associate custom field instance to an entity", e);
                        throw e;
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
                if (productCode.equals(productTemplate.getCode())) {
                    if (productCodeDto.getCustomFields() != null) {
                        try {
                            CustomFieldsDto cfsDto = new CustomFieldsDto();
                            cfsDto.setCustomField(productCodeDto.getCustomFields());
                            populateCustomFields(cfsDto, productTemplate, true);
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
                populateCustomFields(cfsDto, newOfferTemplate, true);
            } catch (MissingParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
            }
        }

        return newOfferTemplate.getId();
    }

    private List<ServiceConfigurationDto> getServiceConfiguration(List<BSMConfigurationDto> bsmsConfig) throws MeveoApiException {
        List<ServiceConfigurationDto> result = new ArrayList<>();

        if (bsmsConfig != null && !bsmsConfig.isEmpty()) {
            for (BSMConfigurationDto bsmConfig : bsmsConfig) {
                BusinessServiceModel bsm = businessServiceModelService.findByCode(bsmConfig.getCode());
                if (bsm == null) {
                    throw new EntityDoesNotExistsException(BusinessServiceModel.class, bsmConfig.getCode());
                }

                if (!bsm.getServiceTemplate().getCode().equals(bsmConfig.getServiceConfiguration().getCode())) {
                    throw new MeveoApiException("Service template with code=" + bsmConfig.getServiceConfiguration().getCode() + " is not linked to BSM with code=" + bsm.getCode());
                }
                bsmConfig.getServiceConfiguration().setInstantiatedFromBSM(true);

                result.add(bsmConfig.getServiceConfiguration());
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
            CustomFieldsDto cfsDto = new CustomFieldsDto();
            cfsDto.setCustomField(postData.getCustomFields());
            populateCustomFields(cfsDto, newServiceTemplateCreated, true);
        } catch (MissingParameterException e) {
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
     * @param postData business product model product
     * @return product template's id
     * @throws MeveoApiException  meveo api exception
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
            
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
            
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        return newProducTemplate.getId();
    }

}
