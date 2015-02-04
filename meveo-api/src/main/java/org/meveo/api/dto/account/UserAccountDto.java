package org.meveo.api.dto.account;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.UserAccount;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "UserAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountDto extends AccountDto {

	private static final long serialVersionUID = -13552444627686818L;

	@XmlAttribute(required = true)
	private String billingAccount;

	private Date subscriptionDate;
	private Date terminationDate;
	private String status;

	private List<SubscriptionDto> subscriptions;

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
				+ ", terminationDate=" + terminationDate + ", status=" + status + ", subscriptions=" + subscriptions
				+ "]";
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

	public List<SubscriptionDto> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<SubscriptionDto> subscriptions) {
		this.subscriptions = subscriptions;
	}

}
