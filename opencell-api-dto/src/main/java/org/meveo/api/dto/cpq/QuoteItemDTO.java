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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.DatePeriod;
import org.meveo.model.quote.QuoteItem;
import org.meveo.model.quote.QuoteStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * DTO to create or update a quote
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteItemDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteItemDTO extends BaseEntityDto{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;
	
	private Long quoteItemId;
	
    private String quote;

    private Integer quoteVersion;

    private String customerServiceCode;
    
    private String offerCode;
    
    private String productCode;
    
    private Integer productQuantity;
    
    private Integer serviceQuantity;
    
    private String serviceCode;
    
    private Object serviceValue;

    private String billableAccountCode;
    
    
	/**
	 * @return the quoteItemId
	 */
	public Long getQuoteItemId() {
		return quoteItemId;
	}

	/**
	 * @param quoteItemId the quoteItemId to set
	 */
	public void setQuoteItemId(Long quoteItemId) {
		this.quoteItemId = quoteItemId;
	}

	/**
	 * @return the quote
	 */
	public String getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(String quote) {
		this.quote = quote;
	}

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
	 * @return the customerServiceCode
	 */
	public String getCustomerServiceCode() {
		return customerServiceCode;
	}

	/**
	 * @param customerServiceCode the customerServiceCode to set
	 */
	public void setCustomerServiceCode(String customerServiceCode) {
		this.customerServiceCode = customerServiceCode;
	}

	/**
	 * @return the offerCode
	 */
	public String getOfferCode() {
		return offerCode;
	}

	/**
	 * @param offerCode the offerCode to set
	 */
	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * @param serviceCode the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * @return the serviceValue
	 */
	public Object getServiceValue() {
		return serviceValue;
	}

	/**
	 * @param serviceValue the serviceValue to set
	 */
	public void setServiceValue(Object serviceValue) {
		this.serviceValue = serviceValue;
	}

	/**
	 * @return the productQuantity
	 */
	public Integer getProductQuantity() {
		return productQuantity;
	}

	/**
	 * @param productQuantity the productQuantity to set
	 */
	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

	/**
	 * @return the serviceQuantity
	 */
	public Integer getServiceQuantity() {
		return serviceQuantity;
	}

	/**
	 * @param serviceQuantity the serviceQuantity to set
	 */
	public void setServiceQuantity(Integer serviceQuantity) {
		this.serviceQuantity = serviceQuantity;
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
    
    
    
	
    
   
}