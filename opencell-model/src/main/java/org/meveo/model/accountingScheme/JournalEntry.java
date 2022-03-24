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
package org.meveo.model.accountingScheme;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Tax;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounting_journal_entry")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "accounting_journal_entry_seq")})
public class JournalEntry extends AuditableEntity {

    /**
     * Account operation for which the accounting entry is created
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_operation_id", nullable = false)
    private AccountOperation accountOperation;

    /**
     * Accounting code for the entry
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id", nullable = false)
    private AccountingCode accountingCode;

    /**
     * Seller for reference invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    /**
     * Customer account for account operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

	/**
	 * Based on account operation type settings
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "direction", nullable = false)
	private JournalEntryDirectionEnum direction;

	/**
	 * Account operation’s amount
	 */
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

    /**
     * Based on account operation type settings
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    /**
     * For analytic code 1
     */
    @Column(name = "analytic_code_1")
    private String analyticCode1;

    /**
     * For analytic code 2
     */
    @Column(name = "analytic_code_2")
    private String analyticCode2;

    /**
     * For analytic code 3
     */
    @Column(name = "analytic_code_3")
    private String analyticCode3;

    public AccountOperation getAccountOperation() {
        return accountOperation;
    }

    public void setAccountOperation(AccountOperation accountOperation) {
        this.accountOperation = accountOperation;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public JournalEntryDirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(JournalEntryDirectionEnum direction) {
        this.direction = direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public String getAnalyticCode1() {
        return analyticCode1;
    }

    public void setAnalyticCode1(String analyticCode1) {
        this.analyticCode1 = analyticCode1;
    }

    public String getAnalyticCode2() {
        return analyticCode2;
    }

    public void setAnalyticCode2(String analyticCode2) {
        this.analyticCode2 = analyticCode2;
    }

    public String getAnalyticCode3() {
        return analyticCode3;
    }

    public void setAnalyticCode3(String analyticCode3) {
        this.analyticCode3 = analyticCode3;
    }
}
