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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.Version;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.QuoteAttributeDTO;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.dto.cpq.QuoteProductDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.CpqQuotesListResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.QuoteAttributeService;
import org.meveo.service.cpq.QuoteLotService;
import org.meveo.service.cpq.QuoteProductService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.quote.QuoteOfferService;

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
		final BillingAccount billingAccount = billingAccountService.findByCode(quote.getApplicantAccountCode());
		if(billingAccount == null) 
				throw new EntityDoesNotExistsException(BillingAccount.class, quote.getApplicantAccountCode());
		cpqQuote.setApplicantAccount(billingAccount);
		if(!Strings.isEmpty(quote.getContractCode())) {
			cpqQuote.setContract(contractService.findByCode(quote.getContractCode()));
		}
		cpqQuote.setStatusDate(Calendar.getInstance().getTime());
		cpqQuote.setSendDate(quote.getSendDate());
		
		cpqQuote.setQuoteLotDateBegin(quote.getQuoteLotDateBegin());
		cpqQuote.setQuoteLotDuration(quote.getQuoteLotDuration());
		cpqQuote.setOpportunityRef(quote.getOpportunityRef());
		cpqQuote.setCustomerRef(quote.getExternalId());
		cpqQuote.setValidity(quote.getValidity());
		cpqQuote.setDescription(quote.getDescription());
		if(!Strings.isEmpty(quote.getBillableAccountCode())) {
			cpqQuote.setBillableAccount(billingAccountService.findByCode(quote.getBillableAccountCode()));
		}
		
		try {
			cpqQuoteService.create(cpqQuote);
			quoteVersionService.create(populateNewQuoteVersion(quote.getQuoteVersion(), cpqQuote));
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		return quote;
	}
	
	private QuoteVersion populateNewQuoteVersion(QuoteVersionDto quoteVersionDto, CpqQuote cpqQuote) {
		QuoteVersion quoteVersion = new QuoteVersion();
		quoteVersion.setStatusDate(Calendar.getInstance().getTime());
		quoteVersion.setQuoteVersion(1);
		quoteVersion.setStatus(VersionStatusEnum.DRAFT);
		quoteVersion.setBillingPlanCode(quoteVersionDto.getBillingPlanCode());
		quoteVersion.setStartDate(quoteVersionDto.getStartDate());
		quoteVersion.setEndDate(quoteVersionDto.getEndDate());
		quoteVersion.setShortDescription(quoteVersionDto.getShortDescription());
		quoteVersion.setQuote(cpqQuote);
		return quoteVersion;
	}
	
	public QuoteVersionDto createQuoteVersion(QuoteVersionDto quoteVersionDto) {
		if(Strings.isEmpty(quoteVersionDto.getQuoteCode()))
			missingParameters.add("quoteCode");
		if(quoteVersionDto.getCurrentVersion() <= 0)
			throw new MeveoApiException("current version must be greater than 0");
		final CpqQuote quote = cpqQuoteService.findByCode(quoteVersionDto.getQuoteCode());
		if(quote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteVersionDto.getQuoteCode());
		final QuoteVersion quoteVersion = populateNewQuoteVersion(quoteVersionDto, quote);
		quoteVersion.setVersion(quoteVersionDto.getCurrentVersion());
		try {
			quoteVersionService.create(quoteVersion);
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		return quoteVersionDto;
	}
	
	
	private void newPopulateProduct(List<QuoteProductDTO> quoteProductDtos, QuoteOffer quoteOffer) {
		if(quoteProductDtos != null) {
			int index = 1;
			quoteOffer.getQuoteProduct().size();
			for (QuoteProductDTO quoteProductDTO : quoteProductDtos) {
				if(Strings.isEmpty(quoteProductDTO.getQuoteCode()))
					missingParameters.add("products["+index+"].quoteCode");
				if(quoteProductDTO.getQuoteVersion() <= 0 )
					throw new MeveoApiException("Quote version for products["+index+"] must be greater than 0");
				if(Strings.isEmpty(quoteProductDTO.getProductCode()))
					missingParameters.add("products["+index+"].productCode");
				handleMissingParameters();
				
				CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteProductDTO.getQuoteCode());
				if(cpqQuote == null)
					throw new EntityDoesNotExistsException(CpqQuote.class, "products["+index+"] = " + quoteProductDTO.getQuoteCode());
				QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteProductDTO.getQuoteCode(), quoteProductDTO.getQuoteVersion());
				if(quoteVersion == null)
					throw new EntityDoesNotExistsException(QuoteVersion.class, "products["+index+"] = " + quoteProductDTO.getQuoteCode() +","+ quoteProductDTO.getQuoteVersion());
				ProductVersion productVersion = productVersionService.findByProductAndVersion(quoteProductDTO.getProductCode(), quoteProductDTO.getProductVersion());
				if(productVersion == null)
					throw new EntityDoesNotExistsException(ProductVersion.class, "products["+index+"] = " + quoteProductDTO.getProductCode() +","+ quoteProductDTO.getProductVersion());
				QuoteProduct quoteProduct = null;
				if(quoteProduct == null)
					quoteProduct = new QuoteProduct();
				if(!Strings.isEmpty(quoteProductDTO.getQuoteLotCode())) {
					quoteProduct.setQuoteLot(quoteLotService.findByCodeAndQuoteVersion(quoteProductDTO.getQuoteLotCode(), quoteVersion.getId()));
				}
				quoteProduct.setQuote(cpqQuote);
				quoteProduct.setQuoteVersion(quoteVersion);
				quoteProduct.setProductVersion(productVersion);
				quoteProduct.setQuantity(new BigDecimal(quoteProductDTO.getQuantity()));
				quoteProduct.setBillableAccount(quoteOffer.getBillableAccount());
				quoteProduct.setQuoteOffre(quoteOffer);
				quoteProductService.create(quoteProduct);
				newPopulateQuoteAttribute(quoteProductDTO.getQuoteAttributes(), quoteProduct);
				quoteOffer.getQuoteProduct().add(quoteProduct);
				++index;
			}
		}
	}
	
	private void newPopulateQuoteAttribute(List<QuoteAttributeDTO> quoteAttributes, QuoteProduct quoteProduct) {
		if(quoteAttributes != null) {
			quoteProduct.getQuoteAttributes().clear();
			for (QuoteAttributeDTO quoteAttributeDTO : quoteAttributes) {
				if(Strings.isEmpty(quoteAttributeDTO.getQuoteAttributeCode()))
					missingParameters.add("quoteAttributeCode");
				handleMissingParameters();
				Attribute attribute = attributeService.findByCode(quoteAttributeDTO.getQuoteAttributeCode());
				if(attribute == null)
					throw new EntityDoesNotExistsException(QuoteLot.class, quoteAttributeDTO.getQuoteAttributeCode());
				QuoteAttribute quoteAttribute = new QuoteAttribute();
				quoteAttribute.setAttribute(attribute);
				quoteAttribute.setValue(quoteAttributeDTO.getValue());
				quoteProduct.getQuoteAttributes().add(quoteAttribute);
				quoteAttributeService.create(quoteAttribute);
				quoteAttribute.setQuoteProduct(quoteProduct);
				}
					
			}
		}
	
	
	
	
	public QuoteDTO getQuote(String quoteCode) {
		if(Strings.isEmpty(quoteCode)) {
			missingParameters.add("quoteCode");
		}
		final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
		if(quote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
		final List<QuoteVersion> quoteVersion = quoteVersionService.findByQuoteId(quote.getId());
		if(!quoteVersion.isEmpty())
			return populateToDto(quote, quoteVersion.get(0));
		else
			return populateToDto(quote);
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
		if(!Strings.isEmpty(quoteDto.getBillableAccountCode())) {
			quote.setBillableAccount(billingAccountService.findByCode(quoteDto.getBillableAccountCode()));
		}

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
					if(!Strings.isEmpty(quoteVersionDto.getBillingPlanCode()))
						qv.setBillingPlanCode(quoteVersionDto.getBillingPlanCode());
					quoteVersionService.update(qv);
					quoteVersionDto = new QuoteVersionDto(qv);
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
    							@FilterProperty(property = "contractCode", entityClass = Contract.class) }, totalRecords = "listSize")
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
	        
	        if(totalCount > 0 ) {
	        	List<CpqQuote> quotes = cpqQuoteService.list(paginationConfiguration);
	        	if(quotes != null)
	        		quotes.forEach(c -> {
	        			result.getQuotes().getQuoteDtos().add(populateToDto(c));
	        		});
	        }

		return result;
	}
	
	public void deleteQuote(String quoteCode) {
		if(Strings.isEmpty(quoteCode))
			missingParameters.add("quoteCode");
		final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
		if(quote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
		if(quote.getStatus().equals(QuoteStatusEnum.CANCELLED) || 
				quote.getStatus().equals(QuoteStatusEnum.REJECTED)) {
			List<QuoteVersion> versions = quoteVersionService.findByQuoteId(quote.getId());
			versions.forEach(qv -> {
				quoteVersionService.remove(qv);
			});
			cpqQuoteService.remove(quote);
		}else {
			throw new MeveoApiException("Impossible to delete the Quote with status : " + quote.getStatus());
		}
		
		
		cpqQuoteService.remove(quote);
	}
	
	private QuoteDTO populateToDto(CpqQuote c) {
		final QuoteDTO dto = new QuoteDTO();
		dto.setValidity(c.getValidity());
		dto.setStatus(c.getStatus());
		if(c.getApplicantAccount() != null)
			dto.setApplicantAccountCode(c.getApplicantAccount().getCode());
		if(c.getBillableAccount() != null)
			dto.setBillableAccountCode(c.getBillableAccount().getCode());
		if(c.getContract() != null)
			dto.setContractCode(c.getContract().getCode());
		dto.setQuoteLotDateBegin(c.getQuoteLotDateBegin());
		dto.setQuoteLotDuration(c.getQuoteLotDuration());
		dto.setOpportunityRef(c.getOpportunityRef());
		if(c.getSeller() != null)
			dto.setSellerCode(c.getSeller().getCode());
		dto.setSendDate(c.getSendDate());
		dto.setExternalId(c.getCustomerRef()); // TODO : not sure if it is the correct field
		dto.setDescription(c.getDescription());
		dto.setCode(c.getCode());
		return dto;
	}
	
	private QuoteDTO populateToDto(CpqQuote c, QuoteVersion v) {
		QuoteDTO dto = populateToDto(c);
		if(v != null) {
			dto.setQuoteVersion(new QuoteVersionDto(v));
		}
		return dto;
	}
	

	public QuoteOfferDTO createQuoteItem(QuoteOfferDTO quoteOfferDto) {

		if(Strings.isEmpty(quoteOfferDto.getOfferCode()))
			missingParameters.add("offerCode");
		if(quoteOfferDto.getQuoteVersion() == null)
			missingParameters.add("quoteVersion");
		if(Strings.isEmpty(quoteOfferDto.getQuoteCode()))
			missingParameters.add("quoteCode");
		OfferTemplate offerTemplate = offerTemplateService.findByCode(quoteOfferDto.getOfferCode());
		if(offerTemplate == null)
			throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDto.getOfferCode());
		final QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteOfferDto.getQuoteCode(), quoteOfferDto.getQuoteVersion());
		if(quoteVersion == null)
			throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteOfferDto.getQuoteCode() + "," + quoteOfferDto.getQuoteVersion() + ")");
		QuoteOffer quoteOffer = quoteOfferService.findByTemplateAndQuoteVersion(quoteOfferDto.getOfferCode(), quoteOfferDto.getQuoteCode(), quoteOfferDto.getQuoteVersion());
		if(quoteOffer != null)
			throw new EntityAlreadyExistsException(QuoteOffer.class, quoteOfferDto.getOfferCode() + "," + quoteOfferDto.getOfferCode() + "," + quoteOfferDto.getQuoteVersion() );
		else
			quoteOffer = new QuoteOffer();
		quoteOffer.setOfferTemplate(offerTemplate);
		quoteOffer.setQuoteVersion(quoteVersion);
		if(Strings.isEmpty(quoteOfferDto.getBillableAccountCode()))
			quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDto.getBillableAccountCode()));
		if(Strings.isEmpty(quoteOfferDto.getQuoteLotCode()))
			quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDto.getQuoteLotCode()));
