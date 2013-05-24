package org.meveo.services.job;

import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;

import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.meveo.commons.utils.DateUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;

@Startup
@Singleton
public class JobPurge implements Job {

	@Resource
	TimerService timerService;
	
	@Inject
	JobExecutionService jobExecutionService;
	
    @Inject
    private Logger log;
    
    
    @PostConstruct
    public void init(){
        TimerEntityService.registerJob(this);
    }
    

    @Override
    public JobExecutionResult execute(String parameter,Provider provider) {
        JobExecutionResultImpl result = new JobExecutionResultImpl();
        String jobname = "";
        int nbDays = 30;
        try { 
            String[] params=parameter.split("-");
            jobname = params[0];
            nbDays = Integer.parseInt(params[1]);
        } catch(Exception e){}
        Date date = DateUtils.addDaysToDate(new Date(), nbDays*(-1));
        long nbItemsToProcess= jobExecutionService.countJobsToDelete(jobname, date);
        result.setNbItemsToProcess(nbItemsToProcess); //it might well happen we dont know in advance how many items we have to process, in that case comment this method
        int nbSuccess = jobExecutionService.delete(jobname, date);
        result.setNbItemsCorrectlyProcessed(nbSuccess);
        result.setNbItemsProcessedWithError(nbItemsToProcess-nbSuccess);
        result.close(nbSuccess>0?("purged "+jobname):"");
        return result;
    }

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression,timerConfig);
		return timer.getHandle();
	}

	@Timeout
	public void trigger(Timer timer){
		TimerInfo info = (TimerInfo) timer.getInfo();
		if(info.isActive()){
            JobExecutionResult result=execute(info.getParametres(),info.getProvider());
            jobExecutionService.persistResult(this, result,info.getParametres(),info.getProvider());
		}
	}
	
	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}

}
