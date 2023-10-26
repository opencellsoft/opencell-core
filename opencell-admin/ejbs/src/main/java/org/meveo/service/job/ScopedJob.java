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

package org.meveo.service.job;

import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;

import java.util.Date;

/**
 * Interface that allow to set a limit job executions by :
 * - a volume of items to process  (ex: 10M items)
 * - a maximum run duration (ex: 3h duration)
 * - a time limit (ex: run until 03:00)
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
public abstract class ScopedJob extends Job {

    /**
     * Custom field for a maximum number of items to process.
     */
    public static final String CF_JOB_ITEMS_LIMIT = "jobItemsLimit";

    /**
     * Custom field for a maximum run duration in minutes.
     */
    public static final String CF_JOB_DURATION_LIMIT = "jobDurationLimit";

    /**
     * Custom field for a maximum time at which the job must stop.
     */
    public static final String CF_JOB_TIME_LIMIT = "jobTimeLimit";

    /**
     * Gets the job items limit CF value.
     *
     * @param jobInstance the job instance
     * @return job items limit CF value
     */
    public Integer getJobItemsLimit(JobInstance jobInstance) {
        return (Integer) this.getParamOrCFValue(jobInstance, CF_JOB_ITEMS_LIMIT);
    }

    /**
     * Gets the job duration limit CF value.
     *
     * @param jobInstance the job instance
     * @return job duration limit CF value
     */
    protected Integer getJobDurationLimit(JobInstance jobInstance) {
        return (Integer) this.getParamOrCFValue(jobInstance, CF_JOB_DURATION_LIMIT);
    }

    /**
     * Gets the job time limit CF value.
     *
     * @param jobInstance the job instance
     * @return job time limit CF value
     */
    protected Date getJobTimeLimit(JobInstance jobInstance) {
        return DateUtils.parseDateWithPattern((String) this.getParamOrCFValue(jobInstance, CF_JOB_TIME_LIMIT), "HH24:MI");
    }

    /**
     * Check if the current job instance have the items limit CF value.
     *
     * @param jobInstance the job instance
     * @return true if the current job instance have the items limit CF value.
     */
    protected boolean hasJobItemsLimit(JobInstance jobInstance) {
        Integer jobItemsLimit = getJobItemsLimit(jobInstance);
        return (jobItemsLimit != null && jobItemsLimit > 0);
    }

    /**
     * Check if the current job instance have the duration limit CF value.
     *
     * @param jobInstance the job instance
     * @return true if the current job instance have the duration limit CF value.
     */
    protected boolean hasJobDurationLimit(JobInstance jobInstance) {
        Integer jobItemsLimit = getJobDurationLimit(jobInstance);
        return (jobItemsLimit != null && jobItemsLimit > 0);
    }

    /**
     * Check if the current job instance have the time limit CF value.
     *
     * @param jobInstance the job instance
     * @return true if the current job instance have the time limit CF value.
     */
    protected boolean hasJobTimeLimit(JobInstance jobInstance) {
        Date jobTimeLimit = getJobTimeLimit(jobInstance);
        return (jobTimeLimit != null);
    }

    protected void checkJobItemsLimitReached(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        Integer jobItemsLimit = getJobItemsLimit(jobInstance);
        if (jobItemsLimit != null && jobItemsLimit > 0 && jobItemsLimit >= jobExecutionResult.getNbItemsProcessed()) {
            jobExecutionService.stopJob(jobInstance);
        }
    }

    protected long checkJobDurationLimitReached(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        Integer jobDurationLimit = getJobDurationLimit(jobInstance);
        Date startDate = jobExecutionResult.getStartDate();
        Date currentDate = new Date();
        long duration = ((currentDate.getTime() / (60 * 1000)) - (startDate.getTime() / (60 * 1000)));
        if (jobDurationLimit != null && jobDurationLimit > 0 && jobDurationLimit >= duration) {
            jobExecutionService.stopJob(jobInstance);
        }
        return (duration - jobDurationLimit) * 60;
    }

    protected long checkJobTimeLimitReached(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        Date jobTimeLimit = getJobTimeLimit(jobInstance);
        Date endDate = jobExecutionResult.getStartDate();
        endDate = DateUtils.setHourToDate(endDate, jobTimeLimit.getHours());
        endDate = DateUtils.setMinuteToDate(endDate, jobTimeLimit.getMinutes());
        Date currentDate = new Date();

        if (jobTimeLimit != null && currentDate.compareTo(endDate) > 1) {
            jobExecutionService.stopJob(jobInstance);
        }
        return (endDate.getTime() - currentDate.getTime()) / 1000;
    }
}
