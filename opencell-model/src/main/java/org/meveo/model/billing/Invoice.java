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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.order.Order;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.persistence.CustomFieldValuesConverter;
import org.meveo.model.quote.Quote;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
@Entity
@ObservableEntity
@Table(name = "billing_invoice", uniqueConstraints = @UniqueConstraint(columnNames = { "invoice_number", "invoice_type_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_seq"), })
@CustomFieldEntity(cftCodePrefix = "INVOICE")
@NamedQueries({
        @NamedQuery(name = "Invoice.validatedByBRNoXml", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.billingRun.id=:billingRunId and inv.xmlFilename IS NULL"),
        @NamedQuery(name = "Invoice.validatedNoXml", query = "select inv.id from Invoice inv where inv.xmlFilename IS NULL and inv.invoiceNumber IS NOT NULL"),
        @NamedQuery(name = "Invoice.validatedNoPdf", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL"),
        @NamedQuery(name = "Invoice.validatedNoPdfByBR", query = "select inv.id from Invoice inv where inv.invoiceNumber IS NOT NULL and inv.pdfFilename IS NULL and inv.xmlFilename IS NOT NULL and inv.billingRun.id=:billingRunId"),
        @NamedQuery(name = "Invoice.invoicesToNumberSummary", query = "select inv.invoiceType.id, inv.seller.id, inv.invoiceDate, count(inv) from Invoice inv where inv.billingRun.id=:billingRunId group by inv.invoiceType.id, inv.seller.id, inv.invoiceDate"),
        @NamedQuery(name = "Invoice.byBrItSelDate", query = "select inv.id from Invoice inv where inv.billingRun.id=:billingRunId and inv.invoiceType.id=:invoiceTypeId and inv.seller.id = :sellerId and inv.invoiceDate=:invoiceDate order by inv.id"),
        @NamedQuery(name = "Invoice.nullifyInvoiceFileNames", query = "update Invoice inv set inv.pdfFilename = null , inv.xmlFilename = null where inv.billingRun = :billingRun"),
        @NamedQuery(name = "Invoice.byBr", query = "select inv from Invoice inv left join fetch inv.billingAccount ba where inv.billingRun.id=:billingRunId") })
public class Invoice extends AuditableEntity implements ICustomFieldEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_invoice_id")
    private RecordedInvoice recordedInvoice;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceAgregate> invoiceAgregates = new ArrayList<>();

    @Column(name = "invoice_number", length = 50)
    @Size(max = 50)
    private String invoiceNumber;

    @Column(name = "temporary_invoice_number", length = 60, unique = true)
    @Size(max = 60)
    private String temporaryInvoiceNumber;

    @Column(name = "product_date")
    private Date productDate;

    @Column(name = "invoice_date")
    private Date invoiceDate;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;

    @Column(name = "discount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discount;

    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountTax;

    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithTax;

    @Column(name = "net_to_pay", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal netToPay;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethodType;

    @Column(name = "iban", length = 255)
    @Size(max = 255)
    private String iban;

    @Column(name = "alias", length = 255)
    @Size(max = 255)
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    private TradingLanguage tradingLanguage;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<RatedTransaction> ratedTransactions = new ArrayList<>();

    @Column(name = "comment", length = 1200)
    @Size(max = 1200)
    private String comment;

    @Type(type = "numeric_boolean")
    @Column(name = "detailed_invoice")
    private boolean isDetailedInvoice = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice adjustedInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;

    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    // @Type(type = "json")
    @Convert(converter = CustomFieldValuesConverter.class)
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    @ManyToMany
    @JoinTable(name = "billing_linked_invoices", joinColumns = { @JoinColumn(name = "id") }, inverseJoinColumns = { @JoinColumn(name = "linked_invoice_id") })
    private Set<Invoice> linkedInvoices = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_invoices_orders", joinColumns = @JoinColumn(name = "invoice_id"), inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<Order> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
    
    @Column(name = "due_balance", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal dueBalance;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Transient
    private Long invoiceAdjustmentCurrentSellerNb;

    @Transient
    private Long invoiceAdjustmentCurrentProviderNb;
    
    
    /**
     * 3583 : dueDate and invoiceDate should be truncated before persist or update.
     */
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        this.dueDate =  DateUtils.truncateTime(this.dueDate);
        this.invoiceDate =  DateUtils.truncateTime(this.invoiceDate);
        setUUIDIfNull();
    }

    public List<RatedTransaction> getRatedTransactions() {
        return ratedTransactions;
    }

    public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }
    
    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { billingRun };
    }

    public Set<Invoice> getLinkedInvoices() {
        return linkedInvoices;
    }

    public void setLinkedInvoices(Set<Invoice> linkedInvoices) {
        this.linkedInvoices = linkedInvoices;
    }

    public void addInvoiceAggregate(InvoiceAgregate obj) {
        if (!invoiceAgregates.contains(obj)) {
            invoiceAgregates.add(obj);
        }
    }

    public List<SubCategoryInvoiceAgregate> getDiscountAgregates() {
        List<SubCategoryInvoiceAgregate> aggregates = new ArrayList<>();

        for (InvoiceAgregate invoiceAggregate : invoiceAgregates) {
            if (invoiceAggregate instanceof SubCategoryInvoiceAgregate && invoiceAggregate.isDiscountAggregate()) {
                aggregates.add((SubCategoryInvoiceAgregate) invoiceAggregate);
            }
        }

        return aggregates;
    }

    public List<RatedTransaction> getRatedTransactionsForCategory(WalletInstance wallet, InvoiceSubCategory invoiceSubCategory) {

        List<RatedTransaction> ratedTransactionsMatched = new ArrayList<>();

        for (RatedTransaction ratedTransaction : ratedTransactions) {
            if (ratedTransaction.getWallet().equals(wallet) && ratedTransaction.getInvoiceSubCategory().equals(invoiceSubCategory)) {
                ratedTransactionsMatched.add(ratedTransaction);
            }

        }
        return ratedTransactionsMatched;
    }

    /**
     * @return the quote
     */
    public Quote getQuote() {
        return quote;
    }

    /**
     * @param quote the quote to set
     */
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    /**
     * Return a PDF filename. Including any subdirectories it might contain. E.g. for "a/b/c.pdf", this method will return "a/b/c.pdf"
     * 
     * @return PDF file name
     */
    public String getPdfFilename() {
        return pdfFilename;
    }

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
     * Return a XML filename. Including any subdirectories it might contain. E.g. for "a/b/c.xml", this method will return "a/b/c.xml"
     * 
     * @return XML file name
     */
    public String getXmlFilename() {
        return xmlFilename;
    }

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
	

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}