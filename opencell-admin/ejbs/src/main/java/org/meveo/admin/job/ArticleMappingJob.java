package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.ScopedJob;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ArticleMappingJob extends ScopedJob {

    /**
     * Job bean
     */
    @Inject
    private ArticleMappingJobBean articleMappingJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        articleMappingJobBean.execute(result, jobInstance);
        return result;
    }

    /**
     * Get job category
     * 
     * @return {@link MeveoJobCategoryEnum#UTILS}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }
}