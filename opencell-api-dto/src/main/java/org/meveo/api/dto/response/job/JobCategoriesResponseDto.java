package org.meveo.api.dto.response.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.jobs.JobCategoryEnum;

/**
 * The Class JobCategoriesResponseDto.
 * 
 * @author akadid abdelmounaim
 */
@XmlRootElement(name = "JobCategoriesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobCategoriesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3392399387123725437L;

    /** Contains job categories. */
    private JobCategoryEnum[] jobCategories;

    /**
     * Gets the job categories.
     *
     * @return the job categories
     */
    public JobCategoryEnum[] getJobCategories() {
        return jobCategories;
    }

    /**
     * Sets the job categories.
     *
     * @param jobCategories the job categories
     */
    public void setJobCategories(JobCategoryEnum[] jobCategories) {
        this.jobCategories = jobCategories;
    }

}
