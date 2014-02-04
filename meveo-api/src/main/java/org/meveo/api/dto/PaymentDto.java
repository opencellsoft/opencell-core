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
import java.util.Date;
import java.util.List;


public class PaymentDto extends BaseDto {

	private static final long serialVersionUID = 1L;

	
	private String paymentMethod; 
	private String occTemplateCode; 
	private BigDecimal amount; 
	private String customerAccountCode; 
	private String reference; 
	private String bankLot; 
	private Date depositDate; 
	private Date bankCollectionDate;
	private Date dueDate; 
	private Date transactionDate; 
	private List<String> listOCCReferenceforMatching;
	private boolean isToMatching;
	

	public String getOccTemplateCode() {
		return occTemplateCode;
	}
	public void setOccTemplateCode(String occTemplateCode) {
		this.occTemplateCode = occTemplateCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getCustomerAccountCode() {
		return customerAccountCode;
	}
	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getBankLot() {
		return bankLot;
	}
	public void setBankLot(String bankLot) {
		this.bankLot = bankLot;
	}
	public Date getDepositDate() {
		return depositDate;
	}
	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}
	public Date getBankCollectionDate() {
		return bankCollectionDate;
	}
	public void setBankCollectionDate(Date bankCollectionDate) {
		this.bankCollectionDate = bankCollectionDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public List<String> getListOCCReferenceforMatching() {
		return listOCCReferenceforMatching;
	}
	public void setListOCCReferenceforMatching(
			List<String> listOCCReferenceforMatching) {
		this.listOCCReferenceforMatching = listOCCReferenceforMatching;
	}
	public boolean isToMatching() {
		return isToMatching;
	}
	public void setToMatching(boolean isToMatching) {
		this.isToMatching = isToMatching;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	
	
	
	

	
}
