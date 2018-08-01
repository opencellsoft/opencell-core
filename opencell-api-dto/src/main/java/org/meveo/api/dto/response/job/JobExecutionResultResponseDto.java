package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class JobExecutionResultResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "JobExecutionResultResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3392399387123725437L;

    /** Contains job execution result information. */
    private JobExecutionResultDto jobExecutionResultDto;

    /**
     * Gets the job execution result dto.
     *
     * @return the job execution result dto
     */
    public JobExecutionResultDto getJobExecutionResultDto() {
        return jobExecutionResultDto;
    }

    /**
     * Sets the job execution result dto.
     *
     * @param jobExecutionResultDto the new job execution result dto
     */
    public void setJobExecutionResultDto(JobExecutionResultDto jobExecutionResultDto) {
        this.jobExecutionResultDto = jobExecutionResultDto;
    }

}
