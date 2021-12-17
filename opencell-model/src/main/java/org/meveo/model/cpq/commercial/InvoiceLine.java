package org.meveo.model.cpq.commercial;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;
import static org.meveo.model.billing.InvoiceLineStatusEnum.OPEN;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.offer.QuoteOffer;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_invoice_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_invoice_line_seq")})
@NamedQueries({
		@NamedQuery(name = "InvoiceLine.listToInvoiceByBillingAccountAndIDs", query = "FROM InvoiceLine il where il.billingAccount.id=:billingAccountId AND il.status='OPEN' AND id in (:listOfIds) "),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByInvoiceID", query = "FROM InvoiceLine il WHERE il.invoice.id =:invoiceId"),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByBRs", query = "FROM InvoiceLine il WHERE il.billingRun IN (:BillingRus)"),
        @NamedQuery(name = "InvoiceLine.findByCommercialOrder", query = "select il from InvoiceLine il where il.commercialOrder = :commercialOrder"),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByBRID", query = "FROM InvoiceLine il WHERE il.billingRun.id = :billingRunId"),
		@NamedQuery(name = "InvoiceLine.AddInvoice", query = "UPDATE InvoiceLine il SET il.invoice = :inv WHERE il.id in (:ids)"),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByOrderNumber", query = "FROM InvoiceLine il where il.status='OPEN' AND il.orderNumber=:orderNumber AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate order by il.billingAccount.id "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceBySubscription", query = "FROM InvoiceLine il where il.subscription.id=:subscriptionId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByBillingAccount", query = "FROM InvoiceLine il where il.billingAccount.id=:billingAccountId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate"),
		@NamedQuery(name = "InvoiceLine.updateWithInvoice", query = "UPDATE InvoiceLine il set il.status=org.meveo.model.billing.InvoiceLineStatusEnum.BILLED, il.auditable.updated = :now , il.billingRun=:billingRun, il.invoice=:invoice, il.invoiceAggregateF=:invoiceAgregateF where il.id in :ids"),
		@NamedQuery(name = "InvoiceLine.updateWithInvoiceInfo", query = "UPDATE InvoiceLine il set il.status=org.meveo.model.billing.InvoiceLineStatusEnum.BILLED, il.billingRun=:billingRun, il.auditable.updated = :now, il.invoice=:invoice, il.amountWithoutTax=:amountWithoutTax, il.amountWithTax=:amountWithTax, il.amountTax=:amountTax, il.tax=:tax, il.taxRate=:taxPercent, il.invoiceAggregateF=:invoiceAgregateF where il.id=:id"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableByOrderNumber", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND il.orderNumber=:orderNumber AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableByServiceWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.serviceInstance.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.subscription=:subscription AND il.serviceInstance.minimumAmountEl is not null GROUP BY il.serviceInstance.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableByServiceWithMinAmountByBillingAccount", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.serviceInstance.id  FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.billingAccount=:billingAccount AND il.serviceInstance.minimumAmountEl is not null GROUP BY il.serviceInstance.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableBySubscriptionWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.subscription=:subscription AND il.subscription.minimumAmountEl is not null GROUP BY il.subscription.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableBySubscriptionWithMinAmountByBillingAccount", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.id  FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.billingAccount=:billingAccount AND il.subscription.minimumAmountEl is not null GROUP BY il.subscription.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForUAWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.userAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.subscription.userAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.subscription.userAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForBAWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.billingAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByBillingAccount", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount=:billingAccount GROUP BY il.subscription.seller.id, il.billingAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForCAWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.billingAccount.customerAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByCA", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount.customerAccount=:customerAccount GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForCustomerWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.customer.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.billingAccount.customerAccount.customer.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByCustomer", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.customer.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount.customerAccount.customer=:customer GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableBySubscription", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableByBA", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount=:billingAccount"),
		@NamedQuery(name = "InvoiceLine.sumPositiveILByBillingRun", query = "select sum(il.amountWithoutTax), sum(il.amountWithTax), il.invoice.id, il.billingAccount.id, il.billingAccount.customerAccount.id, il.billingAccount.customerAccount.customer.id FROM InvoiceLine il where il.billingRun.id=:billingRunId and il.amountWithoutTax > 0 and il.status='BILLED' group by il.invoice.id, il.billingAccount.id, il.billingAccount.customerAccount.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.unInvoiceByInvoiceIds", query = "update InvoiceLine il set il.status='OPEN', il.auditable.updated = :now , il.billingRun= null, il.invoice=null, il.accountingArticle=null where il.status=org.meveo.model.billing.InvoiceLineStatusEnum.BILLED and il.invoice.id IN (:invoiceIds)"),
		@NamedQuery(name = "InvoiceLine.deleteSupplementalILByInvoiceIds", query = "DELETE from InvoiceLine il WHERE il.invoice.id IN (:invoicesIds)"),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByCommercialOrder", query = "FROM InvoiceLine il where il.commercialOrder.id=:commercialOrderId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.BillingAccountByILIds", query = "SELECT il.billingAccount FROM InvoiceLine il WHERE il.id in (:ids)"),
		@NamedQuery(name = "InvoiceLine.listByInvoice", query = "SELECT il FROM InvoiceLine il where il.invoice=:invoice and il.status='BILLED' order by il.valueDate"),
		@NamedQuery(name = "InvoiceLine.listByInvoiceNotFree", query = "SELECT il FROM InvoiceLine il where il.invoice=:invoice and il.amountWithoutTax<>0 and il.status='BILLED' order by il.valueDate"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableByQuote", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND il.quote.id=:quoteId AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByQuote", query = "FROM InvoiceLine il where il.quote.id=:quoteId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.findByQuote", query = "select il from InvoiceLine il where il.quote =:quote"),
		@NamedQuery(name = "InvoiceLine.deleteInvoiceAggrByInvoice", query = "UPDATE InvoiceLine il set il.invoiceAggregateF=null where il.invoice.id=:invoiceId"),
		})
