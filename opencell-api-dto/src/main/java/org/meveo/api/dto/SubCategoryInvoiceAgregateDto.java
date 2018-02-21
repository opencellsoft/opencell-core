/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.SubCategoryInvoiceAgregate;

/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "SubCategoryInvoiceAgregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubCategoryInvoiceAgregateDto implements Serializable {

	private static final long serialVersionUID = 6165612614574594919L;

	private String type;
	private Integer itemNumber;
	private String accountingCode;
	private String description;
	private BigDecimal taxPercent;
	private BigDecimal quantity;
	private BigDecimal discount;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountTax;
	private BigDecimal amountWithTax;
	
    @XmlElement(required = true)
	private String invoiceSubCategoryCode;
	private List<String> taxesCodes=new ArrayList<String>();
	private String userAccountCode;
	@XmlElementWrapper
    @XmlElement(name="ratedTransaction")
	private List<RatedTransactionDto> ratedTransactions = new ArrayList<RatedTransactionDto>();
	
	private String discountPlanCode;
	private String discountPlanItemCode;
	private BigDecimal discountPercent;

	public SubCategoryInvoiceAgregateDto(SubCategoryInvoiceAgregate e) {
		if (e != null) {
			discountPlanCode = e.getDiscountPlanCode();
			discountPlanItemCode = e.getDiscountPlanItemCode();
			discountPercent = e.getDiscountPercent();
			itemNumber = e.getItemNumber();
            if (e.getAccountingCode() != null) {
                accountingCode = e.getAccountingCode().getCode();
            }
			description = e.getDescription();
			taxPercent = e.getTaxPercent();
			quantity = e.getQuantity();
			amountWithoutTax = e.getAmountWithoutTax();
			amountTax = e.getAmountTax();
			amountWithTax = e.getAmountTax();

			if (e.getInvoiceSubCategory() != null) {
				invoiceSubCategoryCode = e.getInvoiceSubCategory().getCode();
			}
			if (e.getUserAccount() != null) {
				userAccountCode = e.getUserAccount().getCode();
			}
		}
	}

	public SubCategoryInvoiceAgregateDto() {

	}
	
	public Integer getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(String accountingCode) {
		this.accountingCode = accountingCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public List<RatedTransactionDto> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransactionDto> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInvoiceSubCategoryCode() {
		return invoiceSubCategoryCode;
	}

	public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
		this.invoiceSubCategoryCode = invoiceSubCategoryCode;
	}

	public List<String> getTaxesCodes() {
		return taxesCodes;
	}

	public void setTaxesCodes(List<String> taxesCodes) {
		this.taxesCodes = taxesCodes;
	}

	public String getUserAccountCode() {
		return userAccountCode;
	}

	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}

	public String getDiscountPlanCode() {
		return discountPlanCode;
	}

	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}

	public String getDiscountPlanItemCode() {
		return discountPlanItemCode;
	}

	public void setDiscountPlanItemCode(String discountPlanItemCode) {
		this.discountPlanItemCode = discountPlanItemCode;
	}

	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(BigDecimal discountPercent) {
		this.discountPercent = discountPercent;
	}


	
	

}
