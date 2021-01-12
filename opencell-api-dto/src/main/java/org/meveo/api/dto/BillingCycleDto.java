/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.ReferenceDateEnum;
import org.meveo.model.billing.ThresholdOptionsEnum;

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

    /**
     * The invoice template name. <br/>
     * Deprecated in v 10.0.0. Use billingTemplateNameEL instead
     */
    @Deprecated
    private String billingTemplateName;

    /** Expression to calculate Invoice template name */
    @Size(max = 2000)
    private String billingTemplateNameEL;

    /**
     * Invoice date delay from a one shot charge date for immediate invoicing <br/>
     * Deprecated in v 10.0.0. Use invoiceDateDelayEL instead
     */
    @XmlElement()
    @Deprecated
    private Integer invoiceDateDelay;

    /**
     * Expression to calculate the invoice date delay from a one shot charge date for immediate invoicing
     */
    @XmlElement(required = true)
    @Size(max = 2000)
    private String invoiceDateDelayEL;

    /** Invoice due date delay from the invoicing date */
    @Deprecated
    @XmlElement()
    private Integer dueDateDelay;

    /**
     * Expression to calculate Invoice due date delay from the invoicing date
     */
    @XmlElement()
    @Size(max = 2000)
    private String dueDateDelayEL;

    /**
     * Expression to calculate Invoice due date delay from the invoicing date - for Spark
     */
    @XmlElement()
    private String dueDateDelayELSpark;

    /**
     * A delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate = BillingRun.processDate + BillingCycle.invoiceDateProductionDelay
     */
    @Deprecated
    @XmlElement()
    private Integer invoiceDateProductionDelay;

    /**
     * Expression to calculate a delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate = BillingRun.processDate +
     * BillingCycle.invoiceDateProductionDelay (resolved from EL).
     */
    @XmlElement()
    @Size(max = 2000)
    private String invoiceDateProductionDelayEL;

    /**
     * A delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate value.
     * BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL). <br/>
     * Deprecated in v 10.0.0. Use lastTransactionDateDelayEL instead
     */
    @Deprecated
    @XmlElement()
    private Integer transactionDateDelay;

    /**
     * Expression to calculate the maximum date up to which to include rated transactions in the invoice. BillingRun.lastTransactionDate = BillingCycle.lastTransactionDate
     * (resolved from EL)
     */
    @XmlElement()
    @Size(max = 2000)
    private String lastTransactionDateDelayEL;

    /**
     * Expression to calculate the maximum date up to which to include rated transactions in the invoice. BillingRun.lastTransactionDate = BillingCycle.lastTransactionDate
     * (resolved from EL)
     */
    @Size(max = 2000)
    private String lastTransactionDateEL;

    /** The calendar. */
    @XmlElement(required = true)
    private String calendar;

    /** The invoicing threshold. */
    @XmlElement()
    private BigDecimal invoicingThreshold;

    /** The split Per Payment Method option. */
    @XmlElement()
    private Boolean splitPerPaymentMethod;

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

    /**
     * What reference date to use when calculating the next invoicing date with an invoice calendar as in: BillingCycle.calendar.nextCalendarDate(referenceDate)
     */
    private ReferenceDateEnum referenceDate;

    /**
     * Code of the script instance.
     */
    private String scriptInstanceCode;

    /**
     * The option on how to check the threshold.
     */
    @XmlElement
    private ThresholdOptionsEnum checkThreshold;
    
    /**
     * 
     * check the threshold per entity/invoice.
     */
    @XmlElement
    private Boolean thresholdPerEntity;
    
    @XmlElement
    private String billingRunValidationScriptCode;

    public String getLastTransactionDateDelayEL() {
		return lastTransactionDateDelayEL;
	}

	public void setLastTransactionDateDelayEL(String lastTransactionDateDelayEL) {
		this.lastTransactionDateDelayEL = lastTransactionDateDelayEL;
	}

	public String getBillingRunValidationScriptCode() {
		return billingRunValidationScriptCode;
	}

	public void setBillingRunValidationScriptCode(String billingRunValidationScriptCode) {
		this.billingRunValidationScriptCode = billingRunValidationScriptCode;
	}

	public Boolean getThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setDueDateDelayELSpark(String dueDateDelayELSpark) {
		this.dueDateDelayELSpark = dueDateDelayELSpark;
	}

    private List<LanguageDescriptionDto> languageDescriptions;

    public Boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(Boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

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
            id = billingCycleEntity.getId();
            billingTemplateNameEL = billingCycleEntity.getBillingTemplateNameEL();
            invoiceDateDelayEL = billingCycleEntity.getInvoiceDateDelayEL();
            dueDateDelayEL = billingCycleEntity.getDueDateDelayEL();
            dueDateDelayELSpark = billingCycleEntity.getDueDateDelayELSpark();
            invoiceDateProductionDelayEL = billingCycleEntity.getInvoiceDateProductionDelayEL();
            lastTransactionDateDelayEL = billingCycleEntity.getLastTransactionDateDelayEL();
            lastTransactionDateEL = billingCycleEntity.getLastTransactionDateEL();
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
            splitPerPaymentMethod = billingCycleEntity.isSplitPerPaymentMethod();
            customFields = customFieldInstances;
            checkThreshold = billingCycleEntity.getCheckThreshold();
            if(billingCycleEntity.getCheckThreshold()!=null) {
            	thresholdPerEntity=billingCycleEntity.isThresholdPerEntity();
            }
            languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(billingCycleEntity.getDescriptionI18n());
            collectionDateDelayEl = billingCycleEntity.getCollectionDateDelayEl();
            computeDatesAtValidation = billingCycleEntity.getComputeDatesAtValidation() == null ? null : billingCycleEntity.getComputeDatesAtValidation();
            billingRunValidationScriptCode=billingCycleEntity.getBillingRunValidationScript()!=null?billingCycleEntity.getBillingRunValidationScript().getCode():null;
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
     * @return Invoice date delay from a one shot charge date for immediate invoicing
     */
    public Integer getInvoiceDateDelay() {
        return invoiceDateDelay;
    }

    /**
     * Sets the invoice date delay.
     *
     * @param invoiceDateDelay Invoice date delay from a one shot charge date for immediate invoicing
     */
    public void setInvoiceDateDelay(Integer invoiceDateDelay) {
        this.invoiceDateDelay = invoiceDateDelay;
    }

    /**
     * @return Expression to calculate the invoice date delay from a one shot charge date for immediate invoicing
     */
    public String getInvoiceDateDelayEL() {
        return invoiceDateDelayEL;
    }

    /**
     * @param invoiceDateDelayEL Expression to calculate the invoice date delay from a one shot charge date for immediate invoicing
     */
    public void setInvoiceDateDelayEL(String invoiceDateDelayEL) {
        this.invoiceDateDelayEL = invoiceDateDelayEL;
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
     * @return A delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate = BillingRun.processDate + BillingCycle.invoiceDateProductionDelay
     */
    public Integer getInvoiceDateProductionDelay() {
        return invoiceDateProductionDelay;
    }

    /**
     * @param invoiceDateProductionDelay A delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate = BillingRun.processDate +
     *        BillingCycle.invoiceDateProductionDelay
     */
    public void setInvoiceDateProductionDelay(Integer invoiceDateProductionDelay) {
        this.invoiceDateProductionDelay = invoiceDateProductionDelay;
    }

    /**
     * @return Expression to calculate a delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate = BillingRun.processDate +
     *         BillingCycle.invoiceDateProductionDelay (resolved from EL).
     */
    public String getInvoiceDateProductionDelayEL() {
        return invoiceDateProductionDelayEL;
    }

    /**
     * @param invoiceDateProductionDelayEL Expression to calculate a delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate =
     *        BillingRun.processDate + BillingCycle.invoiceDateProductionDelay (resolved from EL).
     */
    public void setInvoiceDateProductionDelayEL(String invoiceDateProductionDelayEL) {
        this.invoiceDateProductionDelayEL = invoiceDateProductionDelayEL;
    }

    /**
     * @return A delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate value.
     *         BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public Integer getLastTransactionDateDelay() {
        return transactionDateDelay;
    }

    /**
     * @param transactionDateDelay A delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate
     *        value. BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public void setLastTransactionDateDelay(Integer transactionDateDelay) {
        this.transactionDateDelay = transactionDateDelay;
    }

    /**
     * @return A delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate value.
     *         BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public String getTransactionDateDelayEL() {
        return lastTransactionDateDelayEL;
    }

    /**
     * @param transactionDateDelayEL A delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate
     *        value. BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public void setTransactionDateDelayEL(String transactionDateDelayEL) {
        this.lastTransactionDateDelayEL = transactionDateDelayEL;
    }

    /**
     * @return Expression to calculate the maximum date up to which to include rated transactions in the invoice. BillingRun.lastTransactionDate = BillingCycle.lastTransactionDate
     *         (resolved from EL)
     */
    public String getLastTransactionDateEL() {
        return lastTransactionDateEL;
    }

    /**
     * @param lastTransactionDateEL Expression to calculate the maximum date up to which to include rated transactions in the invoice. BillingRun.lastTransactionDate =
     *        BillingCycle.lastTransactionDate (resolved from EL)
     */
    public void setLastTransactionDateEL(String lastTransactionDateEL) {
        this.lastTransactionDateEL = lastTransactionDateEL;
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
        return "BillingCycleDto [code=" + getCode() + ", description=" + getDescription() + ", billingTemplateName=" + billingTemplateName + ", invoiceDateDelay=" + invoiceDateDelay + ", dueDateDelay=" + dueDateDelay
                + ", dueDateDelayEL=" + dueDateDelayEL + ", invoiceDateProductionDelay=" + invoiceDateProductionDelay + ", transactionDateDelay=" + transactionDateDelay + ", calendar=" + calendar
                + ", invoicingThreshold=" + invoicingThreshold + ", invoiceTypeCode=" + invoiceTypeCode + ", customFields=" + customFields + ", referenceDate=" + referenceDate + "]";
    }

    /**
     * @return Expression to calculate Invoice due date delay from the invoicing date
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * @param dueDateDelayEL Expression to calculate Invoice due date delay from the invoicing date
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    /**
     * @return Expression to calculate Invoice due date delay from the invoicing date - for Spark
     */
    public String getDueDateDelayELSpark() {
        return dueDateDelayELSpark;
    }

    /**
     * @param dueDateDelaySpark Expression to calculate Invoice due date delay from the invoicing date - for Spark
     */
    public void setDueDateDelaySpark(String dueDateDelaySpark) {
        this.dueDateDelayELSpark = dueDateDelaySpark;
    }

    /**
     * @return Expression to calculate Invoice template name
     */
    public String getBillingTemplateNameEL() {
        return billingTemplateNameEL;
    }

    /**
     * @param billingTemplateNameEL Expression to calculate Invoice template name
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
     * @return What reference date to use when calculating the next invoicing date with an invoice calendar as in: BillingCycle.calendar.nextCalendarDate(referenceDate)
     */
    public ReferenceDateEnum getReferenceDate() {
        return referenceDate;
    }

    /**
     * @param referenceDate What reference date to use when calculating the next invoicing date with an invoice calendar as in:
     *        BillingCycle.calendar.nextCalendarDate(referenceDate)
     */
    public void setReferenceDate(ReferenceDateEnum referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * Gets the scriptInstanceCode.
     * 
     * @return code of script instance
     */
    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    /**
     * Sets the scriptInstanceCode.
     * 
     * @param scriptInstanceCode code of script instance
     */
    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    /**
     * Gets the threshold option.
     * 
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     * 
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

    public Boolean getSplitPerPaymentMethod() {
        return splitPerPaymentMethod;
    }

    public void setSplitPerPaymentMethod(Boolean splitPerPaymentMethod) {
        this.splitPerPaymentMethod = splitPerPaymentMethod;
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}