package org.meveo.service.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

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
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class JobPurge implements Job {

	private Logger log = LoggerFactory.getLogger(JobPurge.class);

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
    @Asynchronous 
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(TimerEntity timerEntity, User currentUser) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		TimerInfo info=timerEntity.getTimerInfo();
        if (info.isActive() || currentUser != null) {
			if (currentUser == null) {
				currentUser = userService.findById(info.getUserId());
			}

			String jobname = timerEntity.getStringCustomValue("JobPurge_jobName");
			int nbDays = 30;
			if(timerEntity.getLongCustomValue("JobPurge_nbDays")!=null){
			    nbDays = timerEntity.getLongCustomValue("JobPurge_nbDays").intValue();
			}
			Date date = DateUtils.addDaysToDate(new Date(), nbDays * (-1));
			long nbItemsToProcess = jobExecutionService.countJobsToDelete(jobname, date);
			result.setNbItemsToProcess(nbItemsToProcess); // it might well
															// happen we
															// dont know in
															// advance
															// how many items we
															// have to process,
															// in
															// that case comment
															// this method
			int nbSuccess = jobExecutionService.delete(jobname, date);
			result.setNbItemsCorrectlyProcessed(nbSuccess);
			result.setNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
			result.close(nbSuccess > 0 ? ("purged " + jobname) : "");
		}

		jobExecutionService.persistResult(this, result, timerEntity, currentUser, getJobCategory());
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression, TimerEntity timerEntity) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(timerEntity);
		timerConfig.setPersistent(false);

		return timerService.createCalendarTimer(scheduleExpression, timerConfig);
	}

	@Override
	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void trigger(Timer timer) {
		execute((TimerEntity) timer.getInfo(), null);
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		log.info("cancel " + alltimers.size() + " timers for" + this.getClass().getSimpleName());

		for (Timer timer : alltimers) {
			try {
				timer.cancel();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
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
        Auditable audit= new Auditable();
        audit.setCreated(new Date());
        audit.setCreator(currentUser);
        jobName.setAuditable(audit);
        jobName.setProvider(currentUser.getProvider());
        jobName.setDescription("Job Name (to purge)");
        jobName.setFieldType(CustomFieldTypeEnum.STRING);
        jobName.setValueRequired(true);
        result.add(jobName);
        
        CustomFieldTemplate nbDays=new CustomFieldTemplate();
        nbDays.setCode("JobPurge_nbDays");
        nbDays.setAccountLevel(AccountLevelEnum.TIMER);
        nbDays.setActive(true);
        Auditable audit2= new Auditable();
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
