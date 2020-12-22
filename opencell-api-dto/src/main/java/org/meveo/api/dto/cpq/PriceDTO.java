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
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.enums.PriceTypeEnum;

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
    
    

    
  
    
    
    
}