package org.meveo.model.cpq.commercial;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.Order;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@ObservableEntity
@WorkflowedEntity
@Entity
@Table(name = "cpq_commercial_order")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_commercial_order_seq")})
public class CommercialOrder extends AuditableEntity {


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
		this.realisationDate = copy.realisationDate;
		this.customerServiceBegin = copy.customerServiceBegin;
		this.customerServiceDuration = copy.customerServiceDuration;
		this.externalReference = copy.externalReference;
		this.orderParent = copy.orderParent;
		this.orderInvoiceType = copy.orderInvoiceType;
		this.access = copy.access;
		this.userAccount = copy.userAccount;
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
	@JoinColumn(name = "order_type_id", nullable = false)
	@NotNull
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

	@Column(name = "progress_date", nullable = false)
	@NotNull
	private Date progressDate;


	@Column(name = "order_date", nullable = false)
	@NotNull
	private Date orderDate;


	@Column(name = "realisation_date")
	private Date realisationDate;

	@Column(name = "customer_service_begin")
	private Date customerServiceBegin;


	@Column(name = "customer_service_duration")
	private int customerServiceDuration;

	@Column(name = "external_reference", length = 50)
	@Size(max = 50)
	private String externalReference;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_parent_id")
	private Order orderParent;


	@JoinTable(name = "cpq_commercial_order_billing_invoice",
			joinColumns = @JoinColumn(name = "commercial_order_id"),
			inverseJoinColumns = @JoinColumn(name = "invoice_id"))
	@OneToMany(fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
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
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;

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
	 * @return the realisationDate
	 */
	public Date getRealisationDate() {
		return realisationDate;
	}


	/**
	 * @param realisationDate the realisationDate to set
	 */
	public void setRealisationDate(Date realisationDate) {
		this.realisationDate = realisationDate;
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

	/**
	 * @return the orderLot
	 */
	public OrderLot getOrderLot() {
		return orderLot;
	}

	/**
	 * @param orderLot the orderLot to set
	 */
	public void setOrderLot(OrderLot orderLot) {
		this.orderLot = orderLot;
	}
}
