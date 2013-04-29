package org.meveo.admin.job;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class UsageRatingJob implements Job {

	@Resource
	TimerService timerService;
	
	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	EdrService edrService;

	@Inject
	UsageRatingService usageRatingService;
	
    @Inject
    private Logger log;
    
    
    @PostConstruct
    public void init(){
        TimerEntityService.registerJob(this);
    }
    
	@Override
    public JobExecutionResult execute(String parameter) {
        log.info("execute UsageRatingJob");
        JobExecutionResultImpl result = new JobExecutionResultImpl();
        try {
        	 List<EDR> edrs=edrService.getEDRToRate();
    		log.info("# edr to rate:"+edrs.size());
       		 for(EDR edr:edrs){
       			log.info("rate edr "+edr.getId());
       			try{
       				usageRatingService.ratePostpaidUsage(edr);
       				edrService.update(edr);
     		 	    result.registerSucces();
    			} catch (Exception e) {
    				result.registerError(e.getMessage());
    			}		
     		 }
		} catch (Exception e) {
			 e.printStackTrace();
		}finally {
            try{
            jobExecutionService.cleanupConversationContext();
            }catch(Exception e1){}
        } 
        result.close("");
        return result;
    }
    
    
    
    
	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression,timerConfig);
		return timer.getHandle();
	}

	boolean running=false;
    @Timeout
    public void trigger(Timer timer){
        TimerInfo info = (TimerInfo) timer.getInfo();
        if(!running && info.isActive()){
            try{
                running=true;
                JobExecutionResult result=execute(info.getParametres());
                jobExecutionService.persistResult(this, result,info.getParametres());
            } catch(Exception e){
                e.printStackTrace();
            } finally{
                running = false;
            }
        }
    }
	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}

}