//		quoteOffer.setSequence(quoteOfferDto.gets); // no sequence found in quoteOfferDto
		quoteOfferService.create(quoteOffer);
		quoteOfferDto.setQuoteOfferId(quoteOffer.getId());
		newPopulateProduct(quoteOfferDto.getProducts(), quoteOffer);
		return quoteOfferDto;
	}
	
	
	public QuoteOfferDTO updateQuoteItem(QuoteOfferDTO quoteOfferDTO) {

		if(quoteOfferDTO.getQuoteOfferId() == null)
			missingParameters.add("quoteOfferId");
		handleMissingParameters();
		QuoteOffer quoteOffer = quoteOfferService.findById(quoteOfferDTO.getQuoteOfferId());
		if(quoteOffer == null)
			throw new EntityDoesNotExistsException(QuoteOffer.class, quoteOfferDTO.getQuoteOfferId());
		// check offer template if exist
		if(!Strings.isEmpty(quoteOfferDTO.getOfferCode())) {
			OfferTemplate offerTemplate = offerTemplateService.findByCode(quoteOfferDTO.getOfferCode());
			if(offerTemplate == null)
				throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDTO.getOfferCode());
			quoteOffer.setOfferTemplate(offerTemplate);
		}
		
		// check quote version
		boolean isUniqueKeyChanged = false;
		if(!Strings.isEmpty(quoteOfferDTO.getQuoteCode())) {
			if(quoteOfferDTO.getQuoteVersion() == null)
				missingParameters.add("quoteVersion");
			handleMissingParameters();
			CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteOfferDTO.getQuoteCode());
			if(cpqQuote == null)
				throw new EntityDoesNotExistsException("can not find Quote version with qoute code : " + quoteOfferDTO.getQuoteCode());
			QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteOfferDTO.getQuoteCode(), quoteOfferDTO.getQuoteVersion());
			if(quoteVersion == null)
				throw new EntityDoesNotExistsException("can not find Quote version with qoute code : " + quoteOfferDTO.getQuoteCode() +" and version : " + quoteOfferDTO.getQuoteVersion());
			quoteOffer.setQuoteVersion(quoteVersion);
			isUniqueKeyChanged = true;
		}
		if(isUniqueKeyChanged) {
			QuoteOffer tmpQuoteOffer = quoteOfferService.findById(quoteOfferDTO.getQuoteOfferId());
			if(tmpQuoteOffer.getOfferTemplate().getId() != quoteOffer.getOfferTemplate().getId()  
					||  tmpQuoteOffer.getQuoteVersion().getId() != quoteOffer.getQuoteVersion().getId()) {
//				throw new EntityAlreadyExistsException("Quote Offer already exist with offer template : " + 
//																		tmpQuoteOffer.getOfferTemplate().getCode() + ", and quote version (" + 
//																				quoteOfferDTO.getQuoteCode() + ", " + quoteOfferDTO.getQuoteVersion()  );
				
			}
		}
		if(!Strings.isEmpty(quoteOfferDTO.getBillableAccountCode()))
			quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDTO.getBillableAccountCode()));
		if(!Strings.isEmpty(quoteOfferDTO.getQuoteLotCode()))
			quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDTO.getQuoteLotCode()));
		processQuoteProduct(quoteOfferDTO, quoteOffer);
		
		quoteOfferService.update(quoteOffer);
	
		return quoteOfferDTO;
	}
	
	private void processQuoteProduct(QuoteOfferDTO quoteOfferDTO, QuoteOffer quoteOffer) {
		var quoteProductDtos = quoteOfferDTO.getProducts();
		var hasQuoteProductDtos = quoteProductDtos != null && !quoteProductDtos.isEmpty();
		
		var existencQuoteProducts = quoteOffer.getQuoteProduct();
		var hasExistingQuotes = existencQuoteProducts != null && !existencQuoteProducts.isEmpty();
		
		if(hasQuoteProductDtos) {
			var newQuoteProducts = new ArrayList<QuoteProduct>();
			QuoteProduct quoteProduct = null;
			int i=1;
			for (QuoteProductDTO quoteProductDto : quoteProductDtos) {
				quoteProduct = getQuoteProductFromDto(quoteProductDto, quoteOffer, i);
				newQuoteProducts.add(quoteProduct);
				i++;
			}
			if(!hasExistingQuotes) {
				quoteOffer.getQuoteProduct().addAll(newQuoteProducts);
			}else {
				existencQuoteProducts.retainAll(newQuoteProducts);
				for (QuoteProduct qpNew : newQuoteProducts) {
					int index = existencQuoteProducts.indexOf(qpNew);
					if(index >= 0) {
						QuoteProduct old = existencQuoteProducts.get(index);
						old.update(qpNew);
					}else {
						existencQuoteProducts.add(qpNew);
					}
				}
			}
		}else if(hasExistingQuotes){
			quoteOffer.getQuoteProduct().removeAll(existencQuoteProducts);
		}
	}
	
	private QuoteProduct getQuoteProductFromDto(QuoteProductDTO quoteProductDTO, QuoteOffer quoteOffer, int index) {
		CpqQuote cpqQuote = cpqQuoteService.findByCode(quoteProductDTO.getQuoteCode());
		if(cpqQuote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, "products["+index+"] = " + quoteProductDTO.getQuoteCode());
		QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteProductDTO.getQuoteCode(), quoteProductDTO.getQuoteVersion());
		if(quoteVersion == null)
			throw new EntityDoesNotExistsException(QuoteVersion.class, "products["+index+"] = " + quoteProductDTO.getQuoteCode() +","+ quoteProductDTO.getQuoteVersion());
		ProductVersion productVersion = productVersionService.findByProductAndVersion(quoteProductDTO.getProductCode(), quoteProductDTO.getProductVersion());
		if(productVersion == null)
			throw new EntityDoesNotExistsException(ProductVersion.class, "products["+index+"] = " + quoteProductDTO.getProductCode() +","+ quoteProductDTO.getProductVersion());
		QuoteProduct q = quoteProductService.findByProductVersionAndQuoteOffer(productVersion.getId(), quoteOffer.getId());
		if(q == null)
			throw new EntityDoesNotExistsException("products["+index+"] : doesn't exist");
		if(!Strings.isEmpty(quoteProductDTO.getQuoteLotCode())) {
			q.setQuoteLot(quoteLotService.findByCodeAndQuoteVersion(quoteProductDTO.getQuoteLotCode(), quoteVersion.getId()));
		}
		
		q.setBillableAccount(quoteOffer.getBillableAccount());
		q.setQuantity(new BigDecimal(quoteProductDTO.getQuantity()));
		processQuoteProduct(quoteProductDTO, q);
		return q;
	}
	private void processQuoteProduct(QuoteProductDTO quoteProductDTO, QuoteProduct q) {
		var quoteAttributeDtos = quoteProductDTO.getQuoteAttributes();
		var hasQuoteProductDtos = quoteAttributeDtos != null && !quoteAttributeDtos.isEmpty();
		
		var existencQuoteProducts = q.getQuoteAttributes();
		var hasExistingQuotes = existencQuoteProducts != null && !existencQuoteProducts.isEmpty();
		
		if(hasQuoteProductDtos) {
			var newQuoteProducts = new ArrayList<QuoteAttribute>();
			QuoteAttribute quoteAttribute = null;
			for (QuoteAttributeDTO quoteAttributeDTO : quoteAttributeDtos) {
				quoteAttribute = getQuoteAttributeFromDto(quoteAttributeDTO, q);
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
	private QuoteAttribute getQuoteAttributeFromDto(QuoteAttributeDTO quoteAttributeDTO, QuoteProduct quoteProduct) {
		Attribute attribute = attributeService.findByCode(quoteAttributeDTO.getQuoteAttributeCode());
		if(attribute == null)
			throw new EntityDoesNotExistsException(QuoteLot.class, quoteAttributeDTO.getQuoteAttributeCode());
		QuoteAttribute quoteAttribute = quoteAttributeService.findByAttributeAndQuoteProduct(attribute.getId(), quoteProduct.getId());
		if(quoteAttribute == null) {
			quoteAttribute = new QuoteAttribute();
			quoteAttributeService.create(quoteAttribute);
		}
		
		quoteAttribute.setAttribute(attribute);
		quoteAttribute.setValue(quoteAttributeDTO.getValue());
		quoteProduct.getQuoteAttributes().add(quoteAttribute);
		quoteAttribute.setQuoteProduct(quoteProduct);
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
		if(cpqQuote.getStatus().equals(QuoteStatusEnum.CANCELLED) || cpqQuote.getStatus().equals(QuoteStatusEnum.REJECTED))
			throw new MeveoApiException("quote status can not be publish because of its current status : " + cpqQuote.getStatus().getApiState());
		if(quoteVersion.getStatus().equals(VersionStatusEnum.CLOSED))
			throw new MeveoApiException("Version of quote must not be CLOSED");
		
		Date now = Calendar.getInstance().getTime();
		cpqQuote.setStatus(QuoteStatusEnum.ACCEPTED);
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
   
}
