package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.RatedTransactionAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class RatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatedTransactionAsync ratedTransactionAsync;

	@SuppressWarnings("unchecked")
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, User currentUser,JobInstance jobInstance) {
		try {			
			List<Long> walletOperationIds = walletOperationService.listToInvoiceIds(new Date(), currentUser.getProvider());
			log.info("WalletOperations to convert into rateTransactions={}", walletOperationIds.size());

			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = jobInstance.getLongCustomValue("RatedTransactionsJob_nbRuns").longValue();  			
				waitingMillis = jobInstance.getLongCustomValue("RatedTransactionsJob_waitingMillis").longValue();
				if(nbRuns == -1){
					nbRuns  = (long) Runtime.getRuntime().availableProcessors();
				}
			}catch(Exception e){
				log.warn("Cant get customFields for "+jobInstance.getJobTemplate());
			}

			SubListCreator subListCreator = new SubListCreator(walletOperationIds,nbRuns.intValue());
			List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
			while (subListCreator.isHasNext()) {
				asyncReturns.add(ratedTransactionAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, currentUser));
				try {
					Thread.sleep(waitingMillis.longValue());
				} catch (InterruptedException e) {
					log.error("", e);
				} 
			}
			for(Future<String> futureItsNow : asyncReturns){
				futureItsNow.get();	
			}
			result.setDone(true);
		} catch (Exception e) {
			log.error("Failed to rate transactions", e);
		}
	}

}
