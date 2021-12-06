package org.meveo.admin.job;

import static javax.ejb.TransactionAttributeType.NEVER;
import static org.meveo.model.jobs.MeveoJobCategoryEnum.DUNNING;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

@Stateless
public class TriggerReminderDunningLevelJob extends Job {

    @Inject
    private TriggerReminderDunningLevelJobBean reminderDunningLevelJobBean;

    @Override
    @TransactionAttribute(NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        reminderDunningLevelJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return DUNNING;
    }
}