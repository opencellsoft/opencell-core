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

package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.job.JobExecutionErrorService;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BaseJobBean : Holding a common behaviors for all JoBbeans instances
 */
public abstract class BaseJobBean implements Serializable {

    private static final long serialVersionUID = 4892019854039929214L;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    protected JobExecutionService jobExecutionService;

    @Inject
    protected JobExecutionResultService jobExecutionResultService;

    @Inject
    protected JobExecutionErrorService jobExecutionErrorService;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    /** Logger. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Jobs requested to be stopped. Job identifier is a map key. A value of true indicated that job was requested to be be stopped.
     */
    protected static final Map<Long, Boolean> requestToStopJobs = new HashMap<Long, Boolean>();

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @param defaultValue Default value if no value found
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode, Object defaultValue) {
        Object value = getParamOrCFValue(jobInstance, cfCode);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }

    /**
     * Gets the Enum value from text.
     *
     * @param <T> an Enum status
     * @param jobInstance a job instance
     * @param clazz an enum class
     * @param cfCode a name of the enum
     * @return a list of an enum status
     */
    @SuppressWarnings("unchecked")
    protected <T extends Enum<T>> List<T> getTargetStatusList(JobInstance jobInstance, Class<T> clazz, String cfCode) {
        List<T> formattedStatus = new ArrayList<>();
        List<String> statusList = (List<String>) this.getParamOrCFValue(jobInstance, cfCode, new ArrayList<>());
        for (String status : statusList) {
            T statusEnum = Enum.valueOf(clazz, status.toUpperCase());
            formattedStatus.add(statusEnum);
        }
        return formattedStatus;
    }

    /**
     * Mark job, identified by a job instance as "requested to stop"
     * 
     * @param jobInstance Job instance (id) to stop
     */
    public static void markJobToStop(Long jobInstanceId) {
        requestToStopJobs.put(jobInstanceId, Boolean.TRUE);
    }

    /**
     * Was job requested to be stopped
     * 
     * @param jobInstanceId Job instance Id
     * @return True if job was requested to be stopped
     */
    public static boolean isJobRequestedToStop(Long jobInstanceId) {
        return Boolean.TRUE.equals(requestToStopJobs.get(jobInstanceId));
    }
}