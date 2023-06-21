package org.meveo.model.cpq;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.quote.QuoteStatusEnum;


@Entity
@ObservableEntity
@WorkflowedEntity
@Table(name = "cpq_quote", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_seq")})
@NamedQueries({
    @NamedQuery(name = "CpqQuote.getQuoteIdsUsingCharge", query = "select qp.quote.id from QuoteProduct qp where qp.quote.status not in('CANCELLED','ACCEPTED','REJECTED') and qp.productVersion.product in (select pc.product from ProductChargeTemplateMapping pc where pc.chargeTemplate.id in (:chargeIds))")
    })

public class CpqQuote extends BusinessEntity implements IBillableEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6228457362529323943L;

    public CpqQuote() {
	}
    
	public CpqQuote(CpqQuote copy) {
		super.code = copy.code;
		super.description = copy.description;
		this.seller = copy.seller;
		this.applicantAccount = copy.applicantAccount; 
		//to move to quote version
		this.sendDate = copy.sendDate;
		this.deliveryDate = copy.deliveryDate;
		this.quoteLotDuration = copy.quoteLotDuration;
		this.opportunityRef = copy.opportunityRef;
		this.customerRef = copy.customerRef;
		this.customerName = copy.customerName;
		this.contactName = copy.contactName;
		this.registerNumber = copy.registerNumber;
		this.salesPersonName = copy.salesPersonName;
		this.billableAccount = copy.billableAccount;
		//to move to quote version
		this.validity = copy.validity;
		this.orderInvoiceType = copy.orderInvoiceType;
		this.userAccount=copy.userAccount;
	}
	/**
	 * seller
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	/**
	 * Applicant Account
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_account_id", nullable = false)
	@NotNull
	private BillingAccount applicantAccount;
	
	
	

    /**
     * Order processing status as defined by the workflow.
     */
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private String status = QuoteStatusEnum.IN_PROGRESS.toString();

	/**
	 * Order processing previeuw status as defined by the workflow.
	 */
	@Column(name = "previous_status", length = 20, nullable = false)
	@NotNull
	private String previousStatus = QuoteStatusEnum.IN_PROGRESS.toString();

	/**
	 * statusDate
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date", nullable = false)
	private Date statusDate;

	/**
	 * sendDate
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "send_date")
	private Date sendDate;

	/**
	 * sendDate
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "quote_date")
	private Date quoteDate;
	/**
	 * prestationDateBegin
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "prestation_date_begin")
	private Date deliveryDate;
	/**
	 * prestationDuration
	 */
	@Column(name = "prestation_duration")
	private Integer quoteLotDuration;
	/**
	 * opportunityRef
	 */
	@Column(name = "opportunity_ref", length = 50)
	@Size(max = 50)
	private String opportunityRef;
	/**
	 * customerRef
	 */
	@Column(name = "customer_ref", length = 50)
	@Size(max = 50)
	private String customerRef;
	/**
	 * customerName
	 */
	@Column(name = "customer_name", length = 52)
	@Size(max = 52)
	private String customerName;
	/**
	 * contactName
	 */
	@Column(name = "contact_name", length = 52)
	@Size(max = 52)
	private String contactName;
	/**
	 * registerNumber
	 */
	@Column(name = "register_number", length = 50)
	@Size(max = 50)
	private String registerNumber;
	/**
	 * salesPersonName
	 */
	@Column(name = "sales_person_name", length = 52)
	@Size(max = 52)
	private String salesPersonName;

	/**
	 * billing account invoice code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billable_account_id", nullable = false)
	@NotNull
	private BillingAccount billableAccount;
	

    /**
     * Quote validity dates
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity = new DatePeriod();
    
	
    
    @Column(name = "quote_number", length = 50)
    private String quoteNumber;
    

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_type_id", nullable = false)
	@NotNull
	private InvoiceType orderInvoiceType;
	
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "user_account_id") 
	private UserAccount userAccount;
	 
	
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
    
    /**
     * Billing run
     */
    @Transient
    private BillingRun billingRun;
	    
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
	 * @return the applicantAccount
	 */
	public BillingAccount getApplicantAccount() {
		return applicantAccount;
	}
	/**
	 * @param applicantAccount the applicantAccount to set
	 */
	public void setApplicantAccount(BillingAccount applicantAccount) {
		this.applicantAccount = applicantAccount;
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
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}
	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	/**
	 * @return the opportunityRef
	 */
	public String getOpportunityRef() {
		return opportunityRef;
	}
	/**
	 * @param opportunityRef the opportunityRef to set
	 */
	public void setOpportunityRef(String opportunityRef) {
		this.opportunityRef = opportunityRef;
	}
	/**
	 * @return the customerRef
	 */
	public String getCustomerRef() {
		return customerRef;
	}
	/**
	 * @param customerRef the customerRef to set
	 */
	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}
	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}
	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	/**
	 * @return the registerNumber
	 */
	public String getRegisterNumber() {
		return registerNumber;
	}
	/**
	 * @param registerNumber the registerNumber to set
	 */
	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
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
	 * @return the billableAccount
	 */
	public BillingAccount getBillableAccount() {
		return billableAccount;
	}
	/**
	 * @param billableAccount the billableAccount to set
	 */
	public void setBillableAccount(BillingAccount billableAccount) {
		this.billableAccount = billableAccount;
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
	 * @return the quoteLotDuration
	 */
	public Integer getQuoteLotDuration() {
		return quoteLotDuration;
	}
	/**
	 * @param quoteLotDuration the quoteLotDuration to set
	 */
	public void setQuoteLotDuration(Integer quoteLotDuration) {
		this.quoteLotDuration = quoteLotDuration;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		if(!status.equals(this.status))
			this.previousStatus = this.status;
		this.status = status;
	} 

	/**
	 * @return the quoteNumber
	 */
	public String getQuoteNumber() {
		return quoteNumber;
	}

	/**
	 * @param quoteNumber the quoteNumber to set
	 */
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
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

	public Date getQuoteDate() {
		return quoteDate == null ? new Date() : quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	} 

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	@Override
	public BillingRun getBillingRun() {
		return billingRun;
	}

	@Override
	public void setBillingRun(BillingRun billingRun) {
       this.billingRun=billingRun;
 }

	@Override
	public void setMinRatedTransactions(List<RatedTransaction> ratedTransactions) {
        this.minRatedTransactions=ratedTransactions;
 }

	@Override
	public List<RatedTransaction> getMinRatedTransactions() {
		return minRatedTransactions;
	}

	@Override
	public BigDecimal getTotalInvoicingAmountWithoutTax() {
		return totalInvoicingAmountWithoutTax;
	}

	@Override
	public void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax) {
      this.totalInvoicingAmountWithoutTax=totalInvoicingAmountWithoutTax;
 }

	@Override
	public BigDecimal getTotalInvoicingAmountWithTax() {
		return totalInvoicingAmountWithTax;
	}

	@Override
	public void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax) {
		this.totalInvoicingAmountWithTax=totalInvoicingAmountWithTax;
	}

	@Override
	public BigDecimal getTotalInvoicingAmountTax() {
		return totalInvoicingAmountTax;
	}

	@Override
	public void setTotalInvoicingAmountTax(BigDecimal totalInvoicingAmountTax) {
		this.totalInvoicingAmountTax=totalInvoicingAmountTax;
	}

	@Override
	public BillingCycle getBillingCycle() {
		return null;
	}

	@Override
	public List<InvoiceLine> getMinInvoiceLines() {
		return minInvoiceLines;
	}
	
	

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public void setMinInvoiceLines(List<InvoiceLine> invoiceLines) {
		this.minInvoiceLines=invoiceLines;
	}

	public Date getValidationDate() {
		if(QuoteStatusEnum.ACCEPTED.toString().equals(status)) {
			return statusDate;
		}
		return null;
	}
	
}
