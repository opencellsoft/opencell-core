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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.print.attribute.standard.Media;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.api.dto.cpq.ProductContextDTO;
import org.meveo.api.dto.cpq.QuoteAttributeDTO;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
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
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.StatusUpdated;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.MediaService;
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
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.quote.QuoteOfferService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
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
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    CustomFieldTemplateService customFieldTemplateService;

    @Inject
    OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    WalletOperationService walletOperationService;

    @Inject
    AccountingArticleService accountingArticleService;

    @Inject
    QuoteArticleLineService quoteArticleLineService;

    @Inject
    QuotePriceService quotePriceService;

    @Inject
    private ScriptInstanceService scriptInstanceService;


    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private DiscountPlanItemService discountPlanItemService;

    @Inject
    private QuoteMapper quoteMapper;

    @Inject
    private XmlQuoteFormatter quoteFormatter;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private CommercialRuleHeaderService commercialRuleHeaderService;

    @Inject
    @StatusUpdated
    protected Event<CpqQuote> cpqQuoteStatusUpdatedEvent;
    
    @Inject
    private MediaService mediaService;
    

    @Inject
    UsageChargeInstanceService usageChargeInstanceService;
    
    @Inject
    UsageRatingService usageRatingService;



	public QuoteDTO createQuote(QuoteDTO quote) {
	    if(Strings.isEmpty(quote.getApplicantAccountCode())) {
            missingParameters.add("applicantAccountCode");
        }
        if(Strings.isEmpty(quote.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();
        if(cpqQuoteService.findByCode(quote.getCode()) != null) {
            throw new EntityAlreadyExistsException(CpqQuote.class, quote.getCode());
        }

        CpqQuote cpqQuote = new CpqQuote();
        cpqQuote.setCode(quote.getCode());
        final BillingAccount applicantAccount = billingAccountService.findByCode(quote.getApplicantAccountCode());
        if(applicantAccount == null)
            throw new EntityDoesNotExistsException(BillingAccount.class, quote.getApplicantAccountCode());
        cpqQuote.setApplicantAccount(applicantAccount);
        if(!Strings.isEmpty(quote.getSellerCode())) {
            cpqQuote.setSeller(sellerService.findByCode(quote.getSellerCode()));
        }else {
        	 cpqQuote.setSeller(applicantAccount.getCustomerAccount().getCustomer().getSeller());
        }
        if(!Strings.isEmpty(quote.getBillableAccountCode())) {
            var billableAccount = billingAccountService.findByCode(quote.getBillableAccountCode());
            if(billableAccount == null)
                cpqQuote.setBillableAccount(applicantAccount);
            else
                cpqQuote.setBillableAccount(billableAccount);

        }else
            cpqQuote.setBillableAccount(applicantAccount);

        cpqQuote.setStatusDate(Calendar.getInstance().getTime());
        cpqQuote.setSendDate(quote.getSendDate());
        cpqQuote.setQuoteLotDateBegin(quote.getQuoteLotDateBegin());
        cpqQuote.setQuoteLotDuration(quote.getQuoteLotDuration());
        cpqQuote.setOpportunityRef(quote.getOpportunityRef());
        cpqQuote.setCustomerRef(quote.getExternalId());
        cpqQuote.setValidity(quote.getValidity());
        cpqQuote.setDescription(quote.getDescription());
        cpqQuote.setQuoteDate(quote.getQuoteDate());
        cpqQuote.setOrderInvoiceType(invoiceTypeService.getDefaultQuote());
        
        try {
            cpqQuoteService.create(cpqQuote);
            quoteVersionService.create(populateNewQuoteVersion(quote.getQuoteVersion(), cpqQuote));
        }catch(BusinessApiException e) {
            throw new MeveoApiException(e);
        }
        quote.setStatusDate(cpqQuote.getStatusDate());
        quote.setId(cpqQuote.getId());
        return quote;
	}

	private QuoteVersion populateNewQuoteVersion(QuoteVersionDto quoteVersionDto, CpqQuote cpqQuote) {
		QuoteVersion quoteVersion = new QuoteVersion();
		quoteVersion.setStatusDate(Calendar.getInstance().getTime());
		quoteVersion.setQuoteVersion(1);
		quoteVersion.setStatus(VersionStatusEnum.DRAFT);
		if(quoteVersionDto != null) {
			if(!StringUtils.isBlank(quoteVersionDto.getBillingPlanCode())) {

    			InvoicingPlan invoicingPlan= invoicingPlanService.findByCode(quoteVersionDto.getBillingPlanCode());
    			if (invoicingPlan == null) {
    				throw new EntityDoesNotExistsException(InvoicingPlan.class, quoteVersionDto.getBillingPlanCode());
    			}
    			quoteVersion.setInvoicingPlan(invoicingPlan);
                }
			quoteVersion.setStartDate(quoteVersionDto.getStartDate());
			quoteVersion.setStartDate(quoteVersionDto.getStartDate());
			quoteVersion.setEndDate(quoteVersionDto.getEndDate());
			quoteVersion.setShortDescription(quoteVersionDto.getShortDescription());
			 if(!Strings.isEmpty(quoteVersionDto.getDiscountPlanCode())) {
				 quoteVersion.setDiscountPlan(loadEntityByCode(discountPlanService, quoteVersionDto.getDiscountPlanCode(), DiscountPlan.class));
		        }
			 if(!Strings.isEmpty(quoteVersionDto.getContractCode())) {
				 quoteVersion.setContract(contractService.findByCode(quoteVersionDto.getContractCode()));
		        }
			 if(quoteVersionDto.getMediaCodes() != null) {
				 quoteVersionDto.getMediaCodes().forEach(mediaCode -> {
		        		var media = mediaService.findByCode(mediaCode);
		        		if(media == null)
		        			throw new EntityDoesNotExistsException(Media.class, mediaCode);
		        		quoteVersion.getMedias().add(media);
		        	});
		        }
		}
		quoteVersion.setQuote(cpqQuote);
		populateCustomFields(quoteVersionDto.getCustomFields(), quoteVersion, true);
		return quoteVersion;
	}

	public GetQuoteVersionDtoResponse createQuoteVersion(QuoteVersionDto quoteVersionDto) {
		if(Strings.isEmpty(quoteVersionDto.getQuoteCode()))
			missingParameters.add("quoteCode");
		final CpqQuote quote = cpqQuoteService.findByCode(quoteVersionDto.getQuoteCode());
		if(quote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteVersionDto.getQuoteCode());
		final QuoteVersion quoteVersion = populateNewQuoteVersion(quoteVersionDto, quote);
		try {
            populateCustomFields(quoteVersionDto.getCustomFields(), quoteVersion, true);
            quoteVersionService.create(quoteVersion);
        } catch (BusinessApiException e) {
            throw new MeveoApiException(e);
        }
        return new GetQuoteVersionDtoResponse(quoteVersion, entityToDtoConverter.getCustomFieldsDTO(quoteVersion));
    }


    private void newPopulateProduct(QuoteOfferDTO quoteOfferDto, QuoteOffer quoteOffer) {
        List<QuoteProductDTO> quoteProductDtos = quoteOfferDto.getProducts();
        if (quoteProductDtos != null) {
            int index = 1;
            quoteOffer.getQuoteProduct().size();
            for (QuoteProductDTO quoteProductDTO : quoteProductDtos) {
                if (Strings.isEmpty(quoteProductDTO.getProductCode()))
                    missingParameters.add("products[" + index + "].productCode");
                if (quoteProductDTO.getProductVersion() == null)
                    missingParameters.add("products[" + index + "].productVersion");

                handleMissingParameters();

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
                quoteProduct.setQuote(quoteOffer.getQuoteVersion() != null ? quoteOffer.getQuoteVersion().getQuote() : null);
                quoteProduct.setQuoteVersion(quoteOffer.getQuoteVersion());
                populateCustomFields(quoteProductDTO.getCustomFields(), quoteProduct, true);
                quoteProductService.create(quoteProduct);
                newPopulateQuoteAttribute(quoteProductDTO.getProductAttributes(), quoteProduct);
                quoteOffer.getQuoteProduct().add(quoteProduct);
                List<CommercialRuleHeader> commercialRuleHeader = productVersion.getProduct().getCommercialRuleHeader();
                ProductContextDTO productContextDTO = new ProductContextDTO();
                productContextDTO.setProductCode(productVersion.getProduct().getCode());
                LinkedHashMap<String, Object> selectedAttributes = new LinkedHashMap<>();
                quoteProductDTO.getProductAttributes()
                        .stream()
                        .forEach(productAttribute -> selectedAttributes.put(productAttribute.getQuoteAttributeCode(), productAttribute.getStringValue()));
                productContextDTO.setSelectedAttributes(selectedAttributes);
                List<QuoteAttribute> offerQuoteAttribute = quoteOffer.getQuoteAttributes();
                //processReplacementRules(commercialRuleHeader, productContextDTO, offerQuoteAttribute);
                ++index;
            }
        }
    }

    private void processReplacementRules(List<CommercialRuleHeader> commercialRules, ProductContextDTO productContextDTO, List<QuoteAttribute> offerQuoteAttribute) {
        commercialRules.stream()
                .filter(r -> RuleTypeEnum.REPLACEMENT.equals(r.getRuleType()))
                .forEach(
                        rule -> commercialRuleHeaderService.replacementProcess(rule, List.of(productContextDTO))
                );

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
        	List<Attribute> productAttributes = quoteProduct.getProductVersion().getAttributes();
            quoteProduct.getQuoteAttributes().clear();
            quoteAttributeDTOS.stream()
                    .map(quoteAttributeDTO -> createQuoteAttribute(quoteAttributeDTO, quoteProduct, productAttributes))
                    .collect(Collectors.toList())
                    .forEach(quoteAttribute -> quoteAttributeService.create(quoteAttribute));
        }
    }

    private QuoteAttribute createQuoteAttribute(QuoteAttributeDTO quoteAttributeDTO, QuoteProduct quoteProduct, List<Attribute> productAttributes) {
        if (Strings.isEmpty(quoteAttributeDTO.getQuoteAttributeCode()))
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
            ScriptInstance scriptInstance = invoiceType.getCustomInvoiceXmlScriptInstance();
            if (scriptInstance != null) {
                    String quoteXmlScript = scriptInstance.getCode();
                    ScriptInterface script = scriptInstanceService.getScriptInstance(quoteXmlScript);
                    Map<String, Object> methodContext = new HashMap<String, Object>();
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
                String quoteXml = quoteFormatter.format(quoteMapper.map(quoteVersion));
                xmlContent = quoteXml.getBytes(StandardCharsets.UTF_8);
                result.setXmlContent(xmlContent);
            }
            String fileName = cpqQuoteService.generateFileName(quoteVersion);
            quoteVersion.setXmlFilename(fileName);
            String xmlFilename = quoteXmlDir.getAbsolutePath() + File.separator + fileName + ".xml";
            Files.write(Paths.get(xmlFilename), xmlContent, StandardOpenOption.CREATE);
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


    public GetQuoteDtoResponse getQuote(String quoteCode) {
        if(Strings.isEmpty(quoteCode)) {
            missingParameters.add("quoteCode");
        }
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if(quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);

        return populateToDto(quote,true,true,true);
    }

    public QuoteDTO updateQuote(QuoteDTO quoteDto) {
        String quoteCode = quoteDto.getCode();
        if(Strings.isEmpty(quoteCode)) {
            missingParameters.add("code");
        }
        final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
        if(quote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);

        if(!Strings.isEmpty(quoteDto.getSellerCode())) {
            quote.setSeller(sellerService.findByCode(quoteDto.getSellerCode()));
        }
        if(!Strings.isEmpty(quoteDto.getApplicantAccountCode())) {
            final BillingAccount billingAccount = billingAccountService.findByCode(quoteDto.getApplicantAccountCode());
            if(billingAccount == null)
                throw new EntityDoesNotExistsException(BillingAccount.class, quoteDto.getApplicantAccountCode());
            quote.setApplicantAccount(billingAccount);
        }
        quote.setSendDate(quoteDto.getSendDate());

        quote.setQuoteLotDateBegin(quoteDto.getQuoteLotDateBegin());
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
        if(!Strings.isEmpty(quoteDto.getBillableAccountCode())) {
            var billableAccount = billingAccountService.findByCode(quoteDto.getBillableAccountCode());
            if(billableAccount == null)
                quote.setBillableAccount(quote.getApplicantAccount());
            else
                quote.setBillableAccount(billableAccount);
        }else
            quote.setBillableAccount(quote.getApplicantAccount());
        
        try {
            cpqQuoteService.update(quote);
            QuoteVersionDto quoteVersionDto = quoteDto.getQuoteVersion();
            if(quoteVersionDto != null) {
                final QuoteVersion qv = quoteVersionService.findByQuoteAndVersion(quoteCode, quoteVersionDto.getCurrentVersion());
                if(qv != null) {
                    if(!Strings.isEmpty(quoteVersionDto.getShortDescription()))
                        qv.setShortDescription(quoteVersionDto.getShortDescription());
                    if(quoteVersionDto.getStartDate() != null)
                        qv.setStartDate(quoteVersionDto.getStartDate());
                    if(quoteVersionDto.getEndDate() != null)
                        qv.setEndDate(quoteVersionDto.getEndDate());
                    if(quoteVersionDto.getStatus() != null) {
                        qv.setStatus(quoteVersionDto.getStatus());
                        qv.setStatusDate(Calendar.getInstance().getTime());
                    }
                    if (!StringUtils.isBlank(quoteVersionDto.getBillingPlanCode())) {
                        InvoicingPlan invoicingPlan = invoicingPlanService.findByCode(quoteVersionDto.getBillingPlanCode());
                        if (invoicingPlan == null) {
                            throw new EntityDoesNotExistsException(InvoicingPlan.class, quoteVersionDto.getBillingPlanCode());
                        }
                        qv.setInvoicingPlan(invoicingPlan);
                    }
                    if(!Strings.isEmpty(quoteVersionDto.getDiscountPlanCode())) {
                        qv.setDiscountPlan(loadEntityByCode(discountPlanService, quoteVersionDto.getDiscountPlanCode(), DiscountPlan.class));
                    }
                    if(!Strings.isEmpty(quoteVersionDto.getContractCode())) {
       				 qv.setContract(contractService.findByCode(quoteVersionDto.getContractCode()));
       		        }
                    qv.getMedias().clear();
                    if(quoteVersionDto.getMediaCodes() != null) {
                    	quoteVersionDto.getMediaCodes().forEach(mediaCode -> {
                    		var media = mediaService.findByCode(mediaCode);
                    		if(media == null)
                    			throw new EntityDoesNotExistsException(Media.class, mediaCode);
                    		qv.getMedias().add(media);
                    	});
                    }
                    populateCustomFields(quoteVersionDto.getCustomFields(), qv, false);
                    quoteVersionService.update(qv);
                    quoteVersionDto = new QuoteVersionDto(qv);
                    quoteVersionDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(qv));
                    quoteDto.setQuoteVersion(quoteVersionDto);
                }else {
                    throw new EntityDoesNotExistsException("No quote version with number = " + quoteVersionDto.getCurrentVersion() + " for the quote code = " + quoteCode);
                }
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

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, CpqQuote.class);

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
        if (Strings.isEmpty(quoteCode))
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

    private GetQuoteDtoResponse populateToDto(CpqQuote quote) {
        return populateToDto(quote, false, false, false);
    }

    private QuoteDTO populateQuoteToDto(CpqQuote quote) {
        final QuoteDTO dto = new QuoteDTO();
        dto.setValidity(quote.getValidity());
        dto.setStatus(quote.getStatus());
        if (quote.getApplicantAccount() != null)
            dto.setApplicantAccountCode(quote.getApplicantAccount().getCode());
        if (quote.getBillableAccount() != null)
            dto.setBillableAccountCode(quote.getBillableAccount().getCode());
        dto.setQuoteLotDateBegin(quote.getQuoteLotDateBegin());
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
            quoteVersionDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(version));
            result.addQuoteVersion(quoteVersionDto);
        }
        return result;
    }


    public QuoteOfferDTO createQuoteItem(QuoteOfferDTO quoteOfferDto) {
        try {

            if (quoteOfferDto.getQuoteVersion() == null)
                missingParameters.add("quoteVersion");
            if (Strings.isEmpty(quoteOfferDto.getQuoteCode()))
                missingParameters.add("quoteCode");
            if (quoteOfferDto.getOfferId() == null)
                missingParameters.add("offerId");

            handleMissingParameters();

            OfferTemplate offerTemplate = offerTemplateService.findById(quoteOfferDto.getOfferId());
            if (offerTemplate == null)
                throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDto.getOfferId());
            final QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteOfferDto.getQuoteCode(), quoteOfferDto.getQuoteVersion());
            if (quoteVersion == null)
                throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteOfferDto.getQuoteCode() + "," + quoteOfferDto.getQuoteVersion() + ")");
            QuoteOffer quoteOffer = new QuoteOffer();
            quoteOffer.setOfferTemplate(offerTemplate);
            quoteOffer.setQuoteVersion(quoteVersion);
            if (!Strings.isEmpty(quoteOfferDto.getBillableAccountCode()))
                quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDto.getBillableAccountCode()));
            if (!Strings.isEmpty(quoteOfferDto.getQuoteLotCode()))
                quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDto.getQuoteLotCode()));
