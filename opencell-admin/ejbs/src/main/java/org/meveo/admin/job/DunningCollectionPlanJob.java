package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.job.Job;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class DunningCollectionPlanJob extends Job {

    @Inject
    private DunningCollectionPlanJobBean dunningCollectionPlanJobBean;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        checkActivateDunning(result);
        dunningCollectionPlanJobBean.execute(result, jobInstance);
        return result;
    }

    public void checkActivateDunning(JobExecutionResultImpl result) {
        FinanceSettings lastOne = financeSettingsService.findLastOne();
        if (lastOne != null && !lastOne.isActivateDunning()) {
            result.registerError("The action is not possible, GlobalSettings.activateDunning is disabled");
            throw new BusinessApiException("The action is not possible, GlobalSettings.activateDunning is disabled");
        }
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