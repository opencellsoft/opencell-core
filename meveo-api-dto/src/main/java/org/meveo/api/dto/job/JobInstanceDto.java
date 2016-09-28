package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.jobs.JobCategoryEnum;

@XmlRootElement(name = "JobInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceDto extends BaseDto {

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
     * Cod
     */
    @XmlAttribute(required = true)
    private String code;

    /**
     * Description
     */
    @XmlAttribute()
    private String description;

    /**
     * Use nextJob instead
     */
    @Deprecated
    private String followingJob;

    /**
     * Following job to execute
     */
    private JobInstanceDto nextJob;

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
    private CustomFieldsDto customFields = new CustomFieldsDto();

    /**
     * Job scheduling timer code. Use Timer instead
     */
    @Deprecated
    @XmlAttribute(required = false)
    private String timerCode;

    /**
     * Timer for job scheduling
     */
    private TimerEntityDto timer;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public JobInstanceDto getNextJob() {
        return nextJob;
    }

    public void setNextJob(JobInstanceDto nextJob) {
        this.nextJob = nextJob;
    }

    public TimerEntityDto getTimer() {
        return timer;
    }

    public void setTimer(TimerEntityDto timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return String
            .format(
                "JobInstanceDto [code=%s, description=%s, jobCategory=%s, jobTemplate=%s, followingJob=%s, nextJob=%s, parameter=%s, active=%s, customFields=%s, timerCode=%s, timer=%s]",
                code, description, jobCategory, jobTemplate, followingJob, nextJob, parameter, active, customFields, timerCode, timer);
    }
}