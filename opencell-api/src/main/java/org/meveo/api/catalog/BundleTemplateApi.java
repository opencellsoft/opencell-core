package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.catalog.BundleProductTemplateDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListBundleTemplateResponseDto;
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
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class BundleTemplateApi extends ProductOfferingApi<BundleTemplate, BundleTemplateDto> {

    @Inject
    private BundleTemplateService bundleTemplateService;

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
    public BundleTemplateDto find(String code, Date validFrom, Date validTo)
            throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("bundleTemplate code");
            handleMissingParameters();
        }

        BundleTemplate bundleTemplate = bundleTemplateService.findByCodeBestValidityMatch(code, validFrom, validTo);
        if (bundleTemplate == null) {
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(BundleTemplate.class,
                code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
        }

        return convertBundleTemplateToDto(bundleTemplate);
    }

    private BundleTemplateDto convertBundleTemplateToDto(BundleTemplate bundleTemplate) {
        BundleTemplateDto bundleTemplateDto = new BundleTemplateDto(bundleTemplate, entityToDtoConverter.getCustomFieldsDTO(bundleTemplate, true), false);

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
                    bundleProductTemplateDto.setProductTemplate(new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate, true), false, true));
                }
                bundleProductTemplates.add(bundleProductTemplateDto);
            }
            bundleTemplateDto.setBundleProductTemplates(bundleProductTemplates);
        }

        return bundleTemplateDto;
    }

    @Override
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
            throw new InvalidParameterException(
                "A bundle, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBeanFactory.getInstance().getDateFormat())
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

        if (postData.isDisabled() != null) {
            bundleTemplate.setDisabled(postData.isDisabled());
        }

        try {
            saveImage(bundleTemplate, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        bundleTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        bundleTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), null));

        if (postData.getSellers() != null) {
            bundleTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                bundleTemplate.addSeller(seller);
            }
        }

        if (postData.getChannels() != null && !postData.getChannels().isEmpty()) {
            bundleTemplate.getChannels().clear();
            for (ChannelDto channelDto : postData.getChannels()) {
                Channel channel = channelService.findByCode(channelDto.getCode());
                if (channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, channelDto.getCode());
                }
                bundleTemplate.addChannel(channel);
            }
        }

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

    @Override
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
            String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getCode() + " / " + DateUtils.formatDateWithPattern(postData.getValidFrom(), datePattern) + " / "
                    + DateUtils.formatDateWithPattern(postData.getValidTo(), datePattern));
        }

        List<ProductOffering> matchedVersions = bundleTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(),
            bundleTemplate.getId(), true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException(
                "A bundle, valid on " + new DatePeriod(postData.getValidFrom(), postData.getValidTo()).toString(paramBeanFactory.getInstance().getDateFormat())
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
            bundleTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), bundleTemplate.getDescriptionI18n()));
        }
        if (postData.getLongDescriptionsTranslated() != null) {
            bundleTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), bundleTemplate.getLongDescriptionI18n()));
        }

        if (postData.getSellers() != null) {
            bundleTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                bundleTemplate.addSeller(seller);
            }
        }

        if (postData.getChannels() != null && !postData.getChannels().isEmpty()) {
            bundleTemplate.getChannels().clear();
            for (ChannelDto channelDto : postData.getChannels()) {
                Channel channel = channelService.findByCode(channelDto.getCode());
                if (channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, channelDto.getCode());
                }
                bundleTemplate.addChannel(channel);
            }
        }

        processProductChargeTemplate(postData, bundleTemplate);

        processOfferTemplateCategories(postData, bundleTemplate);

        processDigitalResources(postData, bundleTemplate);

        processBundleProductTemplates(postData, bundleTemplate);

        bundleTemplate = bundleTemplateService.update(bundleTemplate);

        return bundleTemplate;
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
     * List product bundle templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered.If only validFrom is provided, a search will return product bundles valid on a given date. If only valdTo
     * date is provided, a search will return product bundles valid from today to a given date.
     * 
     * @param code Product template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @param pagingAndFiltering Paging and filtering criteria.
     * @return A list of product templates
     * @throws InvalidParameterException invalid parameter exception.
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "bundleTemplates", itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListBundleTemplateResponseDto list(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering)
            throws InvalidParameterException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        pagingAndFiltering.addFilter(PersistenceService.SEARCH_ATTR_TYPE_CLASS, BundleTemplate.class);

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

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, BundleTemplate.class);

        Long totalCount = bundleTemplateService.count(paginationConfig);

        GetListBundleTemplateResponseDto result = new GetListBundleTemplateResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<BundleTemplate> bundleTemplates = bundleTemplateService.list(paginationConfig);
            for (BundleTemplate bundleTemplate : bundleTemplates) {
                result.addBundleTemplate(convertBundleTemplateToDto(bundleTemplate));
            }
        }

        return result;
    }
}