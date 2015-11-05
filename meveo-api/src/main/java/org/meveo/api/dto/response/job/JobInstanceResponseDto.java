package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "JobInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceResponseDto extends BaseResponse{

	private static final long serialVersionUID = -3392399387123725437L;
	
	private JobInstanceDto jobInstanceDto;

	public JobInstanceDto getJobInstanceDto() {
		return jobInstanceDto;
	}

	public void setJobInstanceDto(JobInstanceDto jobInstanceDto) {
		this.jobInstanceDto = jobInstanceDto;
	}

}
