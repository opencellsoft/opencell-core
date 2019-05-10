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
package org.meveo.model.finance;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity to aggregate more than one AO
 * in one accounting writing
 *
 * @author mboukayoua
 */
@Entity
@Table(name="ar_accounting_writing")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ar_accounting_writing_seq"), })
public class AccountingWriting extends AuditableEntity {

    /**
     * Account operations associated with this Acc writing
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ar_accounting_writing_acc_operations", joinColumns = @JoinColumn(name = "accounting_writing_id"), inverseJoinColumns = @JoinColumn(name = "account_operation_id"))
    private List<AccountOperation> accountOperations = new ArrayList<>();

    /**
     * In case this is a contra of another origin accounting writing
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_writing_id")
    private AccountingWriting originWriting;

    /**
     * Associated tax
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    /**
     * Associated invoice category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_category_id")
    private InvoiceCategory invoiceCategory;

    /**
     * Associated accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /**
     * writing total amount
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * event type
     */
    @Column(name = "event_type")
    private String eventType;

    /**
     * writing status
     */
    @Column(name = "export_date")
    @Temporal(TemporalType.DATE)
    private Date exportDate;

    /**
     * Extra param 1
     */
    @Column(name = "extra_param_1")
    private String extraParam1;

    /**
     * Extra param 2
     */
    @Column(name = "extra_param_2")
    private String extraParam2;

    /**
     * Extra param 3
     */
    @Column(name = "extra_param_3")
    private String extraParam3;

	public List<AccountOperation> getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(List<AccountOperation> accountOperations) {
		this.accountOperations = accountOperations;
	}

	public AccountingWriting getOriginWriting() {
		return originWriting;
	}

	public void setOriginWriting(AccountingWriting originWriting) {
		this.originWriting = originWriting;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public InvoiceCategory getInvoiceCategory() {
		return invoiceCategory;
	}

	public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	public AccountingCode getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(AccountingCode accountingCode) {
		this.accountingCode = accountingCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Date getExportDate() {
		return exportDate;
	}

	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}

	public String getExtraParam1() {
		return extraParam1;
	}

	public void setExtraParam1(String extraParam1) {
		this.extraParam1 = extraParam1;
	}

	public String getExtraParam2() {
		return extraParam2;
	}

	public void setExtraParam2(String extraParam2) {
		this.extraParam2 = extraParam2;
	}

	public String getExtraParam3() {
		return extraParam3;
	}

	public void setExtraParam3(String extraParam3) {
		this.extraParam3 = extraParam3;
	}
}
