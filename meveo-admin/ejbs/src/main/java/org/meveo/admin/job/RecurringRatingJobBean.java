package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.RecurringChargeAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.slf4j.Logger;

@Stateless
public class RecurringRatingJobBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2226065462536318643L;
	
	@Inject
	private RecurringChargeAsync recurringChargeAsync;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;


	@Inject
	protected Logger log;

	@Inject
	@Rejected
	Event<Serializable> rejectededChargeProducer;

	@SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, User currentUser,JobInstance jobInstance) {
		try {
			Date maxDate = DateUtils.addDaysToDate(new Date(), 1);
			List<Long> ids = recurringChargeInstanceService.findIdsByStatus(InstanceStatusEnum.ACTIVE, maxDate);
			int inputSize =  ids.size();
			log.info("charges to rate={}", inputSize);
			
			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = jobInstance.getLongCustomValue("RecurringRatingJob_nbRuns").longValue();  			
				waitingMillis = jobInstance.getLongCustomValue("RecurringRatingJob_waitingMillis").longValue();
				if(nbRuns == -1){
					nbRuns = (long) Runtime.getRuntime().availableProcessors();
				}
			}catch(Exception e){
				log.warn("Cant get customFields for "+jobInstance.getJobTemplate());
			}

			List<Future<String>> futures = new ArrayList<Future<String>>();
	    	SubListCreator subListCreator = new SubListCreator(ids,nbRuns.intValue());
			while (subListCreator.isHasNext()) {				
				futures.add(recurringChargeAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(),result, currentUser,maxDate));	

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                    
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }       
        } catch (Exception e) {
            log.error("Failed to run recurring rating job", e);
            result.registerError(e.getMessage());
        }
		result.setDone(true);
	}
}
