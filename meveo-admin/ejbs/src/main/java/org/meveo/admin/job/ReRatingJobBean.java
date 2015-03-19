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
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class ReRatingJobBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2226065462536318643L;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatingService ratingService;

	@Inject
	protected Logger log;

	@Inject
	@Rejected
	Event<WalletOperation> rejectededOperationProducer;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser, boolean useSamePricePlan) {
		try {
			List<WalletOperation> walletOperations = walletOperationService.findByStatus(
					WalletOperationStatusEnum.TO_RERATE, currentUser.getProvider());

			log.info("rerate with useSamePricePlan={} ,#operations={}", useSamePricePlan,walletOperations.size());
			result.setNbItemsToProcess(walletOperations.size());
			for (WalletOperation walletOperation : walletOperations) {
				try {
					ratingService.reRate(walletOperation, useSamePricePlan);
					result.registerSucces();
				} catch (Exception e) {
					rejectededOperationProducer.fire(walletOperation);
					e.printStackTrace();
					log.error(e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
}
