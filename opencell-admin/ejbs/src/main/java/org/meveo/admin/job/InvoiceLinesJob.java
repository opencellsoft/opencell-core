package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;
import org.meveo.service.job.ScopedJob;

@Stateless
public class InvoiceLinesJob extends ScopedJob {

    public static final String CF_INVOICE_LINES_AGGREGATION_PER_UNIT_PRICE = "JobInstance_InvoiceLinesJob_AggregationPerUnitAmount";

    public static final String CF_INVOICE_LINES_IL_DATE_AGGREGATION_OPTIONS = "JobInstance_InvoiceLinesJob_ILDateAggregationOptions";

    public static final String CF_INVOICE_LINES_GROUP_BY_BA = "JobInstance_InvoiceLinesJob_BillingAccountPerTransaction";

    public static final String CF_INVOICE_LINES_NR_RTS_PER_TX = "JobInstance_InvoiceLinesJob_MaxRTsPerTransaction";

    public static final String CF_INVOICE_LINES_NR_ILS_PER_TX = "JobInstance_InvoiceLinesJob_MaxILsPerTransaction";

    public static final String CF_INVOICE_LINES_BR = "InvoiceLinesJob_billingRun";

    @Inject
    private InvoiceLinesJobBean invoiceLinesBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        invoiceLinesBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"), CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1",
            false, null, null, "JobInstance_InvoiceLinesJob"));
        result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null, "JobInstance_InvoiceLinesJob"));
        result.put(CF_INVOICE_LINES_GROUP_BY_BA, CustomFieldTemplateUtils.buildCF(CF_INVOICE_LINES_GROUP_BY_BA, resourceMessages.getString("jobExecution.ilJob.groupByBA"), CustomFieldTypeEnum.BOOLEAN,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:2", "false", true, null, null, "JobInstance_InvoiceLinesJob"));
        result.put(CF_INVOICE_LINES_NR_RTS_PER_TX, CustomFieldTemplateUtils.buildCF(CF_INVOICE_LINES_NR_RTS_PER_TX, resourceMessages.getString("jobExecution.ilJob.numberOfRTsPerTX"), CustomFieldTypeEnum.LONG,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:3", "1000000", true, null, null, "JobInstance_InvoiceLinesJob"));
        result.put(CF_INVOICE_LINES_NR_ILS_PER_TX, CustomFieldTemplateUtils.buildCF(CF_INVOICE_LINES_NR_ILS_PER_TX, resourceMessages.getString("jobExecution.ilJob.numberOfILsPerTX"), CustomFieldTypeEnum.LONG,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:4", "10000", true, null, null, "JobInstance_InvoiceLinesJob"));

        result.put(CF_INVOICE_LINES_BR, CustomFieldTemplateUtils.buildCF(CF_INVOICE_LINES_BR, resourceMessages.getString("jobExecution.ilJob.billingRuns"), CustomFieldTypeEnum.ENTITY,
            "tab:Configuration:0;fieldGroup:Filtering:2;field:1", null, false, CustomFieldStorageTypeEnum.LIST, BillingRun.class.getName(), "JobInstance_InvoiceLinesJob"));

        return result;
    }
}