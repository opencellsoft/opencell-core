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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.api.dto.cpq.QuoteAttributeDTO;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
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
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ContractItemService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
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
    private ContractItemService contractItemService;

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
        if(!Strings.isEmpty(quote.getSellerCode())) {
            cpqQuote.setSeller(sellerService.findByCode(quote.getSellerCode()));
        }
        final BillingAccount applicantAccount = billingAccountService.findByCode(quote.getApplicantAccountCode());
        if(applicantAccount == null)
            throw new EntityDoesNotExistsException(BillingAccount.class, quote.getApplicantAccountCode());
        cpqQuote.setApplicantAccount(applicantAccount);
        if(!Strings.isEmpty(quote.getContractCode())) {
            cpqQuote.setContract(contractService.findByCode(quote.getContractCode()));
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
        if(!Strings.isEmpty(quote.getDiscountPlanCode())) {
            cpqQuote.setDiscountPlan(loadEntityByCode(discountPlanService, quote.getDiscountPlanCode(), DiscountPlan.class));
        }

        cpqQuote.setQuoteLotDateBegin(quote.getQuoteLotDateBegin());
        cpqQuote.setQuoteLotDuration(quote.getQuoteLotDuration());
        cpqQuote.setOpportunityRef(quote.getOpportunityRef());
        cpqQuote.setCustomerRef(quote.getExternalId());
        cpqQuote.setValidity(quote.getValidity());
        cpqQuote.setDescription(quote.getDescription());
        cpqQuote.setQuoteDate(quote.getQuoteDate());

        cpqQuote.setOrderInvoiceType(invoiceTypeService.getDefaultQuote());
        
        try {
            populateCustomFields(quote.getCustomFields(), cpqQuote, true);
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
		}
		quoteVersion.setQuote(cpqQuote);
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
			quoteVersionService.create(quoteVersion);
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		return new GetQuoteVersionDtoResponse(quoteVersion);
	}
	
	
	private void newPopulateProduct(List<QuoteProductDTO> quoteProductDtos, QuoteOffer quoteOffer) {
		if(quoteProductDtos != null) {
			int index = 1;
			quoteOffer.getQuoteProduct().size();
			for (QuoteProductDTO quoteProductDTO : quoteProductDtos) {
				if(Strings.isEmpty(quoteProductDTO.getProductCode()))
					missingParameters.add("products["+index+"].productCode");
				if(quoteProductDTO.getProductVersion() == null)
					missingParameters.add("products["+index+"].productVersion");
				
				handleMissingParameters();
				
				ProductVersion productVersion = productVersionService.findByProductAndVersion(quoteProductDTO.getProductCode(), quoteProductDTO.getProductVersion());
				if(productVersion == null)
					throw new EntityDoesNotExistsException(ProductVersion.class, "products["+index+"] = " + quoteProductDTO.getProductCode() +","+ quoteProductDTO.getProductVersion());
				QuoteProduct quoteProduct = null;
				if(quoteProduct == null)
					quoteProduct = new QuoteProduct();
				quoteProduct.setProductVersion(productVersion);
				quoteProduct.setQuantity(quoteProductDTO.getQuantity());
				quoteProduct.setQuoteOffre(quoteOffer);
				populateCustomFields(quoteProductDTO.getCustomFields(), quoteProduct, true);
				quoteProductService.create(quoteProduct);
				newPopulateQuoteAttribute(quoteProductDTO.getProductAttributes(), quoteProduct);
				quoteOffer.getQuoteProduct().add(quoteProduct);
				++index;
			}
		}
	}
	
	private void newPopulateOfferAttribute(List<QuoteAttributeDTO> quoteAttributeDtos, QuoteOffer quoteOffer) {
		if(quoteAttributeDtos != null) { 
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
		       
		        if(!quoteAttributeDTO.getLinkedQuoteAttribute().isEmpty()){
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
        ParamBean paramBean = ParamBean.getInstance();
        byte[] xmlContent = null;
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");

        try {
            CpqQuote cpqQuote = quoteVersion.getQuote();
            String sellerCode = quoteVersion.getQuote().getSeller() != null ? quoteVersion.getQuote().getSeller().getCode() : null;

            String quoteScriptCode = paramBean.getProperty("seller." + sellerCode + ".quoteScript", "");
            if (!StringUtils.isBlank(quoteScriptCode)) {
                ScriptInstance scriptInstance = scriptInstanceService.findByCode(quoteScriptCode);
                if (scriptInstance != null) {
                    String quoteXmlScript = scriptInstance.getCode();
                    ScriptInterface script = scriptInstanceService.getScriptInstance(quoteXmlScript);
                    Map<String, Object> methodContext = new HashMap<String, Object>();
                    methodContext.put("cpqQuote", quoteVersion);
                    methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                    methodContext.put("XMLQuoteCreator", this);
                    if (script != null) {
                        script.execute(methodContext);
                    }
                    xmlContent = (byte[]) methodContext.get(Script.RESULT_VALUE);
                    result.setXmlContent(xmlContent);
                }

            } else {
                String quoteXml = quoteFormatter.format(quoteMapper.map(quoteVersion));
                String meveoDir = paramBeanFactory.getChrootDir() + File.separator;
                File quoteXmlDir = new File(meveoDir + "quotes" + File.separator + "xml");
                if (!quoteXmlDir.exists()) {
                    quoteXmlDir.mkdirs();
                }
                xmlContent = quoteXml.getBytes();
                String fileName = cpqQuoteService.generateFileName(cpqQuote);
                cpqQuote.setXmlFilename(fileName);
                String xmlFilename = quoteXmlDir.getAbsolutePath() + File.separator + fileName + ".xml";
                Files.write(Paths.get(xmlFilename), quoteXml.getBytes(), StandardOpenOption.CREATE);
                result.setXmlContent(xmlContent);
            }

            if (generatePdf) {
                result.setPdfContent(generateQuotePDF(quoteCode, currentVersion, true));
                CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
                if (quote == null) {
                    throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode, "Code");
                }
                result.setPdfFileName(quote.getPdfFilename());
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
        if(!Strings.isEmpty(quoteDto.getContractCode())) {
            quote.setContract(contractService.findByCode(quoteDto.getContractCode()));
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
        quote.setStatus(quoteDto.getStatus());
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
        if(!Strings.isEmpty(quoteDto.getDiscountPlanCode())) {
        	quote.setDiscountPlan(loadEntityByCode(discountPlanService, quoteDto.getDiscountPlanCode(), DiscountPlan.class));
        }
        try {
            populateCustomFields(quoteDto.getCustomFields(), quote, false);
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
        			if(!StringUtils.isBlank(quoteVersionDto.getBillingPlanCode())) {
        			InvoicingPlan invoicingPlan= invoicingPlanService.findByCode(quoteVersionDto.getBillingPlanCode());  
        			if (invoicingPlan == null) {
        				throw new EntityDoesNotExistsException(InvoicingPlan.class, quoteVersionDto.getBillingPlanCode());
        			}
                        qv.setInvoicingPlan(invoicingPlan);
                    }
                    quoteVersionService.update(qv);
                    quoteVersionDto = new QuoteVersionDto(qv);
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
        if (quote.getStatus().equals(QuoteStatusEnum.CANCELLED) ||
                quote.getStatus().equals(QuoteStatusEnum.REJECTED)) {
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
        if (quote.getContract() != null)
            dto.setContractCode(quote.getContract().getCode());
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
        dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(quote));
        dto.setId(quote.getId());
        dto.setStatusDate(quote.getStatusDate());
        if(quote.getDiscountPlan() != null)
        	dto.setDiscountPlanCode(quote.getDiscountPlan().getCode());
        
        return dto;
    }

    private GetQuoteDtoResponse populateToDto(CpqQuote quote, boolean loadQuoteOffers, boolean loadQuoteProduct, boolean loadQuoteAttributes) {
        GetQuoteDtoResponse result = new GetQuoteDtoResponse();
        result.setQuoteDto(populateQuoteToDto(quote));
        final List<QuoteVersion> quoteVersions = quoteVersionService.findByQuoteCode(quote.getCode());
        GetQuoteVersionDtoResponse quoteVersionDto = null;
        for (QuoteVersion version : quoteVersions) {
            quoteVersionDto = new GetQuoteVersionDtoResponse(version, true, true, true,true);
            result.addQuoteVersion(quoteVersionDto);
        }
        return result;
    }


    public QuoteOfferDTO createQuoteItem(QuoteOfferDTO quoteOfferDto) {

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
        if(!Strings.isEmpty(quoteOfferDto.getDiscountPlanCode())) {
        	quoteOffer.setDiscountPlan(discountPlanService.findByCode(quoteOfferDto.getDiscountPlanCode()));
        }
        populateCustomFields(quoteOfferDto.getCustomFields(), quoteOffer, true);
        quoteOfferService.create(quoteOffer);
        quoteOfferDto.setQuoteOfferId(quoteOffer.getId());
        newPopulateProduct(quoteOfferDto.getProducts(), quoteOffer);
        newPopulateOfferAttribute(quoteOfferDto.getOfferAttributes(), quoteOffer);
        return quoteOfferDto;
    }


    public QuoteOfferDTO updateQuoteItem(QuoteOfferDTO quoteOfferDTO) {

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
        if (!Strings.isEmpty(quoteOfferDTO.getBillableAccountCode()))
            quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDTO.getBillableAccountCode()));
        if (!Strings.isEmpty(quoteOfferDTO.getQuoteLotCode()))
            quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDTO.getQuoteLotCode()));
        processQuoteProduct(quoteOfferDTO, quoteOffer);
        processQuoteAttribute(quoteOfferDTO, quoteOffer);
        populateCustomFields(quoteOfferDTO.getCustomFields(), quoteOffer, false);
        quoteOfferService.update(quoteOffer);

        return quoteOfferDTO;
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
        q.setQuoteOffre(quoteOffer);
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
        if(quoteVersion.getStatus().equals(VersionStatusEnum.CLOSED))
            throw new MeveoApiException("Version of quote must not be CLOSED");

        Date now = Calendar.getInstance().getTime();
        cpqQuote.setStatus(QuoteStatusEnum.ACCEPTED.toString());
        cpqQuote.setStatusDate(now);
        quoteVersion.setStatus(VersionStatusEnum.PUBLISHED);
        quoteVersion.setStatusDate(now);

        cpqQuoteService.update(cpqQuote);
        quoteVersionService.update(quoteVersion);

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

    public void updateQuoteStatus(String quoteCode, String status) {
        CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteCode);
        if (cpqQuote == null)
            throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);

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
       
        if (QuoteStatusEnum.APPROVED.toString().equalsIgnoreCase(status)) {
            cpqQuote = serviceSingleton.assignCpqQuoteNumber(cpqQuote);
        }
        try {
            cpqQuoteService.update(cpqQuote);
        } catch (BusinessApiException e) {
            throw new MeveoApiException(e);
        }
    }

    public void updateQuoteVersionStatus(String quoteCode, int currentVersion, VersionStatusEnum status) {
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");
        if (quoteVersion.getStatus().equals(VersionStatusEnum.CLOSED) ||
                quoteVersion.getStatus().equals(VersionStatusEnum.PUBLISHED)) {
            throw new MeveoApiException("you can not update the quote version with status = " + quoteVersion.getStatus());
        }
        quoteVersion.setStatus(status);
        quoteVersion.setStatusDate(Calendar.getInstance().getTime());
        quoteVersionService.update(quoteVersion);
        if (status.equals(VersionStatusEnum.PUBLISHED)){
        	quoteQuotation(quoteCode, currentVersion);
        	updateQuoteStatus(quoteCode, QuoteStatusEnum.PENDING.toString());
        }
    }

    public GetQuoteVersionDtoResponse quoteQuotation(String quoteCode, int currentVersion) {
        List<QuotePrice> accountingArticlePrices = new ArrayList<>();
        List<PriceDTO> pricesDTO =new ArrayList<>();
        QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteCode, currentVersion);
        if (quoteVersion == null)
            throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteCode + "," + currentVersion + ")");
        for (QuoteOffer quoteOffer : quoteVersion.getQuoteOffers()) {
            accountingArticlePrices.addAll(offerQuotation(quoteOffer));
        }
        accountingArticlePrices.addAll(applyDiscounts(accountingArticlePrices, quoteVersion.getQuote().getSeller(), quoteVersion.getQuote().getBillableAccount(),
        		quoteVersion.getQuote().getQuoteDate()));
        
        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = accountingArticlePrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        quotePriceService.removeByQuoteVersionAndPriceLevel(quoteVersion, PriceLevelEnum.QUOTE);
      
        pricesPerType
                .keySet()
                .stream()
                .map(key -> reducePrices(key, pricesPerType, quoteVersion))
                .filter(Optional::isPresent)
                .map(price -> {
                    QuotePrice quotePrice = price.get();
                    quotePriceService.create(quotePrice);
                    pricesDTO.add(new PriceDTO(quotePrice));
                    if(quoteVersion.getQuote().getDiscountPlan()!=null) {
                    	 List<QuotePrice> quotePrices=discountCalculator(quotePrice, quoteVersion.getQuote().getDiscountPlan(),quoteVersion.getQuote().getSeller(), quoteVersion.getQuote().getBillableAccount(), quoteVersion.getQuote().getQuoteDate());
                         pricesDTO.addAll(populateToDTO(quotePrices));
                    }
                    return pricesDTO;
                }).collect(Collectors.toList());
         
         
      
        GetQuoteVersionDtoResponse response = new GetQuoteVersionDtoResponse(quoteVersion, true, true, true,true);
        response.setPrices(pricesDTO);
        return response;
    }

    private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType, QuoteVersion quoteVersion) {
        return pricesPerType.get(key).stream().reduce((a, b) -> {
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(key);
            quotePrice.setPriceLevelEnum(PriceLevelEnum.QUOTE);
            quotePrice.setQuoteVersion(quoteVersion);
            quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
            quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
            quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
            quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
            quotePrice.setTaxRate(a.getTaxRate().add(b.getTaxRate()));
            return quotePrice;
        });
    }

    public List<QuotePrice> offerQuotation(QuoteOffer quoteOffer) {
        Subscription subscription = instantiateVirtualSubscription(quoteOffer);
        List<WalletOperation> walletOperations = quoteRating(subscription, true);
        QuoteArticleLine quoteArticleLine = null;
        Map<String, QuoteArticleLine> quoteArticleLines = new HashMap<String, QuoteArticleLine>();
        List<QuotePrice> accountingPrices = new ArrayList<>();
        String accountingArticleCode = null;
        clearExistingQuotations(walletOperations);
        for (WalletOperation wo : walletOperations) {
            accountingArticleCode = wo.getAccountingArticle().getCode();

            if (!quoteArticleLines.containsKey(accountingArticleCode)) {
                quoteArticleLine = new QuoteArticleLine();
                quoteArticleLine.setAccountingArticle(wo.getAccountingArticle());
                quoteArticleLine.setQuantity(wo.getQuantity());
                quoteArticleLine.setServiceQuantity(wo.getInputQuantity());
                quoteArticleLine.setBillableAccount(wo.getBillingAccount());
                quoteArticleLine.setQuoteProduct(wo.getServiceInstance().getQuoteProduct());
                wo.getServiceInstance().getQuoteProduct().getQuoteArticleLines().add(quoteArticleLine);
                quoteArticleLine.setQuoteLot(quoteOffer.getQuoteLot());
                quoteArticleLineService.create(quoteArticleLine);
                quoteArticleLines.put(accountingArticleCode, quoteArticleLine);
            }else {
            	quoteArticleLine=quoteArticleLines.get(accountingArticleCode);
            }
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(PriceTypeEnum.getPriceTypeEnum(wo.getChargeInstance()));
            quotePrice.setPriceLevelEnum(PriceLevelEnum.ACCOUNTING_ARTICLE);
            quotePrice.setAmountWithoutTax(wo.getAmountWithoutTax());
            quotePrice.setAmountWithTax(wo.getAmountWithTax());
            quotePrice.setTaxAmount(wo.getAmountTax());
            quotePrice.setCurrencyCode(wo.getCurrency() != null ? wo.getCurrency().getCurrencyCode() : null);
            quotePrice.setQuoteArticleLine(quoteArticleLine);
            quotePrice.setQuoteVersion(quoteOffer.getQuoteVersion());
            quotePrice.setChargeTemplate(wo.getChargeInstance().getChargeTemplate());
            if (PriceTypeEnum.RECURRING.equals(quotePrice.getPriceTypeEnum())) {
                Integer durationTermInMonth = ((RecurringChargeTemplate) wo.getChargeInstance().getChargeTemplate()).getDurationTermInMonth();
                if(durationTermInMonth != null)
                    quotePrice.setRecurrenceDuration(Long.valueOf(durationTermInMonth));
                //quotePrice.setRecurrencePeriodicity(((RecurringChargeTemplate)wo.getChargeInstance().getChargeTemplate()).getCalendar());
            }
            quotePrice.setUnitPriceWithoutTax(wo.getUnitAmountWithoutTax());
            quotePrice.setTaxRate(wo.getTaxPercent());
            quotePriceService.create(quotePrice);
            quoteArticleLine.getQuotePrices().add(quotePrice);
            quoteArticleLine = quoteArticleLineService.update(quoteArticleLine);
            accountingPrices.add(quotePrice);
        }
        return accountingPrices;
    }

    private void clearExistingQuotations(List<WalletOperation> walletOperations) {
        for (WalletOperation wo : walletOperations) {
            if (wo.getAccountingArticle() == null) {
                throw new BusinessException("the walletOperation id=" + wo.getId() + " has is not linked to an accounting article");
            }
            QuoteProduct quoteProduct = wo.getServiceInstance().getQuoteProduct();
            Set<Long> quoteArticleLines = quoteProduct.getQuoteArticleLines().stream().map(l -> l.getId()).collect(Collectors.toSet());
            if (!quoteArticleLines.isEmpty()) {
                quoteProduct.getQuoteArticleLines().clear();
                quoteProductService.update(quoteProduct);
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
                Optional<AccountingArticle> accountingArticle = accountingArticleService.getAccountingArticle(serviceInstance.getProductVersion().getProduct(), attributes);
                if (!accountingArticle.isPresent())
                    throw new BusinessException("No accounting article found for product code: " + serviceInstance.getProductVersion().getProduct().getCode() + " and attributes: " + attributes.toString());
                // Add subscription charges
                for (OneShotChargeInstance subscriptionCharge : serviceInstance.getSubscriptionChargeInstances()) {
                    try {
                        WalletOperation wo = oneShotChargeInstanceService.oneShotChargeApplicationVirtual(subscription,
                                subscriptionCharge, serviceInstance.getSubscriptionDate(),
                                serviceInstance.getQuantity());
                        if (wo != null) {
                            wo.setAccountingArticle(accountingArticle.get());
                            walletOperations.add(wo);
                        }

                    } catch (RatingException e) {
                        log.trace("Failed to apply a subscription charge {}: {}", subscriptionCharge,
                                e.getRejectionReason());
                        throw e; // e.getBusinessException();

                    } catch (BusinessException e) {
                        log.error("Failed to apply a subscription charge {}: {}", subscriptionCharge, e.getMessage(),
                                e);
                        throw e;
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
                                wo.setAccountingArticle(accountingArticle.get());
                                walletOperations.add(wo);
                            }

                        }

                    } catch (RatingException e) {
                        log.trace("Failed to apply a recurring charge {}: {}", recurringCharge, e.getRejectionReason());
                        throw e; // e.getBusinessException();

                    } catch (BusinessException e) {
                        log.error("Failed to apply a recurring charge {}: {}", recurringCharge, e.getMessage(), e);
                        throw e;
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
            throw new BusinessException("Billing account: " + billableAccount.getCode() + " has no user accounts");
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
        if (!cpqQuoteService.isCpqQuotePdfExist(quote)) {
            if (generatePdfIfNotExist) {
            	cpqQuoteService.produceQuotePdf(quote);
            }
        }
        return cpqQuoteService.getQuotePdf(quote);

    }
    
    private List<QuotePrice> applyDiscounts(List<QuotePrice> quotePrices,Seller seller,BillingAccount billingAccount, Date quoteDate) {
    	QuoteOffer quoteOffer=null;
    	QuoteProduct quoteproduct=null;
    	QuoteVersion quoteVersion=null;
    	List<QuotePrice> discountPrices=new ArrayList<QuotePrice>();
    	for(QuotePrice quotePrice : quotePrices) {
    		 quoteproduct=quotePrice.getQuoteArticleLine().getQuoteProduct();
        	 quoteOffer=quoteproduct.getQuoteOffre();
        	 quoteVersion=quotePrice.getQuoteVersion();
        	if(PriceLevelEnum.ACCOUNTING_ARTICLE.equals(quotePrice.getPriceLevelEnum())) {
        		if(quoteOffer.getDiscountPlan()!=null) {
        			discountPrices.addAll(discountCalculator(quotePrice, quoteOffer.getDiscountPlan(),seller, billingAccount, quoteDate));
        		}
        		if(quoteproduct.getDiscountPlan()!=null) {
        			discountPrices.addAll(discountCalculator(quotePrice,quoteproduct.getDiscountPlan(), seller, billingAccount, quoteDate));
        		}
        	}else if(PriceLevelEnum.QUOTE.equals(quotePrice.getPriceLevelEnum()) && quoteVersion.getQuote().getDiscountPlan()!=null) {
        		discountPrices.addAll(discountCalculator(quotePrice,quoteVersion.getQuote().getDiscountPlan(), seller, billingAccount, quoteDate));
    		}
    		
    	}
    	return discountPrices;
    }
    
    private List<QuotePrice> discountCalculator(QuotePrice quotePrice,DiscountPlan discountPlan,Seller seller,BillingAccount billingAccount, Date quoteDate) {
    	
    	List<QuotePrice> discountPrices = new ArrayList<>();
    	if(discountPlan==null) {
    		return new ArrayList<>();
    	}
    	AccountingArticle accountintArticle=quotePrice.getQuoteArticleLine().getAccountingArticle();
    	QuoteProduct quoteproduct=quotePrice.getQuoteArticleLine().getQuoteProduct();
    	QuoteOffer quoteOffer=quoteproduct.getQuoteOffre();
    	if(quoteOffer.getBillableAccount()!=null) {
    		billingAccount=quoteOffer.getBillableAccount();
    	}
    	BigDecimal amountWithoutTax=quotePrice.getAmountWithoutTax();
    	BigDecimal discountAmount=BigDecimal.ZERO;
    	 boolean isEnterprise = appProvider.isEntreprise();	
    	 QuoteArticleLine quoteArticleLine=null;
    	 TaxInfo taxInfo =null;
		boolean isOfferDiscountApplicable=discountPlanService.isDiscountPlanApplicable(billingAccount, discountPlan, quoteOffer.getOfferTemplate(), quoteproduct.getProductVersion().getProduct(), quoteDate);
		if(isOfferDiscountApplicable) {
			 List<DiscountPlanItem>  discountItems=discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, discountPlan, quoteOffer.getOfferTemplate(), quoteproduct.getProductVersion().getProduct(), quoteDate,accountintArticle);
			 Map<String, QuoteArticleLine> quoteArticleLines = new HashMap<String, QuoteArticleLine>();
			 for(DiscountPlanItem discountPlanItem:discountItems) {
				 AccountingArticle discountAccountingArticle=discountPlanItem.getAccountingArticle();
				 @SuppressWarnings("unchecked")
				List<AttributeValue> attributesValues=new ArrayList(quoteproduct.getQuoteAttributes());
				 discountAmount=discountAmount.add(discountPlanItemService.getDiscountAmount(billingAccount, amountWithoutTax, isEnterprise, discountPlanItem,quoteproduct.getProductVersion().getProduct(),attributesValues));
				 if(discountAmount!=null && discountAmount.abs().compareTo(BigDecimal.ZERO)>0) {
					  String accountingArticleCode = discountAccountingArticle.getCode();
			            if (!quoteArticleLines.containsKey(accountingArticleCode)) {
			                quoteArticleLine = new QuoteArticleLine();
			                quoteArticleLine.setAccountingArticle(discountAccountingArticle);
			                quoteArticleLine.setQuantity(BigDecimal.ONE);
			                quoteArticleLine.setServiceQuantity(BigDecimal.ONE);
			                quoteArticleLine.setBillableAccount(billingAccount);
			                quoteArticleLine.setQuoteProduct(quoteproduct);
			                quoteArticleLine.setQuoteLot(quoteOffer.getQuoteLot());
			                quoteproduct.getQuoteArticleLines().add(quoteArticleLine);
			                quoteArticleLineService.create(quoteArticleLine);
			                quoteArticleLines.put(accountingArticleCode, quoteArticleLine);
			            }else {
			            	quoteArticleLine=quoteArticleLines.get(accountingArticleCode);
			            }
					    
					    QuotePrice discountQuotePrice = new QuotePrice();
			            discountQuotePrice.setPriceTypeEnum(quotePrice.getPriceTypeEnum());
			            discountQuotePrice.setPriceLevelEnum(quotePrice.getPriceLevelEnum());
			            
			            BigDecimal taxPercent= quotePrice.getTaxRate();
			            if(discountAccountingArticle.getTaxClass()!=null) {
			            	 taxInfo = taxMappingService.determineTax(discountAccountingArticle.getTaxClass(),seller, billingAccount, null, quoteOffer.getQuoteVersion().getQuote().getQuoteDate(), false, false);
			            	 taxPercent=taxInfo.tax.getPercent();
			            }
			            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(discountAmount, discountAmount, taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
			            discountQuotePrice.setAmountWithoutTax(amounts[0]);
			            discountQuotePrice.setAmountWithTax(amounts[1]);
			            discountQuotePrice.setTaxAmount(amounts[2]);
			            discountQuotePrice.setCurrencyCode(quotePrice.getCurrencyCode());
			            discountQuotePrice.setQuoteArticleLine(quoteArticleLine);
			            discountQuotePrice.setQuoteVersion(quoteOffer.getQuoteVersion());
			            discountQuotePrice.setChargeTemplate(quotePrice.getChargeTemplate());
			            if (PriceTypeEnum.RECURRING.equals(discountQuotePrice.getPriceTypeEnum())) {
			                Integer durationTermInMonth = ((RecurringChargeTemplate) quotePrice.getChargeTemplate()).getDurationTermInMonth();
			                if(durationTermInMonth != null)
			                    discountQuotePrice.setRecurrenceDuration(Long.valueOf(durationTermInMonth));
			                //quotePrice.setRecurrencePeriodicity(((RecurringChargeTemplate)wo.getChargeInstance().getChargeTemplate()).getCalendar());
			            }
			            discountQuotePrice.setUnitPriceWithoutTax(discountAmount);
			            discountQuotePrice.setTaxRate(taxPercent);
			            quotePriceService.create(discountQuotePrice);
			            discountPrices.add(discountQuotePrice);
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

}
