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
package org.meveo.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.payments.AccountOperation;

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
public class AccountingEntry extends AuditableEntity {

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
    private AccountingEntry originEntry;

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
     * event date
     */
    @Column(name = "event_date")
    @Temporal(TemporalType.DATE)
    private Date eventDate;

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
    
    /**
     * is written or not
     */
    @Transient
    private Boolean isWritten;

	public List<AccountOperation> getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(List<AccountOperation> accountOperations) {
		this.accountOperations = accountOperations;
	}

	public AccountingEntry getOriginEntry() {
		return originEntry;
	}

	public void setOriginEntry(AccountingEntry originEntry) {
		this.originEntry = originEntry;
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

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Boolean getIsWritten() {
        return isWritten;
    }

    public void setIsWritten(Boolean isWritten) {
        this.isWritten = isWritten;
    }
	
}
