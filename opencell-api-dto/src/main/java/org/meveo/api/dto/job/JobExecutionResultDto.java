package org.meveo.api.dto.job;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * Contains information about job execution status and history once job is completed
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "JobExecutionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultDto extends BaseDto {

    private static final long serialVersionUID = 5117909144385779437L;

    /**
     * Job execution result identifier
     */
    @XmlAttribute(required = true)
    private Long id;

    /**
     * Job instance identifier
     */
    private Long jobInstanceId;

    /**
     * Nodes that job is CURRENTLY running.
     */
    private String runningOnNodes;

    /**
     * Job start date
     */
    private Date startDate;

    /**
     * Job end date
     */
    private Date endDate;

    /**
     * Number of items to process
     */
    private long nbItemsToProcess;

    /**
     * Number of items that were processed correctly
     */
    @XmlElement(required = true)
    private long nbItemsCorrectlyProcessed;

    /**
     * Number of items that were processed with warning
     */
    @XmlElement(required = true)
    private long nbItemsProcessedWithWarning;

    /**
     * Number of items that were not processed due to some error
     */
    @XmlElement(required = true)
    private long nbItemsProcessedWithError;

    /**
     * Is job execution done - if False, job should be repeated again to finish processing
     */
    @XmlElement(required = true)
    private boolean done = true;

    /**
     * Jon execution report/summary
     */
    private String report;
    
    private String jobInstanceCode;

    public JobExecutionResultDto() {
    }

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
        jobInstanceCode = jobExecutionResult.getJobInstance().getCode();
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

    public long getNbItemsToProcess() {
        return nbItemsToProcess;
    }

    public void setNbItemsToProcess(long nbItemsToProcess) {
        this.nbItemsToProcess = nbItemsToProcess;
    }

    public long getNbItemsCorrectlyProcessed() {
        return nbItemsCorrectlyProcessed;
    }

    public void setNbItemsCorrectlyProcessed(long nbItemsCorrectlyProcessed) {
        this.nbItemsCorrectlyProcessed = nbItemsCorrectlyProcessed;
    }

    public long getNbItemsProcessedWithWarning() {
        return nbItemsProcessedWithWarning;
    }

    public void setNbItemsProcessedWithWarning(long nbItemsProcessedWithWarning) {
        this.nbItemsProcessedWithWarning = nbItemsProcessedWithWarning;
    }

    public long getNbItemsProcessedWithError() {
        return nbItemsProcessedWithError;
    }

    public void setNbItemsProcessedWithError(long nbItemsProcessedWithError) {
        this.nbItemsProcessedWithError = nbItemsProcessedWithError;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public String getRunningOnNodes() {
        return runningOnNodes;
    }

    public void setRunningOnNodes(String runningOnNodes) {
        this.runningOnNodes = runningOnNodes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "JobExecutionResultDto [id=" + id + ", jobInstanceId=" + jobInstanceId + ", runningOnNodes=" + runningOnNodes + ", startDate=" + startDate + ", endDate=" + endDate
                + ", nbItemsToProcess=" + nbItemsToProcess + ", nbItemsCorrectlyProcessed=" + nbItemsCorrectlyProcessed + ", nbItemsProcessedWithWarning="
                + nbItemsProcessedWithWarning + ", nbItemsProcessedWithError=" + nbItemsProcessedWithError + ", done=" + done + ", report=" + report + "]";
    }

    public String getJobInstanceCode() {
        return jobInstanceCode;
    }

    public void setJobInstanceCode(String jobInstanceCode) {
        this.jobInstanceCode = jobInstanceCode;
    }
}