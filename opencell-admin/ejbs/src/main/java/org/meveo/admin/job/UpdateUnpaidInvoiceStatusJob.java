package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * A Job to update unpaid invoice status
 * @author akadid abdelmounaim
 * @lastModifiedVersion 8.0
 */
@Stateless
public class UpdateUnpaidInvoiceStatusJob extends Job {

    @Inject
    UpdateUnpaidInvoiceStatusJobBean unpaidInvoiceStatusJobBean;
    /**
     * The actual job execution logic implementation.
     *
     * @param result      Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
    	unpaidInvoiceStatusJobBean.execute(result, jobInstance);
    }

    /**
     * @return job category enum
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.INVOICING;
    }
}
