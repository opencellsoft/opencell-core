package org.meveo.admin.job;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static javax.ejb.TransactionAttributeType.NEVER;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;
import static org.meveo.model.crm.custom.CustomFieldStorageTypeEnum.LIST;
import static org.meveo.model.crm.custom.CustomFieldTypeEnum.*;
import static org.meveo.model.crm.custom.CustomFieldTypeEnum.CHECKBOX_LIST;
import static org.meveo.model.jobs.MeveoJobCategoryEnum.INVOICING;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class XMLInvoiceGenerationJobV2 extends Job {

    @Inject
    private XMLInvoiceGenerationJobV2Bean xmlInvoiceGenerationJobBean;

    @Override
    @TransactionAttribute(NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance)
            throws BusinessException {
        xmlInvoiceGenerationJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return INVOICING;
    }

    @Override
    public Class getTargetEntityClass(JobInstance jobInstance) {
        return Invoice.class;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        final String APPLIES_TO = "JobInstance_XMLInvoiceGenerationJobV2";

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo(APPLIES_TO);
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo(APPLIES_TO);
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(LONG);
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(CF_WAITING_MILLIS, customFieldNbWaiting);

        CustomFieldTemplate customFieldInvToProcess = new CustomFieldTemplate();
        final String cfInvToProcessCode = "invoicesToProcess";

        customFieldInvToProcess.setCode(cfInvToProcessCode);
        customFieldInvToProcess.setAppliesTo(APPLIES_TO);
        customFieldInvToProcess.setActive(true);

        customFieldInvToProcess.setDescription(resourceMessages.getString("InvoicesToProcessEnum.label"));
        customFieldInvToProcess.setFieldType(CHECKBOX_LIST);
        customFieldInvToProcess.setStorageType(LIST);

        Map<String, String> invoicesStatusToProcessValues = stream(InvoiceStatusEnum.values())
                .collect(toMap(InvoiceStatusEnum::name, value -> resourceMessages.getString(value.getLabel())));
        customFieldInvToProcess.setListValues(invoicesStatusToProcessValues);
        customFieldInvToProcess.setDefaultValue(VALIDATED.name());

        customFieldInvToProcess.setGuiPosition("tab:Configuration:0;field:2");
        result.put(cfInvToProcessCode, customFieldInvToProcess);

        return result;
    }
}