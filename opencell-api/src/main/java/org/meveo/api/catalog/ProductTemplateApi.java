package org.meveo.api.catalog;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListProductTemplateResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.primefaces.model.SortOrder;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ProductTemplateApi extends ProductOfferingApi<ProductTemplate, ProductTemplateDto> {

    @Inject
    private ProductTemplateService productTemplateService;

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public ProductTemplateDto find(String code, Date validFrom, Date validTo)
            throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("productTemplate code");
            handleMissingParameters();
        }

        ProductTemplate productTemplate = productTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (productTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(ProductTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        return convertProductTemplateToDto(productTemplate);
    }

    private ProductTemplateDto convertProductTemplateToDto(ProductTemplate productTemplate) {

        ProductTemplateDto productTemplateDto = new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate, true), false);
        processProductChargeTemplateToDto(productTemplate, productTemplateDto);
        return productTemplateDto;
    }

    @Override
    public ProductTemplate create(ProductTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }

        if (postData.getProductChargeTemplates() != null) {
            List<ProductChargeTemplateDto> productChargeTemplateDtos = postData.getProductChargeTemplates();
            for (ProductChargeTemplateDto productChargeTemplateDto : productChargeTemplateDtos) {
                if (productChargeTemplateDto == null || StringUtils.isBlank(productChargeTemplateDto.getCode())) {
                    missingParameters.add("productChargeTemplate");
                }
            }
        } else {
            missingParameters.add("productChargeTemplates");
        }

        handleMissingParameters();

        List<ProductOffering> matchedVersions = productTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(), null, true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException(
                "A product, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBeanFactory.getInstance().getDateFormat())
                        + ", already exists. Please change the validity dates of an existing product first.");
        }

        if (productTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo()) != null) {
            throw new EntityAlreadyExistsException(ProductTemplate.class, postData.getCode() + " / " + postData.getValidFrom() + " / " + postData.getValidTo());
        }

        ProductTemplate productTemplate = new ProductTemplate();
        productTemplate.setCode(postData.getCode());
        productTemplate.setDescription(postData.getDescription());
        productTemplate.setLongDescription(postData.getLongDescription());
        productTemplate.setName(postData.getName());
        productTemplate.setValidity(new DatePeriod(postData.getValidFrom(), postData.getValidTo()));
        productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

        if (postData.isDisabled() != null) {
            productTemplate.setDisabled(postData.isDisabled());
        }

        try {
            saveImage(productTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        productTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        productTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), null));

        if (postData.getSellers() != null) {
            productTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                productTemplate.addSeller(seller);
            }
        }

        if (postData.getChannels() != null && !postData.getChannels().isEmpty()) {
            productTemplate.getChannels().clear();
            for (ChannelDto channelDto : postData.getChannels()) {
                Channel channel = channelService.findByCode(channelDto.getCode());
                if (channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, channelDto.getCode());
                }
                productTemplate.addChannel(channel);
            }
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), productTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        // save product template now so that they can be referenced by the related entities below.
        productTemplateService.create(productTemplate);

        if (postData.getProductChargeTemplates() != null) {
            processProductChargeTemplate(postData, productTemplate);
        }
        if (postData.getAttachments() != null) {
            processDigitalResources(postData, productTemplate);
        }
        if (postData.getOfferTemplateCategories() != null) {
            processOfferTemplateCategories(postData, productTemplate);
        }

        productTemplateService.update(productTemplate);

        return productTemplate;
    }

    @Override
    public ProductTemplate update(ProductTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }
        handleMissingParameters();

        ProductTemplate productTemplate = productTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo());
        if (productTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode() + " / " + DateUtils.formatDateWithPattern(postData.getValidFrom(), datePattern) + " / "
                    + DateUtils.formatDateWithPattern(postData.getValidTo(), datePattern));
        }

        List<ProductOffering> matchedVersions = productTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(),
            productTemplate.getId(), true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException(
                "A product, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBeanFactory.getInstance().getDateFormat())
                        + ", already exists. Please change the validity dates of an existing product first.");
        }

        productTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        productTemplate.setDescription(postData.getDescription());
        productTemplate.setLongDescription(postData.getLongDescription());
        productTemplate.setName(postData.getName());
        productTemplate.setValidity(new DatePeriod(postData.getValidFrom(), postData.getValidTo()));
        productTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());

        try {
            saveImage(productTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        if (postData.getProductChargeTemplates() != null) {
            processProductChargeTemplate(postData, productTemplate);
        }
        if (postData.getOfferTemplateCategories() != null) {
            processOfferTemplateCategories(postData, productTemplate);
        }
        if (postData.getAttachments() != null) {
            processDigitalResources(postData, productTemplate);
        }

        if (postData.getLanguageDescriptions() != null) {
            productTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), productTemplate.getDescriptionI18n()));
        }
        if (postData.getLongDescriptionsTranslated() != null) {
            productTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), productTemplate.getLongDescriptionI18n()));
        }

        if (postData.getSellers() != null) {
            productTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                productTemplate.addSeller(seller);
            }
        }

        if (postData.getChannels() != null && !postData.getChannels().isEmpty()) {
            productTemplate.getChannels().clear();
            for (ChannelDto channelDto : postData.getChannels()) {
                Channel channel = channelService.findByCode(channelDto.getCode());
                if (channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, channelDto.getCode());
                }
                productTemplate.addChannel(channel);
            }
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), productTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        productTemplate = productTemplateService.update(productTemplate);

        return productTemplate;
    }

    /**
     * List product templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered.If only validFrom is provided, a search will return product bundles valid on a given date. If only valdTo
     * date is provided, a search will return product valid from today to a given date.
     * 
     * @param code Product template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @param pagingAndFiltering Paging and filtering criteria.
     * @return A list of product templates
     * @throws InvalidParameterException invalid parameter exception
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "listProductTemplate", itemPropertiesToFilter = {
            @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListProductTemplateResponseDto list(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering)
            throws InvalidParameterException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        pagingAndFiltering.addFilter(PersistenceService.SEARCH_ATTR_TYPE_CLASS, ProductTemplate.class);

        if (!StringUtils.isBlank(code) || validFrom != null || validTo != null) {

            if (!StringUtils.isBlank(code)) {
                pagingAndFiltering.addFilter("code", code);
            }

            // If only validTo date is provided, a search will return products valid from today to a given date.
            if (validFrom == null && validTo != null) {
                validFrom = new Date();
            }

            // search by a single date
            if (validFrom != null && validTo == null) {
                pagingAndFiltering.addFilter("minmaxOptionalRange validity.from validity.to", validFrom);

                // search by date range
            } else if (validFrom != null && validTo != null) {
                pagingAndFiltering.addFilter("overlapOptionalRange validity.from validity.to", new Date[] { validFrom, validTo });
            }

            pagingAndFiltering.addFilter("disabled", false);

        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, ProductTemplate.class);

        Long totalCount = productTemplateService.count(paginationConfig);

        GetListProductTemplateResponseDto result = new GetListProductTemplateResponseDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<ProductTemplate> productTemplates = productTemplateService.list(paginationConfig);
            for (ProductTemplate productTemplate : productTemplates) {
                result.addProductTemplate(convertProductTemplateToDto(productTemplate));
            }
        }

        return result;
    }
}