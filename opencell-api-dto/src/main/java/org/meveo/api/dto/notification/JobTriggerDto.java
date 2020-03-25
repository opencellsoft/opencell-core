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

package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;

/**
 * The Class JobTriggerDto.
 *
 * @author Tyshan Shi
 */
@XmlRootElement(name = "JobTrigger")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobTriggerDto extends NotificationDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The job params. */
    private Map<String, String> jobParams = new HashMap<String, String>();

    /** The job instance. */
    @XmlElement(required = true)
    private String jobInstance;

    /**
     * Gets the job params.
     *
     * @return the job params
     */
    public Map<String, String> getJobParams() {
        return jobParams;
    }

    /**
     * Sets the job params.
     *
     * @param jobParams the job params
     */
    public void setJobParams(Map<String, String> jobParams) {
        this.jobParams = jobParams;
    }

    /**
     * Gets the job instance.
     *
     * @return the job instance
     */
    public String getJobInstance() {
        return jobInstance;
    }

    /**
     * Sets the job instance.
     *
     * @param jobInstance the new job instance
     */
    public void setJobInstance(String jobInstance) {
        this.jobInstance = jobInstance;
    }

    /**
     * Instantiates a new job trigger dto.
     */
    public JobTriggerDto() {

    }

    /**
     * Instantiates a new job trigger dto.
     *
     * @param jobTrigger the job trigger
     */
    public JobTriggerDto(JobTrigger jobTrigger) {
        super((Notification) jobTrigger);
        if (jobTrigger.getJobParams() != null) {
            jobParams.putAll(jobTrigger.getJobParams());
        }
        if (jobTrigger.getJobInstance() != null) {
            jobInstance = jobTrigger.getJobInstance().getCode();
        }
    }

    @Override
    public String toString() {
        return "JobTriggerDto [jobParams=" + jobParams + ", jobInstance=" + jobInstance + "]";
    }

}