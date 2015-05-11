package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import org.meveo.model.jobs.TimerEntity;
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

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, User currentUser,TimerEntity timerEntity) {
		try {
			Date maxDate = DateUtils.addDaysToDate(new Date(), 1);
			List<Long> ids = recurringChargeInstanceService.findIdsByStatus(InstanceStatusEnum.ACTIVE, maxDate);
			int inputSize =  ids.size();
			log.info("charges to rate={}", inputSize);
			
			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = timerEntity.getLongCustomValue("RecurringRatingJob_nbRuns").longValue();  			
				waitingMillis = timerEntity.getLongCustomValue("RecurringRatingJob_waitingMillis").longValue();
			}catch(Exception e){
				log.warn("Cant get customFields for "+timerEntity.getJobName());
			}

	    	SubListCreator subListCreator = new SubListCreator(ids,nbRuns.intValue());
			while (subListCreator.isHasNext()) {				
				recurringChargeAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(),result, currentUser,maxDate);	
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
