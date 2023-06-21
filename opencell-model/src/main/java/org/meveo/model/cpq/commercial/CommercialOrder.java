package org.meveo.model.cpq.commercial;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.Order;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.quote.QuoteVersion;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@ObservableEntity
@WorkflowedEntity
@Entity
@Table(name = "cpq_commercial_order")
@CustomFieldEntity(cftCodePrefix = "CommercialOrder",inheritCFValuesFrom = {"quoteVersion"})
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_commercial_order_seq")})
@NamedQueries({
		@NamedQuery(name = "CommercialOrder.getOrderIdsUsingCharge", query = "select op.order.id from OrderProduct op where op.order.status not in('CANCELED','VALIDATED','COMPLETED') and op.productVersion.product in (select pc.product from ProductChargeTemplateMapping pc where pc.chargeTemplate.id in (:chargeIds))"),
		@NamedQuery(name = "CommercialOrder.listByCodeOrExternalId", query = "select co from CommercialOrder co where co.code IN :code OR co.externalReference IN :code order by co.id"),
		@NamedQuery(name = "CommercialOrder.findByCodeOrExternalId", query = "select co from CommercialOrder co left join fetch co.billingRun where co.code = :code OR co.externalReference = :code "),
		@NamedQuery(name = "CommercialOrder.findWithInvoicingPlan", query = "select co from CommercialOrder co where co.invoicingPlan is not null")
})
public class CommercialOrder extends BusinessCFEntity implements IBillableEntity  {


	@Transient
	private Integer orderProgressTmp;
	
	public CommercialOrder() {
	}

	public CommercialOrder(CommercialOrder copy) {
		this.seller = copy.seller;
		this.orderNumber = copy.orderNumber;
		this.label = copy.label;
		this.billingAccount = copy.billingAccount;
		this.quote = copy.quote;
		this.contract = copy.contract;
		this.orderType = copy.orderType;
		this.invoicingPlan = copy.invoicingPlan;
		this.orderProgress = copy.orderProgress;
		this.progressDate = copy.progressDate;
		this.orderDate = copy.orderDate;
		this.deliveryDate = copy.deliveryDate;
		this.customerServiceBegin = copy.customerServiceBegin;
		this.customerServiceDuration = copy.customerServiceDuration;
		this.externalReference = copy.externalReference;
		this.orderParent = copy.orderParent;
		this.orderInvoiceType = copy.orderInvoiceType;
		this.access = copy.access;
		this.userAccount = copy.userAccount;
		this.salesPersonName = copy.salesPersonName;
		this.billingCycle = copy.billingCycle;
		this.billingRun = copy.billingRun;
		this.totalInvoicingAmountWithoutTax = copy.totalInvoicingAmountWithoutTax;
		this.totalInvoicingAmountWithTax = copy.totalInvoicingAmountWithTax;
		this.totalInvoicingAmountTax = copy.totalInvoicingAmountTax;
		this.minInvoiceLines = copy.minInvoiceLines;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	@NotNull
	private Seller seller;

	@Column(name = "order_number", length = 50)
	@Size(max = 50)
	private String orderNumber;

	@Column(name = "label", length = 255)
	private String label;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id", nullable = false)
	@NotNull
	private BillingAccount billingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_id")
	private CpqQuote quote;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_type_id")
	private OrderType orderType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoicing_plan_id")
	private InvoicingPlan invoicingPlan;


	@Column(name ="status", nullable = false)
	@NotNull
	private String status;

	@Column(name = "status_date", nullable = false)
	@NotNull
	private Date statusDate;


	@Column(name = "order_progress", nullable = false)
	@NotNull
	private Integer orderProgress = Integer.valueOf(0);

	@Column(name = "rate_invoiced", nullable = false)
	@NotNull
	private Integer rateInvoiced = Integer.valueOf(0);

	@Column(name = "progress_date", nullable = false)
	@NotNull
	private Date progressDate;


	@Column(name = "order_date", nullable = false)
	@NotNull
	private Date orderDate;


	@Column(name = "realisation_date")
	private Date deliveryDate;

	@Column(name = "customer_service_begin")
	private Date customerServiceBegin;


	@Column(name = "customer_service_duration")
	private int customerServiceDuration;

	@Column(name = "external_reference", length = 50)
	@Size(max = 50)
	private String externalReference;
	
	/**
	 * The sales person name
	 */
	@Column(name = "sales_person_name", length = 52)
	@Size(max = 52)
	private String salesPersonName;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_parent_id")
	private Order orderParent;


	@JoinTable(name = "cpq_commercial_order_billing_invoice",
			joinColumns = @JoinColumn(name = "commercial_order_id"),
			inverseJoinColumns = @JoinColumn(name = "invoice_id"))
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Invoice> invoices = new ArrayList<Invoice>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_type_id", nullable = false)
	@NotNull
	private InvoiceType orderInvoiceType;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "access_id")
	private Access access;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
	private List<OrderOffer> offers;

	
	//@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
	@ManyToMany(fetch = FetchType.LAZY, cascade = ALL)
    @JoinTable(name = "cpq_commercial_order_order_lot", joinColumns = @JoinColumn(name = "commercial_order_id"), inverseJoinColumns = @JoinColumn(name = "order_lot_id"))
	private List<OrderLot> orderLots;
	
	
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
	private List<OrderPrice> orderPrices;
	
