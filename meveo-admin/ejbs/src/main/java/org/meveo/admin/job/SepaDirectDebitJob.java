package org.meveo.admin.job;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class SepaDirectDebitJob extends Job {

    @Inject
    private SepaDirectDebitJobBean sepaDirectDebitJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
        sepaDirectDebitJobBean.execute(result, timerEntity.getTimerInfo().getParametres(), currentUser);
    }

    // public Collection<Timer> getTimers() {
    // return timerService.getTimers();
    // }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }
}