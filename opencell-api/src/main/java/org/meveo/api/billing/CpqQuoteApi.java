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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.QuoteDTO;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqQuote;
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
	
	public void updateQuote(String quoteCode, QuoteDTO quote) {
		if(Strings.isEmpty(quoteCode)) {
			missingParameters.add("quoteCode");
		}
	}
	
	/*
	 *
	
    private String externalId;
	 */
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
