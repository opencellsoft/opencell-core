package org.meveo.admin.job.importexport;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Stateless
public class ExportCustomersJob extends Job {

    @Inject
    private ExportCustomersJobBean exportCustomersJobBean;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        exportCustomersJobBean.execute(result, jobInstance.getParametres());
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }
}
