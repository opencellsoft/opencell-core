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
import java.util.Map;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.DiscountAggregationModeEnum;
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

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The billing cycle type. */
    @XmlElement
    private BillingEntityTypeEnum type = BillingEntityTypeEnum.BILLINGACCOUNT;

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
     * EL to compute invoice.initialCollectionDate delay.
     */
    @XmlElement
    private String collectionDateDelayEl;

    /**
     * To decide whether or not dates should be recomputed at invoice validation.
     */
    private Boolean computeDatesAtValidation;

    /**
     *
     * check the threshold per entity/invoice.
     */
    @XmlElement
    private Boolean thresholdPerEntity;

    @XmlElement
    private String billingRunValidationScriptCode;
    
    private Map<String, Object> filters;
    
    @XmlElement
    private Integer priority;
    
    private Boolean disableAggregation;
    
    private Boolean useAccountingArticleLabel;
    
    private DateAggregationOption dateAggregation = DateAggregationOption.NO_DATE_AGGREGATION;
    
    private Boolean aggregateUnitAmounts;
    
    private Boolean ignoreSubscriptions;
    
    private Boolean ignoreOrders;

    /**
     * Discount type Rated transaction aggregation mode
     */
    @Schema(description = "Discount aggregation mode", allowableValues = {"NO_AGGREGATION", "FULL_AGGREGATION"}, defaultValue = "FULL_AGGREGATION")
    private DiscountAggregationModeEnum discountAggregation;

    /**
     * To decide to use incremental invoice lines or not.
     */
    @Schema(description = "Use incremental mode in invoice lines or not", nullable = true)
    private Boolean incrementalInvoiceLines;

    @Size(max = 2000)
    private String applicationEl;

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

    private List<LanguageDescriptionDto> languageDescriptions;

    public Boolean getThresholdPerEntity() {
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
            invoiceDateProductionDelayEL = billingCycleEntity.getInvoiceDateProductionDelayEL();
            lastTransactionDateDelayEL = billingCycleEntity.getLastTransactionDateDelayEL();
            lastTransactionDateEL = billingCycleEntity.getLastTransactionDateEL();
            invoicingThreshold = billingCycleEntity.getInvoicingThreshold();
            type = billingCycleEntity.getType();
            invoiceTypeEl = billingCycleEntity.getInvoiceTypeEl();
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
            computeDatesAtValidation = billingCycleEntity.isComputeDatesAtValidation();
            billingRunValidationScriptCode=billingCycleEntity.getBillingRunValidationScript()!=null?billingCycleEntity.getBillingRunValidationScript().getCode():null;
            filters = billingCycleEntity.getFilters();
            priority = billingCycleEntity.getPriority();
            disableAggregation = billingCycleEntity.isDisableAggregation();
            useAccountingArticleLabel = billingCycleEntity.isUseAccountingArticleLabel();
            dateAggregation = billingCycleEntity.getDateAggregation();
            aggregateUnitAmounts = billingCycleEntity.isAggregateUnitAmounts();
            ignoreSubscriptions = billingCycleEntity.isIgnoreSubscriptions();
            ignoreOrders = billingCycleEntity.isIgnoreOrders();
            discountAggregation = billingCycleEntity.getDiscountAggregation();
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
    public Integer getTransactionDateDelay() {
        return transactionDateDelay;
    }

    /**
     * @param transactionDateDelay A delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate
     *        value. BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public void setTransactionDateDelay(Integer transactionDateDelay) {
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
        return "BillingCycleDto [billingTemplateName=" + billingTemplateName + ", billingTemplateNameEL=" + billingTemplateNameEL + ", invoiceDateDelay=" + invoiceDateDelay
                + ", invoiceDateDelayEL=" + invoiceDateDelayEL + ", dueDateDelay=" + dueDateDelay + ", dueDateDelayEL=" + dueDateDelayEL + ", invoiceDateProductionDelay="
                + invoiceDateProductionDelay + ", invoiceDateProductionDelayEL=" + invoiceDateProductionDelayEL + ", transactionDateDelay=" + transactionDateDelay
                + ", lastTransactionDateDelayEL=" + lastTransactionDateDelayEL + ", lastTransactionDateEL=" + lastTransactionDateEL + ", calendar=" + calendar
                + ", invoicingThreshold=" + invoicingThreshold + ", splitPerPaymentMethod=" + splitPerPaymentMethod + ", invoiceTypeCode=" + invoiceTypeCode + ", invoiceTypeEl="
                + invoiceTypeEl + ", customFields=" + customFields + ", type=" + type + ", referenceDate=" + referenceDate + ", scriptInstanceCode=" + scriptInstanceCode
                + ", checkThreshold=" + checkThreshold + ", collectionDateDelayEl=" + collectionDateDelayEl + ", computeDatesAtValidation=" + computeDatesAtValidation
                + ", thresholdPerEntity=" + thresholdPerEntity + ", billingRunValidationScriptCode=" + billingRunValidationScriptCode + ", filters=" + filters + ", priority="
                + priority + ", disableAggregation=" + disableAggregation + ", useAccountingArticleLabel=" + useAccountingArticleLabel + ", dateAggregation=" + dateAggregation
                + ", aggregateUnitAmounts=" + aggregateUnitAmounts + ", ignoreSubscriptions=" + ignoreSubscriptions + ", ignoreOrders=" + ignoreOrders + ", languageDescriptions="
                + languageDescriptions + "]";
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

    /**
     * EL to compute invoice.initialCollectionDate delay.
     *
     * @return
     */
    public String getCollectionDateDelayEl() {
        return collectionDateDelayEl;
    }

    /**
     * EL to compute invoice.initialCollectionDate delay.
     *
     * @param collectionDateDelayEl
     */
    public void setCollectionDateDelayEl(String collectionDateDelayEl) {
        this.collectionDateDelayEl = collectionDateDelayEl;
    }

    /**
     * Check if invoice dates can be recalcukated
     *
     * @return
     */
    public Boolean getComputeDatesAtValidation() {
        return computeDatesAtValidation;
    }

    /**
     * Sets computeDatesAtValidation
     *
     * @param computeDatesAtValidation
     */
    public void setComputeDatesAtValidation(Boolean computeDatesAtValidation) {
        this.computeDatesAtValidation = computeDatesAtValidation;
    }

	public Map<String, Object> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

    public Boolean getDisableAggregation() {
        return disableAggregation;
    }

    public void setDisableAggregation(Boolean disableAggregation) {
        this.disableAggregation = disableAggregation;
    }

    public Boolean getUseAccountingArticleLabel() {
        return useAccountingArticleLabel;
    }

    public void setUseAccountingArticleLabel(Boolean useAccountingArticleLabel) {
        this.useAccountingArticleLabel = useAccountingArticleLabel;
    }

    public DateAggregationOption getDateAggregation() {
        return dateAggregation;
    }

    public void setDateAggregation(DateAggregationOption dateAggregation) {
        this.dateAggregation = dateAggregation;
    }

    public Boolean getAggregateUnitAmounts() {
        return aggregateUnitAmounts;
    }

    public void setAggregateUnitAmounts(Boolean aggregateUnitAmounts) {
        this.aggregateUnitAmounts = aggregateUnitAmounts;
    }

    public Boolean getIgnoreSubscriptions() {
        return ignoreSubscriptions;
    }

    public void setIgnoreSubscriptions(Boolean ignoreSubscriptions) {
        this.ignoreSubscriptions = ignoreSubscriptions;
    }

    public Boolean getIgnoreOrders() {
        return ignoreOrders;
    }

    public void setIgnoreOrders(Boolean ignoreOrders) {
        this.ignoreOrders = ignoreOrders;
    }

    /**
     * Set the incrementalInvoiceLines
     *
     * @param incrementalInvoiceLines Boolean true if using incremental mode, false otherwise
     */
    public void setIncrementalInvoiceLines(Boolean incrementalInvoiceLines) {
        this.incrementalInvoiceLines = incrementalInvoiceLines;
    }

    /**
     * Get the incrementalInvoiceLines
     *
     * @return incrementalInvoiceLines
     */
    public Boolean getIncrementalInvoiceLines() {
        return incrementalInvoiceLines;
    }

    public String getApplicationEl() {
        return applicationEl;

    /**
     * Get the discountAggregation
     * @return discountAggregation value
     */
    public DiscountAggregationModeEnum getDiscountAggregation() {
        return discountAggregation;
    }

    public void setApplicationEl(String applicationEl) {
        this.applicationEl = applicationEl;
    }
}

    /**
     * set the discountAggregation
     *
     * @param discountAggregation to set
     */
    public void setDiscountAggregation(DiscountAggregationModeEnum discountAggregation) {
        this.discountAggregation = discountAggregation;
    }
}
