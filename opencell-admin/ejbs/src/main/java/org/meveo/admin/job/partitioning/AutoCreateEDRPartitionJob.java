package org.meveo.admin.job.partitioning;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class AutoCreateEDRPartitionJob extends Job {

    @Inject
    private AutoCreateEDRPartitionBean autoCreateEDRPartitionBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        autoCreateEDRPartitionBean.createNewEDRPartition(result);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

}
