package org.meveo.admin.job;

import static org.meveo.model.jobs.MeveoJobCategoryEnum.ORDERING;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class OpenOrderStatusJob extends Job {

	@Inject
    private OpenOrderStatusJobBean openOrderStatusJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result,
                                             JobInstance jobInstance) throws BusinessException {
        openOrderStatusJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return ORDERING;
    }
}