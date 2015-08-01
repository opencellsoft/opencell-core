package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class PurgeJob extends Job {

    @Inject
    private PurgeJobBean purgeJobBean;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {

        purgeJobBean.execute(result, jobInstance, currentUser);

    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public List<CustomFieldTemplate> getCustomFields() {
        List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_jobExecHistory_jobName");
        cft.setAccountLevel(AccountLevelEnum.TIMER);
        cft.setActive(true);
        cft.setDescription("Purge job execution history: job name");
        cft.setFieldType(CustomFieldTypeEnum.STRING);
        cft.setValueRequired(false);
        result.add(cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_jobExecHistory_nbDays");
        cft.setAccountLevel(AccountLevelEnum.TIMER);
        cft.setActive(true);
        cft.setDescription("Purge job execution history: older then (in days)");
        cft.setFieldType(CustomFieldTypeEnum.LONG);
        cft.setValueRequired(false);
        result.add(cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_counterPeriod_nbDays");
        cft.setAccountLevel(AccountLevelEnum.TIMER);
        cft.setActive(true);
        cft.setDescription("Purge counter periods: older then (in days)");
        cft.setFieldType(CustomFieldTypeEnum.LONG);
        cft.setValueRequired(false);
        result.add(cft);

        return result;
    }
}
