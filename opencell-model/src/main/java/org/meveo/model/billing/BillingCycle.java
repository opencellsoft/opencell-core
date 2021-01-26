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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.scripts.ScriptInstance;

/**
 * Billing cycle
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "BillingCycle")
@Table(name = "billing_cycle", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_cycle_seq"), })
public class BillingCycle extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Expression to calculate Invoice template name
     */
    @Column(name = "billing_template_name_el", length = 2000)
    @Size(max = 2000)
    private String billingTemplateNameEL;

    /**
     * Invoicing calendar
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar")
    private Calendar calendar;

    /**
     * Expression to calculate a delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate value.
     * BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    @Column(name = "transaction_date_delay_EL", length = 2000)
    @Size(max = 2000)
    private String lastTransactionDateDelayEL;

    /**
     * Expression to calculate the maximum date up to which to include rated transactions in the invoice. BillingRun.lastTransactionDate = BillingCycle.lastTransactionDate
     * (resolved from EL)
     */
    @Column(name = "transaction_date_el", length = 2000)
    @Size(max = 2000)
    private String lastTransactionDateEL;

    /**
     * Expression to calculate a delay to apply when calculating the invoice date. Invoice.invoiceDate = BillingRun.invoiceDate = BillingRun.processDate +
     * BillingCycle.invoiceDateProductionDelay (resolved from EL).
     */
    @Column(name = "invoice_date_production_delay_el", length = 2000)
    @Size(max = 2000)
    private String invoiceDateProductionDelayEL;

    /**
     * Expression to calculate the invoice date delay from a one shot charge date for immediate invoicing
     */
    @Column(name = "invoice_date_delay_el", length = 2000)
    @Size(max = 2000)
    private String invoiceDateDelayEL;

    /**
     * Billing accounts
     */
    @OneToMany(mappedBy = "billingCycle", fetch = FetchType.LAZY)
    private List<BillingAccount> billingAccounts = new ArrayList<>();

    /**
     * Invoice amount threshold - will disregard invoices below this amount
     */
    @Column(name = "invoicing_threshold")
    private BigDecimal invoicingThreshold;

    /**
     * Invoice type
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;

    /**
     * Expression to calculate Invoice due date delay from the invoicing date
     */
    @Column(name = "due_date_delay_el", length = 2000, nullable = false)
    @Size(max = 2000)
    @NotNull
    private String dueDateDelayEL;

    /**
     * Expression to calculate Invoice due date delay from the invoicing date value - for Spark
     */
    @Column(name = "due_date_delay_el_sp", length = 2000)
    @Size(max = 2000)
    private String dueDateDelayELSpark;

    /**
     * Expression to resolve invoice type code
     */
    @Column(name = "invoice_type_el", length = 2000)
    @Size(max = 2000)
    private String invoiceTypeEl;

    /**
     * Expression to resolve invoice type code - for Spark
     */
    @Column(name = "invoice_type_el_sp", length = 2000)
    @Size(max = 2000)
    private String invoiceTypeElSpark;

    /**
     * Entity type to bill
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle_type", nullable = false)
    private BillingEntityTypeEnum type;

    /**
     * Script to group rated transactions by invoice type or other parameters. Script accepts a RatedTransaction list as an input.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /**
     * What reference date to use when calculating the next invoicing date with an invoice calendar as in: BillingCycle.calendar.nextCalendarDate(referenceDate)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_date")
    private ReferenceDateEnum referenceDate = ReferenceDateEnum.TODAY;

    /**
     * The option on how to check the threshold.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "check_threshold")
    private ThresholdOptionsEnum checkThreshold;
    
    /**
     * check threshold per entity?
     */
    @Type(type = "numeric_boolean")
    @Column(name = "threshold_per_entity")
    private boolean thresholdPerEntity;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    public boolean isThresholdPerEntity() {
    	return thresholdPerEntity;
	}

	public void setThresholdPerEntity(boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}
	
    /**
     * if true then subscriptions are grouped by paymentMethod and billed separately.
     */
    @Column(name = "split_per_payment_method")
    @Type(type = "numeric_boolean")
    private boolean splitPerPaymentMethod;

    /**
     * executed for each invoice, Will raise an exception if the invoice is invalid. Context will contain billingRun and invoice.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_validation_script_id")
    private ScriptInstance billingRunValidationScript;
    
    /**
     * @return Invoicing calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * @param calendar Invoicing calendar
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return Expression to calculate a delay to apply when calculating the maximum date up to which to include rated transactions in the invoice - BillingRun.lastTransactionDate
     *         value. BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public String getLastTransactionDateDelayEL() {
        return lastTransactionDateDelayEL;
    }

    /**
     * @param lastTransactionDateDelayEL Expression to calculate a delay to apply when calculating the maximum date up to which to include rated transactions in the invoice -
     *        BillingRun.lastTransactionDate value. BillingRun.lastTransactionDate = BillingRun.processDate + BillingCycle.transactionDateDelay (resolved from EL).
     */
    public void setLastTransactionDateDelayEL(String lastTransactionDateDelayEL) {
        this.lastTransactionDateDelayEL = lastTransactionDateDelayEL;
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
     * @return Expression to calculate the number of days to add to a billing run date to compute the invoice date
     */
    public String getInvoiceDateProductionDelayEL() {
        return invoiceDateProductionDelayEL;
    }

    /**
     * @param invoiceDateProductionDelay Expression to calculate the number of days to add to a billing run date to compute the invoice date
     */
    public void setInvoiceDateProductionDelayEL(String invoiceDateProductionDelayEL) {
        this.invoiceDateProductionDelayEL = invoiceDateProductionDelayEL;
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

    public List<BillingAccount> getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(List<BillingAccount> billingAccounts) {
        this.billingAccounts = billingAccounts;
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
     * @return the invoiceType
     */
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    /**
     * @param invoiceType the invoiceType to set
     */
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * @return Expression to calculate Invoice due date delay from the invoicing date value
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * @param dueDateDelayEL Expression to calculate Invoice due date delay from the invoicing date value
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    /**
     * @return Expression to calculate Invoice due date delay from the invoicing date value - for Spark
     */
    public String getDueDateDelayELSpark() {
        return dueDateDelayELSpark;
    }

    /**
     * @param dueDateDelayELSpark Expression to calculate Invoice due date delay from the invoicing date value - for Spark
     */
    public void setDueDateDelayELSpark(String dueDateDelayELSpark) {
        this.dueDateDelayELSpark = dueDateDelayELSpark;
    }

    public String getBillingTemplateNameEL() {
        return billingTemplateNameEL;
    }

    public void setBillingTemplateNameEL(String billingTemplateNameEL) {
        this.billingTemplateNameEL = billingTemplateNameEL;
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

    public BillingEntityTypeEnum getType() {
        if (type == null) {
            return BillingEntityTypeEnum.BILLINGACCOUNT;
        }
        return type;
    }

    public void setType(BillingEntityTypeEnum type) {
        this.type = type;
    }

    /**
     * @return Script to group rated transactions by invoice type or other parameters. Script accepts a RatedTransaction list as an input.
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * @param scriptInstance Script to group rated transactions by invoice type or other parameters. Script accepts a RatedTransaction list as an input.
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
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

    /**
     * Gets the splitPerPaymentMethod option.
     *
     * @return the splitPerPaymentMethod option
     */
    public boolean isSplitPerPaymentMethod() {
        return splitPerPaymentMethod;
    }

    /**
     * Sets the threshold option.
     *
     * @param splitPerPaymentMethod the splitPerPaymentMethod option
     */
    public void setSplitPerPaymentMethod(boolean splitPerPaymentMethod) {
        this.splitPerPaymentMethod = splitPerPaymentMethod;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.description);
        } else {
            return this.description;
        }
    }
    
	public ScriptInstance getBillingRunValidationScript() {
		return billingRunValidationScript;
	}

	public void setBillingRunValidationScript(ScriptInstance billingRunValidationScript) {
		this.billingRunValidationScript = billingRunValidationScript;
	}
}