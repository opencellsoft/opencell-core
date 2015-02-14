package org.meveo.api.dto.billing;

import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccessesDto;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.mediation.Access;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Subscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionDto extends BaseDto {

	private static final long serialVersionUID = -6021918810749866648L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	@XmlElement(required = true)
	private String userAccount;

	@XmlElement(required = true)
	private String offerTemplate;

	@XmlElement(required = true)
	private Date subscriptionDate;

	private Date terminationDate;
	private String status;
	
	@XmlElement(required = false)
	private CustomFieldsDto customFields = new CustomFieldsDto();

	@XmlElement(required = false)
	private AccessesDto accesses = new AccessesDto();

	@XmlElement(required = false)
	private ServiceInstancesDto services = new ServiceInstancesDto();	

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
			for (Access ac : e.getAccessPoints()) {
				accesses.getAccess().add(new AccessDto(ac));
			}
		}

		if (e.getCustomFields() != null) {
			for (Map.Entry<String, CustomFieldInstance> entry : e.getCustomFields().entrySet()) {
				CustomFieldDto cfDto = new CustomFieldDto();
				cfDto.setCode(entry.getValue().getCode());
				cfDto.setDateValue(entry.getValue().getDateValue());
				cfDto.setDescription(entry.getValue().getDescription());
				cfDto.setDoubleValue(entry.getValue().getDoubleValue());
				cfDto.setLongValue(entry.getValue().getLongValue());
				cfDto.setStringValue(entry.getValue().getStringValue());
				customFields.getCustomField().add(cfDto);
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
				+ terminationDate + ", status=" + status + ", accesses=" + accesses + ", services=" + services
				+ ", customFields=" + customFields + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AccessesDto getAccesses() {
		return accesses;
	}

	public void setAccesses(AccessesDto accesses) {
		this.accesses = accesses;
	}

	public ServiceInstancesDto getServices() {
		return services;
	}

	public void setServices(ServiceInstancesDto services) {
		this.services = services;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

}
