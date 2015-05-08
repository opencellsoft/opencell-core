package org.meveo.admin.job.importexport;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.async.ImportCustomersAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class ImportCustomersJob extends Job {

    @Inject
    private ImportCustomersAsync importCustomersAsync;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
    	
    	Long nbRuns = null;//timerEntity.getLongCustomValue("nbRuns").longValue();
    	Long waitingMillis = null;//timerEntity.getLongCustomValue("waitingMillis").longValue();
    	
    	if(nbRuns == null )
    		nbRuns = new Long(5);
    	if(waitingMillis == null )
    		waitingMillis = new Long(500);
    	
    	for(int i=0; i< nbRuns.intValue();i++){
    		importCustomersAsync.launchAndForget(result, currentUser);
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