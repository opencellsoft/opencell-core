package org.meveo.admin.job;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.jobs.MeveoJobCategoryEnum.DUNNING;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

@Stateless
public class TriggerCollectionPlanLevelsJob extends Job {

    @Inject
    private TriggerCollectionPlanLevelsJobBean collectionPlanLevelsJobBean;

    @Override
    @TransactionAttribute(REQUIRES_NEW)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        collectionPlanLevelsJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return DUNNING;
    }
}