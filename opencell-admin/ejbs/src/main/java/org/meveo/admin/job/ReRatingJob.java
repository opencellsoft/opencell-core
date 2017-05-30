package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Stateless
public class ReRatingJob extends Job {

    @Inject
    private ReRatingJobBean reRatingJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        reRatingJobBean.execute(result, "justPrice".equalsIgnoreCase(jobInstance.getParametres()));
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.RATING;
    }
}