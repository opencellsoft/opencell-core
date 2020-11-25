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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class ServiceDto.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "OfferContextDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferContextDTO extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2850157608109341441L;


	private CustomerContextDTO customerContextDTO;
	
	private String customerServiceCode ;
	
    /**
     * The selected products in the quote
     */
    private List<ProductContextDTO> products;
    
    /**
     * The offer code
     */
    
    private String offerCode; 
    
    /**
     * The currentProductCode 
     */
    private String currentProductCode;
    
    
    /**
     * The currentProductVersion
     */
    private int currentProductVersion;
    
	 
	public List<ProductContextDTO> getProducts() {
		return products;
	}
	public void setProducts(List<ProductContextDTO> products) {
		this.products = products;
	}
	public CustomerContextDTO getCustomerContextDTO() {
		return customerContextDTO;
	}
	public void setCustomerContextDTO(CustomerContextDTO customerContextDTO) {
		this.customerContextDTO = customerContextDTO;
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
	 * @return the currentProductCode
	 */
	public String getCurrentProductCode() {
		return currentProductCode;
	}
	/**
	 * @param currentProductCode the currentProductCode to set
	 */
	public void setCurrentProductCode(String currentProductCode) {
		this.currentProductCode = currentProductCode;
	}
	/**
	 * @return the currentProductVersion
	 */
	public int getCurrentProductVersion() {
		return currentProductVersion;
	}
	/**
	 * @param currentProductVersion the currentProductVersion to set
	 */
	public void setCurrentProductVersion(int currentProductVersion) {
		this.currentProductVersion = currentProductVersion;
	}

	

	

    
    
    
    
}