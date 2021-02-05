package org.meveo.model.cpq.commercial;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.*;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.FetchType.LAZY;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_invoice_line", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_invoice_line_seq")})
@NamedQueries({
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByBRs", query = "FROM InvoiceLine il WHERE il.billingRun IN (:BillingRus)"),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByBRID", query = "FROM InvoiceLine il WHERE il.billingRun.id = :billingRunId"),
		@NamedQuery(name = "InvoiceLine.AddInvoice", query = "UPDATE InvoiceLine il SET il.invoice = :inv WHERE il.id in (:ids)"),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByOrderNumber", query = "FROM InvoiceLine il where il.orderNumber=:orderNumber AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate order by il.billingAccount.id "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceBySubscription", query = "FROM InvoiceLine il where il.subscription.id=:subscriptionId AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByBillingAccount", query = "FROM InvoiceLine il where il.billingAccount.id=:billingAccountId AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate"),
		@NamedQuery(name = "InvoiceLine.updateWithInvoice", query = "UPDATE InvoiceLine il set il.auditable.updated = :now , il.invoice=:invoice where il.id in :ids"),
		@NamedQuery(name = "InvoiceLine.updateWithInvoiceInfo", query = "UPDATE InvoiceLine il set il.auditable.updated = :now, il.invoice=:invoice, il.amountWithoutTax=:amountWithoutTax, il.amountWithTax=:amountWithTax, il.amountTax=:amountTax, il.tax=:tax, il.taxRate=:taxPercent where il.id=:id")

})
public class InvoiceLine extends BusinessEntity {

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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;

	@Transient
	private boolean taxRecalculated;

	public InvoiceLine() {
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
}
