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
public class PrepaidWalletMatchJob extends Job {

    @Inject
    private PrepaidWalletMatchJobBean prepaidWalletMatchJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
        prepaidWalletMatchJobBean.execute(timerEntity.getTimerInfo().getParametres(), result, currentUser);

    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.WALLET;
    }
}