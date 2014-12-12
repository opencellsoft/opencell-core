package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class RecurringRatingJobBean {

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	protected Logger log;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class })
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			List<RecurringChargeInstance> activeRecurringChargeInstances = recurringChargeInstanceService
					.findByStatus(em, InstanceStatusEnum.ACTIVE,
							DateUtils.addDaysToDate(new Date(), 1));

			log.info("charges to rate={}"
					+ activeRecurringChargeInstances.size());

			for (RecurringChargeInstance activeRecurringChargeInstance : activeRecurringChargeInstances) {
				try {
					RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance
							.getRecurringChargeTemplate();
					if (recurringChargeTemplate.getCalendar() == null) {
						// FIXME : should not stop the method execution
						throw new IncorrectChargeTemplateException(
								"Recurring charge template has no calendar: code="
										+ recurringChargeTemplate.getCode());
					}
					Date applicationDate = null;
					if (recurringChargeTemplate.getApplyInAdvance()) {
						applicationDate = activeRecurringChargeInstance
								.getNextChargeDate();
					} else {
						applicationDate = activeRecurringChargeInstance
								.getChargeDate();
					}

					log.info("applicationDate=" + applicationDate);

					applicationDate = DateUtils.parseDateWithPattern(
							applicationDate, "dd/MM/yyyy");

					if (!recurringChargeTemplate.getApplyInAdvance()) {
						walletOperationService
								.applyNotAppliedinAdvanceReccuringCharge(em,
										activeRecurringChargeInstance, false,
										recurringChargeTemplate, currentUser);
						result.registerSucces();
					} else {
						walletOperationService.applyReccuringCharge(em,
								activeRecurringChargeInstance, false,
								recurringChargeTemplate, currentUser);
						result.registerSucces();
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
