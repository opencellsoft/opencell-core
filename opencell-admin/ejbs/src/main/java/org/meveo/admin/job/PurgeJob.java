package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * The Class PurgeJob purge old data for job execution history, counter periods .
 */
@Stateless
public class PurgeJob extends Job {

    /** The purge job bean. */
    @Inject
    private PurgeJobBean purgeJobBean;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        purgeJobBean.execute(result, jobInstance);

    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_jobExecHistory_jobName");
        cft.setAppliesTo("JOB_PurgeJob");
        cft.setActive(true);
        cft.setDescription("Job name");
        cft.setFieldType(CustomFieldTypeEnum.STRING);
        cft.setValueRequired(false);
        cft.setMaxValue(100L);
        cft.setGuiPosition("tab:Configuration:0;fieldGroup:Purge job execution history:0;field:0");
        result.put("PurgeJob_jobExecHistory_jobName", cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_jobExecHistory_nbDays");
        cft.setAppliesTo("JOB_PurgeJob");
        cft.setActive(true);
        cft.setDescription("Purge history older than (in days) *");
        cft.setFieldType(CustomFieldTypeEnum.LONG);
        cft.setValueRequired(false);
        cft.setGuiPosition("tab:Configuration:0;fieldGroup:Purge job execution history:0;field:1");
        result.put("PurgeJob_jobExecHistory_nbDays", cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_counterPeriod_nbDays");
        cft.setAppliesTo("JOB_PurgeJob");
        cft.setActive(true);
        cft.setDescription("Purge periods with end date older than (in days) *");
        cft.setFieldType(CustomFieldTypeEnum.LONG);
        cft.setValueRequired(false);
        cft.setGuiPosition("tab:Configuration:0;fieldGroup:Purge counter periods:1;field:0");
        result.put("PurgeJob_counterPeriod_nbDays", cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_notificationHistory_notifCode");
        cft.setAppliesTo("JOB_PurgeJob");
        cft.setActive(true);
        cft.setDescription("Notification code");
        cft.setFieldType(CustomFieldTypeEnum.STRING);
        cft.setValueRequired(false);
        cft.setMaxValue(100L);
        cft.setGuiPosition("tab:Configuration:0;fieldGroup:Purge notification history:3;field:0");
        result.put("PurgeJob_notificationHistory_notifCode", cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_notificationHistory_nbDays");
        cft.setAppliesTo("JOB_PurgeJob");
        cft.setActive(true);
        cft.setDescription("Purge events older than (in days) *");
        cft.setFieldType(CustomFieldTypeEnum.LONG);
        cft.setValueRequired(false);
        cft.setGuiPosition("tab:Configuration:0;fieldGroup:Purge notification history:3;field:1");
        result.put("PurgeJob_notificationHistory_nbDays", cft);

        cft = new CustomFieldTemplate();
        cft.setCode("PurgeJob_inboundRequests_nbDays");
        cft.setAppliesTo("JOB_PurgeJob");
        cft.setActive(true);
        cft.setDescription("Purge requests older than (in days) *");
        cft.setFieldType(CustomFieldTypeEnum.LONG);
        cft.setValueRequired(false);
        cft.setGuiPosition("tab:Configuration:0;fieldGroup:Inbound requests:4;field:0");
        result.put("PurgeJob_inboundRequests_nbDays", cft);

        return result;
    }
}
