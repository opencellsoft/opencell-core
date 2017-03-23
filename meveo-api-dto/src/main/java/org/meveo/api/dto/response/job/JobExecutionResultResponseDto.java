package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "JobExecutionResultResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultResponseDto extends BaseResponse{

	private static final long serialVersionUID = -3392399387123725437L;
	
	/**
	 * Contains job execution result information
	 */
	private JobExecutionResultDto jobExecutionResultDto;

	public JobExecutionResultDto getJobExecutionResultDto() {
		return jobExecutionResultDto;
	}

	public void setJobExecutionResultDto(JobExecutionResultDto jobExecutionResultDto) {
		this.jobExecutionResultDto = jobExecutionResultDto;
	}

}
