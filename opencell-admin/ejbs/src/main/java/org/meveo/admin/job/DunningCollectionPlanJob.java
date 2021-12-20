package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class DunningCollectionPlanJob extends Job {

    @Inject
    private DunningCollectionPlanJobBean dunningCollectionPlanJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        dunningCollectionPlanJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.DUNNING;
    }

    @Override
    public Class getTargetEntityClass(JobInstance jobInstance) {
        return DunningCollectionPlan.class;
    }
}