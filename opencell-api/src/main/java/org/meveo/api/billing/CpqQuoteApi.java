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

package org.meveo.api.billing;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.print.attribute.standard.Media;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.NoTaxException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.*;
import org.meveo.api.dto.cpq.xml.TaxPricesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.CpqQuotesListResponseDto;
import org.meveo.api.dto.response.cpq.GetPdfQuoteResponseDto;
import org.meveo.api.dto.response.cpq.GetQuoteDtoResponse;
import org.meveo.api.dto.response.cpq.GetQuoteVersionDtoResponse;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.StatusUpdated;
import org.meveo.model.BaseEntity;
import org.meveo.model.RatingResult;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.enums.PriceVersionDateSettingEnum;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.AttributeInstanceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.RecurringRatingService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.OfferTemplateAttributeService;
import org.meveo.service.cpq.ProductVersionAttributeService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.QuoteArticleLineService;
import org.meveo.service.cpq.QuoteAttributeService;
import org.meveo.service.cpq.QuoteLotService;
import org.meveo.service.cpq.QuoteMapper;
import org.meveo.service.cpq.QuoteProductService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.cpq.XmlQuoteFormatter;
import org.meveo.service.cpq.order.InvoicingPlanService;
import org.meveo.service.cpq.order.QuotePriceService;
import org.meveo.service.quote.QuoteOfferService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.settings.impl.GlobalSettingsService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;


/**
 * @author Rachid.AITYAAZZA
 * @lastModifiedVersion 11.0
 */
@Stateless
public class CpqQuoteApi extends BaseApi {

    @Inject
    private CpqQuoteService cpqQuoteService;
    @Inject
    private SellerService sellerService;
    @Inject
    private BillingAccountService billingAccountService;
    @Inject
    private ContractService contractService;
    @Inject
    private QuoteOfferService quoteOfferService;
    @Inject
    private QuoteVersionService quoteVersionService;
    @Inject
    private QuoteLotService quoteLotService;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private AttributeService attributeService;
    @Inject
    private QuoteAttributeService quoteAttributeService;
    @Inject
    private QuoteProductService quoteProductService;
    @Inject
    private ProductVersionService productVersionService;
    @Inject
    private ServiceSingleton serviceSingleton;
    @Inject
    private InvoiceTypeService invoiceTypeService;
    @Inject
    private InvoicingPlanService invoicingPlanService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private AccountingArticleService accountingArticleService;

    @Inject
    private QuoteArticleLineService quoteArticleLineService;

    @Inject
    private QuotePriceService quotePriceService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private QuoteMapper quoteMapper;

    @Inject
    private XmlQuoteFormatter quoteFormatter;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private CommercialRuleHeaderService commercialRuleHeaderService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    @StatusUpdated
    protected Event<CpqQuote> cpqQuoteStatusUpdatedEvent;

    @Inject
    private MediaService mediaService;

    @Inject
    private RecurringRatingService recurringRatingService;
    
    @Inject
    private AttributeInstanceService attributeInstanceService;
    
    @Inject
    private UsageRatingService usageRatingService;
    
    @Inject
    private GlobalSettingsService globalSettingsService;
    
    @Inject
    private SubscriptionService subscriptionService;
    
    @Inject
    private WalletOperationService walletOperationService;
    
	@Inject
	private ContractHierarchyHelper contractHierarchyHelper;

    @Inject
    private TaxService taxService;

    @Inject
    private PriceListService priceListService;
    
	
	@Inject
	private ProductVersionAttributeService productVersionAttributeService;
	
	@Inject
	private OfferTemplateAttributeService offerTemplateAttributeService;
	
    private static final String ADMINISTRATION_VISUALIZATION = "administrationVisualization";
    
    private static final String ADMINISTRATION_MANAGEMENT = "administrationManagement";

	public QuoteDTO createQuote(QuoteDTO quoteDto) {
	    if(StringUtils.isBlank(quoteDto.getApplicantAccountCode())) {
            missingParameters.add("applicantAccountCode");
        }
        CpqQuote cpqQuote = new CpqQuote();
        if(StringUtils.isBlank(quoteDto.getCode())) {
            customGenericEntityCodeService.getGenericEntityCode(cpqQuote);
        }
        handleMissingParameters();
        
        if(cpqQuoteService.findByCode(quoteDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(CpqQuote.class, quoteDto.getCode());
        }

        cpqQuote.setCode(quoteDto.getCode());
        final BillingAccount applicantAccount = billingAccountService.findByCode(quoteDto.getApplicantAccountCode());
        if(applicantAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, quoteDto.getApplicantAccountCode());
        }
        cpqQuote.setApplicantAccount(applicantAccount);

        //Manage Seller
        Seller seller = null;
        if(StringUtils.isNotBlank(quoteDto.getSellerCode())) {
            seller = sellerService.findByCode(quoteDto.getSellerCode());
            if(seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, quoteDto.getSellerCode());
            }
        } else {
            seller = applicantAccount.getCustomerAccount().getCustomer().getSeller();
            if(seller == null) {
                throw new EntityDoesNotExistsException("No seller found. a seller must be defined either on quote or at customer level");
            }
        }
        cpqQuote.setSeller(seller);

        if(StringUtils.isNotBlank(quoteDto.getBillableAccountCode())) {
            var billableAccount = billingAccountService.findByCode(quoteDto.getBillableAccountCode());
            if(billableAccount == null) {
                cpqQuote.setBillableAccount(applicantAccount);
            }
            else {
                cpqQuote.setBillableAccount(billableAccount);
            }
        } else {
            cpqQuote.setBillableAccount(applicantAccount);
        }
        if(StringUtils.isNotBlank(quoteDto.getUserAccountCode())) {
            UserAccount userAccount = userAccountService.findByCode(quoteDto.getUserAccountCode());
            if(userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, quoteDto.getUserAccountCode());
            }
            
