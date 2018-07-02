package org.meveo.service.catalog.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.GenericModuleService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.meveo.service.script.offer.OfferScriptInterface;
import org.meveo.service.script.product.ProductModelScriptService;
import org.meveo.service.script.product.ProductScriptInterface;
import org.meveo.service.script.service.ServiceModelScriptService;
import org.meveo.service.script.service.ServiceScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessOfferModelService extends GenericModuleService<BusinessOfferModel> {

    @Inject
    private BusinessServiceModelService businessServiceModelService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private CatalogHierarchyBuilderService catalogHierarchyBuilderService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private ServiceModelScriptService serviceModelScriptService;

    @Inject
    private OfferModelScriptService offerModelScriptService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private ProductModelScriptService productModelScriptService;

    @Inject
    private BusinessProductModelService businessProductModelService;

    /**
     * Creates an offer given a BusinessOfferModel.
     * 
     * @param bomParams business offer model parameters.
     * @return offer template
     * @throws BusinessException business exception.
     */
    public OfferTemplate instantiateFromBOM(BOMInstantiationParameters bomParams) throws BusinessException {

        OfferTemplate bomOffer = bomParams.getBusinessOfferModel().getOfferTemplate();

        // 1 create offer
        OfferTemplate newOfferTemplate = new OfferTemplate();

        // check if offer already exists
        if (offerTemplateService.findByCode(bomParams.getCode(), bomParams.getValidFrom(), bomParams.getValidTo()) != null) {
            throw new ValidationException("Offer template with code " + bomParams.getCode() + " for dates " + (bomParams.getValidFrom() == null ? "-" : bomParams.getValidFrom())
                    + " / " + (bomParams.getValidTo() == null ? "-" : bomParams.getValidTo()) + " already exists");
        }

        OfferScriptInterface offerScript = null;
        if (bomParams.getBusinessOfferModel() != null && bomParams.getBusinessOfferModel().getScript() != null) {
            try {
                offerScript = offerModelScriptService.beforeCreateOfferFromBOM(bomParams.getCustomFields(), bomParams.getBusinessOfferModel().getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", bomParams.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        newOfferTemplate.setCode(bomParams.getCode());

        ImageUploadEventHandler<OfferTemplate> offerImageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
        try {
            String imagePath = bomParams.getImagePath();
            if (StringUtils.isBlank(bomParams.getImagePath())) {
                imagePath = bomOffer.getImagePath();
            }
            String newImagePath = offerImageUploadEventHandler.duplicateImage(newOfferTemplate, imagePath);
            newOfferTemplate.setImagePath(newImagePath);
        } catch (IOException e1) {
            log.error("IPIEL: Failed duplicating offer image: {}", e1.getMessage());
        }

        newOfferTemplate.setDescription(bomParams.getOfferDescription());
        newOfferTemplate.setDescriptionI18n(bomParams.getDescriptionI18n());
        newOfferTemplate.setLongDescription(bomParams.getLongDescription());
        newOfferTemplate.setLongDescriptionI18n(bomParams.getLongDescriptionI18n());

        if (StringUtils.isBlank(bomParams.getName())) {
            newOfferTemplate.setName(bomOffer.getName());
        } else {
            newOfferTemplate.setName(bomParams.getName());
        }

        newOfferTemplate.setValidity(bomOffer.getValidity());
        if (bomParams.getValidFrom() != null) {
            if (newOfferTemplate.getValidity() == null) {
                newOfferTemplate.setValidity(new DatePeriod());
            }
            newOfferTemplate.getValidity().setFrom(bomParams.getValidFrom());
        }
        if (bomParams.getValidTo() != null) {
            if (newOfferTemplate.getValidity() == null) {
                newOfferTemplate.setValidity(new DatePeriod());
            }
            newOfferTemplate.getValidity().setTo(bomParams.getValidTo());
        }
        newOfferTemplate.setBusinessOfferModel(bomParams.getBusinessOfferModel());
        if (bomOffer.getAttachments() != null) {
            newOfferTemplate.getAttachments().addAll(bomOffer.getAttachments());
        }
        if (bomParams.getOfferTemplateCategories() != null) {
            newOfferTemplate.getOfferTemplateCategories().addAll(bomParams.getOfferTemplateCategories());
        } else if (bomOffer.getOfferTemplateCategories() != null) {
            newOfferTemplate.getOfferTemplateCategories().addAll(bomOffer.getOfferTemplateCategories());
        }
        if (bomParams.getChannels() != null) {
            newOfferTemplate.getChannels().addAll(bomParams.getChannels());
        }
        if (bomParams.getBams() != null) {
            newOfferTemplate.getBusinessAccountModels().addAll(bomParams.getBams());
        }
        newOfferTemplate.setActive(true);
        if (bomParams.getLifeCycleStatusEnum() != null) {
            newOfferTemplate.setLifeCycleStatus(bomParams.getLifeCycleStatusEnum());
        } else {
            newOfferTemplate.setLifeCycleStatus(LifeCycleStatusEnum.ACTIVE);
        }

        newOfferTemplate.setSubscriptionRenewal(bomOffer.getSubscriptionRenewal());

        if (bomParams.getOfferCfValue() != null) {
            newOfferTemplate.getCfValuesNullSafe().setValues(bomParams.getOfferCfValue());
        } else if (bomOffer.getCfValues() != null) {
            newOfferTemplate.getCfValuesNullSafe().setValues(bomOffer.getCfValues().getValuesByCode());
        }

        offerTemplateService.create(newOfferTemplate);

        String prefix = newOfferTemplate.getId() + "_";

        // 2 create services
        List<OfferServiceTemplate> newOfferServiceTemplates = instantiateServiceTemplate(prefix, bomOffer, newOfferTemplate, bomParams.getServiceCodes(),
            bomParams.getBusinessOfferModel());

        // 3 create product templates
        List<OfferProductTemplate> newOfferProductTemplates = instantiateProductTemplate(prefix, bomOffer, bomParams.getProductCodes(), bomParams.getBusinessOfferModel());

        // add to offer
        for (OfferServiceTemplate newOfferServiceTemplate : newOfferServiceTemplates) {
            newOfferTemplate.addOfferServiceTemplate(newOfferServiceTemplate);
        }
        for (OfferProductTemplate newOfferProductTemplate : newOfferProductTemplates) {
            newOfferTemplate.addOfferProductTemplate(newOfferProductTemplate);
        }

        if (newOfferTemplate.getBusinessOfferModel() != null && newOfferTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.afterCreateOfferFromBOM(newOfferTemplate, bomParams.getCustomFields(), offerScript);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", newOfferTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        return newOfferTemplate;
    }

    private List<OfferProductTemplate> instantiateProductTemplate(String prefix, OfferTemplate offerTemplateInBom, List<ServiceConfigurationDto> productConfigurations,
            BusinessOfferModel businessOfferModel) throws BusinessException {

        List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();

        if (productConfigurations == null || productConfigurations.isEmpty()) {
            return newOfferProductTemplates;
        }

        List<OfferProductTemplate> offerProductTemplatesToInstantiate = offerTemplateInBom.getOfferProductTemplates();

        // Validate that product configurations are valid
        for (ServiceConfigurationDto productConfiguration : productConfigurations) {
            boolean productFound = false;
            String productCode = productConfiguration.getCode();

            for (OfferProductTemplate offerProductTemplate : offerTemplateInBom.getOfferProductTemplates()) {
                ProductTemplate productTemplate = offerProductTemplate.getProductTemplate();
                if (productCode.equals(productTemplate.getCode())) {
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                ProductTemplate pt = productTemplateService.findByCode(productConfiguration.getCode());
                if (pt == null) {
                    throw new BusinessException("Product template with code=" + productConfiguration.getCode() + " not found.");
                }
                OfferProductTemplate opt = new OfferProductTemplate();
                opt.setProductTemplate(pt);
                offerProductTemplatesToInstantiate.add(opt);
            }
        }

        // Instantiate products
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
        for (OfferProductTemplate offerProductTemplate : offerProductTemplatesToInstantiate) {
            ProductTemplate productTemplate = productTemplateService.findById(offerProductTemplate.getProductTemplate().getId());

            boolean productFound = false;
            ServiceConfigurationDto matchedProductConfigurationDto = null;
            for (ServiceConfigurationDto productConfiguration : productConfigurations) {
                String productCode = productConfiguration.getCode();
                if (productCode.equals(productTemplate.getCode())) {
                    matchedProductConfigurationDto = productConfiguration;
                    productFound = true;
                    break;
                }
            }
            if (!productFound) {
                continue;
            }

            // get the BPM from BOM
            BusinessProductModel bpm = null;
            for (MeveoModuleItem item : businessOfferModel.getModuleItems()) {
                if (item.getItemClass().equals(BusinessProductModel.class.getName())) {
                    bpm = businessProductModelService.findByCode(item.getItemCode());
                    if (bpm.getProductTemplate().equals(productTemplate)) {
                        break;
                    }
                }
                bpm = null;
            }

            ProductScriptInterface productScript = null;
            if (bpm != null && bpm.getScript() != null) {
                try {
                    productScript = productModelScriptService.beforeCreate(matchedProductConfigurationDto.getCustomFields(), bpm.getScript().getCode());
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bpm.getScript().getCode(), e);
                }
            }

            OfferProductTemplate newOfferProductTemplate = catalogHierarchyBuilderService.duplicateOfferProductTemplate(offerProductTemplate, prefix,
                matchedProductConfigurationDto, pricePlansInMemory, chargeTemplateInMemory);

            newOfferProductTemplates.add(newOfferProductTemplate);

            if (productScript != null) {
                try {
                    productModelScriptService.afterCreate(newOfferProductTemplate.getProductTemplate(), matchedProductConfigurationDto.getCustomFields(), productScript);
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bpm.getScript().getCode(), e);
                }
            }
        }

        return newOfferProductTemplates;
    }

    private List<OfferServiceTemplate> instantiateServiceTemplate(String prefix, OfferTemplate bomOffer, OfferTemplate newOfferTemplate, List<ServiceConfigurationDto> serviceCodes,
            BusinessOfferModel businessOfferModel) throws BusinessException {
        List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
        // we need this to check in case of non-bsm, non-existing service template
        List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<>(bomOffer.getOfferServiceTemplates());

        if (offerServiceTemplates == null || serviceCodes == null || serviceCodes.isEmpty()) {
            return newOfferServiceTemplates;
        }

        // check if service exists in offer
        for (ServiceConfigurationDto serviceCodeDto : serviceCodes) {
            boolean serviceFound = false;
            String serviceCode = serviceCodeDto.getCode();

            OfferServiceTemplate tempOfferServiceTemplate = null;
            if (serviceCodeDto.isInstantiatedFromBSM()) {
                // no need to check in offer, we initialized a new instance and add to the newly created offer
                // instantiated from bsm
                ServiceTemplate stSource = serviceTemplateService.findByCode(serviceCode);
                if (stSource != null) {
                    // check if exists in bsm or is from offer entity
                    // (meaning not from bsm = non transient)
                    BusinessServiceModel bsm = findBsmFromBom(businessOfferModel, stSource);
                    if (bsm != null) {
                        tempOfferServiceTemplate = new OfferServiceTemplate();
                        tempOfferServiceTemplate.setMandatory(serviceCodeDto.isMandatory());
                        tempOfferServiceTemplate.setOfferTemplate(newOfferTemplate);

                        ServiceTemplate stTarget = new ServiceTemplate();
                        stTarget.setCode(stSource.getCode());
                        stTarget.setDescription(stSource.getDescription());
                        stTarget.setInstantiatedFromBSM(serviceCodeDto.isInstantiatedFromBSM());
                        stTarget.setBusinessServiceModel(bsm);

                        tempOfferServiceTemplate.setServiceTemplate(stTarget);
                        offerServiceTemplates.add(tempOfferServiceTemplate);
                        serviceFound = true;
                    }
                }
            } else {
                for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
                    ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
                    if (serviceCode.equals(serviceTemplate.getCode()) && !serviceCodeDto.isInstantiatedFromBSM()) {
                        serviceFound = true;
                        break;
                    }
                }
            }

            if (!serviceFound) {
                // service is not defined in offer
                throw new BusinessException("Service " + serviceCode + " is not defined in the offer");
            }
        }

        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
        // duplicate the services
        // note that ost now contains st with null id from bsm
        for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(offerServiceTemplate.getServiceTemplate().getCode());

            boolean serviceFound = false;
            ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
            for (ServiceConfigurationDto tempServiceCodeDto : serviceCodes) {
                String serviceConfigurationCode = tempServiceCodeDto.getCode();
                // set match to true when a match is found
                if (serviceConfigurationCode.equals(serviceTemplate.getCode()) && !tempServiceCodeDto.isMatch()) {
                    tempServiceCodeDto.setMatch(true);
                    serviceConfigurationDto = tempServiceCodeDto;
                    serviceFound = true;
                    break;
                }
            }

            if (!serviceFound) {
                continue;
            }

            // get the BSM from BOM
            BusinessServiceModel bsm = findBsmFromBom(businessOfferModel, serviceTemplate);

            ServiceScriptInterface serviceScipt = null;
            if (bsm != null && bsm.getScript() != null) {
                try {
                    serviceScipt = serviceModelScriptService.beforeCreateServiceFromBSM(serviceConfigurationDto.getCustomFields(), bsm.getScript().getCode());
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
                }
            }

            OfferServiceTemplate newOfferServiceTemplate = catalogHierarchyBuilderService.duplicateService(offerServiceTemplate, serviceConfigurationDto, prefix,
                pricePlansInMemory, chargeTemplateInMemory);
            newOfferServiceTemplates.add(newOfferServiceTemplate);

            if (serviceScipt != null) {
                try {
                    serviceModelScriptService.afterCreateServiceFromBSM(newOfferServiceTemplate.getServiceTemplate(), serviceConfigurationDto.getCustomFields(), serviceScipt);
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
                }
            }
        }

        return newOfferServiceTemplates;
    }

    /**
     * @param businessOfferModel business offer model
     * @return list of business service modle.
     */
    public List<BusinessServiceModel> getBusinessServiceModels(BusinessOfferModel businessOfferModel) {
        List<BusinessServiceModel> businessServiceModels = new ArrayList<>();
        for (MeveoModuleItem item : businessOfferModel.getModuleItems()) {
            if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
                businessServiceModels.add(businessServiceModelService.findByCode(item.getItemCode()));
            }
        }

        return businessServiceModels;
    }

    /**
     * @param businessOfferModel business offer model
     * @param serviceTemplate service template
     * @return business service model.
     */
    public BusinessServiceModel findBsmFromBom(BusinessOfferModel businessOfferModel, ServiceTemplate serviceTemplate) {
        for (MeveoModuleItem item : businessOfferModel.getModuleItems()) {
            if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
                BusinessServiceModel bsm = businessServiceModelService.findByCode(item.getItemCode());
                if (bsm.getServiceTemplate().equals(serviceTemplate)) {
                    return bsm;
                }
            }
        }

        return null;
    }

}