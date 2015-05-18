package org.meveo.admin.job.importexport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.async.ImportAccountsAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class ImportAccountsJob extends Job {

    @Inject
    private ImportAccountsAsync importAccountsAsync;

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {

        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            try {
                nbRuns = timerEntity.getLongCustomValue("ImportAccountsJob_nbRuns").longValue();
                waitingMillis = timerEntity.getLongCustomValue("ImportAccountsJob_waitingMillis").longValue();
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                log.warn("Cant get customFields for " + timerEntity.getJobName());
            }

            List<Future<String>> futures = new ArrayList<Future<String>>();
            for (int i = 0; i < nbRuns.intValue(); i++) {
                futures.add(importAccountsAsync.launchAndForget(result, currentUser));
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

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

        } catch (Exception e) {
            log.error("Failed to import accounts", e);
            result.registerError(e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }

    @Override
    public List<CustomFieldTemplate> getCustomFields(User currentUser) {
        List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("ImportAccountsJob_nbRuns");
        customFieldNbRuns.setAccountLevel(AccountLevelEnum.TIMER);
        customFieldNbRuns.setActive(true);
        Auditable audit = new Auditable();
        audit.setCreated(new Date());
        audit.setCreator(currentUser);
        customFieldNbRuns.setAuditable(audit);
        customFieldNbRuns.setProvider(currentUser.getProvider());
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setLongValue(new Long(1));
        customFieldNbRuns.setValueRequired(false);
        result.add(customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("ImportAccountsJob_waitingMillis");
        customFieldNbWaiting.setAccountLevel(AccountLevelEnum.TIMER);
        customFieldNbWaiting.setActive(true);
        Auditable audit2 = new Auditable();
        audit2.setCreated(new Date());
        audit2.setCreator(currentUser);
        customFieldNbWaiting.setAuditable(audit2);
        customFieldNbWaiting.setProvider(currentUser.getProvider());
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setLongValue(new Long(0));
        customFieldNbWaiting.setValueRequired(false);
        result.add(customFieldNbWaiting);

        return result;
    }

}