package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;

@Entity
@Table(name = "AR_REVENUE_SCHEDULE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_REVENUE_SCHEDULE_SEQ")
@NamedQueries({
	@NamedQuery(name = "RevenueSchedule.deleteForChargeInstance", 
					query = "DELETE FROM RevenueSchedule o WHERE o.chargeInstance =:chargeInstance"
							+ " AND o.provider=:provider")
})
public class RevenueSchedule extends BaseEntity {

	private static final long serialVersionUID = 5431403800062032929L;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED", nullable = false, updatable = false)
	private Date created;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR_ID", updatable = false)
	private User creator;
	
	@Column(name = "REVENUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date revenueDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHARGE_INSTANCE_ID")
	private ChargeInstance chargeInstance;

	@Column(name = "CHARGE_CODE", length = 60)
    @Size(max = 60)
	private String chargeCode;
	
	@Column(name = "SUBSCRIPTION_CODE", length = 60)
    @Size(max = 60)
	private String subscriptionCode;
	
	@Column(name = "USER_ACCOUNT_CODE", length = 60)
    @Size(max = 60)
	private String userAccountCode;
	
	@Column(name = "BILLING_ACCOUNT_CODE", length = 60)
    @Size(max = 60)
	private String billingAccountCode;
	
	@Column(name = "CUSTOMER_ACCOUNT_CODE", length = 60)
    @Size(max = 60)
	private String customerAccountCode;
	
	@Column(name = "CUSTOMER_CODE", length = 60)
    @Size(max = 60)
	private String customerCode;

	@Column(name = "SERVICE_CODE", length = 100)
    @Size(max = 100)
	private String serviceCode;

	@Column(name = "OFFER_CODE", length = 100)
    @Size(max = 100)
	private String offerCode;
	
	@Column(name = "RECOGNIZED_AMOUNT", precision = 23, scale = 12)
	private BigDecimal recognizedAmount = BigDecimal.ZERO;

	@Column(name = "INVOICED_AMOUNT", precision = 23, scale = 12)
	private BigDecimal invoicedAmount = BigDecimal.ZERO;
	
	@Column(name = "ACCRUED_AMOUNT", precision = 23, scale = 12)
	private BigDecimal accruedAmount = BigDecimal.ZERO;

	@Column(name = "DEFFERED_AMOUNT", precision = 23, scale = 12)
	private BigDecimal defferedAmount = BigDecimal.ZERO;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getRevenueDate() {
		return revenueDate;
	}

	public void setRevenueDate(Date revenueDate) {
		this.revenueDate = revenueDate;
	}

	public ChargeInstance getChargeInstance() {
		return chargeInstance;
	}

	public void setChargeInstance(ChargeInstance chargeInstance) {
		this.chargeInstance = chargeInstance;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getUserAccountCode() {
		return userAccountCode;
	}

	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}

	public String getBillingAccountCode() {
		return billingAccountCode;
	}

	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}

	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public BigDecimal getRecognizedAmount() {
		return recognizedAmount;
	}

	public void setRecognizedAmount(BigDecimal recognizedAmount) {
		this.recognizedAmount = recognizedAmount;
	}

	public BigDecimal getInvoicedAmount() {
		return invoicedAmount;
	}

	public void setInvoicedAmount(BigDecimal invoicedAmount) {
		this.invoicedAmount = invoicedAmount;
	}

	public BigDecimal getAccruedAmount() {
		return accruedAmount;
	}

	public void setAccruedAmount(BigDecimal accruedAmount) {
		this.accruedAmount = accruedAmount;
	}

	public BigDecimal getDefferedAmount() {
		return defferedAmount;
	}

	public void setDefferedAmount(BigDecimal defferedAmount) {
		this.defferedAmount = defferedAmount;
	}

}
