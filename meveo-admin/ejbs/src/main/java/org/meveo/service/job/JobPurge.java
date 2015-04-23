package org.meveo.service.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.shared.DateUtils;

@Startup
@Singleton
public class JobPurge extends Job {

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {

        String jobname = timerEntity.getStringCustomValue("JobPurge_jobName");
        int nbDays = 30;
        if (timerEntity.getLongCustomValue("JobPurge_nbDays") != null) {
            nbDays = timerEntity.getLongCustomValue("JobPurge_nbDays").intValue();
        }
        Date date = DateUtils.addDaysToDate(new Date(), nbDays * (-1));
        long nbItemsToProcess = jobExecutionService.countJobsToDelete(jobname, date);
        result.setNbItemsToProcess(nbItemsToProcess); // it might well happen we dont know in advance how many items we have to process,in that case comment this method
        int nbSuccess = jobExecutionService.delete(jobname, date);
        result.setNbItemsCorrectlyProcessed(nbSuccess);
        result.setNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
        result.setReport(nbSuccess > 0 ? ("purged " + jobname) : "");
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public List<CustomFieldTemplate> getCustomFields(User currentUser) {
        List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

        CustomFieldTemplate jobName = new CustomFieldTemplate();
        jobName.setCode("JobPurge_jobName");
        jobName.setAccountLevel(AccountLevelEnum.TIMER);
        jobName.setActive(true);
        Auditable audit = new Auditable();
        audit.setCreated(new Date());
        audit.setCreator(currentUser);
        jobName.setAuditable(audit);
        jobName.setProvider(currentUser.getProvider());
        jobName.setDescription("Job Name (to purge)");
        jobName.setFieldType(CustomFieldTypeEnum.STRING);
        jobName.setValueRequired(true);
        result.add(jobName);

        CustomFieldTemplate nbDays = new CustomFieldTemplate();
        nbDays.setCode("JobPurge_nbDays");
        nbDays.setAccountLevel(AccountLevelEnum.TIMER);
        nbDays.setActive(true);
        Auditable audit2 = new Auditable();
        audit2.setCreated(new Date());
        audit2.setCreator(currentUser);
        nbDays.setAuditable(audit2);
        nbDays.setProvider(currentUser.getProvider());
        nbDays.setDescription("older that (in days)");
        nbDays.setFieldType(CustomFieldTypeEnum.LONG);
        nbDays.setValueRequired(true);
        result.add(nbDays);

        return result;
    }
}
