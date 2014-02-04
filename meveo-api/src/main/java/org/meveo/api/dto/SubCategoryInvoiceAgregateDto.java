/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    
	private Integer itemNumber;

	private String accountingCode;

	private String description;

	private BigDecimal taxPercent;
	private BigDecimal quantity;

	private BigDecimal discount;
	private BigDecimal amountWithoutTax;

	private BigDecimal amountTax;

	private BigDecimal amountWithTax;

	private List<RatedTransactionDTO> ratedTransactions = new ArrayList<RatedTransactionDTO>();


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


	public List<RatedTransactionDTO> getRatedTransactions() {
		return ratedTransactions;
	}


	public void setRatedTransactions(List<RatedTransactionDTO> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}





	
	
}
