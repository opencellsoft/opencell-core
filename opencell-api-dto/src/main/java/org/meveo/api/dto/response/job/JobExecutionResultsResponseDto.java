package org.meveo.api.dto.response.job;

import static java.lang.String.format;

import org.meveo.api.dto.job.JobExecutionResultsDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultsResponseDto extends SearchResponse {

    private static final long serialVersionUID = -2043156401123771499L;

    private JobExecutionResultsDto jobExecutionResult;

    public JobExecutionResultsDto getJobExecutionResult() {
        return jobExecutionResult;
    }

    public void setJobExecutionResult(JobExecutionResultsDto jobExecutionResult) {
        this.jobExecutionResult = jobExecutionResult;
    }

    @Override
    public String toString() {
        return format("JobExecutionResultListResponseDto[jobExecutionResult=%stoString()=%s]", jobExecutionResult, super.toString());
    }
}
