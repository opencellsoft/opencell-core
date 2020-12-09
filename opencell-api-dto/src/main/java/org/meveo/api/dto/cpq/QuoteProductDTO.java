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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO to create or update a quoteProduct
 * 
 * @author Rachid.AIT
 * @lastModiedVersion 11.0 
 */
@XmlRootElement(name = "QuoteProductDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteProductDTO extends BaseEntityDto{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7231556751341912018L;

    @NotNull
	private String quoteCode;
    @NotNull
    private String productCode;

    @NotNull
    private int quoteVersion;
    
    private Integer productVersion;

    @NotNull
    private Integer quantity;
    
    private List<QuoteAttributeDTO> quoteAttributes=new ArrayList<QuoteAttributeDTO>();

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
	 * @return the productVersion
	 */
	public Integer getProductVersion() {
		return productVersion;
	}

	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(Integer productVersion) {
		this.productVersion = productVersion;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the quoteAttributes
	 */
	public List<QuoteAttributeDTO> getQuoteAttributes() {
		return quoteAttributes;
	}

	/**
	 * @param quoteAttributes the quoteAttributes to set
	 */
	public void setQuoteAttributes(List<QuoteAttributeDTO> quoteAttributes) {
		this.quoteAttributes = quoteAttributes;
	}

	/**
	 * @return the cpqQuoteCode
	 */
	public String getQuoteCode() {
		return quoteCode;
	}

	/**
	 * @param cpqQuoteCode the cpqQuoteCode to set
	 */
	public void setQuoteCode(String cpqQuoteCode) {
		this.quoteCode = cpqQuoteCode;
	}

	/**
	 * @return the quoteVersion
	 */
	public int getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(int quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


    
    
    
    
    
   
}