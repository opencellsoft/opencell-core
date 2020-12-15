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
import org.meveo.api.dto.response.PagingAndFiltering;

/**
 * The Class ServiceDto.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "CustomerContextDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerContextDTO extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2850157608109341441L;



    /**
     * The billing account code
     */
    private String billingAccountCode;
    
   /**
    * The the seller tags
    */
    private List<String> sellerTags;
    
    /**
    * The customer tags
    */
    private List<String> customerTags;
    /**
     * The contract code
     */
    private String contractCode;
    
    /**
     * requested tag types
     */
     private List<String> requestedTagTypes=new ArrayList<String>();
    
    /**
     * paging And Filtering
     */
    private PagingAndFiltering pagingAndFiltering;
    
    
	public PagingAndFiltering getPagingAndFiltering() {
		return pagingAndFiltering;
	}
	public void setPagingAndFiltering(PagingAndFiltering pagingAndFiltering) {
		this.pagingAndFiltering = pagingAndFiltering;
	}
	public String getBillingAccountCode() {
		return billingAccountCode;
	}
	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}

	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	/**
	 * @return the sellerTags
	 */
	public List<String> getSellerTags() {
		return sellerTags;
	}
	/**
	 * @param sellerTags the sellerTags to set
	 */
	public void setSellerTags(List<String> sellerTags) {
		this.sellerTags = sellerTags;
	}
	/**
	 * @return the customerTags
	 */
	public List<String> getCustomerTags() {
		return customerTags;
	}
	/**
	 * @param customerTags the customerTags to set
	 */
	public void setCustomerTags(List<String> customerTags) {
		this.customerTags = customerTags;
	}
	/**
	 * @return the requestedTagTypes
	 */
	public List<String> getRequestedTagTypes() {
		return requestedTagTypes;
	}
	/**
	 * @param requestedTagTypes the requestedTagTypes to set
	 */
	public void setRequestedTagTypes(List<String> requestedTagTypes) {
		this.requestedTagTypes = requestedTagTypes;
	}


    
    
    
    
}