public class InvoiceLine extends AuditableEntity {

	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;
	
	@Column(name = "prestation")
	@Size(max = 255)
	private String prestation;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "accounting_article_id", nullable = false)
	private AccountingArticle accountingArticle;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "offer_service_template_id")
	private OfferServiceTemplate offerServiceTemplate;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "service_template_id")
	private ServiceTemplate serviceTemplate;

    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "begin_date")), @AttributeOverride(name = "to", column = @Column(name = "end_date")) })
    private DatePeriod validity = new DatePeriod();
    
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal unitPrice;
    
    @Column(name = "discount_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal discountRate = BigDecimal.ZERO;

    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal amountWithoutTax;

    @Column(name = "tax_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal taxRate;

    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal amountWithTax;

    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal amountTax;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id")
    private DiscountPlan discountPlan;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "tax_id")
    private Tax tax;
    
	@Column(name = "order_ref", length = 20)
	@Size(max = 20)
    private String orderRef;
	
	@Column(name = "access_point", length = 20)
	@Size(max = 20)
    private String accessPoint;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "commercial_order_id")
    private CommercialOrder commercialOrder;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "billing_run_id")
	private BillingRun billingRun;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "billing_account_id")
	private BillingAccount billingAccount;

	@Column(name = "value_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date valueDate;

	@Column(name = "order_number")
	private String orderNumber;

	@Column(name = "discount_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(name = "label")
	private String label;

	@Column(name = "raw_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal rawAmount = BigDecimal.ZERO;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "service_instance_id")
	private ServiceInstance serviceInstance;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "subscription_id")
	private Subscription subscription;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "offer_template_id")
	private OfferTemplate offerTemplate;
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "product_version_id")
	private ProductVersion productVersion;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;

	@Transient
	private boolean taxRecalculated;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@NotNull
	private InvoiceLineStatusEnum status = OPEN;

	@OneToMany(mappedBy = "invoiceLine", fetch = LAZY)
	private List<RatedTransaction> ratedTransactions;

	/**
	 * Subcategory invoice aggregate that invoice line was invoiced under
	 */
	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "aggregate_id_f")
	private SubCategoryInvoiceAgregate invoiceAggregateF;
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "cpq_quote_id")
    private CpqQuote quote;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_offer_id")
	private QuoteOffer quoteOffer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_offer_id")
	private OrderOffer orderOffer;

	public InvoiceLine() {
	}

	public InvoiceLine(Date valueDate, BigDecimal quantity, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, InvoiceLineStatusEnum status,
					   BillingAccount billingAccount, String label, Tax tax, BigDecimal taxRate, AccountingArticle accountingArticle) {
		this.label = label;
		this.valueDate = valueDate;
		this.quantity = quantity;
		this.amountWithoutTax = amountWithoutTax;
		this.amountWithTax = amountWithTax;
		this.amountTax = amountTax;
		this.status = status;
		this.billingAccount = billingAccount;
		this.tax = tax;
		this.taxRate = taxRate;
		this.accountingArticle = accountingArticle;
	}
	
	

	public InvoiceLine(InvoiceLine copy, Invoice invoice) {
		this.invoice = invoice;
		this.prestation = copy.prestation;
		this.accountingArticle = copy.accountingArticle;
		this.offerServiceTemplate = copy.offerServiceTemplate;
		this.product = copy.product;
		this.serviceTemplate = copy.serviceTemplate;
		this.validity = copy.validity;
		this.quantity = copy.quantity;
		this.unitPrice = copy.unitPrice;
		this.discountRate = copy.discountRate;
		this.amountWithoutTax = copy.amountWithoutTax;
		this.taxRate = copy.taxRate;
		this.amountWithTax = copy.amountWithTax;
		this.amountTax = copy.amountTax;
		this.discountPlan = copy.discountPlan;
		this.tax = copy.tax;
		this.orderRef = copy.orderRef;
		this.accessPoint = copy.accessPoint;
		this.commercialOrder = copy.commercialOrder;
		this.billingRun = copy.billingRun;
		this.billingAccount = copy.billingAccount;
		this.valueDate = copy.valueDate;
		this.orderNumber = copy.orderNumber;
		this.discountAmount = copy.discountAmount;
		this.label = copy.label;
		this.rawAmount = copy.rawAmount;
		this.serviceInstance = copy.serviceInstance;
		this.subscription = copy.subscription;
		this.offerTemplate = copy.offerTemplate;
		this.productVersion = copy.productVersion;
		this.orderLot = copy.orderLot;
		this.taxRecalculated = copy.taxRecalculated;
		this.status = InvoiceLineStatusEnum.OPEN;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public String getPrestation() {
		return prestation;
	}

	public void setPrestation(String prestation) {
		this.prestation = prestation;
	}

	public AccountingArticle getAccountingArticle() {
		return accountingArticle;
	}

	public void setAccountingArticle(AccountingArticle accountingArticle) {
		this.accountingArticle = accountingArticle;
	}

	public OfferServiceTemplate getOfferServiceTemplate() {
		return offerServiceTemplate;
	}

	public void setOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
		this.offerServiceTemplate = offerServiceTemplate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public DatePeriod getValidity() {
		return validity;
	}

	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}

	public String getAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}

	public CommercialOrder getCommercialOrder() {
		return commercialOrder;
	}

	public void setCommercialOrder(CommercialOrder commercialOrder) {
		this.commercialOrder = commercialOrder;
	}

	public BillingRun getBillingRun() {
		return billingRun;
	}

	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getRawAmount() {
		return rawAmount;
	}

	public void setRawAmount(BigDecimal rawAmount) {
		this.rawAmount = rawAmount;
	}

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public ProductVersion getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}

	public OrderLot getOrderLot() {
		return orderLot;
	}

	public void setOrderLot(OrderLot orderLot) {
		this.orderLot = orderLot;
	}

	public boolean isTaxOverridden() {
		return accountingArticle.getTaxClass() == null;
	}

	public boolean isTaxRecalculated() {
		return taxRecalculated;
	}

	public void setTaxRecalculated(boolean taxRecalculated) {
		this.taxRecalculated = taxRecalculated;
	}

	public void computeDerivedAmounts(boolean isEnterprise, int rounding, RoundingModeEnum roundingMode) {
		BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amountWithoutTax, amountWithTax, taxRate, isEnterprise, rounding, roundingMode.getRoundingMode());
		amountWithoutTax = amounts[0];
		amountWithTax = amounts[1];
		amountTax = amounts[2];
	}

	public InvoiceLineStatusEnum getStatus() {
		return status;
	}

	public void setStatus(InvoiceLineStatusEnum status) {
		this.status = status;
	}

	public List<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public SubCategoryInvoiceAgregate getInvoiceAggregateF() {
		return invoiceAggregateF;
	}

	public void setInvoiceAggregateF(SubCategoryInvoiceAgregate invoiceAggregateF) {
		this.invoiceAggregateF = invoiceAggregateF;
	}

	public CpqQuote getQuote() {
		return quote;
	}

	public void setQuote(CpqQuote quote) {
		this.quote = quote;
	}

	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}

	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}

	public OrderOffer getOrderOffer() {
		return orderOffer;
	}

	public void setOrderOffer(OrderOffer orderOffer) {
		this.orderOffer = orderOffer;
	}
	
	
	
}
