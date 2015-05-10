package org.meveo.admin.job;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class UsageRatingJob extends Job {

    @Inject
    private UsageRatingJobBean usageRatingJobBean;

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(TimerEntity timerEntity, User currentUser) {
        super.execute(timerEntity, currentUser);
    }

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
        usageRatingJobBean.execute(result, currentUser,timerEntity);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.RATING;
    }
}