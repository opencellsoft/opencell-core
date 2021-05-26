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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;

/**
 * The Class AccountingArticlePrices.
 *
 * @author Rachid.AIT
 * @lastModifiedVersion 11.0.0
 */
@XmlRootElement(name = "PriceDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class PriceDTO extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1054495149414405858L;

	
	@XmlAttribute
	private PriceTypeEnum priceType;

    private BigDecimal amountWithtax;
    
    private BigDecimal unitPriceWithoutTax;

    private BigDecimal amountWithoutTax;
    
    private BigDecimal amountWithoutTaxWithDiscount;

    private BigDecimal taxAmount;
    
    private BigDecimal taxRate;
    
    private Boolean priceOverCharged;

    private String currencyCode;
    
    private Long recurrenceDuration;
    private String recurrencePeriodicity;
    private String chargeCode;
    private String chargeLabel;
    
    private String taxCategory;
    private String taxCode;
    
    private CustomFieldsDto customFields;
    
    
	public PriceDTO(QuotePrice quotePrice) {
		super();
		priceType=quotePrice.getPriceTypeEnum();
	    unitPriceWithoutTax=quotePrice.getUnitPriceWithoutTax();
	    taxAmount=quotePrice.getTaxAmount();
	    taxRate=quotePrice.getTaxRate();
	    priceOverCharged=quotePrice.getPriceOverCharged();
	    currencyCode=quotePrice.getCurrencyCode();
	    recurrenceDuration=quotePrice.getRecurrenceDuration();
	    recurrencePeriodicity=quotePrice.getRecurrencePeriodicity();
		if(recurrenceDuration != null){
			amountWithtax=quotePrice.getAmountWithTax().multiply(BigDecimal.valueOf(recurrenceDuration));
			amountWithoutTax=quotePrice.getAmountWithoutTax().multiply(BigDecimal.valueOf(recurrenceDuration));
			amountWithoutTaxWithDiscount=quotePrice.getAmountWithoutTaxWithDiscount() != null ? quotePrice.getAmountWithoutTaxWithDiscount().multiply(BigDecimal.valueOf(recurrenceDuration)) : null;
		}else {
			amountWithtax=quotePrice.getAmountWithTax();
			amountWithoutTax=quotePrice.getAmountWithoutTax();
			amountWithoutTaxWithDiscount=quotePrice.getAmountWithoutTaxWithDiscount();
		}

	    chargeCode=quotePrice.getChargeTemplate()!=null?quotePrice.getChargeTemplate().getCode():null;
	    chargeLabel=quotePrice.getChargeTemplate()!=null?quotePrice.getChargeTemplate().getDescription():null;
	   TaxCategory taxCategoryEntity = quotePrice.getQuoteArticleLine() != null ? quotePrice.getQuoteArticleLine().getBillableAccount().getTaxCategory()!=null ? quotePrice.getQuoteArticleLine().getBillableAccount().getTaxCategory(): 
	    	quotePrice.getQuoteArticleLine().getBillableAccount().getCustomerAccount().getCustomer().getCustomerCategory().getTaxCategory() : null;
	   taxCategory=taxCategoryEntity!=null?taxCategoryEntity.getCode():null;
	   TaxClass taxClass=quotePrice.getQuoteArticleLine() != null ? quotePrice.getQuoteArticleLine().getAccountingArticle().getTaxClass() : null;
	   taxCode=taxClass!=null?taxClass.getCode():null;
		
	}
	public PriceDTO(QuotePrice quotePrice,CustomFieldsDto customFields) {
		this(quotePrice);
		this.customFields = customFields;
	}
	
	
	
	public PriceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PriceTypeEnum getPriceType() {
		return priceType;
	}
	public void setPriceType(PriceTypeEnum priceType) {
		this.priceType = priceType;
	}
	public BigDecimal getAmountWithtax() {
		return amountWithtax;
	}
	public void setAmountWithtax(BigDecimal amountWithtax) {
		this.amountWithtax = amountWithtax;
	}
	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}
	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public Long getRecurrenceDuration() {
		return recurrenceDuration;
	}
	public void setRecurrenceDuration(Long recurrenceDuration) {
		this.recurrenceDuration = recurrenceDuration;
	}
	public String getRecurrencePeriodicity() {
		return recurrencePeriodicity;
	}
	public void setRecurrencePeriodicity(String recurrencePeriodicity) {
		this.recurrencePeriodicity = recurrencePeriodicity;
	}
	public BigDecimal getUnitPriceWithoutTax() {
		return unitPriceWithoutTax;
	}
	public void setUnitPriceWithoutTax(BigDecimal unitPriceWithoutTax) {
		this.unitPriceWithoutTax = unitPriceWithoutTax;
	}
	public BigDecimal getAmountWithoutTaxWithDiscount() {
		return amountWithoutTaxWithDiscount;
	}
	public void setAmountWithoutTaxWithDiscount(BigDecimal amountWithoutTaxWithDiscount) {
		this.amountWithoutTaxWithDiscount = amountWithoutTaxWithDiscount;
	}
	/**
	 * @return the priceOverCharged
	 */
	public Boolean getPriceOverCharged() {
		return priceOverCharged;
	}
	/**
	 * @param priceOverCharged the priceOverCharged to set
	 */
	public void setPriceOverCharged(Boolean priceOverCharged) {
		this.priceOverCharged = priceOverCharged;
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}

	/**
	 * @param chargeCode the chargeCode to set
	 */
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the chargeLabel
	 */
	public String getChargeLabel() {
		return chargeLabel;
	}

	/**
	 * @param chargeLabel the chargeLabel to set
	 */
	public void setChargeLabel(String chargeLabel) {
		this.chargeLabel = chargeLabel;
	}

	public String getTaxCategory() {
		return taxCategory;
	}

	public void setTaxCategory(String taxCategory) {
		this.taxCategory = taxCategory;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	
	
    
    

    
  
    
    
    
}