            if(!userAccount.getIsConsumer()) {
                throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Quote for this user account is not allowed.");
            }
            cpqQuote.setUserAccount(userAccount);
        }
        cpqQuote.setStatusDate(new Date());
        cpqQuote.setSendDate(quoteDto.getSendDate());
        
    	if(quoteDto.getDeliveryDate()!=null && quoteDto.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");
    	}
    	
    	//Fill the CpqQuote with CpqQouteDto
    	fillCpqQuote(quoteDto, cpqQuote);
        cpqQuote.setOrderInvoiceType(invoiceTypeService.getDefaultQuote());
        
        try {
            cpqQuoteService.create(cpqQuote);
            QuoteVersion newQuoteVersion = populateNewQuoteVersion(quoteDto.getQuoteVersion(), cpqQuote);
            quoteVersionService.create(newQuoteVersion);
            quoteDto.setQuoteVersion(new QuoteVersionDto(newQuoteVersion));
        } catch(BusinessApiException e) {
            throw new MeveoApiException(e);
        }
        
        quoteDto.setStatusDate(cpqQuote.getStatusDate());
        quoteDto.setId(cpqQuote.getId());
        quoteDto.setCode(cpqQuote.getCode());
        return quoteDto;
	}

	/**
	 * Fill Cpq Quate with Quote Dto informations
	 * @param quoteDto {@link QuoteDTO}
	 * @param cpqQuote {@link CpqQuote}
	 */
	private void fillCpqQuote(QuoteDTO quoteDto, CpqQuote cpqQuote) {
		cpqQuote.setDeliveryDate(quoteDto.getDeliveryDate());
        cpqQuote.setQuoteLotDuration(quoteDto.getQuoteLotDuration());
        cpqQuote.setOpportunityRef(quoteDto.getOpportunityRef());
        cpqQuote.setCustomerRef(quoteDto.getExternalId());
        cpqQuote.setValidity(quoteDto.getValidity());
        cpqQuote.setDescription(quoteDto.getDescription());
        cpqQuote.setQuoteDate(quoteDto.getQuoteDate());
        cpqQuote.setSalesPersonName(quoteDto.getSalesPersonName());
	}

	private QuoteVersion populateNewQuoteVersion(QuoteVersionDto quoteVersionDto, CpqQuote cpqQuote) {
		QuoteVersion quoteVersion = new QuoteVersion();
		quoteVersion.setStatusDate(new Date());
		quoteVersion.setQuoteVersion(1);
		quoteVersion.setStatus(VersionStatusEnum.DRAFT);
		if(quoteVersionDto != null) {
    		if(StringUtils.isNotBlank(quoteVersionDto.getBillingPlanCode())) {
    			InvoicingPlan invoicingPlan = invoicingPlanService.findByCode(quoteVersionDto.getBillingPlanCode());
    			if (invoicingPlan == null) {
    				throw new EntityDoesNotExistsException(InvoicingPlan.class, quoteVersionDto.getBillingPlanCode());
    			}
			    quoteVersion.setInvoicingPlan(invoicingPlan);
            }

            if(StringUtils.isNotBlank(quoteVersionDto.getPriceListCode())) {
                String priceListCode = quoteVersionDto.getPriceListCode();
                PriceList priceList = priceListService.findByCode(priceListCode);
                if(priceList == null) {
                    throw new EntityDoesNotExistsException(PriceList.class, priceListCode);
                }
                quoteVersion.setPriceList(priceList);
            }

    		if (quoteVersionDto.getStartDate() == null) {
    		    quoteVersion.setStartDate(new Date());
            }
    		else {
    		    quoteVersion.setStartDate(quoteVersionDto.getStartDate());
    		}

    		if (quoteVersionDto.getEndDate() == null) {
    		    GlobalSettings globalSettings = globalSettingsService.findLastOne();
    		    Date endDate = null;
    		    if (globalSettings != null) {
    		        endDate = DateUtils.addDaysToDate(new Date(), globalSettings.getQuoteDefaultValidityDelay());
                }
                quoteVersion.setEndDate(endDate);
            }
    		else {
    		    quoteVersion.setEndDate(quoteVersionDto.getEndDate());
    		}

    		quoteVersion.setShortDescription(quoteVersionDto.getShortDescription());
    		if(StringUtils.isNotBlank(quoteVersionDto.getDiscountPlanCode())) {
    			 quoteVersion.setDiscountPlan(loadEntityByCode(discountPlanService, quoteVersionDto.getDiscountPlanCode(), DiscountPlan.class));
    		}
    		quoteVersion.setContract(contractHierarchyHelper.checkContractHierarchy(cpqQuote.getBillableAccount(), quoteVersionDto.getContractCode()));
    		if(quoteVersionDto.getMediaCodes() != null) {
    		    quoteVersionDto.getMediaCodes().forEach(mediaCode -> {
            		var media = mediaService.findByCode(mediaCode);
            		if(media == null) {
            			throw new EntityDoesNotExistsException(Media.class, mediaCode);
            		}
            		quoteVersion.getMedias().add(media);
            	});
	        }
    		if(StringUtils.isNotBlank(quoteVersionDto.getComment())) {
    			quoteVersion.setComment(quoteVersionDto.getComment());
    		}
    	populateCustomFields(quoteVersionDto.getCustomFields(), quoteVersion, true);
		}
		quoteVersion.setQuote(cpqQuote);
	
		return quoteVersion;
	}

	public GetQuoteVersionDtoResponse createQuoteVersion(QuoteVersionDto quoteVersionDto) {
		if(StringUtils.isBlank(quoteVersionDto.getQuoteCode()))
			missingParameters.add("quoteCode");
		final CpqQuote quote = cpqQuoteService.findByCode(quoteVersionDto.getQuoteCode());
		if(quote == null) {
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteVersionDto.getQuoteCode());
		}
		final QuoteVersion quoteVersion = populateNewQuoteVersion(quoteVersionDto, quote);
		try {
            quoteVersionService.create(quoteVersion);
        } catch (BusinessApiException e) {
            throw new MeveoApiException(e);
        }
        return new GetQuoteVersionDtoResponse(quoteVersion, entityToDtoConverter.getCustomFieldsDTO(quoteVersion));
    }


    private void newPopulateProduct(QuoteOfferDTO quoteOfferDto, QuoteOffer quoteOffer) {
        List<QuoteProductDTO> quoteProductDtos = quoteOfferDto.getProducts();
        if (CollectionUtils.isEmpty(quoteProductDtos)) {
            missingParameters.add("products");
            handleMissingParameters();
        }
        int index = 1;
        quoteOffer.getQuoteProduct().size();
        for (QuoteProductDTO quoteProductDTO : quoteProductDtos) {
            if (StringUtils.isBlank(quoteProductDTO.getProductCode()))
                missingParameters.add("products[" + index + "].productCode");
            if (quoteProductDTO.getProductVersion() == null)
                missingParameters.add("products[" + index + "].productVersion");

            handleMissingParameters();
            if(quoteProductDTO.getQuantity() == null || quoteProductDTO.getQuantity().equals(BigDecimal.ZERO)) {
                throw new BusinessException("The quantity for product code " + quoteProductDTO.getProductCode() + " must be great than 0" );
            }

            ProductVersion productVersion = productVersionService.findByProductAndVersion(quoteProductDTO.getProductCode(), quoteProductDTO.getProductVersion());

            if (productVersion == null)
                throw new EntityDoesNotExistsException(ProductVersion.class, "products[" + index + "] = " + quoteProductDTO.getProductCode() + "," + quoteProductDTO.getProductVersion());
            DiscountPlan discountPlan = null;
            if (quoteProductDTO.getDiscountPlanCode() != null) {
                discountPlan = discountPlanService.findByCode(quoteProductDTO.getDiscountPlanCode());
                if (discountPlan == null)
                    throw new EntityDoesNotExistsException(DiscountPlan.class, quoteProductDTO.getDiscountPlanCode());
            }
            QuoteProduct quoteProduct = null;
            if (quoteProduct == null)
                quoteProduct = new QuoteProduct();
            quoteProduct.setProductVersion(productVersion);
            quoteProduct.setQuantity(quoteProductDTO.getQuantity());
            quoteProduct.setQuoteOffer(quoteOffer);
            quoteProduct.setDiscountPlan(discountPlan);
            if(quoteProduct.getDiscountPlan() == null){
                resolveProductDPIfExist(quoteOffer, quoteProduct);
            }
            quoteProduct.setQuote(quoteOffer.getQuoteVersion() != null ? quoteOffer.getQuoteVersion().getQuote() : null);
            quoteProduct.setQuoteVersion(quoteOffer.getQuoteVersion());
            
        	if(quoteProductDTO.getDeliveryDate()!=null && quoteProductDTO.getDeliveryDate().before(new Date())) {
        		throw new MeveoApiException("Delivery date should be in the future");	
        	}
        	quoteProduct.setDeliveryDate(quoteProductDTO.getDeliveryDate());
            
            populateCustomFields(quoteProductDTO.getCustomFields(), quoteProduct, true);
            quoteProductService.create(quoteProduct);
            newPopulateQuoteAttribute(quoteProductDTO.getProductAttributes(), quoteProduct);
            quoteOffer.getQuoteProduct().add(quoteProduct);
            ProductContextDTO productContextDTO = new ProductContextDTO();
            productContextDTO.setProductCode(productVersion.getProduct().getCode());
            LinkedHashMap<String, Object> selectedAttributes = new LinkedHashMap<>();
            quoteProductDTO.getProductAttributes()
                    .stream()
                    .forEach(productAttribute -> {
						selectedAttributes.put(productAttribute.getQuoteAttributeCode(), productAttribute.getStringValue());
	                    Attribute attribute = attributeService.findByCode(productAttribute.getQuoteAttributeCode());
						if(attribute != null){
							ProductVersionAttribute productVersionAttribute = productVersionAttributeService.findByProductVersionAndAttribute(productVersion.getId(), attribute.getId());
							if(productVersionAttribute != null) {
								productAttribute.setSequence(productVersionAttribute.getSequence());
							}
						}
                    });
            productContextDTO.setSelectedAttributes(selectedAttributes);
            ++index;
        }
        
    }
	
    private void newPopulateOfferAttribute(List<QuoteAttributeDTO> quoteAttributeDtos, QuoteOffer quoteOffer) {
        if (quoteAttributeDtos != null) {
            for (QuoteAttributeDTO quoteAttributeDTO : quoteAttributeDtos) {
                Attribute attribute = attributeService.findByCode(quoteAttributeDTO.getQuoteAttributeCode());
                if (attribute == null)
                    throw new EntityDoesNotExistsException(Attribute.class, quoteAttributeDTO.getQuoteAttributeCode());
                QuoteAttribute quoteAttribute = new QuoteAttribute();
                quoteAttribute.setAttribute(attribute);
                quoteAttribute.setStringValue(quoteAttributeDTO.getStringValue());
                quoteAttribute.setDoubleValue(quoteAttributeDTO.getDoubleValue());
                if(quoteAttribute.getDoubleValue()==null && quoteAttribute.getStringValue()!=null ) {
                	if(org.apache.commons.lang3.math.NumberUtils.isCreatable(quoteAttribute.getStringValue().trim())) {
                		quoteAttribute.setDoubleValue(Double.valueOf(quoteAttribute.getStringValue()));
        			}
                }
                quoteAttribute.setDateValue(quoteAttributeDTO.getDateValue());
                quoteAttribute.updateAudit(currentUser);
                quoteAttribute.setQuoteOffer(quoteOffer);

                if (!quoteAttributeDTO.getLinkedQuoteAttribute().isEmpty()) {
                    List<QuoteAttribute> linkedQuoteAttributes = quoteAttributeDTO.getLinkedQuoteAttribute()
                            .stream()
                            .map(dto -> {
                                QuoteAttribute linkedAttribute = createQuoteAttribute(dto, null, null);
                                linkedAttribute.setParentAttributeValue(quoteAttribute);
                                return linkedAttribute;
                            })
                            .collect(Collectors.toList());
                    quoteAttribute.setAssignedAttributeValue(linkedQuoteAttributes);
                }
                quoteAttributeService.create(quoteAttribute);
                quoteOffer.getQuoteAttributes().add(quoteAttribute);
            }

        }
    }


    private void newPopulateQuoteAttribute(List<QuoteAttributeDTO> quoteAttributeDTOS, QuoteProduct quoteProduct) {
        if (quoteAttributeDTOS != null && !quoteAttributeDTOS.isEmpty()) {
        	 List<Attribute> productAttributes = quoteProduct.getProductVersion().getAttributes().stream().map(pva -> pva.getAttribute()).collect(Collectors.toList());
            quoteProduct.getQuoteAttributes().clear();
            quoteAttributeDTOS.stream()
                    .map(quoteAttributeDTO -> createQuoteAttribute(quoteAttributeDTO, quoteProduct, productAttributes))
                    .collect(Collectors.toList())
                    .forEach(quoteAttribute -> quoteAttributeService.create(quoteAttribute));
        }
    }

    private QuoteAttribute createQuoteAttribute(QuoteAttributeDTO quoteAttributeDTO, QuoteProduct quoteProduct, List<Attribute> productAttributes) {
        if (StringUtils.isBlank(quoteAttributeDTO.getQuoteAttributeCode()))
            missingParameters.add("quoteAttributeCode");
        handleMissingParameters();
        Attribute attribute = attributeService.findByCode(quoteAttributeDTO.getQuoteAttributeCode());
        if (attribute == null)
            throw new EntityDoesNotExistsException(Attribute.class, quoteAttributeDTO.getQuoteAttributeCode());
        if(productAttributes != null && !productAttributes.contains(attribute)){
            throw new BusinessApiException(String.format("Product version (code: %s, version: %d), doesn't contain attribute code: %s", quoteProduct.getProductVersion().getProduct().getCode() , quoteProduct.getProductVersion().getCurrentVersion(), attribute.getCode()));
        }
        QuoteAttribute quoteAttribute = new QuoteAttribute();
        quoteAttribute.setAttribute(attribute);
        quoteAttribute.setStringValue(quoteAttributeDTO.getStringValue());
        quoteAttribute.setDoubleValue(quoteAttributeDTO.getDoubleValue());
        if(quoteAttribute.getDoubleValue()==null && quoteAttribute.getStringValue()!=null ) {
        	if(org.apache.commons.lang3.math.NumberUtils.isCreatable(quoteAttribute.getStringValue().trim())) {
        		quoteAttribute.setDoubleValue(Double.valueOf(quoteAttribute.getStringValue()));
			}
        }
        quoteAttribute.setDateValue(quoteAttributeDTO.getDateValue());
        if(productAttributes != null) {
            quoteProduct.getQuoteAttributes().add(quoteAttribute);
            quoteAttribute.setQuoteProduct(quoteProduct);
        }
        quoteAttribute.updateAudit(currentUser);
        if(!quoteAttributeDTO.getLinkedQuoteAttribute().isEmpty()){
            List<QuoteAttribute> linkedQuoteAttributes = quoteAttributeDTO.getLinkedQuoteAttribute()
                    .stream()
                    .map(dto -> {
                        QuoteAttribute linkedAttribute = createQuoteAttribute(dto, quoteProduct, null);
                        linkedAttribute.setParentAttributeValue(quoteAttribute);
                        return linkedAttribute;
                    })
                    .collect(Collectors.toList());
            quoteAttribute.setAssignedAttributeValue(linkedQuoteAttributes);
        }
        return quoteAttribute;
    }

    public GetPdfQuoteResponseDto generateQuoteXml(String quoteCode, int currentVersion, boolean generatePdf) {
        GetPdfQuoteResponseDto result = new GetPdfQuoteResponseDto();
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion); 
        byte[] xmlContent = null;
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");

        try {
            InvoiceType invoiceType=invoiceTypeService.getDefaultQuote();
            String meveoDir = paramBeanFactory.getChrootDir() + File.separator;
            File quoteXmlDir = new File(meveoDir + "quotes" + File.separator + "xml");
            if (!quoteXmlDir.exists()) {
                quoteXmlDir.mkdirs();
            }

            TaxDetailDTO taxDetail = new TaxDetailDTO();
            Map<String, TaxDTO> mapTaxIndexes = buildTaxesIndexes(quoteVersion, taxDetail);

            ScriptInstance scriptInstance = invoiceType.getCustomInvoiceXmlScriptInstance();
            if (scriptInstance != null) {
                    String quoteXmlScript = scriptInstance.getCode();
                    ScriptInterface script = scriptInstanceService.getScriptInstance(quoteXmlScript);
                    Map<String, Object> methodContext = new HashMap<>();
                    methodContext.put("quoteVersion", quoteVersion);
                    methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                    methodContext.put("XMLQuoteCreator", this);
                    if (script != null) {
                        script.execute(methodContext);
                    }
                    xmlContent = (byte[]) methodContext.get(Script.RESULT_VALUE);
                    result.setXmlContent(xmlContent);

            } else {
                String quoteXml = quoteFormatter.format(quoteMapper.map(quoteVersion, mapTaxIndexes, taxDetail));
                xmlContent = quoteXml.getBytes(StandardCharsets.UTF_8);
                result.setXmlContent(xmlContent);
            }
            String fileName = cpqQuoteService.generateFileName(quoteVersion);
            quoteVersion.setXmlFilename(fileName);
            String xmlFilename = quoteXmlDir.getPath() + File.separator + fileName + ".xml";
            Path xmlPath = Paths.get(xmlFilename);
            Files.write(xmlPath, xmlContent, xmlPath.toFile().exists() ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.CREATE);
            if (generatePdf) {
                result.setPdfContent(generateQuotePDF(quoteCode, currentVersion, true));
                CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
                if (quote == null) {
                    throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode, "Code");
                }
                result.setPdfFileName(quoteVersion.getPdfFilename());
            }


        } catch (Exception exp) {
            log.error("Technical error", exp);
            throw new BusinessException(exp.getMessage());
        }

        return result;
    }

    private Map<String, TaxDTO> buildTaxesIndexes(QuoteVersion quoteVersion, TaxDetailDTO taxDetail) {
        Map<String, TaxDTO> mapTaxesIndexes = new HashMap<>();

        List<TaxDTO> taxes = new ArrayList<>();
        taxDetail.setTaxes(taxes);

        StringBuilder vatex = new StringBuilder();

        int i = 0;
        for (QuoteArticleLine quoteArticleLine : quoteVersion.getQuoteArticleLines()) {
            for (QuotePrice quotePrice : quoteArticleLine.getQuotePrices().stream().filter(e -> PriceLevelEnum.QUOTE.equals(e.getPriceLevelEnum())).collect(Collectors.toList())) {
                Tax tax = taxService.findTaxByPercent(quotePrice.getTaxRate());

                TaxDTO taxDTO = mapTaxesIndexes.get(quotePrice.getTaxRate().toString());

                if (taxDTO == null) {
                    taxDTO = new TaxDTO();
                    taxDTO.setTax(quotePrice.getTaxRate());
                    taxDTO.setIndex(String.valueOf(++i));
                    taxDTO.setPercent(String.valueOf(tax.getPercent()));
                    taxDTO.setCode(tax.getCode());
                    taxDTO.setCompositeRate(getTaxCompositeRate(tax,
                            quoteVersion.getQuote().getBillableAccount().getTradingCountry().getCode()));

                    taxDTO.setAmountTax(taxDTO.getAmountTax().add(quotePrice.getTaxAmount()));
                    taxDTO.setAmountWithTax(taxDTO.getAmountWithTax().add(quotePrice.getAmountWithTax()));
                    taxDTO.setAmountWithoutTax(taxDTO.getAmountWithoutTax().add(quotePrice.getAmountWithoutTax()));

                    if (tax.getUntdidVatex() != null) {
                        if (vatex.length() > 0) {
                            vatex.append("|");
                        }
                        vatex.append(tax.getUntdidVatex().getCodeName());
                    }

                    mapTaxesIndexes.put(quotePrice.getTaxRate().toString(), taxDTO);
                } else {
                    taxDTO.setAmountTax(taxDTO.getAmountTax().add(quotePrice.getTaxAmount()));
                    taxDTO.setAmountWithTax(taxDTO.getAmountWithTax().add(quotePrice.getAmountWithTax()));
                    taxDTO.setAmountWithoutTax(taxDTO.getAmountWithoutTax().add(quotePrice.getAmountWithoutTax()));
                }

                taxDetail.setTotalAmountTax(taxDetail.getTotalAmountTax().add(quotePrice.getTaxAmount()));
                taxDetail.setTotalAmountWithTax(taxDetail.getTotalAmountWithTax().add(quotePrice.getAmountWithTax()));
                taxDetail.setTotalAmountWithoutTax(taxDetail.getTotalAmountWithoutTax().add(quotePrice.getAmountWithoutTax()));

            }
        }

        taxDetail.setVatex(vatex.toString());
        taxDetail.setTaxes(new ArrayList<>(mapTaxesIndexes.values()));

        return mapTaxesIndexes;
    }

    private String getTaxCompositeRate(Tax tax, String country) {
        if (!tax.isComposite() || org.apache.commons.collections.CollectionUtils.isEmpty(tax.getSubTaxes())) {
            return StringUtils.EMPTY;
        } else {
            return getSubTaxesCompositeRate(tax.getSubTaxes(), country);
        }
    }

    private String getSubTaxesCompositeRate(List<Tax> subTaxes, String country) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(subTaxes)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder result = new StringBuilder();

        String prefix = "";
        for (Tax subTax : subTaxes) {
            result.append(prefix);
            prefix = ",";

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(subTax.getSubTaxes())) {
                result.append(getSubTaxesCompositeRate(subTax.getSubTaxes(), country));
            } else {
                if("FR".equalsIgnoreCase(country) || "UK".equalsIgnoreCase(country)) {
                    result.append(new java.text.DecimalFormat("#,##0.00", java.text.DecimalFormatSymbols.getInstance(Locale.FRENCH)).format(subTax.getPercent())).append("%");
                } else {
                    result.append(new java.text.DecimalFormat("#,##0.00", java.text.DecimalFormatSymbols.getInstance(Locale.US)).format(subTax.getPercent())).append("%");
                }
            }
        }
        return result.toString();
    }


    public GetQuoteDtoResponse getQuote(String quoteCode) {
        if(StringUtils.isBlank(quoteCode)) {
            missingParameters.add("quoteCode");
        }
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if(quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);

        return populateToDto(quote,true,true,true);
    }

    public QuoteDTO updateQuote(QuoteDTO quoteDto) {
        String quoteCode = quoteDto.getCode();
        if(StringUtils.isBlank(quoteCode)) {
            missingParameters.add("code");
        }
        
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if(quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);

        if(StringUtils.isNotBlank(quoteDto.getSellerCode())) {
            quote.setSeller(sellerService.findByCode(quoteDto.getSellerCode()));
        }

        BillingAccount applicantAccount = quote.getApplicantAccount();

        //Manage Seller
        Seller seller = null;
        if(StringUtils.isNotBlank(quoteDto.getSellerCode())) {
            seller = sellerService.findByCode(quoteDto.getSellerCode());
            if(seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, quoteDto.getSellerCode());
            }
        } else {
            seller = applicantAccount.getCustomerAccount().getCustomer().getSeller();
            if(seller == null) {
                throw new EntityDoesNotExistsException("No seller found. a seller must be defined either on quote or at customer level");
            }
        }
        quote.setSeller(seller);


        if(StringUtils.isNotBlank(quoteDto.getApplicantAccountCode())) {
            final BillingAccount billingAccount = billingAccountService.findByCode(quoteDto.getApplicantAccountCode());
            if(billingAccount == null)
                throw new EntityDoesNotExistsException(BillingAccount.class, quoteDto.getApplicantAccountCode());
            quote.setApplicantAccount(billingAccount);
        }
        quote.setSendDate(quoteDto.getSendDate());

        
    	if(quoteDto.getDeliveryDate()!=null && quoteDto.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
    	quote.setDeliveryDate(quoteDto.getDeliveryDate());
        
        quote.setQuoteLotDuration(quoteDto.getQuoteLotDuration());
        quote.setOpportunityRef(quoteDto.getOpportunityRef());
        quote.setCustomerRef(quoteDto.getExternalId());
        quote.setValidity(quoteDto.getValidity());
        var allStatus = allStatus(QuoteStatusEnum.class, "cpqQuote.status", "");
        if(!allStatus.contains(quoteDto.getStatus().toLowerCase())) {
			throw new MeveoApiException("Status is invalid, here is the list of available status : " + allStatus);
		}
        quote.setDescription(quoteDto.getDescription());
        quote.setQuoteDate(quote.getQuoteDate());
        if(StringUtils.isNotBlank(quoteDto.getBillableAccountCode())) {
            var billableAccount = billingAccountService.findByCode(quoteDto.getBillableAccountCode());
            if(billableAccount == null)
                quote.setBillableAccount(quote.getApplicantAccount());
            else
                quote.setBillableAccount(billableAccount);
        } else {
            quote.setBillableAccount(quote.getApplicantAccount());
        }
        if(StringUtils.isNotBlank(quoteDto.getUserAccountCode())) {
            UserAccount userAccount = userAccountService.findByCode(quoteDto.getUserAccountCode());
            if(userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, quoteDto.getUserAccountCode());
            }
            
            if(!userAccount.getIsConsumer()) {
                throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Quote for this user account is not allowed.");
            }
            quote.setUserAccount(userAccount);
        }
        
        //Check if the CPQQuote is ACCEPTED then check if the user is admin 
        boolean isAdmin = currentUser.hasRoles(ADMINISTRATION_VISUALIZATION, ADMINISTRATION_MANAGEMENT);
        
        if(QuoteStatusEnum.ACCEPTED.toString().equals(quote.getStatus()) && !isAdmin) {
        	throw new MeveoApiException("The CpqQuote can not be updated if the status is " + quote.getStatus() + " and the user is not admin");
        } else {
        	//Update the sales person name in CpqQuote
        	quote.setSalesPersonName(quoteDto.getSalesPersonName());
        }
        
        try {
            cpqQuoteService.update(quote);
            QuoteVersionDto quoteVersionDto = quoteDto.getQuoteVersion();
            if(quoteVersionDto != null) {
                final QuoteVersion qv = quoteVersionService.findByQuoteAndVersion(quoteCode, quoteVersionDto.getCurrentVersion());
                if(qv == null) {
                    throw new EntityDoesNotExistsException("No quote version with number = " + quoteVersionDto.getCurrentVersion() + " for the quote code = " + quoteCode);
                }
                if(StringUtils.isNotBlank(quoteVersionDto.getShortDescription()))
                    qv.setShortDescription(quoteVersionDto.getShortDescription());

                if (quoteVersionDto.getStartDate() == null) {
                    qv.setStartDate(new Date());
                }
                else {
                    qv.setStartDate(quoteVersionDto.getStartDate());
                }

                if (quoteVersionDto.getEndDate() == null) {
                    GlobalSettings globalSettings = globalSettingsService.findLastOne();
                    Date endDate = null;
                    if (globalSettings != null) {
                        endDate = DateUtils.addDaysToDate(new Date(), globalSettings.getQuoteDefaultValidityDelay());
                    }
                    qv.setEndDate(endDate);
                }
                else {
                    qv.setEndDate(quoteVersionDto.getEndDate());
                }

                if(quoteVersionDto.getStatus() != null) {
                    qv.setStatus(quoteVersionDto.getStatus());
                    qv.setStatusDate(new Date());
                }
                if (StringUtils.isNotBlank(quoteVersionDto.getBillingPlanCode())) {
                    InvoicingPlan invoicingPlan = invoicingPlanService.findByCode(quoteVersionDto.getBillingPlanCode());
                    if (invoicingPlan == null) {
                        throw new EntityDoesNotExistsException(InvoicingPlan.class, quoteVersionDto.getBillingPlanCode());
                    }
                    qv.setInvoicingPlan(invoicingPlan);
                }
                
                if(quoteVersionDto.getDiscountPlanCode() !=null ) {
	                if(StringUtils.isNotBlank(quoteVersionDto.getDiscountPlanCode())) {
		                qv.setDiscountPlan(loadEntityByCode(discountPlanService, quoteVersionDto.getDiscountPlanCode(), DiscountPlan.class));
	                }else{
		                qv.setDiscountPlan(null);
	                }
                }
                
                
                qv.setContract(contractHierarchyHelper.checkContractHierarchy(quote.getBillableAccount(), quoteVersionDto.getContractCode()));
                qv.getMedias().clear();
                if(quoteVersionDto.getMediaCodes() != null) {
                	quoteVersionDto.getMediaCodes().forEach(mediaCode -> {
                		var media = mediaService.findByCode(mediaCode);
                		if(media == null)
                			throw new EntityDoesNotExistsException(Media.class, mediaCode);
                		qv.getMedias().add(media);
                	});
                }
                if(StringUtils.isNotBlank(quoteVersionDto.getComment())) {
       			 qv.setComment(quoteVersionDto.getComment());
       	         }

                if(quoteVersionDto.getPriceListCode() != null) {
                    if(quoteVersionDto.getPriceListCode().isBlank()) {
                       qv.setPriceList(null);
                    } else {
                        PriceList priceList = priceListService.findByCode(quoteVersionDto.getPriceListCode());
                        if(priceList == null) {
                            throw new EntityDoesNotExistsException(PriceList.class, quoteVersionDto.getPriceListCode());
                        }
                        qv.setPriceList(priceList);
                    }
                }

                populateCustomFields(quoteVersionDto.getCustomFields(), qv, false);
                quoteVersionService.update(qv);
                quoteVersionDto = new QuoteVersionDto(qv);
                quoteVersionDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(qv));
                quoteDto.setQuoteVersion(quoteVersionDto);
            }
            
        }catch(BusinessApiException e) {
            throw new MeveoApiException(e);
        }
        return quoteDto;

    }

    private static final String DEFAULT_SORT_ORDER_ID = "id";

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "quoteDto",
            itemPropertiesToFilter = {
                    @FilterProperty(property = "sellerCode", entityClass = Seller.class),
                    @FilterProperty(property = "billableAccountCode", entityClass = BillingAccount.class),
                    @FilterProperty(property = "applicantAccountCode", entityClass = BillingAccount.class),
                    @FilterProperty(property = "contractCode", entityClass = Contract.class)}, totalRecords = "listSize")
    public CpqQuotesListResponseDto findQuotes(PagingAndFiltering pagingAndFiltering) {
        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, PagingAndFiltering.SortOrder.ASCENDING, null, pagingAndFiltering, CpqQuote.class);

        Long totalCount = cpqQuoteService.count(paginationConfiguration);

        CpqQuotesListResponseDto result = new CpqQuotesListResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<CpqQuote> quotes = cpqQuoteService.list(paginationConfiguration);
            if (quotes != null)
                quotes.forEach(c -> {
                    result.getQuotes().getQuoteDtos().add(populateQuoteToDto(c));
                });
        }

        return result;
    }

    public void deleteQuote(String quoteCode) {
        if (StringUtils.isBlank(quoteCode))
            missingParameters.add("quoteCode");
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if (quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
        if (quote.getStatus().equals(QuoteStatusEnum.CANCELLED.toString()) ||
                quote.getStatus().equals(QuoteStatusEnum.REJECTED.toString())) {
            List<QuoteVersion> versions = quoteVersionService.findByQuoteId(quote.getId());
            versions.forEach(qv -> {
                quoteVersionService.remove(qv);
            });
            cpqQuoteService.remove(quote);
        } else {
            throw new MeveoApiException("Impossible to delete the Quote with status : " + quote.getStatus());
        }


        cpqQuoteService.remove(quote);
    }


    private QuoteDTO populateQuoteToDto(CpqQuote quote) {
        final QuoteDTO dto = new QuoteDTO();
        dto.setValidity(quote.getValidity());
        dto.setStatus(quote.getStatus());
        if (quote.getApplicantAccount() != null)
            dto.setApplicantAccountCode(quote.getApplicantAccount().getCode());
        if (quote.getBillableAccount() != null)
            dto.setBillableAccountCode(quote.getBillableAccount().getCode());
        dto.setDeliveryDate(quote.getDeliveryDate());
        dto.setQuoteLotDuration(quote.getQuoteLotDuration());
        dto.setOpportunityRef(quote.getOpportunityRef());
        if (quote.getSeller() != null)
            dto.setSellerCode(quote.getSeller().getCode());
        dto.setSendDate(quote.getSendDate());
        dto.setExternalId(quote.getCustomerRef()); // TODO : not sure if it is the correct field
        dto.setDescription(quote.getDescription());
        dto.setCode(quote.getCode());
        dto.setQuoteNumber(quote.getQuoteNumber());
        dto.setId(quote.getId());
        dto.setStatusDate(quote.getStatusDate());
        dto.setSalesPersonName(quote.getSalesPersonName());
        if (quote.getUserAccount()!= null)
            dto.setUserAccountCode(quote.getUserAccount().getCode());
        dto.setQuoteDate(quote.getQuoteDate());
        return dto;
    }

    private GetQuoteDtoResponse populateToDto(CpqQuote quote, boolean loadQuoteOffers, boolean loadQuoteProduct, boolean loadQuoteAttributes) {
        GetQuoteDtoResponse result = new GetQuoteDtoResponse();
        result.setQuoteDto(populateQuoteToDto(quote));
        final List<QuoteVersion> quoteVersions = quoteVersionService.findByQuoteCode(quote.getCode());
        GetQuoteVersionDtoResponse quoteVersionDto = null;
        for (QuoteVersion version : quoteVersions) {
            quoteVersionDto = new GetQuoteVersionDtoResponse(version, true, true, true,true);
            if(version.getDiscountPlan() != null) {
            	quoteVersionDto.setDiscountPlanCode(version.getDiscountPlan().getCode());
            }
            if (version.getContract() != null) {
            	quoteVersionDto.setContractCode(version.getContract().getCode());
            }
            if (StringUtils.isNotBlank(version.getComment())) {
            	quoteVersionDto.setComment(version.getComment());
            }
            quoteVersionDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(version));
            quoteVersionDto.setPrices(calculateTotalsPerQuote(version, PriceLevelEnum.PRODUCT));
            result.addQuoteVersion(quoteVersionDto);
        }
        return result;
    }


    public QuoteOfferDTO createQuoteItem(QuoteOfferDTO quoteOfferDto) {
        try {

            if (quoteOfferDto.getQuoteVersion() == null)
                missingParameters.add("quoteVersion");
            if (StringUtils.isBlank(quoteOfferDto.getQuoteCode()))
                missingParameters.add("quoteCode");
            if (quoteOfferDto.getOfferId() == null && quoteOfferDto.getOfferCode() == null)
                missingParameters.add("offerId or offerCode");

            handleMissingParameters();
            
            OfferTemplate offerTemplate = null;
            if(quoteOfferDto.getOfferId() != null && quoteOfferDto.getOfferCode() != null) {
            	offerTemplate = offerTemplateService.findById(quoteOfferDto.getOfferId());
            	if(offerTemplate != null && !offerTemplate.getCode().equals(quoteOfferDto.getOfferCode())) {
            		throw new MeveoApiException("The offer ID doesn’t match with the offer CODE, please correct the request");
            	}
            	if (offerTemplate == null)
                    throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDto.getOfferId());
            }else if(quoteOfferDto.getOfferId() != null) {
            	offerTemplate = offerTemplateService.findById(quoteOfferDto.getOfferId());
            	if (offerTemplate == null)
                    throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDto.getOfferId());
            }else if(quoteOfferDto.getOfferCode() != null) {
            	offerTemplate = offerTemplateService.findByCode(quoteOfferDto.getOfferCode());
            	if (offerTemplate == null)
                    throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDto.getOfferCode());
            }
            
            final QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteOfferDto.getQuoteCode(), quoteOfferDto.getQuoteVersion());
            if (quoteVersion == null)
                throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteOfferDto.getQuoteCode() + "," + quoteOfferDto.getQuoteVersion() + ")");
            QuoteOffer quoteOffer = new QuoteOffer();
            quoteOffer.setOfferTemplate(offerTemplate);
            quoteOffer.setQuoteVersion(quoteVersion);
            if (StringUtils.isNotBlank(quoteOfferDto.getBillableAccountCode()))
                quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDto.getBillableAccountCode()));
            if (StringUtils.isNotBlank(quoteOfferDto.getQuoteLotCode()))
                quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDto.getQuoteLotCode()));
            if(StringUtils.isNotBlank(quoteOfferDto.getDiscountPlanCode())) {
            	quoteOffer.setDiscountPlan(discountPlanService.findByCode(quoteOfferDto.getDiscountPlanCode()));
            }
            if(!StringUtils.isBlank(quoteOfferDto.getUserAccountCode())) {
                UserAccount userAccount = userAccountService.findByCode(quoteOfferDto.getUserAccountCode());
                if(userAccount == null) {
                    throw new EntityDoesNotExistsException(UserAccount.class, quoteOfferDto.getUserAccountCode());
                }
                
                if(!userAccount.getIsConsumer()) {
                    throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Quote item for this user account is not allowed.");
                }
                quoteOffer.setUserAccount(userAccount);
            }
            quoteOffer.setSequence(quoteOfferDto.getSequence());
            quoteOffer.setCode(quoteOfferDto.getCode());
            quoteOffer.setDescription(quoteOfferDto.getDescription());
            quoteOffer.setQuoteLineType(quoteOfferDto.getQuoteLineType());
            quoteOffer.setContract(contractHierarchyHelper.checkContractHierarchy(quoteOffer.getBillableAccount(), quoteOfferDto.getContractCode()));
            populateCustomFields(quoteOfferDto.getCustomFields(), quoteOffer, true);
            quoteOfferService.create(quoteOffer);
            quoteOfferDto.setQuoteOfferId(quoteOffer.getId());
            quoteOfferDto.setCode(quoteOffer.getCode());
            quoteOfferDto.setDescription(quoteOffer.getDescription());
            if(quoteOffer.getDiscountPlan() == null){
                resolveOfferDPFromBAIfExist(quoteOffer);
            }
        	if(quoteOfferDto.getDeliveryDate()!=null && quoteOfferDto.getDeliveryDate().before(new Date())) {
        		throw new MeveoApiException("Delivery date should be in the future");	
        	}
        	quoteOffer.setDeliveryDate(quoteOfferDto.getDeliveryDate());
        	
        	if(quoteOfferDto.getQuoteLineType() == OfferLineTypeEnum.AMEND) {
            	if (quoteOfferDto.getSubscriptionCode() == null) {
    				throw new BusinessApiException("Subscription is missing");
    			}
            	List<QuoteOffer> quoteOffers = quoteOfferService.findBySubscriptionAndStatus(quoteOfferDto.getSubscriptionCode(), OfferLineTypeEnum.AMEND);
            	if(!quoteOffers.isEmpty()) {
            		throw new BusinessApiException("Amendement order line already exists on subscription"+quoteOfferDto.getSubscriptionCode());
            	}
            	quoteOffer.setQuoteLineType(OfferLineTypeEnum.AMEND);
            	Subscription subscription = subscriptionService.findByCode(quoteOfferDto.getSubscriptionCode());
            	if(subscription == null) {
            		throw new EntityDoesNotExistsException("Subscription with code "+quoteOfferDto.getSubscriptionCode()+" does not exist");
            	}
            	quoteOffer.setSubscription(subscription);
            }else {
            	quoteOffer.setQuoteLineType(OfferLineTypeEnum.CREATE);
            }
            
            newPopulateProduct(quoteOfferDto, quoteOffer);
            newPopulateOfferAttribute(quoteOfferDto.getOfferAttributes(), quoteOffer);
    
    
            return quoteOfferDto;
        } catch(BusinessException exp){
                throw new BusinessApiException(exp.getMessage());
        }
    }


    public QuoteOfferDTO updateQuoteItem(QuoteOfferDTO quoteOfferDTO) {
        try {
            if (quoteOfferDTO.getQuoteOfferId() == null)
                missingParameters.add("quoteOfferId");
            handleMissingParameters();
            QuoteOffer quoteOffer = quoteOfferService.findById(quoteOfferDTO.getQuoteOfferId());
            if (quoteOffer == null)
                throw new EntityDoesNotExistsException(QuoteOffer.class, quoteOfferDTO.getQuoteOfferId());
            // check offer template if exist
            if (quoteOfferDTO.getOfferId() != null) {
                OfferTemplate offerTemplate = offerTemplateService.findById(quoteOfferDTO.getOfferId());
                if (offerTemplate == null)
                    throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDTO.getOfferId());
                quoteOffer.setOfferTemplate(offerTemplate);
            }

        // check quote version
        if (StringUtils.isNotBlank(quoteOfferDTO.getQuoteCode())) {
            if (quoteOfferDTO.getQuoteVersion() == null)
                missingParameters.add("quoteVersion");
            handleMissingParameters();
            CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteOfferDTO.getQuoteCode());
            if (cpqQuote == null)
                throw new EntityDoesNotExistsException("can not find Quote version with qoute code : " + quoteOfferDTO.getQuoteCode());
            QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteOfferDTO.getQuoteCode(), quoteOfferDTO.getQuoteVersion());
            if (quoteVersion == null)
                throw new EntityDoesNotExistsException("can not find Quote version with qoute code : " + quoteOfferDTO.getQuoteCode() + " and version : " + quoteOfferDTO.getQuoteVersion());
            quoteOffer.setQuoteVersion(quoteVersion);
        }
        if(StringUtils.isNotBlank(quoteOfferDTO.getDiscountPlanCode())) {
        	quoteOffer.setDiscountPlan(discountPlanService.findByCode(quoteOfferDTO.getDiscountPlanCode()));
        }
        if(!StringUtils.isBlank(quoteOfferDTO.getUserAccountCode())) {
            UserAccount userAccount = userAccountService.findByCode(quoteOfferDTO.getUserAccountCode());
            if(userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, quoteOfferDTO.getUserAccountCode());
            }
            
            if(!userAccount.getIsConsumer()) {
                throw new BusinessApiException("UserAccount: " + userAccount.getCode() + " is not a consumer. Quote item for this user account is not allowed.");
            }
            quoteOffer.setUserAccount(userAccount);
        }
        if (StringUtils.isNotBlank(quoteOfferDTO.getBillableAccountCode())){
            quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDTO.getBillableAccountCode()));
        }
        if(quoteOffer.getDiscountPlan() == null){
            resolveOfferDPFromBAIfExist(quoteOffer);
        }
        if (StringUtils.isNotBlank(quoteOfferDTO.getQuoteLotCode()))
            quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDTO.getQuoteLotCode()));
        
    	if(quoteOfferDTO.getDeliveryDate() != null && quoteOfferDTO.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
    	quoteOffer.setDeliveryDate(quoteOfferDTO.getDeliveryDate());
        quoteOffer.setQuoteLineType(quoteOfferDTO.getQuoteLineType());
        
        if(quoteOfferDTO.getQuoteLineType() == OfferLineTypeEnum.AMEND) {
        	if (quoteOfferDTO.getSubscriptionCode() == null) {
				throw new BusinessApiException("Subscription is missing");
			}
        	List<QuoteOffer> orderOffers = quoteOfferService.findBySubscriptionAndStatus(quoteOfferDTO.getSubscriptionCode(), OfferLineTypeEnum.AMEND);
        	if(!orderOffers.isEmpty()) {
        		throw new BusinessApiException("Amendement quote line already exists on subscription"+quoteOfferDTO.getSubscriptionCode());
        	}
        	
        	Subscription subscription = subscriptionService.findByCode(quoteOfferDTO.getSubscriptionCode());
        	if(subscription == null) {
        		throw new EntityDoesNotExistsException("Subscription with code "+quoteOfferDTO.getSubscriptionCode()+" does not exist");
        	}
        	quoteOffer.setSubscription(subscription);
        }
        quoteOffer.setContract(contractHierarchyHelper.checkContractHierarchy(quoteOffer.getBillableAccount(), quoteOfferDTO.getContractCode()));
        processQuoteProduct(quoteOfferDTO, quoteOffer);
        processQuoteAttribute(quoteOfferDTO, quoteOffer);
        populateCustomFields(quoteOfferDTO.getCustomFields(), quoteOffer, false);
        
        quoteOfferService.update(quoteOffer);

            return quoteOfferDTO;
        }catch(BusinessException exp){
            throw new BusinessApiException(exp.getMessage());
        }
    }

    private void resolveOfferDPFromBAIfExist(QuoteOffer quoteOffer) {
        quoteOffer.getQuoteVersion().getQuote().getBillableAccount().getDiscountPlanInstances().stream()
                .map(dpi -> dpi.getDiscountPlan())
                .forEach(dp -> {
                    if(DiscountPlanTypeEnum.OFFER.equals(dp.getDiscountPlanType())){
                        quoteOffer.getOfferTemplate().getAllowedDiscountPlans().stream()
                                .filter(odp -> odp.getId().equals(dp.getId()))
                                .findFirst()
                                .ifPresent(matchedDP -> quoteOffer.setDiscountPlan(matchedDP));
                    }
                });
    }

    private void processQuoteProduct(QuoteOfferDTO quoteOfferDTO, QuoteOffer quoteOffer) {
        var quoteProductDtos = quoteOfferDTO.getProducts();
        var hasQuoteProductDtos = quoteProductDtos != null && !quoteProductDtos.isEmpty();
        
        if(!hasQuoteProductDtos) {
            missingParameters.add("products");
            handleMissingParameters();
        }

        var existencQuoteProducts = quoteOffer.getQuoteProduct();
        var hasExistingQuotes = existencQuoteProducts != null && !existencQuoteProducts.isEmpty();

        var newQuoteProducts = new ArrayList<QuoteProduct>();
        QuoteProduct quoteProduct = null;
        int i = 1;
        for (QuoteProductDTO quoteProductDto : quoteProductDtos) {
            quoteProduct = getQuoteProductFromDto(quoteProductDto, quoteOffer, i);
            newQuoteProducts.add(quoteProduct);
            i++;
        }
        if (!hasExistingQuotes) {
            quoteOffer.getQuoteProduct().addAll(newQuoteProducts);
        } else {
            existencQuoteProducts.retainAll(newQuoteProducts);
            for (QuoteProduct qpNew : newQuoteProducts) {
                int index = existencQuoteProducts.indexOf(qpNew);
                if (index >= 0) {
                    QuoteProduct old = existencQuoteProducts.get(index);
                    old.update(qpNew);
                } else {
                    existencQuoteProducts.add(qpNew);
                }
            }
        }
    }

    private void processQuoteAttribute(QuoteOfferDTO quoteOfferDTO, QuoteOffer quoteOffer) {
        var quoteAttributeDtos = quoteOfferDTO.getOfferAttributes();
        var existencQuoteAttributes = quoteOffer.getQuoteAttributes();
        var hasExistingQuotes = existencQuoteAttributes != null && !existencQuoteAttributes.isEmpty();

        if (quoteAttributeDtos != null && !quoteAttributeDtos.isEmpty()) {
            var newQuoteAttributes = new ArrayList<QuoteAttribute>();
            QuoteAttribute quoteAttribute = null;
            for (QuoteAttributeDTO quoteAttributeDto : quoteAttributeDtos) {
                quoteAttribute = getQuoteAttributeFromDto(quoteAttributeDto, null,quoteOffer);
                newQuoteAttributes.add(quoteAttribute);
            }
            if (!hasExistingQuotes) {
                quoteOffer.getQuoteAttributes().addAll(newQuoteAttributes);
            } else {
                existencQuoteAttributes.retainAll(newQuoteAttributes);
                for (QuoteAttribute qpNew : newQuoteAttributes) {
                    int index = existencQuoteAttributes.indexOf(qpNew);
                    if (index >= 0) {
                        QuoteAttribute old = existencQuoteAttributes.get(index);
                        old.update(qpNew);
                    } else {
                        existencQuoteAttributes.add(qpNew);
                    }
                }
            }
        } else if (hasExistingQuotes) {
            quoteOffer.getQuoteAttributes().removeAll(existencQuoteAttributes);
        }
    }

    private QuoteProduct getQuoteProductFromDto(QuoteProductDTO quoteProductDTO, QuoteOffer quoteOffer, int index) {
        ProductVersion productVersion = productVersionService.findByProductAndVersion(quoteProductDTO.getProductCode(), quoteProductDTO.getProductVersion());
        if (productVersion == null)
            throw new EntityDoesNotExistsException(ProductVersion.class, "products[" + index + "] = " + quoteProductDTO.getProductCode() + "," + quoteProductDTO.getProductVersion());

    	DiscountPlan discountPlan=null;
		if(quoteProductDTO.getDiscountPlanCode()!=null) {
	    discountPlan = discountPlanService.findByCode(quoteProductDTO.getDiscountPlanCode());
		if(discountPlan == null)
			throw new EntityDoesNotExistsException(DiscountPlan.class,quoteProductDTO.getDiscountPlanCode());
		}

		boolean isNew = false;
		QuoteProduct q = null;
		if (quoteProductDTO.getProductCode() != null) {
			q = quoteProductService.findByQuoteAndOfferAndProduct(quoteOffer.getQuoteVersion().getId(), quoteOffer.getCode(), quoteProductDTO.getProductCode());
			isNew = false;
		}
        if (q == null) {
            q = new QuoteProduct();
            isNew = true;
        }

        if(StringUtils.isNotBlank(quoteProductDTO.getQuoteCode())) {
            q.setQuote(cpqQuoteService.findByCode(quoteProductDTO.getQuoteCode()));
        }

        q.setProductVersion(productVersion);
        q.setQuantity(quoteProductDTO.getQuantity());
        
    	if(quoteProductDTO.getDeliveryDate()!=null && quoteProductDTO.getDeliveryDate().before(new Date())) {
    		throw new MeveoApiException("Delivery date should be in the future");	
    	}
    	q.setDeliveryDate(quoteProductDTO.getDeliveryDate());
        
        
        q.setQuoteOffer(quoteOffer);
        q.setQuoteVersion(quoteOffer.getQuoteVersion());
        q.setDiscountPlan(discountPlan);
        if(q.getDiscountPlan() == null){
            resolveProductDPIfExist(quoteOffer, q);
        }
        if(isNew) {
        	populateCustomFields(quoteProductDTO.getCustomFields(), q, true);
            quoteProductService.create(q);
        }else
        	populateCustomFields(quoteProductDTO.getCustomFields(), q, false);
        processQuoteProduct(quoteProductDTO, q);
        return q;
    }

    private void resolveProductDPIfExist(QuoteOffer quoteOffer, QuoteProduct qProduct) {
        quoteOffer.getQuoteVersion().getQuote().getBillableAccount().getDiscountPlanInstances().stream()
                .map(dpi -> dpi.getDiscountPlan())
                .filter(dp -> DiscountPlanTypeEnum.PRODUCT.equals(dp.getDiscountPlanType()))
                .forEach(dp -> qProduct.getProductVersion().getProduct().getDiscountList().stream()
                        .filter(pdp -> pdp != null && pdp.getId().equals(dp.getId()))
                        .findFirst()
                        .ifPresent(matchedDP -> qProduct.setDiscountPlan(matchedDP)));
    }

    private void processQuoteProduct(QuoteProductDTO quoteProductDTO, QuoteProduct q) {
        var quoteAttributeDtos = quoteProductDTO.getProductAttributes();
        var hasQuoteProductDtos = quoteAttributeDtos != null && !quoteAttributeDtos.isEmpty();

        var existencQuoteProducts = q.getQuoteAttributes();
        var hasExistingQuotes = existencQuoteProducts != null && !existencQuoteProducts.isEmpty();

        if(hasQuoteProductDtos) {
            var newQuoteProducts = new ArrayList<QuoteAttribute>();
            QuoteAttribute quoteAttribute = null;
            for (QuoteAttributeDTO quoteAttributeDTO : quoteAttributeDtos) {
                quoteAttribute = getQuoteAttributeFromDto(quoteAttributeDTO, q,null);
                newQuoteProducts.add(quoteAttribute);
            }
            if(!hasExistingQuotes) {
                q.getQuoteAttributes().addAll(newQuoteProducts);
            }else {
                existencQuoteProducts.retainAll(newQuoteProducts);
                for (QuoteAttribute qpNew : newQuoteProducts) {
                    int index = existencQuoteProducts.indexOf(qpNew);
                    if(index >= 0) {
                        QuoteAttribute old = existencQuoteProducts.get(index);
                        old.update(qpNew);
                    }else {
                        existencQuoteProducts.add(qpNew);
                    }
                }
            }
        }else if(hasExistingQuotes){
            q.getQuoteAttributes().removeAll(existencQuoteProducts);
        }
    }
    private QuoteAttribute getQuoteAttributeFromDto(QuoteAttributeDTO quoteAttributeDTO, QuoteProduct quoteProduct,QuoteOffer quoteOffer) {
        Attribute attribute = attributeService.findByCode(quoteAttributeDTO.getQuoteAttributeCode());
        if(attribute == null)
            throw new EntityDoesNotExistsException(Attribute.class, quoteAttributeDTO.getQuoteAttributeCode());
        boolean isNew = false;
        QuoteAttribute quoteAttribute = null;
        if(quoteAttributeDTO.getQuoteAttributeId() != null) {
            quoteAttribute = quoteAttributeService.findById(quoteAttributeDTO.getQuoteAttributeId());

        }
        if(quoteAttribute == null) {
            isNew = true;
            quoteAttribute = new QuoteAttribute();
        }else{
            if(quoteProduct.getId() != quoteAttribute.getQuoteProduct().getId())
                throw new MeveoApiException("Quote Attribute is Already attached to : " + quoteAttribute.getQuoteProduct().getId());
        }
        quoteAttribute.setAttribute(attribute);
        quoteAttribute.setStringValue(quoteAttributeDTO.getStringValue());
        quoteAttribute.setDateValue(quoteAttributeDTO.getDateValue());
        quoteAttribute.setDoubleValue(quoteAttributeDTO.getDoubleValue());
        if (quoteAttribute.getDoubleValue() == null && quoteAttribute.getStringValue() != null) {
            if (org.apache.commons.lang3.math.NumberUtils.isCreatable(quoteAttribute.getStringValue().trim())) {
                quoteAttribute.setDoubleValue(Double.valueOf(quoteAttribute.getStringValue()));
            }
        }
        if(quoteOffer!=null){
        	quoteAttribute.setQuoteOffer(quoteOffer);
        }
        if(quoteProduct!=null) {
        quoteProduct.getQuoteAttributes().add(quoteAttribute);
        quoteAttribute.setQuoteProduct(quoteProduct);
        }
        if(isNew)
            quoteAttributeService.create(quoteAttribute);
		ProductVersion productVersion = null;
		
		if(quoteProduct != null){
			ProductVersionAttribute productVersionAttribute = productVersionAttributeService.findByProductVersionAndAttribute(quoteProduct.getProductVersion().getId(), attribute.getId());
			if(productVersionAttribute != null){
				quoteAttributeDTO.setSequence(productVersionAttribute.getSequence());
			}
		}else if(quoteOffer != null) {
			OfferTemplateAttribute offerTemplateAttribute = offerTemplateAttributeService.findByOfferTemplateAndAttribute(quoteOffer.getOfferTemplate().getId(), attribute.getId());
			if(offerTemplateAttribute != null) {
				quoteAttributeDTO.setSequence(offerTemplateAttribute.getSequence());
			}
		}
        return quoteAttribute;
    }


    public void deleteQuoteVersion(String quoteCode, int quoteVersion) {
        if(StringUtils.isBlank(quoteCode))
            missingParameters.add("quoteCode");
        handleMissingParameters();
        QuoteVersion version = quoteVersionService.findByQuoteAndVersion(quoteCode, quoteVersion);
        if(version == null)
            throw new EntityDoesNotExistsException("quote version is unknown for quote code : " + quoteCode + ", and version : " + quoteVersion);
        quoteVersionService.remove(version);
    }

    public void deleteQuoteItem(Long quoteItemId) {
        if(quoteItemId == null)
            missingParameters.add("quoteItemId");
        handleMissingParameters();
        QuoteOffer quoteOffer = quoteOfferService.findById(quoteItemId);
        if(quoteOffer == null)
            throw new EntityDoesNotExistsException(QuoteOffer.class, quoteItemId);
        quoteOfferService.remove(quoteOffer);
    }

    public void placeOrder(String quoteCode, int version) {
        CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteCode);
        if(cpqQuote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, version);
        validateProducts(quoteVersion);
        if(quoteVersion == null)
            throw new EntityDoesNotExistsException("No quote version found for quote: " + quoteCode + ", and version : " + version);
        if(cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.CANCELLED.toString())
        			|| cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.REJECTED.toString()))
            throw new MeveoApiException("quote status can not be publish because of its current status : " + cpqQuote.getStatus());
        if(!quoteVersion.getStatus().equals(VersionStatusEnum.PUBLISHED))
            throw new MeveoApiException("the current quote version is not published");

        Date now = new Date();
        cpqQuote.setStatus(QuoteStatusEnum.ACCEPTED.toString());
        cpqQuote.setStatusDate(now);
        cpqQuoteService.update(cpqQuote);
        cpqQuoteStatusUpdatedEvent.fire(cpqQuote);

        List<QuoteVersion> versions = quoteVersionService.findByQuoteId(cpqQuote.getId());
        versions.stream().filter(q -> q.getId() != quoteVersion.getId()).forEach(q -> {
            q.setStatus(VersionStatusEnum.CLOSED);
            q.setStatusDate(now);
            quoteVersionService.update(q);

        });

    }


    public CpqQuote duplicateQuote(String quoteCode, int version) {
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if (quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
        final QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, version);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException("No quote version with number = " + version + " for the quote code = " + quoteCode); 
        return cpqQuoteService.duplicate(quote, quoteVersion, false, true);
    }


    public QuoteVersion duplicateQuoteVersion(String quoteCode, int version) {
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if (quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
        final QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, version);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException("No quote version with number = " + version + " for the quote code = " + quoteCode);
        return quoteVersionService.duplicate(quote, quoteVersion);
    }

    public void updateQuoteStatus(String quoteCode, String status) {
        CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteCode);
        if (cpqQuote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);

        if(status.equalsIgnoreCase(cpqQuote.getStatus()))
            return;
        if (cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.REJECTED.toString()) ||
                cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.CANCELLED.toString())) {
            throw new MeveoApiException("you can not update the quote with status = " + cpqQuote.getStatus());
        }
        var allStatus = allStatus(QuoteStatusEnum.class, "cpqQuote.status", "");
		if(!allStatus.contains(status.toLowerCase())) {
				throw new MeveoApiException("Status is invalid, here is the list of available status : " + allStatus);
		}
        cpqQuote.setStatus(status);
        cpqQuote.setStatusDate(new Date());

        if (QuoteStatusEnum.APPROVED.toString().equalsIgnoreCase(status)) {
            if(quoteVersionService.findByQuoteCode(quoteCode).stream()
                    .filter(quoteVersion -> VersionStatusEnum.PUBLISHED.equals(quoteVersion.getStatus()))
                    .findAny().isEmpty()){
                throw new BusinessException("APPROVE a QUOTE is not be possible if at least one QUOTE Version is not published");
            }
            cpqQuote = serviceSingleton.assignCpqQuoteNumber(cpqQuote);
        }
        List<QuoteVersion> quoteVersionsList = quoteVersionService.findLastVersionByCode(cpqQuote.getCode());
        if(!quoteVersionsList.isEmpty()) {
            validateProducts(quoteVersionsList.get(0));
        }
        try {
            cpqQuoteService.update(cpqQuote);
            cpqQuoteStatusUpdatedEvent.fire(cpqQuote);
            if (cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.REJECTED.toString())) {
                List<QuoteVersion> quoteVersions = quoteVersionService.findByQuoteIdAndStatusActive(cpqQuote.getId());                
                for (QuoteVersion quoteVersion: quoteVersions) {
                    quoteVersion.setStatus(VersionStatusEnum.CLOSED);
                    quoteVersion.setStatusDate(new Date());
                    quoteVersionService.update(quoteVersion);
                }
            }
        } catch (BusinessApiException e) {
            throw new MeveoApiException(e);
        }
    }

    public void updateQuoteVersionStatus(String quoteCode, int currentVersion, VersionStatusEnum status) {
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
        if (quoteVersion == null) {
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");
        }

        if(VersionStatusEnum.PUBLISHED.equals(status)) {
            validateProducts(quoteVersion);
        }
        if(quoteVersion.getQuoteOffers().isEmpty()) {
        	throw new MeveoApiException("link an offer to a version before publishing it");
        }
        if (!quoteVersion.getStatus().allowedTargets().contains(status)) {
            throw new MeveoApiException("You can not update the quote version with status = " + quoteVersion.getStatus() + " allowed target status are: " + quoteVersion.getStatus().allowedTargets());
        }
        var quoteVersionPublished = quoteVersionService.findByQuoteIdAndStatusActive(quoteVersion.getQuote().getId());
        var numberQuoteVersionPublished = quoteVersionPublished.stream().filter(qv -> qv.getQuoteVersion().intValue() != currentVersion)
                                                                        .map(qv -> {
                                                                            if(qv.getQuote().getStatus().equalsIgnoreCase(QuoteStatusEnum.REJECTED.toString()) && qv.getStatus().equals(VersionStatusEnum.PUBLISHED)) {
                                                                                    qv.getQuote().setStatus(QuoteStatusEnum.IN_PROGRESS.toString());
                                                                                    return null;
                                                                            }
                                                                            return qv;
                                                                        })
                                                                        .filter(Objects::nonNull)
                                                                        .collect(Collectors.toList()).size();
        if(numberQuoteVersionPublished > 0)
        	throw new MeveoApiException("You already have a published version. Please close it to publish a new one");
        quoteVersion.setStatus(status);
        quoteVersion.setStatusDate(new Date());
        quoteVersionService.update(quoteVersion);
        if (status.equals(VersionStatusEnum.PUBLISHED)){
        	try {
        		quoteQuotation(quoteCode, currentVersion);
        	}catch(IncorrectChargeTemplateException e) {
        		throw new MeveoApiException(e.getMessage());
        	}
        	updateQuoteStatus(quoteCode, QuoteStatusEnum.PENDING.toString());
        }
    }

    private void validateProducts(QuoteVersion quoteVersion) {
        if (quoteVersion.getQuoteProducts() != null) {
            quoteVersion.getQuoteProducts()
                    .stream()
                    .map(QuoteProduct::getProductVersion)
                    .filter(Objects::nonNull)
                    .map(ProductVersion::getProduct)
                    .filter(product -> Objects.nonNull(product) && ProductStatusEnum.CLOSED.equals(product.getStatus()))
                    .findAny()
                    .ifPresent(product -> {
                        throw new BusinessApiException("Can not perform action product status is CLOSED, product code : "
                                + product.getCode());
                    });
        }
    }

    public GetQuoteVersionDtoResponse quoteQuotation(String quoteCode, int currentVersion) {
        List<QuotePrice> accountingArticlePrices = new ArrayList<>();
        List<TaxPricesDto> pricesPerTaxDTO = new ArrayList<>();
        Set<DiscountPlanItem> quoteEligibleFixedDiscountItems = new HashSet<>();
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
        validateProducts(quoteVersion);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");

        clearExistingQuotations(quoteVersion);

        quotePriceService.removeByQuoteVersionAndPriceLevel(quoteVersion, PriceLevelEnum.QUOTE);
        //calculate totalQuoteAttribute
        calculateTotalAttributes (quoteVersion);
        for (QuoteOffer quoteOffer : quoteVersion.getQuoteOffers()) {
            accountingArticlePrices.addAll(offerQuotation(quoteOffer,quoteEligibleFixedDiscountItems));
        } 
        var offerFixedDiscountWalletOperation = discountPlanService.calculateDiscountplanItems(new ArrayList<>(quoteEligibleFixedDiscountItems), quoteVersion.getQuote().getSeller(), quoteVersion.getQuote().getBillableAccount(), new Date(), new BigDecimal(1d), null,
        		null, null, null, null, null, null, true, null, null, DiscountPlanTypeEnum.QUOTE);
        createFixedDiscountQuotePrices(offerFixedDiscountWalletOperation, quoteVersion, null,quoteVersion.getQuote().getBillableAccount(),PriceLevelEnum.QUOTE);
        
        Map<BigDecimal, List<QuotePrice>> pricesPerTaux = accountingArticlePrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getTaxRate));
        
        BigDecimal quoteTotalAmount = BigDecimal.ZERO;

        for (BigDecimal taux: pricesPerTaux.keySet()) {

        	 Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = pricesPerTaux.get(taux).stream()
        			 							.collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));
            log.debug("quoteQuotation pricesPerType size={}",pricesPerType.size());


            List<PriceDTO> prices = pricesPerType
                    .keySet()
                    .stream()
                    .map(key -> reducePrices(key, pricesPerType, quoteVersion, null, PriceLevelEnum.QUOTE))
                    .filter(Optional::isPresent)
                    .map(price -> new PriceDTO(price.get(), new HashMap<>())).collect(Collectors.toList());

            pricesPerTaxDTO.add(new TaxPricesDto(taux, prices));
            quoteTotalAmount=prices.stream().map(o->o.getAmountWithoutTax()).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
