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
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

/**
 * Account Transaction.
 * 
 * @author Tyshan(tyshan@manaty.net) Nov 12, 2010 3:04:33 AM
 * 
 */
@Entity
@Table(name = "AR_ACCOUNT_OPERATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRANSACTION_TYPE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_ACCOUNT_OPERATION_SEQ")
public class AccountOperation extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "DUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date dueDate;

	@Column(name = "TRANSACTION_TYPE", insertable = false, updatable = false)
	private String type;

	@Column(name = "TRANSACTION_DATE")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;

	@Column(name = "TRANSACTION_CATEGORY")
	@Enumerated(EnumType.STRING)
	private OperationCategoryEnum transactionCategory;

	@Column(name = "REFERENCE")
	private String reference;

	@Column(name = "ACCOUNT_CODE")
	private String accountCode;

	@Column(name = "ACCOUNT_CODE_CLIENT_SIDE")
	private String accountCodeClientSide;

	@Column(name = "AMOUNT", precision = 23, scale = 12)
	private BigDecimal amount;

	@Column(name = "MATCHING_AMOUNT", precision = 23, scale = 12)
	private BigDecimal matchingAmount = BigDecimal.ZERO;

	@Column(name = "UN_MATCHING_AMOUNT", precision = 23, scale = 12)
	private BigDecimal unMatchingAmount = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ACCOUNT_ID")
	private CustomerAccount customerAccount;

	@Enumerated(EnumType.STRING)
	@Column(name = "MATCHING_STATUS")
	private MatchingStatusEnum matchingStatus;

	@OneToMany(mappedBy = "accountOperation")
	private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

	@Column(name = "OCC_CODE")
	private String occCode;

	@Column(name = "OCC_DESCRIPTION")
	private String occDescription;

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public OperationCategoryEnum getTransactionCategory() {
		return transactionCategory;
	}

	public void setTransactionCategory(OperationCategoryEnum transactionCategory) {
		this.transactionCategory = transactionCategory;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getMatchingAmount() {
		return matchingAmount;
	}

	public void setMatchingAmount(BigDecimal matchingAmount) {
		this.matchingAmount = matchingAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getUnMatchingAmount() {
		return unMatchingAmount;
	}

	public void setUnMatchingAmount(BigDecimal unMatchingAmount) {
		this.unMatchingAmount = unMatchingAmount;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public String getAccountCodeClientSide() {
		return accountCodeClientSide;
	}

	public void setAccountCodeClientSide(String accountCodeClientSide) {
		this.accountCodeClientSide = accountCodeClientSide;
	}

	public MatchingStatusEnum getMatchingStatus() {
		return matchingStatus;
	}

	public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
		this.matchingStatus = matchingStatus;
	}

	public String getOccCode() {
		return occCode;
	}

	public void setOccCode(String occCode) {
		this.occCode = occCode;
	}

	public String getOccDescription() {
		return occDescription;
	}

	public void setOccDescription(String occDescription) {
		this.occDescription = occDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((occCode == null) ? 0 : occCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		AccountOperation other = (AccountOperation) obj;
		if (occCode == null) {
			if (other.occCode != null)
				return false;
		} else if (!occCode.equals(other.occCode))
			return false;
		return true;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
		this.matchingAmounts = matchingAmounts;
	}

	public List<MatchingAmount> getMatchingAmounts() {
		return matchingAmounts;
	}
}
