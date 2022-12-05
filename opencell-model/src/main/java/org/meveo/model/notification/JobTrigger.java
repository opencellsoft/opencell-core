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

package org.meveo.model.notification;

import java.util.HashMap;
import java.util.Map;

import org.meveo.model.ModuleItem;
import org.meveo.model.jobs.JobInstance;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Notification to trigger Job execution
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ModuleItem
@Table(name = "adm_notif_job")
public class JobTrigger extends Notification {

    private static final long serialVersionUID = -8948201462950547554L;

    /**
     * Job execution parameters
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_job_params")
    private Map<String, String> jobParams = new HashMap<String, String>();

    /**
     * Job to execute
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id")
    private JobInstance jobInstance;

    public JobTrigger() {

    }

    /**
     * @return the jobParams
     */
    public Map<String, String> getJobParams() {
        return jobParams;
    }

    /**
     * @param jobParams the jobParams to set
     */
    public void setJobParams(Map<String, String> jobParams) {
        this.jobParams = jobParams;
    }

    /**
     * @return the jobInstance
     */
    public JobInstance getJobInstance() {
        return jobInstance;
    }

    /**
     * @param jobInstance the jobInstance to set
     */
    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

}