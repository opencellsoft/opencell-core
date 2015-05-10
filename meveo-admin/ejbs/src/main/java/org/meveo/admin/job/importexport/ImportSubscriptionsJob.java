package org.meveo.admin.job.importexport;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.async.ImportSubscriptionsAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class ImportSubscriptionsJob extends Job {

    @Inject
    private ImportSubscriptionsAsync importSubscriptionsAsync;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
       
    	Long nbRuns = timerEntity.getLongCustomValue("nbRuns").longValue();
    	Long waitingMillis = timerEntity.getLongCustomValue("waitingMillis").longValue();
    	
    	if(nbRuns == null ){
    		nbRuns = new Long(1);
    	}
    	if(waitingMillis == null ){
    		waitingMillis = new Long(0);
    	}
    	
    	for(int i=0; i< nbRuns.intValue();i++){
    		importSubscriptionsAsync.launchAndForget(result, currentUser);
    		 try {
				Thread.sleep(waitingMillis.longValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
    	} 		
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }
}