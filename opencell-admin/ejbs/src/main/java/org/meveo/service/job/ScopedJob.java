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
 * @since 16.0.0
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

}
