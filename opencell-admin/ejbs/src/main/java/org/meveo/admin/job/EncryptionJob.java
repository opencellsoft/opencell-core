package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import static org.meveo.model.jobs.MeveoJobCategoryEnum.UTILS;

@Stateless
public class EncryptionJob extends Job {

    @Inject
    private EncryptionJobBean encryptionJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        encryptionJobBean.execute(result, jobInstance);

        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return UTILS;
    }
}
