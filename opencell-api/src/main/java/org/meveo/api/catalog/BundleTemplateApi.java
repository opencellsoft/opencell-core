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
import org.meveo.api.dto.catalog.BundleProductTemplateDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;

@Stateless
public class BundleTemplateApi extends ProductOfferingApi<BundleTemplate, BundleTemplateDto> {

    @Inject
    private BundleTemplateService bundleTemplateService;

    @Inject
    private ProductTemplateService productTemplateService;

    private ParamBean paramBean = ParamBean.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public BundleTemplateDto find(String code, Date validFrom, Date validTo)
            throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("bundleTemplate code");
            handleMissingParameters();
        }

        BundleTemplate bundleTemplate = bundleTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (bundleTemplate == null) {
            String datePattern = paramBean.getDateTimeFormat();
            throw new EntityDoesNotExistsException(BundleTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        return convertBundleTemplateToDto(bundleTemplate);
    }

    private BundleTemplateDto convertBundleTemplateToDto(BundleTemplate bundleTemplate) {
        BundleTemplateDto bundleTemplateDto = new BundleTemplateDto(bundleTemplate, entityToDtoConverter.getCustomFieldsWithInheritedDTO(bundleTemplate, true), false);

        processProductChargeTemplateToDto(bundleTemplate, bundleTemplateDto);

        // process all bundleProductTemplates then create
        // bundleProductTemplateDtos accordingly.
        List<BundleProductTemplate> bundleProducts = bundleTemplate.getBundleProducts();
        if (bundleProducts != null && !bundleProducts.isEmpty()) {
            List<BundleProductTemplateDto> bundleProductTemplates = new ArrayList<>();
            BundleProductTemplateDto bundleProductTemplateDto = null;
            ProductTemplate productTemplate = null;
            for (BundleProductTemplate bundleProductTemplate : bundleProducts) {
                bundleProductTemplateDto = new BundleProductTemplateDto();
                bundleProductTemplateDto.setQuantity(bundleProductTemplate.getQuantity());
                productTemplate = bundleProductTemplate.getProductTemplate();
                if (productTemplate != null) {
                    bundleProductTemplateDto
                        .setProductTemplate(new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsWithInheritedDTO(productTemplate, true), false));
                }
                bundleProductTemplates.add(bundleProductTemplateDto);
            }
            bundleTemplateDto.setBundleProductTemplates(bundleProductTemplates);
        }

        return bundleTemplateDto;
    }

    public BundleTemplate createOrUpdate(BundleTemplateDto bundleTemplateDto) throws MeveoApiException, BusinessException {
        BundleTemplate bundleTemplate = bundleTemplateService.findByCode(bundleTemplateDto.getCode(), bundleTemplateDto.getValidFrom(), bundleTemplateDto.getValidTo());

        if (bundleTemplate == null) {
            return create(bundleTemplateDto);
        } else {
            return update(bundleTemplateDto);
        }
    }

    public BundleTemplate create(BundleTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        List<BundleProductTemplateDto> bundleProductTemplates = postData.getBundleProductTemplates();
        if (bundleProductTemplates == null || bundleProductTemplates.isEmpty()) {
            missingParameters.add("bundleProductTemplates");
        }

        handleMissingParameters();

        List<ProductOffering> matchedVersions = bundleTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(), null, true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException("A bundle, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBean.getDateFormat())
                    + ", already exists. Please change the validity dates of an existing bundle first.");
        }

        if (bundleTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo()) != null) {
            throw new EntityAlreadyExistsException(ProductTemplate.class, postData.getCode() + " / " + postData.getValidFrom() + " / " + postData.getValidTo());
        }

        BundleTemplate bundleTemplate = new BundleTemplate();
        bundleTemplate.setCode(postData.getCode());
        bundleTemplate.setDescription(postData.getDescription());
        bundleTemplate.setLongDescription(postData.getLongDescription());
        bundleTemplate.setName(postData.getName());
        bundleTemplate.setValidity(new DatePeriod(postData.getValidFrom(), postData.getValidTo()));
        bundleTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());
        try {
            saveImage(bundleTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        bundleTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions()));
        bundleTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated()));

        // save product template now so that they can be referenced by the
        // related entities below.
        bundleTemplateService.create(bundleTemplate);

        processProductChargeTemplate(postData, bundleTemplate);

        processDigitalResources(postData, bundleTemplate);

        processOfferTemplateCategories(postData, bundleTemplate);

        processBundleProductTemplates(postData, bundleTemplate);

        bundleTemplate = bundleTemplateService.update(bundleTemplate);

        return bundleTemplate;

    }

    public BundleTemplate update(BundleTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        List<BundleProductTemplateDto> bundleProductTemplates = postData.getBundleProductTemplates();
        if (bundleProductTemplates == null || bundleProductTemplates.isEmpty()) {
            missingParameters.add("bundleProductTemplates");
        }

        handleMissingParameters();

        BundleTemplate bundleTemplate = bundleTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo());
        if (bundleTemplate == null) {
            String datePattern = paramBean.getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode() + " / " + DateUtils.formatDateWithPattern(postData.getValidFrom(), datePattern) + " / "
                    + DateUtils.formatDateWithPattern(postData.getValidTo(), datePattern));
        }

        List<ProductOffering> matchedVersions = bundleTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(),
            bundleTemplate.getId(), true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException("A bundle, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBean.getDateFormat())
                    + ", already exists. Please change the validity dates of an existing bundle first.");
        }

        bundleTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        bundleTemplate.setDescription(postData.getDescription());
        bundleTemplate.setLongDescription(postData.getLongDescription());
        bundleTemplate.setName(postData.getName());
        bundleTemplate.setValidity(new DatePeriod(postData.getValidFrom(), postData.getValidTo()));
        bundleTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());
        try {
            saveImage(bundleTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        if (postData.getLanguageDescriptions() != null) {
            bundleTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions()));
        }
        if (postData.getLongDescriptionsTranslated() != null) {
            bundleTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated()));
        }

        processProductChargeTemplate(postData, bundleTemplate);

        processOfferTemplateCategories(postData, bundleTemplate);

        processDigitalResources(postData, bundleTemplate);

        processBundleProductTemplates(postData, bundleTemplate);

        bundleTemplate = bundleTemplateService.update(bundleTemplate);

        return bundleTemplate;
    }

    public void remove(String code, Date validFrom, Date validTo) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("bundleTemplate code");
            handleMissingParameters();
        }

        BundleTemplate bundleTemplate = bundleTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (bundleTemplate == null) {
            String datePattern = paramBean.getDateTimeFormat();
            throw new EntityDoesNotExistsException(BundleTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        deleteImage(bundleTemplate);

        bundleTemplateService.remove(bundleTemplate);
    }

    private void processBundleProductTemplates(BundleTemplateDto postData, BundleTemplate bundleTemplate) throws MeveoApiException, BusinessException {
        List<BundleProductTemplateDto> bundleProductTemplates = postData.getBundleProductTemplates();
        boolean hasBundleProductTemplateDtos = bundleProductTemplates != null && !bundleProductTemplates.isEmpty();
        List<BundleProductTemplate> existingProductTemplates = bundleTemplate.getBundleProducts();
        boolean hasExistingProductTemplates = existingProductTemplates != null && !existingProductTemplates.isEmpty();
        if (hasBundleProductTemplateDtos) {
            List<BundleProductTemplate> newBundleProductTemplates = new ArrayList<>();
            BundleProductTemplate bundleProductTemplate = null;
            for (BundleProductTemplateDto bundleProductTemplateDto : bundleProductTemplates) {
                bundleProductTemplate = getBundleProductTemplatesFromDto(bundleProductTemplateDto);
                bundleProductTemplate.setBundleTemplate(bundleTemplate);
                newBundleProductTemplates.add(bundleProductTemplate);
            }
            if (hasExistingProductTemplates) {
                List<BundleProductTemplate> bundleProductTemplatesForRemoval = new ArrayList<>(existingProductTemplates);
                List<BundleProductTemplate> newBundleProductTemplateForRemoval = new ArrayList<>();
                bundleProductTemplatesForRemoval.removeAll(newBundleProductTemplates);
                bundleTemplate.getBundleProducts().removeAll(bundleProductTemplatesForRemoval);
                for (BundleProductTemplate currentBundleProductTemplate : bundleTemplate.getBundleProducts()) {
                    for (BundleProductTemplate newBundleProductTemplate : newBundleProductTemplates) {
                        if (newBundleProductTemplate.equals(currentBundleProductTemplate)) {
                            currentBundleProductTemplate.setQuantity(newBundleProductTemplate.getQuantity());
                            newBundleProductTemplateForRemoval.add(currentBundleProductTemplate);
                            break;
                        }
                    }
                }
                newBundleProductTemplates.removeAll(newBundleProductTemplateForRemoval);
            }
            bundleTemplate.getBundleProducts().addAll(newBundleProductTemplates);
        } else if (hasExistingProductTemplates) {
            bundleTemplate.getBundleProducts().removeAll(existingProductTemplates);
        }

    }

    private BundleProductTemplate getBundleProductTemplatesFromDto(BundleProductTemplateDto bundleProductTemplateDto) throws MeveoApiException, BusinessException {

        ProductTemplateDto productTemplateDto = bundleProductTemplateDto.getProductTemplate();
        ProductTemplate productTemplate = null;
        if (productTemplateDto != null) {
            productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), bundleProductTemplateDto.getProductTemplate().getValidFrom(),
                bundleProductTemplateDto.getProductTemplate().getValidTo());
            if (productTemplate == null) {
                throw new MeveoApiException(String.format("ProductTemplate %s / %s / %s does not exist.", productTemplateDto.getCode(),
                    bundleProductTemplateDto.getProductTemplate().getValidFrom(), bundleProductTemplateDto.getProductTemplate().getValidTo()));
            }
        }

        BundleProductTemplate bundleProductTemplate = new BundleProductTemplate();

        bundleProductTemplate.setProductTemplate(productTemplate);
        bundleProductTemplate.setQuantity(bundleProductTemplateDto.getQuantity());

        return bundleProductTemplate;
    }

    /**
     * List all product bundle templates optionally filtering by code and validity dates. If neither date is provided, validity dates will not be considered.If only validFrom is
     * provided, a search will return product bundles valid on a given date. If only valdTo date is provided, a search will return product bundles valid from today to a given date.
     * 
     * @param code Product template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return A list of product templates
     */
    public List<BundleTemplateDto> list(String code, Date validFrom, Date validTo) {

        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, BundleTemplate.class);

        if (!StringUtils.isBlank(code) || validFrom != null || validTo != null) {

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
        }

        PaginationConfiguration config = new PaginationConfiguration(filters);
        List<BundleTemplate> bundleTemplates = bundleTemplateService.list(config);

        List<BundleTemplateDto> dtos = new ArrayList<BundleTemplateDto>();
        if (bundleTemplates != null) {
            for (BundleTemplate bundleTemplate : bundleTemplates) {
                dtos.add(convertBundleTemplateToDto(bundleTemplate));
            }
        }
        return dtos;
    }
}