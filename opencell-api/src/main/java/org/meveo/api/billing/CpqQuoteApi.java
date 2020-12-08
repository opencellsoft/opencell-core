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

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.cpq.CpqQuotesListResponseDto;
import org.meveo.api.dto.response.cpq.GetListQuotesDtoResponse;
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
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.cpq.CpqQuoteService;

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
	
	public Long createQuote(QuoteDTO quote) {
		if(Strings.isEmpty(quote.getApplicantAccountCode())) {
			missingParameters.add("applicantAccountCode");
		}
		if(Strings.isEmpty(quote.getApplicantAccountCode())) {
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
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		
		return cpqQuote.getId();
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
	
	public void updateQuote(String quoteCode, QuoteDTO quoteDto) {
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
		}catch(BusinessApiException e) {
			throw new MeveoApiException(e);
		}
		
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
	
	
   
}
