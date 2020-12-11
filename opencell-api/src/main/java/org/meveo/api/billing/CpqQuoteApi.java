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
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
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
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuoteProduct;
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
		final QuoteVersion quoteVersion = populateNewQuoteVersion(quoteVersionDto, null);
		if(quoteVersion.getQuote() == null) {
			final CpqQuote quote = cpqQuoteService.findByCode(quoteVersionDto.getQuoteCode());
			if(quote == null)
				throw new EntityDoesNotExistsException(CpqQuote.class, quoteVersionDto.getQuoteCode());
			quoteVersion.setQuote(quote);
		}
		try {
			quoteVersionService.create(quoteVersion);
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		return quoteVersionDto;
	}
	
	
	
	
	public QuoteDTO getQuote(String quoteCode) {
		if(Strings.isEmpty(quoteCode)) {
			missingParameters.add("quoteCode");
		}
		final CpqQuote quote = cpqQuoteService.findByCode(quoteCode);
		if(quote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteCode);
		
		return populateToDto(quote);
	}
	
	public QuoteDTO updateQuote(String quoteCode, QuoteDTO quoteDto) {
		if(Strings.isEmpty(quoteCode)) {
			missingParameters.add("quoteCode");
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
			final QuoteVersionDto quoteVersionDto = quoteDto.getQuoteVersion();
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
		cpqQuoteService.remove(quote);
	}
	
	private QuoteDTO populateToDto(CpqQuote c) {
		final QuoteDTO dto = new QuoteDTO();
		dto.setQuoteVersion(null); // TODO : doesnt exist on CpqQuote
		dto.setValidity(c.getValidity());
		dto.setStatus(null); // TODO : doesnt exist on CpqQuote
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
		return dto;
	}
	

	public QuoteOfferDTO createQuoteItem(QuoteOfferDTO quoteItem) {
		if(Strings.isEmpty(quoteItem.getOfferCode()))
			missingParameters.add("offerCode");
		if(quoteItem.getQuoteVersion() == null)
			missingParameters.add("quoteVersion");
		if(Strings.isEmpty(quoteItem.getQuoteCode()))
			missingParameters.add("quoteCode");
		handleMissingParameters();
		final CpqQuote quote = cpqQuoteService.findByCode(quoteItem.getQuoteCode());
		if(quote == null)
			throw new EntityDoesNotExistsException(CpqQuote.class, quoteItem.getQuoteCode());
		final QuoteVersion quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteItem.getQuoteCode(), quoteItem.getQuoteVersion());
		if(quoteVersion == null)
			throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteItem.getQuoteCode() + "," + quoteItem.getQuoteVersion() + ")");
		// TODO : check if the quote exist
		var quoteOffer = new QuoteOffer();
		QuoteLot  quoteLot = null;
		if(!Strings.isEmpty(quoteItem.getQuoteLotCode())) {
			quoteLot = quoteLotService.findByCode(quoteItem.getQuoteLotCode());
			quoteOffer.setQuoteCustomerService(quoteLot);
		}
		if(!Strings.isEmpty(quoteItem.getOfferCode()))
			quoteOffer.setOfferTemplate(offerTemplateService.findByCode(quoteItem.getOfferCode()));
		BillingAccount BillableAccounte = null;
		if(!Strings.isEmpty(quoteItem.getBillableAccountCode())) {
			BillableAccounte = billingAccountService.findByCode(quoteItem.getBillableAccountCode());
			quoteOffer.setBillableAccount(BillableAccounte);
		}
		quoteOffer.setQuoteVersion(quoteVersion);

		try {
			quoteOfferService.create(quoteOffer);
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		if(quoteItem.getProducts() != null) {
			for (QuoteProductDTO q : quoteItem.getProducts()) {
				QuoteProduct qp = processQuoteProductDTO(q, quote, quoteVersion, quoteLot, BillableAccounte, quoteOffer);
				quoteOffer.getQuoteProduct().add(qp);
			}
			quoteOfferService.update(quoteOffer);
		}
		return quoteItem;
	}
	
	
	private QuoteAttribute processQuoteAttributeDTO(QuoteAttributeDTO qad) {
		final QuoteAttribute qa = new QuoteAttribute();
		Attribute attr = attributeService.findByCode(qad.getQuoteAttributeCode());
		if(attr == null)
			throw new EntityDoesNotExistsException(Attribute.class, qa.getAttribute().getCode());
		qa.setAttribute(attr);
		return qa;
	}
	
	private QuoteProduct processQuoteProductDTO(QuoteProductDTO q, CpqQuote quote, QuoteVersion quoteVersion, QuoteLot quoteLot, BillingAccount BillableAccounte, QuoteOffer quoteOffer) {
		final QuoteProduct quoteProduct = new QuoteProduct();
		quoteProduct.setQuote(quote);
		quoteProduct.setQuoteVersion(quoteVersion);
		quoteProduct.setQuoteLot(quoteLot);
		if(!Strings.isEmpty(q.getProductCode())) {
			ProductVersion p = productVersionService.findByProductAndVersion(q.getProductCode(), q.getQuoteVersion());
			if(p == null)
				throw new EntityDoesNotExistsException(ProductVersion.class, q.getProductCode());
			quoteProduct.setProductVersion(p);
		}
		quoteProduct.setQuantity(new BigDecimal(q.getQuantity()));
		quoteProduct.setBillableAccount(BillableAccounte);
		quoteProduct.setQuoteOffre(quoteOffer);
		quoteProductService.create(quoteProduct);
		if(q.getQuoteAttributes() != null) {
			for (QuoteAttributeDTO qad : q.getQuoteAttributes()) {
				QuoteAttribute qatt = processQuoteAttributeDTO(qad);
				qatt.setQuoteProduct(quoteProduct);
				quoteAttributeService.create(qatt);
			}
		}
		return quoteProduct;
		
	}
	
	public QuoteOfferDTO updateQuoteItem(QuoteOfferDTO quoteOfferDTO) {
		if(quoteOfferDTO.getQuoteOfferId() == null) 
			missingParameters.add("QuoteOfferId");
			
		handleMissingParameters();
		
		final QuoteOffer quoteOffer = quoteOfferService.findById(quoteOfferDTO.getQuoteOfferId());
		if(quoteOffer == null)
			throw new EntityDoesNotExistsException(QuoteOffer.class, quoteOfferDTO.getQuoteOfferId());
		// adding offer template if exist
		//boolean isOfferTemplateChange = false;
		/*OfferTemplate offerTemplate = null;
		if(!Strings.isEmpty(quoteOfferDTO.getOfferCode())) {
			offerTemplate = offerTemplateService.findByCode(quoteOfferDTO.getOfferCode());
			if(offerTemplate == null)
				throw new EntityDoesNotExistsException(OfferTemplate.class, quoteOfferDTO.getOfferCode());
			quoteOffer.setOfferTemplate(offerTemplate);
			isOfferTemplateChange = true;
		}*/
		// adding quote version if exist
		//boolean isQuoteVersionChange = false;
		/*QuoteVersion quoteVersion = null;
		if(quoteOfferDTO.getQuoteVersion() != null) {
			quoteVersion = quoteVersionService.findByQuoteAndVersion(quoteOfferDTO.getQuoteCode(), quoteOfferDTO.getQuoteVersion());
			if(quoteVersion == null)
				throw new EntityDoesNotExistsException(QuoteVersion.class, "(" + quoteOfferDTO.getQuoteCode() + "," + quoteOfferDTO.getQuoteVersion() + ")");
			quoteOffer.setQuoteVersion(quoteVersion);
			isQuoteVersionChange = true;
		}*/
		/*if(isOfferTemplateChange || isQuoteVersionChange) {
			if(quoteOfferService.findByTemplateAndQuoteVersion(quoteOffer.getOfferTemplate().getCode(), quoteVersion.getQuote().getCode(), quoteVersion.getQuoteVersion()) != null)
				throw new EntityAlreadyExistsException(QuoteOffer.class, quoteOfferDTO.getQuoteOfferId() + "");
		}*/
		// billing account
		if(!Strings.isEmpty(quoteOfferDTO.getBillableAccountCode()))
			quoteOffer.setBillableAccount(billingAccountService.findByCode(quoteOfferDTO.getBillableAccountCode()));
		if(!Strings.isEmpty(quoteOfferDTO.getQuoteLotCode()))
			quoteOffer.setQuoteLot(quoteLotService.findByCode(quoteOfferDTO.getQuoteLotCode()));
		processQuoteProduct(quoteOfferDTO, quoteOffer);
		return quoteOfferDTO;
	}
	
	private void processQuoteProduct(QuoteOfferDTO quoteOfferDTO, QuoteOffer quoteOffer) {
		var quoteProductDtos = quoteOfferDTO.getProducts();
		var hasQuoteProductDtos = quoteProductDtos != null && !quoteProductDtos.isEmpty();
		
		var existencQuoteProducts = quoteOffer.getQuoteProduct();
		var hasExistingQuotes = existencQuoteProducts != null && !existencQuoteProducts.isEmpty();
		
		if(hasQuoteProductDtos) {
			var newQuoteProducts = new ArrayList<QuoteProduct>();

			CpqQuote quote = null;
			if(!Strings.isEmpty(quoteOfferDTO.getQuoteCode())) {
				quote = cpqQuoteService.findByCode(quoteOfferDTO.getQuoteCode());
				if(quote == null)
					throw new EntityDoesNotExistsException(CpqQuote.class, quoteOfferDTO.getQuoteCode());
			}
			
			QuoteProduct quoteProduct = null;
			for (QuoteProductDTO quoteProductDto : quoteProductDtos) {
				quoteProduct = getQuoteProductFromDto(quoteProductDto, quoteOffer);
				if(quote != null)
					quoteProduct.setQuote(quote);
				if(quoteProductDto.getQuoteAttributes() != null) {
					for (QuoteAttributeDTO quoteAttributeDTO : quoteProductDto.getQuoteAttributes()) {
						getQuoteAttributeFromDto(quoteAttributeDTO, quoteProduct);
					}
				}
				newQuoteProducts.add(quoteProduct);
			}
			if(!hasExistingQuotes) {
				quoteOffer.getQuoteProduct().addAll(newQuoteProducts);
			}else {
				existencQuoteProducts.retainAll(newQuoteProducts);
				for (QuoteProduct qpNew : newQuoteProducts) {
					int index = existencQuoteProducts.indexOf(qpNew);
					if(index > 0) {
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
	
	private QuoteProduct getQuoteProductFromDto(QuoteProductDTO dto, QuoteOffer quoteOffer) {
		final ProductVersion productVersion = productVersionService.findByProductAndVersion(dto.getProductCode(), dto.getProductVersion());
		if(productVersion == null)
			throw new EntityDoesNotExistsException(QuoteVersion.class, dto.getProductCode() + "," + dto.getProductVersion());
		final QuoteProduct qp = quoteProductService.findByQuoteVersionAndQuoteOffer(productVersion.getId(), quoteOffer.getId());
		if(qp == null)
			throw new EntityDoesNotExistsException(QuoteVersion.class, productVersion.getId() + "," + quoteOffer.getId());
		
		return qp;
	}
	
	private QuoteAttribute getQuoteAttributeFromDto(QuoteAttributeDTO dto, QuoteProduct quoteProduct) {
		QuoteAttribute attribute = quoteAttributeService.findByAttributeAndQuoteProduct(dto.getQuoteAttributeCode(), quoteProduct.getId());
		if(attribute == null)
			throw new EntityDoesNotExistsException(QuoteAttribute.class, dto.getQuoteAttributeCode() + ","+ quoteProduct.getId());
		return attribute;
	}
	/**
	 * 
	 */
	
	
	
   
}
