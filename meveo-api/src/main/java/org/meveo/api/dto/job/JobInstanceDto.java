package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.jobs.JobInstance;

@XmlRootElement(name = "JobInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceDto extends BaseDto {

    private static final long serialVersionUID = 5166093858617578774L;

    @XmlElement(required = true)
    private String jobCategory;

    @XmlAttribute(required = true)
    private String jobTemplate;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute(required = false)
    private String description;

    @XmlElement(required = false)
    private String followingJob;;

    @XmlElement(required = false)
    private String parameter;

    @XmlElement(required = true)
    private boolean active = false;

    @XmlElement(required = true)
    private long userId;

    @XmlElement(required = false)
    private CustomFieldsDto customFields = new CustomFieldsDto();

    @XmlAttribute(required = false)
    private String timerCode;

    public JobInstanceDto() {
    }

    public JobInstanceDto(JobInstance jobInstance) {
        this.code = jobInstance.getCode();
        this.active = jobInstance.isActive();
        this.customFields = new CustomFieldsDto(jobInstance.getCustomFields());
        this.description = jobInstance.getDescription();
        if (jobInstance.getFollowingJob() != null) {
            this.followingJob = jobInstance.getFollowingJob().getCode();
        }
        this.jobCategory = jobInstance.getJobCategoryEnum().name();
        this.jobTemplate = jobInstance.getJobTemplate();
        this.parameter = jobInstance.getParametres();
        if (jobInstance.getTimerEntity() != null) {
            this.timerCode = jobInstance.getTimerEntity().getCode();
        }
        this.userId = jobInstance.getUserId();
    }

    /**
     * @return the jobCategory
     */
    public String getJobCategory() {
        return jobCategory;
    }

    /**
     * @param jobCategory the jobCategory to set
     */
    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    /**
     * @return the jobTemplate
     */
    public String getJobTemplate() {
        return jobTemplate;
    }

    /**
     * @param jobTemplate the jobTemplate to set
     */
    public void setJobTemplate(String jobTemplate) {
        this.jobTemplate = jobTemplate;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the followingJobs
     */

    /**
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    public String getFollowingJob() {
        return followingJob;
    }

    public void setFollowingJob(String followingJob) {
        this.followingJob = followingJob;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * @return the timerCode
     */
    public String getTimerCode() {
        return timerCode;
    }

    /**
     * @param timerCode the timerCode to set
     */
    public void setTimerCode(String timerCode) {
        this.timerCode = timerCode;
    }

    @Override
    public String toString() {
        return "JobInstanceDto [jobCategory=" + jobCategory + ", jobTemplate=" + jobTemplate + ", code=" + code + ", description=" + description + ", followingJob=" + followingJob
                + ", parameter=" + parameter + ", active=" + active + ", userId=" + userId + ", customFields=" + customFields + ", timerCode=" + timerCode + "]";
    }

}
