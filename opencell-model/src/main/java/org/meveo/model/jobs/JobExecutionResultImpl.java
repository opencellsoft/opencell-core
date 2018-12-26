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
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "job_execution_seq"), })
@NamedQueries({ @NamedQuery(name = "JobExecutionResult.countHistoryToPurgeByDate", query = "select count(*) FROM JobExecutionResultImpl hist WHERE hist.startDate<=:date"),
        @NamedQuery(name = "JobExecutionResult.purgeHistoryByDate", query = "delete JobExecutionResultImpl hist WHERE hist.startDate<=:date"),
        @NamedQuery(name = "JobExecutionResult.countHistoryToPurgeByDateAndJobInstance", query = "select count(*) FROM JobExecutionResultImpl hist WHERE hist.startDate<=:date and hist.jobInstance=:jobInstance"),
        @NamedQuery(name = "JobExecutionResult.purgeHistoryByDateAndJobInstance", query = "delete JobExecutionResultImpl hist WHERE hist.startDate<=:date and hist.jobInstance=:jobInstance") })

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

    /**
     * True if the job finished processing completely. If false the Jobservice will execute it again immediately
     */
    @Type(type = "numeric_boolean")
    @Column(name = "job_done")
    private boolean done = true;

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
    @Column(name = "report", columnDefinition = "LONGTEXT")
    private String report;

    /**
     * Increment a count of successfully processed items
     */
    public synchronized void registerSucces() {
        nbItemsCorrectlyProcessed++;
    }

    /**
     * Increment a count of items processed with warning and log a warning
     * 
     * @param identifier Record identifier
     * @param warning Message to log
     */
    public synchronized void registerWarning(Serializable identifier, String warning) {
        registerWarning(identifier + ": " + warning);
    }

    /**
     * Increment a count of items processed with warning and log a warning
     * 
     * @param warning Message to log
     */
    public synchronized void registerWarning(String warning) {
        warnings.add(warning);
        nbItemsProcessedWithWarning++;
    }

    /**
     * Increment a count of items processed with error and log an error
     * 
     * @param identifier Record identifier
     * @param error Message to log
     */
    public synchronized void registerError(Serializable identifier, String error) {
        registerError(identifier + ": " + error);
    }

    /**
     * Increment a count of items processed with error and log an error
     * 
     * @param error Message to log
     */
    public synchronized void registerError(String error) {
        errors.add(error);
        nbItemsProcessedWithError++;
    }

    /**
     * Increment a count of items processed with error
     */
    public synchronized void registerError() {
        nbItemsProcessedWithError++;
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
        this.addReport(getErrorsAString());
        this.addReport(getWarningAString());
    }

    /**
     * Create a copy of job execution statistics
     * 
     * @param jobInstance Job instance
     * @param res Job execution statistics
     * @return A new JobExecutionResultImpl instance
     */
    public static JobExecutionResultImpl createFromInterface(JobInstance jobInstance, JobExecutionResultImpl res) {
        JobExecutionResultImpl result = new JobExecutionResultImpl();
        result.setJobInstance(jobInstance);
        result.setEndDate(res.getEndDate());
        result.setStartDate(res.getStartDate());
        result.setErrors(res.getErrors());
        result.setNbItemsCorrectlyProcessed(res.getNbItemsCorrectlyProcessed());
        result.setNbItemsProcessedWithError(res.getNbItemsProcessedWithError());
        result.setNbItemsProcessedWithWarning(res.getNbItemsProcessedWithWarning());
        result.setNbItemsToProcess(res.getNbItemsToProcess());
        result.setReport(res.getReport());
        result.setWarnings(res.getWarnings());
        result.setDone(res.isDone());
        result.setId(res.getId());
        return result;
    }

    /**
     * Update one JobExecutionResultImpl entity with data from another
     * 
     * @param source An entity to update from
     * @param target An entity to update
     */
    public static void updateFromInterface(JobExecutionResultImpl source, JobExecutionResultImpl target) {
        target.setEndDate(source.getEndDate());
        target.setStartDate(source.getStartDate());
        target.setErrors(source.getErrors());
        target.setNbItemsCorrectlyProcessed(source.getNbItemsCorrectlyProcessed());
        target.setNbItemsProcessedWithError(source.getNbItemsProcessedWithError());
        target.setNbItemsProcessedWithWarning(source.getNbItemsProcessedWithWarning());
        target.setNbItemsToProcess(source.getNbItemsToProcess());
        target.setReport(source.getReport());
        target.setWarnings(source.getWarnings());
        target.setDone(source.isDone());
        target.setId(source.getId());
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
    public void addNbItemsToProcess(long nbItemsToProcess) {
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
    public void addNbItemsCorrectlyProcessed(long incrementBy) {
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
    public void addNbItemsProcessedWithWarning(long incrementBy) {
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
    public void addNbItemsProcessedWithError(long incrementBy) {
        this.nbItemsProcessedWithError += incrementBy;
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
     * Append message to report
     * 
     * @param messageToAppend A message to append
     */

    public void addReport(String messageToAppend) {
        if (!StringUtils.isBlank(messageToAppend)) {
            this.report = (this.report == null ? "" : (this.report + " \n ")) + messageToAppend;
        }
    }

    /**
     * 
     * @return True if job finished processing all data completely
     */
    public boolean isDone() {
        return done;
    }

    /**
     * 
     * @param done True if job finished processing all data completely
     */
    public void setDone(boolean done) {
        this.done = done;
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
     * @return Errors as a string
     */
    public String getErrorsAString() {
        StringBuffer errorsBuffer = new StringBuffer();
        for (String error : errors) {
            errorsBuffer.append(error + "\n");
        }
        return errorsBuffer.toString();
    }

    /**
     * 
     * @return Warnings as a string
     */
    public String getWarningAString() {
        StringBuffer warningBuffer = new StringBuffer();
        for (String warning : warnings) {
            warningBuffer.append(warning + "\n");
        }
        return warningBuffer.toString();
    }

    @Override
    public String toString() {
        return "JobExecutionResultImpl [jobInstanceCode=" + (jobInstance == null ? null : jobInstance.getCode()) + ", startDate=" + startDate + ", endDate=" + endDate
                + ", nbItemsToProcess=" + nbItemsToProcess + ", nbItemsCorrectlyProcessed=" + nbItemsCorrectlyProcessed + ", nbItemsProcessedWithWarning="
                + nbItemsProcessedWithWarning + ", nbItemsProcessedWithError=" + nbItemsProcessedWithError + ", done=" + done + ", jobLauncherEnum=" + jobLauncherEnum
                + ", warnings=" + warnings + ", errors=" + errors + ", report=" + report + "]";
    }
}