package org.meveo.api.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.BillingCycle;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "BillingCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCycleDto extends BusinessDto {

	private static final long serialVersionUID = 5986901351613880941L;

	private String billingTemplateName;
	
	private String billingTemplateNameEL;

	@XmlElement(required = true)
	private Integer invoiceDateDelay;

	@XmlElement(required = true)
	private Integer dueDateDelay;
	
	@XmlElement(required = false)
	private String dueDateDelayEL;

	@XmlElement(required = false)
	private Integer invoiceDateProductionDelay;

	@XmlElement(required = false)
	private Integer transactionDateDelay;

	@XmlElement(required = true)
	private String calendar;
	
	@XmlElement(required = false)
	private BigDecimal invoicingThreshold;

	private String invoiceTypeCode;
	
	private CustomFieldsDto customFields;
	
	public BillingCycleDto() {

	}

	public BillingCycleDto(BillingCycle billingCycleEntity,CustomFieldsDto customFieldInstances) {
		super(billingCycleEntity);
		
		if(billingCycleEntity != null){			
			billingTemplateName = billingCycleEntity.getBillingTemplateName();
			billingTemplateNameEL = billingCycleEntity.getBillingTemplateNameEL();
			invoiceDateDelay = billingCycleEntity.getInvoiceDateDelay();
			dueDateDelay = billingCycleEntity.getDueDateDelay();
			dueDateDelayEL = billingCycleEntity.getDueDateDelayEL();
			invoiceDateProductionDelay = billingCycleEntity.getInvoiceDateProductionDelay();
			transactionDateDelay = billingCycleEntity.getTransactionDateDelay();
			invoicingThreshold  = billingCycleEntity.getInvoicingThreshold(); 
			
			if (billingCycleEntity.getInvoiceType() != null) {
				invoiceTypeCode = billingCycleEntity.getInvoiceType().getCode();
			}
			if (billingCycleEntity.getCalendar() != null) {
				calendar = billingCycleEntity.getCalendar().getCode();
			}
			customFields = customFieldInstances;
		}
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
	
	

	/**
	 * @return the invoicingThreshold
	 */
	public BigDecimal getInvoicingThreshold() {
		return invoicingThreshold;
	}

	/**
	 * @param invoicingThreshold the invoicingThreshold to set
	 */
	public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
		this.invoicingThreshold = invoicingThreshold;
	}

	/**
	 * @return the invoiceTypeCode
	 */
	public String getInvoiceTypeCode() {
		return invoiceTypeCode;
	}

	/**
	 * @param invoiceTypeCode the invoiceTypeCode to set
	 */
	public void setInvoiceTypeCode(String invoiceTypeCode) {
		this.invoiceTypeCode = invoiceTypeCode;
	}
	
	

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	@Override
	public String toString() {
		return "BillingCycleDto [code=" + getCode() + ", description=" + getDescription() + ", billingTemplateName=" + billingTemplateName + ", invoiceDateDelay=" + invoiceDateDelay
				+ ", dueDateDelay=" + dueDateDelay + ", dueDateDelayEL=" + dueDateDelayEL + ", invoiceDateProductionDelay=" + invoiceDateProductionDelay
				+ ", transactionDateDelay=" + transactionDateDelay + ", calendar=" + calendar + ", invoicingThreshold=" + invoicingThreshold + ", invoiceTypeCode="
				+ invoiceTypeCode + ", customFields=" + customFields + "]";
	}

	public String getDueDateDelayEL() {
		return dueDateDelayEL;
	}

	public void setDueDateDelayEL(String dueDateDelayEL) {
		this.dueDateDelayEL = dueDateDelayEL;
	}

    public String getBillingTemplateNameEL() {
        return billingTemplateNameEL;
    }

    public void setBillingTemplateNameEL(String billingTemplateNameEL) {
        this.billingTemplateNameEL = billingTemplateNameEL;
    }



}
