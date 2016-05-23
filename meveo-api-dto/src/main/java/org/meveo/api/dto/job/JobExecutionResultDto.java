package org.meveo.api.dto.job;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.jobs.JobExecutionResult;

@XmlRootElement(name = "JobExecutionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultDto extends BaseDto {

	private static final long serialVersionUID = 5117909144385779437L;

	@XmlElement(required = false)
    private Date startDate;

    @XmlAttribute(required = false)
    private Date endDate;

    @XmlAttribute(required = false)
    private long nbItemsToProcess;

    @XmlElement(required = true)
    private long nbItemsCorrectlyProcessed;

    @XmlElement(required = true)
    private long nbItemsProcessedWithWarning;

    @XmlElement(required = true)
    private long nbItemsProcessedWithError;

    @XmlElement(required = true)
    private boolean done = true;

    public JobExecutionResultDto() {}

    public JobExecutionResultDto(JobExecutionResult jobExecutionResult) {
        this.startDate = jobExecutionResult.getStartDate();
        this.endDate = jobExecutionResult.getEndDate();
        this.nbItemsToProcess = jobExecutionResult.getNbItemsToProcess();
        this.nbItemsCorrectlyProcessed = jobExecutionResult.getNbItemsCorrectlyProcessed();
        this.nbItemsProcessedWithWarning = jobExecutionResult.getNbItemsProcessedWithWarning();
        this.nbItemsProcessedWithError = jobExecutionResult.getNbItemsProcessedWithError();
        this.done = jobExecutionResult.isDone();
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

	@Override
    public String toString() {
        return "JobExecutionResultDto [startDate=" + startDate + ", endDate=" + endDate + ", nbItemsToProcess=" + nbItemsToProcess 
        		+ ", nbItemsCorrectlyProcessed=" + nbItemsCorrectlyProcessed + ", nbItemsProcessedWithWarning=" + nbItemsProcessedWithWarning
                + ", nbItemsProcessedWithError=" + nbItemsProcessedWithError + ", nbItemsProcessedWithError=" + nbItemsProcessedWithError;
    }
	
}
