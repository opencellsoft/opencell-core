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

package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.DatePeriod;
import org.meveo.model.quote.QuoteStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * DTO to create or update a quote
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteDTO extends BaseEntityDto{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;
	
	
    private Integer quoteVersion;

    private Date quoteDate = new Date();

    private DatePeriod validity = new DatePeriod();

    private QuoteStatusEnum status = QuoteStatusEnum.IN_PROGRESS;
    
    private List<QuoteOfferDTO > quoteItems = new ArrayList<QuoteOfferDTO>();
    @NotNull
	private String applicantAccountCode;
	private String billableAccountCode;
	
	private String contractCode;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date customerServiceDateBegin;
	private int customerServiceDuration;
	private String opportunityRef;


    private String externalId;

	/**
	 * @return the quoteVersion
	 */
	public Integer getQuoteVersion() {
		return quoteVersion;
	}


	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(Integer quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


	/**
	 * @return the quoteDate
	 */
	public Date getQuoteDate() {
		return quoteDate;
	}


	/**
	 * @param quoteDate the quoteDate to set
	 */
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}


	/**
	 * @return the validity
	 */
	public DatePeriod getValidity() {
		return validity;
	}


	/**
	 * @param validity the validity to set
	 */
	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}


	/**
	 * @return the status
	 */
	public QuoteStatusEnum getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(QuoteStatusEnum status) {
		this.status = status;
	}


	/**
	 * @return the quoteItems
	 */
	public List<QuoteOfferDTO> getQuoteItems() {
		return quoteItems;
	}


	/**
	 * @param quoteItems the quoteItems to set
	 */
	public void setQuoteItems(List<QuoteOfferDTO> quoteItems) {
		this.quoteItems = quoteItems;
	}


	/**
	 * @return the applicantAccountCode
	 */
	public String getApplicantAccountCode() {
		return applicantAccountCode;
	}


	/**
	 * @param applicantAccountCode the applicantAccountCode to set
	 */
	public void setApplicantAccountCode(String applicantAccountCode) {
		this.applicantAccountCode = applicantAccountCode;
	}


	/**
	 * @return the billableAccountCode
	 */
	public String getBillableAccountCode() {
		return billableAccountCode;
	}


	/**
	 * @param billableAccountCode the billableAccountCode to set
	 */
	public void setBillableAccountCode(String billableAccountCode) {
		this.billableAccountCode = billableAccountCode;
	}


	/**
	 * @return the contractCode
	 */
	public String getContractCode() {
		return contractCode;
	}


	/**
	 * @param contractCode the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}


	/**
	 * @return the customerServiceDateBegin
	 */
	public Date getCustomerServiceDateBegin() {
		return customerServiceDateBegin;
	}


	/**
	 * @param customerServiceDateBegin the customerServiceDateBegin to set
	 */
	public void setCustomerServiceDateBegin(Date customerServiceDateBegin) {
		this.customerServiceDateBegin = customerServiceDateBegin;
	}


	/**
	 * @return the customerServiceDuration
	 */
	public int getCustomerServiceDuration() {
		return customerServiceDuration;
	}


	/**
	 * @param customerServiceDuration the customerServiceDuration to set
	 */
	public void setCustomerServiceDuration(int customerServiceDuration) {
		this.customerServiceDuration = customerServiceDuration;
	}


	/**
	 * @return the opportunityRef
	 */
	public String getOpportunityRef() {
		return opportunityRef;
	}


	/**
	 * @param opportunityRef the opportunityRef to set
	 */
	public void setOpportunityRef(String opportunityRef) {
		this.opportunityRef = opportunityRef;
	}


	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}


	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}


	
    
   
}