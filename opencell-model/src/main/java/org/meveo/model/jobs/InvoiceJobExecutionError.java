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

package org.meveo.model.jobs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Invoice;

/**
 * Invoice related job execution error log
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "job_execution_error")
@Immutable
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "job_execution_error_seq"), })
public class InvoiceJobExecutionError extends BaseEntity {
    private static final long serialVersionUID = 430457580612075457L;

    /**
     * Job instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id")
    private JobInstance jobInstance;

    /**
     * Record timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created = new Date();

    /**
     * Invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    private Invoice invoice;

    /**
     * Period from date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "period_from")
    private Date periodFrom;

    /**
     * Period to date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "period_to")
    private Date periodTo;

    /**
     * Error reason
     */
    @Column(name = "error_reason")
    private String errorReason;

    /**
     * @return Job instance
     */
    public JobInstance getJobInstance() {
        return jobInstance;
    }

    /**
     * @param jobInstance Job instance
     */
    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

    /**
     * @return Record timestamp
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created Record timestamp
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return Invoice
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * @param invoice Invoice
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * @return Period from date
     */
    public Date getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom Period from date
     */
    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    /**
     * @return Period to date
     */
    public Date getPeriodTo() {
        return periodTo;
    }

    /**
     * @param periodTo Period to date
     */
    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    /**
     * @return Error reason
     */
    public String getErrorReason() {
        return errorReason;
    }

    /**
     * @param errorReason Error reason
     */
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }
}