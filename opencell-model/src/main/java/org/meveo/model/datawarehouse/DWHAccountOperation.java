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
package org.meveo.model.datawarehouse;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.AccountingCode;

/**
 * @deprecated As of version 5.0. Not used anymore.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Deprecated
@Entity
@Table(name = "dwh_account_operation")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "dwh_account_operation_seq"), })
public class DWHAccountOperation extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "account_code", length = 50)
	@Size(max = 50)
	private String accountCode;

	@Column(name = "account_description", length = 255)
    @Size(max = 255)
	private String accountDescription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_code_id")
	private AccountingCode accountingCode;

	@Column(name = "accounting_code_client_side", length = 255)
    @Size(max = 255)
	private String accountingCodeClientSide;

	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;

	@Column(name = "due_date")
	@Temporal(TemporalType.DATE)
	private Date dueDate;

	@Column(name = "due_month")
	private Integer dueMonth;

	@Column(name = "category")
	private int category;

	@Column(name = "type")
	private int type;

	@Column(name = "occ_code", length = 10)
	@Size(max = 10)
	private String occCode;

	@Column(name = "occ_description", length = 255)
    @Size(max = 255)
	private String occDescription;

	@Column(name = "reference", length = 50)
    @Size(max = 50)
	private String reference;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "status")
	private int status;

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}

	public AccountingCode getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(AccountingCode accountingCode) {
		this.accountingCode = accountingCode;
	}

	public String getAccountingCodeClientSide() {
		return accountingCodeClientSide;
	}

	public void setAccountingCodeClientSide(String accountingCodeClientSide) {
		this.accountingCodeClientSide = accountingCodeClientSide;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getDueMonth() {
		return dueMonth;
	}

	public void setDueMonth(Integer dueMonth) {
		this.dueMonth = dueMonth;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
