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

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Job template for APIv2 permissions synchronization
 *
 * @author Mounir BOUKAYOUA
 * @since 10.X
 */
@Stateless
public class APIv2PermissionsSyncJob  extends Job {

    /**
     * Job bean
     */
    @Inject
    private APIv2PermissionsSyncJobBean apIv2PermissionsSyncJobBean;

    /**
     * Job execute method
     * @param result Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Business exception
     */
    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        apIv2PermissionsSyncJobBean.execute(result,  jobInstance);
        return result;
    }

    /**
     * Get job category
     * @return {@link MeveoJobCategoryEnum#UTILS}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }
}