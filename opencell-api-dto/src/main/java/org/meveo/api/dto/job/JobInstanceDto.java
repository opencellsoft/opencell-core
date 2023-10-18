/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobClusterBehaviorEnum;
import org.meveo.model.jobs.JobInstance;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class JobInstanceDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "JobInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5166093858617578774L;

    /** Job category. */
    @XmlElement(required = true)
    private JobCategoryEnum jobCategory;

    /** Job template. */
    @XmlAttribute(required = true)
    @Schema(description = "Job template", required = true)
    private String jobTemplate;

    /** Following job to execute. */
    @Schema(description = "Following job to execute")
    private String followingJob;

    /** Parameter to job execution. */
    @Schema(description = "Parameter to job execution")
    private String parameter;

    /**
     * Is job active. A negative of Disabled. Deprecated in 5.0.1. Use Disabled field instead.
     * 
     */
    @Deprecated
    @Schema(description = "Is job active. A negative of Disabled. Deprecated in 5.0.1. Use Disabled field instead", deprecated = true)
    private Boolean active;

    /** Custom fields. */
    private CustomFieldsDto customFields;

    /** Job scheduling timer code. */
    @XmlAttribute(required = false)
    @Schema(description = "Job scheduling timer code")
    private String timerCode;

    /**
     * What cluster nodes job could/should run on. A comma separated list of custer nodes. A job can/will be run on any node if value is null.
     */
    @Schema(description = "What cluster nodes job could/should run on. A comma separated list of custer nodes. A job can/will be run on any node if value is null")
    private String runOnNodes;

    /**
     * Can job be run in parallel on several cluster nodes. Value of True indicates that job can be run on a single node at a time.
     */
    @Schema(description = "Can job be run in parallel on several cluster nodes. Value of True indicates that job can be run on a single node at a time. Deprecated. Use clusterBehavior instead.")
    @Deprecated
    private Boolean limitToSingleNode;

    @Schema(description = "Job execution behavior when running in a clustered environment")
    private JobClusterBehaviorEnum clusterBehavior;

    /**
     * Whether a verbose error log will be kept.
     */
    @Schema(description = "Whether a verbose error log will be kept")
    private Boolean verboseReport;

    /**
     * How often (in seconds) the job progress should be stored to DB
     */
    @Schema(description = "How often (in seconds) the job progress should be stored to DB")
    private Integer jobStatusReportFrequency;

    /**
     * Instantiate a new JobInstance DTO
     */
    public JobInstanceDto() {
    }

    /**
     * Convert JobInstance entity to DTO including its custom field values
     * 
     * @param jobInstance JobInstance entity
     * @param customFieldInstances Custom field values
     */
    public JobInstanceDto(JobInstance jobInstance, CustomFieldsDto customFieldInstances) {
        super(jobInstance);

        setActive(jobInstance.isActive());

        setJobCategory(jobInstance.getJobCategoryEnum());
        setJobTemplate(jobInstance.getJobTemplate());
        setParameter(jobInstance.getParametres());

        if (jobInstance.getTimerEntity() != null) {
            setTimerCode(jobInstance.getTimerEntity().getCode());
        }

        setRunOnNodes(jobInstance.getRunOnNodes());
        setLimitToSingleNode(jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.LIMIT_TO_SINGLE_NODE);

        setCustomFields(customFieldInstances);

        if (jobInstance.getFollowingJob() != null) {
            setFollowingJob(jobInstance.getFollowingJob().getCode());
        }
        verboseReport = jobInstance.isVerboseReport();
        jobStatusReportFrequency = jobInstance.getJobStatusReportFrequency();
    }

    /**
     * Gets the job category.
     *
     * @return the job category
     */
    public JobCategoryEnum getJobCategory() {
        return jobCategory;
    }

    /**
     * Sets the job category.
     *
     * @param jobCategory the new job category
     */
    public void setJobCategory(JobCategoryEnum jobCategory) {
        this.jobCategory = jobCategory;
    }

    /**
     * Gets the job template.
     *
     * @return the job template
     */
    public String getJobTemplate() {
        return jobTemplate;
    }

    /**
     * Sets the job template.
     *
     * @param jobTemplate the new job template
     */
    public void setJobTemplate(String jobTemplate) {
        this.jobTemplate = jobTemplate;
    }

    /**
     * Gets the parameter.
     *
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Gets the following job.
     *
     * @return the following job
     */
    public String getFollowingJob() {
        return followingJob;
    }

    /**
     * Sets the following job.
     *
     * @param followingJob the new following job
     */
    public void setFollowingJob(String followingJob) {
        this.followingJob = followingJob;
    }

    /**
     * Sets the parameter.
     *
     * @param parameter the new parameter
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active the new active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the timer code.
     *
     * @return the timer code
     */
    public String getTimerCode() {
        return timerCode;
    }

    /**
     * Sets the timer code.
     *
     * @param timerCode the new timer code
     */
    public void setTimerCode(String timerCode) {
        this.timerCode = timerCode;
    }

    /**
     * Gets the run on nodes.
     *
     * @return the run on nodes
     */
    public String getRunOnNodes() {
        return runOnNodes;
    }

    /**
     * Sets the run on nodes.
     *
     * @param runOnNodes the new run on nodes
     */
    public void setRunOnNodes(String runOnNodes) {
        this.runOnNodes = runOnNodes;
    }

    /**
     * Gets the limit to single node.
     *
     * @return the limit to single node
     */
    public Boolean getLimitToSingleNode() {
        return limitToSingleNode;
    }

    /**
     * Sets the limit to single node.
     *
     * @param limitToSingleNode the new limit to single node
     */
    public void setLimitToSingleNode(Boolean limitToSingleNode) {
        this.limitToSingleNode = limitToSingleNode;
    }

    @Override
    public String toString() {
        return "JobInstanceDto [jobCategory=" + jobCategory + ", jobTemplate=" + jobTemplate + ", followingJob=" + followingJob + ", parameter=" + parameter + ", active=" + active + ", customFields=" + customFields
                + ", timerCode=" + timerCode + ", runOnNodes=" + runOnNodes + ", limitToSingleNode=" + limitToSingleNode + "]";
    }

    public Boolean getVerboseReport() {
        return verboseReport;
    }

    public void setVerboseReport(Boolean verboseReport) {
        this.verboseReport = verboseReport;
    }

    /**
     * @return How often (in seconds) the job progress should be stored to DB
     */
    public Integer getJobStatusReportFrequency() {
        return jobStatusReportFrequency;
    }

    /**
     * @param jobStatusReportFrequency How often (in seconds) the job progress should be stored to DB
     */
    public void setJobStatusReportFrequency(Integer jobStatusReportFrequency) {
        this.jobStatusReportFrequency = jobStatusReportFrequency;
    }

    /**
     * @return Job execution behavior when running in a clustered environment
     */
    public JobClusterBehaviorEnum getClusterBehavior() {
        return clusterBehavior;
    }

    /**
     * @param clusterBehavior Job execution behavior when running in a clustered environment
     */
    public void setClusterBehavior(JobClusterBehaviorEnum clusterBehavior) {
        this.clusterBehavior = clusterBehavior;
    }
}