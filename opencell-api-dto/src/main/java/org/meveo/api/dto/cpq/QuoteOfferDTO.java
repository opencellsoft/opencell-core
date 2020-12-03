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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO to create or update a quoteOffer
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteOfferDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteOfferDTO extends BaseEntityDto{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8115890992793236496L;
	
	private Long quoteOfferId;
	
    private String quoteCode;

    private Integer quoteVersion;

    private String customerServiceCode;
    
    private String offerCode;

    private String billableAccountCode;
    
    private List<QuoteProductDTO> products = new ArrayList<QuoteProductDTO>();

	/**
	 * @return the quoteOfferId
	 */
	public Long getQuoteOfferId() {
		return quoteOfferId;
	}

	/**
	 * @param quoteOfferId the quoteOfferId to set
	 */
	public void setQuoteOfferId(Long quoteOfferId) {
		this.quoteOfferId = quoteOfferId;
	}

	/**
	 * @return the quoteCode
	 */
	public String getQuoteCode() {
		return quoteCode;
	}

	/**
	 * @param quoteCode the quoteCode to set
	 */
	public void setQuoteCode(String quoteCode) {
		this.quoteCode = quoteCode;
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
	 * @return the products
	 */
	public List<QuoteProductDTO> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<QuoteProductDTO> products) {
		this.products = products;
	}
    
    
    
    
    
    
   
}