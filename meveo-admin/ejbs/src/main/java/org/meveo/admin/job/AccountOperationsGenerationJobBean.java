package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.AccOpGenerationAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.InvoiceService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountOperationsGenerationJobBean {

	@Inject
	private Logger log;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private AccOpGenerationAsync accOpGenerationAsync;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, User currentUser, JobInstance jobInstance) {
		Provider currentProvider = currentUser.getProvider();
		log.info("Running for user={}, provider={}", currentUser,currentProvider.getCode());
		try {
			
			List<Long> ids = invoiceService.getInvoiceIdsWithNoAccountOperation(null, currentProvider);
			log.debug("invoices to traite:" +( ids == null ? null:ids.size()));
			
			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = (Long) jobInstance.getCFValue("AccOpGeneratioJobb_nbRuns");  			
				waitingMillis = (Long) jobInstance.getCFValue("AccOpGeneratioJobb_waitingMillis");
				if(nbRuns == -1){
					nbRuns  = (long) Runtime.getRuntime().availableProcessors();
				}
			}catch(Exception e){
				nbRuns = new Long(1);
				waitingMillis = new Long(0);
				log.warn("Cant get customFields for "+jobInstance.getJobTemplate());
			}
			List<Future<String>> futures = new ArrayList<Future<String>>();
	    	SubListCreator subListCreator = new SubListCreator(ids,nbRuns.intValue());
	    	log.debug("block to run:" + subListCreator.getBlocToRun());
	    	log.debug("nbThreads:" + nbRuns);
			while (subListCreator.isHasNext()) {	
				futures.add(accOpGenerationAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(),result, currentUser));
                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                    
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
		} catch (Exception e) {
            log.error("Failed to run accountOperation generation  job",e);
            result.registerError(e.getMessage());
        }
	}

}
