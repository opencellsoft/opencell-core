package org.meveo.api.dto.job;

import static java.lang.String.format;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultsDto implements Serializable {

    private static final long serialVersionUID = -8442798673237108841L;

    private List<JobExecutionResultDto> jobExecutionResults;

    private Long totalNumberOfRecords;

    public JobExecutionResultsDto() {
    }

    public JobExecutionResultsDto(List<JobExecutionResultDto> jobExecutionResults) {
        this.jobExecutionResults = jobExecutionResults;
    }

    public List<JobExecutionResultDto> getJobExecutionResults() {
        return jobExecutionResults;
    }

    public void setJobExecutionResults(List<JobExecutionResultDto> jobExecutionResults) {
        this.jobExecutionResults = jobExecutionResults;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

    @Override
    public String toString() {
        return format("JobExecutionResultListDto{jobExecutionResults=%s}", jobExecutionResults);
    }
}
