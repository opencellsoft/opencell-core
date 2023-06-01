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
import org.meveo.model.billing.ChartOfAccountTypeEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Tax;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounting_journal_entry")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "accounting_journal_entry_seq")})
@NamedQueries({
        @NamedQuery(name = "JournalEntry.checkExistenceWithAccountingCode",
                query = "SELECT COUNT(je) FROM JournalEntry je WHERE je.accountOperation.id = :ID_AO AND je.accountingCode.id = :ID_ACCOUNTING_CODE"),
        @NamedQuery(name = "JournalEntry.checkAuxiliaryCodeUniqniess",
                query = "SELECT COUNT(je) FROM JournalEntry je WHERE je.auxiliaryAccountCode = :auxiliaryAccountCode AND je.customerAccount <> :customerAccount"),
        @NamedQuery(name = "JournalEntry.getByAccountOperationAndDirection",
                query = "SELECT je FROM JournalEntry je WHERE je.accountOperation.id = :ID_AO AND je.direction = :DIRECTION"),
        @NamedQuery(name = "JournalEntry.findAoWithoutMatchingCode", query = "SELECT je.accountOperation FROM JournalEntry je" +
                " JOIN FETCH je.accountOperation.matchingAmounts ma" +
                " WHERE je.accountOperation.matchingStatus = 'L' AND je.accountOperation.type = 'I' AND je.matchingCode IS NULL" +
                " AND je.accountOperation.status = 'EXPORTED'")
})
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

    /**
     * Operation number
     */
    @Column(name = "operation_number")
    private Long operationNumber; 
    
    /**
     * Seller code
     */
    @Column(name = "seller_code", nullable = false)
    private String sellerCode; 
    
    /**
     * Client unique id
     */
    @Column(name = "client_unique_id")
    private String clientUniqueId; 

    /**
     * Code currency
     */
    @Column(name = "currency", nullable = false)
    private String currency; 
    
    /**
     * Invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supporting_document_ref")
    private Invoice supportingDocumentRef;
    
    /**
     * Invoice type
     */
    @Column(name = "supporting_document_type")
    private String supportingDocumentType;

    /**
     * Code trading currency
     */
    @Column(name = "trading_currency")
    private String tradingCurrency; 
    
	/**
	 * transactional amount
	 */
	@Column(name = "transactional_amount")
	private BigDecimal transactionalAmount;

    /**
     * Auxiliary account code
     */
    @Column(name = "auxiliary_account_code")
	private String auxiliaryAccountCode;

    /**
     * Auxiliary account label
     */
    @Column(name = "auxiliary_account_label")
	private String auxiliaryAccountLabel;

    /**
     * Journal Code
     */
    @Column(name = "journal_code")
    private String journalCode;

    /**
     * Category
     */
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ChartOfAccountTypeEnum category;

    /**
     * Account
     */
    @Column(name = "account")
    private String account;

    /**
     * Label
     */
    @Column(name = "label")
    private String label;

    /**
     * Customer Code
     */
    @Column(name = "customer_code")
    private String customerCode;

    /**
     * Customer Name
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * Seller Name
     */
    @Column(name = "seller_name")
    private String sellerName;

    /**
     * Reference
     */
    @Column(name = "reference")
    private String reference;

    /**
     * Document type
     */
    @Column(name = "document_type")
    private String documentType;

    @Column(name = "matching_code")
    private String matchingCode;
    
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

	public Long getOperationNumber() {
		return operationNumber;
	}

	public void setOperationNumber(Long operationNumber) {
		this.operationNumber = operationNumber;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getClientUniqueId() {
		return clientUniqueId;
	}

	public void setClientUniqueId(String clientUniqueId) {
		this.clientUniqueId = clientUniqueId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Invoice getSupportingDocumentRef() {
		return supportingDocumentRef;
	}

	public void setSupportingDocumentRef(Invoice supportingDocumentRef) {
		this.supportingDocumentRef = supportingDocumentRef;
	}

	public String getSupportingDocumentType() {
		return supportingDocumentType;
	}

	public void setSupportingDocumentType(String supportingDocumentType) {
		this.supportingDocumentType = supportingDocumentType;
	}

	public String getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(String tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public BigDecimal getTransactionalAmount() {
		return transactionalAmount;
	}

	public void setTransactionalAmount(BigDecimal transactionalAmount) {
		this.transactionalAmount = transactionalAmount;
	}

    public String getAuxiliaryAccountCode() {
        return auxiliaryAccountCode;
    }

    public void setAuxiliaryAccountCode(String auxiliaryAccountCode) {
        this.auxiliaryAccountCode = auxiliaryAccountCode;
    }

    public String getAuxiliaryAccountLabel() {
        return auxiliaryAccountLabel;
    }

    public void setAuxiliaryAccountLabel(String auxiliaryAccountLabel) {
        this.auxiliaryAccountLabel = auxiliaryAccountLabel;
    }

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public ChartOfAccountTypeEnum getCategory() {
		return category;
	}

	public void setCategory(ChartOfAccountTypeEnum category) {
		this.category = category;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

    public String getMatchingCode() {
        return matchingCode;
    }

    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }
}