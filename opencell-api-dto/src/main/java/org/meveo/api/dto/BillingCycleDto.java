package org.meveo.api.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.ReferenceDateEnum;

/**
 * The Class BillingCycleDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@XmlRootElement(name = "BillingCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCycleDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5986901351613880941L;

    /** The billing template name. */
    private String billingTemplateName;

    /** The billing template name EL. */
    private String billingTemplateNameEL;

    /** The invoice date delay. */
    @XmlElement(required = true)
    private Integer invoiceDateDelay;

    /** The due date delay. */
    @XmlElement(required = true)
    private Integer dueDateDelay;

    /**
     * Expression to calculate the due date delay
     */
    @XmlElement(required = false)
    private String dueDateDelayEL;

    /**
     * Expression to calculate the due date delay - for Spark
     */
    @XmlElement(required = false)
    private String dueDateDelayELSpark;

    /** The invoice date production delay. */
    @XmlElement(required = false)
    private Integer invoiceDateProductionDelay;

    /** The transaction date delay. */
    @XmlElement(required = false)
    private Integer transactionDateDelay;

    /** The calendar. */
    @XmlElement(required = true)
    private String calendar;

    /** The invoicing threshold. */
    @XmlElement(required = false)
    private BigDecimal invoicingThreshold;

    /** The invoice type code. */
    private String invoiceTypeCode;

    /**
     * Expression to resolve invoice type code
     */
    private String invoiceTypeEl;

    /**
     * Expression to resolve invoice type code - for Spark
     */
    private String invoiceTypeElSpark;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The billing cycle type. */
    @XmlElement
    private BillingEntityTypeEnum type;

    /** The reference date. */
    private ReferenceDateEnum referenceDate;

    /**
     * Code of the script instance.
     */
    private String scriptInstanceCode;

    /**
     * Instantiates a new billing cycle dto.
     */
    public BillingCycleDto() {

    }

    /**
     * Instantiates a new billing cycle dto.
     *
     * @param billingCycleEntity the billing cycle entity
     * @param customFieldInstances the custom field instances
     */
    public BillingCycleDto(BillingCycle billingCycleEntity, CustomFieldsDto customFieldInstances) {
        super(billingCycleEntity);

        if (billingCycleEntity != null) {
            billingTemplateName = billingCycleEntity.getBillingTemplateName();
            billingTemplateNameEL = billingCycleEntity.getBillingTemplateNameEL();
            invoiceDateDelay = billingCycleEntity.getInvoiceDateDelay();
            dueDateDelay = billingCycleEntity.getDueDateDelay();
            dueDateDelayEL = billingCycleEntity.getDueDateDelayEL();
            dueDateDelayELSpark = billingCycleEntity.getDueDateDelayELSpark();
            invoiceDateProductionDelay = billingCycleEntity.getInvoiceDateProductionDelay();
            transactionDateDelay = billingCycleEntity.getTransactionDateDelay();
            invoicingThreshold = billingCycleEntity.getInvoicingThreshold();
            type = billingCycleEntity.getType();
            invoiceTypeEl = billingCycleEntity.getInvoiceTypeEl();
            invoiceTypeElSpark = billingCycleEntity.getInvoiceTypeElSpark();
            referenceDate = billingCycleEntity.getReferenceDate();
			if (billingCycleEntity.getScriptInstance() != null) {
				scriptInstanceCode = billingCycleEntity.getScriptInstance().getCode();
			}

            if (billingCycleEntity.getInvoiceType() != null) {
                invoiceTypeCode = billingCycleEntity.getInvoiceType().getCode();
            }
            if (billingCycleEntity.getCalendar() != null) {
                calendar = billingCycleEntity.getCalendar().getCode();
            }
            customFields = customFieldInstances;
        }
    }

    /**
     * Gets the billing template name.
     *
     * @return the billing template name
     */
    public String getBillingTemplateName() {
        return billingTemplateName;
    }

    /**
     * Sets the billing template name.
     *
     * @param billingTemplateName the new billing template name
     */
    public void setBillingTemplateName(String billingTemplateName) {
        this.billingTemplateName = billingTemplateName;
    }

    /**
     * Gets the invoice date delay.
     *
     * @return the invoice date delay
     */
    public Integer getInvoiceDateDelay() {
        return invoiceDateDelay;
    }

    /**
     * Sets the invoice date delay.
     *
     * @param invoiceDateDelay the new invoice date delay
     */
    public void setInvoiceDateDelay(Integer invoiceDateDelay) {
        this.invoiceDateDelay = invoiceDateDelay;
    }

    /**
     * Gets the due date delay.
     *
     * @return the due date delay
     */
    public Integer getDueDateDelay() {
        return dueDateDelay;
    }

    /**
     * Sets the due date delay.
     *
     * @param dueDateDelay Invoice due date delay
     */
    public void setDueDateDelay(Integer dueDateDelay) {
        this.dueDateDelay = dueDateDelay;
    }

    /**
     * Gets the invoice date production delay.
     *
     * @return the invoice date production delay
     */
    public Integer getInvoiceDateProductionDelay() {
        return invoiceDateProductionDelay;
    }

    /**
     * Sets the invoice date production delay.
     *
     * @param invoiceDateProductionDelay the new invoice date production delay
     */
    public void setInvoiceDateProductionDelay(Integer invoiceDateProductionDelay) {
        this.invoiceDateProductionDelay = invoiceDateProductionDelay;
    }

    /**
     * Gets the transaction date delay.
     *
     * @return the transaction date delay
     */
    public Integer getTransactionDateDelay() {
        return transactionDateDelay;
    }

    /**
     * Sets the transaction date delay.
     *
     * @param transactionDateDelay the new transaction date delay
     */
    public void setTransactionDateDelay(Integer transactionDateDelay) {
        this.transactionDateDelay = transactionDateDelay;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public String getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets the invoicing threshold.
     *
     * @return the invoicingThreshold
     */
    public BigDecimal getInvoicingThreshold() {
        return invoicingThreshold;
    }

    /**
     * Sets the invoicing threshold.
     *
     * @param invoicingThreshold the invoicingThreshold to set
     */
    public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    /**
     * Gets the invoice type code.
     *
     * @return the invoiceTypeCode
     */
    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    /**
     * Sets the invoice type code.
     *
     * @param invoiceTypeCode the invoiceTypeCode to set
     */
    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    /**
     * @return Expression to resolve invoice type code
     */
    public String getInvoiceTypeEl() {
        return invoiceTypeEl;
    }

    /**
     * @param invoiceTypeEl Expression to resolve invoice type code
     */
    public void setInvoiceTypeEl(String invoiceTypeEl) {
        this.invoiceTypeEl = invoiceTypeEl;
    }

    /**
     * @return Expression to resolve invoice type code for Spark
     */
    public String getInvoiceTypeElSpark() {
        return invoiceTypeElSpark;
    }

    /**
     * @param invoiceTypeElSpark Expression to resolve invoice type code for Spark
     */
    public void setInvoiceTypeElSpark(String invoiceTypeElSpark) {
        this.invoiceTypeElSpark = invoiceTypeElSpark;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BillingCycleDto [code=" + getCode() + ", description=" + getDescription() + ", billingTemplateName=" + billingTemplateName + ", invoiceDateDelay="
                + invoiceDateDelay + ", dueDateDelay=" + dueDateDelay + ", dueDateDelayEL=" + dueDateDelayEL + ", invoiceDateProductionDelay=" + invoiceDateProductionDelay
                + ", transactionDateDelay=" + transactionDateDelay + ", calendar=" + calendar + ", invoicingThreshold=" + invoicingThreshold + ", invoiceTypeCode="
                + invoiceTypeCode + ", customFields=" + customFields + ", referenceDate=" + referenceDate + "]";
    }

    /**
     * Gets the expression to calculate the due date delay
     *
     * @return Expression to calculate the due date delay
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * Sets the expression to calculate the due date delay
     *
     * @param dueDateDelayEL Expression to calculate the due date delay
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    /**
     * Gets the expression to calculate the due date delay - for Spark
     * 
     * @return Expression to calculate the due date delay - for Spark
     */
    public String getDueDateDelayELSpark() {
        return dueDateDelayELSpark;
    }

    /**
     * Sets the expression to calculate the due date delay - for Spark
     * 
     * @param dueDateDelaySpark Expression to calculate the due date delay - for Spark
     */
    public void setDueDateDelaySpark(String dueDateDelaySpark) {
        this.dueDateDelayELSpark = dueDateDelaySpark;
    }

    /**
     * Gets the billing template name EL.
     *
     * @return the billing template name EL
     */
    public String getBillingTemplateNameEL() {
        return billingTemplateNameEL;
    }

    /**
     * Sets the billing template name EL.
     *
     * @param billingTemplateNameEL the new billing template name EL
     */
    public void setBillingTemplateNameEL(String billingTemplateNameEL) {
        this.billingTemplateNameEL = billingTemplateNameEL;
    }

    /**
     * Gets the billing cycle type.
     *
     * @return the billing cycle type
     */
    public BillingEntityTypeEnum getType() {
        return type;
    }

    /**
     * Sets the billing cycle type.
     *
     * @param type the billing cycle type
     */
    public void setType(BillingEntityTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the referenceDate
     *
     * @return the referenceDate
     */
    public ReferenceDateEnum getReferenceDate() {
        return referenceDate;
    }

    /**
     * Sets the referenceDate.
     *
     * @param referenceDate the new referenceDate
     */
    public void setReferenceDate(ReferenceDateEnum referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * Gets the scriptInstanceCode.
     * @return code of script instance
     */
	public String getScriptInstanceCode() {
		return scriptInstanceCode;
	}

	/**
	 * Sets the scriptInstanceCode.
	 * @param scriptInstanceCode code of script instance
	 */
	public void setScriptInstanceCode(String scriptInstanceCode) {
		this.scriptInstanceCode = scriptInstanceCode;
	}

}