//		quoteOffer.setSequence(quoteOfferDto.gets); // no sequence found in quoteOfferDto
            if (!Strings.isEmpty(quoteOfferDto.getDiscountPlanCode())) {
                quoteOffer.setDiscountPlan(discountPlanService.findByCode(quoteOfferDto.getDiscountPlanCode()));
            }
            quoteOffer.setSequence(quoteOfferDto.getSequence());
            quoteOffer.setCode(quoteOfferDto.getCode());
            quoteOffer.setDescription(quoteOfferDto.getDescription());
            populateCustomFields(quoteOfferDto.getCustomFields(), quoteOffer, true);
            quoteOfferService.create(quoteOffer);
            quoteOfferDto.setQuoteOfferId(quoteOffer.getId());
            quoteOfferDto.setCode(quoteOffer.getCode());
            quoteOfferDto.setDescription(quoteOffer.getDescription());
            newPopulateProduct(quoteOfferDto, quoteOffer);
            newPopulateOfferAttribute(quoteOfferDto.getOfferAttributes(), quoteOffer);


            return quoteOfferDto;
        }catch(BusinessException exp){
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
            if (!Strings.isEmpty(quoteOfferDTO.getQuoteCode())) {
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
            if (!Strings.isEmpty(quoteOfferDTO.getDiscountPlanCode())) {
                quoteOffer.setDiscountPlan(discountPlanService.findByCode(quoteOfferDTO.getDiscountPlanCode()));
            }
            if (!Strings.isEmpty(quoteOfferDTO.getBillableAccountCode()))
                quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDTO.getBillableAccountCode()));
            if (!Strings.isEmpty(quoteOfferDTO.getQuoteLotCode()))
                quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDTO.getQuoteLotCode()));
            processQuoteProduct(quoteOfferDTO, quoteOffer);
            processQuoteAttribute(quoteOfferDTO, quoteOffer);
            populateCustomFields(quoteOfferDTO.getCustomFields(), quoteOffer, false);
            quoteOfferService.update(quoteOffer);

            return quoteOfferDTO;
        }catch(BusinessException exp){
            throw new BusinessApiException(exp.getMessage());
        }
    }

    private void processQuoteProduct(QuoteOfferDTO quoteOfferDTO, QuoteOffer quoteOffer) {
        var quoteProductDtos = quoteOfferDTO.getProducts();
        var hasQuoteProductDtos = quoteProductDtos != null && !quoteProductDtos.isEmpty();

        var existencQuoteProducts = quoteOffer.getQuoteProduct();
        var hasExistingQuotes = existencQuoteProducts != null && !existencQuoteProducts.isEmpty();

        if (hasQuoteProductDtos) {
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
        } else if (hasExistingQuotes) {
            quoteOffer.getQuoteProduct().removeAll(existencQuoteProducts);
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
        if (quoteProductDTO.getQuoteProductId() != null) {
            q = quoteProductService.findById(quoteProductDTO.getQuoteProductId());
            isNew = false;
        }
        if (q == null) {
            q = new QuoteProduct();
            isNew = true;
        }

        if(!Strings.isEmpty(quoteProductDTO.getQuoteCode())) {
            q.setQuote(cpqQuoteService.findByCode(quoteProductDTO.getQuoteCode()));
        }

        q.setProductVersion(productVersion);
        q.setQuantity(quoteProductDTO.getQuantity());
        q.setQuoteOffer(quoteOffer);
        q.setQuoteVersion(quoteOffer.getQuoteVersion());
        q.setDiscountPlan(discountPlan);
        if(isNew) {
        	populateCustomFields(quoteProductDTO.getCustomFields(), q, true);
            quoteProductService.create(q);
        }else
        	populateCustomFields(quoteProductDTO.getCustomFields(), q, false);
        processQuoteProduct(quoteProductDTO, q);
        return q;
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
        if(quoteOffer!=null){
        	quoteAttribute.setQuoteOffer(quoteOffer);
        }
        if(quoteProduct!=null) {
        quoteProduct.getQuoteAttributes().add(quoteAttribute);
        quoteAttribute.setQuoteProduct(quoteProduct);
        }
        if(isNew)
            quoteAttributeService.create(quoteAttribute);
        return quoteAttribute;
    }


    public void deleteQuoteVersion(String quoteCode, int quoteVersion) {
        if(Strings.isEmpty(quoteCode))
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
        if(quoteVersion == null)
            throw new EntityDoesNotExistsException("No quote version found for quote: " + quoteCode + ", and version : " + version);
        if(cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.CANCELLED.toString())
        			|| cpqQuote.getStatus().equalsIgnoreCase(QuoteStatusEnum.REJECTED.toString()))
            throw new MeveoApiException("quote status can not be publish because of its current status : " + cpqQuote.getStatus());
        if(!quoteVersion.getStatus().equals(VersionStatusEnum.PUBLISHED))
            throw new MeveoApiException("the current quote version is not published");

        Date now = Calendar.getInstance().getTime();
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
        cpqQuote.setStatusDate(Calendar.getInstance().getTime());

        if (QuoteStatusEnum.APPROVED.toString().equalsIgnoreCase(status) && StringUtils.isBlank(cpqQuote.getQuoteNumber())) {
            cpqQuote = serviceSingleton.assignCpqQuoteNumber(cpqQuote);
        }
        try {
            cpqQuoteService.update(cpqQuote);
            cpqQuoteStatusUpdatedEvent.fire(cpqQuote);
        } catch (BusinessApiException e) {
            throw new MeveoApiException(e);
        }
    }

    public void updateQuoteVersionStatus(String quoteCode, int currentVersion, VersionStatusEnum status) {
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");
        if(quoteVersion.getQuoteOffers().isEmpty()) {
        	throw new MeveoApiException("link an offer to a version before publishing it");
        }
        if (!quoteVersion.getStatus().allowedTargets().contains(status)) {
            throw new MeveoApiException("You can not update the quote version with status = " + quoteVersion.getStatus() + " allowed target status are: " + quoteVersion.getStatus().allowedTargets());
        }
        var quoteVersionPublished = quoteVersionService.findByQuoteIdAndStatusActive(quoteVersion.getQuote().getId());
        var numberQuoteVersionPublished = quoteVersionPublished.stream().filter(qv -> qv.getQuoteVersion().intValue() != currentVersion).collect(Collectors.toList()).size();
        if(numberQuoteVersionPublished > 0)
        	throw new MeveoApiException("There are already publish version.One Version can be published per Quote!!");
        quoteVersion.setStatus(status);
        quoteVersion.setStatusDate(Calendar.getInstance().getTime());
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

    public GetQuoteVersionDtoResponse quoteQuotation(String quoteCode, int currentVersion) {
        List<QuotePrice> accountingArticlePrices = new ArrayList<>();
        List<TaxPricesDto> pricesPerTaxDTO = new ArrayList<>();
         QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");

        clearExistingQuotations(quoteVersion);

        quotePriceService.removeByQuoteVersionAndPriceLevel(quoteVersion, PriceLevelEnum.QUOTE);

        for (QuoteOffer quoteOffer : quoteVersion.getQuoteOffers()) {
            accountingArticlePrices.addAll(offerQuotation(quoteOffer));
        }
        accountingArticlePrices.addAll(applyDiscounts(accountingArticlePrices, quoteVersion.getQuote().getSeller(), quoteVersion.getQuote().getBillableAccount(),
        		quoteVersion));

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
                    .map(price -> new PriceDTO(price.get())).collect(Collectors.toList());

            pricesPerTaxDTO.add(new TaxPricesDto(taux, prices));
            quoteTotalAmount.add(prices.stream().map(o->o.getAmountWithoutTax()).reduce(BigDecimal.ZERO, BigDecimal::add));
        }








        CpqQuote quote=quoteVersion.getQuote();
        applyFixedDiscount(quoteVersion.getDiscountPlan(), quoteTotalAmount, quote.getSeller(),
        		quote.getBillableAccount(), null, null,null, quoteVersion);

        //Get the updated quote version and construct the DTO
        QuoteVersion updatedQuoteVersion=quoteVersionService.findById(quoteVersion.getId());
        GetQuoteVersionDtoResponse response = new GetQuoteVersionDtoResponse(updatedQuoteVersion, true, true, true,true);
        response.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(updatedQuoteVersion));
        response.setPrices(pricesPerTaxDTO);
        return response;
    }

    private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType, QuoteVersion quoteVersion,QuoteOffer quoteOffer, PriceLevelEnum level) {
    	log.debug("reducePrices quoteVersion={}, quoteOffer={}, level={}",quoteVersion!=null?quoteVersion.getId():null,quoteOffer!=null?quoteOffer.getId():null,level);
    	if(pricesPerType.get(key).size()==1){
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
            if(!PriceLevelEnum.OFFER.equals(level)) {
                quotePriceService.create(quotePrice);
            }
            log.debug("reducePrices1 quotePriceId={}, level={}",quotePrice.getId(),quotePrice.getPriceLevelEnum());
            return Optional.of(quotePrice);
    	}
    	return pricesPerType.get(key).stream().reduce((a, b) -> {
    		QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(key);
            quotePrice.setPriceLevelEnum(level);
            quotePrice.setQuoteVersion(quoteVersion!=null?quoteVersion:quoteOffer.getQuoteVersion());
            quotePrice.setQuoteOffer(quoteOffer);
            quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
            quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
            quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
            quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
            quotePrice.setTaxRate(a.getTaxRate());
            quotePrice.setChargeTemplate(a.getChargeTemplate());
            if(a.getRecurrenceDuration()!=null) {
            	quotePrice.setRecurrenceDuration(a.getRecurrenceDuration());
            }
            if(a.getRecurrencePeriodicity()!=null) {
            	quotePrice.setRecurrencePeriodicity(a.getRecurrencePeriodicity());
            }
            if(!PriceLevelEnum.OFFER.equals(level)) {
                quotePriceService.create(quotePrice);
            }
            log.debug("reducePrices2 quotePriceId={}, level={}",quotePrice.getId(),quotePrice.getPriceLevelEnum());

            return quotePrice;
        });
    }

    public List<QuotePrice> offerQuotation(QuoteOffer quoteOffer) {
        Subscription subscription = instantiateVirtualSubscription(quoteOffer);
        List<PriceDTO> pricesDTO =new ArrayList<>();
        List<WalletOperation> walletOperations = quoteRating(subscription, true);
        QuoteArticleLine quoteArticleLine = null;
        Map<String, QuoteArticleLine> quoteArticleLines = new HashMap<String, QuoteArticleLine>();
        Map<Long, BigDecimal> quoteProductTotalAmount = new HashMap<Long, BigDecimal>();
        List<QuotePrice> accountingPrices = new ArrayList<>();
        String accountingArticleCode = null;
        BigDecimal quoteProductAmount=BigDecimal.ZERO;
        for (WalletOperation wo : walletOperations) {
            accountingArticleCode = wo.getAccountingArticle().getCode();
            Long quoteProductId=wo.getServiceInstance().getQuoteProduct().getId();
            if (!quoteProductTotalAmount.containsKey(quoteProductId)) {
            	quoteProductTotalAmount.put(quoteProductId, wo.getAmountWithoutTax());
            }else {
            	quoteProductAmount=quoteProductTotalAmount.get(quoteProductId);
            	quoteProductTotalAmount.put(quoteProductId,quoteProductAmount.add(wo.getAmountWithoutTax()));
            }
            if (!quoteArticleLines.containsKey(accountingArticleCode)) {
                quoteArticleLine = new QuoteArticleLine();
                quoteArticleLine.setAccountingArticle(wo.getAccountingArticle());
                quoteArticleLine.setQuantity(wo.getQuantity());
                quoteArticleLine.setServiceQuantity(wo.getInputQuantity());
                quoteArticleLine.setBillableAccount(wo.getBillingAccount());
                quoteArticleLine.setQuoteProduct(wo.getServiceInstance().getQuoteProduct());
                wo.getServiceInstance().getQuoteProduct().getQuoteArticleLines().add(quoteArticleLine);
                quoteArticleLine.setQuoteLot(quoteOffer.getQuoteLot());
                quoteArticleLine.setQuoteVersion(quoteOffer.getQuoteVersion());
                quoteArticleLineService.create(quoteArticleLine);
                quoteArticleLines.put(accountingArticleCode, quoteArticleLine);
            }else {
            	quoteArticleLine=quoteArticleLines.get(accountingArticleCode);
            	quoteArticleLine.setQuantity(quoteArticleLine.getQuantity().add(wo.getQuantity()));
            }
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(PriceTypeEnum.getPriceTypeEnum(wo.getChargeInstance()));
            quotePrice.setPriceLevelEnum(PriceLevelEnum.PRODUCT);
            quotePrice.setAmountWithoutTax(wo.getAmountWithoutTax());
            quotePrice.setAmountWithoutTaxWithDiscount(wo.getAmountWithoutTax());
            quotePrice.setAmountWithTax(wo.getAmountWithTax());
            quotePrice.setTaxAmount(wo.getAmountTax());
            quotePrice.setCurrencyCode(wo.getCurrency() != null ? wo.getCurrency().getCurrencyCode() : null);
            quotePrice.setQuoteArticleLine(quoteArticleLine);
            quotePrice.setQuoteVersion(quoteOffer.getQuoteVersion());
            quotePrice.setQuoteOffer(quoteOffer);
        
            quotePrice.setChargeTemplate(wo.getChargeInstance().getChargeTemplate());
            if (PriceTypeEnum.RECURRING.equals(quotePrice.getPriceTypeEnum())) {
                RecurringChargeTemplate recurringCharge = ((RecurringChargeTemplate) wo.getChargeInstance().getChargeTemplate());

                Long recurrenceDuration = Long.valueOf(getDurationTerminInMonth(recurringCharge.getAttributeDuration(), recurringCharge.getDurationTermInMonth(), quoteOffer, wo.getServiceInstance().getQuoteProduct()));
                quotePrice.setRecurrenceDuration(recurrenceDuration);
                quotePrice.setRecurrencePeriodicity(((RecurringChargeTemplate)wo.getChargeInstance().getChargeTemplate()).getCalendar().getDescription());
                overrideAmounts(quotePrice, recurrenceDuration);
            } 
            quotePrice.setUnitPriceWithoutTax(wo.getUnitAmountWithoutTax());
            quotePrice.setTaxRate(wo.getTaxPercent());
            quotePriceService.create(quotePrice);
            quoteArticleLine.getQuotePrices().add(quotePrice);
            quoteArticleLine = quoteArticleLineService.update(quoteArticleLine);
            accountingPrices.add(quotePrice);
        }

        //Calculate totals by offer

        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = accountingPrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        quotePriceService.removeByQuoteOfferAndPriceLevel(quoteOffer, PriceLevelEnum.OFFER);
        log.debug("offerQuotation pricesPerType size={}",pricesPerType.size());
        pricesPerType
        .keySet()
        .stream()
        .map(key -> reducePrices(key, pricesPerType, null,quoteOffer,PriceLevelEnum.OFFER))
        .filter(Optional::isPresent)
        .map(price -> {
            QuotePrice quotePrice = price.get();
            quotePriceService.create(quotePrice);
            quoteOffer.getQuotePrices().add(quotePrice);
            pricesDTO.add(new PriceDTO(quotePrice));
            return pricesDTO;
        }).collect(Collectors.toList());

        //apply fixed discounts on products
        quoteProductTotalAmount.forEach((id, amount) -> {
        	QuoteProduct quoteProduct=quoteProductService.findById(id);
        	 applyFixedDiscount(quoteProduct.getDiscountPlan(), amount, quoteOffer.getQuoteVersion().getQuote().getSeller(),
             		(quoteOffer.getBillableAccount()!=null?quoteOffer.getBillableAccount():quoteOffer.getQuoteVersion().getQuote().getBillableAccount()), quoteOffer, quoteProduct, null,quoteOffer.getQuoteVersion());

		});

        BigDecimal offerTotalAmount = pricesDTO.stream().map(o->o.getAmountWithoutTax()).reduce(BigDecimal.ZERO, BigDecimal::add);
        applyFixedDiscount(quoteOffer.getDiscountPlan(), offerTotalAmount, quoteOffer.getQuoteVersion().getQuote().getSeller(),
        		quoteOffer.getBillableAccount()!=null?quoteOffer.getBillableAccount():quoteOffer.getQuoteVersion().getQuote().getBillableAccount(), quoteOffer, null, null,quoteOffer.getQuoteVersion());

        return accountingPrices;
    }

    private void overrideAmounts(QuotePrice quotePrice, Long recurrenceDuration) {
        quotePrice.setAmountWithTax(quotePrice.getAmountWithTax().multiply(BigDecimal.valueOf(recurrenceDuration)));
        quotePrice.setAmountWithoutTax(quotePrice.getAmountWithoutTax().multiply(BigDecimal.valueOf(recurrenceDuration)));
        quotePrice.setAmountWithoutTaxWithDiscount(quotePrice.getAmountWithoutTaxWithDiscount() != null ?
                quotePrice.getAmountWithoutTaxWithDiscount().multiply(BigDecimal.valueOf(recurrenceDuration)) : null);
        quotePrice.setTaxAmount(quotePrice.getTaxAmount() != null ?
                quotePrice.getTaxAmount().multiply(BigDecimal.valueOf(recurrenceDuration)) : null);
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
            if(productQuoteAttribute.isPresent())
                durationTermInMonth = getDurationTermInMonth(productQuoteAttribute);
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
            if(quoteVersion.getQuoteArticleLines()!=null) {
            	 Set<Long> quoteArticleLines = quoteVersion.getQuoteArticleLines().stream().map(l -> l.getId()).collect(Collectors.toSet());
                 if (!quoteArticleLines.isEmpty()) {
                 	quoteVersion.getQuoteArticleLines().clear();
                     quoteVersionService.update(quoteVersion);
                     quoteArticleLineService.remove(quoteArticleLines);
                 }
            }



    }

    @SuppressWarnings("unused")
    public List<WalletOperation> quoteRating(Subscription subscription, boolean isVirtual) throws BusinessException {

        List<WalletOperation> walletOperations = new ArrayList<>();
        BillingAccount billingAccount = null;
        if (subscription != null) {

            billingAccount = subscription.getUserAccount().getBillingAccount();
            Map<String, Object> attributes = new HashMap<String, Object>();
            ;
            // Add Service charges
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                List<AttributeValue> attributeValues = serviceInstance.getAttributeInstances().stream().map(ai -> (AttributeValue)ai).collect(Collectors.toList());
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
                        WalletOperation wo = oneShotChargeInstanceService.oneShotChargeApplicationVirtual(subscription,
                                subscriptionCharge, serviceInstance.getSubscriptionDate(),
                                serviceInstance.getQuantity());
                        if (wo != null) {
                            wo.setAccountingArticle(accountingArticleService.getAccountingArticle(serviceInstance.getProductVersion().getProduct(), subscriptionCharge.getChargeTemplate(), attributes)
                                    .orElseThrow(() -> new BusinessException(errorMsg+" and charge "+subscriptionCharge.getChargeTemplate())));
                            walletOperations.add(wo);
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
                        Date nextApplicationDate = walletOperationService.getRecurringPeriodEndDate(recurringCharge, recurringCharge.getSubscriptionDate());

                        List<WalletOperation> walletOps = recurringChargeInstanceService
                                .applyRecurringCharge(recurringCharge, nextApplicationDate, false, true, null);
                        if (walletOps != null && !walletOps.isEmpty()) {
                            for (WalletOperation wo : walletOps) {
                            	 wo.setAccountingArticle(accountingArticleService.getAccountingArticle(serviceInstance.getProductVersion().getProduct(), recurringCharge.getChargeTemplate(), attributes)
                                         .orElseThrow(() -> new BusinessException(errorMsg+" and charge "+recurringCharge.getChargeTemplate())));
                                walletOperations.add(wo);
                            }

                        }

                    } catch (RatingException e) {
                        log.trace("Failed to apply a recurring charge {}: {}", recurringCharge, e.getRejectionReason());
                        throw new BusinessException("Failed to apply a subscription charge {}: {}"+recurringCharge.getCode(),e); // e.getBusinessException();

                    }
                }
                
                // Add subscription charges
                EDR edr =null;
                for (UsageChargeInstance usageCharge : serviceInstance.getUsageChargeInstances()) {
                	edr =new EDR();
                	 try {
                		 
                		 edr.setAccessCode(null);
                		 edr.setEventDate(usageCharge.getChargeDate());
                		 edr.setSubscription(subscription);
                		 edr.setStatus(EDRStatusEnum.OPEN);
                		 edr.setCreated(new Date());
                		 edr.setOriginBatch("QUOTE");
                		 edr.setOriginRecord(System.currentTimeMillis()+"");
                		 UsageChargeTemplate chargetemplate=(UsageChargeTemplate)usageCharge.getChargeTemplate();
                		 Double quantity=(Double)attributes.get(chargetemplate.getUsageQuantityAttribute().getCode());
                		 edr.setQuantity(quantity!=null?new BigDecimal(quantity):BigDecimal.ZERO);
                         List<WalletOperation> walletOperationsFromEdr = usageRatingService.rateVirtualEDR(edr);
                         
                         if (walletOperationsFromEdr != null) {
                        	 for(WalletOperation walletOperation:walletOperationsFromEdr) {
                        		 walletOperation.setAccountingArticle(accountingArticleService.getAccountingArticle(serviceInstance.getProductVersion().getProduct(), usageCharge.getChargeTemplate(), attributes)
                                         .orElseThrow(() -> new BusinessException(errorMsg+" and charge "+usageCharge.getChargeTemplate())));
                                 walletOperations.addAll(walletOperationsFromEdr);
                        	 }
                        	 
                         }
                        

                     } catch (RatingException e) {
                         log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
                         throw new BusinessException("Failed to apply a subscription charge {}: {}"+usageCharge.getCode(),e); // e.getBusinessException();


                     } catch (BusinessException e) {
                         log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
                         throw new BusinessException("Failed to apply a subscription charge {}: {}"+usageCharge.getCode(),e); // e.getBusinessException();

                     }
                }
            }
            

        }
        return walletOperations;
    }

    private Subscription instantiateVirtualSubscription(QuoteOffer quoteOffer) {


        String subscriptionCode = UUID.randomUUID().toString();

        Subscription subscription = new Subscription();
        subscription.setCode(subscriptionCode);
        BillingAccount billableAccount=quoteOffer.getBillableAccount()!=null? quoteOffer.getBillableAccount():quoteOffer.getQuoteVersion().getQuote().getBillableAccount();
        Seller seller =billableAccount.getCustomerAccount().getCustomer().getSeller();
        subscription.setSeller(seller);

        subscription.setOffer(quoteOffer.getOfferTemplate());
        subscription.setSubscriptionDate(new Date());
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


        // instantiate and activate services
        processProducts(subscription, quoteOffer.getQuoteProduct());

        return subscription;
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
    	QuoteOfferDTO dto = new QuoteOfferDTO(offer, true, true,true);
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


    private List<QuotePrice> applyDiscounts(List<QuotePrice> quotePrices,Seller seller,BillingAccount billingAccount, QuoteVersion quoteVersion) {
    	QuoteOffer quoteOffer=null;
    	QuoteProduct quoteproduct=null;
    	List<QuotePrice> discountPrices=new ArrayList<QuotePrice>();
    	for(QuotePrice quotePrice : quotePrices) {
    		 quoteproduct=quotePrice.getQuoteArticleLine().getQuoteProduct();
        	 quoteOffer=quoteproduct.getQuoteOffer();
        	 quoteVersion=quotePrice.getQuoteVersion();
        	if(PriceLevelEnum.PRODUCT.equals(quotePrice.getPriceLevelEnum())) {
        		if(quoteOffer.getDiscountPlan()!=null) {
        			discountPrices.addAll(applyPercentageDiscount(quotePrice, quoteOffer.getDiscountPlan(),seller, billingAccount, quoteVersion));
        		}
        		if(quoteproduct.getDiscountPlan()!=null) {
        			discountPrices.addAll(applyPercentageDiscount(quotePrice,quoteproduct.getDiscountPlan(), seller, billingAccount, quoteVersion));
        		}
        		if(quoteVersion.getDiscountPlan()!=null) {
        			discountPrices.addAll(applyPercentageDiscount(quotePrice,quoteVersion.getDiscountPlan(), seller, billingAccount, quoteVersion));
        		}
        	}
    	}
    	return discountPrices;
    }

    private void applyFixedDiscount( DiscountPlan discountPlan,BigDecimal amountToApplyDiscountOn, Seller seller, BillingAccount billingAccount, QuoteOffer quoteOffer,QuoteProduct quoteproduct,AccountingArticle accountingArticle,QuoteVersion quoteVersion) {
    	log.debug("applyFixedDiscount discountPlan code={},amountToApplyDiscountOn={}",discountPlan!=null?discountPlan.getCode():null,amountToApplyDiscountOn);
    	if(discountPlan==null|| amountToApplyDiscountOn==null || amountToApplyDiscountOn.compareTo(BigDecimal.ZERO)<=0) {
    		return;
    	}
    	OfferTemplate offerTemplate=quoteOffer!=null?quoteOffer.getOfferTemplate():null;
    	Product product =quoteproduct!=null?quoteproduct.getProductVersion().getProduct():null;
         BigDecimal unitDiscountAmount = BigDecimal.ZERO;
         boolean isEnterprise = appProvider.isEntreprise();
         QuoteArticleLine quoteArticleLine = null;
         TaxInfo taxInfo = null;
    	boolean isDiscountApplicable = discountPlanService.isDiscountPlanApplicable(billingAccount, discountPlan, offerTemplate, product, quoteVersion.getQuote().getQuoteDate());
    	log.debug("applyFixedDiscount discountPlan code={},isDiscountApplicable={}",discountPlan.getCode(),isDiscountApplicable);

        if (isDiscountApplicable) {
        	  Map<String, QuoteArticleLine> quoteArticleLines = new HashMap<String, QuoteArticleLine>();
        	List<DiscountPlanItem> discountItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, discountPlan, offerTemplate, product, accountingArticle);

        	 for (DiscountPlanItem discountPlanItem : discountItems) {
        		 log.debug("applyFixedDiscount discountPlan code={},discountPlanItem type={}",discountPlan.getCode(),discountPlanItem.getDiscountPlanItemType());
           	  if (discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.FIXED) {
        		  AccountingArticle discountAccountingArticle = discountPlanItem.getAccountingArticle();
                  if(discountAccountingArticle == null)
                  	throw new EntityDoesNotExistsException("Discount plan item ("+discountPlanItem.getCode()+") doesn't have an accounting article");

                  unitDiscountAmount = unitDiscountAmount.add(discountPlanItemService.getDiscountAmount(amountToApplyDiscountOn, discountPlanItem,product, Collections.emptyList()));
                  log.debug("applyFixedDiscount discountPlan code={},unitDiscountAmount={}",discountPlan.getCode(),unitDiscountAmount);
                  if (unitDiscountAmount != null && unitDiscountAmount.abs().compareTo(BigDecimal.ZERO) > 0) {
                      String accountingArticleCode = discountAccountingArticle.getCode();
                      if (!quoteArticleLines.containsKey(accountingArticleCode)) {
                          quoteArticleLine = new QuoteArticleLine();
                          quoteArticleLine.setAccountingArticle(discountAccountingArticle);
                          quoteArticleLine.setQuantity(BigDecimal.ONE);
                          quoteArticleLine.setServiceQuantity(BigDecimal.ONE);
                          quoteArticleLine.setBillableAccount(billingAccount);
                          quoteArticleLine.setQuoteProduct(quoteproduct);
                          quoteArticleLine.setQuoteVersion(quoteVersion);
                          if(quoteOffer != null) {
                              quoteArticleLine.setQuoteLot(quoteOffer.getQuoteLot());
                          }
                          quoteArticleLineService.create(quoteArticleLine);
                          quoteArticleLines.put(accountingArticleCode, quoteArticleLine);
                      } else {
                          quoteArticleLine = quoteArticleLines.get(accountingArticleCode);
                      }

                      QuotePrice discountQuotePrice = new QuotePrice();
                      discountQuotePrice.setPriceLevelEnum(quoteproduct!=null?PriceLevelEnum.PRODUCT:quoteOffer!=null?PriceLevelEnum.OFFER:PriceLevelEnum.QUOTE);
                      discountQuotePrice.setPriceTypeEnum(PriceTypeEnum.FIXED_DISCOUNT);
                      BigDecimal taxPercent = null;
                      if (discountAccountingArticle.getTaxClass() != null) {
                          taxInfo = taxMappingService.determineTax(discountAccountingArticle.getTaxClass(), seller, billingAccount, null, quoteVersion.getQuote().getQuoteDate(), false, false);
                          taxPercent = taxInfo.tax.getPercent();
                      }
                      BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(unitDiscountAmount, unitDiscountAmount, taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                      discountQuotePrice.setUnitPriceWithoutTax(unitDiscountAmount);
                      discountQuotePrice.setAmountWithoutTax(amounts[0]);
                      discountQuotePrice.setAmountWithTax(amounts[1]);
                      discountQuotePrice.setTaxAmount(amounts[2]);
                      discountQuotePrice.setQuoteArticleLine(quoteArticleLine);
                      discountQuotePrice.setQuoteVersion(quoteVersion);
                      discountQuotePrice.setQuoteOffer(quoteOffer);

                      discountQuotePrice.setTaxRate(taxPercent);
                      quotePriceService.create(discountQuotePrice);
                      quoteArticleLine = quoteArticleLineService.update(quoteArticleLine);
                      log.debug("applyFixedDiscount discountPlan code={},unitDiscountAmount={},Article code={}",discountPlan.getCode(),unitDiscountAmount,quoteArticleLine.getAccountingArticle().getCode());
                  }
              }

        	  }
        }

    }

    private List<QuotePrice> applyPercentageDiscount(QuotePrice quotePrice, DiscountPlan discountPlan, Seller seller, BillingAccount billingAccount, QuoteVersion quoteVersion) {

        List<QuotePrice> discountPrices = new ArrayList<>();
        if (discountPlan == null) {
            return new ArrayList<>();
        }
        AccountingArticle accountintArticle = null;
        QuoteProduct quoteproduct = null;
        QuoteOffer quoteOffer = null;
        OfferTemplate offerTemplate = null;
        List<AttributeValue> attributesValues = null;
        Product product = null;
        BigDecimal quantity=BigDecimal.ONE;
        BigDecimal serviceQuantity=BigDecimal.ONE;
        if(quotePrice.getQuoteArticleLine() != null) {
        	quantity=quotePrice.getQuoteArticleLine().getQuantity();
        	serviceQuantity=quotePrice.getQuoteArticleLine().getServiceQuantity();
            accountintArticle = quotePrice.getQuoteArticleLine().getAccountingArticle();
            quoteproduct = quotePrice.getQuoteArticleLine().getQuoteProduct();
            quoteOffer = quoteproduct.getQuoteOffer();
            offerTemplate = quoteOffer.getOfferTemplate();
            product = quoteproduct.getProductVersion().getProduct();
            attributesValues = new ArrayList(quoteproduct.getQuoteAttributes());
            if (quoteOffer.getBillableAccount() != null) {
                billingAccount = quoteOffer.getBillableAccount();
            }
        }
        BigDecimal amountWithoutTax = quotePrice.getUnitPriceWithoutTax();
        BigDecimal unitDiscountAmount = BigDecimal.ZERO;
        boolean isEnterprise = appProvider.isEntreprise();
        QuoteArticleLine quoteArticleLine = null;
        TaxInfo taxInfo = null;

        boolean isOfferDiscountApplicable = discountPlanService.isDiscountPlanApplicable(billingAccount, discountPlan, offerTemplate, product, quoteVersion.getQuote().getQuoteDate());
        if (isOfferDiscountApplicable) {
            List<DiscountPlanItem> discountItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, discountPlan, offerTemplate, product, accountintArticle);
            Map<String, QuoteArticleLine> quoteArticleLines = new HashMap<String, QuoteArticleLine>();
            for (DiscountPlanItem discountPlanItem : discountItems) {
            	  if (discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.PERCENTAGE) {
            		  AccountingArticle discountAccountingArticle = discountPlanItem.getAccountingArticle();
                      if(discountAccountingArticle == null)
                      	throw new EntityDoesNotExistsException("Discount plan item ("+discountPlanItem.getCode()+") doesn't have an accounting article");
                      if(quoteproduct == null)
                      	throw new MeveoApiException("No product found for this discount : " + discountPlanItem.getCode());

                      unitDiscountAmount = unitDiscountAmount.add(discountPlanItemService.getDiscountAmount(amountWithoutTax, discountPlanItem,quoteproduct.getProductVersion().getProduct(), attributesValues == null ? Collections.emptyList() : attributesValues));
                      if (unitDiscountAmount != null && unitDiscountAmount.abs().compareTo(BigDecimal.ZERO) > 0) {
                          String accountingArticleCode = discountAccountingArticle.getCode();
                          if (!quoteArticleLines.containsKey(accountingArticleCode)) {
                              quoteArticleLine = new QuoteArticleLine();
                              quoteArticleLine.setAccountingArticle(discountAccountingArticle);
                              quoteArticleLine.setQuantity(quantity);
                              quoteArticleLine.setServiceQuantity(serviceQuantity);
                              quoteArticleLine.setBillableAccount(billingAccount);
                              quoteArticleLine.setQuoteProduct(quoteproduct);
                              quoteArticleLine.setQuoteVersion(quoteVersion);
                              if(quoteOffer != null) {
                                  quoteArticleLine.setQuoteLot(quoteOffer.getQuoteLot());
                                  quoteproduct.getQuoteArticleLines().add(quoteArticleLine);
                              }
                              quoteArticleLineService.create(quoteArticleLine);
                              quoteArticleLines.put(accountingArticleCode, quoteArticleLine);
                          } else {
                              quoteArticleLine = quoteArticleLines.get(accountingArticleCode);
                          }

                          QuotePrice discountQuotePrice = new QuotePrice();
                          discountQuotePrice.setPriceTypeEnum(quotePrice.getPriceTypeEnum());
                          discountQuotePrice.setPriceLevelEnum(quotePrice.getPriceLevelEnum());

                        BigDecimal taxPercent = quotePrice.getTaxRate();
                        if (discountAccountingArticle.getTaxClass() != null) {
                            taxInfo = taxMappingService.determineTax(discountAccountingArticle.getTaxClass(), seller, billingAccount, null, quoteVersion.getQuote().getQuoteDate(), false, false);
                            taxPercent = taxInfo.tax.getPercent();
                        }
                        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(unitDiscountAmount, unitDiscountAmount, taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                        discountQuotePrice.setUnitPriceWithoutTax(unitDiscountAmount);
                        discountQuotePrice.setAmountWithoutTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[0]):BigDecimal.ZERO);
                        discountQuotePrice.setAmountWithTax(quantity.multiply(amounts[1]));
                        discountQuotePrice.setTaxAmount(quantity.multiply(amounts[2]));
                        discountQuotePrice.setCurrencyCode(quotePrice.getCurrencyCode());
                        discountQuotePrice.setQuoteArticleLine(quoteArticleLine);
                        discountQuotePrice.setQuoteVersion(quoteVersion);
                        discountQuotePrice.setChargeTemplate(quotePrice.getChargeTemplate());
                        if (PriceTypeEnum.RECURRING.equals(discountQuotePrice.getPriceTypeEnum())) {
                            RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) quotePrice.getChargeTemplate();
                            Long recurrenceDuration = Long.valueOf(getDurationTerminInMonth(recurringChargeTemplate.getAttributeDuration(), recurringChargeTemplate.getDurationTermInMonth(), quoteOffer, quoteproduct));
                            discountQuotePrice.setRecurrenceDuration(recurrenceDuration);
                            //quotePrice.setRecurrencePeriodicity(((RecurringChargeTemplate)wo.getChargeInstance().getChargeTemplate()).getCalendar());
                            discountQuotePrice.setAmountWithTax(discountQuotePrice.getAmountWithTax().multiply(BigDecimal.valueOf(recurrenceDuration)));
                            discountQuotePrice.setAmountWithoutTax(discountQuotePrice.getAmountWithoutTax().multiply(BigDecimal.valueOf(recurrenceDuration)));
                            discountQuotePrice.setTaxAmount(discountQuotePrice.getTaxAmount() != null ?
                                    discountQuotePrice.getTaxAmount().multiply(BigDecimal.valueOf(recurrenceDuration)) : null);

                            //set AmountWithoutTaxWithDiscount
                            quotePrice.setAmountWithoutTaxWithDiscount(quotePrice.getAmountWithoutTax().add(discountQuotePrice.getAmountWithoutTax()));
                        }else if (PriceTypeEnum.USAGE.equals(quotePrice.getPriceTypeEnum()) && ((UsageChargeTemplate) quotePrice.getChargeTemplate()).getUsageQuantityAttribute() != null){
                            UsageChargeTemplate usageChargeTemplate = (UsageChargeTemplate) quotePrice.getChargeTemplate();
                            Long usageQuantity = Long.valueOf(getDurationTerminInMonth(usageChargeTemplate.getUsageQuantityAttribute(), 1, quoteOffer, quoteproduct));
                            quotePrice.setRecurrenceDuration(usageQuantity);
                            overrideAmounts(quotePrice, usageQuantity);
                        }
                        discountQuotePrice.setTaxRate(taxPercent);
                        quotePriceService.create(discountQuotePrice);
                        quoteArticleLine.getQuotePrices().add(quotePrice);
                        quoteArticleLine = quoteArticleLineService.update(quoteArticleLine);
                        discountPrices.add(discountQuotePrice);
                    }
                }

            	  }

        }
        return discountPrices;
    }



    private List<PriceDTO> populateToDTO(List<QuotePrice> quotePrices){
    	if(quotePrices==null) {
    		return new ArrayList<PriceDTO>();
    	}
    	List<PriceDTO> priceDTO=
    	quotePrices
    	.stream()
        .map(price -> {
            return new PriceDTO(price);
        }).collect(Collectors.toList());
    	return priceDTO;
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

}
