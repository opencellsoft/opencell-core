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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.IEntity;
import org.meveo.model.IJPAVersionedEntity;
import org.meveo.model.billing.AccountingCode;

@Entity
@Table(name = "dwh_journal_entries", uniqueConstraints = @UniqueConstraint(columnNames = {
		"origin_id", "invoice_number", "accounting_code_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "dwh_journal_entries_seq"), })
public class JournalEntry implements IEntity, IJPAVersionedEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
	@Column(name = "id")
    @Access(AccessType.PROPERTY)
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private JournalEntryTypeEnum type;

	@Column(name = "origin_id")
	private Long originId;

	@Column(name = "invoice_number", length = 20)
    @Size(max = 20)
	private String invoiceNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_code_id")
	private AccountingCode accountingCode;

	@Column(name = "invoice_date")
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;

	@Column(name = "customer_account_code", length = 20)
    @Size(max = 20)
	private String customerAccountCode;

	@Column(name = "tax_code", length = 10)
    @Size(max = 10)
	private String taxCode;

	@Column(name = "tax_description", length = 20)
    @Size(max = 20)
	private String taxDescription;

	@Column(name = "tax_percent")
	private BigDecimal taxPercent;

	@Column(name = "sub_cat_desc", length = 50)
    @Size(max = 50)
	private String subCatDescription;

	@Column(name = "amount_without_tax", precision = 23, scale = 12)
	private BigDecimal amountWithoutTax;

	@Column(name = "amount_tax", precision = 23, scale = 12)
	private BigDecimal amountTax;

	@Column(name = "amount_with_tax", precision = 23, scale = 12)
	private BigDecimal amountWithTax;

	public JournalEntryTypeEnum getType() {
		return type;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setType(JournalEntryTypeEnum type) {
		this.type = type;
	}

	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long originId) {
		this.originId = originId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public AccountingCode getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(AccountingCode accountingCode) {
		this.accountingCode = accountingCode;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxDescription() {
		return taxDescription;
	}

	public void setTaxDescription(String taxDescription) {
		this.taxDescription = taxDescription;
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	public String getSubCatDescription() {
		return subCatDescription;
	}

	public void setSubCatDescription(String subCatDescription) {
		this.subCatDescription = subCatDescription;
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

	public void setId(Long id) {
		this.id = id;
	}

	public Serializable getId() {
		return id;
	}

	public boolean isTransient() {
		return false;
	}

}
