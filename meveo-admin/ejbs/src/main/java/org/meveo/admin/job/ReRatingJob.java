package org.meveo.admin.job;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class ReRatingJob extends Job {

    @Inject
    private ReRatingJobBean reRatingJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
        reRatingJobBean.execute(result, currentUser, "justPrice".equalsIgnoreCase(jobInstance.getParametres()));
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.RATING;
    }
}