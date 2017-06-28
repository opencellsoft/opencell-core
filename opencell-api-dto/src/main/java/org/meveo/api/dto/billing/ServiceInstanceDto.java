package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UsageChargeInstance;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInstanceDto extends BaseDto {

	private static final long serialVersionUID = -4084004747483067153L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	private InstanceStatusEnum status;

	private Date statusDate;

	private Date subscriptionDate;

	private Date terminationDate;

	private BigDecimal quantity;

	private String terminationReason;

	private Date endAgreementDate;

	private CustomFieldsDto customFields = new CustomFieldsDto();

	@XmlElementWrapper(name = "recurringChargeInstances")
	@XmlElement(name = "recurringChargeInstance")
	private List<ChargeInstanceDto> recurringChargeInstances;

	@XmlElementWrapper(name = "subscriptionChargeInstances")
	@XmlElement(name = "subscriptionChargeInstance")
	private List<ChargeInstanceDto> subscriptionChargeInstances;

	@XmlElementWrapper(name = "terminationChargeInstances")
	@XmlElement(name = "terminationChargeInstance")
	private List<ChargeInstanceDto> terminationChargeInstances;

	@XmlElementWrapper(name = "usageChargeInstances")
	@XmlElement(name = "usageChargeInstance")
	private List<ChargeInstanceDto> usageChargeInstances;
	
	private String orderNumber;

	public ServiceInstanceDto() {

	}

	public ServiceInstanceDto(ServiceInstance e, CustomFieldsDto customFieldInstances) {
		code = e.getCode();
		description = e.getDescription();
		status = e.getStatus();
		statusDate = e.getStatusDate();
		subscriptionDate = e.getSubscriptionDate();
		terminationDate = e.getTerminationDate();
		quantity = e.getQuantity();
		orderNumber = e.getOrderNumber();
		if (e.getSubscriptionTerminationReason() != null) {
			terminationReason = e.getSubscriptionTerminationReason().getCode();
		}
		endAgreementDate = e.getEndAgreementDate();

		if (e.getRecurringChargeInstances() != null) {
			recurringChargeInstances = new ArrayList<ChargeInstanceDto>();

			for (RecurringChargeInstance ci : e.getRecurringChargeInstances()) {
				recurringChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(), ci
						.getSeller().getCode(), ci.getUserAccount().getCode()));
			}
		}

		if (e.getSubscriptionChargeInstances() != null) {
			subscriptionChargeInstances = new ArrayList<ChargeInstanceDto>();

			for (OneShotChargeInstance ci : e.getSubscriptionChargeInstances()) {
				subscriptionChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(), ci
						.getSeller().getCode(), ci.getUserAccount().getCode()));
			}
		}

		if (e.getTerminationChargeInstances() != null) {
			terminationChargeInstances = new ArrayList<ChargeInstanceDto>();

			for (OneShotChargeInstance ci : e.getTerminationChargeInstances()) {
				terminationChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(), ci
						.getSeller().getCode(), ci.getUserAccount().getCode()));
			}
		}

		if (e.getUsageChargeInstances() != null) {
			usageChargeInstances = new ArrayList<ChargeInstanceDto>();

			for (UsageChargeInstance ci : e.getUsageChargeInstances()) {
				usageChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(), ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(), ci
						.getSeller().getCode(), ci.getUserAccount().getCode()));
			}
		}

		customFields = customFieldInstances;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public InstanceStatusEnum getStatus() {
		return status;
	}

	public void setStatus(InstanceStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
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

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "ServiceInstanceDto [code=" + code + ", description=" + description + ", status=" + status + ", subscriptionDate=" + subscriptionDate + ", terminationDate="
				+ terminationDate + ", quantity=" + quantity + ", terminationReason=" + terminationReason + ", orderNumber="+orderNumber+"]";
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEndAgreementDate() {
		return endAgreementDate;
	}

	public void setEndAgreementDate(Date endAgreementDate) {
		this.endAgreementDate = endAgreementDate;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
}