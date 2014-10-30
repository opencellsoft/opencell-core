/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "subCategoryInvoiceAgregate")
@XmlAccessorType(XmlAccessType.FIELD)
public  class SubCategoryInvoiceAgregateDto {

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

	private List<RatedTransactionDto> ratedTransactions = new ArrayList<RatedTransactionDto>();


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





	
	
}
