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

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
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
	UnitRecurringRatingJobBean unitRecurringRatingJobBean;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;


	@Inject
	protected Logger log;

	@Inject
	@Rejected
	Event<Serializable> rejectededChargeProducer;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			Date maxDate = DateUtils.addDaysToDate(new Date(), 1);
			List<Long> ids = recurringChargeInstanceService.findIdsByStatus(
					InstanceStatusEnum.ACTIVE, maxDate);

			log.info("charges to rate={}", ids.size());

			for (Long id : ids) {
				
				unitRecurringRatingJobBean.execute(result, currentUser,id,maxDate);
				
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
