package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitRecurringRatingJobBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2226065462536318643L;


	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	protected Logger log;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser, Long ID_activeRecurringChargeInstance, Date maxDate) {
		log.debug("Running for user={}, activeRecurringChargeInstanceID={}", currentUser, ID_activeRecurringChargeInstance);
		try{
			int nbRating=recurringChargeInstanceService.applyRecurringCharge(ID_activeRecurringChargeInstance,maxDate,currentUser);
			if(nbRating==1){
				result.registerSucces();
			} else if(nbRating>1){
				result.registerWarning(ID_activeRecurringChargeInstance+" rated "+nbRating+" times");
			} else {
				result.registerWarning(ID_activeRecurringChargeInstance+" not rated");
			}
		} catch(BusinessException e){
            result.registerError(ID_activeRecurringChargeInstance, e.getMessage());
		}
		log.debug("end executed!");
	}
}
