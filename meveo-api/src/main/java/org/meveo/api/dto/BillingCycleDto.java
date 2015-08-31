package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.BillingCycle;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BillingCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCycleDto extends BaseDto {

	private static final long serialVersionUID = 5986901351613880941L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private String billingTemplateName;

	@XmlElement(required = true)
	private Integer invoiceDateDelay;

	@XmlElement(required = true)
	private Integer dueDateDelay;

	@XmlElement(required = false)
	private Integer invoiceDateProductionDelay;

	@XmlElement(required = false)
	private Integer transactionDateDelay;

	@XmlElement(required = true)
	private String calendar;

	public BillingCycleDto() {

	}

	public BillingCycleDto(BillingCycle billingCycleEntity) {
		if(billingCycleEntity != null){
			code = billingCycleEntity.getCode();
			description = billingCycleEntity.getDescription();
			billingTemplateName = billingCycleEntity.getBillingTemplateName();
			invoiceDateDelay = billingCycleEntity.getInvoiceDateDelay();
			dueDateDelay = billingCycleEntity.getDueDateDelay();
			invoiceDateProductionDelay = billingCycleEntity.getInvoiceDateProductionDelay();
			transactionDateDelay = billingCycleEntity.getTransactionDateDelay();

			if (billingCycleEntity.getCalendar() != null) {
				calendar = billingCycleEntity.getCalendar().getCode();
			}
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBillingTemplateName() {
		return billingTemplateName;
	}

	public void setBillingTemplateName(String billingTemplateName) {
		this.billingTemplateName = billingTemplateName;
	}

	public Integer getInvoiceDateDelay() {
		return invoiceDateDelay;
	}

	public void setInvoiceDateDelay(Integer invoiceDateDelay) {
		this.invoiceDateDelay = invoiceDateDelay;
	}

	public Integer getDueDateDelay() {
		return dueDateDelay;
	}

	public void setDueDateDelay(Integer dueDateDelay) {
		this.dueDateDelay = dueDateDelay;
	}

	public Integer getInvoiceDateProductionDelay() {
		return invoiceDateProductionDelay;
	}

	public void setInvoiceDateProductionDelay(Integer invoiceDateProductionDelay) {
		this.invoiceDateProductionDelay = invoiceDateProductionDelay;
	}

	public Integer getTransactionDateDelay() {
		return transactionDateDelay;
	}

	public void setTransactionDateDelay(Integer transactionDateDelay) {
		this.transactionDateDelay = transactionDateDelay;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString() {
		return "BillingCycleDto [code=" + code + ", description=" + description + ", billingTemplateName="
				+ billingTemplateName + ", invoiceDateDelay=" + invoiceDateDelay + ", dueDateDelay=" + dueDateDelay
				+ ", invoiceDateProductionDelay=" + invoiceDateProductionDelay+ ", transactionDateDelay=" + transactionDateDelay
				+ ", calendar=" + calendar + "]";
	}

}
