package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HORRI on 07/01/2019.
 */
@Stateless
public class SendInvoiceJob extends Job {

    @Inject
    SendInvoiceJobBean sendInvoiceJobBean;
    /**
     * The actual job execution logic implementation.
     *
     * @param result      Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        sendInvoiceJobBean.execute(result, jobInstance);
    }

    /**
     * @return job category enum
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate sendDraft = new CustomFieldTemplate();
        sendDraft.setCode("sendDraft");
        sendDraft.setAppliesTo("JOB_SendInvoiceJob");
        sendDraft.setActive(true);
        sendDraft.setDescription(resourceMessages.getString("jobExecution.sendDraft"));
        sendDraft.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        sendDraft.setValueRequired(false);
        result.put("sendDraft", sendDraft);

        CustomFieldTemplate overrideEmailEl = new CustomFieldTemplate();
        overrideEmailEl.setCode("overrideEmailEl");
        overrideEmailEl.setAppliesTo("JOB_SendInvoiceJob");
        overrideEmailEl.setActive(true);
        overrideEmailEl.setDescription(resourceMessages.getString("jobExecution.overrideEmailEl"));
        overrideEmailEl.setFieldType(CustomFieldTypeEnum.STRING);
        overrideEmailEl.setValueRequired(false);
        overrideEmailEl.setMaxValue(Long.MAX_VALUE);
        result.put("overrideEmailEl", overrideEmailEl);

        return result;
    }
}
