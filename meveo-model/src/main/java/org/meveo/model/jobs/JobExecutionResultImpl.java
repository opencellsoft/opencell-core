package org.meveo.model.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "JOB_EXECUTION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "JOB_EXECUTION_SEQ")
public class JobExecutionResultImpl extends BaseEntity implements
		JobExecutionResult {
	private static final long serialVersionUID = 430457580612075457L;

	@Column(name = "JOB_NAME")
	private String jobName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "NB_TO_PROCESS")
	private long nbItemsToProcess = -1;

	@Column(name = "NB_SUCCESS")
	private long nbItemsCorrectlyProcessed;

	@Column(name = "NB_WARNING")
	private long nbItemsProcessedWithWarning;

	@Column(name = "NB_ERROR")
	private long nbItemsProcessedWithError;

	@Column(name = "JOB_DONE")
	private boolean done = true;

	@Transient
	private List<String> warnings = new ArrayList<String>();

	@Transient
	private List<String> errors = new ArrayList<String>();

	@Column(name = "REPORT")
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
        String errorTxt = getErrorsAString();
        setReport(errorTxt == null ? "" : errorTxt.substring(0, errorTxt.length() < 256 ? errorTxt.length() : 255));
    }

	public void close(String report) {
		this.report = report;
		this.endDate = new Date();
	}
	
	public void close() {
	    this.endDate = new Date();
    }

	// helper
	public static JobExecutionResultImpl createFromInterface(String jobName,
			JobExecutionResult res) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		result.setJobName(jobName);
		result.setEndDate(res.getEndDate());
		result.setStartDate(res.getStartDate());
		result.setErrors(res.getErrors());
		result.setNbItemsCorrectlyProcessed(res.getNbItemsCorrectlyProcessed());
		result.setNbItemsProcessedWithError(res.getNbItemsProcessedWithError());
		result.setNbItemsProcessedWithWarning(res
				.getNbItemsProcessedWithWarning());
		result.setNbItemsToProcess(res.getNbItemsToProcess());
		result.setReport(res.getReport());
		result.setWarnings(res.getWarnings());
		result.setDone(res.isDone());
		return result;
	}

	// Getter & setters

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
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

}
