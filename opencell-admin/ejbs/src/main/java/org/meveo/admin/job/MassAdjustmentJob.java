package org.meveo.admin.job;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

/**
 * The Class MassAdjustmentJob launch adjustment invoices to a credit note invoice
 * @author Adil EL JAOUHARI
 *
 */
@Stateless
public class MassAdjustmentJob extends Job {

    @Inject
    private MassAdjustmentJobBean massAdjustmentJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
       return massAdjustmentJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.MASSADJUSTMENT;
    }

}