package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.model.billing.AccountStatusEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountDto extends AccountDto {

	private static final long serialVersionUID = -13552444627686818L;

	@XmlElement(required = true)
	private String billingAccount;
	private String billingAccountDescription;
	private String customerAccount;
	private String customerAccountDescription;
	private String customer;
	private String customerDescription;

	private Date subscriptionDate;
	private Date terminationDate;
	private AccountStatusEnum status;
	private Date statusDate;
	private String terminationReason;

	/**
	 * Use for GET / LIST only.
	 */
	private SubscriptionsDto subscriptions = new SubscriptionsDto();

	public UserAccountDto() {
		super();
	}

	@Override
	public String toString() {
		return "UserAccountDto [billingAccount=" + billingAccount + ", subscriptionDate=" + subscriptionDate + ", terminationDate=" + terminationDate + ", status=" + status
				+ ",statusDate="+statusDate+", terminationReason=" + terminationReason + ", subscriptions=" + subscriptions + "]";
	}

	public String getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(String billingAccount) {
		this.billingAccount = billingAccount;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public AccountStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AccountStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}

	public SubscriptionsDto getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(SubscriptionsDto subscriptions) {
		this.subscriptions = subscriptions;
	}

	public String getBillingAccountDescription() {
		return billingAccountDescription;
	}

	public void setBillingAccountDescription(String billingAccountDescription) {
		this.billingAccountDescription = billingAccountDescription;
	}

	public String getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}

	public String getCustomerAccountDescription() {
		return customerAccountDescription;
	}

	public void setCustomerAccountDescription(String customerAccountDescription) {
		this.customerAccountDescription = customerAccountDescription;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerDescription() {
		return customerDescription;
	}

	public void setCustomerDescription(String customerDescription) {
		this.customerDescription = customerDescription;
	}

}
