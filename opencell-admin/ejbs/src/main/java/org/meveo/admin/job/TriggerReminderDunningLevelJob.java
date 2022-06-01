package org.meveo.admin.job;

import static javax.ejb.TransactionAttributeType.NEVER;
import static org.meveo.model.jobs.MeveoJobCategoryEnum.DUNNING;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.service.job.Job;
import org.meveo.service.settings.impl.GlobalSettingsService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

@Stateless
public class TriggerReminderDunningLevelJob extends Job {

    @Inject
    private TriggerReminderDunningLevelJobBean reminderDunningLevelJobBean;

    @Inject
    private GlobalSettingsService globalSettingsService;

    @Override
    @TransactionAttribute(NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        checkActivateDunning(result);
        reminderDunningLevelJobBean.execute(result, jobInstance);
        return result;
    }

    public void checkActivateDunning(JobExecutionResultImpl result) {
        GlobalSettings lastOne = globalSettingsService.findLastOne();
        if(lastOne != null && !lastOne.getActivateDunning()) {
            result.registerError("The action is not possible, GlobalSettings.activateDunning is disabled");
            throw new BusinessApiException("The action is not possible, GlobalSettings.activateDunning is disabled");
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return DUNNING;
    }
}