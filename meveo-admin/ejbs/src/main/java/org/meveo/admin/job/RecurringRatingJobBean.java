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
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class RecurringRatingJobBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2226065462536318643L;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	protected Logger log;

	@Inject
	@Rejected
	Event<Serializable> rejectededChargeProducer;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			Date maxDate = DateUtils.addDaysToDate(new Date(), 1);
			List<RecurringChargeInstance> activeRecurringChargeInstances = recurringChargeInstanceService.findByStatus(
					InstanceStatusEnum.ACTIVE, maxDate);

			log.info("charges to rate={}", activeRecurringChargeInstances.size());

			for (RecurringChargeInstance activeRecurringChargeInstance : activeRecurringChargeInstances) {
				try {

					RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance
							.getRecurringChargeTemplate();
					if (recurringChargeTemplate.getCalendar() == null) {
						// FIXME : should not stop the method execution
						rejectededChargeProducer.fire(recurringChargeTemplate);
						log.error("Recurring charge template has no calendar: code="
								+ recurringChargeTemplate.getCode());
						result.registerError("Recurring charge template has no calendar: code="
								+ recurringChargeTemplate.getCode());
						continue;
					}

					Date applicationDate = null;
					if (recurringChargeTemplate.getApplyInAdvance()) {
						applicationDate = activeRecurringChargeInstance.getNextChargeDate();
					} else {
						applicationDate = activeRecurringChargeInstance.getChargeDate();
					}

					if (applicationDate.getTime() <= maxDate.getTime()) {
						log.info("applicationDate={}", applicationDate);

						applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

						if (!recurringChargeTemplate.getApplyInAdvance()) {
							walletOperationService.applyNotAppliedinAdvanceReccuringCharge(
									activeRecurringChargeInstance, false, recurringChargeTemplate, currentUser);
							result.registerSucces();
						} else {
							walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false,
									recurringChargeTemplate, currentUser);
							result.registerSucces();
						}
					} else {
						log.info("applicationDate={} is posterior to maxdate={} 2nd level cache is probably in cause",
								applicationDate, maxDate);
					}
				} catch (Exception e) {
					rejectededChargeProducer.fire(activeRecurringChargeInstance);
					log.error(e.getMessage());
					result.registerError(e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
