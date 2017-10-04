package org.meveo.service.catalog.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.GenericModuleService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.meveo.service.script.product.ProductModelScriptService;
import org.meveo.service.script.service.ServiceModelScriptService;

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
     * @param businessOfferModel
     * @param customFields
     * @param code
     * @param name
     * @param offerDescription
     * @param serviceCodes
     * @return
     * @throws BusinessException
     */
    public OfferTemplate createOfferFromBOM(BusinessOfferModel businessOfferModel, List<CustomFieldDto> customFields, String code, String name, String offerDescription,
            List<ServiceConfigurationDto> serviceCodes, List<ServiceConfigurationDto> productCodes) throws BusinessException {
        return createOfferFromBOM(businessOfferModel, customFields, code, name, offerDescription, serviceCodes, productCodes, null, null, null, LifeCycleStatusEnum.IN_DESIGN, null,
            null, null, null, null, null);
    }

    /**
     * Creates an offer given a BusinessOfferModel.
     * 
     * @param businessOfferModel
     * @param customFields
     * @param code
     * @param name
     * @param offerDescription
     * @param serviceCodes
     * @param channels
     * @param bams
     * @param offerTemplateCategories
     * @param lifeCycleStatusEnum
     * @param map
     * @return
     * @throws BusinessException
     */
    public OfferTemplate createOfferFromBOM(BusinessOfferModel businessOfferModel, List<CustomFieldDto> customFields, String code, String name, String offerDescription,
            List<ServiceConfigurationDto> serviceCodes, List<ServiceConfigurationDto> productCodes, List<Channel> channels, List<BusinessAccountModel> bams,
            List<OfferTemplateCategory> offerTemplateCategories, LifeCycleStatusEnum lifeCycleStatusEnum, String imagePath, Date validFrom, Date validTo,
            Map<String, String> descriptionI18n, String longDescription, Map<String, String> longDescriptionI18n) throws BusinessException {

        OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
        bomOffer = offerTemplateService.refreshOrRetrieve(bomOffer);

        // 1 create offer
        OfferTemplate newOfferTemplate = new OfferTemplate();

        // check if offer already exists
        if (offerTemplateService.findByCode(code, validFrom, validTo) != null) {
            throw new ValidationException(
                "Offer template with code " + code + " for dates " + (validFrom == null ? "-" : validFrom) + " / " + (validTo == null ? "-" : validTo) + " already exists");
        }

        if (businessOfferModel != null && businessOfferModel.getScript() != null) {
            try {
                offerModelScriptService.beforeCreateOfferFromBOM(customFields, businessOfferModel.getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", businessOfferModel.getScript().getCode(), e);
            }
        }

        newOfferTemplate.setCode(code);

        ImageUploadEventHandler<OfferTemplate> offerImageUploadEventHandler = new ImageUploadEventHandler<>(appProvider);
        try {
            if (StringUtils.isBlank(imagePath)) {
                imagePath = bomOffer.getImagePath();
            }
            String newImagePath = offerImageUploadEventHandler.duplicateImage(newOfferTemplate, imagePath);
            newOfferTemplate.setImagePath(newImagePath);
        } catch (IOException e1) {
            log.error("IPIEL: Failed duplicating offer image: {}", e1.getMessage());
        }

        newOfferTemplate.setDescription(offerDescription);
        newOfferTemplate.setDescriptionI18n(descriptionI18n);
        newOfferTemplate.setLongDescription(longDescription);
        newOfferTemplate.setLongDescriptionI18n(longDescriptionI18n);

        if (StringUtils.isBlank(name)) {
            newOfferTemplate.setName(bomOffer.getName());
        } else {
            newOfferTemplate.setName(name);
        }

		newOfferTemplate.setValidity(bomOffer.getValidity());
        if (validFrom != null) {
		    if (newOfferTemplate.getValidity()==null){
		        newOfferTemplate.setValidity(new DatePeriod());
		    }
            newOfferTemplate.getValidity().setFrom(validFrom);
        }
        if (validTo != null) {
		    if (newOfferTemplate.getValidity()==null){
                newOfferTemplate.setValidity(new DatePeriod());
            }
            newOfferTemplate.getValidity().setTo(validTo);
        }
        newOfferTemplate.setBusinessOfferModel(businessOfferModel);
        if (bomOffer.getAttachments() != null) {
            newOfferTemplate.getAttachments().addAll(bomOffer.getAttachments());
        }
        if (offerTemplateCategories != null) {
            newOfferTemplate.getOfferTemplateCategories().addAll(offerTemplateCategories);
        }
        if (channels != null) {
            newOfferTemplate.getChannels().addAll(channels);
        }
        if (bams != null) {
            newOfferTemplate.getBusinessAccountModels().addAll(bams);
        }
        newOfferTemplate.setActive(true);
        newOfferTemplate.setLifeCycleStatus(lifeCycleStatusEnum);

        offerTemplateService.create(newOfferTemplate);
        newOfferTemplate.setCfValues(bomOffer.getCfValues());
        

        String prefix = newOfferTemplate.getId() + "_";

        // 2 create services
        List<OfferServiceTemplate> newOfferServiceTemplates = getOfferServiceTemplate(prefix, bomOffer, serviceCodes, businessOfferModel);

        // 3 create product templates
        List<OfferProductTemplate> newOfferProductTemplates = getOfferProductTemplate(prefix, bomOffer, productCodes, businessOfferModel);

        // add to offer
        for (OfferServiceTemplate newOfferServiceTemplate : newOfferServiceTemplates) {
            newOfferTemplate.addOfferServiceTemplate(newOfferServiceTemplate);
        }
        for (OfferProductTemplate newOfferProductTemplate : newOfferProductTemplates) {
            newOfferTemplate.addOfferProductTemplate(newOfferProductTemplate);
        }

        offerTemplateService.update(newOfferTemplate);

        if (newOfferTemplate.getBusinessOfferModel() != null && newOfferTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.afterCreateOfferFromBOM(newOfferTemplate, customFields, newOfferTemplate.getBusinessOfferModel().getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", newOfferTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        return newOfferTemplate;
    }

    private List<OfferProductTemplate> getOfferProductTemplate(String prefix, OfferTemplate offerTemplateInBom, List<ServiceConfigurationDto> productConfigurations,
            BusinessOfferModel businessOfferModel) throws BusinessException {

        List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();

        if (offerTemplateInBom.getOfferProductTemplates() == null || offerTemplateInBom.getOfferProductTemplates().isEmpty() || productConfigurations == null
                || productConfigurations.isEmpty()) {
            return newOfferProductTemplates;
        }

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
                throw new BusinessException("ProductTemplate with code=" + productCode + " is not defined in the offer");
            }
        }

        // Instantiate products
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
        for (OfferProductTemplate offerProductTemplate : offerTemplateInBom.getOfferProductTemplates()) {
            ProductTemplate productTemplate = productTemplateService.findById(offerProductTemplate.getProductTemplate().getId());

            boolean productFound = false;
            ServiceConfigurationDto matchedProductConfigurationDto = null;
            for (ServiceConfigurationDto productConfiguration : productConfigurations) {
                String serviceCode = productConfiguration.getCode();
                if (serviceCode.equals(productTemplate.getCode())) {
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
            }

            if (bpm != null && bpm.getScript() != null) {
                try {
                    productModelScriptService.beforeCreateServiceFromBSM(matchedProductConfigurationDto.getCustomFields(), bpm.getScript().getCode());
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bpm.getScript().getCode(), e);
                }
            }

            OfferProductTemplate newOfferProductTemplate = catalogHierarchyBuilderService.duplicateProduct(offerProductTemplate, prefix, matchedProductConfigurationDto,
                pricePlansInMemory, chargeTemplateInMemory);

            newOfferProductTemplates.add(newOfferProductTemplate);

            if (bpm != null && bpm.getScript() != null) {
                try {
                    productModelScriptService.afterCreateServiceFromBSM(newOfferProductTemplate.getProductTemplate(), matchedProductConfigurationDto.getCustomFields(),
                        bpm.getScript().getCode());
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bpm.getScript().getCode(), e);
                }
            }
        }

        return newOfferProductTemplates;
    }

    private List<OfferServiceTemplate> getOfferServiceTemplate(String prefix, OfferTemplate bomOffer, List<ServiceConfigurationDto> serviceCodes,
            BusinessOfferModel businessOfferModel) throws BusinessException {
        List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();

        if (bomOffer.getOfferServiceTemplates() == null || bomOffer.getOfferServiceTemplates().isEmpty() || serviceCodes == null || serviceCodes.isEmpty()) {
            return newOfferServiceTemplates;
        }

        for (ServiceConfigurationDto serviceCodeDto : serviceCodes) {
            boolean serviceFound = false;
            String serviceCode = serviceCodeDto.getCode();

            for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
                ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
                if (serviceCode.equals(serviceTemplate.getCode())) {
                    serviceFound = true;
                    break;
                }
            }

            if (!serviceFound) {
                throw new BusinessException("Service " + serviceCode + " is not defined in the offer");
            }
        }

        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
        for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(offerServiceTemplate.getServiceTemplate().getCode());

            boolean serviceFound = false;
            ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
            for (ServiceConfigurationDto tempServiceCodeDto : serviceCodes) {
                String serviceCode = tempServiceCodeDto.getCode();
                if (serviceCode.equals(serviceTemplate.getCode())) {
                    serviceConfigurationDto = tempServiceCodeDto;
                    serviceFound = true;
                    break;
                }
            }
            if (!serviceFound) {
                continue;
            }

            // get the BSM from BOM
            BusinessServiceModel bsm = null;
            for (MeveoModuleItem item : businessOfferModel.getModuleItems()) {
                if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
                    bsm = businessServiceModelService.findByCode(item.getItemCode());
                    if (bsm.getServiceTemplate().equals(serviceTemplate)) {
                        break;
                    }
                }
            }

            if (bsm != null && bsm.getScript() != null) {
                try {
                    serviceModelScriptService.beforeCreateServiceFromBSM(serviceConfigurationDto.getCustomFields(), bsm.getScript().getCode());
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
                }
            }

            OfferServiceTemplate newOfferServiceTemplate = catalogHierarchyBuilderService.duplicateService(offerServiceTemplate, serviceConfigurationDto, prefix,
                pricePlansInMemory, chargeTemplateInMemory);
            newOfferServiceTemplates.add(newOfferServiceTemplate);

            if (bsm != null && bsm.getScript() != null) {
                try {
                    serviceModelScriptService.afterCreateServiceFromBSM(newOfferServiceTemplate.getServiceTemplate(), serviceConfigurationDto.getCustomFields(),
                        bsm.getScript().getCode());
                } catch (BusinessException e) {
                    log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
                }
            }
        }

        return newOfferServiceTemplates;
    }

    @SuppressWarnings("unchecked")
    public List<BusinessOfferModel> listInstalled() {
        QueryBuilder qb = new QueryBuilder(BusinessOfferModel.class, "b", null);
        qb.startOrClause();
        qb.addCriterion("installed", "=", true, true);
        qb.addSql("moduleSource is null");
        qb.endOrClause();

        try {
            return (List<BusinessOfferModel>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}