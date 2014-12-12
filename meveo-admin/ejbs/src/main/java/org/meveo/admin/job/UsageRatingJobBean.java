package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class UsageRatingJobBean {

	@Inject
	private Logger log;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	@Inject
	private EdrService edrService;

	@Inject
	private UsageRatingService usageRatingService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class })
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			List<EDR> edrs = edrService.getEDRToRate(em,
					currentUser.getProvider());

			log.info("edr to rate:" + edrs.size());

			for (EDR edr : edrs) {
				log.info("rate edr={}", edr.getId());

				try {
					usageRatingService.ratePostpaidUsage(edr);

					edrService.setProvider(currentUser.getProvider());
					edrService.update(em, edr, currentUser);

					if (edr.getStatus() == EDRStatusEnum.RATED) {
						result.registerSucces();
					} else {
						result.registerError(edr.getRejectReason());
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
