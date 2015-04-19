package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;

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
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.WalletOperationService;
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
	private WalletOperationService walletOperationService;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	protected Logger log;

	@Inject
	@Rejected
	Event<Serializable> rejectededChargeProducer;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser,
			Long ID_activeRecurringChargeInstance, Date maxDate) {
		try {

			RecurringChargeInstance activeRecurringChargeInstance = recurringChargeInstanceService
					.findById(ID_activeRecurringChargeInstance);

			RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance
					.getRecurringChargeTemplate();
			if (recurringChargeTemplate.getCalendar() == null) {
				// FIXME : should not stop the method execution
				rejectededChargeProducer.fire(recurringChargeTemplate);
				log.error("Recurring charge template has no calendar: code="
						+ recurringChargeTemplate.getCode());
				result.registerError("Recurring charge template has no calendar: code="
						+ recurringChargeTemplate.getCode());
				return;
			}

			Date applicationDate = null;
			if (recurringChargeTemplate.getApplyInAdvance()) {
				applicationDate = activeRecurringChargeInstance.getNextChargeDate();
			} else {
				applicationDate = activeRecurringChargeInstance.getChargeDate();
			}

			if (applicationDate.getTime() <= maxDate.getTime()) {
				log.info("applicationDate={}", applicationDate);

				applicationDate = DateUtils.parseDateWithPattern(
						applicationDate, "dd/MM/yyyy");

				if (!recurringChargeTemplate.getApplyInAdvance()) {
					walletOperationService
							.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate, currentUser);
					result.registerSucces();
				} else {
					walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false,recurringChargeTemplate, currentUser);
					result.registerSucces();
				}
			} else {
				log.info(
						"applicationDate={} is posterior to maxdate={} 2nd level cache is probably in cause",
						applicationDate, maxDate);
			}
		} catch (Exception e) {
			rejectededChargeProducer.fire("RecurringCharge "
					+ ID_activeRecurringChargeInstance);
			log.error(e.getMessage());
			result.registerError(e.getMessage());
			e.printStackTrace();
		}
	}
}
