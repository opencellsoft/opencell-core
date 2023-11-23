package org.meveo.admin.job;

import org.apache.commons.collections.MapUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;

import javax.ejb.Stateless;
import javax.inject.Inject;

import static java.util.Optional.of;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Stateless
public class DuplicateRatedTransactionJobBean extends IteratorBasedJobBean<RatedTransaction>{

	private static final long serialVersionUID = 1L;
	
	@Inject
    private RatedTransactionService ratedTransactionService;
    
	@Override
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::processRatedTransactionToDuplicate, null, null, null);
    }
    
	private Optional<Iterator<RatedTransaction>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
		Map<String, Object> filters = jobExecutionResult.getJobInstance().getRunTimeValues() != null ? (Map<String, Object>) jobExecutionResult.getJobInstance().getRunTimeValues().get(DuplicateRatedTransactionJob.DUPLICATION_RT_JOB_ADVANCED_PARAMETERS) : null;
        return (MapUtils.isNotEmpty(filters)) ? Optional.of(new SynchronizedIterator<RatedTransaction>(ratedTransactionService.findByFilter(filters))) : of(new SynchronizedIterator<>(Collections.emptyList())); 
    }

    private void processRatedTransactionToDuplicate(RatedTransaction ratedTransaction, JobExecutionResultImpl jobExecutionResult) {
    	Boolean negateAmount = jobExecutionResult.getJobInstance().getRunTimeValues() != null ? (Boolean) jobExecutionResult.getJobInstance().getRunTimeValues().get(DuplicateRatedTransactionJob.DUPLICATION_RT_JOB_NEGATE_AMOUNT) : true;
    	duplicateRated(ratedTransaction, negateAmount);
    }

	private void duplicateRated(RatedTransaction ratedTransaction, boolean isNegate) {
		RatedTransaction duplicate = new RatedTransaction(ratedTransaction);
		if (isNegate) {
			duplicate.setUnitAmountTax(duplicate.getUnitAmountTax() != null ? duplicate.getUnitAmountTax().negate() : null);
			duplicate.setUnitAmountWithoutTax(duplicate.getUnitAmountWithoutTax() != null ? duplicate.getUnitAmountWithoutTax().negate() : null);
			duplicate.setUnitAmountWithTax(duplicate.getUnitAmountWithTax() != null ? duplicate.getUnitAmountWithTax().negate() : null);
			duplicate.setAmountTax(duplicate.getAmountTax() != null ? duplicate.getAmountTax().negate() : null);
			duplicate.setAmountWithoutTax(duplicate.getAmountWithoutTax() != null ? duplicate.getAmountWithoutTax().negate() : null);
			duplicate.setAmountWithTax(duplicate.getAmountWithTax() != null ? duplicate.getAmountWithTax().negate() : null);
			duplicate.setRawAmountWithTax(duplicate.getRawAmountWithTax() != null ? duplicate.getRawAmountWithTax().negate() : null);
			duplicate.setRawAmountWithoutTax(duplicate.getRawAmountWithoutTax() != null ? duplicate.getRawAmountWithoutTax().negate() : null);
			duplicate.setTransactionalAmountTax(duplicate.getTransactionalAmountTax() != null ? duplicate.getTransactionalAmountTax().negate() : null);
			duplicate.setTransactionalAmountWithoutTax(duplicate.getTransactionalAmountWithoutTax() != null ? duplicate.getTransactionalAmountWithoutTax().negate()	: null);
			duplicate.setTransactionalAmountWithTax(duplicate.getTransactionalAmountWithTax() != null ? duplicate.getTransactionalAmountWithTax().negate() : null);
		}
		duplicate.setOriginRatedTransaction(ratedTransaction);
		ratedTransactionService.create(duplicate);
	}
}
