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

package org.meveo.api.dto.job;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.IEntityDto;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * Contains information about job execution status and history once job is completed.
 *
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "JobExecutionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultDto extends BaseEntityDto implements IEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5117909144385779437L;

    /** Job execution result identifier. */
    @XmlAttribute(required = true)
    private Long id;

    /** Job instance identifier. */
    private Long jobInstanceId;

    /**
     * Nodes that job is CURRENTLY running.
     */
    private String runningOnNodes;

    /** Job start date. */
    private Date startDate;

    /** Job end date. */
    private Date endDate;

    /** Number of items to process. */
    private long nbItemsToProcess;

    /** Number of items that were processed correctly. */
    @XmlElement(required = true)
    private long nbItemsCorrectlyProcessed;

    /** Number of items that were processed with warning. */
    @XmlElement(required = true)
    private long nbItemsProcessedWithWarning;

    /** Number of items that were not processed due to some error. */
    @XmlElement(required = true)
    private long nbItemsProcessedWithError;

    /** Is job execution done - if False, job should be repeated again to finish processing. */
    @XmlElement(required = true)
    private boolean done = true;

    /** Jon execution report/summary. */
    private String report;

    /**
     * JobInstance code.
     */
    private String jobInstanceCode;

    /**
     * Instantiates a new job execution result dto.
     */
    public JobExecutionResultDto() {
    }

    /**
     * Instantiates a new job execution result dto.
     *
     * @param jobExecutionResult the job execution result
     */
    public JobExecutionResultDto(JobExecutionResultImpl jobExecutionResult) {
        this.id = jobExecutionResult.getId();
        this.jobInstanceId = jobExecutionResult.getJobInstance().getId();
        this.startDate = jobExecutionResult.getStartDate();
        this.endDate = jobExecutionResult.getEndDate();
        this.nbItemsToProcess = jobExecutionResult.getNbItemsToProcess();
        this.nbItemsCorrectlyProcessed = jobExecutionResult.getNbItemsCorrectlyProcessed();
        this.nbItemsProcessedWithWarning = jobExecutionResult.getNbItemsProcessedWithWarning();
        this.nbItemsProcessedWithError = jobExecutionResult.getNbItemsProcessedWithError();
        this.done = jobExecutionResult.isDone();
        this.report = jobExecutionResult.getReport();
        this.jobInstanceCode = jobExecutionResult.getJobInstance().getCode();
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the nb items to process.
     *
     * @return the nb items to process
     */
    public long getNbItemsToProcess() {
        return nbItemsToProcess;
    }

    /**
     * Sets the nb items to process.
     *
     * @param nbItemsToProcess the new nb items to process
     */
    public void setNbItemsToProcess(long nbItemsToProcess) {
        this.nbItemsToProcess = nbItemsToProcess;
    }

    /**
     * Gets the nb items correctly processed.
     *
     * @return the nb items correctly processed
     */
    public long getNbItemsCorrectlyProcessed() {
        return nbItemsCorrectlyProcessed;
    }

    /**
     * Sets the nb items correctly processed.
     *
     * @param nbItemsCorrectlyProcessed the new nb items correctly processed
     */
    public void setNbItemsCorrectlyProcessed(long nbItemsCorrectlyProcessed) {
        this.nbItemsCorrectlyProcessed = nbItemsCorrectlyProcessed;
    }

    /**
     * Gets the nb items processed with warning.
     *
     * @return the nb items processed with warning
     */
    public long getNbItemsProcessedWithWarning() {
        return nbItemsProcessedWithWarning;
    }

    /**
     * Sets the nb items processed with warning.
     *
     * @param nbItemsProcessedWithWarning the new nb items processed with warning
     */
    public void setNbItemsProcessedWithWarning(long nbItemsProcessedWithWarning) {
        this.nbItemsProcessedWithWarning = nbItemsProcessedWithWarning;
    }

    /**
     * Gets the nb items processed with error.
     *
     * @return the nb items processed with error
     */
    public long getNbItemsProcessedWithError() {
        return nbItemsProcessedWithError;
    }

    /**
     * Sets the nb items processed with error.
     *
     * @param nbItemsProcessedWithError the new nb items processed with error
     */
    public void setNbItemsProcessedWithError(long nbItemsProcessedWithError) {
        this.nbItemsProcessedWithError = nbItemsProcessedWithError;
    }

    /**
     * Checks if is done.
     *
     * @return true, if is done
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Sets the done.
     *
     * @param done the new done
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Gets the report.
     *
     * @return the report
     */
    public String getReport() {
        return report;
    }

    /**
     * Sets the report.
     *
     * @param report the new report
     */
    public void setReport(String report) {
        this.report = report;
    }

    /**
     * Gets the job instance id.
     *
     * @return the job instance id
     */
    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    /**
     * Sets the job instance id.
     *
     * @param jobInstanceId the new job instance id
     */
    public void setJobInstanceId(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    /**
     * Gets the running on nodes.
     *
     * @return the running on nodes
     */
    public String getRunningOnNodes() {
        return runningOnNodes;
    }

    /**
     * Sets the running on nodes.
     *
     * @param runningOnNodes the new running on nodes
     */
    public void setRunningOnNodes(String runningOnNodes) {
        this.runningOnNodes = runningOnNodes;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the job instance code.
     *
     * @return the job instance code
     */
    public String getJobInstanceCode() {
        return jobInstanceCode;
    }

    /**
     * Sets the job instance code.
     *
     * @param jobInstanceCode the new job instance code
     */
    public void setJobInstanceCode(String jobInstanceCode) {
        this.jobInstanceCode = jobInstanceCode;
    }
    
    @Override
    public String toString() {
        return "JobExecutionResultDto [id=" + id + ", jobInstanceId=" + jobInstanceId + ", runningOnNodes=" + runningOnNodes + ", startDate=" + startDate + ", endDate=" + endDate
                + ", nbItemsToProcess=" + nbItemsToProcess + ", nbItemsCorrectlyProcessed=" + nbItemsCorrectlyProcessed + ", nbItemsProcessedWithWarning="
                + nbItemsProcessedWithWarning + ", nbItemsProcessedWithError=" + nbItemsProcessedWithError + ", done=" + done + ", report=" + report + "]";
    }
}