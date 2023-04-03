package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class DuplicateRatedTransactionJob extends Job {

    @Inject
    private DuplicateRatedTransactionJobBean duplicateRatedTransactionJobBean;
    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        duplicateRatedTransactionJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, buildCF(CF_NB_RUNS, "jobExecution.nbRuns", CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", false, null, null));

        result.put(Job.CF_WAITING_MILLIS, buildCF(Job.CF_WAITING_MILLIS, "jobExecution.waitingMillis", CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null));

        return result;
    }

    private CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum type,
                                        String guiPosition, String defaultValue, boolean valueRequire, CustomFieldStorageTypeEnum cFSTEnum, String entityClazz) {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode(code);
        cft.setAppliesTo("JobInstance_DuplicateRatedTransactionJob");
        cft.setActive(true);
        cft.setDescription(resourceMessages.getString(description));
        cft.setFieldType(type);
        cft.setValueRequired(valueRequire);
        cft.setGuiPosition(guiPosition);
        if (defaultValue!= null) {
            cft.setDefaultValue(defaultValue);
        }
        if (cFSTEnum != null) {
            cft.setStorageType(cFSTEnum);
        }
        if (entityClazz != null) {
            cft.setEntityClazz(entityClazz);
        }
        return cft;
    }
}
