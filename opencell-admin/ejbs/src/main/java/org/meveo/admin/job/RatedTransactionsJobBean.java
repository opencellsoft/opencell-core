/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationAggregationSettingsService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.job.Job;

/**
 * A job implementation to convert Open Wallet operations to Rated transactions
 * 
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 */
@Stateless
public class RatedTransactionsJobBean extends IteratorBasedJobBean<WalletOperation> {

    private static final long serialVersionUID = -2740290205290535899L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private WalletOperationAggregationSettingsService walletOperationAggregationSettingsService;
    
    private  List<Long> ids = new ArrayList<Long>();
    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private boolean hasMore = false;
    private StatelessSession statelessSession;
    private ScrollableResults scrollableResults;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::convertWoToRTBatch, this::hasMore, this::fillDiscountedRT);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    @SuppressWarnings("unchecked")
	private Optional<Iterator<WalletOperation>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        EntityReferenceWrapper aggregationSettingsWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "woAggregationSettings", null);
        WalletOperationAggregationSettings aggregationSettings = null;
        if (aggregationSettingsWrapper != null) {
            aggregationSettings = walletOperationAggregationSettingsService.findByCode(aggregationSettingsWrapper.getCode());
        }

        // Aggregation is not supported here
        if (aggregationSettings != null) {
            return Optional.empty();
        }

        log.info("Remove wallet operations rated to 0");
        walletOperationService.removeZeroWalletOperation();

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 10000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        // Number of Wallet operations to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("ratedTransactionsJob.processNrInJobRun", 4000000);

        Object[] convertSummary = (Object[]) emWrapper.getEntityManager().createNamedQuery("WalletOperation.getConvertToRTsSummary").getSingleResult();

        Long nrOfRecords = (Long) convertSummary[0];
        Long maxId = (Long) convertSummary[1];

        if (nrOfRecords.intValue() == 0) {
            return Optional.empty();
        }
        


        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();
        scrollableResults = statelessSession.createNamedQuery("WalletOperation.listConvertToRTs").setParameter("maxId", maxId).setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun)
                .setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);
        
        ids.clear();
        Query query = statelessSession.createNamedQuery("WalletOperation.listConvertToRTs").setParameter("maxId", maxId).setCacheable(false).setMaxResults(processNrInJobRun)
                .setFetchSize(fetchSize);
        ids = ((List<WalletOperation>) query.getResultList()).stream().filter(obj -> Objects.nonNull(obj)).map(obj -> ((WalletOperation) obj).getId()).collect(Collectors.toList());

        hasMore = nrOfRecords >= processNrInJobRun;

        return Optional.of(new SynchronizedIterator<WalletOperation>(scrollableResults, nrOfRecords.intValue()));
    }

    /**
     * Convert a multiple Wallet operations to a Rated transactions
     * 
     * @param walletOperations Wallet operations
     * @param jobExecutionResult Job execution result
     */
    private void convertWoToRTBatch(List<WalletOperation> walletOperations, JobExecutionResultImpl jobExecutionResult) {
        Boolean isApplyBillingRules = (Boolean) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), Job.CF_APPLY_BILING_RULES);

        List<RatedTransaction> lstRatedTransaction = ratedTransactionService.createRatedTransactionsInBatch(walletOperations);
        if (isApplyBillingRules) {
            ratedTransactionService.applyInvoicingRules(lstRatedTransaction);
        }  
        Boolean ss = true;
    }
    

    /**
     * Convert a single Wallet operation to a Rated transaction
     * 
     * @param woId Wallet operation id to convert
     * @param jobExecutionResult Job execution result
     */
    private void fillDiscountedRT(JobExecutionResultImpl jobExecutionResult) {
    	List<WalletOperation> discountWO=walletOperationService.getDiscountWalletOperation(ids);
    	for(WalletOperation walletOperation:discountWO) {
    		
    		log.debug("createRatedTransaction walletOperation={}",walletOperation.getDiscountedWalletOperation());
        	RatedTransaction discountedRatedTransaction = ratedTransactionService.findByWalletOperationId(walletOperation.getDiscountedWalletOperation());
        	
        	if(discountedRatedTransaction!=null) {
        		log.debug("createRatedTransaction discountedRatedTransaction={}",discountedRatedTransaction.getId());
        		RatedTransaction discountRatedTransaction = ratedTransactionService.findByWalletOperationId(walletOperation.getId());
            	
        		discountRatedTransaction.setDiscountedRatedTransaction(discountedRatedTransaction.getId());
        		discountRatedTransaction.setDiscountPlan(walletOperation.getDiscountPlan());
        		discountRatedTransaction.setDiscountPlanItem(walletOperation.getDiscountPlanItem());
        		discountRatedTransaction.setDiscountPlanType(walletOperation.getDiscountPlanType());
        		discountRatedTransaction.setDiscountValue(walletOperation.getDiscountValue());
        		discountRatedTransaction.setSequence(walletOperation.getSequence());
        		ratedTransactionService.update(discountRatedTransaction);
        	}
    		
    	}
    }

    private boolean hasMore(JobInstance jobInstance) {
        return hasMore;
    }

    private void closeResultset(JobExecutionResultImpl jobExecutionResult) {
        scrollableResults.close();
        statelessSession.close();
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return false;
    }
}