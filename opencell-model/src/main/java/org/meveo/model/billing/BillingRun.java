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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.ReferenceIdentifierQuery;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Billing run
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ReferenceIdentifierQuery("BillingRun.findByIdAndBCCode")
@CustomFieldEntity(cftCodePrefix = "BillingRun")
@Table(name = "billing_billing_run")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_billing_run_seq") })
@NamedQueries({
        @NamedQuery(name = "BillingRun.getForInvoicing", query = "SELECT br FROM BillingRun br where br.status in ('NEW', 'PREVALIDATED', 'INVOICES_GENERATED', 'POSTVALIDATED') or (br.status='POSTINVOICED' and br.processType='FULL_AUTOMATIC') order by br.id asc"),
        @NamedQuery(name = "BillingRun.findByIdAndBCCode", query = "from BillingRun br join fetch br.billingCycle bc where lower(concat(br.id,'/',bc.code)) like :code ") })

public class BillingRun extends AuditableEntity implements ICustomFieldEntity, IReferenceEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Billing run processing start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "process_date")
    private Date processDate;

    /**
     * Execution status
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private BillingRunStatusEnum status;

    /**
     * Last status change date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

    /**
     * Billing cycle
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "billing_cycle_id")
    private BillingCycle billingCycle;

    /**
     * Number of matched Billing accounts
     */
    @Column(name = "nb_billing_account")
    private Integer billingAccountNumber;

    /**
     * Number of billable Billing accounts
     */
    @Column(name = "nb_billable_billing_account")
    private Integer billableBillingAcountNumber;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "nb_producible_invoice")
    private Integer producibleInvoiceNumber;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "producible_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal producibleAmountWithoutTax;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "producible_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal producibleAmountTax;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "nb_invoice")
    private Integer InvoiceNumber;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "producible_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal producibleAmountWithTax;

    /**
     * Billed amount without tax
     */
    @Column(name = "pr_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal prAmountWithoutTax;

    /**
     * Billed amount with tax
     */
    @Column(name = "pr_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal prAmountWithTax;

    /**
     * Billed tax amount
     */
    @Column(name = "pr_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal prAmountTax;

    /**
     * Invoices produced by a Billing run
     */
    @OneToMany(mappedBy = "billingRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<Invoice>();

    /**
     * Billing run lists
     */
    @OneToMany(mappedBy = "billingRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BillingRunList> billingRunLists = new HashSet<BillingRunList>();

    /**
     * Billed billing accounts
     */
    @OneToMany(mappedBy = "billingRun", fetch = FetchType.LAZY)
    private List<BillingAccount> billableBillingAccounts = new ArrayList<BillingAccount>();

    /**
     * Billing run processing type
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "process_type")
    private BillingProcessTypesEnum processType;

    /**
     * Include Rated transactions from Billing Accounts which next invoicing date is between the dates - from date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Include Rated transactions from Billing Accounts which next invoicing date is between the dates - to date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Invoice date
     */
    @Column(name = "invoice_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoiceDate;

    /**
     * Include in invoice Rated transactions up to that date
     */
    @Column(name = "last_transaction_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastTransactionDate;

    /**
     * Rejection reason
     */
    @Column(name = "rejection_reason", length = 255)
    @Size(max = 255)
    private String rejectionReason;

    /**
     * Currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_currency_id")
    private Currency currency;

    /**
     * Country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_country_id")
    private Country country;

    /**
     * Language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_language_id")
    private Language language;

    /**
     * Selected billing accounts (identifiers)
     */
    @Column(name = "selected_billing_accounts", columnDefinition = "TEXT")
    private String selectedBillingAccounts;

    /**
     * Pre-invoicing reports
     */
    @Transient
    PreInvoicingReportsDTO preInvoicingReports = new PreInvoicingReportsDTO();

    /**
     * Post-invoicing reports
     */
    @Transient
    PostInvoicingReportsDTO postInvoicingReports = new PostInvoicingReportsDTO();

    /**
     * Rejected billing accounts
     */
    @OneToMany(mappedBy = "billingRun", fetch = FetchType.LAZY)
    private List<RejectedBillingAccount> rejectedBillingAccounts = new ArrayList<RejectedBillingAccount>();

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Reference date
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_date")
    private ReferenceDateEnum referenceDate = ReferenceDateEnum.TODAY;
    
    @Type(type = "numeric_boolean")
    @Column(name = "skip_validation_script")
    private boolean skipValidationScript = false;

    /**
     * EL to compute invoice.initialCollectionDate delay.
     */
    @Column(name = "collection_date")
    private Date collectionDate;

    /**
     * To decide whether or not dates should be recomputed at invoice validation.
     */
    @Column(name = "compute_dates_validation")
    private Boolean computeDatesAtValidation;
    
    /**
     * The next BillingRun where rejected/suspect invoices may be moved.
     */
    @OneToOne
    @JoinColumn(name = "next_billing_run_id")
    private BillingRun nextBillingRun;

    public BillingRun getNextBillingRun() {
		return nextBillingRun;
	}

	public void setNextBillingRun(BillingRun nextBillingRun) {
		this.nextBillingRun = nextBillingRun;
	}

	public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public BillingRunStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BillingRunStatusEnum status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Integer getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public void setBillingAccountNumber(Integer billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }

    public Integer getBillableBillingAcountNumber() {
        return billableBillingAcountNumber;
    }

    public void setBillableBillingAcountNumber(Integer billableBillingAcountNumber) {
        this.billableBillingAcountNumber = billableBillingAcountNumber;
    }

    public Integer getProducibleInvoiceNumber() {
        return producibleInvoiceNumber;
    }

    public void setProducibleInvoiceNumber(Integer producibleInvoiceNumber) {
        this.producibleInvoiceNumber = producibleInvoiceNumber;
    }

    public BigDecimal getProducibleAmountWithoutTax() {
        return producibleAmountWithoutTax;
    }

    public void setProducibleAmountWithoutTax(BigDecimal producibleAmountWithoutTax) {
        this.producibleAmountWithoutTax = producibleAmountWithoutTax;
    }

    public BigDecimal getProducibleAmountTax() {
        return producibleAmountTax;
    }

    public void setProducibleAmountTax(BigDecimal producibleAmountTax) {
        this.producibleAmountTax = producibleAmountTax;
    }

    public Integer getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        InvoiceNumber = invoiceNumber;
    }

    public BigDecimal getProducibleAmountWithTax() {
        return producibleAmountWithTax;
    }

    public void setProducibleAmountWithTax(BigDecimal producibleAmountWithTax) {
        this.producibleAmountWithTax = producibleAmountWithTax;
    }

    public void setPrAmountTax(BigDecimal prAmountTax) {
        this.prAmountTax = prAmountTax;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public Set<BillingRunList> getBillingRunLists() {
        return billingRunLists;
    }

    public void setBillingRunLists(Set<BillingRunList> billingRunLists) {
        this.billingRunLists = billingRunLists;
    }

    public BillingProcessTypesEnum getProcessType() {
        return processType;
    }

    public void setProcessType(BillingProcessTypesEnum processType) {
        this.processType = processType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * @return Include in invoice Rated transactions up to that date
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * @param lastTransactionDate Include in invoice Rated transactions up to that date
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public PreInvoicingReportsDTO getPreInvoicingReports() {
        return preInvoicingReports;
    }

    public void setPreInvoicingReports(PreInvoicingReportsDTO preInvoicingReports) {
        this.preInvoicingReports = preInvoicingReports;
    }

    public PostInvoicingReportsDTO getPostInvoicingReports() {
        return postInvoicingReports;
    }

    public void setPostInvoicingReports(PostInvoicingReportsDTO postInvoicingReports) {
        this.postInvoicingReports = postInvoicingReports;
    }

    public List<BillingAccount> getBillableBillingAccounts() {
        return billableBillingAccounts;
    }

    public void setBillableBillingAccounts(List<BillingAccount> selectedBillingAccounts) {
        this.billableBillingAccounts = selectedBillingAccounts;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public BigDecimal getPrAmountWithoutTax() {
        return prAmountWithoutTax;
    }

    public void setPrAmountWithoutTax(BigDecimal prAmountWithoutTax) {
        this.prAmountWithoutTax = prAmountWithoutTax;
    }

    public BigDecimal getPrAmountWithTax() {
        return prAmountWithTax;
    }

    public void setPrAmountWithTax(BigDecimal prAmountWithTax) {
        this.prAmountWithTax = prAmountWithTax;
    }

    public BigDecimal getPrAmountTax() {
        return prAmountTax;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getSelectedBillingAccounts() {
        return selectedBillingAccounts;
    }

    public void setSelectedBillingAccounts(String selectedBillingAccounts) {
        this.selectedBillingAccounts = selectedBillingAccounts;
    }

    public List<RejectedBillingAccount> getRejectedBillingAccounts() {
        return rejectedBillingAccounts;
    }

    public void setRejectedBillingAccounts(List<RejectedBillingAccount> rejectedBillingAccounts) {
        this.rejectedBillingAccounts = rejectedBillingAccounts;
    }

    public void addRejectedBillingAccounts(RejectedBillingAccount rejectedBillingAccount) {
        if (rejectedBillingAccounts == null) {
            rejectedBillingAccounts = new ArrayList<RejectedBillingAccount>();
        }
        rejectedBillingAccounts.add(rejectedBillingAccount);
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    @Override
    public String getUuid() {
        setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public int hashCode() {
        return 961 + (("BR" + (id == null ? "" : id)).hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof User)) {
            return false;
        }

        BillingRun other = (BillingRun) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (id == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!id.equals(other.getId())) {
            return false;
        }
        return true;
    }

    public void setDescriptionOrCode(String description) {

    }

    public String getReferenceCode() {
        return id + "/" + billingCycle.getCode();
    }

    public void setReferenceCode(Object value) {
        String id = null;
        if (value != null) {
            id = value.toString().split("/")[0];
            setId(Long.valueOf(id));
            billingCycle = new BillingCycle();
            billingCycle.setCode(getBillingCycleCode(value.toString(), id));
        }
    }

    private String getBillingCycleCode(String value, String id) {
        return value.substring(id.length() + 1, value.length());
    }

    public String getReferenceDescription() {
        return billingCycle.getDescription();
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
     * Gets CollectionDate date.
     *
     * @return ollectionDate date.
     */
    public Date getCollectionDate() {
        return collectionDate;
    }

    /**
     * Sets CollectionDate delay EL.
     *
     * @param collectionDate
     */
    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    /**
     * @return
     */
    public Boolean getComputeDatesAtValidation() {
        return computeDatesAtValidation;
    }

    /**
     * @param computeDatesAtValidation
     */
    public void setComputeDatesAtValidation(Boolean computeDatesAtValidation) {
        this.computeDatesAtValidation = computeDatesAtValidation;
    }
    
    @Enumerated(value = EnumType.STRING)
    @Column(name = "reject_auto_action")
    private BillingRunAutomaticActionEnum rejectAutoAction = BillingRunAutomaticActionEnum.MOVE;
    
    @Enumerated(value = EnumType.STRING)
    @Column(name = "suspect_auto_action")
    private BillingRunAutomaticActionEnum suspectAutoAction = BillingRunAutomaticActionEnum.MOVE;
    
    public BillingRunAutomaticActionEnum getRejectAutoAction() {
		return rejectAutoAction;
	}

	public void setRejectAutoAction(BillingRunAutomaticActionEnum autoRejectAction) {
		this.rejectAutoAction = autoRejectAction;
	}

	public BillingRunAutomaticActionEnum getSuspectAutoAction() {
		return suspectAutoAction;
	}

	public void setSuspectAutoAction(BillingRunAutomaticActionEnum autoSuspectAction) {
		this.suspectAutoAction = autoSuspectAction;
	}

	public boolean isSkipValidationScript() {
		return skipValidationScript;
	}

	public void setSkipValidationScript(boolean skipValidationScript) {
		this.skipValidationScript = skipValidationScript;
	}
}
