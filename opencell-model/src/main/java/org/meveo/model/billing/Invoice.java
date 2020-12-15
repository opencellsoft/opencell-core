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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditTarget;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.order.Order;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.quote.Quote;
import org.meveo.model.shared.DateUtils;

/**
 * Invoice
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Table(name = "billing_invoice", uniqueConstraints = @UniqueConstraint(columnNames = { "invoice_number", "invoice_type_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_invoice_seq"), })
@CustomFieldEntity(cftCodePrefix = "Invoice")
@NamedQueries({ @NamedQuery(name = "Invoice.validatedByBRNoXml", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.billingRun.id=:billingRunId and inv.xmlFilename IS NULL"),
        @NamedQuery(name = "Invoice.draftByBRNoXml", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL and inv.billingRun.id=:billingRunId and inv.xmlFilename IS NULL"),
        @NamedQuery(name = "Invoice.allByBRNoXml", query = "select inv.id from Invoice inv where inv.billingRun.id=:billingRunId and inv.xmlFilename IS NULL"),

        @NamedQuery(name = "Invoice.validatedNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL and inv.invoiceNumber IS NOT NULL"),
        @NamedQuery(name = "Invoice.draftNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL  and inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL"),
        @NamedQuery(name = "Invoice.allNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL"),

        @NamedQuery(name = "Invoice.validatedNoPdf", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL"),
        @NamedQuery(name = "Invoice.draftNoPdf", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL"),
        @NamedQuery(name = "Invoice.allNoPdf", query = "select inv.id from Invoice inv where inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL"),

        @NamedQuery(name = "Invoice.validatedNoPdfByBR", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.draftNoPdfByBR", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.allNoPdfByBR", query = "select inv.id from Invoice inv where inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId"),

        @NamedQuery(name = "Invoice.invoicesToNumberSummary", query = "select inv.invoiceType.id, inv.seller.id, inv.invoiceDate, count(inv) from Invoice inv where inv.billingRun.id=:billingRunId group by inv.invoiceType.id, inv.seller.id, inv.invoiceDate"),
        @NamedQuery(name = "Invoice.byBrItSelDate", query = "select inv.id from Invoice inv where inv.billingRun.id=:billingRunId and inv.invoiceType.id=:invoiceTypeId and inv.seller.id = :sellerId and inv.invoiceDate=:invoiceDate order by inv.id"),
        @NamedQuery(name = "Invoice.billingAccountIdByBrItSelDate", query = "select distinct inv.billingAccount.id from Invoice inv where inv.billingRun.id=:billingRunId and inv.invoiceType.id=:invoiceTypeId and inv.seller.id = :sellerId and inv.invoiceDate=:invoiceDate"),
        @NamedQuery(name = "Invoice.nullifyInvoiceFileNames", query = "update Invoice inv set inv.pdfFilename = null , inv.xmlFilename = null where inv.billingRun = :billingRun"),
        @NamedQuery(name = "Invoice.byBr", query = "select inv from Invoice inv left join fetch inv.billingAccount ba where inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.deleteByBR", query = "delete from Invoice inv where inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.moveToBRByIds", query = "update Invoice inv set inv.billingRun=:billingRun where invoice in (:invoiceIds)"),
        @NamedQuery(name = "Invoice.moveToBR", query = "update Invoice inv set inv.billingRun=:nextBR where inv.billingRun.id=:billingRunId and inv.status in(:statusList)"),
        @NamedQuery(name = "Invoice.deleteByStatusAndBR", query = "delete from Invoice inv where inv.status in(:statusList) and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.findByStatusAndBR", query = "from Invoice inv where inv.status in (:statusList) and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.updateUnpaidInvoicesStatus", query = "UPDATE Invoice inv set inv.status = org.meveo.model.billing.InvoiceStatusEnum.UNPAID"
                + " WHERE inv.dueDate <= NOW() AND inv.status IN (org.meveo.model.billing.InvoiceStatusEnum.CREATED, org.meveo.model.billing.InvoiceStatusEnum.GENERATED, org.meveo.model.billing.InvoiceStatusEnum.SENT)"),
        @NamedQuery(name = "Invoice.sumInvoiceableAmountByBR", query = "select sum(inv.amountWithoutTax), sum(inv.amountWithTax), inv.id, inv.billingAccount.id, inv.billingAccount.customerAccount.id, inv.billingAccount.customerAccount.customer.id "
                + "FROM Invoice inv where inv.billingRun.id=:billingRunId group by inv.id, inv.billingAccount.id, inv.billingAccount.customerAccount.id, inv.billingAccount.customerAccount.customer.id"),
        @NamedQuery(name = "Invoice.deleteByIds", query = "delete from Invoice inv where inv.id IN (:invoicesIds)"),
        @NamedQuery(name = "Invoice.excludePrpaidInvoices", query = "select inv.id from Invoice inv where inv.id IN (:invoicesIds) and inv.prepaid=false"),
        @NamedQuery(name = "Invoice.countRejectedByBillingRun", query = "select count(id) from Invoice where billingRun.id =:billingRunId and status = org.meveo.model.billing.InvoiceStatusEnum.REJECTED") 

})
public class Invoice extends AuditableEntity implements ICustomFieldEntity, ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * Billing account that invoice was issued to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    /**
     * Billing run that produced the invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

    /**
     * Recorded invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_invoice_id")
    private RecordedInvoice recordedInvoice;

    /**
     * Invoice aggregates
     */
    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceAgregate> invoiceAgregates = new LinkedList<>();

    /**
     * Invoice number
     */
    @Column(name = "invoice_number", length = 50)
    @Size(max = 50)
    private String invoiceNumber;

    /**
     * Temporary invoice number
     */
    @Column(name = "temporary_invoice_number", length = 60, unique = true)
    @Size(max = 60)
    private String temporaryInvoiceNumber;

    /**
     * Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "product_date")
    private Date productDate;

    /**
     * Invoice issue date
     */
    @Column(name = "invoice_date")
    private Date invoiceDate;

    /**
     * Invoice status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25)
    @AuditTarget(type = AuditChangeTypeEnum.STATUS, history = true, notif = true)
    private InvoiceStatusEnum status = InvoiceStatusEnum.CREATED;

    /**
     * Payment due date
     */
    @Column(name = "due_date")
    private Date dueDate;

    /**
     * Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;

    /**
     * Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "discount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discount;

    /**
     * Invoiced amount without tax
     */
    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    /**
     * Invoiced tax amount
     */
    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountTax;

    /**
     * Invoiced amount with tax
     */
    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithTax;

    /**
     * Total amount to pay - amountWith/withoutTax + balanceDue
     */
    @Column(name = "net_to_pay", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal netToPay;

    /**
     * Expected payment method
     */
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethodType;

    /**
     * IBAN number. Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "iban", length = 255)
    @Size(max = 255)
    private String iban;

    /**
     * Alias
     */
    @Column(name = "alias", length = 255)
    @Size(max = 255)
    private String alias;

    /**
     * Amount currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    /**
     * Country that invoice was applied to (for tax purpose)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    /**
     * Language invoice is in
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    private TradingLanguage tradingLanguage;

    /**
     * Comment
     */
    @Column(name = "comment", length = 1200)
    @Size(max = 1200)
    private String comment;

    /**
     * Is this a detailed invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "detailed_invoice")
    private boolean isDetailedInvoice = true;

    /**
     * Adjusted invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice adjustedInvoice;

    /**
     * Invoice type
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Linked invoices
     */
    @ManyToMany
    @JoinTable(name = "billing_linked_invoices", joinColumns = { @JoinColumn(name = "id") }, inverseJoinColumns = { @JoinColumn(name = "linked_invoice_id") })
    private Set<Invoice> linkedInvoices = new HashSet<>();

    /**
     * Orders that produced rated transactions that were included in the invoice
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_invoices_orders", joinColumns = @JoinColumn(name = "invoice_id"), inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<Order> orders = new ArrayList<>();

    /**
     * Quote that invoice was produced for
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    /**
     * Subscription that invoice was produced for
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Order that invoice was produced for
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * XML file name. Might contain subdirectories relative to directory where all XML files are located.
     */
    @Column(name = "xml_filename", length = 255)
    @Size(max = 255)
    private String xmlFilename;

    /**
     * PDF file name. Might contain subdirectories relative to directory where all PDF files are located.
     */
    @Column(name = "pdf_filename", length = 255)
    @Size(max = 255)
    private String pdfFilename;

    /**
     * Payment method
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    /**
     * Balance due
     */
    @Column(name = "due_balance", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal dueBalance;

    /**
     * Check if the invoice already sent
     */
    @Type(type = "numeric_boolean")
    @Column(name = "already_sent")
    private boolean alreadySent;

    /**
     * Dont send the invoice if true.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "dont_send")
    private boolean dontSend;

    /**
     * Seller that invoice was issued to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    /**
     * Is this a prepaid invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "prepaid", nullable = false)
    @NotNull
    protected boolean prepaid;

    /**
     * External reference
     */
    @Column(name = "external_ref", length = 255)
    @Size(max = 255)
    private String externalRef;
    
    /**
     * Invoicing error reason
     */
    @Column(name = "reject_reason", columnDefinition = "text")
    @Size(max = 255)
    private String rejectReason;

    /**
     * Invoice payment collection date.
     */
    @Column(name = "initial_collection_date")
    private Date initialCollectionDate;

	@Transient
    private Long invoiceAdjustmentCurrentSellerNb;

    @Transient
    private Long invoiceAdjustmentCurrentProviderNb;

    /**
     * Used to track if "invoiceNumber" field value has changed. Value is populated on postLoad, postPersist and postUpdate JPA events
     */
    @Transient
    private String previousInvoiceNumber;

    /**
     * A flag to indicate if the invoice is a draft.
     */
    @Transient
    private Boolean draft;

    /**
     * Code
     */
    @Transient
    private String code;
    /**
     * Description
     */
    @Transient
    private String description;

    /**
     *
     */
    @Transient
    private List<RatedTransaction> draftRatedTransactions = new ArrayList<>();

    /**
     * 3583 : dueDate and invoiceDate should be truncated before persist or update.
     */
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        this.dueDate = DateUtils.truncateTime(this.dueDate);
        this.invoiceDate = DateUtils.truncateTime(this.invoiceDate);
        setUUIDIfNull();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        this.status=InvoiceStatusEnum.CREATED;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(PaymentMethodEnum paymentMethod) {
        this.paymentMethodType = paymentMethod;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public List<InvoiceAgregate> getInvoiceAgregates() {
        return invoiceAgregates;
    }

    public void setInvoiceAgregates(List<InvoiceAgregate> invoiceAgregates) {
        this.invoiceAgregates = invoiceAgregates;
    }

    public void addAmountWithTax(BigDecimal amountToAdd) {
        if (amountWithTax == null) {
            amountWithTax = BigDecimal.ZERO;
        }
        if (amountToAdd != null) {
            amountWithTax = amountWithTax.add(amountToAdd);
        }
    }

    public void addAmountWithoutTax(BigDecimal amountToAdd) {
        if (amountWithoutTax == null) {
            amountWithoutTax = BigDecimal.ZERO;
        }
        if (amountToAdd != null) {
            amountWithoutTax = amountWithoutTax.add(amountToAdd);
        }
    }

    public void addAmountTax(BigDecimal amountToAdd) {
        if (amountTax == null) {
            amountTax = BigDecimal.ZERO;
        }
        if (amountToAdd != null) {
            amountTax = amountTax.add(amountToAdd);
        }
    }

    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

    public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

    public String getInvoiceNumberOrTemporaryNumber() {
        if (invoiceNumber != null) {
            return invoiceNumber;
        } else {
            return "[" + temporaryInvoiceNumber + "]";
        }
    }

    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public TradingCountry getTradingCountry() {
        return tradingCountry;
    }

    public void setTradingCountry(TradingCountry tradingCountry) {
        this.tradingCountry = tradingCountry;
    }

    public TradingLanguage getTradingLanguage() {
        return tradingLanguage;
    }

    public void setTradingLanguage(TradingLanguage tradingLanguage) {
        this.tradingLanguage = tradingLanguage;
    }

    public BigDecimal getNetToPay() {
        return netToPay;
    }

    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

    public RecordedInvoice getRecordedInvoice() {
        return recordedInvoice;
    }

    public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
        this.recordedInvoice = recordedInvoice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDetailedInvoice() {
        return isDetailedInvoice;
    }

    public void setDetailedInvoice(boolean isDetailedInvoice) {
        this.isDetailedInvoice = isDetailedInvoice;
    }

    public Invoice getAdjustedInvoice() {
        return adjustedInvoice;
    }

    public void setAdjustedInvoice(Invoice adjustedInvoice) {
        this.adjustedInvoice = adjustedInvoice;
    }

    public Long getInvoiceAdjustmentCurrentSellerNb() {
        return invoiceAdjustmentCurrentSellerNb;
    }

    public void setInvoiceAdjustmentCurrentSellerNb(Long invoiceAdjustmentCurrentSellerNb) {
        this.invoiceAdjustmentCurrentSellerNb = invoiceAdjustmentCurrentSellerNb;
    }

    public Long getInvoiceAdjustmentCurrentProviderNb() {
        return invoiceAdjustmentCurrentProviderNb;
    }

    public void setInvoiceAdjustmentCurrentProviderNb(Long invoiceAdjustmentCurrentProviderNb) {
        this.invoiceAdjustmentCurrentProviderNb = invoiceAdjustmentCurrentProviderNb;
    }

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public InvoiceStatusEnum getRealTimeStatus() {
        if (dueDate != null && dueDate.before(new Date()) && (status == InvoiceStatusEnum.CREATED || status == InvoiceStatusEnum.GENERATED || status == InvoiceStatusEnum.SENT)) {
            return InvoiceStatusEnum.UNPAID;
        }
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Invoice)) {
            return false;
        }

        Invoice other = (Invoice) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 961 + ("Invoice" + id).hashCode();
    }

    /**
     * @return the invoiceType
     */
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    /**
     * @param invoiceType the invoiceType to set
     */
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * @return the orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * @param orders the orders to set
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * setting uuid if null
     */
    public void setUUIDIfNull() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @Override
    public String getUuid() {
        setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    /**
     * @param uuid Unique identifier
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (billingRun != null) {
            return new ICustomFieldEntity[] { billingRun };
        }
        return null;
    }

    public Set<Invoice> getLinkedInvoices() {
        return linkedInvoices;
    }

    public void setLinkedInvoices(Set<Invoice> linkedInvoices) {
        this.linkedInvoices = linkedInvoices;
    }

    public void addInvoiceAggregate(InvoiceAgregate obj) {
        invoiceAgregates.add(obj);
    }

    public List<SubCategoryInvoiceAgregate> getDiscountAgregates() {
        List<SubCategoryInvoiceAgregate> aggregates = new ArrayList<>();

        for (InvoiceAgregate invoiceAggregate : invoiceAgregates) {
            if (invoiceAggregate instanceof SubCategoryInvoiceAgregate && ((SubCategoryInvoiceAgregate) invoiceAggregate).isDiscountAggregate()) {
                aggregates.add((SubCategoryInvoiceAgregate) invoiceAggregate);
            }
        }

        return aggregates;
    }
//
//    public List<RatedTransaction> getRatedTransactionsForCategory(WalletInstance wallet, InvoiceSubCategory invoiceSubCategory) {
//
//        List<RatedTransaction> ratedTransactionsMatched = new ArrayList<>();
//
//        for (RatedTransaction ratedTransaction : ratedTransactions) {
//            if (ratedTransaction.getWallet().equals(wallet) && ratedTransaction.getInvoiceSubCategory().equals(invoiceSubCategory)) {
//                ratedTransactionsMatched.add(ratedTransaction);
//            }
//
//        }
//        return ratedTransactionsMatched;
//    }

    /**
     * @return Quote that invoice was produced for
     */
    public Quote getQuote() {
        return quote;
    }

    /**
     * @param quote Quote that invoice was produced for
     */
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    /**
     * @return PDF filename. Including any subdirectories it might contain. E.g. for "a/b/c.pdf", this method will return "a/b/c.pdf"
     */
    public String getPdfFilename() {
        return pdfFilename;
    }

    /**
     * @param pdfFilename PDF filename. Including any subdirectories it might contain. E.g. for "a/b/c.pdf", this method will return "a/b/c.pdf"
     */
    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }

    /**
     * Return a PDF filename without any subdirectories it might contain. E.g. for "a/b/c.pdf", this method will return "c.pdf"
     * 
     * @return PDF file name without any subdirectories it might contain.
     */
    public String getPdfFilenameOnly() {
        if (pdfFilename != null) {
            int pos = Integer.max(pdfFilename.lastIndexOf("/"), pdfFilename.lastIndexOf("\\"));
            if (pos > -1) {
                return pdfFilename.substring(pos + 1);
            }
        }
        return pdfFilename;
    }

    /**
     * @return XML filename. Including any subdirectories it might contain. E.g. for "a/b/c.xml", this method will return "a/b/c.xml"
     */
    public String getXmlFilename() {
        return xmlFilename;
    }

    /**
     * @param xmlFilename XML filename. Including any subdirectories it might contain. E.g. for "a/b/c.xml", this method will return "a/b/c.xml"
     */
    public void setXmlFilename(String xmlFilename) {
        this.xmlFilename = xmlFilename;
    }

    /**
     * Return a XML filename without any subdirectories it might contain. E.g. for "a/b/c.xml", this method will return "c.xml"
     * 
     * @return XML file name without any subdirectories it might contain.
     */
    public String getXmlFilenameOnly() {
        if (xmlFilename != null) {
            int pos = Integer.max(xmlFilename.lastIndexOf("/"), xmlFilename.lastIndexOf("\\"));
            if (pos > -1) {
                return xmlFilename.substring(pos + 1);
            }
        }
        return xmlFilename;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void assignTemporaryInvoiceNumber() {

        StringBuffer num1 = new StringBuffer("000000000");
        num1.append(id + "");
        String invoiceNumber = num1.substring(num1.length() - 9);
        int key = 0;

        for (int i = 0; i < invoiceNumber.length(); i++) {
            key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
        }

        setTemporaryInvoiceNumber(invoiceNumber + "-" + key % 10);
    }

    public BigDecimal getDueBalance() {
        return dueBalance;
    }

    public void setDueBalance(BigDecimal dueBalance) {
        this.dueBalance = dueBalance;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * @return Subscription that invoice was produced for
     */
    public Subscription getSubscription() {
        return subscription;
    }

    /**
     * @param subscription Subscription that invoice was produced for
     */
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * 
     * @return Order that invoice was produced for
     */
    public Order getOrder() {
        return order;
    }

    /**
     * @param order Order that invoice was produced for
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * @return The previous invoice number
     */
    public String getPreviousInvoiceNumber() {
        return previousInvoiceNumber;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    /**
     * Tracks what was the previous invoice number
     */
    private void trackPreviousValues() {
        previousInvoiceNumber = invoiceNumber;
    }

    /**
     * @return true if the invoice is draft, false else.
     */
    public Boolean isDraft() {
        if (draft == null) {
            return false;
        }
        return draft;
    }

    /**
     * Set the draft flag
     *
     * @param draft the draft flag
     */
    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public boolean isAlreadySent() {
        return alreadySent;
    }

    public void setAlreadySent(boolean alreadySent) {
        this.alreadySent = alreadySent;
    }

    public boolean isDontSend() {
        return dontSend;
    }

    public void setDontSend(boolean dontSend) {
        this.dontSend = dontSend;
    }

    @Override
    public String toString() {
        return String.format("%s[%s, invoiceNumber=%s, invoiceType=%s]", this.getClass().getSimpleName(), super.toString(), invoiceNumber, invoiceType);
    }

    @Override
    public String getCode() {
        return getInvoiceNumber();
    }

    @Override
    public void setCode(String code) {

    }

    @Override
    public String getDescription() {
        return getAlias();
    }

    @Override
    public void setDescription(String description) {

    }

    /**
     * @return Is this a prepaid invoice report
     */
    public boolean isPrepaid() {
        return prepaid;
    }

    /**
     * @param prepaid Is this a prepaid invoice report
     */
    public void setPrepaid(boolean prepaid) {
        this.prepaid = prepaid;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public void setDraftRatedTransactions(List<RatedTransaction> draftRatedTransactions) {
        this.draftRatedTransactions = draftRatedTransactions;
    }

    public List<RatedTransaction> getDraftRatedTransactions() {
        return draftRatedTransactions;
    }

    /**
     * Gets the invoice payment collection date
     *
     * @return Invoice payment collection date
     */
    public Date getInitialCollectionDate() {
        return initialCollectionDate;
    }

    /**
     * Sets Invoice payment collection date.
     *
     * @param initialCollectionDate
     */
    public void setInitialCollectionDate(Date initialCollectionDate) {
        this.initialCollectionDate = initialCollectionDate;
    }

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
    
    
}