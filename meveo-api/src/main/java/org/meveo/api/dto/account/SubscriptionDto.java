package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Subscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionDto extends BaseDto {

	private static final long serialVersionUID = -6021918810749866648L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String userAccount;

	@XmlAttribute(required = true)
	private String offerTemplate;

	private Date subscriptionDate;
	private Date terminationDate;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(String offerTemplate) {
		this.offerTemplate = offerTemplate;
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

	@Override
	public String toString() {
		return "SubscriptionDto [code=" + code + ", userAccount=" + userAccount + ", offerTemplate=" + offerTemplate
				+ ", subscriptionDate=" + subscriptionDate + ", terminationDate=" + terminationDate + "]";
	}

}
