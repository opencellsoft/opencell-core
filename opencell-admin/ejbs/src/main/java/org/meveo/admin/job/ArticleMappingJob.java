package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ArticleMappingJob extends Job {


    /**
     * Job bean
     */
    @Inject
    private ArticleMappingBean articleMappingBean;


    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        articleMappingBean.execute(result, jobInstance);
    }

    /**
     * Get job category
     * @return {@link MeveoJobCategoryEnum#UTILS}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }
}
