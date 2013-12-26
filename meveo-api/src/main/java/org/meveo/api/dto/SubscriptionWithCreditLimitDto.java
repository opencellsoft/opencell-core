package org.meveo.api.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@XmlRootElement(name = "subscriptionWithCreditLimitDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitDto extends BaseDto {

	private static final long serialVersionUID = -6700315102709912658L;

	private String userId; // unused
	private String organizationId;
	private String offerId;
	private List<ServiceToAddDto> servicesToAdd;
	private List<CreditLimitDto> creditLimits;
	private Date subscriptionDate;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public List<ServiceToAddDto> getServicesToAdd() {
		return servicesToAdd;
	}

	public void setServicesToAdd(List<ServiceToAddDto> servicesToAdd) {
		this.servicesToAdd = servicesToAdd;
	}

	public List<CreditLimitDto> getCreditLimits() {
		return creditLimits;
	}

	public void setCreditLimits(List<CreditLimitDto> creditLimits) {
		this.creditLimits = creditLimits;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

}
