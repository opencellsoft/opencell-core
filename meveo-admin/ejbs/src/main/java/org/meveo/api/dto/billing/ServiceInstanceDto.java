package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.billing.ServiceInstance;

@XmlType(name = "ServiceInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInstanceDto extends BaseDto {

	private static final long serialVersionUID = -4084004747483067153L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private String status;
	private Date subscriptionDate;
	private Date terminationDate;
	private BigDecimal quantity;
	private String terminationReason;

	public ServiceInstanceDto() {

	}

	public ServiceInstanceDto(ServiceInstance e) {
		code = e.getCode();
		description = e.getDescription();
		status = e.getStatus().name();
		subscriptionDate = e.getSubscriptionDate();
		terminationDate = e.getTerminationDate();
		quantity = e.getQuantity();
		if (e.getSubscriptionTerminationReason() != null) {
			terminationReason = e.getSubscriptionTerminationReason().getCode();
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
				+ terminationDate + ", quantity=" + quantity + ", terminationReason=" + terminationReason + "]";
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

}
