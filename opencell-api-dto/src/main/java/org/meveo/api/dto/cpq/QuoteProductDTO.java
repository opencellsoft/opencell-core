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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.quote.QuoteProduct;

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
    private BigDecimal quantity;
    
    private List<QuoteAttributeDTO> quoteAttributes=new ArrayList<QuoteAttributeDTO>();

    private String quoteLotCode;
    
    private List<AccountingArticlePricesDTO> accountingArticlePrices = new ArrayList<AccountingArticlePricesDTO>();
    
    
    

	public QuoteProductDTO(QuoteProduct quoteProduct) {
		super();
		quoteCode=quoteProduct.getQuote()!=null?quoteProduct.getQuote().getCode():null;
		productCode=quoteProduct.getProductVersion().getProduct().getCode();
		productVersion=quoteProduct.getProductVersion().getCurrentVersion();
		quantity=quoteProduct.getQuantity();
		quoteLotCode=quoteProduct.getQuoteLot()!=null?quoteProduct.getQuoteLot().getCode():null;
		
	}
	
	public QuoteProductDTO(QuoteProduct quoteProduct, boolean loadAttributes) {
		new QuoteProductDTO(quoteProduct);
		if(loadAttributes) {
			quoteAttributes=new ArrayList<QuoteAttributeDTO>();
			for(QuoteAttribute quoteAttribute:quoteProduct.getQuoteAttributes()) {
				quoteAttributes.add(new QuoteAttributeDTO(quoteAttribute));
			}
		}
		
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
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
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

	/**
	 * @return the quoteLotCode
	 */
	public String getQuoteLotCode() {
		return quoteLotCode;
	}

	/**
	 * @param quoteLotCode the quoteLotCode to set
	 */
	public void setQuoteLotCode(String quoteLotCode) {
		this.quoteLotCode = quoteLotCode;
	}

	/**
	 * @return the accountingArticlePrices
	 */
	public List<AccountingArticlePricesDTO> getAccountingArticlePrices() {
		return accountingArticlePrices;
	}

	/**
	 * @param accountingArticlePrices the accountingArticlePrices to set
	 */
	public void setAccountingArticlePrices(List<AccountingArticlePricesDTO> accountingArticlePrices) {
		this.accountingArticlePrices = accountingArticlePrices;
	}


    
    
    
    
    
   
}