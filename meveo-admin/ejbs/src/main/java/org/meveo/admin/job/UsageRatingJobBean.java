package org.meveo.admin.job;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.UsageRatingAsync;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.billing.impl.EdrService;
import org.slf4j.Logger;

@Stateless
public class UsageRatingJobBean {

	@Inject
	private Logger log;

	@Inject
	private EdrService edrService;

	@Inject
	private UsageRatingAsync usageRatingAsync;

	@Inject
	@Rejected
	Event<Serializable> rejectededEdrProducer;


	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, User currentUser,TimerEntity timerEntity) {
		try {
			
			List<Long> ids = edrService.getEDRidsToRate(currentUser.getProvider());		
			log.debug("edr to rate:" + ids.size());
			
			Long nbRuns = timerEntity.getLongCustomValue("nbRuns").longValue();
	    	Long waitingMillis = timerEntity.getLongCustomValue("waitingMillis").longValue();
			
	    	if(nbRuns == null ){
	    		nbRuns = new Long(1);
	    	}
	    	if(waitingMillis == null ){
	    		waitingMillis = new Long(0);
	    	}

	    	SubListCreator subListCreator = new SubListCreator(ids,nbRuns.intValue());
			while (subListCreator.isHasNext()) {	
				usageRatingAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(),result, currentUser);
				try {
					Thread.sleep(waitingMillis.longValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}


}
