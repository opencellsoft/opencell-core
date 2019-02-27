package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.job.JobInstanceListDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class JobInstanceListResponseDto.
 * 
 * @author Adnane Boubia
 */
@XmlRootElement(name = "JobInstanceListResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2043156401122771487L;
	
	/** The job instances. */
    public JobInstanceListDto jobInstances;

    /**
     * Gets the job instances.
     *
     * @return the job instances
     */
    public JobInstanceListDto getJobInstances() {
        if (jobInstances == null) {
        	jobInstances = new JobInstanceListDto();
        }
        return jobInstances;
    }

    /**
     * Sets the job instances.
     *
     * @param job instances the new job instances
     */
    public void setJobInstances(JobInstanceListDto jobInstances) {
        this.jobInstances = jobInstances;
    }

    @Override
    public String toString() {
        return "ListJobInstanceResponseDto [jobInstances=" + jobInstances + ", toString()=" + super.toString() + "]";
    }}