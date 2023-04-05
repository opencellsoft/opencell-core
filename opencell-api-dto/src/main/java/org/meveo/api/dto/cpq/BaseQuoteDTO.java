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

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.DatePeriod;
import org.meveo.model.quote.QuoteStatusEnum;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO to create or update a quote
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "BaseQuoteDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseQuoteDTO extends BusinessEntityDto{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;


    @Schema(description = "The date of the quote")
    private Date quoteDate;

    private DatePeriod validity = new DatePeriod();

    @Schema(description = "The status of the quote", example = "Possible value : IN_PROGRESS, PENDING, CANCELLED, APPROVED, ACCEPTED, REJECTED", defaultValue = "IN_PROGRESS")
    private String status = QuoteStatusEnum.IN_PROGRESS.toString();
 
    @NotNull
    @Schema(description = "The code of the billing account of the applicant", required = true)
	private String applicantAccountCode;
    
    @Schema(description = "The code of the billing account of the billable")
	private String billableAccountCode;
    
    @Schema(description = "The delivery date of quote lot ")
	private Date deliveryDate;
	
    @Schema(description = "Duration of the quote lot")
	private int quoteLotDuration;
    
    @Schema(description = "The opportunity ref")
	private String opportunityRef;
    
    @Schema(description = "The code of the seller")
	private String sellerCode;
    
    @Schema(description = "The send date")
	private Date sendDate;
    
    @Schema(description = "The quote number")
	private String quoteNumber;

    @Schema(description = "The external id")
    private String externalId;

    @Schema(description = "The status date")
	private Date statusDate;
	
	@Schema(description = "The code of the user account")
	private String userAccountCode;
	
	@Schema(description = "The sales person name")
	private String salesPersonName;
	
	@Schema(description = "Contract code")
	private String contractCode;
	
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
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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

	/**
	 * @return the sellerCode
	 */
	public String getSellerCode() {
		return sellerCode;
	}

	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the quoteLotDuration
	 */
	public int getQuoteLotDuration() {
		return quoteLotDuration;
	}

	/**
	 * @param quoteLotDuration the quoteLotDuration to set
	 */
	public void setQuoteLotDuration(int quoteLotDuration) {
		this.quoteLotDuration = quoteLotDuration;
	}

	/**
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * @return the quoteNumber
	 */
	public String getQuoteNumber() {
		return quoteNumber;
	}

	/**
	 * @param quoteNumber the quoteNumber to set
	 */
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the user account code
	 */
	public String getUserAccountCode() {
		return userAccountCode;
	}

	/**
	 * @param userAccountCode the user account code to set
	 */
	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}

	/**
	 * @return the sales person name
	 */
	public String getSalesPersonName() {
		return salesPersonName;
	}

	/**
	 * @param salesPersonName the sales person name to set
	 */
	public void setSalesPersonName(String salesPersonName) {
		this.salesPersonName = salesPersonName;
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
}