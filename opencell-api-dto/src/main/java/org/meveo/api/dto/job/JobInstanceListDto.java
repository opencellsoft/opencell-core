package org.meveo.api.dto.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class JobInstanceListDto.
 * 
 * @author Adnane Boubia
 */
@XmlRootElement(name = "JobInstanceListDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceListDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8442798673237108841L;

	/** The list size. */
    private int listSize;

    /** The job instance. */
    private List<JobInstanceDto> jobInstance;

    /**
     * Gets the job instances.
     *
     * @return the job instance
     */
    public List<JobInstanceDto> getJobInstances() {
        if (jobInstance == null) {
        	jobInstance = new ArrayList<JobInstanceDto>();
        }

        return jobInstance;
    }

    /**
     * Sets the job instances.
     *
     * @param job instance the new job instances
     */
    public void setJobInstances(List<JobInstanceDto> jobInstance) {
        this.jobInstance = jobInstance;
    }

    /**
     * Gets the list size.
     *
     * @return the list size
     */
    public int getListSize() {
        return listSize;
    }

    /**
     * Sets the list size.
     *
     * @param listSize the new list size
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "JobInstanceDto [JobInstance=" + jobInstance + "]";
    }


}