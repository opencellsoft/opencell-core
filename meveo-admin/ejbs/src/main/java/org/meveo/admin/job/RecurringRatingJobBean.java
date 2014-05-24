package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class RecurringRatingJobBean {

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private WalletOperationService walletOperationService;

    @Inject
    protected Logger log;
	
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result){
		try {
			List<RecurringChargeInstance> activeRecurringChargeInstances = recurringChargeInstanceService
					.findByStatus(InstanceStatusEnum.ACTIVE, DateUtils.addDaysToDate(new Date(), 1));

			log.info("# charge to rate:" + activeRecurringChargeInstances.size());
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
						applicationDate = activeRecurringChargeInstance.getNextChargeDate();
					} else {
						applicationDate = activeRecurringChargeInstance.getChargeDate();
					}

					log.info("applicationDate=" + applicationDate);

					applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

					if (!recurringChargeTemplate.getApplyInAdvance()) {
						walletOperationService
								.applyNotAppliedinAdvanceReccuringCharge(
										activeRecurringChargeInstance, false,
										recurringChargeTemplate, null);
						result.registerSucces();
					} else {
						walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false, recurringChargeTemplate, null);
						result.registerSucces();
					}
				} catch (Exception e) {
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
