package org.meveo.admin.job.dwh;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
/**
 * This job is made to create MeasuredValue of some MeasurableQuantity whose code is given as parameter
 * The JPA query to execute is stored in the MeasurableQuantity, and we assume it returns
 * a list of (Date measureDate, Long value)
 * each result is used to create a MeasuredValue
 */
public class DWHQueryJob extends Job {

    @Inject
    private DWHQueryBean queryBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {

        queryBean.executeQuery(result, jobInstance.getParametres(), currentUser.getProvider());
        result.setDone(true); // TODO why is here DONE and other places is close()?
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.DWH;
    }

}