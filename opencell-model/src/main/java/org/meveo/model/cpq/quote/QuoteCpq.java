package org.meveo.model.cpq.quote;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.VersionStatusEnum;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@Embeddable
public class QuoteCpq {

	/**
	 * seller
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	/**
	 * billingAccount
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id", nullable = false)
	private BillingAccount billingAccount;

	/**
	 * contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;

	/**
	 * statusVersion
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status_version", nullable = false)
	private VersionStatusEnum statusVersion;

	/**
	 * statusDate
	 */
	@Column(name = "status_date", nullable = false)
	private Date statusDate;

	/**
	 * sendDate
	 */
	@Column(name = "send_date")
	private Date sendDate;

	/**
	 * prestationDateBegin
	 */
	@Column(name = "prestation_date_begin")
	private Date prestationDateBegin;

	/**
	 * prestationDuration
	 */
	@Column(name = "prestation_duration")
	private int prestationDuration;

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
	 * seller associated to the quote
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
	 * @return the statusVersion
	 */
	public VersionStatusEnum getStatusVersion() {
		return statusVersion;
	}

	/**
	 * @param statusVersion the statusVersion to set
	 */
	public void setStatusVersion(VersionStatusEnum statusVersion) {
		this.statusVersion = statusVersion;
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
	 * @return the prestationDateBegin
	 */
	public Date getPrestationDateBegin() {
		return prestationDateBegin;
	}

	/**
	 * @param prestationDateBegin the prestationDateBegin to set
	 */
	public void setPrestationDateBegin(Date prestationDateBegin) {
		this.prestationDateBegin = prestationDateBegin;
	}

	/**
	 * @return the prestationDuration
	 */
	public int getPrestationDuration() {
		return prestationDuration;
	}

	/**
	 * @param prestationDuration the prestationDuration to set
	 */
	public void setPrestationDuration(int prestationDuration) {
		this.prestationDuration = prestationDuration;
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
	 * legal information of client, update automatically from opencell or other source
	 * followed by the process of operation's customer
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
	 * sales person name, update automatically from opencell or other source
	 * followed by the process of operation's customer
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

	@Override
	public int hashCode() {
		return Objects.hash(billingAccount, contactName, contract, customerName, customerRef, opportunityRef,
				prestationDateBegin, prestationDuration, registerNumber, salesPersonName, seller, sendDate, statusDate,
				statusVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuoteCpq other = (QuoteCpq) obj;
		return Objects.equals(billingAccount, other.billingAccount) && Objects.equals(contactName, other.contactName)
				&& Objects.equals(contract, other.contract) && Objects.equals(customerName, other.customerName)
				&& Objects.equals(customerRef, other.customerRef)
				&& Objects.equals(opportunityRef, other.opportunityRef)
				&& Objects.equals(prestationDateBegin, other.prestationDateBegin)
				&& prestationDuration == other.prestationDuration
				&& Objects.equals(registerNumber, other.registerNumber)
				&& Objects.equals(salesPersonName, other.salesPersonName) && Objects.equals(seller, other.seller)
				&& Objects.equals(sendDate, other.sendDate) && Objects.equals(statusDate, other.statusDate)
				&& statusVersion == other.statusVersion;
	}
}
