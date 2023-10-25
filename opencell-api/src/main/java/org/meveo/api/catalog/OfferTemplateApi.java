/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.catalog;

import static org.meveo.service.base.PersistenceService.SEARCH_IS_NULL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.CpqOfferDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.OfferProductTemplateDto;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ProductOfferTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.cpq.CustomerContextDTO;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.cpq.OfferContextConfigDTO;
import org.meveo.api.dto.cpq.OfferProductsDto;
import org.meveo.api.dto.cpq.OfferTemplateAttributeDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductVersionAttributeDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListCpqOfferResponseDto;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.document.Document;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.FileTypeService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.document.DocumentService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class OfferTemplateApi extends ProductOfferingApi<OfferTemplate, OfferTemplateDto> {

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

    @Inject
    private SubscriptionApi subscriptionApi;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private AttributeService attributeService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private CustomerCategoryService customerCategoryService;

    @Inject
    private TagService tagService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private ProductService productService;

    @Inject
    private ProductVersionService productVersionService;

    @Inject
    private MediaService mediaService;

    @Inject
    private CommercialRuleHeaderService commercialRuleHeaderService;

    @Inject
    private DocumentService documentService;
    @Inject
    private FileTypeService fileTypeService;

    @Override
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "sellers", entityClass = Seller.class, parser = ObjectPropertyParser.class))
    public OfferTemplate create(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getName())) {
            postData.setName(postData.getCode());
        }
        if (postData.getLifeCycleStatus() == null) {
            postData.setLifeCycleStatus(LifeCycleStatusEnum.IN_DESIGN);
        }
        postData.setNewValidFrom(postData.getValidFrom());
        postData.setNewValidTo(postData.getValidTo());
        handleMissingParameters();

        if (offerTemplateService.findByCode(postData.getCode(), postData.getValidFrom(), postData.getValidTo()) != null) {
            throw new EntityAlreadyExistsException(OfferTemplate.class, postData.getCode() + " / " + postData.getValidFrom() + " / " + postData.getValidTo());
        }

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(postData.getCode(), postData.getValidFrom(), postData.getValidTo(), null, true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException("Offer with the same code already exists on this period." +
                    " You cannot create offer versions with overlapping validity periods.");
        }

        OfferTemplate offerTemplate = populateFromDto(postData, null);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplate, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplateService.create(offerTemplate);

        return offerTemplate;
    }

    @Override
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "sellers", entityClass = Seller.class, parser = ObjectPropertyParser.class))
    public OfferTemplate update(OfferTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            postData.setName(postData.getCode());
        }

        if (postData.getNewValidFrom() == null && postData.getNewValidTo() == null) {
            postData.setNewValidFrom(postData.getValidFrom());
            postData.setNewValidTo(postData.getValidTo());
        }

        handleMissingParametersAndValidate(postData);

        OfferTemplate offerTemplate = findOfferTemplate(postData.getCode(), postData.getValidFrom(), postData.getValidTo());

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(postData.getCode(), postData.getNewValidFrom(), postData.getNewValidTo(),
                offerTemplate.getId(), true);
        if (!matchedVersions.isEmpty()) {
            throw new InvalidParameterException("Offer with the same code already exists on this period." +
                    " You cannot create offer versions with overlapping validity periods.");
        }

        offerTemplate = populateFromDto(postData, offerTemplate);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        offerTemplate = offerTemplateService.update(offerTemplate);

        return offerTemplate;
    }

    private OfferTemplate populateFromDto(OfferTemplateDto postData, OfferTemplate offerTemplateToUpdate) throws MeveoApiException, BusinessException {

        OfferTemplate offerTemplate = offerTemplateToUpdate;

        if (offerTemplate == null) {
            offerTemplate = new OfferTemplate();
            if (postData.isDisabled() != null) {
                offerTemplate.setDisabled(postData.isDisabled());
            }
        }

        if (!Strings.isEmpty(postData.getOfferModelCode())) {
            offerTemplate.setOfferModel(loadEntityByCode(offerTemplateService, postData.getOfferModelCode(), OfferTemplate.class));
        }

        offerTemplate.setIsOfferChangeRestricted(postData.isOfferChangeRestricted());

        if (postData.getAllowedOfferChange() != null && !postData.getAllowedOfferChange().isEmpty()) {
            List<OfferTemplate> allowedOffers = new ArrayList<>();
            for (String offerTemplateCode : postData.getAllowedOfferChange()) {
                OfferTemplate allowedOffer = offerTemplateService.findByCode(offerTemplateCode);
                if (allowedOffer == null) {
                    throw new EntityDoesNotExistsException(OfferTemplate.class, offerTemplateCode);
                }
                allowedOffers.add(allowedOffer);
            }
            offerTemplate.setAllowedOffersChange(allowedOffers);
        }

        Boolean autoEndOfEngagement = postData.getAutoEndOfEngagement();
        if (autoEndOfEngagement != null) {
            offerTemplate.setAutoEndOfEngagement(autoEndOfEngagement);
        }

        Boolean isModel = postData.getIsModel();
        if (isModel != null) {
            offerTemplate.setIsModel(isModel);
        }

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

        if (!StringUtils.isBlank(postData.getGlobalRatingScriptInstance())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getGlobalRatingScriptInstance());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getGlobalRatingScriptInstance());
            }
            offerTemplate.setGlobalRatingScriptInstance(scriptInstance);
        }

        if (postData.getSellers() != null) {
            offerTemplate.getSellers().clear();
            for (String sellerCode : postData.getSellers()) {
                Seller seller = sellerService.findByCode(sellerCode);
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, sellerCode);
                }
                offerTemplate.addSeller(seller);
            }
        }

        if (postData.getChannels() != null) {
            offerTemplate.getChannels().clear();
            for (ChannelDto channelDto : postData.getChannels()) {
                Channel channel = channelService.findByCode(channelDto.getCode());
                if (channel == null) {
                    throw new EntityDoesNotExistsException(Channel.class, channelDto.getCode());
                }
                offerTemplate.addChannel(channel);
            }
        }

        if (postData.getCustomerCategories() != null) {
            offerTemplate.getCustomerCategories().clear();
            addCustomerCategories(postData.getCustomerCategories(), offerTemplate);
        }

        offerTemplate.setBusinessOfferModel(businessOffer);
        offerTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        if(postData.getDescription() != null) 
        	offerTemplate.setDescription(postData.getDescription());
        if(postData.getName() != null) 
        	offerTemplate.setName(postData.getName());
        if(postData.getLongDescription() != null) 
        	offerTemplate.setLongDescription(postData.getLongDescription());
        
        if(!LifeCycleStatusEnum.RETIRED.equals(offerTemplate.getLifeCycleStatus())) {
            offerTemplate.setSequence(postData.getSequence());
            offerTemplate.setDisplay(postData.isDisplay());  
        }       
        
        var datePeriod = new DatePeriod();

        datePeriod.setFrom(postData.getNewValidFrom());
        datePeriod.setTo(postData.getNewValidTo());

        offerTemplate.setValidity(datePeriod);
        var isValidityIsSet = offerTemplate.getValidity().getTo() != null && offerTemplate.getValidity().getFrom() != null;
        if (isValidityIsSet && offerTemplate.getValidity().getTo().compareTo(offerTemplate.getValidity().getFrom()) <= 0) {
            throw new MeveoApiException("Date 'valid to' must be great than Data 'valid from'");
        }
        offerTemplate.setMinimumAmountEl(postData.getMinimumAmountEl());
        offerTemplate.setMinimumLabelEl(postData.getMinimumLabelEl());
        offerTemplate.setStatusDate(Calendar.getInstance().getTime());

        if (!StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
            OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
            if (minimumChargeTemplate == null) {
                throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
            } else {
                offerTemplate.setMinimumChargeTemplate(minimumChargeTemplate);
            }
        }
        if (!StringUtils.isBlank(postData.getLifeCycleStatus())) {
            offerTemplate.setLifeCycleStatus(postData.getLifeCycleStatus());
        }

        if (postData.getLanguageDescriptions() != null) {
            offerTemplate.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), offerTemplate.getDescriptionI18n()));
        }
        if (postData.getLongDescriptionsTranslated() != null) {
            offerTemplate.setLongDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLongDescriptionsTranslated(), offerTemplate.getLongDescriptionI18n()));
        }

        if (postData.getDocumentCode() != null) {
            Document document = documentService.findByCode(postData.getDocumentCode());
            if (document == null) {
                throw new EntityDoesNotExistsException("The document with code " + postData.getDocumentCode() + " does not exist");
            } else {
                offerTemplate.setDocument(document);
            }
        }

        offerTemplate.setSubscriptionRenewal(subscriptionApi.subscriptionRenewalFromDto(offerTemplate.getSubscriptionRenewal(), postData.getRenewalRule(), false));
        offerTemplate.setGenerateQuoteEdrPerProduct(postData.getGenerateQuoteEdrPerProduct());
        processAllowedDiscountPlans(postData, offerTemplate);
        processTags(postData, offerTemplate);
        processOfferProductDtos(postData, offerTemplate);
        processTemplateAttribute(postData, offerTemplate);
        processMedias(postData, offerTemplate);
        processCommercialRule(postData, offerTemplate);
        try {
            String imagePath = postData.getImagePath();
            if (StringUtils.isBlank(imagePath) && StringUtils.isBlank(postData.getImageBase64())) {
                deleteImage(offerTemplate);
                offerTemplate.setImagePath(imagePath);
            } else {
                saveImage(offerTemplate, imagePath, postData.getImageBase64());
            }
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        // check service templates
        processOfferServiceTemplates(postData, offerTemplate);

        // check offer product templates
        processOfferProductTemplates(postData, offerTemplate);

        return offerTemplate;
    }

    private void addCustomerCategories(List<CustomerCategoryDto> customerCategoryDtos, OfferTemplate offerTemplate) {
        for (CustomerCategoryDto categoryDto : customerCategoryDtos) {
            CustomerCategory customerCategory = customerCategoryService.findByCode(categoryDto.getCode());
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, categoryDto.getCode());
            }
            offerTemplate.addCustomerCategory(customerCategory);
        }
    }

    private void processAllowedDiscountPlans(OfferTemplateDto postData, OfferTemplate offerTemplate) {
        List<DiscountPlanDto> allowedDiscountPlans = postData.getAllowedDiscountPlans();
        offerTemplate.getAllowedDiscountPlans().clear();
        if (allowedDiscountPlans != null && !allowedDiscountPlans.isEmpty()) {
            offerTemplate.getAllowedDiscountPlans()
                    .addAll(allowedDiscountPlans.stream().map(discountPlanDto -> discountPlanService.findByCode(discountPlanDto.getCode())).collect(Collectors.toList()));
        }
    }

    private void processTemplateAttribute(OfferTemplateDto postData, OfferTemplate offerTemplate) {
        List<OfferTemplateAttributeDTO> offerAttributes = postData.getOfferAttributes();
        offerTemplate.getOfferAttributes().clear();
        if (offerAttributes != null && !offerAttributes.isEmpty()) {
            offerTemplate.getOfferAttributes().addAll(offerAttributes.stream().map(offerAttributeDto -> {
                var attribute = attributeService.findByCode(offerAttributeDto.getAttributeCode());
                if (attribute == null)
                    throw new EntityDoesNotExistsException(Attribute.class, offerAttributeDto.getAttributeCode());
                var templateAttribute = new OfferTemplateAttribute();
                templateAttribute.setOfferTemplate(offerTemplate);
                templateAttribute.setAttribute(attribute);
                templateAttribute.setMandatoryWithEl(offerAttributeDto.getMandatoryWithEl());
                templateAttribute.setSequence(offerAttributeDto.getSequence());
                templateAttribute.setDisplay(offerAttributeDto.isDisplay());
                templateAttribute.setReadOnly(offerAttributeDto.isReadOnly());
                templateAttribute.setMandatory(offerAttributeDto.isMandatory());
                templateAttribute.setDefaultValue(offerAttributeDto.getDefaultValue());
                templateAttribute.setValidationLabel(offerAttributeDto.getValidationLabel());
                templateAttribute.setValidationPattern(offerAttributeDto.getValidationPattern());
                templateAttribute.setValidationType(offerAttributeDto.getValidationType());
                validateTemplateAttribute(templateAttribute);
                return templateAttribute;
            }).collect(Collectors.toList()));
        }
    }
    
    private void validateTemplateAttribute(OfferTemplateAttribute templateAttribute) {
    	// A hidden mandatory field must have a default value 
    	if (templateAttribute.isMandatory() && !templateAttribute.isDisplay() 
         		&& (templateAttribute.getDefaultValue() == null || templateAttribute.getDefaultValue().isEmpty())) 
         	 throw new InvalidParameterException("Default value is required for an attribute mandatory and hidden");

    	// A read-only mandatory attribute must have a default value
    	 if (templateAttribute.isMandatory() && templateAttribute.getReadOnly()
          		&& (templateAttribute.getDefaultValue() == null || templateAttribute.getDefaultValue().isEmpty())) 
          	 throw new InvalidParameterException("Default value is required for an attribute mandatory and read-only");
    }

    private void processTags(OfferTemplateDto postData, OfferTemplate offerTemplate) {
        Set<String> tagCodes = postData.getTagCodes();
        offerTemplate.getTags().clear();
        if (tagCodes != null && !tagCodes.isEmpty()) {
            List<Tag> tags = new ArrayList<Tag>();
            for (String code : tagCodes) {
                Tag tag = tagService.findByCode(code);
                if (tag == null) {
                    throw new EntityDoesNotExistsException(Tag.class, code);
                }
                tags.add(tag);
            }
            offerTemplate.setTags(tags);
        }
    }

    private void processMedias(OfferTemplateDto postData, OfferTemplate offerTemplate) {
        Set<String> mediaCodes = postData.getMediaCodes();
        offerTemplate.getMedias().clear();
        if (mediaCodes != null && !mediaCodes.isEmpty()) {
            List<Media> medias = new ArrayList<Media>();
            for (String code : mediaCodes) {
                Media media = mediaService.findByCode(code);
                if (media == null) {
                    throw new EntityDoesNotExistsException(Media.class, code);
                }
                medias.add(media);
            }
            offerTemplate.setMedias(medias);
        }
    }

    private void processCommercialRule(OfferTemplateDto postData, OfferTemplate offerTemplate) {
        Set<String> commercialRuleCodes = new HashSet<String>(postData.getCommercialRuleCodes());
        offerTemplate.getCommercialRules().clear();
        if (commercialRuleCodes != null && !commercialRuleCodes.isEmpty()) {
            for (String commercialCode : commercialRuleCodes) {
                CommercialRuleHeader commercialRuleHeader = loadEntityByCode(commercialRuleHeaderService, commercialCode, CommercialRuleHeader.class);
                offerTemplate.getCommercialRules().add(commercialRuleHeader);
            }
        }
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

    private void processOfferProductDtos(OfferTemplateDto postData, OfferTemplate offerTemplate) throws MeveoApiException, BusinessException {
        processOfferProductDtos(postData, offerTemplate, true);
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

    private OfferComponent getOfferComponentFromDto(OfferProductsDto offerComponentDto) throws MeveoApiException, BusinessException {
        ProductDto productDto = offerComponentDto.getProduct();
        Product product = null;
        if (productDto != null) {
            product = productService.findByCode(productDto.getCode());
            if (product == null) {
                throw new MeveoApiException(String.format("productCode %s does not exist.", productDto.getCode()));
            }
        }
        OfferComponent offerComponent = new OfferComponent();
        offerComponent.setProduct(product);
        offerComponent.setSequence(offerComponentDto.getSequence());
        offerComponent.setMandatory(offerComponentDto.isMandatory());
        offerComponent.setDisplay(offerComponentDto.isDisplay());
        offerComponent.setQuantityDefault(offerComponentDto.getQuantityDefault());
        offerComponent.setQuantityMin(offerComponentDto.getQuantityMin());
        offerComponent.setQuantityMax(offerComponentDto.getQuantityMax());
        offerComponent.setProductSet(offerComponentDto.getProductSet());
        offerComponent.setSequence(offerComponentDto.getSequence());
        return offerComponent;
    }

    private OfferProductTemplate getOfferProductTemplatesFromDto(OfferProductTemplateDto offerProductTemplateDto) throws MeveoApiException, BusinessException {

        ProductTemplateDto productTemplateDto = offerProductTemplateDto.getProductTemplate();
        ProductTemplate productTemplate = null;
        if (productTemplateDto != null) {
            productTemplate = productTemplateService.findByCode(productTemplateDto.getCode(), offerProductTemplateDto.getProductTemplate().getValidFrom(),
                    offerProductTemplateDto.getProductTemplate().getValidTo());
            if (productTemplate == null) {
                throw new MeveoApiException(
                        String.format("ProductTemplate %s / %s / %s does not exist.", productTemplateDto.getCode(), offerProductTemplateDto.getProductTemplate().getValidFrom(),
                                offerProductTemplateDto.getProductTemplate().getValidTo()));
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
    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public OfferTemplateDto find(String code, Date validFrom, Date validTo) throws MeveoApiException {
        return find(code, validFrom, validTo, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
    }

    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetOfferTemplateResponseDto find(String code, Date validFrom, Date validTo, CustomFieldInheritanceEnum inheritCF, boolean loadOfferServiceTemplate,
            boolean loadOfferProductTemplate, boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate, boolean loadAllowedDiscountPlan) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("offerTemplateCode");
            handleMissingParameters();
        }

        OfferTemplate offerTemplate = findOfferTemplate(code, validFrom, validTo);

        return fromOfferTemplate(offerTemplate, inheritCF, Boolean.TRUE, loadOfferServiceTemplate, loadOfferProductTemplate, loadServiceChargeTemplate, loadProductChargeTemplate,
                loadAllowedDiscountPlan, Boolean.FALSE, Boolean.FALSE, null,null);
    }

    @Override
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "sellers", entityClass = Seller.class, parser = ObjectPropertyParser.class))
    public OfferTemplate createOrUpdate(OfferTemplateDto postData) throws MeveoApiException, BusinessException {
        return super.createOrUpdate(postData);
    }

    public OfferTemplateDto fromOfferTemplate(OfferTemplate offerTemplate) {
        return fromOfferTemplate(offerTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, true, true, true, true, true, true, false, false, null,null);
    }

    public GetOfferTemplateResponseDto fromOfferTemplate(OfferTemplate offerTemplate, CustomFieldInheritanceEnum inheritCF, boolean loadOfferProducts,
            boolean loadOfferServiceTemplate, boolean loadOfferProductTemplate, boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate,
            boolean loadAllowedDiscountPlan, boolean loadAttributes, boolean loadTags, List<String> requestedTagTypes, OfferContextConfigDTO config) {

        GetOfferTemplateResponseDto dto = new GetOfferTemplateResponseDto(offerTemplate, entityToDtoConverter.getCustomFieldsDTO(offerTemplate, inheritCF), false, true, true);

        dto.setMinimumAmountEl(offerTemplate.getMinimumAmountEl());
        dto.setMinimumLabelEl(offerTemplate.getMinimumLabelEl());
        //dto.setOfferTemplate(new OfferTemplateDto(offerTemplate,entityToDtoConverter.getCustomFieldsDTO(offerTemplate, inheritCF), false));

        if (loadOfferServiceTemplate && offerTemplate.getOfferServiceTemplates() != null && !offerTemplate.getOfferServiceTemplates().isEmpty()) {
            List<OfferServiceTemplateDto> offerTemplateServiceDtos = new ArrayList<>();
            for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
                offerTemplateServiceDtos.add(
                        new OfferServiceTemplateDto(st, entityToDtoConverter.getCustomFieldsDTO(st.getServiceTemplate(), inheritCF), loadServiceChargeTemplate));
            }
            dto.setOfferServiceTemplates(offerTemplateServiceDtos);
        }

        if (loadOfferProductTemplate) {
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
                        offerProductTemplateDto.setProductTemplate(
                                new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate, inheritCF), false, loadProductChargeTemplate));
                    }
                    offerProductTemplates.add(offerProductTemplateDto);
                }
                dto.setOfferProductTemplates(offerProductTemplates);
            }
        }
        if (loadOfferProducts) {
            List<OfferComponent> offerComponents = offerTemplate.getOfferComponents();
            if (offerComponents != null && !offerComponents.isEmpty()) {

                List<ProductVersion> productVersionList = null;
                List<Tag> tags = null;
                List<OfferProductsDto> offerProducts = new ArrayList<>();
                OfferProductsDto offerProductsDto = null;
                GetProductVersionResponse getProductVersionResponse = null;
                Product product = null;

                for (OfferComponent offerComponent : offerComponents) {
                    product = offerComponent.getProduct();
                    offerProductsDto = new OfferProductsDto();
                    if (product != null && ProductStatusEnum.ACTIVE.equals(product.getStatus())) {
                        ProductDto productDTO = new ProductDto(product);
                        productDTO.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(product));
                        offerProductsDto.setOfferTemplateCode(offerTemplate.getCode());
                        productVersionList = productVersionService.getVersionsByStatusAndProduct(VersionStatusEnum.PUBLISHED, product.getCode());
                        if (productVersionList != null && !productVersionList.isEmpty()) {

                            for (ProductVersion productVersion : productVersionList) {
                                if (productVersion.getValidity() != null) {
                                    if (productVersion.getValidity().isCorrespondsToPeriod(new Date())) {
                                        if (requestedTagTypes != null && !requestedTagTypes.isEmpty()) {
                                            tags = productVersionService.getProductTagsByType(requestedTagTypes);
                                            productVersion.setTags(new HashSet<Tag>(tags));
                                        }

                                        getProductVersionResponse = new GetProductVersionResponse(productVersion, false, true);

                                        if (productVersion.getAttributes() != null && !productVersion.getAttributes().isEmpty()) {
                                            Set<ProductVersionAttributeDTO> attributes = productVersion.getAttributes().stream().map(ProductVersionAttributeDTO::new)
                                                    .collect(Collectors.toSet());
                                            getProductVersionResponse.setProductAttributes(attributes);
                                            Set<GroupedAttributeDto> groupedAttributeDtos = productVersion.getGroupedAttributes().stream().map(att -> new GroupedAttributeDto(att))
                                                    .collect(Collectors.toSet());
                                            getProductVersionResponse.setGroupedAttributes(groupedAttributeDtos);
                                        }
                                        productDTO.setCurrentProductVersion(getProductVersionResponse);
                                    }
                                }
                            }
                        }
                        offerProductsDto.setProduct(productDTO);
                        offerProductsDto.setMandatory(offerComponent.isMandatory());
                        offerProductsDto.setSequence(offerComponent.getSequence());
                        offerProductsDto.setDisplay(offerComponent.isDisplay());
                        offerProductsDto.setQuantityDefault(offerComponent.getQuantityDefault());
                        offerProductsDto.setQuantityMin(offerComponent.getQuantityMin());
                        offerProductsDto.setQuantityMax(offerComponent.getQuantityMax());
                        offerProductsDto.setProductSet(offerComponent.getProductSet());
                        offerProducts.add(offerProductsDto);
                    }

                }
                dto.setOfferProducts(offerProducts);

            }
        }

        if (loadAllowedDiscountPlan) {
            List<DiscountPlan> allowedDiscountPlans = offerTemplate.getAllowedDiscountPlans();
            if (allowedDiscountPlans != null && !allowedDiscountPlans.isEmpty()) {
                List<DiscountPlanDto> discountPlanDtos = new ArrayList<>();
                for (DiscountPlan discountPlan : allowedDiscountPlans) {
                    discountPlanDtos.add(new DiscountPlanDto(discountPlan, entityToDtoConverter.getCustomFieldsDTO(discountPlan)));
                }
                dto.setAllowedDiscountPlans(discountPlanDtos);
            }
        }
        if (loadAttributes) {
            dto.setOfferAttributes(offerTemplate.getOfferAttributes().stream().map(OfferTemplateAttributeDTO::new).collect(Collectors.toList()));
        }
        return dto;
    }

    public OfferTemplateDto fromOfferTemplate(OfferTemplate offerTemplate, CustomFieldInheritanceEnum inheritCF, boolean loadOfferServiceTemplate, boolean loadOfferProductTemplate,
            boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate, boolean loadAllowedDiscountPlan) {
        OfferTemplateDto dto = new OfferTemplateDto(offerTemplate, entityToDtoConverter.getCustomFieldsDTO(offerTemplate, inheritCF), false);
        dto.setMinimumAmountEl(offerTemplate.getMinimumAmountEl());
        dto.setMinimumLabelEl(offerTemplate.getMinimumLabelEl());
        if (loadOfferServiceTemplate && offerTemplate.getOfferServiceTemplates() != null && !offerTemplate.getOfferServiceTemplates().isEmpty()) {
            List<OfferServiceTemplateDto> offerTemplateServiceDtos = new ArrayList<>();
            for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
                offerTemplateServiceDtos.add(
                        new OfferServiceTemplateDto(st, entityToDtoConverter.getCustomFieldsDTO(st.getServiceTemplate(), inheritCF), loadServiceChargeTemplate));
            }
            dto.setOfferServiceTemplates(offerTemplateServiceDtos);
        }
        if (loadOfferProductTemplate) {
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
                        offerProductTemplateDto.setProductTemplate(
                                new ProductTemplateDto(productTemplate, entityToDtoConverter.getCustomFieldsDTO(productTemplate, inheritCF), false, loadProductChargeTemplate));
                    }
                    offerProductTemplates.add(offerProductTemplateDto);
                }
                dto.setOfferProductTemplates(offerProductTemplates);
            }
        }
        if (loadAllowedDiscountPlan) {
            List<DiscountPlan> allowedDiscountPlans = offerTemplate.getAllowedDiscountPlans();
            if (allowedDiscountPlans != null && !allowedDiscountPlans.isEmpty()) {
                List<DiscountPlanDto> discountPlanDtos = new ArrayList<>();
                for (DiscountPlan discountPlan : allowedDiscountPlans) {
                    discountPlanDtos.add(new DiscountPlanDto(discountPlan, entityToDtoConverter.getCustomFieldsDTO(discountPlan)));
                }
                dto.setAllowedDiscountPlans(discountPlanDtos);
            }
        }
        return dto;
    }

    /**
     * List Offer templates matching filtering and query criteria or code and validity dates.
     *
     * If neither date is provided, validity dates will not be considered. If only validFrom is provided, a search will return offers valid on a given date. If only validTo date is
     * provided, a search will return offers valid from today to a given date.
     *
     * @param code               Offer template code for optional filtering
     * @param validFrom          Validity range from date.
     * @param validTo            Validity range to date.
     * @param pagingAndFiltering Paging and filtering criteria.
     * @return A list of offer templates
     * @throws InvalidParameterException invalid parametter exception.
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "offerTemplates", itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListOfferTemplateResponseDto list(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering)
            throws InvalidParameterException {
        return list(code, validFrom, validTo, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    public GetListCpqOfferResponseDto list(CustomerContextDTO customerContextDto) {
        String billingAccountCode = customerContextDto.getBillingAccountCode();

        if (Strings.isEmpty(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
        }
        GetListCpqOfferResponseDto result = new GetListCpqOfferResponseDto();
        List<String> baTagCodes = new ArrayList<String>();

        HashSet<String> requestedTagsByType=new HashSet<String>();
    	if(customerContextDto.getRequestedTagTypes()!=null && !customerContextDto.getRequestedTagTypes().isEmpty()) {
    		requestedTagsByType = new HashSet<>(tagService.findByRequestedTagType(customerContextDto.getRequestedTagTypes()));
    	}
        BillingAccount ba = billingAccountService.findByCode(billingAccountCode);

        if (ba != null) {
            List<Tag> baTags = ba.getTags();
            if (!baTags.isEmpty()) {
                for (Tag tag : baTags) {
                    baTagCodes.add(tag.getCode());
                }
            }
        }
        List<String> sellerTags = customerContextDto.getSellerTags();
        List<String> customerTags = customerContextDto.getCustomerTags();
        HashSet<String> requestedTags = new HashSet<String>();
        if (baTagCodes != null) {
            requestedTags.addAll(baTagCodes);
        }
        if (customerTags != null) {
            requestedTags.addAll(customerTags);
        }
        if (sellerTags != null) {
            requestedTags.addAll(sellerTags);
        }

        if(!requestedTagsByType.isEmpty() && requestedTags.isEmpty()) {
			requestedTags=requestedTagsByType;
		}else if(!requestedTagsByType.isEmpty() ){
			requestedTags.retainAll(requestedTagsByType);
		}
		
		log.info("OfferTemplateApi.list resultBaTag={}",requestedTags);  
		String tags=null;
		if (!requestedTags.isEmpty()) {
			tags=requestedTags.stream().collect(Collectors.joining(","));
		} 
		PagingAndFiltering pagingAndFiltering=customerContextDto.getPagingAndFiltering();
		if(pagingAndFiltering==null) {
			pagingAndFiltering=new PagingAndFiltering();
		}
		List<OfferTemplate> offersWithoutTags =new ArrayList<>();
		if(tags!=null) {
		pagingAndFiltering.addFilter("inList tags", tags);
		offersWithoutTags=offerTemplateService.list().stream().filter(offer -> offer.getTags().isEmpty()).collect(Collectors.toList());
		}else {
		pagingAndFiltering.addFilter("tags", SEARCH_IS_NULL);
	    }
		PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, OfferTemplate.class);
		List<OfferTemplate> offers=offerTemplateService.list(paginationConfig);
		if(offersWithoutTags!=null && !offersWithoutTags.isEmpty()) {
			offers.addAll(offersWithoutTags);
		}
		result.setPaging(pagingAndFiltering);
		if(!offers.isEmpty()) {
		result.getPaging().setTotalNumberOfRecords(offers.size());
		for (OfferTemplate offerTemplate : offers) {
			boolean loadTags=customerContextDto.getRequestedTagTypes()!=null && !customerContextDto.getRequestedTagTypes().isEmpty();
			GetOfferTemplateResponseDto offertemplateDTO=fromOfferTemplate(offerTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE,true,true,false, false,false,true,false,loadTags,customerContextDto.getRequestedTagTypes(),null);
			result.addOffer(new CpqOfferDto(offertemplateDTO));
		}
		}
		return result;
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "offerTemplates", itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListOfferTemplateResponseDto list(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering,
            CustomFieldInheritanceEnum inheritCF) throws InvalidParameterException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

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

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, OfferTemplate.class);

        Long totalCount = offerTemplateService.count(paginationConfig);

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<OfferTemplate> offers = offerTemplateService.list(paginationConfig);
            for (OfferTemplate offerTemplate : offers) {
                result.addOfferTemplate(
                        fromOfferTemplate(offerTemplate, inheritCF, pagingAndFiltering.hasFieldOption("offerProduct"), pagingAndFiltering.hasFieldOption("offerServiceTemplate"),
                                pagingAndFiltering.hasFieldOption("offerProductTemplate"), pagingAndFiltering.hasFieldOption("serviceChargeTemplate"),
                                pagingAndFiltering.hasFieldOption("productChargeTemplate"), pagingAndFiltering.hasFieldOption("loadAllowedDiscountPlan"), false, false, null,null));
            }
        }

        return result;
    }

    public OfferTemplateDto duplicate(String offerTemplateCode, boolean duplicateHierarchy, boolean preserveCode, Date validFrom, Date validTo) {
    	OfferTemplate offerTemplate = findOfferTemplate(offerTemplateCode, validFrom, validTo);
    	OfferTemplate duplicated = offerTemplateService.duplicate(offerTemplate, duplicateHierarchy, true, preserveCode);
    	return new GetOfferTemplateResponseDto(duplicated, entityToDtoConverter.getCustomFieldsDTO(offerTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), false,true,true);
    }

    public void updateStatus(String offerTemplateCode, LifeCycleStatusEnum status, Date validFrom, Date validTo) {
        if (status == null)
            missingParameters.add("status");
        handleMissingParameters();
        OfferTemplate offerTemplate = findOfferTemplate(offerTemplateCode, validFrom, validTo);

        if (LifeCycleStatusEnum.ACTIVE.equals(status)) {
            if (offerTemplate.getOfferComponents().isEmpty()) {
                throw new MeveoApiException("Offer Template code " + offerTemplateCode + " doesn't have product");
            }
            offerTemplate.getOfferComponents().forEach(offerComponent -> {
                if (offerComponent.getProduct() != null) {
                    if (!offerComponent.getProduct().getStatus().equals(ProductStatusEnum.ACTIVE)) {
                        throw new MeveoApiException("All product must be activated before activating offer");
                    }
                } else {
                    throw new MeveoApiException("Offer Template code " + offerTemplateCode + " doesn't have product");
                }
            });
        }
        offerTemplate.setLifeCycleStatus(status);
        offerTemplateService.update(offerTemplate);
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "offerTemplates", itemPropertiesToFilter = { @FilterProperty(property = "sellers", entityClass = Seller.class, allowAccessIfNull = true) })
    public GetListOfferTemplateResponseDto listGetAll(@Deprecated String code, @Deprecated Date validFrom, @Deprecated Date validTo, PagingAndFiltering pagingAndFiltering,
            CustomFieldInheritanceEnum inheritCF) throws InvalidParameterException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

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

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, OfferTemplate.class);

        Long totalCount = offerTemplateService.count(paginationConfig);

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<OfferTemplate> offers = offerTemplateService.list(paginationConfig);
            for (OfferTemplate offerTemplate : offers) {
                result.addOfferTemplate(fromOfferTemplate(offerTemplate, inheritCF, pagingAndFiltering.hasFieldOption("offerServiceTemplate"),
                        pagingAndFiltering.hasFieldOption("offerProductTemplate"), pagingAndFiltering.hasFieldOption("serviceChargeTemplate"),
                        pagingAndFiltering.hasFieldOption("productChargeTemplate"), pagingAndFiltering.hasFieldOption("loadAllowedDiscountPlan")));
            }
        }

        return result;
    }

    public OfferTemplateDto addProduct(String offerCode, ProductOfferTemplateDto productDto) {
        if (Strings.isBlank(offerCode))
            missingParameters.add("offerCode");
        if (productDto == null)
            missingParameters.add("productDto");
    	if(productDto.getProducts().isEmpty())
            missingParameters.add("products");
        handleMissingParameters();

        var offerTemplate = findOfferTemplate(offerCode, productDto.getValidFrom(), productDto.getValidTo());
        var tmpOfferTemplateDto = new OfferTemplateDto();
        tmpOfferTemplateDto.getOfferProducts().addAll(productDto.getProducts());
        List<String> productCodes = productDto.getProducts().stream().map(offerProductDto -> offerProductDto.getProduct().getCode()).collect(Collectors.toList());
        offerTemplate.getOfferComponents().removeIf(offerComponent -> offerComponent.getProduct() != null && productCodes.contains(offerComponent.getProduct().getCode()));
        processOfferProductDtos(tmpOfferTemplateDto, offerTemplate, false);
        offerTemplateService.update(offerTemplate);
        return find(offerCode, productDto.getValidFrom(), productDto.getValidTo());
    }

    public OfferTemplateDto dissociateProduct(String offerCode, Date validFrom, Date validTo, List<String> productCodes) {
        if (Strings.isBlank(offerCode)) {
            missingParameters.add("offerCode");
            handleMissingParameters();
        }
        var offerTemplate = findOfferTemplate(offerCode, validFrom, validTo);
        productCodes.forEach(productCode -> {
            offerTemplate.getOfferComponents()
                    .removeIf(offerComponent -> offerComponent.getProduct() != null && offerComponent.getProduct().getCode().equalsIgnoreCase(productCode));
        });
        return find(offerCode, validFrom, validTo);
    }

    private OfferTemplate findOfferTemplate(String offerCode, Date validFrom, Date validTo) {
    	var offerTemplate = offerTemplateService.findByCodeBestValidityMatch(offerCode, validFrom, validTo);
    	String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
        if(offerTemplate == null)
        	 throw new EntityDoesNotExistsException(OfferTemplate.class, offerCode + "and validity dates from:" + DateUtils.formatDateWithPattern(validFrom, datePattern) + " - to: "
                     + DateUtils.formatDateWithPattern(validTo, datePattern));
        return offerTemplate;
    }

    private void processOfferProductDtos(OfferTemplateDto postData, OfferTemplate offerTemplate, boolean clearOfferComponent) throws MeveoApiException, BusinessException {
        List<OfferProductsDto> offerProductDtos = postData.getOfferProducts();
        List<OfferComponent> newOfferProductDtos = new ArrayList<>();
        var productCodes = new HashSet<String>();
        if (clearOfferComponent)
            offerTemplate.getOfferComponents().clear();
        boolean hasOfferComponentDtos = offerProductDtos != null && !offerProductDtos.isEmpty();
        if (hasOfferComponentDtos) {
            updateSequence(offerProductDtos);
            for (var currentOfferProduct : offerProductDtos) {
                if (currentOfferProduct.getProduct() == null || !productCodes.add(currentOfferProduct.getProduct().getCode()))
                    continue;
                OfferComponent offerComponent = getOfferComponentFromDto(currentOfferProduct);
                offerComponent.setOfferTemplate(offerTemplate);
                newOfferProductDtos.add(offerComponent);
            }
            offerTemplate.getOfferComponents().addAll(newOfferProductDtos);
        }
    }

    /**
     * Increment a sequence in offer products in the same offer template.
     *
     * @param offerProductDtoList a list of offer products
     */
    private void updateSequence(List<OfferProductsDto> offerProductDtoList) {
        int next = offerProductDtoList.parallelStream().mapToInt(OfferProductsDto::getSequence).max().orElse(1) + 1;
        Set<Integer> all = new HashSet<>();
        for (OfferProductsDto dto : offerProductDtoList) {
            if ((dto.getSequence() == 0) || all.contains(dto.getSequence())) {
                dto.setSequence(next++);
            }
            all.add(dto.getSequence());
        }
    }
}