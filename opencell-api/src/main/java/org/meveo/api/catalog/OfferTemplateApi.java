package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudVersionedApi;
import org.meveo.api.dto.catalog.OfferProductTemplateDto;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferTemplateApi extends BaseCrudVersionedApi<OfferTemplate, OfferTemplateDto> {

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private ProductTemplateService productTemplateService;

    private ParamBean paramBean = ParamBean.getInstance();

    public OfferTemplate create(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            postData.setName(postData.getCode());
        }

        handleMissingParameters();

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(), null, true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException("An offer, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBean.getDateFormat())
                    + ", already exists. Please change the validity dates of an existing offer first.");
        }

        if (offerTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo()) != null) {
            throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode() + " / " + postData.getValidFrom() + " / " + postData.getValidTo());
        }

        OfferTemplate offerTemplate = new OfferTemplate();
        populateFromDto(offerTemplate, postData);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplate, true);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplateService.create(offerTemplate);

        return offerTemplate;
    }

    public OfferTemplate update(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            postData.setName(postData.getCode());
        }
        handleMissingParametersAndValidate(postData);

        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo());
        if (offerTemplate == null) {
            String datePattern = paramBean.getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode() + " / " + DateUtils.formatDateWithPattern(postData.getValidFrom(), datePattern) + " / "
                    + DateUtils.formatDateWithPattern(postData.getValidTo(), datePattern));
        }

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(), offerTemplate.getId(),
            true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException("An offer, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBean.getDateFormat())
                    + ", already exists. Please change the validity dates of an existing offer first.");
        }

        populateFromDto(offerTemplate, postData);
        offerTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplate, false);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplate = offerTemplateService.update(offerTemplate);

        return offerTemplate;
    }

    private void populateFromDto(OfferTemplate offerTemplate, OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        BusinessOfferModel businessOffer = null;
        if (!StringUtils.isBlank(postData.getBomCode())) {
            businessOffer = businessOfferModelService.findByCode(postData.getBomCode());
            if (businessOffer == null) {
                throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getBomCode());
            }
        }

        if (!StringUtils.isBlank(postData.getOfferTemplateCategoryCode())) {
            OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getOfferTemplateCategoryCode());
            if (offerTemplateCategory == null) {
                throw new EntityDoesNotExistsException(OfferTemplateCategory.class, postData.getOfferTemplateCategoryCode());
            }
            offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
        }

        if (postData.getOfferTemplateCategories() != null) {
            offerTemplate.getOfferTemplateCategories().clear();
            for (OfferTemplateCategoryDto categoryDto : postData.getOfferTemplateCategories()) {
                OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(categoryDto.getCode());
                if (offerTemplateCategory == null) {
                    throw new EntityDoesNotExistsException(OfferTemplateCategory.class, categoryDto.getCode());
                }
                offerTemplate.addOfferTemplateCategory(offerTemplateCategory);
            }
        }

        offerTemplate.setBusinessOfferModel(businessOffer);
        offerTemplate.setCode(postData.getCode());
        offerTemplate.setDescription(postData.getDescription());
        offerTemplate.setName(postData.getName());
        offerTemplate.setLongDescription(postData.getLongDescription());
        offerTemplate.setDisabled(postData.isDisabled());
        offerTemplate.setValidity(new DatePeriod(postData.getValidFrom(), postData.getValidTo()));
        if (postData.getLanguageDescriptions() != null) {
            offerTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), offerTemplate.getDescriptionI18n()));
        }
        if (postData.getLongDescriptionsTranslated() != null) {
            offerTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), offerTemplate.getLongDescriptionI18n()));
        }

        try {
            saveImage(offerTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        // check service templates
        processOfferServiceTemplates(postData, offerTemplate);

        // check offer product templates
        processOfferProductTemplates(postData, offerTemplate);
    }

    private void processOfferServiceTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
        List<OfferServiceTemplateDto> offerServiceTemplateDtos = postData.getOfferServiceTemplates();
        boolean hasOfferServiceTemplateDtos = offerServiceTemplateDtos != null && !offerServiceTemplateDtos.isEmpty();

        List<OfferServiceTemplate> existingServiceTemplates = offerTemplate.getOfferServiceTemplates();
        boolean hasExistingServiceTemplates = existingServiceTemplates != null && !existingServiceTemplates.isEmpty();

        if (hasOfferServiceTemplateDtos) {
            List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
            OfferServiceTemplate offerServiceTemplate = null;
            for (OfferServiceTemplateDto offerServiceTemplateDto : offerServiceTemplateDtos) {
                offerServiceTemplate = getOfferServiceTemplatesFromDto(offerServiceTemplateDto);
                offerServiceTemplate.setOfferTemplate(offerTemplate);
                newOfferServiceTemplates.add(offerServiceTemplate);
            }

            if (!hasExistingServiceTemplates) {
                offerTemplate.getOfferServiceTemplates().addAll(newOfferServiceTemplates);

            } else {

                // Keep only services that repeat
                existingServiceTemplates.retainAll(newOfferServiceTemplates);

                // Update existing services or add new ones
                for (OfferServiceTemplate ostNew : newOfferServiceTemplates) {

                    int index = existingServiceTemplates.indexOf(ostNew);
                    if (index >= 0) {
                        OfferServiceTemplate ostOld = existingServiceTemplates.get(index);
                        ostOld.update(ostNew);

                    } else {
                        existingServiceTemplates.add(ostNew);
                    }
                }
            }

        } else if (hasExistingServiceTemplates) {
            offerTemplate.getOfferServiceTemplates().removeAll(existingServiceTemplates);
        }
    }

    private void processOfferProductTemplates(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
        List<OfferProductTemplateDto> offerProductTemplateDtos = postData.getOfferProductTemplates();
        boolean hasOfferProductTemplateDtos = offerProductTemplateDtos != null && !offerProductTemplateDtos.isEmpty();
        List<OfferProductTemplate> existingProductTemplates = offerTemplate.getOfferProductTemplates();
        boolean hasExistingProductTemplates = existingProductTemplates != null && !existingProductTemplates.isEmpty();
        if (hasOfferProductTemplateDtos) {
            List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();
            OfferProductTemplate offerProductTemplate = null;
            for (OfferProductTemplateDto offerProductTemplateDto : offerProductTemplateDtos) {
                offerProductTemplate = getOfferProductTemplatesFromDto(offerProductTemplateDto);
                offerProductTemplate.setOfferTemplate(offerTemplate);
                newOfferProductTemplates.add(offerProductTemplate);
            }

            if (hasExistingProductTemplates) {
                List<OfferProductTemplate> offerProductTemplatesForRemoval = new ArrayList<>(existingProductTemplates);
                offerProductTemplatesForRemoval.removeAll(newOfferProductTemplates);
                List<OfferProductTemplate> retainOfferProductTemplates = new ArrayList<>(newOfferProductTemplates);
                retainOfferProductTemplates.retainAll(existingProductTemplates);
                offerProductTemplatesForRemoval.addAll(retainOfferProductTemplates);
                newOfferProductTemplates.removeAll(new ArrayList<>(existingProductTemplates));
                offerTemplate.getOfferProductTemplates().removeAll(new ArrayList<>(offerProductTemplatesForRemoval));
                offerTemplate.getOfferProductTemplates().addAll(retainOfferProductTemplates);
            }

            offerTemplate.getOfferProductTemplates().addAll(newOfferProductTemplates);

        } else if (hasExistingProductTemplates) {
            offerTemplate.getOfferProductTemplates().removeAll(existingProductTemplates);
        }
    }

    private OfferServiceTemplate getOfferServiceTemplatesFromDto(OfferServiceTemplateDto offerServiceTemplateDto) throws MeveoApiException, BusinessException {

        ServiceTemplateDto serviceTemplateDto = offerServiceTemplateDto.getServiceTemplate();
        ServiceTemplate serviceTemplate = null;
        if (serviceTemplateDto != null) {
            serviceTemplate = serviceTemplateService.findByCode(serviceTemplateDto.getCode());
            if (serviceTemplate == null) {
                throw new MeveoApiException(String.format("ServiceTemplatecode %s does not exist.", serviceTemplateDto.getCode()));
            }
        }

        OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
        Boolean mandatory = offerServiceTemplateDto.getMandatory();
        mandatory = mandatory == null ? false : mandatory;

        offerServiceTemplate.setServiceTemplate(serviceTemplate);
        offerServiceTemplate.setMandatory(mandatory);

        if (offerServiceTemplateDto.getIncompatibleServices() != null) {
            List<ServiceTemplate> incompatibleServices = new ArrayList<>();
            for (ServiceTemplateDto stDto : offerServiceTemplateDto.getIncompatibleServices()) {
                ServiceTemplate incompatibleService = serviceTemplateService.findByCode(stDto.getCode());
                if (incompatibleService == null) {
                    throw new EntityDoesNotExistsException(ServiceTemplate.class, stDto.getCode());
                }
                incompatibleServices.add(incompatibleService);
            }
            offerServiceTemplate.setIncompatibleServices(incompatibleServices);
        }

        return offerServiceTemplate;
    }

    private OfferProductTemplate getOfferProductTemplatesFromDto(OfferProductTemplateDto offerProductTemplateDto) throws MeveoApiException, BusinessException {

        ProductTemplateDto productTemplateDto = offerProductTemplateDto.getProductTemplate();
        ProductTemplate productTemplate = null;
        if (productTemplateDto != null) {
            productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), offerProductTemplateDto.getProductTemplate().getValidFrom(),
                offerProductTemplateDto.getProductTemplate().getValidTo());
            if (productTemplate == null) {
                throw new MeveoApiException(String.format("ProductTemplate %s / %s / %s does not exist.", productTemplateDto.getCode(),
                    offerProductTemplateDto.getProductTemplate().getValidFrom(), offerProductTemplateDto.getProductTemplate().getValidTo()));
            }
        }

        OfferProductTemplate offerProductTemplate = new OfferProductTemplate();
        Boolean mandatory = offerProductTemplateDto.getMandatory();
        mandatory = mandatory == null ? false : mandatory;

        offerProductTemplate.setProductTemplate(productTemplate);
        offerProductTemplate.setMandatory(mandatory);

        return offerProductTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiVersionedService#find(java.lang.String)
     */
    @Override
    public OfferTemplateDto find(String code, Date validFrom, Date validTo)
            throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("offerTemplateCode");
            handleMissingParameters();
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (offerTemplate == null) {
            String datePattern = paramBean.getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        OfferTemplateDto offerTemplateDto = convertOfferTemplateToDto(offerTemplate);

        return offerTemplateDto;
    }

    public void remove(String code, Date validFrom, Date validTo) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("offerTemplateCode");
            handleMissingParameters();
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (offerTemplate == null) {
            String datePattern = paramBean.getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        // deleteImage(offerTemplate);
        offerTemplateService.remove(offerTemplate);
    }

    /**
     * Create or updates the OfferTemplate based on code
     *
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public OfferTemplate createOrUpdate(OfferTemplateDto postData) throws MeveoApiException, BusinessException {
        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo());

        if (offerTemplate == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }

    public OfferTemplateDto convertOfferTemplateToDto(OfferTemplate offerTemplate) {

        OfferTemplateDto dto = new OfferTemplateDto(offerTemplate, entityToDtoConverter.getCustomFieldsWithInheritedDTO(offerTemplate, true), false);

        if (offerTemplate.getOfferServiceTemplates() != null && offerTemplate.getOfferServiceTemplates().size() > 0) {
            List<OfferServiceTemplateDto> offerTemplateServiceDtos = new ArrayList<OfferServiceTemplateDto>();
            for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
                offerTemplateServiceDtos.add(new OfferServiceTemplateDto(st, entityToDtoConverter.getCustomFieldsWithInheritedDTO(st.getServiceTemplate(), true)));
            }
            dto.setOfferServiceTemplates(offerTemplateServiceDtos);
        }

        List<OfferProductTemplate> childOfferProductTemplates = offerTemplate.getOfferProductTemplates();
        if (childOfferProductTemplates != null && !childOfferProductTemplates.isEmpty()) {
            List<OfferProductTemplateDto> offerProductTemplates = new ArrayList<>();
            OfferProductTemplateDto offerProductTemplateDto = null;
            ProductTemplate productTemplate = null;
            for (OfferProductTemplate offerProductTemplate : childOfferProductTemplates) {
                productTemplate = offerProductTemplate.getProductTemplate();
                offerProductTemplateDto = new OfferProductTemplateDto();
                offerProductTemplateDto.setMandatory(offerProductTemplate.isMandatory());
                if (productTemplate != null) {
                    offerProductTemplateDto
                        .setProductTemplate(new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsWithInheritedDTO(productTemplate, true), false));
                }
                offerProductTemplates.add(offerProductTemplateDto);
            }
            dto.setOfferProductTemplates(offerProductTemplates);
        }

        return dto;
    }

    /**
     * List all offer templates optionally filtering by code and validity dates. If neither date is provided, validity dates will not be considered.If only validFrom is provided, a
     * search will return offers valid on a given date. If only validTo date is provided, a search will return offers valid from today to a given date.
     * 
     * @param code Offer template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return A list of offer templates
     */
    public List<OfferTemplateDto> list(String code, Date validFrom, Date validTo) {
        List<OfferTemplate> listOfferTemplates = null;

        if (StringUtils.isBlank(code) && validFrom == null && validTo == null) {
            listOfferTemplates = offerTemplateService.list();
        } else {

            Map<String, Object> filters = new HashMap<String, Object>();
            if (!StringUtils.isBlank(code)) {
                filters.put("code", code);
            }

            // If only validTo date is provided, a search will return products valid from today to a given date.
            if (validFrom == null && validTo != null) {
                validFrom = new Date();
            }

            // search by a single date
            if (validFrom != null && validTo == null) {

                filters.put("minmaxOptionalRange-validity.from-validity.to", validFrom);

                // search by date range
            } else if (validFrom != null && validTo != null) {
                filters.put("overlapOptionalRange-validity.from-validity.to", new Date[] { validFrom, validTo });
            }

            PaginationConfiguration config = new PaginationConfiguration(filters);
            listOfferTemplates = offerTemplateService.list(config);
        }

        List<OfferTemplateDto> dtos = new ArrayList<OfferTemplateDto>();
        if (listOfferTemplates != null) {
            for (OfferTemplate offerTemplate : listOfferTemplates) {
                dtos.add(convertOfferTemplateToDto(offerTemplate));
            }
        }
        return dtos;
    }
}