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
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.scripts.ScriptInstance;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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
    @ManyToOne(fetch = FetchType.EAGER)
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
     * Invoice amount threshold - will disregard invoices below this amount
     */
    @Column(name = "invoicing_threshold")
    private BigDecimal invoicingThreshold;

    /**
     * Invoice type
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;

    /**
     * Expression to calculate Invoice due date delay from the invoicing date
     */
    @Column(name = "due_date_delay_el", length = 2000, nullable = false)
    @Size(max = 2000)
    private String dueDateDelayEL;

    /**
     * Expression to resolve invoice type code
     */
    @Column(name = "invoice_type_el", length = 2000)
    @Size(max = 2000)
    private String invoiceTypeEl;

    /**
     * Entity type to bill
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle_type", nullable = false)
    private BillingEntityTypeEnum type;

    /**
     * Script to group rated transactions by invoice type or other parameters. Script accepts a RatedTransaction list as an input.
     */
    @ManyToOne(fetch = FetchType.EAGER)
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
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    /**
     * if true then subscriptions are grouped by paymentMethod and billed separately.
     */
    @Column(name = "split_per_payment_method")
    @Type(type = "numeric_boolean")
    private boolean splitPerPaymentMethod;

    /**
     * EL to compute invoice.initialCollectionDate delay.
     */
    @Column(name = "collection_date_delay_el", length = 2000)
    @Size(max = 2000)
    private String collectionDateDelayEl;

    /**
     * To decide whether or not dates should be recomputed at invoice validation.
     */
    @Column(name = "compute_dates_validation")
    @Type(type = "numeric_boolean")
    private boolean computeDatesAtValidation = false;

    /**
     * executed for each invoice, Will raise an exception if the invoice is invalid. Context will contain billingRun and invoice.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "billing_run_validation_script_id")
    private ScriptInstance billingRunValidationScript;
    
    /**
     * Filtering option used in billing cycle.
     */
    @Type(type = "json")
    @Column(name = "filters", columnDefinition = "jsonb")
    private Map<String, Object> filters;
    
    /**
     *  Higher priority macth with lowest priority value
     */
    @Column(name = "priority")
    private int priority = 0;
    
    @Type(type = "numeric_boolean")
    @Column(name = "disable_aggregation")
    private boolean disableAggregation = false;
    
    @Type(type = "numeric_boolean")
    @Column(name = "use_accounting_article_label")
    private boolean useAccountingArticleLabel = false;
    
    @Enumerated(value = EnumType.STRING)
    @Column(name = "date_aggregation")
    private DateAggregationOption dateAggregation = DateAggregationOption.NO_DATE_AGGREGATION;
    
    @Type(type = "numeric_boolean")
    @Column(name = "aggregate_unit_amounts")
    private boolean aggregateUnitAmounts = false;
    
    @Type(type = "numeric_boolean")
    @Column(name = "ignore_subscriptions")
    private boolean ignoreSubscriptions = true;
    
    @Type(type = "numeric_boolean")
    @Column(name = "ignore_orders")
    private boolean ignoreOrders = true;

    /**
     * To decide to use incremental invoice lines or not.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "incremental_invoice_lines")
    private boolean incrementalInvoiceLines = FALSE;

    /**
     * Default configuration for billingRun.preReportAutoOnCreate
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_pre_report_auto_on_create")
    private Boolean reportConfigPreReportAutoOnCreate = FALSE;

    /**
     * Default configuration for billingRun.preReportAutoOnInvoiceLinesJob
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_pre_report_auto_on_invoice_lines_job")
    private Boolean reportConfigPreReportAutoOnInvoiceLinesJob = FALSE;

    /**
     * Pilots computation and display of billing accounts block
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_display_billing_accounts")
    private Boolean reportConfigDisplayBillingAccounts = TRUE;

    /**
     * Pilots computation and display of subscriptions block
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_display_subscriptions")
    private Boolean reportConfigDisplaySubscriptions = TRUE;

    /**
     *
     * Pilots computation and display of offers block
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_display_offers")
    private Boolean reportConfigDisplayOffers = TRUE;

    /**
     * Pilots computation and display of products block
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_display_products")
    private Boolean reportConfigDisplayProducts = TRUE;

    /**
     * Pilots computation and display of articles block
     */
    @Type(type = "numeric_boolean")
    @Column(name = "report_config_display_articles")
    private Boolean reportConfigDisplayArticles = TRUE;

    /**
     * Report billing accounts block size between 1 and 100
     */
    @Column(name = "report_config_block_size_billing_accounts")
    private int reportConfigBlockSizeBillingAccounts = 10;

    /**
     * Report subscriptions block size between 1 and 100
     */
    @Column(name = "report_config_block_size_subscriptions")
    private int reportConfigBlockSizeSubscriptions = 10;

    /**
     * Report offers block size between 1 and 100
     */
    @Column(name = "report_config_block_size_offers")
    private int reportConfigBlockSizeOffers = 10;

    /**
     * Report products block size between 1 and 100
     */
    @Column(name = "report_config_block_size_products")
    private int reportConfigBlockSizeProducts = 10;

    /**
     *
     * Report articles block size between 1 and 100
     */
    @Column(name = "report_config_block_size_articles")
    private int reportConfigBlockSizeArticles = 10;

    @Column(name = "application_el", length = 2000)
    @Size(max = 2000)
    private String applicationEl;

    public boolean isThresholdPerEntity() {
        return thresholdPerEntity;
    }

    public void setThresholdPerEntity(boolean thresholdPerEntity) {
        this.thresholdPerEntity = thresholdPerEntity;
    }

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

    /**
     * Gets CollectionDate delay EL.
     *
     * @return ollectionDate delay EL.
     */
    public String getCollectionDateDelayEl() {
        return collectionDateDelayEl;
    }

    /**
     * Sets CollectionDate delay EL.
     *
     * @param collectionDateDelayEl
     */
    public void setCollectionDateDelayEl(String collectionDateDelayEl) {
        this.collectionDateDelayEl = collectionDateDelayEl;
    }

    public boolean isComputeDatesAtValidation() {
        return computeDatesAtValidation;
    }

    public void setComputeDatesAtValidation(boolean computeDatesAtValidation) {
        this.computeDatesAtValidation = computeDatesAtValidation;
    }

	public ScriptInstance getBillingRunValidationScript() {
		return billingRunValidationScript;
	}

	public void setBillingRunValidationScript(ScriptInstance billingRunValidationScript) {
		this.billingRunValidationScript = billingRunValidationScript;
	}

	public Map<String, Object> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public boolean isDisableAggregation() {
        return disableAggregation;
    }

    public void setDisableAggregation(boolean disableAggregation) {
        this.disableAggregation = disableAggregation;
    }

    public boolean isUseAccountingArticleLabel() {
        return useAccountingArticleLabel;
    }

    public void setUseAccountingArticleLabel(boolean useAccountingArticleLabel) {
        this.useAccountingArticleLabel = useAccountingArticleLabel;
    }

    public DateAggregationOption getDateAggregation() {
        return dateAggregation;
    }

    public void setDateAggregation(DateAggregationOption dateAggregation) {
        this.dateAggregation = dateAggregation;
    }

    public boolean isAggregateUnitAmounts() {
        return aggregateUnitAmounts;
    }

    public void setAggregateUnitAmounts(boolean aggregateUnitAmounts) {
        this.aggregateUnitAmounts = aggregateUnitAmounts;
    }

    public boolean isIgnoreSubscriptions() {
        return ignoreSubscriptions;
    }

    public void setIgnoreSubscriptions(boolean ignoreSubscriptions) {
        this.ignoreSubscriptions = ignoreSubscriptions;
    }

    public boolean isIgnoreOrders() {
        return ignoreOrders;
    }

    public void setIgnoreOrders(boolean ignoreOrders) {
        this.ignoreOrders = ignoreOrders;
    }

    public Boolean getIncrementalInvoiceLines() {
        return incrementalInvoiceLines;
    }

    public void setIncrementalInvoiceLines(boolean incrementalInvoiceLines) {
        this.incrementalInvoiceLines = incrementalInvoiceLines;
    }

    public String getApplicationEl() {
        return applicationEl;
    }

    public void setApplicationEl(String applicationEl) {
        this.applicationEl = applicationEl;
    }

    public Boolean getReportConfigPreReportAutoOnCreate() {
        return reportConfigPreReportAutoOnCreate;
    }

    public void setReportConfigPreReportAutoOnCreate(Boolean reportConfigPreReportAutoOnCreate) {
        this.reportConfigPreReportAutoOnCreate = reportConfigPreReportAutoOnCreate;
    }

    public Boolean getReportConfigPreReportAutoOnInvoiceLinesJob() {
        return reportConfigPreReportAutoOnInvoiceLinesJob;
    }

    public void setReportConfigPreReportAutoOnInvoiceLinesJob(Boolean reportConfigPreReportAutoOnInvoiceLinesJob) {
        this.reportConfigPreReportAutoOnInvoiceLinesJob = reportConfigPreReportAutoOnInvoiceLinesJob;
    }

    public Boolean getReportConfigDisplayBillingAccounts() {
        return reportConfigDisplayBillingAccounts;
    }

    public void setReportConfigDisplayBillingAccounts(Boolean reportConfigDisplayBillingAccounts) {
        this.reportConfigDisplayBillingAccounts = reportConfigDisplayBillingAccounts;
    }

    public Boolean getReportConfigDisplaySubscriptions() {
        return reportConfigDisplaySubscriptions;
    }

    public void setReportConfigDisplaySubscriptions(Boolean reportConfigDisplaySubscriptions) {
        this.reportConfigDisplaySubscriptions = reportConfigDisplaySubscriptions;
    }

    public Boolean getReportConfigDisplayOffers() {
        return reportConfigDisplayOffers;
    }

    public void setReportConfigDisplayOffers(Boolean reportConfigDisplayOffers) {
        this.reportConfigDisplayOffers = reportConfigDisplayOffers;
    }

    public Boolean getReportConfigDisplayProducts() {
        return reportConfigDisplayProducts;
    }

    public void setReportConfigDisplayProducts(Boolean reportConfigDisplayProducts) {
        this.reportConfigDisplayProducts = reportConfigDisplayProducts;
    }

    public Boolean getReportConfigDisplayArticles() {
        return reportConfigDisplayArticles;
    }

    public void setReportConfigDisplayArticles(Boolean reportConfigDisplayArticles) {
        this.reportConfigDisplayArticles = reportConfigDisplayArticles;
    }

    public int getReportConfigBlockSizeBillingAccounts() {
        return reportConfigBlockSizeBillingAccounts;
    }

    public void setReportConfigBlockSizeBillingAccounts(int reportConfigBlockSizeBillingAccounts) {
        this.reportConfigBlockSizeBillingAccounts = reportConfigBlockSizeBillingAccounts;
    }

    public int getReportConfigBlockSizeSubscriptions() {
        return reportConfigBlockSizeSubscriptions;
    }

    public void setReportConfigBlockSizeSubscriptions(int reportConfigBlockSizeSubscriptions) {
        this.reportConfigBlockSizeSubscriptions = reportConfigBlockSizeSubscriptions;
    }

    public int getReportConfigBlockSizeOffers() {
        return reportConfigBlockSizeOffers;
    }

    public void setReportConfigBlockSizeOffers(int reportConfigBlockSizeOffers) {
        this.reportConfigBlockSizeOffers = reportConfigBlockSizeOffers;
    }

    public int getReportConfigBlockSizeProducts() {
        return reportConfigBlockSizeProducts;
    }

    public void setReportConfigBlockSizeProducts(int reportConfigBlockSizeProducts) {
        this.reportConfigBlockSizeProducts = reportConfigBlockSizeProducts;
    }

    public int getReportConfigBlockSizeArticles() {
        return reportConfigBlockSizeArticles;
    }

    public void setReportConfigBlockSizeArticles(int reportConfigBlockSizeArticles) {
        this.reportConfigBlockSizeArticles = reportConfigBlockSizeArticles;
    }
}
