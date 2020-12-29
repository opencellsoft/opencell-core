package org.meveo.model.cpq.commercial;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceType;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_order_invoice")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_invoice_seq")})
public class OrderInvoice extends BusinessEntity {


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	@NotNull
	private Seller seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id", nullable = false)
	@NotNull
	private BillingAccount billingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_type_id", nullable = false)
	@NotNull
	private InvoiceType  invoiceType;
	
	@Column(name = "invoice_ref", length = 20)
	private String invoiceRef;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private OrderInvoiceStatusEnum status;
	
	@Column(name = "status_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date statusDate;
	
	@Column(name = "invoice_date", nullable = false)
	@NotNull
	private Date invoiceDate;
	
	@Column(name = "due_date", nullable = false)
	@NotNull
	private Date dueDate;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "access_point_list")
	@CollectionTable(name = "cpq_order_invoice_access_point_list", joinColumns = @JoinColumn(name = "order_invoice_id", referencedColumnName = "id"))
	private Set<String> accessPointList = new HashSet<String>();

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "order_ref_list")
	@CollectionTable(name = "cpq_order_invoice_order_ref_list", joinColumns = @JoinColumn(name = "order_invoice_id", referencedColumnName = "id"))
	private Set<String> orderRefList = new HashSet<String>();

    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "begin_date")), @AttributeOverride(name = "to", column = @Column(name = "end_date")) })
    private DatePeriod validity = new DatePeriod();

	@Column(name = "amount_without_tax", nullable = false)
	@NotNull
    private BigDecimal amountWithoutTax;

	@Column(name = "amount_with_tax", nullable = false)
	@NotNull
    private BigDecimal amountWithTax;

	@Column(name = "amount_tax", nullable = false)
	@NotNull
    private BigDecimal amountTax;

	@Column(name = "payment_method", nullable = false)
	@NotNull
    private BigDecimal paymentMethod;

	@Column(name = "pdf_link", length = 255)
    private String pdfLink;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commercial_order_id")
    private CommercialOrder commercialOrder;

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
	 * @return the invoiceRef
	 */
	public String getInvoiceRef() {
		return invoiceRef;
	}

	/**
	 * @param invoiceRef the invoiceRef to set
	 */
	public void setInvoiceRef(String invoiceRef) {
		this.invoiceRef = invoiceRef;
	}

	/**
	 * @return the status
	 */
	public OrderInvoiceStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(OrderInvoiceStatusEnum status) {
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
	 * @return the invoiceDate
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	/**
	 * @return the dueDate
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return the accessPointList
	 */
	public Set<String> getAccessPointList() {
		return accessPointList;
	}

	/**
	 * @param accessPointList the accessPointList to set
	 */
	public void setAccessPointList(Set<String> accessPointList) {
		this.accessPointList = accessPointList;
	}

	/**
	 * @return the orderRefList
	 */
	public Set<String> getOrderRefList() {
		return orderRefList;
	}

	/**
	 * @param orderRefList the orderRefList to set
	 */
	public void setOrderRefList(Set<String> orderRefList) {
		this.orderRefList = orderRefList;
	}

	/**
	 * @return the validity
	 */
	public DatePeriod getValidity() {
		return validity;
	}

	/**
	 * @param validity the validity to set
	 */
	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}

	/**
	 * @return the amountWithoutTax
	 */
	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	/**
	 * @param amountWithoutTax the amountWithoutTax to set
	 */
	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	/**
	 * @return the amountWithTax
	 */
	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	/**
	 * @param amountWithTax the amountWithTax to set
	 */
	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	/**
	 * @return the amountTax
	 */
	public BigDecimal getAmountTax() {
		return amountTax;
	}

	/**
	 * @param amountTax the amountTax to set
	 */
	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	/**
	 * @return the paymentMethod
	 */
	public BigDecimal getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(BigDecimal paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * @return the pdfLink
	 */
	public String getPdfLink() {
		return pdfLink;
	}

	/**
	 * @param pdfLink the pdfLink to set
	 */
	public void setPdfLink(String pdfLink) {
		this.pdfLink = pdfLink;
	}

	/**
	 * @return the commercialOrder
	 */
	public CommercialOrder getCommercialOrder() {
		return commercialOrder;
	}

	/**
	 * @param commercialOrder the commercialOrder to set
	 */
	public void setCommercialOrder(CommercialOrder commercialOrder) {
		this.commercialOrder = commercialOrder;
	}
    
    
    
	
	

}