//        CpqQuote quote=quoteVersion.getQuote();
//        applyFixedDiscount(quoteVersion.getDiscountPlan(), quoteTotalAmount, quote.getSeller(),
//        		quote.getBillableAccount(), null, null,null, quoteVersion,quote.getQuoteDate());

        //Get the updated quote version and construct the DTO
        QuoteVersion updatedQuoteVersion=quoteVersionService.findById(quoteVersion.getId());
        GetQuoteVersionDtoResponse getQuoteVersionDtoResponse = new GetQuoteVersionDtoResponse(updatedQuoteVersion,true,true,true,true);
        getQuoteVersionDtoResponse.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(updatedQuoteVersion));
        getQuoteVersionDtoResponse.setPrices(calculateTotalsPerQuote(updatedQuoteVersion, PriceLevelEnum.QUOTE));
        return getQuoteVersionDtoResponse;
    }

    private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType,
													    							QuoteVersion quoteVersion,QuoteOffer quoteOffer, PriceLevelEnum level) {
    	log.debug("reducePrices quoteVersion={}, quoteOffer={}, level={}",quoteVersion!=null?quoteVersion.getId():null,quoteOffer!=null?quoteOffer.getId():null,level);
    	if(pricesPerType.get(key).size() == 1){
    		QuotePrice accountingArticlePrice =pricesPerType.get(key).get(0);
    		QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(key);
            quotePrice.setPriceLevelEnum(level);
            quotePrice.setQuoteVersion(quoteVersion!=null?quoteVersion:quoteOffer.getQuoteVersion());
            quotePrice.setQuoteOffer(quoteOffer);
            quotePrice.setTaxAmount(accountingArticlePrice.getTaxAmount());
            quotePrice.setAmountWithTax(accountingArticlePrice.getAmountWithTax());
            quotePrice.setAmountWithoutTax(accountingArticlePrice.getAmountWithoutTax());
            quotePrice.setUnitPriceWithoutTax(accountingArticlePrice.getUnitPriceWithoutTax());
            quotePrice.setTaxRate(accountingArticlePrice.getTaxRate());
            quotePrice.setRecurrenceDuration(accountingArticlePrice.getRecurrenceDuration());
            quotePrice.setRecurrencePeriodicity(accountingArticlePrice.getRecurrencePeriodicity());
            quotePrice.setChargeTemplate(accountingArticlePrice.getChargeTemplate());
            quotePrice.setCurrencyCode(accountingArticlePrice.getCurrencyCode());
		    quotePrice.setQuoteArticleLine(accountingArticlePrice.getQuoteArticleLine());
		    quotePrice.setOverchargedUnitAmountWithoutTax(accountingArticlePrice.getOverchargedUnitAmountWithoutTax());
		    quotePrice.setApplyDiscountsOnOverridenPrice(accountingArticlePrice.getApplyDiscountsOnOverridenPrice());
		    if(PriceLevelEnum.PRODUCT.equals(level)) {
		    	quotePrice.setContractItem(accountingArticlePrice.getContractItem());
		    	quotePrice.setPricePlanMatrixVersion(accountingArticlePrice.getPricePlanMatrixVersion());
		    	quotePrice.setPricePlanMatrixLine(accountingArticlePrice.getPricePlanMatrixLine());
		    }
            if(!PriceLevelEnum.OFFER.equals(level)) {
                quotePriceService.create(quotePrice);
            }
            log.debug("reducePrices1 quotePriceId={}, level={}",quotePrice.getId(),quotePrice.getPriceLevelEnum());
            return Optional.of(quotePrice);
    	}
    	QuotePrice quotePrice = new QuotePrice();
        quotePrice.setPriceTypeEnum(key);
        quotePrice.setPriceLevelEnum(level);
        quotePrice.setQuoteVersion(quoteVersion!=null?quoteVersion:quoteOffer.getQuoteVersion());
        quotePrice.setQuoteOffer(quoteOffer);
    	Optional<QuotePrice> price =  pricesPerType.get(key).stream().reduce((a, b) -> {
            quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
            quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
            quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
            quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
            if(quotePrice.getAmountWithoutTaxWithoutDiscount().compareTo(BigDecimal.ZERO)==0 && a.getDiscountedQuotePrice()==null) {
           	 quotePrice.setAmountWithoutTaxWithoutDiscount(a.getAmountWithoutTax());
           }
		    quotePrice.setQuoteArticleLine(a.getQuoteArticleLine());
			if(a.getOverchargedUnitAmountWithoutTax() != null && b.getOverchargedUnitAmountWithoutTax() != null) {
				quotePrice.setOverchargedUnitAmountWithoutTax(a.getOverchargedUnitAmountWithoutTax().add(b.getOverchargedUnitAmountWithoutTax()));
			}else if(a.getOverchargedUnitAmountWithoutTax() != null) {
				quotePrice.setOverchargedUnitAmountWithoutTax(a.getOverchargedUnitAmountWithoutTax());
			}else if(b.getOverchargedUnitAmountWithoutTax() != null){
				quotePrice.setOverchargedUnitAmountWithoutTax(b.getOverchargedUnitAmountWithoutTax());
			}
		 if(PriceLevelEnum.PRODUCT.equals(level)) {
		    	quotePrice.setContractItem(a.getContractItem());
		    	quotePrice.setPricePlanMatrixVersion(a.getPricePlanMatrixVersion());
		    	quotePrice.setPricePlanMatrixLine(a.getPricePlanMatrixLine());
		    }
       	 if(b.getDiscountedQuotePrice()==null)
            	quotePrice.setAmountWithoutTaxWithoutDiscount(quotePrice.getAmountWithoutTaxWithoutDiscount().add(b.getAmountWithoutTax()));
            quotePrice.setTaxRate(a.getTaxRate());
            quotePrice.setCurrencyCode(a.getCurrencyCode());
            quotePrice.setChargeTemplate(a.getChargeTemplate());
		    quotePrice.setApplyDiscountsOnOverridenPrice(a.getApplyDiscountsOnOverridenPrice());
            if(a.getRecurrenceDuration()!=null) {
            	quotePrice.setRecurrenceDuration(a.getRecurrenceDuration());
            }
            if(a.getRecurrencePeriodicity()!=null) {
            	quotePrice.setRecurrencePeriodicity(a.getRecurrencePeriodicity());
            }
            log.debug("reducePrices2 quotePriceId={}, level={}",quotePrice.getId(),quotePrice.getPriceLevelEnum());

            return quotePrice;
        });
    	if(!PriceLevelEnum.OFFER.equals(level) && price.isPresent()) {
        	 quotePriceService.create(price.get());
        }
        return price;
    }

    public List<QuotePrice> offerQuotation(QuoteOffer quoteOffer, Set<DiscountPlanItem> quoteEligibleFixedDiscountItems) {
    	quotePriceService.removeByQuoteOfferAndPriceLevel(quoteOffer, PriceLevelEnum.OFFER);
        Subscription subscription = instantiateVirtualSubscription(quoteOffer);
        List<PriceDTO> pricesDTO =new ArrayList<>();
        List<QuotePrice> offerQuotePrices = new ArrayList<>();
        List<WalletOperation> walletOperations = quoteRating(subscription,quoteOffer,quoteEligibleFixedDiscountItems, offerQuotePrices,true);
        QuoteArticleLine quoteArticleLine = null;
        Map<String, QuoteArticleLine> quoteArticleLines = new HashMap<>();
		List<QuotePrice> accountingPrices = new ArrayList<>();

//        Map<Long, BigDecimal> quoteProductTotalAmount =new HashMap<Long, BigDecimal>();;
//        for(QuoteArticleLine overrodeLine : quoteOffer.getQuoteVersion().getQuoteArticleLines()){
//            if(overrodeLine.getQuoteProduct().getQuoteOffer().getId().equals(quoteOffer.getId())) {
//                quoteArticleLines.put(overrodeLine.getAccountingArticle().getCode(), quoteArticleLine);
//                quoteProductTotalAmount.put(overrodeLine.getQuoteProduct().getId(), overrodeLine.getQuotePrices().stream().map(QuotePrice::getAmountWithoutTax).reduce(BigDecimal::add).get());
//                productQuotePrices.addAll(overrodeLine.getQuotePrices());
//            }
//
//        }
        String accountingArticleKey  = null;
        clearOfferPrices(quoteOffer); 
        for (WalletOperation wo : walletOperations) {
            accountingArticleKey = wo.getAccountingArticle().getCode() + "_" + wo.getServiceInstance().getQuoteProduct().getId();
            if (!quoteArticleLines.containsKey(accountingArticleKey) || wo.getDiscountPlan() != null ) {
            	quoteArticleLine=createQuoteArticleLine(wo, quoteOffer.getQuoteVersion());
            	quoteArticleLines.put(accountingArticleKey  , quoteArticleLine);
            }else {
                quoteArticleLine=quoteArticleLines.get(accountingArticleKey);
            	var isGroupedBy = quoteArticleLine.getQuoteProduct() != null && 
            	                      wo.getServiceInstance() != null && 
            	                      quoteArticleLine.getQuoteProduct().getId() == wo.getServiceInstance().getId();
            	if(isGroupedBy)
            	    quoteArticleLine.setQuantity(quoteArticleLine.getQuantity().add(wo.getQuantity()));
            	else {
            	    quoteArticleLine=createQuoteArticleLine(wo, quoteOffer.getQuoteVersion());
                    quoteArticleLines.put(accountingArticleKey, quoteArticleLine);
            	}
            }
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(PriceTypeEnum.getPriceTypeEnum(wo.getChargeInstance()));
            quotePrice.setPriceLevelEnum(PriceLevelEnum.PRODUCT);
            quotePrice.setAmountWithoutTax(wo.getAmountWithoutTax());
            quotePrice.setAmountWithTax(wo.getAmountWithTax());
            quotePrice.setTaxAmount(wo.getAmountTax());
            quotePrice.setCurrencyCode(wo.getCurrency() != null ? wo.getCurrency().getCurrencyCode() : null);
            quotePrice.setQuoteArticleLine(quoteArticleLine);
            quotePrice.setQuoteVersion(quoteOffer.getQuoteVersion());
            quotePrice.setQuoteOffer(quoteOffer);
            quotePrice.setQuantity(wo.getQuantity());
            quotePrice.setDiscountPlan(wo.getDiscountPlan());
            quotePrice.setDiscountPlanItem(wo.getDiscountPlanItem());
            quotePrice.setDiscountPlanType(wo.getDiscountPlanType());
            quotePrice.setDiscountValue(wo.getDiscountValue());
            quotePrice.setPriceOverCharged(wo.isOverrodePrice());
            quotePrice.setDiscountedAmount(wo.getDiscountedAmount());
            QuotePrice discounteQuotePrice = quotePriceService.findByUuid(wo.getUuid());
            if (discounteQuotePrice != null) {
                quotePrice.setDiscountedQuotePrice(discounteQuotePrice);
                quotePrice.setSequence(wo.getSequence());
            } else {
                quotePrice.setUuid(wo.getUuid());
            }
            

            ChargeInstance chargeInstance = wo.getChargeInstance();
            quotePrice.setChargeTemplate(chargeInstance != null ? chargeInstance.getChargeTemplate() : null);
            quotePrice.setApplyDiscountsOnOverridenPrice(chargeInstance != null ? chargeInstance.getApplyDiscountsOnOverridenPrice() : Boolean.FALSE);
            quotePrice.setOverchargedUnitAmountWithoutTax(chargeInstance != null ? chargeInstance.getOverchargedUnitAmountWithoutTax() : null);
            
            if (chargeInstance != null && PriceTypeEnum.RECURRING.equals(quotePrice.getPriceTypeEnum())) {
                RecurringChargeTemplate recurringCharge = ((RecurringChargeTemplate) wo.getChargeInstance().getChargeTemplate());

                Long recurrenceDuration = Long.valueOf(getDurationTerminInMonth(recurringCharge.getAttributeDuration(), recurringCharge.getDurationTermInMonth(), quoteOffer, wo.getServiceInstance().getQuoteProduct()));
                quotePrice.setRecurrenceDuration(recurrenceDuration);
                RecurringChargeTemplate recChargeTemplate = (RecurringChargeTemplate) chargeInstance.getChargeTemplate();
                if(recChargeTemplate != null && !StringUtils.isBlank(recChargeTemplate.getCalendarCodeEl())) {
                    Calendar calendarFromEl = recurringRatingService.getCalendarFromEl(recChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), null, recChargeTemplate, (RecurringChargeInstance) chargeInstance);
                    quotePrice.setRecurrencePeriodicity(calendarFromEl != null ? calendarFromEl.getDescription() : null);
                }
                if(StringUtils.isBlank(quotePrice.getRecurrencePeriodicity()) && recChargeTemplate != null && recChargeTemplate.getCalendar() != null){
                    quotePrice.setRecurrencePeriodicity(recChargeTemplate.getCalendar().getDescription());
                }
                overrideAmounts(quotePrice, recurrenceDuration, wo);
            } 
            quotePrice.setUnitPriceWithoutTax(wo.getUnitAmountWithoutTax()!=null?wo.getUnitAmountWithoutTax():wo.getAmountWithoutTax());
            quotePrice.setTaxRate(wo.getTaxPercent());
            quotePrice.setPricePlanMatrixVersion(wo.getPricePlanMatrixVersion());
            quotePrice.setPricePlanMatrixLine(wo.getPricePlanMatrixLine());
            quotePrice.setContractItem(wo.getContractLine());
            quotePriceService.create(quotePrice);
            quotePriceService.getEntityManager().flush();
            quoteArticleLine.getQuotePrices().add(quotePrice);
            quoteArticleLine = quoteArticleLineService.update(quoteArticleLine);
            accountingPrices.add(quotePrice);
        }
        //Calculate totals by offer

        //Calculate totals by offer
        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = accountingPrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        log.debug("offerQuotation pricesPerType size={}",pricesPerType.size());
        pricesDTO = pricesPerType.keySet().stream()
        			.map(key -> reducePrices(key, pricesPerType, null,quoteOffer,PriceLevelEnum.OFFER))
        			.filter(Optional::isPresent)
			        .map(price -> {
			            QuotePrice quotePrice = price.get();
			            quotePriceService.create(quotePrice);
			            offerQuotePrices.add(quotePrice);
			            quoteOffer.getQuotePrices().add(quotePrice);
			            return new PriceDTO(quotePrice, new HashMap<>());
			        })
			        .collect(Collectors.toList());
        return offerQuotePrices;
    }

    private void overrideAmounts(QuotePrice quotePrice, Long recurrenceDuration, WalletOperation walletOperation) {
        BigDecimal coeff = quotePrice.getQuantity().add(walletOperation.getServiceInstance().getQuantity().multiply(BigDecimal.valueOf(recurrenceDuration - 1)));
    	
        quotePrice.setAmountWithTax(quotePrice.getAmountWithTax().divide(quotePrice.getQuantity(),
                appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()).multiply(coeff));
        quotePrice.setAmountWithoutTax(quotePrice.getAmountWithoutTax().divide(quotePrice.getQuantity(),
                appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()).multiply(coeff));
        quotePrice.setAmountWithoutTaxWithoutDiscount(quotePrice.getAmountWithoutTaxWithoutDiscount() != null ?
                quotePrice.getAmountWithoutTaxWithoutDiscount().divide(quotePrice.getQuantity(),
                        appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()).multiply(coeff) : null);
        quotePrice.setTaxAmount(quotePrice.getTaxAmount() != null ?
                quotePrice.getTaxAmount().divide(quotePrice.getQuantity(),
                        appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()).multiply(coeff) : null);
    }

    private Integer getDurationTerminInMonth(Attribute durationOrQuantityAttribute, Integer defaultValue, QuoteOffer quoteOffer, QuoteProduct quoteProduct) {
        Integer durationTermInMonth = null;
        if (durationOrQuantityAttribute != null &&
                (durationOrQuantityAttribute.getAttributeType() == AttributeTypeEnum.NUMERIC ||
                durationOrQuantityAttribute.getAttributeType() == AttributeTypeEnum.LIST_TEXT ||
                durationOrQuantityAttribute.getAttributeType() == AttributeTypeEnum.LIST_NUMERIC ||
                durationOrQuantityAttribute.getAttributeType() == AttributeTypeEnum.INTEGER)) {
            Optional<QuoteAttribute> offerQuoteAttribute = quoteOffer.getQuoteAttributes()
                    .stream()
                    .filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(durationOrQuantityAttribute.getCode()))
                    .findAny();
            if(offerQuoteAttribute.isPresent()){
                durationTermInMonth = getDurationTermInMonth(offerQuoteAttribute);
            }
            Optional<QuoteAttribute> productQuoteAttribute = quoteProduct.getQuoteAttributes()
                    .stream()
                    .filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(durationOrQuantityAttribute.getCode()))
                    .findAny();
            if(productQuoteAttribute.isPresent()) {
                durationTermInMonth = getDurationTermInMonth(productQuoteAttribute);
            }
        } else if(quoteOffer.getOfferTemplate().getSubscriptionRenewal() != null) {
            durationTermInMonth = quoteOffer.getOfferTemplate().getSubscriptionRenewal().getInitialyActiveFor();
        }
        return durationTermInMonth != null ? durationTermInMonth : defaultValue != null ? defaultValue : 1;
    }

    private Integer getDurationTermInMonth(Optional<QuoteAttribute> offerQuoteAttribute) {
        Object value = offerQuoteAttribute.get().getAttribute().getAttributeType().getValue(offerQuoteAttribute.get());
        if(value instanceof String && !((String) value).isEmpty()){
            return Integer.parseInt((String) value);
        } else if(value instanceof Double){
            return new BigDecimal((Double) value).intValue();
        }
        return null;
    }

    private void clearExistingQuotations(QuoteVersion quoteVersion) {
        if (quoteVersion.getQuoteArticleLines() != null) {
            List<QuoteArticleLine> articleToRemove = quoteVersion.getQuoteArticleLines()
                    .stream()
                    .filter(article -> article.getQuotePrices().stream().noneMatch(price -> BooleanUtils.isTrue(price.getPriceOverCharged())))
                    .collect(Collectors.toList());
            quoteVersion.getQuoteArticleLines().removeAll(articleToRemove);
            articleToRemove.forEach(article -> article.setQuoteVersion(null));
            quoteVersionService.update(quoteVersion);
            quoteVersionService.commit();
        }
    }

    
    private void clearOfferPrices(QuoteOffer quoteOffer) {
    	QuoteVersion quoteVersion=quoteOffer.getQuoteVersion();
        if (quoteVersion.getQuoteArticleLines() != null) {
            List<QuoteArticleLine> articleToRemove = quoteVersion.getQuoteArticleLines()
                    .stream()
                    .filter(article -> article.getQuoteProduct().getQuoteOffer().getId()==quoteOffer.getId())
                    .collect(Collectors.toList());
            quoteVersion.getQuoteArticleLines().removeAll(articleToRemove);
            quoteVersionService.update(quoteVersion);
        }
    }
    
    @SuppressWarnings("unused")
    public List<WalletOperation> quoteRating(Subscription subscription, QuoteOffer quoteOffer, Set<DiscountPlanItem> quoteEligibleFixedDiscountItems,List<QuotePrice> offerQuotePrices,boolean isVirtual) throws BusinessException {

        List<WalletOperation> walletOperations = new ArrayList<>();
        Set<DiscountPlanItem> productEligibleFixedDiscountItems;
        Set<DiscountPlanItem> offerEligibleFixedDiscountItems = new HashSet<>();
        BillingAccount billingAccount;
        AccountingArticle usageArticle;
        
        Map<AccountingArticle, List<QuoteArticleLine>> overrodeArticle = quoteOffer.getQuoteVersion().getQuoteArticleLines()
                .stream()
                .filter(articleLine -> articleLine.getQuoteProduct().getQuoteOffer().getId().equals(quoteOffer.getId()))
                .collect(Collectors.groupingBy(QuoteArticleLine::getAccountingArticle));
        
        
        if (subscription != null) {

            billingAccount = subscription.getUserAccount().getBillingAccount();
            Map<String, Object> attributes = new HashMap<>();
            
            // Add Service charges
            Double edrQuantity = 0d;
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
            	
                 productEligibleFixedDiscountItems = new HashSet<>();
            	Set<AttributeValue> attributeValues = serviceInstance.getAttributeInstances()
                        .stream()
                        .map(attributeInstance -> attributeInstanceService.getAttributeValue(attributeInstance,serviceInstance, subscription))
                        .collect(Collectors.toSet());
            	
            	
                for (AttributeValue attributeValue : attributeValues) {
                    Attribute attribute = attributeValue.getAttribute();
                    Object value = attribute.getAttributeType().getValue(attributeValue);
                    if (value != null) {
                        attributes.put(attributeValue.getAttribute().getCode(), value);
                    }
                }
                Optional<AccountingArticle> accountingArticle = Optional.empty();
                 var errorMsg="No accounting article found for product code: " + serviceInstance.getProductVersion().getProduct().getCode() +" and attributes: " + attributes.toString();
              // Add subscription charges
                 for (OneShotChargeInstance subscriptionCharge : serviceInstance.getSubscriptionChargeInstances()) {
                     try {
                    	 AccountingArticle subscriptionChargeArticle = accountingArticleService.getAccountingArticle(serviceInstance.getProductVersion().getProduct(), subscriptionCharge.getChargeTemplate(), subscription.getOffer(), attributes,null)
                                 .orElseThrow(() -> new BusinessException(errorMsg + " and charge " + subscriptionCharge.getChargeTemplate()));
                    	 
                         if (overrodeArticle.keySet().contains(subscriptionChargeArticle)) {
                             QuoteArticleLine quoteArticleLine = overrodeArticle.get(subscriptionChargeArticle).get(0);
                             subscriptionCharge.setAmountWithoutTax(quoteArticleLine.getQuotePrices().get(0).getUnitPriceWithoutTax());
                             subscriptionCharge.setAmountWithTax(quoteArticleLine.getQuotePrices().get(0).getAmountWithTax().divide(quoteArticleLine.getQuantity(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
                             subscriptionCharge.setApplyDiscountsOnOverridenPrice(quoteArticleLine.getQuotePrices().get(0).getApplyDiscountsOnOverridenPrice());
                             subscriptionCharge.setOverchargedUnitAmountWithoutTax(quoteArticleLine.getQuotePrices().get(0).getOverchargedUnitAmountWithoutTax());
                         }
                         if (subscriptionCharge.getSeller() == null) {
                             setChargeSeller(quoteOffer, subscriptionCharge);
                         }
                         RatingResult ratingResult = oneShotChargeInstanceService.applyOneShotChargeVirtual(subscriptionCharge, serviceInstance.getSubscriptionDate(), serviceInstance.getQuantity());
                         if (ratingResult != null) {
                             walletOperations.addAll(ratingResult.getWalletOperations());
                             productEligibleFixedDiscountItems.addAll(ratingResult.getEligibleFixedDiscountItems());
                         }


                     } catch (RatingException e) {
                         log.trace("Failed to apply a subscription charge {}: {}", subscriptionCharge,
                                 e.getRejectionReason());
                         throw new BusinessException("Failed to apply a subscription charge {}: {}"+subscriptionCharge.getCode(),e); // e.getBusinessException();

                     }
                 }

                // Add recurring charges
                for (RecurringChargeInstance recurringCharge : serviceInstance.getRecurringChargeInstances()) {
                    try {
                    	AccountingArticle recurringArticle = accountingArticleService.getAccountingArticle(serviceInstance.getProductVersion().getProduct(), recurringCharge.getChargeTemplate(), subscription.getOffer(), attributes, null)
                                .orElseThrow(() -> new BusinessException(errorMsg + " and charge " + recurringCharge.getChargeTemplate()));
                    	
                        if (overrodeArticle.keySet().contains(recurringArticle)) {
                            QuoteArticleLine quoteArticleLine = overrodeArticle.get(recurringArticle).get(0);
                            recurringCharge.setAmountWithoutTax(quoteArticleLine.getQuotePrices().get(0).getUnitPriceWithoutTax());
                            recurringCharge.setAmountWithTax(quoteArticleLine.getQuotePrices().get(0).getAmountWithTax().divide(quoteArticleLine.getQuantity(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
                            recurringCharge.setApplyDiscountsOnOverridenPrice(quoteArticleLine.getQuotePrices().get(0).getApplyDiscountsOnOverridenPrice());
                            recurringCharge.setOverchargedUnitAmountWithoutTax(quoteArticleLine.getQuotePrices().get(0).getOverchargedUnitAmountWithoutTax());
                        }
                        Date nextApplicationDate = walletOperationService.getRecurringPeriodEndDate(recurringCharge, recurringCharge.getSubscriptionDate());
                        if (recurringCharge.getSeller() == null) {
                            setChargeSeller(quoteOffer, recurringCharge);
                        }
                        RatingResult ratingResult = recurringChargeInstanceService
                                .applyRecurringCharge(recurringCharge, nextApplicationDate, false, true, null);
                        if (ratingResult != null && !ratingResult.getWalletOperations().isEmpty()) {
                            walletOperations.addAll(ratingResult.getWalletOperations());
                        }

                    }catch (NoTaxException e) {
                        throw new MeveoApiException(e.getMessage());
                    } catch (RatingException e) {
                        log.trace("Failed to apply a recurring charge {}: {}", recurringCharge, e.getRejectionReason());
                        throw new BusinessException("Failed to apply a subscription charge {}: {}"+recurringCharge.getCode(),e); // e.getBusinessException();
                    }
                }
                // Add usage charges
                EDR edr = null;
				boolean quantityFound=false;
				for (UsageChargeInstance usageCharge : serviceInstance.getUsageChargeInstances()) {
					if (!walletOperationService.ignoreChargeTemplate(usageCharge)) {
						UsageChargeTemplate chargetemplate = (UsageChargeTemplate) usageCharge.getChargeTemplate();
						if (overrodeArticle != null && !overrodeArticle.isEmpty()) {
							usageArticle = accountingArticleService.getAccountingArticleByChargeInstance(usageCharge);
							if (usageArticle == null)
								throw new BusinessException(errorMsg + " and charge " + usageCharge.getChargeTemplate());
							if (overrodeArticle.keySet().contains(usageArticle)) {
								log.info("Usage quotation : usageArticle={}", usageArticle.getCode());
								QuoteArticleLine quoteArticleLine = overrodeArticle.get(usageArticle).get(0);
								usageCharge.setAmountWithoutTax(quoteArticleLine.getQuotePrices().get(0).getUnitPriceWithoutTax());
								usageCharge.setAmountWithTax(quoteArticleLine.getQuotePrices().get(0).getAmountWithTax()
										.divide(quoteArticleLine.getQuantity(), BaseEntity.NB_DECIMALS,	RoundingMode.HALF_UP));
								usageCharge.setApplyDiscountsOnOverridenPrice(quoteArticleLine.getQuotePrices().get(0).getApplyDiscountsOnOverridenPrice());
								usageCharge.setOverchargedUnitAmountWithoutTax(quoteArticleLine.getQuotePrices().get(0).getOverchargedUnitAmountWithoutTax());
								log.info("Usage quotation : usageCharge amountWTax={}", usageCharge.getAmountWithoutTax());
							}
						}
						if (usageCharge.getSeller() == null) {
                            setChargeSeller(quoteOffer, usageCharge);
                        }
						if (!quantityFound && chargetemplate.getUsageQuantityAttribute() != null) {
							Object quantityValue = attributes.get(chargetemplate.getUsageQuantityAttribute().getCode());
							if (quantityValue != null && quantityValue instanceof String) {
								try {
									edrQuantity = Double.parseDouble(quantityValue.toString());
								} catch (NumberFormatException exp) {
									log.warn("The following parameters are required or contain invalid values: The attribute {} for the usage charge {}",
                                            chargetemplate.getUsageQuantityAttribute().getCode(), usageCharge.getCode());
								}
							} else if (quantityValue != null && quantityValue instanceof Double) {
								edrQuantity = (Double) quantityValue;
							} else {
                                log.warn("The following parameters are required or contain invalid values: The attribute {} for the usage charge {} ",
                                        chargetemplate.getUsageQuantityAttribute().getCode(), usageCharge.getCode());
							}
							if (edrQuantity > 0) {
								quantityFound=true;
							}
							
						}
						
					}
				}
			
			if(subscription.getOffer() != null && !subscription.getOffer().isGenerateQuoteEdrPerProduct()) {
		        createEDR(edrQuantity, subscription, attributes, walletOperations);
			}
			
			//applicable only for oneshot other	
            walletOperations.addAll(discountPlanService.calculateDiscountplanItems(new ArrayList<>(productEligibleFixedDiscountItems), subscription.getSeller(), subscription.getUserAccount().getBillingAccount(), new Date(), serviceInstance.getQuantity(), null,
					serviceInstance.getCode(), subscription.getUserAccount().getWallet(), subscription.getOffer(), serviceInstance, subscription, serviceInstance.getCode(), false, null, null, DiscountPlanTypeEnum.PRODUCT));
        
            offerEligibleFixedDiscountItems.addAll(productEligibleFixedDiscountItems);
        }
        
        if(subscription.getOffer() != null && subscription.getOffer().isGenerateQuoteEdrPerProduct()) {
        createEDR(edrQuantity, subscription, attributes, walletOperations);
            }

        var offerFixedDiscountWalletOperation = discountPlanService.calculateDiscountplanItems(new ArrayList<>(offerEligibleFixedDiscountItems), subscription.getSeller(), subscription.getUserAccount().getBillingAccount(), new Date(), new BigDecimal(1d), null,
        		subscription.getOffer().getCode(), subscription.getUserAccount().getWallet(), subscription.getOffer(), null, subscription, subscription.getOffer().getDescription(), true, null, null, DiscountPlanTypeEnum.OFFER);
        offerQuotePrices.addAll(createFixedDiscountQuotePrices(offerFixedDiscountWalletOperation, quoteOffer.getQuoteVersion(), quoteOffer,billingAccount,PriceLevelEnum.OFFER));

    }
        List<WalletOperation>  sortedWalletOperations = walletOperations.stream()
        		  .filter(w->w.getDiscountPlan()==null)
        		  .collect(Collectors.toList());
         sortedWalletOperations.addAll(walletOperations.stream()
          		  .filter(w->w.getDiscountPlan()!=null)
             		  .collect(Collectors.toList()));
    
    quoteEligibleFixedDiscountItems.addAll(offerEligibleFixedDiscountItems);
    return sortedWalletOperations;
}
    private void setChargeSeller(QuoteOffer quoteOffer, ChargeInstance chargeInstance) {
        chargeInstance.setSeller(quoteOffer.getQuoteVersion() != null &&
                quoteOffer.getQuoteVersion().getQuote() != null ? quoteOffer.getQuoteVersion().getQuote().getSeller() : null);
    }
    private void createEDR(Double edrQuantity, Subscription subscription, Map<String, Object> attributes, List<WalletOperation> walletOperations) {
    	if (edrQuantity != null && edrQuantity>0) {
			EDR edr = new EDR();
			try {

				edr.setAccessCode(null);
				edr.setEventDate(new Date());
				edr.setSubscription(subscription);
				edr.setStatus(EDRStatusEnum.OPEN);
				edr.setCreated(new Date());
				edr.setOriginBatch("QUOTE");
				edr.setOriginRecord(System.currentTimeMillis() + "");
				edr.setQuantity(new BigDecimal(edrQuantity));
				Object param1 = attributes.get("EDR_text_parameter_1");
				if (param1 != null) {
					edr.setParameter1(param1.toString());
				}
				Object param2 = attributes.get("EDR_text_parameter_2");
				if (param2 != null) {
					edr.setParameter2(param2.toString());
				}
				Object param3 = attributes.get("EDR_text_parameter_3");
				if (param3 != null) {
					edr.setParameter3(param3.toString());
				}
				RatingResult localRatingResult = usageRatingService.rateVirtualEDR(edr);
				List<WalletOperation> walletOperationsFromEdr = localRatingResult.getWalletOperations();
				log.debug("walletOperationsFromEdr count={}",walletOperationsFromEdr.size());
				if (walletOperationsFromEdr != null) {
					for (WalletOperation walletOperation : walletOperationsFromEdr) {
						log.debug("walletOperationsFromEdr code={},UnitAmountWithoutTax={}",walletOperation.getCode(),walletOperation.getUnitAmountWithoutTax());
						if ((walletOperation.getUnitAmountWithoutTax() != null )
								|| (walletOperation.getUnitAmountWithTax() != null))
						walletOperations.add(walletOperation);
					}
				} 

			} catch (RatingException e) {
				log.error("Quotation : Failed to rate EDR {}: {}", edr, e.getRejectionReason());
				throw new BusinessApiException(e.getMessage());

			} catch (BusinessException e) {
				log.error("Quotation : Failed to rate EDR {}: {}", edr, e.getMessage(), e);
				throw new BusinessApiException(e.getMessage());

			}
		}
    }
    private List<QuotePrice> createFixedDiscountQuotePrices( List<WalletOperation> fixedDiscountWalletOperation,QuoteVersion quoteVersion, QuoteOffer quoteOffer,BillingAccount billingAccount,PriceLevelEnum priceLevelEnum) {
    	List<QuotePrice> discountQuotePrices=new ArrayList<>();
    	for (WalletOperation wo : fixedDiscountWalletOperation) {
    	   QuotePrice discountQuotePrice = new QuotePrice();
           discountQuotePrice.setPriceLevelEnum(priceLevelEnum);
           discountQuotePrice.setPriceTypeEnum(PriceTypeEnum.ONE_SHOT_OTHER);
           final AccountingArticle accountingArticle = wo.getAccountingArticle();
           if (accountingArticle != null && accountingArticle.getTaxClass() != null) {
        	   final TaxInfo taxInfo = taxMappingService.determineTax(wo);
        	   if(taxInfo != null)
        		   discountQuotePrice.setTaxRate(taxInfo.tax.getPercent());
           }
           
           discountQuotePrice.setQuoteArticleLine(createQuoteArticleLine(wo, quoteVersion));
           discountQuotePrice.setAmountWithoutTax(wo.getAmountWithoutTax());
           discountQuotePrice.setAmountWithTax(wo.getAmountWithTax());
           discountQuotePrice.setUnitPriceWithoutTax(wo.getUnitAmountWithoutTax());
           discountQuotePrice.setTaxAmount(wo.getAmountTax());
           discountQuotePrice.setCurrencyCode(wo.getCurrency() != null ? wo.getCurrency().getCurrencyCode() : null);
           discountQuotePrice.setQuoteVersion(quoteVersion);
           discountQuotePrice.setQuoteOffer(quoteOffer);
           discountQuotePrice.setQuantity(wo.getQuantity());
           discountQuotePrice.setDiscountPlan(wo.getDiscountPlan());
           discountQuotePrice.setDiscountPlanItem(wo.getDiscountPlanItem());
           discountQuotePrice.setDiscountPlanType(wo.getDiscountPlanType());
           discountQuotePrice.setDiscountValue(wo.getDiscountValue());
           quotePriceService.create(discountQuotePrice);
           discountQuotePrices.add(discountQuotePrice);
       }
        return discountQuotePrices;
    }
 
    private  QuoteArticleLine createQuoteArticleLine(WalletOperation wo,QuoteVersion quoteVersion) {
        QuoteArticleLine quoteArticleLine = new QuoteArticleLine();
        quoteArticleLine.setAccountingArticle(wo.getAccountingArticle());
        quoteArticleLine.setQuantity(wo.getQuantity());
        quoteArticleLine.setServiceQuantity(wo.getInputQuantity());
        quoteArticleLine.setBillableAccount(wo.getBillingAccount());
        if(wo.getServiceInstance()!=null) {
        	  quoteArticleLine.setQuoteProduct(wo.getServiceInstance().getQuoteProduct());
              wo.getServiceInstance().getQuoteProduct().getQuoteArticleLines().add(quoteArticleLine);
        }
        quoteArticleLine.setQuoteVersion(quoteVersion);
        quoteArticleLineService.create(quoteArticleLine);
        return quoteArticleLine;
    }

    private Subscription instantiateVirtualSubscription(QuoteOffer quoteOffer) {


        String subscriptionCode = UUID.randomUUID().toString();

        Subscription subscription = new Subscription();
        subscription.setCode(subscriptionCode);
        BillingAccount billableAccount=quoteOffer.getBillableAccount()!=null? quoteOffer.getBillableAccount():quoteOffer.getQuoteVersion().getQuote().getBillableAccount();
        Seller seller =billableAccount.getCustomerAccount().getCustomer().getSeller();
        subscription.setSeller(seller);

        subscription.setOffer(quoteOffer.getOfferTemplate());
        subscription.setSubscriptionDate(getSubscriptionDeliveryDate(quoteOffer.getQuoteVersion().getQuote(), quoteOffer));
        subscription.setEndAgreementDate(null);

        if (billableAccount.getUsersAccounts().isEmpty())
            throw new MeveoApiException("Billing account: " + billableAccount.getCode() + " has no user accounts");
        subscription.setUserAccount(billableAccount.getUsersAccounts().get(0));
//
//        String terminationReasonCode = null;
//
//        Date terminationDate = null;
//
//        if (terminationDate == null && terminationReasonCode != null) {
//            throw new MissingParameterException("terminationDate");
//        } else if (terminationDate != null && terminationReasonCode == null) {
//            throw new MissingParameterException("terminationReason");
//        }
//
//        if (terminationReasonCode != null) {
//            subscription.setTerminationDate(terminationDate);
//
//            SubscriptionTerminationReason terminationReason = terminationReasonService.findByCode(terminationReasonCode);
//            if (terminationReason != null) {
//                subscription.setSubscriptionTerminationReason(terminationReason);
//            } else {
//                throw new InvalidParameterException("terminationReason", terminationReasonCode);
//            }
//        }

        //TODO: add discountplan instance for quoteOffer
        if(quoteOffer.getDiscountPlan() != null) {
        	createDiscountPlanInstance(quoteOffer.getDiscountPlan(), subscription,null);
        }
        if(quoteOffer.getQuoteVersion().getDiscountPlan() != null) {
        	createDiscountPlanInstance(quoteOffer.getQuoteVersion().getDiscountPlan(), subscription,null);
        }
        if(quoteOffer.getOfferTemplate().getAllowedDiscountPlans() != null) {
        	quoteOffer.getOfferTemplate().getAllowedDiscountPlans().stream().filter(DiscountPlan::isAutomaticApplication).forEach(dp -> createDiscountPlanInstance(dp, subscription,null));
        }
        subscription.setContract(quoteOffer.getContract() != null ? quoteOffer.getContract() : quoteOffer.getQuoteVersion().getContract() != null ? quoteOffer.getQuoteVersion().getContract() : null);

        subscription.setPriceList(quoteOffer.getQuoteVersion().getPriceList());
        
        // instantiate and activate services
        processProducts(subscription, quoteOffer.getQuoteProduct());

        return subscription;
    }

    private void createDiscountPlanInstance(DiscountPlan discountPlan,Subscription subscription, ServiceInstance serviceInstance) {
		if (discountPlan != null) {
			DiscountPlanInstance dpi = new DiscountPlanInstance();
			dpi.setDiscountPlan(discountPlan);
			dpi.copyEffectivityDates(discountPlan);
			dpi.setDiscountPlanInstanceStatus(discountPlan);
			dpi.setCfValues(discountPlan.getCfValues());
            // this method is called only in the quote case, and in the quote neither the subscription nor the service is created
            // so we can not attach the unsaved transient instance to session one.
            /*
			dpi.setSubscription(subscription);
			dpi.setServiceInstance(serviceInstance);

			if (serviceInstance != null) {
				dpi.assignEntityToDiscountPlanInstances(serviceInstance);
				serviceInstance.getDiscountPlanInstances().add(dpi);
			} else {
				dpi.assignEntityToDiscountPlanInstances(subscription);
				subscription.getDiscountPlanInstances().add(dpi);
			}
			**/
		}
    				
    }
    private void processProducts(Subscription subscription, List<QuoteProduct> products) {
    	
        for (QuoteProduct quoteProduct : products) {
            commercialRuleHeaderService.processProductReplacementRule(quoteProduct);
            Product product = quoteProduct.getProductVersion().getProduct();
            String productCode = product.getCode();

            if (StringUtils.isBlank(productCode)) {
                throw new MissingParameterException("serviceCode");
            }

            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setCode(productCode);
            serviceInstance.setQuantity(quoteProduct.getQuantity());
            serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate());
            serviceInstance.setEndAgreementDate(subscription.getEndAgreementDate());
            serviceInstance.setRateUntilDate(subscription.getEndAgreementDate());
            serviceInstance.setQuoteProduct(quoteProduct);
            serviceInstance.setProductVersion(quoteProduct.getProductVersion());
            if (serviceInstance.getTerminationDate() == null && subscription.getTerminationDate() != null) {
                serviceInstance.setTerminationDate(subscription.getTerminationDate());
                serviceInstance.setSubscriptionTerminationReason(subscription.getSubscriptionTerminationReason());
            }

            serviceInstance.setSubscription(subscription);
            if(PriceVersionDateSettingEnum.QUOTE.equals(product.getPriceVersionDateSetting())) {
            	CpqQuote cpqQuote = quoteProduct.getQuote();
            	if(cpqQuote != null) {
            		cpqQuote = cpqQuoteService.refreshOrRetrieve(cpqQuote);
                	serviceInstance.setPriceVersionDate(cpqQuote.getQuoteDate());
            	}
            }

            createDiscountPlanInstance(quoteProduct.getDiscountPlan(), subscription, serviceInstance);

            AttributeInstance attributeInstance = null;
            for (QuoteAttribute quoteAttribute : quoteProduct.getQuoteAttributes()) {
                attributeInstance = new AttributeInstance(quoteAttribute);
                attributeInstance.setServiceInstance(serviceInstance);
                serviceInstance.addAttributeInstance(attributeInstance);
            }
            serviceInstanceService.cpqServiceInstanciation(serviceInstance, product,null, null, true);
            
            List<SubscriptionChargeInstance> oneShotCharges = serviceInstance.getSubscriptionChargeInstances();
            for (SubscriptionChargeInstance oneShotChargeInstance : oneShotCharges) {
                oneShotChargeInstance.setQuantity(serviceInstance.getQuantity());
                oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
            }

            List<RecurringChargeInstance> recurringChargeInstances = serviceInstance.getRecurringChargeInstances();
            for (RecurringChargeInstance recurringChargeInstance : recurringChargeInstances) {
                recurringChargeInstance.setSubscriptionDate(serviceInstance.getSubscriptionDate());
                recurringChargeInstance.setQuantity(serviceInstance.getQuantity());
                recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            }
            List<UsageChargeInstance> usageChargeInstances = serviceInstance.getUsageChargeInstances();
            for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {
            	usageChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
                usageChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            }
        }
    }

    public List<QuoteOfferDTO> findQuoteOffer(String quoteCode, int version) {
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, version);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException("No Quote verion found for quote code= " + quoteCode + " and version = " + version);
        return quoteOfferService.findByQuoteVersion(quoteVersion).stream().map(qo -> {
        	QuoteOfferDTO dto = new QuoteOfferDTO(qo);
        	dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(qo));
        	return dto;
        }).collect(Collectors.toList());
    }

    public QuoteOfferDTO findById(Long quoteItemId) {
    	QuoteOffer offer = quoteOfferService.findById(quoteItemId);
    	if(offer == null)
    		throw new EntityDoesNotExistsException(QuoteOffer.class, quoteItemId);
    	QuoteOfferDTO dto = new QuoteOfferDTO(offer, true, true,true, new HashMap<>());
    	dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(offer));
    	return dto;
    }

    public byte[] generateQuotePDF(String quoteCode, int currentVersion,boolean generatePdfIfNotExist)
            throws MissingParameterException, EntityDoesNotExistsException, Exception {
        log.debug("getPdfQuote  quoteCode:{}", quoteCode);

        if (StringUtils.isBlank(quoteCode)) {
            missingParameters.add("code");
        }
        handleMissingParameters();
        CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if (quote == null) {
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode, "Code");
        }
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
       if (generatePdfIfNotExist) {
            	cpqQuoteService.produceQuotePdf(quoteVersion);
            }

        return cpqQuoteService.getQuotePdf(quoteVersion);

    }

    public static Object getAttributeValue(QuoteProduct quoteProduct, Attribute attribute) {
    	Optional<QuoteAttribute> quoteAttribute=null;
    	if(!quoteProduct.getQuoteAttributes().isEmpty())
    			quoteAttribute=quoteProduct.getQuoteAttributes().stream().filter(qt -> qt.getAttribute().getCode().equals(attribute.getCode())).findFirst();

    	if(attribute.getAttributeType()!=null) {
    		switch (attribute.getAttributeType()) {
			case TOTAL :
			case COUNT :
			case NUMERIC :
			case INTEGER:
				return quoteAttribute.get().getDoubleValue();
			case LIST_MULTIPLE_TEXT:
			case LIST_TEXT:
			case EXPRESSION_LANGUAGE :
			case TEXT:
				return quoteAttribute.get().getStringValue();
			case DATE:
				return quoteAttribute.get().getDateValue();
			default:
				break;
			}
    	}
    	return null;
    }
    
    public Date getSubscriptionDeliveryDate(CpqQuote quote, QuoteOffer offer) {
		if (offer.getDeliveryDate() != null) {
			return offer.getDeliveryDate();
		}else if (quote.getDeliveryDate() != null) {
			return quote.getDeliveryDate();
		}else {
			return quote.getQuoteDate();
		}
	}
    public void calculateTotalAttributes (QuoteVersion quoteVersion) {
    	List<QuoteAttribute>totalQuoteAttributes=quoteAttributeService.findByQuoteVersionAndTotaltype(quoteVersion.getId());
    	log.info("totalQuoteAttributes size{}",totalQuoteAttributes.size());
    	Double sumTotalAttribute=0.0;
    	Double totalSum=0.0;
    	for(QuoteAttribute quoteAttribute: totalQuoteAttributes) {
    		sumTotalAttribute=0.0; 
    	for(Attribute attribute : quoteAttribute.getAttribute().getAssignedAttributes()) { 
    	    totalSum=quoteAttributeService.getSumDoubleByVersionAndAttribute(quoteVersion.getId(),attribute.getId());
    		
    		if(totalSum!=null) {
                sumTotalAttribute = Double.sum(totalSum, sumTotalAttribute);
    		}

        }
    	log.info("sumTotalAttribute={}",sumTotalAttribute);
    	quoteAttribute.setDoubleValue(sumTotalAttribute);
    	quoteAttribute.setStringValue(sumTotalAttribute+"");
    	quoteAttributeService.update(quoteAttribute);
    	}


    	}
    
	private List<TaxPricesDto> calculateTotalsPerQuote(QuoteVersion quoteVersion, PriceLevelEnum priceLevelEnum) {
		log.debug("calculateTotalsPerQuote1 quotePrices size={}",quoteVersion.getQuotePrices().size());
		List<QuotePrice> quotePrices =quotePriceService.loadByQuoteVersionAndPriceLevel(quoteVersion, priceLevelEnum);
		log.debug("calculateTotalsPerQuote quotePrices size={}",quotePrices.size());
		List<TaxPricesDto> taxPricesDtos =new ArrayList<>();
		Map<BigDecimal, List<QuotePrice>> pricesPerTax = quotePrices.stream()
				.collect(Collectors.groupingBy(QuotePrice::getTaxRate));


		for (BigDecimal taxRate : pricesPerTax.keySet() ) {
			
			List<QuotePrice> quotePricesPerTax= pricesPerTax.get(taxRate);
			log.debug("calculateTotalsPerQuote taxRate={}, quotePricesPerTax size={}",taxRate,quotePricesPerTax.size());
			List<PriceDTO> taxPrices = quotePricesPerTax.stream()
					.map(price -> new PriceDTO(price, new HashMap<>()))
					.collect(Collectors.toList());

			taxPricesDtos.add(new TaxPricesDto(taxRate, taxPrices));
		}
		return taxPricesDtos;
	}

	 public void overridePrices(OverrideChargedPricesDto overrodPricesDto) {
	        overrodPricesDto.getPrices()
	                .forEach(overrodePrice -> {
	                    List<QuotePrice> quotePrices = quotePriceService.loadByQuoteOfferAndArticleCodeAndPriceLevel(overrodePrice.getOfferId(), overrodePrice.getAccountingArticleCode());
		                quotePrices = quotePrices.stream().filter(qp -> qp.getDiscountedQuotePrice() == null).collect(Collectors.toList());
	                    if(!quotePrices.isEmpty()) {
	                    	  BigDecimal unitPriceWithoutTax = overrodePrice.getUnitAmountWithoutTax().divide(BigDecimal.valueOf(quotePrices.size()), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());

	                          quotePrices.forEach(quotePrice -> {
	                        	  if( quotePrice.getOverchargedUnitAmountWithoutTax()==null) {
	                            	  quotePrice.setOverchargedUnitAmountWithoutTax(quotePrice.getUnitPriceWithoutTax());
	                              }
	                        	  BigDecimal quantity = quotePrice.getUnitPriceWithoutTax().compareTo(BigDecimal.ZERO)!=0? 
	                        			  quotePrice.getAmountWithoutTax().divide(quotePrice.getUnitPriceWithoutTax(),appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()):BigDecimal.ZERO;
	                              quotePrice.setUnitPriceWithoutTax(unitPriceWithoutTax);
	                              quotePrice.setAmountWithoutTax(unitPriceWithoutTax.multiply(quantity));
	                              quotePrice.setTaxAmount(quotePrice.getAmountWithoutTax().multiply(quotePrice.getTaxRate().divide(BigDecimal.valueOf(100))));
	                              quotePrice.setAmountWithTax(quotePrice.getAmountWithoutTax().add(quotePrice.getTaxAmount()));
	                             
	                              quotePrice.setApplyDiscountsOnOverridenPrice(overrodePrice.getApplyDiscountsOnOverridenPrice());
	                              if (overrodePrice.getPriceOverCharged() == null)
	                                  quotePrice.setPriceOverCharged(true);
	                              else
	                                  quotePrice.setPriceOverCharged(overrodePrice.getPriceOverCharged());
	                              quotePriceService.update(quotePrice);
	                          });
	                    }
	                  
	                });
	    }

}
