package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetJobTriggerResponseDto.
 *
 * @author Tyshan Shi
 */
@XmlRootElement(name = "GetJobTriggerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetJobTriggerResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The job trigger dto. */
    private JobTriggerDto jobTriggerDto;

    /**
     * Gets the job trigger dto.
     *
     * @return the job trigger dto
     */
    public JobTriggerDto getJobTriggerDto() {
        return jobTriggerDto;
    }

    /**
     * Sets the job trigger dto.
     *
     * @param jobTriggerDto the new job trigger dto
     */
    public void setJobTriggerDto(JobTriggerDto jobTriggerDto) {
        this.jobTriggerDto = jobTriggerDto;
    }

}
