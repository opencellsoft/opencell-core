package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Tyshan Shi
 **/
@XmlRootElement(name = "GetJobTriggerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetJobTriggerResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JobTriggerDto jobTriggerDto;
	public JobTriggerDto getJobTriggerDto() {
		return jobTriggerDto;
	}
	public void setJobTriggerDto(JobTriggerDto jobTriggerDto) {
		this.jobTriggerDto = jobTriggerDto;
	}

}
