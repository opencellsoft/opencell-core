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
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

import io.swagger.v3.oas.annotations.media.Schema;

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

	  /**
     * The offer code
     */
    @Schema(description = "code of offer template")
    private String offerCode;

    @Schema(description = "information for customer")
	private CustomerContextDTO customerContextDTO;

    @Schema(description = "code of quote lot")
	private String quoteLotCode ;
	
	
	
    /**
     * The selected products in the quote
     */
    @Schema(description = "The selected products in the quote")
    private List<ProductContextDTO> selectedProducts=new ArrayList<ProductContextDTO>();
    
    
    
    @XmlElement
    @Schema(description = "The selected services in the quote with their values, DO NOT change to Map. Used LinkedHashMap to preserve the item order during read/write")
    private LinkedHashMap<String, Object> selectedOfferAttributes;
    
    @Schema(description = "Context config")
	private OfferContextConfigDTO config = new OfferContextConfigDTO();
    
    
	 
	public List<ProductContextDTO> getSelectedProducts() {
		return selectedProducts;
	}
	public void setSelectedProducts(List<ProductContextDTO> selectedProducts) {
		this.selectedProducts = selectedProducts;
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
	public String getQuoteLotCode() {
		return quoteLotCode;
	}
	/**
	 * @param customerServiceCode the customerServiceCode to set
	 */
	public void setQuoteLotCode(String quoteLotCode) {
		this.quoteLotCode = quoteLotCode;
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
	public LinkedHashMap<String, Object> getSelectedOfferAttributes() {
		return selectedOfferAttributes;
	}
	public void setSelectedOfferAttributes(LinkedHashMap<String, Object> selectedOfferAttributes) {
		this.selectedOfferAttributes = selectedOfferAttributes;
	}
	public OfferContextConfigDTO getConfig() {
		return config;
	}
	public void setConfig(OfferContextConfigDTO config) {
		this.config = config;
	}
 

	

	

    
    
    
    
}