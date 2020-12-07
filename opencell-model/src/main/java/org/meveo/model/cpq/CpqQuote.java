package org.meveo.model.cpq;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.contract.Contract;


@Entity
@Table(name = "cpq_quote", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_seq")})
public class CpqQuote extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6228457362529323943L;

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
	private BillingAccount applicantAccount;
	
	/**
	 * contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;
	
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
	@JoinColumn(name = "billable_account_id")
	private BillingAccount billableAccount;
	

    /**
     * Quote validity dates
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity = new DatePeriod();
    
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
}