    @Column(name = "oneshot_total_amount")
    private BigDecimal oneShotTotalAmount;
    
    @Transient
    private QuoteVersion quoteVersion;
    
	
	/**
	 * discountPlan attached to this orderOffer
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;

	    /**
     * Billing cycle when invoicing by order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_cycle_id")
    private BillingCycle billingCycle;

    /**
     * Last billing run that processed this order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

	/**
	 * Default PriceList (Optional)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_list_id")
	private PriceList priceList;

	/**
     * Rated transactions to reach minimum amount per invoice
     */
    @Transient
    private List<RatedTransaction> minRatedTransactions;

    /**
     * Total invoicing amount without tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithoutTax;

    /**
     * Total invoicing amount with tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithTax;

    /**
     * Total invoicing tax amount
     */
    @Transient
    private BigDecimal totalInvoicingAmountTax;

    @Transient
    private List<InvoiceLine> minInvoiceLines;
	
    
    @Override
	public ICustomFieldEntity[] getParentCFEntities() {
		if(quoteVersion != null && quoteVersion.getStatus().equals(VersionStatusEnum.PUBLISHED))
			return new ICustomFieldEntity[] { quoteVersion };
		return null;
	}
    
    
	/**
	 * @return the seller
	 */
	public Seller getSeller() {
		return seller;
	}


