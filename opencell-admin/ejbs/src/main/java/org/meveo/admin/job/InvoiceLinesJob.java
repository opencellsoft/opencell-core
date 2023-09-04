package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

@Stateless
public class InvoiceLinesJob extends Job {
	
	public static final String ONE_BILLING_ACCOUNT_PER_TRANSACTION ="JobInstance_InvoiceLinesJob_BillingAccountPerTransaction";
	public static final String MAX_RATED_TRANSACTIONS_PER_TRANSACTION ="JobInstance_InvoiceLinesJob_MaxRTsPerTransaction";
	public static final String MAX_INVOICE_LINES_PER_TRANSACTION ="JobInstance_InvoiceLinesJob_MaxLinesPerTransaction";
	public static final String MAX_RATED_TRANSACTIONS_PER_INVOICE_LINE ="JobInstance_InvoiceLinesJob_MaxRTsPerLine";
	
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