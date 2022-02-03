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
import java.util.Date;
import java.util.List;

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
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.threshold.ThresholdLevelEnum;
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
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_cycle_seq"), })
public class BillingCycle extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Invoice template name
     */
    @Column(name = "billing_template_name")
    @Size(max = 50)
    private String billingTemplateName;

    /**
     * Expression to calculate Invoice template name
     */
    @Column(name = "billing_template_name_el", length = 2000)
    @Size(max = 2000)
    private String billingTemplateNameEL;

    /**
     * Invoicing calendar (identifier
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendar")
    private Calendar calendar;

    /**
     * Transaction date delay
     */
    @Column(name = "transaction_date_delay")
    private Integer transactionDateDelay;

    /**
     * Used to compute the invoice date from date of billing run
     */
    @Column(name = "invoice_date_production_delay")
    private Integer invoiceDateProductionDelay;

    /**
     * Used for immediate invoicing by oneshot charge
     */
    @Column(name = "invoice_date_delay")
    private Integer invoiceDateDelay;

    /**
     * Invoice due date delay from the invoicing date
     */
    @Column(name = "due_date_delay")
    private Integer dueDateDelay;

    /**
     * Billing accounts
     */
    @OneToMany(mappedBy = "billingCycle", fetch = FetchType.LAZY)
    private List<BillingAccount> billingAccounts = new ArrayList<BillingAccount>();

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
     * Expression to calculate Invoice due date delay from the invoicing date value
     */
    @Column(name = "due_date_delay_el", length = 2000)
    @Size(max = 2000)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /**
     * Reference date
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "threshold_level")
    private ThresholdLevelEnum thresholdLevel;
    
    /**
     * check threshold per entity?
     */
    @Type(type = "numeric_boolean")
    @Column(name = "threshold_per_entity")
    private boolean thresholdPerEntity;

    public boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

    public String getBillingTemplateName() {
        return billingTemplateName;
    }

    public void setBillingTemplateName(String billingTemplateName) {
        this.billingTemplateName = billingTemplateName;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Integer getTransactionDateDelay() {
        return transactionDateDelay;
    }

    public void setTransactionDateDelay(Integer transactionDateDelay) {
        this.transactionDateDelay = transactionDateDelay;
    }

    /**
     * @return Number of days to add to a billing run date to compute the invoice date
     */
    public Integer getInvoiceDateProductionDelay() {
        return invoiceDateProductionDelay;
    }

    /**
     * @param invoiceDateProductionDelay Number of days to add to a billing run date to compute the invoice date
     */
    public void setInvoiceDateProductionDelay(Integer invoiceDateProductionDelay) {
        this.invoiceDateProductionDelay = invoiceDateProductionDelay;
    }

    /**
     * Used for immediate invoicing by oneshot charge
     *
     * @return Number of days to add to a charge date to compute the invoice date
     */
    public Integer getInvoiceDateDelay() {
        return invoiceDateDelay;
    }

    /**
     * Used for immediate invoicing by oneshot charge
     *
     * @param invoiceDateDelay Number of days to add to a charge date to compute the invoice date
     */
    public void setInvoiceDateDelay(Integer invoiceDateDelay) {
        this.invoiceDateDelay = invoiceDateDelay;
    }

    /**
     * @return Invoice due date delay from the invoicing date
     */
    public Integer getDueDateDelay() {
        return dueDateDelay;
    }

    /**
     * @param dueDateDelay Invoice due date delay from the invoicing date
     */
    public void setDueDateDelay(Integer dueDateDelay) {
        this.dueDateDelay = dueDateDelay;
    }

    public List<BillingAccount> getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(List<BillingAccount> billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

    public Date getNextCalendarDate(Date subscriptionDate, Date date) {
        Date result = null;
        if (calendar != null) {
            calendar.setInitDate(subscriptionDate);
            result = calendar.nextCalendarDate(date != null ? date : new Date());
        }
        return result;
    }

    public Date getNextCalendarDate(Date subscriptionDate) {
        return getNextCalendarDate(subscriptionDate, new Date());
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
     * Gets the reference date
     *
     * @return the reference date
     */
    public ReferenceDateEnum getReferenceDate() {
        return referenceDate;
    }

    /**
     * Sets the reference date.
     *
     * @param referenceDate the new reference date
     */
    public void setReferenceDate(ReferenceDateEnum referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * Gets the threshold option.
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

	/**
	 * @return the thresholdLevel
	 */
	public ThresholdLevelEnum getThresholdLevel() {
		return thresholdLevel;
	}

	/**
	 * @param thresholdLevel the thresholdLevel to set
	 */
	public void setThresholdLevel(ThresholdLevelEnum thresholdLevel) {
		this.thresholdLevel = thresholdLevel;
	}
}