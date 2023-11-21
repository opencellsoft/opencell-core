package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Job definition to mark Open Wallet operations to rerate.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Stateless
public class MarkWOToRerateJob extends Job {

    /**
     * Job bean
     */
    @Inject
    private MarkWOToRerateJobBean markWOToRerateJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        markWOToRerateJobBean.execute(result, jobInstance);
        return result;
    }

    /**
     * Get job category
     *
     * @return {@link MeveoJobCategoryEnum#RATING}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.RATING;
    }
}