	/**
	 * @param seller the seller to set
	 */
	public void setSeller(Seller seller) {
		this.seller = seller;
	}


	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}


	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}


	/**
	 * @return the billingAccount
	 */
	public BillingAccount getBillingAccount() {
		return billingAccount;
	}


	/**
	 * @param billingAccount the billingAccount to set
	 */
	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}


	/**
	 * @return the quote
	 */
	public CpqQuote getQuote() {
		return quote;
	}


	/**
	 * @param quote the quote to set
	 */
	public void setQuote(CpqQuote quote) {
		this.quote = quote;
	}


	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}


	/**
	 * @param contract the contract to set
	 */
	public void setContract(Contract contract) {
		this.contract = contract;
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}


	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}


	/**
	 * @return the orderProgress
	 */
	public Integer getOrderProgress() {
		return orderProgress;
	}


	/**
	 * @param orderProgress the orderProgress to set
	 */
	public void setOrderProgress(Integer orderProgress) {
		this.orderProgress = orderProgress;
	}


	/**
	 * @return the progressDate
	 */
	public Date getProgressDate() {
		return progressDate;
	}


	/**
	 * @param progressDate the progressDate to set
	 */
	public void setProgressDate(Date progressDate) {
		this.progressDate = progressDate;
	}


	/**
	 * @return the orderDate
	 */
	public Date getOrderDate() {
		return orderDate;
	}


	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}


	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}


	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	/**
	 * @return the customerServiceBegin
	 */
	public Date getCustomerServiceBegin() {
		return customerServiceBegin;
	}


	/**
	 * @param customerServiceBegin the customerServiceBegin to set
	 */
	public void setCustomerServiceBegin(Date customerServiceBegin) {
		this.customerServiceBegin = customerServiceBegin;
	}


	/**
	 * @return the customerServiceDuration
	 */
	public int getCustomerServiceDuration() {
		return customerServiceDuration;
	}


	/**
	 * @param customerServiceDuration the customerServiceDuration to set
	 */
	public void setCustomerServiceDuration(int customerServiceDuration) {
		this.customerServiceDuration = customerServiceDuration;
	}


	/**
	 * @return the externalReference
	 */
	public String getExternalReference() {
		return externalReference;
	}


	/**
	 * @param externalReference the externalReference to set
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	
	/**
	 * @return the salesPersonName
	 */
	public String getSalesPersonName() {
		return salesPersonName;
	}
	
	/**
	 * @param salesPersonName the salesPersonName to set
	 */
	public void setSalesPersonName(String salesPersonName) {
		this.salesPersonName = salesPersonName;
	}

	/**
	 * @return the orderParent
	 */
	public Order getOrderParent() {
		return orderParent;
	}


	/**
	 * @param orderParent the orderParent to set
	 */
	public void setOrderParent(Order orderParent) {
		this.orderParent = orderParent;
	}


	/**
	 * @return the invoices
	 */
	public List<Invoice> getInvoices() {
		return invoices;
	}


	/**
	 * @param invoices the invoices to set
	 */
	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}


	/**
	 * @return the orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}


	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}


	/**
	 * @return the invoicingPlan
	 */
	public InvoicingPlan getInvoicingPlan() {
		return invoicingPlan;
	}


	/**
	 * @return the orderInvoiceType
	 */
	public InvoiceType getOrderInvoiceType() {
		return orderInvoiceType;
	}

	/**
	 * @param orderInvoiceType the orderInvoiceType to set
	 */
	public void setOrderInvoiceType(InvoiceType orderInvoiceType) {
		this.orderInvoiceType = orderInvoiceType;
	}

	/**
	 * @param invoicingPlan the invoicingPlan to set
	 */
	public void setInvoicingPlan(InvoicingPlan invoicingPlan) {
		this.invoicingPlan = invoicingPlan;
	}

	/**
	 * @return the userAccount
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}

	/**
	 * @param userAccount the userAccount to set
	 */
	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	/**
	 * @return the access
	 */
	public Access getAccess() {
		return access;
	}

	/**
	 * @param access the access to set
	 */
	public void setAccess(Access access) {
		this.access = access;
	}

	/**
	 * @return the orderProgressTmp
	 */
	public Integer getOrderProgressTmp() {
		return orderProgressTmp;
	}

	/**
	 * @param orderProgressTmp the orderProgressTmp to set
	 */
	public void setOrderProgressTmp(Integer orderProgressTmp) {
		this.orderProgressTmp = orderProgressTmp;
	}

	public List<OrderOffer> getOffers() {
		return offers;
	}

	public void setOffers(List<OrderOffer> offers) {
		this.offers = offers;
	}

	public Integer getRateInvoiced() {
		return rateInvoiced;
	}

	public void setRateInvoiced(Integer rateInvoiced) {
		this.rateInvoiced = rateInvoiced;
	}

	public void addInvoicedRate(BigDecimal rateToBill) {
		this.rateInvoiced = this.rateInvoiced.intValue() + rateToBill.intValue();
	}

	/**
	 * @return the orderLots
	 */
	public List<OrderLot> getOrderLots() {
		return orderLots;
	}

	/**
	 * @param orderLots the orderLots to set
	 */
	public void setOrderLots(List<OrderLot> orderLots) {
		this.orderLots = orderLots;
	}

	/**
	 * @return the orderPrices
	 */
	public List<OrderPrice> getOrderPrices() {
		return orderPrices;
	}

	/**
	 * @param orderPrices the orderPrices to set
	 */
	public void setOrderPrices(List<OrderPrice> orderPrices) {
		this.orderPrices = orderPrices;
	}

	/**
	 * @return the oneShotTotalAmount
	 */
	public BigDecimal getOneShotTotalAmount() {
		return oneShotTotalAmount;
	}

	/**
	 * @param oneShotTotalAmount the oneShotTotalAmount to set
	 */
	public void setOneShotTotalAmount(BigDecimal oneShotTotalAmount) {
		this.oneShotTotalAmount = oneShotTotalAmount;
	}

	@Override
	public BillingCycle getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(BillingCycle billingCycle) {
		this.billingCycle = billingCycle;
	}

	@Override
	public BillingRun getBillingRun() {
		return billingRun;
	}

	@Override
	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
	}

	@Override
	public List<RatedTransaction> getMinRatedTransactions() {
		return minRatedTransactions;
	}

	@Override
	public void setMinRatedTransactions(List<RatedTransaction> minRatedTransactions) {
		this.minRatedTransactions = minRatedTransactions;
	}

	@Override
	public BigDecimal getTotalInvoicingAmountWithoutTax() {
		return totalInvoicingAmountWithoutTax;
	}

	@Override
	public void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax) {
		this.totalInvoicingAmountWithoutTax = totalInvoicingAmountWithoutTax;
	}

	@Override
	public BigDecimal getTotalInvoicingAmountWithTax() {
		return totalInvoicingAmountWithTax;
	}

	@Override
	public void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax) {
		this.totalInvoicingAmountWithTax = totalInvoicingAmountWithTax;
	}

	@Override
	public BigDecimal getTotalInvoicingAmountTax() {
		return totalInvoicingAmountTax;
	}

	@Override
	public void setTotalInvoicingAmountTax(BigDecimal totalInvoicingAmountTax) {
		this.totalInvoicingAmountTax = totalInvoicingAmountTax;
	}

	@Override
	public List<InvoiceLine> getMinInvoiceLines() {
		return minInvoiceLines;
	}

	@Override
	public void setMinInvoiceLines(List<InvoiceLine> minInvoiceLines) {
		this.minInvoiceLines = minInvoiceLines;
	}

	/**
	 * @return the quoteVersion
	 */
	public QuoteVersion getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(QuoteVersion quoteVersion) {
		this.quoteVersion = quoteVersion;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	/**
	 * PriceList Getter
	 * @return the priceList
	 */
	public PriceList getPriceList() {
		return priceList;
	}

	/**
	 * PriceList Setter
	 * @param priceList value to set
	 */
	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}
}
