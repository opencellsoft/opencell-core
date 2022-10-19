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

package org.meveo.admin.job.importexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.async.ImportAccountsAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.UnchckedThreadingExcpetion;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.Job;

/**
 * @author phung
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ImportAccountsJob extends Job {

    @Inject
    private ImportAccountsAsync importAccountsAsync;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        List<Future<String>> futures = new ArrayList<Future<String>>();

        MeveoUser lastCurrentUser = currentUser.unProxy();
        for (int i = 0; i < nbRuns.intValue(); i++) {
            futures.add(importAccountsAsync.launchAndForget(result, lastCurrentUser));
            if (i > 0) {
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                    throw new UnchckedThreadingExcpetion(e);
                }
            }
        }
        // Wait for all async methods to finish
        for (Future<String> future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
                throw new UnchckedThreadingExcpetion(e);

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                result.registerError(cause.getMessage());
                log.error("Failed to execute async method", cause);
            }
        }

        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.IMPORT_HIERARCHY;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo("JobInstance_ImportAccountsJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo("JobInstance_ImportAccountsJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        return result;
    }
}
