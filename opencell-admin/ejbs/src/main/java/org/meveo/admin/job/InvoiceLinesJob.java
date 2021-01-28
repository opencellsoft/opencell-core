package org.meveo.admin.job;

import static org.meveo.model.jobs.MeveoJobCategoryEnum.UTILS;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class InvoiceLinesJob extends Job {

    @Inject
    private InvoiceLinesJobBean invoiceLinesBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        invoiceLinesBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return UTILS;
    }
}