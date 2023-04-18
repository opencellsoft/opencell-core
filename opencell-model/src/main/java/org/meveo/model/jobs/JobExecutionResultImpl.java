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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.NotifiableEntity;

/**
 * Job execution statistics
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "job_execution")
@NotifiableEntity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "job_execution_seq"), })
@NamedQueries({ @NamedQuery(name = "JobExecutionResult.countHistoryToPurgeByDate", query = "select count(*) FROM JobExecutionResultImpl hist WHERE hist.startDate<=:date"),
        @NamedQuery(name = "JobExecutionResult.purgeHistoryByDate", query = "delete JobExecutionResultImpl hist WHERE hist.startDate<=:date"),
        @NamedQuery(name = "JobExecutionResult.countHistoryToPurgeByDateAndJobInstance", query = "select count(*) FROM JobExecutionResultImpl hist WHERE hist.startDate<=:date and hist.jobInstance=:jobInstance"),
        @NamedQuery(name = "JobExecutionResult.purgeHistoryByDateAndJobInstance", query = "delete JobExecutionResultImpl hist WHERE hist.startDate<=:date and hist.jobInstance=:jobInstance"),
        @NamedQuery(name = "JobExecutionResult.updateProgress", query = "update JobExecutionResultImpl set endDate=:endDate, nbItemsToProcess=:nbItemsToProcess, nbItemsCorrectlyProcessed=:nbItemsCorrectlyProcessed, nbItemsProcessedWithError=:nbItemsProcessedWithError, nbItemsProcessedWithWarning=:nbItemsProcessedWithWarning, report=:report, status=:status where id=:id"),
        @NamedQuery(name = "JobExecutionResult.cancelAllRunningJobs", query = "update JobExecutionResultImpl je set je.status='CANCELLED', je.endDate=NOW(), je.report='job cancelled due to the server was shutdown in the middle of job execution' where je.status = 'RUNNING'") })

public class JobExecutionResultImpl extends BaseEntity {
    private static final long serialVersionUID = 430457580612075457L;

    /**
     * Job instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id")
    private JobInstance jobInstance;

    /**
     * Job execution start time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate = new Date();

    /**
     * Job execution end time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Number of items to process
     */
    @Column(name = "nb_to_process")
    private long nbItemsToProcess;

    /**
     * Number of items processed successfully
     */
    @Column(name = "nb_success")
    private long nbItemsCorrectlyProcessed;

    /**
     * Number of items processed with warning
     */
    @Column(name = "nb_warning")
    private long nbItemsProcessedWithWarning;

    /**
     * Number of items processed with error
     */
    @Column(name = "nb_error")
    private long nbItemsProcessedWithError;

    @Column(name = "parent_job_result_id")
    private Long parentJobExecutionResult;

    @OneToMany(mappedBy = "parentJobExecutionResult", fetch = FetchType.LAZY)
    private List<JobExecutionResultImpl> workerJobExecutionResults = new ArrayList<>();

    /**
     * Job execution status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", length = 14)
    private JobExecutionResultStatusEnum status;

    /**
     * How job was launched
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_launcher")
    private JobLauncherEnum jobLauncherEnum;

    /**
     * Null if warnings are available somewhere else (for example in a file)
     */
    @Transient
    private List<String> warnings = new ArrayList<String>();

    /**
     * Null if errors are available somewhere else (for example in a file)
     */
    @Transient
    private List<String> errors = new ArrayList<String>();

    /**
     * General report displayed in GUI, put here info that do not fit other places
     */
    @Type(type = "longText")
    @Column(name = "report")
    private String report;

    /**
     * Indicates that job has not completed fully - there might be more data to process
     */
    @Transient
    private boolean moreToProcess = false;

    @Transient
    private Date cumulativeEndDate;

    @Transient
    private long cumulativeNbItemsCorrectlyProcessed = -1L;

    @Transient
    private long cumulativeNbItemsProcessedWithWarning = -1L;

    @Transient
    private long cumulativeNbItemsProcessedWithError = -1L;

    @Transient
    private JobExecutionResultStatusEnum cumulativeStatus;

    /**
     * Constructor
     */
    public JobExecutionResultImpl() {

    }

    /**
     * Constructor
     * 
     * @param jobInstance Job instance
     */
    public JobExecutionResultImpl(JobInstance jobInstance, JobLauncherEnum jobLauncher) {
        this.jobInstance = jobInstance;
        this.status = JobExecutionResultStatusEnum.RUNNING;
        this.startDate = new Date();
        this.jobLauncherEnum = jobLauncher;
    }

    /**
     * Increment a count of successfully processed items
     * 
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerSucces() {
        return registerSucces(1);
    }

    /**
     * Increment a count of successfully processed items
     * 
     * @param nrToAdd Number of items successfully processed
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerSucces(int nrToAdd) {
        nbItemsCorrectlyProcessed = nbItemsCorrectlyProcessed + nrToAdd;
        return nbItemsCorrectlyProcessed + nbItemsProcessedWithError + nbItemsProcessedWithWarning;
    }

    /**
     * Decrement a count of successfully processed items
     */
    public synchronized void unRegisterSucces() {
        nbItemsCorrectlyProcessed--;
    }

    /**
     * Increment a count of items processed with warning and log a warning
     * 
     * @param identifier Record identifier
     * @param warning Message to log
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerWarning(Serializable identifier, String warning) {
        return registerWarning(identifier + ": " + warning);
    }

    /**
     * Increment a count of items processed with warning and log a warning
     * 
     * @param warning Message to log
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerWarning(String warning) {
        if (jobInstance.isVerboseReport() && !StringUtils.isBlank(warning)) {
            addReport(warning);
            warnings.add(warning);
        }

        nbItemsProcessedWithWarning++;

        return nbItemsCorrectlyProcessed + nbItemsProcessedWithError + nbItemsProcessedWithWarning;
    }

    /**
     * Increment a count of items processed with error and log an error
     * 
     * @param identifier Record identifier
     * @param error Message to log
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerError(Serializable identifier, String error) {
        return registerError(identifier + ": " + error);
    }

    /**
     * Increment a count of items processed with error and log an error
     * 
     * @param error Message to log
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerError(String error) {
        if (jobInstance.isVerboseReport() && !StringUtils.isBlank(error)) {
            addReport(error);
            errors.add(error);
        }
        nbItemsProcessedWithError++;

        return nbItemsCorrectlyProcessed + nbItemsProcessedWithError + nbItemsProcessedWithWarning;
    }

    /**
     * Increment a count of items processed with error
     * 
     * @return A total number of processed items, successful or failed
     */
    public synchronized long registerError() {
        nbItemsProcessedWithError++;
        return nbItemsCorrectlyProcessed + nbItemsProcessedWithError + nbItemsProcessedWithWarning;
    }

    /**
     * Override report message and mark job execution end time
     * 
     * @param report Report message
     */
    public void close(String report) {
        this.report = report;
        this.endDate = new Date();
    }

    /**
     * Compile report message by concatenating error and warning messages and mark job execution end time
     */
    public void close() {
        this.endDate = new Date();
    }

    /**
     * @return Job execution start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 
     * @param startDate Job execution start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return Job execution end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate Job execution end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return Number of items to process
     */
    public long getNbItemsToProcess() {
        return nbItemsToProcess;
    }

    /**
     * 
     * @param nbItemsToProcess Number of items to process
     */
    public void setNbItemsToProcess(long nbItemsToProcess) {
        this.nbItemsToProcess = nbItemsToProcess;
    }

    /**
     * Increment number of items to process
     * 
     * @param nbItemsToProcess Number to increment by
     */
    public synchronized void addNbItemsToProcess(long nbItemsToProcess) {
        this.nbItemsToProcess += nbItemsToProcess;
    }

    /**
     * @return Number of items processed successfully
     * 
     */
    public long getNbItemsCorrectlyProcessed() {
        return nbItemsCorrectlyProcessed;
    }

    /**
     * @param nbItemsCorrectlyProcessed Number of items processed successfully
     */
    public void setNbItemsCorrectlyProcessed(long nbItemsCorrectlyProcessed) {
        this.nbItemsCorrectlyProcessed = nbItemsCorrectlyProcessed;
    }

    /**
     * Increment number of items processed successfully
     * 
     * @param incrementBy Number to increment by
     */
    public synchronized void addNbItemsCorrectlyProcessed(long incrementBy) {
        this.nbItemsCorrectlyProcessed += incrementBy;
    }

    /**
     * @return Number of items processed with warning
     */
    public long getNbItemsProcessedWithWarning() {
        return nbItemsProcessedWithWarning;
    }

    /**
     * @param nbItemsProcessedWithWarning Number of items processed with warning
     */
    public void setNbItemsProcessedWithWarning(long nbItemsProcessedWithWarning) {
        this.nbItemsProcessedWithWarning = nbItemsProcessedWithWarning;
    }

    /**
     * Increment number of items processed with warning
     * 
     * @param incrementBy Number to increment by
     */
    public synchronized void addNbItemsProcessedWithWarning(long incrementBy) {
        this.nbItemsProcessedWithWarning += incrementBy;
    }

    /**
     * @return Number of items processed with error
     */
    public long getNbItemsProcessedWithError() {
        return nbItemsProcessedWithError;
    }

    /**
     * @param nbItemsProcessedWithError Number of items processed with error
     */
    public void setNbItemsProcessedWithError(long nbItemsProcessedWithError) {
        this.nbItemsProcessedWithError = nbItemsProcessedWithError;
    }

    /**
     * Increment number of items processed with warning
     * 
     * @param incrementBy Number to increment by
     */
    public synchronized void addNbItemsProcessedWithError(long incrementBy) {
        this.nbItemsProcessedWithError += incrementBy;
    }

    /**
     * Get a total number of items processed.
     * 
     * @return A sum of items processed correctly, with error and with warning.
     */
    public long getNbItemsProcessed() {
        return nbItemsCorrectlyProcessed + nbItemsProcessedWithError + nbItemsProcessedWithWarning;
    }

    /**
     * 
     * @return A list of warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * 
     * @param warnings A list of warnings
     */
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    /**
     * 
     * @return A list of errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * 
     * @param errors A list of errors
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * 
     * @return Report message
     */
    public String getReport() {
        return report;
    }

    /**
     * 
     * @param report Report message
     */
    public void setReport(String report) {
        this.report = report;
    }

    /**
     * Increment a count of items processed with error and log an error always, irrelevant of verbose report flag.
     * 
     * @param error Message to log
     */
    public synchronized void addErrorReport(String error) {
        if (!StringUtils.isBlank(error)) {
            addReport(error);
            errors.add(error);
        }
        nbItemsProcessedWithError++;
    }

    /**
     * Append message to report
     * 
     * @param messageToAppend A message to append
     */
    public synchronized void addReport(String messageToAppend) {
        this.report = (this.report == null ? "" : (this.report + " \n ")) + messageToAppend;
    }

    /**
     * @return Job execution status
     */
    public JobExecutionResultStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Job execution status
     */
    public void setStatus(JobExecutionResultStatusEnum status) {
        this.status = status;
    }

    /**
     * 
     * @param jobInstance Related job instance
     */
    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

    /**
     * 
     * @return Related job instance
     */
    public JobInstance getJobInstance() {
        return jobInstance;
    }

    /**
     * @return The way job was launched
     */
    public JobLauncherEnum getJobLauncherEnum() {
        return jobLauncherEnum;
    }

    /**
     * @param jobLauncherEnum The way job was launched
     */
    public void setJobLauncherEnum(JobLauncherEnum jobLauncherEnum) {
        this.jobLauncherEnum = jobLauncherEnum;
    }

    /**
     * @return Indicates that job has not completed fully - there might be more data to process
     */
    public boolean isMoreToProcess() {
        return moreToProcess;
    }

    /**
     * @param moreToProcess Indicates that job has not completed fully - there might be more data to process
     */
    public void setMoreToProcess(boolean moreToProcess) {
        this.moreToProcess = moreToProcess;
    }

    /**
     * @return Parent job execution result
     */
    public Long getParentJobExecutionResult() {
        return parentJobExecutionResult;
    }

    /**
     * @param parentJobExecutionResult Parent job execution result
     */
    public void setParentJobExecutionResult(Long parentJobExecutionResult) {
        this.parentJobExecutionResult = parentJobExecutionResult;
    }

    /**
     * @return Execution results from worker jobs in other cluster nodes
     */
    public List<JobExecutionResultImpl> getWorkerJobExecutionResults() {
        return workerJobExecutionResults;
    }

    /**
     * @return All execution results - main plus worker job/thread execution results
     */
    public List<JobExecutionResultImpl> getCumulativeJobExecutionResults() {
        ArrayList<JobExecutionResultImpl> allResults = new ArrayList<JobExecutionResultImpl>();
        allResults.add(this);
        allResults.addAll(workerJobExecutionResults);

        return allResults;
    }

    /**
     * @param workerJobExecutionResults Execution results from worker jobs in other cluster nodes
     */
    public void setWorkerJobExecutionResults(List<JobExecutionResultImpl> workerJobExecutionResults) {
        this.workerJobExecutionResults = workerJobExecutionResults;
    }

    public Date getCumulativeStartDate() {
        return startDate;
    }

    public Date getCumulativeEndDate() {
        if (cumulativeEndDate == null) {
            cumulativeEndDate = endDate;

            if (endDate != null) {
                for (JobExecutionResultImpl workerJob : workerJobExecutionResults) {
                    if (workerJob.getEndDate() == null) {
                        return null;
                    } else if (cumulativeEndDate.before(workerJob.getEndDate())) {
                        cumulativeEndDate = workerJob.getEndDate();
                    }
                }
            }
        }
        return cumulativeEndDate;
    }

    /**
     * Get a total number of items correctly processed between the main and other worker jobs/threads
     * 
     * @return A total number of items correctly processed
     */
    public long getCumulativeNbItemsCorrectlyProcessed() {
        if (cumulativeNbItemsCorrectlyProcessed == -1L) {
            cumulativeNbItemsCorrectlyProcessed = nbItemsCorrectlyProcessed;

            for (JobExecutionResultImpl workerJob : workerJobExecutionResults) {
                cumulativeNbItemsCorrectlyProcessed = cumulativeNbItemsCorrectlyProcessed + workerJob.getNbItemsCorrectlyProcessed();
            }
        }

        return cumulativeNbItemsCorrectlyProcessed;
    }

    /**
     * Get a total number of items processed with warning between the main and other worker jobs/threads
     * 
     * @return A total number of items processed with warning
     */
    public long getCumulativeNbItemsProcessedWithWarning() {
        if (cumulativeNbItemsProcessedWithWarning == -1L) {
            cumulativeNbItemsProcessedWithWarning = nbItemsProcessedWithWarning;

            for (JobExecutionResultImpl workerJob : workerJobExecutionResults) {
                cumulativeNbItemsProcessedWithWarning = cumulativeNbItemsProcessedWithWarning + workerJob.getNbItemsProcessedWithWarning();
            }
        }
        return cumulativeNbItemsProcessedWithWarning;
    }

    /**
     * Get a total number of items processed with error between the main and other worker jobs/threads
     * 
     * @return A total number of items processed with error
     */
    public long getCumulativeNbItemsProcessedWithError() {
        cumulativeNbItemsProcessedWithError = nbItemsProcessedWithError;

        for (JobExecutionResultImpl workerJob : workerJobExecutionResults) {
            cumulativeNbItemsProcessedWithError = cumulativeNbItemsProcessedWithError + workerJob.getNbItemsProcessedWithError();
        }

        return cumulativeNbItemsProcessedWithError;
    }

    /**
     * Get a total number of items processed between the main and other worker jobs/threads
     * 
     * @return A total number of items processed
     */
    public long getCumulativeNbItemsProcessed() {
        return getCumulativeNbItemsCorrectlyProcessed() + getCumulativeNbItemsProcessedWithWarning() + getCumulativeNbItemsProcessedWithError();
    }

    /**
     * @return Job execution status
     */
    public JobExecutionResultStatusEnum getCumulativeStatus() {
        cumulativeStatus = status;

        for (JobExecutionResultImpl workerJob : workerJobExecutionResults) {
            cumulativeStatus = cumulativeStatus.getStatusWithHigherPriority(workerJob.getStatus());
        }

        return cumulativeStatus;
    }

    @Override
    public String toString() {
        return "JobExecutionResultImpl [jobInstanceCode=" + (jobInstance == null ? null : jobInstance.getCode()) + ", startDate=" + startDate + ", endDate=" + endDate + ", nbItemsToProcess=" + nbItemsToProcess
                + ", nbItemsCorrectlyProcessed=" + nbItemsCorrectlyProcessed + ", nbItemsProcessedWithWarning=" + nbItemsProcessedWithWarning + ", nbItemsProcessedWithError=" + nbItemsProcessedWithError + ", status="
                + status + ", jobLauncherEnum=" + jobLauncherEnum + ", report=" + report + "]";
    }
}