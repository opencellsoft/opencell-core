package org.meveo.api.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingCycleTypeEnum;


/**
 * The Class BillingCycleDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BillingCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCycleDto extends BusinessDto {

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

    /** The due date delay EL. */
    @XmlElement(required = false)
    private String dueDateDelayEL;

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

    /** The custom fields. */
    private CustomFieldsDto customFields;
    
    /** The billing cycle type. */
    @XmlElement
    private BillingCycleTypeEnum type;


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
            invoiceDateProductionDelay = billingCycleEntity.getInvoiceDateProductionDelay();
            transactionDateDelay = billingCycleEntity.getTransactionDateDelay();
            invoicingThreshold = billingCycleEntity.getInvoicingThreshold();
            type = billingCycleEntity.getType();

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
     * @param dueDateDelay the new due date delay
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BillingCycleDto [code=" + getCode() + ", description=" + getDescription() + ", billingTemplateName=" + billingTemplateName + ", invoiceDateDelay="
                + invoiceDateDelay + ", dueDateDelay=" + dueDateDelay + ", dueDateDelayEL=" + dueDateDelayEL + ", invoiceDateProductionDelay=" + invoiceDateProductionDelay
                + ", transactionDateDelay=" + transactionDateDelay + ", calendar=" + calendar + ", invoicingThreshold=" + invoicingThreshold + ", invoiceTypeCode="
                + invoiceTypeCode + ", customFields=" + customFields + "]";
    }

    /**
     * Gets the due date delay EL.
     *
     * @return the due date delay EL
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * Sets the due date delay EL.
     *
     * @param dueDateDelayEL the new due date delay EL
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
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
    public BillingCycleTypeEnum getType() {
        return type;
    }

    /**
     * Sets the billing cycle type.
     *
     * @param type the billing cycle type
     */
    public void setType(BillingCycleTypeEnum type) {
        this.type = type;
    }

}
