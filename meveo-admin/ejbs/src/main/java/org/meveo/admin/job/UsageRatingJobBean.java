package org.meveo.admin.job;

import java.io.Serializable;
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
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.slf4j.Logger;

@Stateless
public class UsageRatingJobBean {

	@Inject
	private Logger log;

	@Inject
	private EdrService edrService;

	@Inject
	private UsageRatingService usageRatingService;

	@Inject
	@Rejected
	Event<Serializable> rejectededEdrProducer;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			List<EDR> edrs = edrService.getEDRToRate(currentUser.getProvider());

			log.debug("edr to rate:" + edrs.size());

			for (EDR edr : edrs) {
				log.debug("rate edr={}", edr.getId());

				try {
					usageRatingService.ratePostpaidUsage(edr, currentUser);

					edrService.setProvider(currentUser.getProvider());
					edrService.update(edr, currentUser);

					if (edr.getStatus() == EDRStatusEnum.RATED) {
						result.registerSucces();
					} else {
						rejectededEdrProducer.fire(edr);
						result.registerError(edr.getRejectReason());
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					rejectededEdrProducer.fire(edr);
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
