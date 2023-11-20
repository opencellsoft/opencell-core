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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * Batch entity
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Entity
@Table(name = "batch_entity")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@Parameter(name = "sequence_name", value = "batch_entity_seq"),})
@NamedQueries({
        @NamedQuery(name = "BatchEntity.getOpenedBatchEntity",
                query = "SELECT b FROM BatchEntity b WHERE b.status=org.meveo.model.billing.BatchEntityStatusEnum.OPEN and b.targetJob=:targetJob"),
        @NamedQuery(name = "BatchEntity.cancelOpenedBatchEntity",
                query = "UPDATE BatchEntity b set b.status=org.meveo.model.billing.BatchEntityStatusEnum.CANCELED where b.id=:id " +
                        "and b.status=org.meveo.model.billing.BatchEntityStatusEnum.OPEN"),
        @NamedQuery(name = "BatchEntity.listBatchEntities", query = "SELECT b FROM BatchEntity b WHERE b.id in (:ids)")
})
public class BatchEntity extends EnableBusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * The job template classname
     */
    @Column(name = "target_job", length = 255)
    @Size(max = 255)
    private String targetJob;

    /**
     * The entity name for wallet operation
     */
    @Column(name = "target_entity", length = 255)
    @Size(max = 255)
    private String targetEntity;

    /**
     * Filtering option.
     */
    @Type(type = "json")
    @Column(name = "filters", columnDefinition = "jsonb")
    private Map<String, Object> filters;

    /**
     * if it's true, then an email is sent to the creator in the user’s language if available (or default, if not)
     */
    @Type(type = "numeric_boolean")
    @Column(name = "notify")
    private boolean notify;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchEntityStatusEnum status = BatchEntityStatusEnum.OPEN;

    /**
     * Job instance processing the batch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id")
    private JobInstance jobInstance;

    /**
     * Job execution result
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id")
    private JobExecutionResultImpl jobExecutionResult;

    /**
     * Gets targetJob.
     *
     * @return value of targetJob
     */
    public String getTargetJob() {
        return targetJob;
    }

    /**
     * Sets targetJob.
     *
     * @param targetJob value of targetJob
     */
    public void setTargetJob(String targetJob) {
        this.targetJob = targetJob;
    }

    /**
     * Gets targetEntity.
     *
     * @return value of targetEntity
     */
    public String getTargetEntity() {
        return targetEntity;
    }

    /**
     * Sets targetEntity.
     *
     * @param targetEntity value of targetEntity
     */
    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    /**
     * Gets filters.
     *
     * @return value of filters
     */
    public Map<String, Object> getFilters() {
        return filters;
    }

    /**
     * Sets filters.
     *
     * @param filters value of filters
     */
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    /**
     * Gets notify.
     *
     * @return value of notify
     */
    public boolean isNotify() {
        return notify;
    }

    /**
     * Sets notify.
     *
     * @param notify value of notify
     */
    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    /**
     * Gets status.
     *
     * @return value of status
     */
    public BatchEntityStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status value of status
     */
    public void setStatus(BatchEntityStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets jobInstance.
     *
     * @return value of jobInstance
     */
    public JobInstance getJobInstance() {
        return jobInstance;
    }

    /**
     * Sets jobInstance.
     *
     * @param jobInstance value of jobInstance
     */
    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

    /**
     * Gets jobExecutionResult.
     *
     * @return value of jobExecutionResult
     */
    public JobExecutionResultImpl getJobExecutionResult() {
        return jobExecutionResult;
    }

    /**
     * Sets jobExecutionResult.
     *
     * @param jobExecutionResult value of jobExecutionResult
     */
    public void setJobExecutionResult(JobExecutionResultImpl jobExecutionResult) {
        this.jobExecutionResult = jobExecutionResult;
    }
}
