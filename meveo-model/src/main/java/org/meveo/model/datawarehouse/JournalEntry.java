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
package org.meveo.model.datawarehouse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.meveo.model.IEntity;
import org.meveo.model.IVersionedEntity;

@Entity
@Table(name = "DWH_JOURNAL_ENTRIES", uniqueConstraints = @UniqueConstraint(columnNames = {
		"ORIGIN_ID", "INVOICE_NUMBER", "ACCOUNTING_CODE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_JOURNAL_ENTRIES_SEQ")
public class JournalEntry implements IEntity, IVersionedEntity {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@Column(name = "PROVIDER_CODE", length = 20)
	private String providerCode;

	@Column(name = "TYPE")
	@Enumerated(EnumType.STRING)
	private JournalEntryTypeEnum type;

	@Column(name = "ORIGIN_ID")
	private Long originId;

	@Column(name = "INVOICE_NUMBER", length = 20)
	private String invoiceNumber;

	@Column(name = "ACCOUNTING_CODE", length = 255)
	private String accountingCode;

	@Column(name = "INVOICE_DATE")
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;

	@Column(name = "CUSTOMER_ACCOUNT_CODE", length = 20)
	private String customerAccountCode;

	@Column(name = "TAX_CODE", length = 10)
	private String taxCode;

	@Column(name = "TAX_DESCRIPTION", length = 20)
	private String taxDescription;

	@Column(name = "TAX_PERCENT")
	private BigDecimal taxPercent;

	@Column(name = "SUB_CAT_DESC", length = 50)
	private String subCatDescription;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
	private BigDecimal amountWithoutTax;

	@Column(name = "AMOUNT_TAX", precision = 23, scale = 12)
	private BigDecimal amountTax;

	@Column(name = "AMOUNT_WITH_TAX", precision = 23, scale = 12)
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

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
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

	public String getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(String accountingCode) {
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
