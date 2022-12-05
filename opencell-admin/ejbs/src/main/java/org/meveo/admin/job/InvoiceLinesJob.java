package org.meveo.admin.job;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

@Stateless
public class InvoiceLinesJob extends Job {

	public static final String  INVOICE_LINES_AGGREGATION_PER_UNIT_PRICE = "JobInstance_InvoiceLinesJob_AggregationPerUnitAmount";
	
	public static final String  INVOICE_LINES_IL_DATE_AGGREGATION_OPTIONS = "JobInstance_InvoiceLinesJob_ILDateAggregationOptions";
	
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
}