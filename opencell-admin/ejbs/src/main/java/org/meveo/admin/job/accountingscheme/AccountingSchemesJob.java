package org.meveo.admin.job.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 13
 */
@Stateless
public class AccountingSchemesJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_AccountingSchemesJob";
    private static final String CF_ONLYCLOSEDPERIODS = "onlyClosedPeriods";

    @Inject
    private AccountingSchemesJobBean accountingSchemesJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        accountingSchemesJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.ACCOUNTING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:0", "1", APPLIES_TO_NAME));

        result.put(CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:1", "0", APPLIES_TO_NAME));

        result.put(CF_ONLYCLOSEDPERIODS, CustomFieldTemplateUtils.buildCF(CF_ONLYCLOSEDPERIODS, resourceMessages.getString("jobExecution.onlyClosedPeriods"),
                CustomFieldTypeEnum.BOOLEAN, "tab:Configuration:0;field:2", "false", APPLIES_TO_NAME));

        return result;
    }
}