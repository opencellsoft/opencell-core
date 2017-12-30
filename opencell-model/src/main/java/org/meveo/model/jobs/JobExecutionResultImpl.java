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

@Entity
@Table(name = "job_execution")
@NotifiableEntity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "job_execution_seq"), })
public class JobExecutionResultImpl extends BaseEntity {
    private static final long serialVersionUID = 430457580612075457L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id")
    private JobInstance jobInstance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "nb_to_process")
    private long nbItemsToProcess;

    @Column(name = "nb_success")
    private long nbItemsCorrectlyProcessed;

    @Column(name = "nb_warning")
    private long nbItemsProcessedWithWarning;

    @Column(name = "nb_error")
    private long nbItemsProcessedWithError;

    /**
     * True if the job didn't detect anything else to do. If false the Jobservice will execute it again immediatly
     */
    @Type(type="numeric_boolean")
    @Column(name = "job_done")
    private boolean done = true;

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
    @Column(name = "report", columnDefinition="LONGTEXT")
    private String report;

    public synchronized void registerSucces() {
        nbItemsCorrectlyProcessed++;
    }

    public synchronized void registerWarning(Serializable identifier, String warning) {
        registerWarning(identifier + ": " + warning);
    }

    public synchronized void registerWarning(String warning) {
        warnings.add(warning);
        nbItemsProcessedWithWarning++;
    }

    public synchronized void registerError(Serializable identifier, String error) {
        registerError(identifier + ": " + error);
    }

    public synchronized void registerError(String error) {
        errors.add(error);
        nbItemsProcessedWithError++;
    }

    public synchronized void registerError() {        
        nbItemsProcessedWithError++;
    }
    public void close(String report) {
        this.report = report;
        this.endDate = new Date();
    }

    public void close() {
        this.endDate = new Date();
        this.addReport(getErrorsAString());
        this.addReport(getWarningAString());
    }

    // helper
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

    public static void updateFromInterface(JobExecutionResultImpl source, JobExecutionResultImpl result) {
        result.setEndDate(source.getEndDate());
        result.setStartDate(source.getStartDate());
        result.setErrors(source.getErrors());
        result.setNbItemsCorrectlyProcessed(source.getNbItemsCorrectlyProcessed());
        result.setNbItemsProcessedWithError(source.getNbItemsProcessedWithError());
        result.setNbItemsProcessedWithWarning(source.getNbItemsProcessedWithWarning());
        result.setNbItemsToProcess(source.getNbItemsToProcess());
        result.setReport(source.getReport());
        result.setWarnings(source.getWarnings());
        result.setDone(source.isDone());
        result.setId(source.getId());
    }

    // Getter & setters

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

    public void addNbItemsToProcess(long nbItemsToProcess) {
        this.nbItemsToProcess += nbItemsToProcess;
    }

    public long getNbItemsCorrectlyProcessed() {
        return nbItemsCorrectlyProcessed;
    }

    public void setNbItemsCorrectlyProcessed(long nbItemsCorrectlyProcessed) {
        this.nbItemsCorrectlyProcessed = nbItemsCorrectlyProcessed;
    }

    public void addNbItemsCorrectlyProcessed(long nbItemsCorrectlyProcessed) {
        this.nbItemsCorrectlyProcessed += nbItemsCorrectlyProcessed;
    }

    public long getNbItemsProcessedWithWarning() {
        return nbItemsProcessedWithWarning;
    }

    public void setNbItemsProcessedWithWarning(long nbItemsProcessedWithWarning) {
        this.nbItemsProcessedWithWarning = nbItemsProcessedWithWarning;
    }

    public void addNbItemsProcessedWithWarning(long nbItemsProcessedWithWarning) {
        this.nbItemsProcessedWithWarning += nbItemsProcessedWithWarning;
    }

    public long getNbItemsProcessedWithError() {
        return nbItemsProcessedWithError;
    }

    public void setNbItemsProcessedWithError(long nbItemsProcessedWithError) {
        this.nbItemsProcessedWithError = nbItemsProcessedWithError;
    }

    public void addNbItemsProcessedWithError(long nbItemsProcessedWithError) {
        this.nbItemsProcessedWithError += nbItemsProcessedWithError;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void addReport(String report) {
        if (!StringUtils.isBlank(report)) {
            this.report = (this.report == null ? "" : (this.report + " \n ")) + report;
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

    public JobInstance getJobInstance() {
        return jobInstance;
    }

    /**
     * @return the jobLauncherEnum
     */
    public JobLauncherEnum getJobLauncherEnum() {
        return jobLauncherEnum;
    }

    /**
     * @param jobLauncherEnum the jobLauncherEnum to set
     */
    public void setJobLauncherEnum(JobLauncherEnum jobLauncherEnum) {
        this.jobLauncherEnum = jobLauncherEnum;
    }

    public String getErrorsAString() {
        StringBuffer errorsBuffer = new StringBuffer();
        for (String error : errors) {
            errorsBuffer.append(error + "\n");
        }
        return errorsBuffer.toString();
    }

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
