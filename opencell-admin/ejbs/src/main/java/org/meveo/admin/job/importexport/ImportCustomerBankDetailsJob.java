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

import org.meveo.admin.async.ImportCustomerBankDetailsAsync;
import org.meveo.admin.async.ImportSubscriptionsAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.Job;

@Stateless
public class ImportCustomerBankDetailsJob extends Job {

    @Inject
    private ImportCustomerBankDetailsAsync importCustomerBankDetailsAsync;

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
            futures.add(importCustomerBankDetailsAsync.launchAndForget(result, lastCurrentUser));
            if (i > 0) {
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }
        // Wait for all async methods to finish
        for (Future<String> future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
                // It was cancelled from outside - no interest

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
        customFieldNbRuns.setAppliesTo("JobInstance_ImportCustomerBankDetailsJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo("JobInstance_ImportCustomerBankDetailsJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        return result;
    }
}