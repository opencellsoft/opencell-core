package org.meveo.admin.matching;

import org.meveo.admin.exception.BusinessException;
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
 * @since V14
 */
@Stateless
public class AssignMatchingCodeToOldEntriesJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_AssignMatchingCodeToOldEntriesJob";

    @Inject
    private AssignMatchingCodeToOldEntriesJobBean assignMatchingCodeToOldEntriesJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        assignMatchingCodeToOldEntriesJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.ACCOUNTING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:0", "1"));

        result.put(CF_WAITING_MILLIS, buildCF(CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:1", "0"));

        return result;
    }

    private CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum type,
                                        String guiPosition, String defaultValue) {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode(code);
        cft.setAppliesTo(APPLIES_TO_NAME);
        cft.setActive(true);
        cft.setDescription(description);
        cft.setFieldType(type);
        cft.setValueRequired(false);
        cft.setGuiPosition(guiPosition);
        cft.setDefaultValue(defaultValue);
        return cft;
    }
}
