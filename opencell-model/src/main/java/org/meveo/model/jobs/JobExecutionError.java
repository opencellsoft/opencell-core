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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.NotifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

/**
 * Job execution error log
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "job_execution_error")
@NotifiableEntity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "job_execution_error_seq"), })
@NamedQueries({ @NamedQuery(name = "JobExecutionError.purgeByJobInstance", query = "delete JobExecutionError err WHERE err.jobInstance=:jobInstance") })

public class JobExecutionError extends BaseEntity {
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
     * Entity identifier
     */
    @Column(name = "entity_id")
    private Long entityId;

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
    @Column(name = "error_reason", length = 2000)
    private String errorReason;

    
    /**
     * Entity - looked up by entity Id
     */
    @Transient
    private IEntity entity;
    
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
     * @return Entity identifier
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * @param entityId Entity identifier
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    /**
     * @return Entity - looked up by entity Id
     */
    public IEntity getEntity() {
        return entity;
    }
    
    /**
     * @param entity Entity - looked up by entity Id
     */
    public void setEntity(IEntity entity) {
        this.entity = entity;
    }
}