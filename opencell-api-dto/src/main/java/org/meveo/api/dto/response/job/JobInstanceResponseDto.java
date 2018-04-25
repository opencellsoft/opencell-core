package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class JobInstanceResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "JobInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3392399387123725437L;

    /** Contains job instance information. */
    private JobInstanceDto jobInstanceDto;

    /**
     * Gets the job instance dto.
     *
     * @return the job instance dto
     */
    public JobInstanceDto getJobInstanceDto() {
        return jobInstanceDto;
    }

    /**
     * Sets the job instance dto.
     *
     * @param jobInstanceDto the new job instance dto
     */
    public void setJobInstanceDto(JobInstanceDto jobInstanceDto) {
        this.jobInstanceDto = jobInstanceDto;
    }

}
