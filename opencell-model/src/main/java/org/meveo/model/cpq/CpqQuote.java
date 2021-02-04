package org.meveo.model.cpq;

import static javax.persistence.FetchType.LAZY;

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.quote.QuoteStatusEnum;


@Entity
@WorkflowedEntity
@ObservableEntity
@Table(name = "cpq_quote", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_seq")})
public class CpqQuote extends BusinessEntity {

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
		this.contract = copy.contract;
		this.sendDate = copy.sendDate;
		this.quoteLotDateBegin = copy.quoteLotDateBegin;
		this.quoteLotDuration = copy.quoteLotDuration;
		this.opportunityRef = copy.opportunityRef;
		this.customerRef = copy.customerRef;
		this.customerName = copy.customerName;
		this.contactName = copy.contactName;
		this.registerNumber = copy.registerNumber;
		this.salesPersonName = copy.salesPersonName;
		this.billableAccount = copy.billableAccount;
		this.validity = copy.validity;
		this.pdfFilename=copy.pdfFilename;
		this.xmlFilename=copy.xmlFilename;
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
	 * contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;
	

    /**
     * Order processing status as defined by the workflow.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private QuoteStatusEnum status = QuoteStatusEnum.IN_PROGRESS;
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
	private Date quoteLotDateBegin;
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
    
	/**
	 * discountPlan attached to this quote
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    @Column(name = "quote_number", length = 50)
    private String quoteNumber;
    

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_type_id", nullable = false)
	@NotNull
	private InvoiceType orderInvoiceType;
	
	   /**
	  * XML file name
	  */
	 @Column(name = "xml_filename", length = 255)
	 @Size(max = 255)
	 private String xmlFilename;
	 
	 /**
	  * PDF file name
	  */
	@Column(name = "pdf_filename", length = 255)
	@Size(max = 255)
	private String pdfFilename;
	    
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
	 * @return the quoteLotDateBegin
	 */
	public Date getQuoteLotDateBegin() {
		return quoteLotDateBegin;
	}
	/**
	 * @param quoteLotDateBegin the quoteLotDateBegin to set
	 */
	public void setQuoteLotDateBegin(Date quoteLotDateBegin) {
		this.quoteLotDateBegin = quoteLotDateBegin;
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
	public QuoteStatusEnum getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(QuoteStatusEnum status) {
		this.status = status;
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
/**
	 * @return the xmlFilename
	 */
	public String getXmlFilename() {
		return xmlFilename;
	}

	/**
	 * @param xmlFilename the xmlFilename to set
	 */
	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	/**
	 * @return the pdfFilename
	 */
	public String getPdfFilename() {
		return pdfFilename;
	}

	/**
	 * @param pdfFilename the pdfFilename to set
	 */
	public void setPdfFilename(String pdfFilename) {
		this.pdfFilename = pdfFilename;
	}	
}
