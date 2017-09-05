package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.jobs.JobCategoryEnum;

@XmlRootElement(name = "JobInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceDto extends BusinessDto {

    private static final long serialVersionUID = 5166093858617578774L;

    /**
     * Job category
     */
    @XmlElement(required = true)
    private JobCategoryEnum jobCategory;

    /**
     * Job template
     */
    @XmlAttribute(required = true)
    private String jobTemplate;

    /**
     * Following job to execute
     */
    private String followingJob;

    /**
     * Parameter to job execution
     */
    private String parameter;

    /**
     * Is job active
     */
    @XmlElement(required = true)
    private boolean active = false;

    /**
     * Custom fields
     */
    private CustomFieldsDto customFields;

    /**
     * Job scheduling timer code
     */
    @XmlAttribute(required = false)
    private String timerCode;

    public JobCategoryEnum getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(JobCategoryEnum jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getJobTemplate() {
        return jobTemplate;
    }

    public void setJobTemplate(String jobTemplate) {
        this.jobTemplate = jobTemplate;
    }

    public String getParameter() {
        return parameter;
    }

    public String getFollowingJob() {
        return followingJob;
    }

    public void setFollowingJob(String followingJob) {
        this.followingJob = followingJob;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getTimerCode() {
        return timerCode;
    }

    public void setTimerCode(String timerCode) {
        this.timerCode = timerCode;
    }

    @Override
    public String toString() {
        return String
            .format(
                "JobInstanceDto [code=%s, description=%s, jobCategory=%s, jobTemplate=%s, followingJob=%s,  parameter=%s, active=%s, customFields=%s, timerCode=%s]",
                getCode(), getDescription(), jobCategory, jobTemplate, followingJob, parameter, active, customFields, timerCode);
    }
}