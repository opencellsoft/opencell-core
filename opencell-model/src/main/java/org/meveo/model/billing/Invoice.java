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

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.ValidationException;
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
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.plan.PaymentPlan;
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

        @NamedQuery(name = "Invoice.noXmlWithStatus", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL and inv.status in(:statusList) and (inv.billingRun IS NULL OR inv.billingRun.disabled = false)"),
        @NamedQuery(name = "Invoice.noXmlWithStatusAndBR", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL and inv.billingRun.id=:billingRunId and inv.billingRun.disabled = false and inv.status in(:statusList)"),
        @NamedQuery(name = "Invoice.validatedNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL and inv.invoiceNumber IS NOT NULL"),
        @NamedQuery(name = "Invoice.draftNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL  and inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL"),
        @NamedQuery(name = "Invoice.allNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL"),

        @NamedQuery(name = "Invoice.validatedNoPdf", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and (inv.billingRun IS NULL OR inv.billingRun.disabled = false)"),
        @NamedQuery(name = "Invoice.draftNoPdf", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and (inv.billingRun IS NULL OR inv.billingRun.disabled = false)"),
        @NamedQuery(name = "Invoice.allNoPdf", query = "select inv.id from Invoice inv where inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and (inv.billingRun IS NULL OR inv.billingRun.disabled = false)"),

        @NamedQuery(name = "Invoice.validatedNoPdfByBR", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId and inv.billingRun.disabled = false"),
        @NamedQuery(name = "Invoice.draftNoPdfByBR", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NULL and inv.temporaryInvoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId and inv.billingRun.disabled = false"),
        @NamedQuery(name = "Invoice.allNoPdfByBR", query = "select inv.id from Invoice inv where inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId and inv.billingRun.disabled = false"),

        @NamedQuery(name = "Invoice.invoicesToNumberSummary", query = "select inv.invoiceType.id, inv.seller.id, inv.invoiceDate, count(inv) from Invoice inv where inv.billingRun.id=:billingRunId AND inv.status in ('DRAFT', 'SUSPECT', 'REJECTED') group by inv.invoiceType.id, inv.seller.id, inv.invoiceDate"),
        @NamedQuery(name = "Invoice.byBrItSelDate", query = "select inv.id from Invoice inv where inv.billingRun.id=:billingRunId and inv.invoiceType.id=:invoiceTypeId and inv.seller.id = :sellerId and inv.invoiceDate=:invoiceDate AND inv.status in ('DRAFT', 'SUSPECT', 'REJECTED') order by inv.id"),
        @NamedQuery(name = "Invoice.billingAccountIdByBrItSelDate", query = "select distinct inv.billingAccount.id from Invoice inv where inv.billingRun.id=:billingRunId and inv.invoiceType.id=:invoiceTypeId and inv.seller.id = :sellerId and inv.invoiceDate=:invoiceDate"),
        @NamedQuery(name = "Invoice.nullifyInvoiceFileNames", query = "update Invoice inv set inv.pdfFilename = null , inv.xmlFilename = null where inv.billingRun = :billingRun"),
        @NamedQuery(name = "Invoice.nullifyInvoiceXMLFileNames", query = "update Invoice inv set inv.xmlFilename = null where inv.billingRun = :billingRun"),
        @NamedQuery(name = "Invoice.nullifyInvoicePDFFileNames", query = "update Invoice inv set inv.pdfFilename = null where inv.billingRun = :billingRun"),
        @NamedQuery(name = "Invoice.portInvoiceReport", query = "select inv.amountWithTax, inv.amountWithoutTax, inv.amountTax, inv.paymentMethodType, pm.yearExpiration, pm.monthExpiration, ba.electronicBilling from Invoice inv inner join inv.billingAccount ba left join inv.paymentMethod pm where inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.deleteByBR", query = "delete from Invoice inv where inv.billingRun.id=:billingRunId AND inv.status <> 'VALIDATED'"),
        @NamedQuery(name = "Invoice.moveToBRByIds", query = "update Invoice inv set inv.billingRun=:billingRun, inv.status=org.meveo.model.billing.InvoiceStatusEnum.DRAFT where inv.id in (:invoiceIds)"),
        @NamedQuery(name = "Invoice.moveToBR", query = "update Invoice inv set inv.billingRun=:nextBR where inv.billingRun.id=:billingRunId and inv.status in(:statusList)"),
        @NamedQuery(name = "Invoice.deleteByStatusAndBR", query = "delete from Invoice inv where inv.status in(:statusList) and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.findByStatusAndBR", query = "from Invoice inv where inv.status in (:statusList) and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.listUnpaidInvoicesIds", query = "SELECT inv.id FROM Invoice inv "
                                + " WHERE inv.dueDate <= NOW() AND inv.status = org.meveo.model.billing.InvoiceStatusEnum.VALIDATED "
                                + " AND inv.paymentStatus not in (org.meveo.model.billing.InvoicePaymentStatusEnum.PAID,"
                                + " org.meveo.model.billing.InvoicePaymentStatusEnum.PPAID, org.meveo.model.billing.InvoicePaymentStatusEnum.DISPUTED)"),
        @NamedQuery(name = "Invoice.detachAOFromInvoice", query = "UPDATE Invoice set recordedInvoice = null where recordedInvoice = :ri"),
        @NamedQuery(name = "Invoice.sumInvoiceableAmountByBR", query =
        "select sum(inv.amountWithoutTax), sum(inv.amountWithTax), inv.subscription.id, inv.commercialOrder.id , inv.id, inv.billingAccount.id, inv.billingAccount.customerAccount.id, inv.billingAccount.customerAccount.customer.id "
                + "FROM Invoice inv where inv.billingRun.id=:billingRunId group by inv.subscription.id, inv.commercialOrder.id , inv.id, inv.billingAccount.id, inv.billingAccount.customerAccount.id, inv.billingAccount.customerAccount.customer.id"),

        @NamedQuery(name = "Invoice.sumAmountsByBR", query = "select sum(inv.amountTax),sum(inv.amountWithoutTax), sum(inv.amountWithTax) FROM Invoice inv where inv.billingRun.id=:billingRunId and inv.status <> 'CANCELED'"),
        @NamedQuery(name = "Invoice.billingAccountsByBr", query = "select distinct inv.billingAccount from Invoice inv where inv.billingRun.id=:billingRunId and inv.status <> 'CANCELED'"),
		@NamedQuery(name = "Invoice.cancelInvoiceById", query = "update Invoice inv set inv.status='CANCELED', inv.rejectedByRule = null, inv.rejectReason = null, inv.auditable.updated=:now WHERE inv.id=:invoiceId AND inv.status <> 'VALIDATED'"),

        @NamedQuery(name = "Invoice.deleteByIds", query = "delete from Invoice inv where inv.id IN (:invoicesIds)"),
        @NamedQuery(name = "Invoice.excludePrpaidInvoices", query = "select inv.id from Invoice inv where inv.id IN (:invoicesIds) and inv.prepaid=false"),
        @NamedQuery(name = "Invoice.countRejectedByBillingRun", query = "select count(id) from Invoice where billingRun.id =:billingRunId and status = org.meveo.model.billing.InvoiceStatusEnum.REJECTED"),
        @NamedQuery(name = "Invoice.countSuspectByBillingRun", query = "select count(id) from Invoice where billingRun.id =:billingRunId and status = org.meveo.model.billing.InvoiceStatusEnum.SUSPECT"),
        @NamedQuery(name = "Invoice.getInvoiceTypeANDRecordedInvoiceID", query = "select inv.invoiceType.code, inv.recordedInvoice.id from Invoice inv where inv.id =:id"),
        @NamedQuery(name = "Invoice.initAmounts", query = "UPDATE Invoice inv set inv.amount = 0, inv.amountTax = 0, inv.amountWithTax = 0, inv.amountWithoutTax = 0, inv.netToPay = 0, inv.discountAmount = 0, inv.amountWithoutTaxBeforeDiscount = 0, inv.transactionalAmountTax =0, inv.transactionalAmountWithTax = 0, inv.transactionalAmountWithoutTax = 0, inv.transactionalNetToPay = 0, inv.transactionalAmountWithoutTaxBeforeDiscount = 0, inv.transactionalDiscountAmount = 0, inv.transactionalRawAmount = 0 where inv.id = :invoiceId"),
        @NamedQuery(name = "Invoice.loadByBillingRunNotValidatedInvoices", query = "SELECT inv.id FROM Invoice inv WHERE inv.billingRun.id = :billingRunId AND inv.status <> org.meveo.model.billing.InvoiceStatusEnum.VALIDATED "),
        @NamedQuery(name = "Invoice.loadByBillingRun", query = "SELECT inv.id FROM Invoice inv WHERE inv.billingRun.id = :billingRunId"),
        @NamedQuery(name = "Invoice.findLinkedInvoicesByIdAndType", query = "SELECT inv.invoiceNumber, inv.invoiceDate, linkedinv.amount FROM Invoice inv inner join LinkedInvoice linkedinv on inv.id = linkedinv.linkedInvoiceValue.id where linkedinv.id.id = :invoiceId and inv.invoiceType.code =: invoiceTypeCode"),
        @NamedQuery(name = "Invoice.findInvoiceEligibleAdv", query = "select bi, adv from Invoice bi inner join  BillingAccount bba on bi.billingAccount.id = bba.id inner join Invoice adv on adv.billingAccount.id = bba.id inner join InvoiceType  it on it.id = adv.invoiceType.id inner join Subscription sub on sub.id = bi.subscription.id"
                + " where bi.billingRun.id = :billingRunId and it.code = 'ADV' and adv.status = 'VALIDATED' and adv.invoiceBalance > 0 and bi.status in ('DRAFT', 'REJECTED', 'SUSPECT') and adv.tradingCurrency.id = bi.tradingCurrency.id  order by bi.id, adv.auditable.created, adv.invoiceBalance"),
        @NamedQuery(name = "Invoice.findValidatedInvoiceAdvOrder", query = "select inv from Invoice inv  where  (inv.commercialOrder is null or inv.commercialOrder=:commercialOrder) and  (inv.subscription is null or inv.subscription.id =:subscriptionId) and inv.status='VALIDATED' and inv.invoiceType.code = 'ADV' and inv.invoiceBalance > 0 and inv.billingAccount.id =:billingAccountId and inv.tradingCurrency.id =:tradingCurrencyId order by inv.commercialOrder, inv.subscription, inv.auditable.created ASC"),
        @NamedQuery(name = "Invoice.findValidatedInvoiceAdvWithoutOrder", query = "select inv from Invoice inv  where  inv.commercialOrder is null  and inv.status='VALIDATED' and inv.invoiceType.code = 'ADV' and inv.invoiceBalance > 0 and inv.billingAccount.id =:billingAccountId"),
        @NamedQuery(name = "Invoice.findValidatedInvoiceAdvWithOrder", query = "select inv from Invoice inv  where  inv.commercialOrder=:commercialOrder and inv.status='VALIDATED' and inv.invoiceType.code = 'ADV' and inv.invoiceBalance > 0 and inv.billingAccount.id =:billingAccountId"),
        @NamedQuery(name = "Invoice.findWithFuntionalCurrencyDifferentFromOne", query = "SELECT i FROM Invoice i JOIN Provider p ON p.currency.id = i.tradingCurrency.currency.id WHERE i.lastAppliedRate <> :EXPECTED_RATE"),
        @NamedQuery(name = "Invoice.countByValidationRule", query = "SELECT count(id) FROM Invoice WHERE rejectedByRule.id = :ruleId"),
		@NamedQuery(name = "Invoice.xmlWithStatusForUBL", query = "select inv.id from Invoice inv where inv.status in(:statusList) and inv.ublReference = false"),
        @NamedQuery(name = "Invoice.SUM_VALIDATED_LINKED_INVOICES", query = "SELECT SUM(i.amountWithTax) FROM Invoice i" +
                " WHERE i.id in (SELECT li.linkedInvoiceValue.id FROM LinkedInvoice li WHERE li.id.id = :SRC_INVOICE_ID) AND i.status = 'VALIDATED'")
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "Invoice.rollbackAdvance", query = "update billing_invoice set invoice_balance = invoice_balance + li.amount from (select bli.linked_invoice_id, bli.amount from billing_linked_invoices bli join billing_invoice i on i.id = bli.id where i.billing_run_id = :billingRunId and bli.type = 'ADVANCEMENT_PAYMENT') li where li.linked_invoice_id = id")
})
public class Invoice extends AuditableEntity implements ICustomFieldEntity, ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * Billing account that invoice was issued to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id", nullable = false)
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
    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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
    private InvoiceStatusEnum status = InvoiceStatusEnum.NEW;

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
    @Column(name = "iban")
    @Size(max = 255)
    private String iban;

    /**
     * Alias
     */
    @Column(name = "alias")
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
    @Column(name = "comment_text", length = 1200)
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
    @Column(name = "cf_values", columnDefinition = "jsonb")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Transient
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Linked invoices
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<LinkedInvoice> linkedInvoices = new HashSet<>();

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
    @Column(name = "xml_filename")
    @Size(max = 255)
    private String xmlFilename;

    /**
     * PDF file name. Might contain subdirectories relative to directory where all PDF files are located.
     */
    @Column(name = "pdf_filename")
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
    @Column(name = "external_ref")
    @Size(max = 255)
    private String externalRef;
    
    /**
     * Invoicing error reason
     */
    @Column(name = "reject_reason")
    @Size(max = 255)
    private String rejectReason;

    /**
     * Invoice payment collection date.
     */
    @Column(name = "initial_collection_date")
    private Date initialCollectionDate;

    /**
     * Invoice status change date
     */
    @Column(name = "status_date")
    private Date statusDate;

    /**
     * Date when the XML has been produced on a validated invoice.
     */
    @Column(name = "xml_date")
    @AuditTarget(type = AuditChangeTypeEnum.OTHER, history = true, notif = true)
    private Date xmlDate;

    /**
     * Date when the PDf has been produced on a validated invoice.
     */
    @Column(name = "pdf_date")
    @AuditTarget(type = AuditChangeTypeEnum.OTHER, history = true, notif = true)
    private Date pdfDate;

    /**
     * Date when the invoice has been sent for a validated invoice
     */
    @Column(name = "email_sent_date")
    @AuditTarget(type = AuditChangeTypeEnum.OTHER, history = true, notif = true)
    private Date emailSentDate;

    /**
     *
     */
	@Column(name = "payment_status")
	@Enumerated(EnumType.STRING)
    @AuditTarget(type = AuditChangeTypeEnum.OTHER, history = true, notif = true)
	private InvoicePaymentStatusEnum paymentStatus = InvoicePaymentStatusEnum.NONE;

    /**
     * Payment status change date
     */
    @Column(name = "payment_status_date")
    private Date paymentStatusDate;

    /**
     * Beginning of the billed period (based on billing cycle period whenever possible or min(invoiceLine.valueDate))
     */
    @Column(name = "start_date")
    private Date startDate;


    /**
     * End of the billed period (based on billing cycle period whenever possible or applied lastTransactionDate or max(invoiceLine.valueDate))
     */
    @Column(name = "end_date")
    private Date endDate;


    /**
     * Total raw amount from invoice lines.
     *      -Does not include discount.
     *      -With or without tax depending on provider setting (isEnterprise).
     */
    @Column(name = "raw_amount", nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rawAmount= ZERO;

    /**
     * Discount rate to apply (in %).
     * Initialize with discount rate from linked invoice discount plan.
     */
    @Column(name = "discount_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountRate;

	/**
     * Total discount amount with or without tax depending on provider settings.
	 * Can be inconsistent with discountRate.
	 * discountAmount has precedence over discountRate
     */
    @Column(name = "discount_amount", nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountAmount= ZERO;

    /**
     * Indicates if the invoicing minimum has already been applied
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_already_applied_minimum")
    private boolean isAlreadyAppliedMinimum;
    
    /**
     * Cpq quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpq_quote_id")
    private CpqQuote cpqQuote;

    /**
     * Indicates if the invoice discounts have already been applied
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_already_added_discount")
    private boolean isAlreadyAddedDiscount;

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
     * Is invoice generated using new invoice process
     */
    @Type(type = "numeric_boolean")
    @Column(name = "new_invoicing_process")
    private boolean newInvoicingProcess = false;

    @Type(type = "numeric_boolean")
    @Column(name = "has_taxes")
    private boolean hasTaxes;

    @Type(type = "numeric_boolean")
    @Column(name = "has_discounts")
    private boolean hasDiscounts;

    @Type(type = "numeric_boolean")
    @Column(name = "has_minimum")
    private boolean hasMinimum;

    /**
	 * discountPlan attached to the invoice
	 */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;

    /**
	 * invoiceLines attached to the invoice
	 */
    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<InvoiceLine>  invoiceLines;


    /**
     * Commercial order attached to the invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
  	@JoinColumn(name = "commercial_order_id", referencedColumnName = "id")
  	private CommercialOrder commercialOrder;

    @Transient
    private List<InvoiceLine> draftInvoiceLines = new ArrayList<>();

    /**
     * Amount without tax before discount
     */
    @Column(name = "amount_without_tax_before_discount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTaxBeforeDiscount;

    @Type(type = "numeric_boolean")
    @Column(name = "is_reminder_level_triggered")
    private boolean isReminderLevelTriggered;

    /**
     * The collection plan related invoice
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_dunning_collection_plan_id")
    private DunningCollectionPlan relatedDunningCollectionPlan;

    @Type(type = "numeric_boolean")
    @Column(name = "dunning_collection_plan_triggered")
    private boolean dunningCollectionPlanTriggered;
    @Transient
  	private Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates;
    
    /**
     * The exchange rate that converted amounts of the invoice.
     */
    @Column(name = "last_applied_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal lastAppliedRate;
    
    /**
     * The date of exchange rate applied to amounts of the invoice.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_applied_rate_date")
    private Date lastAppliedRateDate = new Date();
    @Transient
  	private Date nextInvoiceDate;

    /**
     * Transactional amount without tax
     */
    @Column(name = "transactional_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalAmountWithoutTax;

    /**
     * Transactional amount with tax
     */
    @Column(name = "transactional_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalAmountWithTax;

    /**
     * Transactional amount tax
     */
    @Column(name = "transactional_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalAmountTax;

    /**
     * Transactional total amount to pay
     */
    @Column(name = "transactional_net_to_pay", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalNetToPay;

    /**
     * Transactional raw amount
     */
    @Column(name = "transactional_raw_amount", nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalRawAmount= ZERO;

    /**
     * Transactional discount amount
     */
    @Column(name = "transactional_discount_amount", nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalDiscountAmount = ZERO;

    /**
     * Transactional amount without tax before discount
     */
    @Column(name = "transactional_amount_without_tax_before_discount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalAmountWithoutTaxBeforeDiscount;

    /**
     * Payment plan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_plan_id")
    private PaymentPlan paymentPlan;

    /**
     * Open Order Number
     */
    @Column(name = "open_order_number")
    @Size(max = 255)
	private String openOrderNumber;

    @Column(name = "invoice_balance", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal invoiceBalance;
    
    /**
     * Indicates if the current rate has already been applied.
     */
    @Column(name = "use_current_rate")
    @Type(type = "numeric_boolean")
    private boolean useCurrentRate = false;
    
    /**
     * Rejected rule validation invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_validation_rule_id")
    private InvoiceValidationRule rejectedByRule;

    @Column(name = "transactional_invoice_balance", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalInvoiceBalance;

    @Column(name = "use_specific_price_conversion")
    @Type(type = "numeric_boolean")
    private boolean useSpecificPriceConversion = false;
    
    @Column(name = "conversion_from_billing_currency")
    @Type(type = "numeric_boolean")
    private boolean conversionFromBillingCurrency = false;
	
	@Column(name = "auto_matching")
	@Type(type = "numeric_boolean")
	private boolean autoMatching;
	
    @Column(name = "external_purchase_order_number")
    @Size(max = 100)
    private String externalPurchaseOrderNumber;
	
	@Column(name = "ubl_reference")
	@Type(type = "numeric_boolean")
	private boolean ublReference;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pdp_status_id")
	private PDPStatusEntity pdpStatus;
    
    public Invoice() {
	}

    public Invoice(Invoice copy) {
		this.billingAccount = copy.billingAccount;
		this.paymentMethodType = copy.paymentMethodType;
		this.dueDate = copy.dueDate;
		this.invoiceDate = copy.invoiceDate;
		this.discountPlan = copy.discountPlan;
		this.amountWithoutTax = copy.amountWithoutTax;
		this.amountTax = copy.amountTax;
		this.amountWithTax = copy.amountWithTax;
		this.netToPay = copy.netToPay;
		this.tradingCurrency = copy.tradingCurrency;
		this.tradingLanguage = copy.tradingLanguage;
		this.tradingCountry = copy.tradingCountry;
		this.iban = copy.iban;
		this.isDetailedInvoice = copy.isDetailedInvoice;
		this.discountRate = copy.discountRate;
		this.discountAmount = copy.discountAmount;
		this.externalRef = copy.externalRef;
		setUUIDIfNull();
		this.cfValues = copy.cfValues;
		this.cfAccumulatedValues = copy.cfAccumulatedValues;
		this.seller = copy.seller;
		this.invoiceType = copy.invoiceType;

		this.alias = null;
		this.quote = null;
		this.commercialOrder = null;
		this.status = InvoiceStatusEnum.NEW;
		this.paymentStatus = InvoicePaymentStatusEnum.NONE;
		this.alreadySent = false;
		this.invoiceNumber = null;
		this.temporaryInvoiceNumber = null;
		this.comment = null;
		this.linkedInvoices = new HashSet<>();
		this.orders = new ArrayList<>();
		this.xmlFilename = null;
		this.pdfFilename = null;
		this.rejectReason = null;

		this.xmlDate = null;
		this.pdfDate = null;
		this.emailSentDate = null;
		this.paymentStatusDate = null;
		this.invoiceLines = new ArrayList<>();
		this.invoiceAgregates = new ArrayList<>();
		this.isReminderLevelTriggered = copy.isReminderLevelTriggered;
		this.relatedDunningCollectionPlan = copy.relatedDunningCollectionPlan;
		this.externalPurchaseOrderNumber = copy.externalPurchaseOrderNumber;
	}


	/**
     * 3583 : dueDate and invoiceDate should be truncated before persist or update.
     */
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        auditable.setUpdated(new Date());
        this.dueDate = DateUtils.truncateTime(this.dueDate);
        this.invoiceDate = DateUtils.truncateTime(this.invoiceDate);
        setUUIDIfNull();

        if(this.getBillingAccount() != null) {
        	CustomerAccount customerAccount = this.getBillingAccount().getCustomerAccount();
        	this.tradingCountry = billingAccount.getTradingCountry() != null ? billingAccount.getTradingCountry() :this.getSeller().getTradingCountry();
            if(this.tradingCurrency == null) {
                this.tradingCurrency = billingAccount.getTradingCurrency() != null
                        ? billingAccount.getTradingCurrency() : this.getSeller().getTradingCurrency();
                if(this.tradingCurrency == null) {
                    this.tradingCurrency = customerAccount.getTradingCurrency() != null
                            ? customerAccount.getTradingCurrency() : this.getSeller().getTradingCurrency();
                }
            }
            if(billingAccount.getTradingLanguage() != null) {
        		this.tradingLanguage = billingAccount.getTradingLanguage();
            }
        	if(this.tradingLanguage == null) {
        		this.tradingLanguage = customerAccount.getTradingLanguage() != null ? customerAccount.getTradingLanguage() : this.getSeller().getTradingLanguage();
        	}
        }
        if (this.id == null) {
            if (this.lastAppliedRateDate == null) {
                this.lastAppliedRateDate = invoiceDate;
            }
            if (this.lastAppliedRate == null) {
                if (this.tradingCurrency != null) {
                    ExchangeRate rate = this.tradingCurrency.getExchangeRate(invoiceDate);
                    this.lastAppliedRate = rate != null ? rate.getExchangeRate() : ONE;
                }
            }
        }

		if (!this.useSpecificPriceConversion) {
			BigDecimal appliedRate = getAppliedRate();
			setTransactionalAmountTax(toTransactional(amountTax, appliedRate));
			setTransactionalAmountWithoutTax(toTransactional(amountWithoutTax, appliedRate));
			setTransactionalAmountWithTax(toTransactional(amountWithTax, appliedRate));
			setTransactionalDiscountAmount(toTransactional(discountAmount, appliedRate));
			setTransactionalNetToPay(toTransactional(netToPay, appliedRate));
			setTransactionalRawAmount(toTransactional(rawAmount, appliedRate));
			setTransactionalAmountWithoutTaxBeforeDiscount(toTransactional(amountWithoutTaxBeforeDiscount, appliedRate));
			setTransactionalInvoiceBalance(toTransactional(invoiceBalance, appliedRate));
		}
        
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        if(this.status == null) {
        	this.status=InvoiceStatusEnum.NEW;
        }
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
            amountWithTax = ZERO;
        }
        if (amountToAdd != null) {
            amountWithTax = amountWithTax.add(amountToAdd);
        }
    }

    public void addAmountWithoutTax(BigDecimal amountToAdd) {
        if (amountWithoutTax == null) {
            amountWithoutTax = ZERO;
        }
        if (amountToAdd != null) {
            amountWithoutTax = amountWithoutTax.add(amountToAdd);
        }
    }

    public void addAmountTax(BigDecimal amountToAdd) {
        if (amountTax == null) {
            amountTax = ZERO;
        }
        if (amountToAdd != null) {
            amountTax = amountTax.add(amountToAdd);
        }
    }

    public void addTransactionalAmountWithTax(BigDecimal amountToAdd) {
        if (transactionalAmountWithTax == null) {
        	transactionalAmountWithTax = ZERO;
        }
        if (amountToAdd != null) {
        	transactionalAmountWithTax = transactionalAmountWithTax.add(amountToAdd);
        }
    }

    public void addTransactionalAmountWithoutTax(BigDecimal amountToAdd) {
        if (transactionalAmountWithoutTax == null) {
        	transactionalAmountWithoutTax = ZERO;
        }
        if (amountToAdd != null) {
        	transactionalAmountWithoutTax = transactionalAmountWithoutTax.add(amountToAdd);
        }
    }

    public void addTransactionalAmountTax(BigDecimal amountToAdd) {
        if (transactionalAmountTax == null) {
        	transactionalAmountTax = ZERO;
        }
        if (amountToAdd != null) {
        	transactionalAmountTax = transactionalAmountTax.add(amountToAdd);
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

    public InvoicePaymentStatusEnum getRealTimeStatus() {
    	if(dueDate!=null && dueDate.before( new Date()) && (status==InvoiceStatusEnum.VALIDATED)) {
    		return InvoicePaymentStatusEnum.UNPAID;
    	}
        return paymentStatus;
    }

    public void setStatus(InvoiceStatusEnum status) {
    	if(status == this.status) {
    		return;
    	}
    	if(status!=null && status.getPreviousStats().contains(this.status)) {
	        this.status = status;
	        setStatusDate(new Date());
    	} else {
    		throw new ValidationException("Not possible to change invoice status from "+this.status+" to "+status) ;
    	}
    }

    public void rebuildStatus(InvoiceStatusEnum status) {
    	if(status==InvoiceStatusEnum.DRAFT || status==InvoiceStatusEnum.SUSPECT || status==InvoiceStatusEnum.REJECTED) {
			setStatusDate(new Date());
	        this.status = status;
    	} else {
    		throw new ValidationException("Not possible to rebuild invoice status with "+status) ;
    	}
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

    public Set<LinkedInvoice> getLinkedInvoices() {
        return linkedInvoices;
    }

    public void setLinkedInvoices(Set<LinkedInvoice> linkedInvoices) {
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
		if (id != null) {
			num1.append(id + "");
		}
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

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getXmlDate() {
		return xmlDate;
	}

	public void setXmlDate(Date xmlDate) {
		this.xmlDate = xmlDate;
	}

	public Date getPdfDate() {
		return pdfDate;
	}

	public void setPdfDate(Date pdfDate) {
		this.pdfDate = pdfDate;
	}

	public Date getEmailSentDate() {
		return emailSentDate;
	}

	public void setEmailSentDate(Date emailSentDate) {
		this.emailSentDate = emailSentDate;
	}

	public InvoicePaymentStatusEnum getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(InvoicePaymentStatusEnum paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getPaymentStatusDate() {
		return paymentStatusDate;
	}

	public void setPaymentStatusDate(Date paymentStatusDate) {
		this.paymentStatusDate = paymentStatusDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getRawAmount() {
		return rawAmount;
	}

	public void setRawAmount(BigDecimal rawAmount) {
		this.rawAmount = rawAmount;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public boolean isAlreadyAppliedMinimum() {
		return isAlreadyAppliedMinimum;
	}

	public void setAlreadyAppliedMinimum(boolean isAlreadyAppliedMinimum) {
		this.isAlreadyAppliedMinimum = isAlreadyAppliedMinimum;
	}

	public boolean isAlreadyAddedDiscount() {
		return isAlreadyAddedDiscount;
	}

	public void setAlreadyAddedDiscount(boolean isAlreadyAddedDiscount) {
		this.isAlreadyAddedDiscount = isAlreadyAddedDiscount;
	}

	public Boolean getDraft() {
		return draft;
	}

	public void setPreviousInvoiceNumber(String previousInvoiceNumber) {
		this.previousInvoiceNumber = previousInvoiceNumber;
	}

	/**
	 * @return the newInvoicingProcess
	 */
	public boolean isNewInvoicingProcess() {
		return newInvoicingProcess;
	}

	/**
	 * @param newInvoicingProcess the newInvoicingProcess to set
	 */
	public void setNewInvoicingProcess(boolean newInvoicingProcess) {
		this.newInvoicingProcess = newInvoicingProcess;
	}

    public boolean isHasTaxes() {
        return hasTaxes;
    }

    public void setHasTaxes(boolean hasTaxes) {
        this.hasTaxes = hasTaxes;
    }

    public boolean isHasDiscounts() {
        return hasDiscounts;
    }

    public void setHasDiscounts(boolean hasDiscounts) {
        this.hasDiscounts = hasDiscounts;
    }

    public boolean isHasMinimum() {
        return hasMinimum;
    }

    public void setHasMinimum(boolean hasMinimum) {
        this.hasMinimum = hasMinimum;
    }

	/**
	 * @return the discountPlan
	 */
	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	/**
	 * @param discountPlan the discountPlan to set
	 */
	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	/**
	 * @return the invoiceLines
	 */
	public List<InvoiceLine> getInvoiceLines() {
		return invoiceLines;
	}

	/**
	 * @param invoiceLines the invoiceLines to set
	 */
	public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

	public CommercialOrder getCommercialOrder() {
		return commercialOrder;
	}

	public void setCommercialOrder(CommercialOrder commercialOrder) {
		this.commercialOrder = commercialOrder;
	}


    public List<InvoiceLine> getDraftInvoiceLines() {
        return draftInvoiceLines;
    }

    public void setDraftInvoiceLines(List<InvoiceLine> draftInvoiceLines) {
        this.draftInvoiceLines = draftInvoiceLines;
    }


	public CpqQuote getCpqQuote() {
		return cpqQuote;
	}


	public void setCpqQuote(CpqQuote cpqQuote) {
		this.cpqQuote = cpqQuote;
	}


    public BigDecimal getAmountWithoutTaxBeforeDiscount() {
        return amountWithoutTaxBeforeDiscount;
    }

    public void setAmountWithoutTaxBeforeDiscount(BigDecimal amountWithoutTaxBeforeDiscount) {
        this.amountWithoutTaxBeforeDiscount = amountWithoutTaxBeforeDiscount;
    }

    public boolean isReminderLevelTriggered() {
        return isReminderLevelTriggered;
    }

    public void setReminderLevelTriggered(boolean reminderLevelTriggered) {
        isReminderLevelTriggered = reminderLevelTriggered;
    }

    public DunningCollectionPlan getRelatedDunningCollectionPlan() {
        return relatedDunningCollectionPlan;
    }

    public void setRelatedDunningCollectionPlan(DunningCollectionPlan relatedDunningCollectionPlan) {
        this.relatedDunningCollectionPlan = relatedDunningCollectionPlan;
    }

    public boolean isDunningCollectionPlanTriggered() {
        return dunningCollectionPlanTriggered;
    }

    public void setDunningCollectionPlanTriggered(boolean dunningCollectionPlanTriggered) {
        this.dunningCollectionPlanTriggered = dunningCollectionPlanTriggered;
    }

    /**
     * @return the lastAppliedRate
     */
    public BigDecimal getLastAppliedRate() {
        return lastAppliedRate;
    }

    /**
     * @param lastAppliedRate the lastAppliedRate to set
     */
    public void setLastAppliedRate(BigDecimal lastAppliedRate) {
        this.lastAppliedRate = lastAppliedRate;
    }

    /**
     * @return the lastAppliedRateDate
     */
    public Date getLastAppliedRateDate() {
        return lastAppliedRateDate;
    }

    /**
     * @param lastAppliedRateDate the lastAppliedRateDate to set
     */
    public void setLastAppliedRateDate(Date lastAppliedRateDate) {
        this.lastAppliedRateDate = lastAppliedRateDate;
    }

    public BigDecimal getTransactionalAmountWithoutTax() {
        return transactionalAmountWithoutTax;
    }

    public void setTransactionalAmountWithoutTax(BigDecimal transactionalAmountWithoutTax) {
        this.transactionalAmountWithoutTax = transactionalAmountWithoutTax;
    }

    public BigDecimal getTransactionalAmountWithTax() {
        return transactionalAmountWithTax;
    }

    public void setTransactionalAmountWithTax(BigDecimal transactionalAmountWithTax) {
        this.transactionalAmountWithTax = transactionalAmountWithTax;
    }

    public BigDecimal getTransactionalAmountTax() {
        return transactionalAmountTax;
    }

    public void setTransactionalAmountTax(BigDecimal transactionalAmountTax) {
        this.transactionalAmountTax = transactionalAmountTax;
    }

    public BigDecimal getTransactionalNetToPay() {
        return transactionalNetToPay;
    }

    public void setTransactionalNetToPay(BigDecimal transactionalNetToPay) {
        this.transactionalNetToPay = transactionalNetToPay;
    }

    public BigDecimal getTransactionalRawAmount() {
        return transactionalRawAmount;
    }

    public void setTransactionalRawAmount(BigDecimal transactionalRawAmount) {
        this.transactionalRawAmount = transactionalRawAmount;
    }

    public BigDecimal getTransactionalDiscountAmount() {
        return transactionalDiscountAmount;
    }

    public void setTransactionalDiscountAmount(BigDecimal transactionalDiscountAmount) {
        this.transactionalDiscountAmount = transactionalDiscountAmount;
    }

    public BigDecimal getTransactionalAmountWithoutTaxBeforeDiscount() {
        return transactionalAmountWithoutTaxBeforeDiscount;
    }

    public void setTransactionalAmountWithoutTaxBeforeDiscount(BigDecimal transactionalAmountWithoutTaxBeforeDiscount) {
        this.transactionalAmountWithoutTaxBeforeDiscount = transactionalAmountWithoutTaxBeforeDiscount;
    }

    public PaymentPlan getPaymentPlan() {
        return paymentPlan;
    }

    public void setPaymentPlan(PaymentPlan paymentPlan) {
        this.paymentPlan = paymentPlan;
    }

    public String getOpenOrderNumber() {
		return openOrderNumber;
	}

	public void setOpenOrderNumber(String openOrderNumber) {
		this.openOrderNumber = openOrderNumber;
	}

	public boolean isUseCurrentRate() {
		return useCurrentRate;
	}

	public void setUseCurrentRate(boolean useCurrentRate) {
		this.useCurrentRate = useCurrentRate;
	}


	/**
     * Check if an invoice can be refreshed
     * @return refresh check result
     */
    public boolean canBeRefreshed() {
        return this.status == InvoiceStatusEnum.NEW || this.status == InvoiceStatusEnum.DRAFT
                && (this.lastAppliedRate != null
                && !this.lastAppliedRate.equals(this.tradingCurrency.getCurrentRate()));
    }

    /**
     * Get applied rate for an invoice
     * @return last applied rate
     */
    public BigDecimal getAppliedRate() {
        return this.lastAppliedRate != null && !this.lastAppliedRate.equals(ZERO) ? this.lastAppliedRate : ONE;
    }

    /**
	 * @param invoiceSCAs
	 * @return
	 */
	public void setSubCategoryInvoiceAgregate(Set<SubCategoryInvoiceAgregate> invoiceSCAs) {
		this.subCategoryInvoiceAgregates = invoiceSCAs;
	}

	/**
	 * @param invoiceSCAs
	 * @return
	 */
	public Set<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregate() {
		return this.subCategoryInvoiceAgregates;
	}

	public void setNextInvoiceDate(Date nextInvoiceDate) {
		this.nextInvoiceDate = nextInvoiceDate;
	}

	/**
	 * @return
	 */
	public Date getNextInvoiceDate() {
		return nextInvoiceDate;
	}

	/**
	 * set all invoice amounts to 0
	 */
	public void initAmounts() {
		this.amountTax=BigDecimal.ZERO;
		this.amountWithTax=BigDecimal.ZERO;
		this.amountWithoutTax=BigDecimal.ZERO;
	}

    public BigDecimal getInvoiceBalance() {
        return invoiceBalance;
    }


    public void setInvoiceBalance(BigDecimal invoiceBalance) {
        this.invoiceBalance = invoiceBalance;
    }

	public InvoiceValidationRule getRejectedByRule() {
		return rejectedByRule;
	}

	public void setRejectedByRule(InvoiceValidationRule rejectedByRule) {
		this.rejectedByRule = rejectedByRule;
	}

    public BigDecimal getTransactionalInvoiceBalance() {
        return transactionalInvoiceBalance;
    }
    public void setTransactionalInvoiceBalance(BigDecimal transactionalInvoiceBalance) {
        this.transactionalInvoiceBalance = transactionalInvoiceBalance;
    }


	/**
	 * @return the useSpecificPriceConversion
	 */
	public boolean isUseSpecificPriceConversion() {
		return useSpecificPriceConversion;
	}


	/**
	 * @param useSpecificPriceConversion the useSpecificPriceConversion to set
	 */
	public void setUseSpecificPriceConversion(boolean useSpecificPriceConversion) {
		this.useSpecificPriceConversion = useSpecificPriceConversion;
	}

	public boolean isConversionFromBillingCurrency() {
		return conversionFromBillingCurrency;
	}

	public void setConversionFromBillingCurrency(boolean conversionFromBillingCurrency) {
		this.conversionFromBillingCurrency = conversionFromBillingCurrency;
	}
	
	private BigDecimal toTransactional(BigDecimal amount, BigDecimal rate) {
		return amount != null ? amount.multiply(rate) : ZERO;
	}
	
	public boolean isAutoMatching() {
		return autoMatching;
	}
	
	public void setAutoMatching(boolean autoMatching) {
		this.autoMatching = autoMatching;
	}

	public String getExternalPurchaseOrderNumber() {
		return externalPurchaseOrderNumber;
	}

	public void setExternalPurchaseOrderNumber(String externalPurchaseOrderNumber) {
		this.externalPurchaseOrderNumber = externalPurchaseOrderNumber;
	}
	public boolean isUblReference() {
		return ublReference;
	}
	
	public void setUblReference(boolean ublReference) {
		this.ublReference = ublReference;
	}
	
	public PDPStatusEntity getPdpStatus() {
		return pdpStatus;
	}
	
	public void setPdpStatus(PDPStatusEntity pdpStatus) {
		this.pdpStatus = pdpStatus;
	}
}
