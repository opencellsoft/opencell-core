package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.UserAccount;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "UserAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountDto extends AccountDto {

	private static final long serialVersionUID = -13552444627686818L;

	@XmlElement(required = true)
	private String billingAccount;

	private Date subscriptionDate;
	private Date terminationDate;
	private String status;
	private String terminationReason;

	private SubscriptionsDto subscriptions;

	public UserAccountDto() {
		super();
	}

	public UserAccountDto(UserAccount e) {
		super((AccountEntity) e);

		if (e.getBillingAccount() != null) {
			billingAccount = e.getBillingAccount().getCode();
		}

		subscriptionDate = e.getSubscriptionDate();
		terminationDate = e.getTerminationDate();
		try {
			status = e.getStatus().name();
		} catch (NullPointerException ex) {
		}
	}

	@Override
	public String toString() {
		return "UserAccountDto [billingAccount=" + billingAccount + ", subscriptionDate=" + subscriptionDate
				+ ", terminationDate=" + terminationDate + ", status=" + status + ", terminationReason="
				+ terminationReason + ", subscriptions=" + subscriptions + "]";
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

}
