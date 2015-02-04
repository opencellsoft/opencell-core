package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Subscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionDto extends BaseDto {

	private static final long serialVersionUID = -6021918810749866648L;

	@XmlAttribute(required = true)
	private String code;

	private String description;

	@XmlAttribute(required = true)
	private String userAccount;

	@XmlAttribute(required = true)
	private String offerTemplate;

	private Date subscriptionDate;
	private Date terminationDate;

	private List<AccessDto> accesses;

	private List<ServiceInstanceDto> services;

	public SubscriptionDto() {

	}

	public SubscriptionDto(Subscription e) {
		code = e.getCode();
		description = e.getDescription();

		if (e.getUserAccount() != null) {
			userAccount = e.getUserAccount().getCode();
		}

		if (e.getOffer() != null) {
			offerTemplate = e.getOffer().getCode();
		}

		subscriptionDate = e.getSubscriptionDate();
		terminationDate = e.getTerminationDate();

		if (e.getAccessPoints() != null) {
			accesses = new ArrayList<AccessDto>();
			for (Access ac : e.getAccessPoints()) {
				accesses.add(new AccessDto(ac));
			}
		}
	}

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
		return "SubscriptionDto [code=" + code + ", description=" + description + ", userAccount=" + userAccount
				+ ", offerTemplate=" + offerTemplate + ", subscriptionDate=" + subscriptionDate + ", terminationDate="
				+ terminationDate + ", accesses=" + accesses + ", services=" + services + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AccessDto> getAccesses() {
		return accesses;
	}

	public void setAccesses(List<AccessDto> accesses) {
		this.accesses = accesses;
	}

	public List<ServiceInstanceDto> getServices() {
		return services;
	}

	public void setServices(List<ServiceInstanceDto> services) {
		this.services = services;
	}

}
