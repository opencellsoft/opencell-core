package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.job.Job;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.jobs.MeveoJobCategoryEnum.DUNNING;

@Stateless
public class TriggerCollectionPlanLevelsJob extends Job {

    @Inject
    private TriggerCollectionPlanLevelsJobBean collectionPlanLevelsJobBean;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Override
    @TransactionAttribute(REQUIRES_NEW)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        checkActivateDunning(result);
        collectionPlanLevelsJobBean.execute(result, jobInstance);
        return result;
    }

    public void checkActivateDunning(JobExecutionResultImpl result) {
        FinanceSettings lastOne = financeSettingsService.getFinanceSetting();
        if (lastOne != null && !lastOne.isActivateDunning()) {
            result.registerError("The action is not possible, GlobalSettings.activateDunning is disabled");
            throw new BusinessApiException("The action is not possible, GlobalSettings.activateDunning is disabled");
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return DUNNING;
    }
}