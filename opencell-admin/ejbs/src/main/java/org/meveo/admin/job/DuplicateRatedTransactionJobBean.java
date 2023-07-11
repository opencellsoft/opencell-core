package org.meveo.admin.job;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

@Stateless
public class DuplicateRatedTransactionJobBean extends IteratorBasedJobBean<RatedTransaction>{

    @Inject
    private RatedTransactionService ratedTransactionService;
    @Override
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::processRatedTransactionToDuplicate, null, null, null);
    }
    private Optional<Iterator<RatedTransaction>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return Optional.of(new SynchronizedIterator<RatedTransaction>(ratedTransactionService.getEntityManager().createNamedQuery("RatedTransaction.findPendingOrNegateDuplicated").getResultList()));
    }

    private void processRatedTransactionToDuplicate(RatedTransaction ratedTransaction, JobExecutionResultImpl jobExecutionResult) {
        duplicateRated(ratedTransaction, ratedTransaction.getPendingDuplicates(), false);
        duplicateRated(ratedTransaction, ratedTransaction.getPendingDuplicatesToNegate(), true);
    }

    private void duplicateRated(RatedTransaction ratedTransaction, int numberIteration, boolean isNegate) {
        for(int i = 0; i < numberIteration; ++i) {
            RatedTransaction duplicate = new RatedTransaction(ratedTransaction);
            if(isNegate) {
                duplicate.setUnitAmountTax(duplicate.getUnitAmountTax() != null ? duplicate.getUnitAmountTax().negate() : null);
                duplicate.setUnitAmountWithoutTax(duplicate.getUnitAmountWithoutTax() != null ? duplicate.getUnitAmountWithoutTax().negate() : null);
                duplicate.setUnitAmountWithTax(duplicate.getUnitAmountWithTax() != null ? duplicate.getUnitAmountWithTax().negate() : null);
                duplicate.setAmountTax(duplicate.getAmountTax() != null ? duplicate.getAmountTax().negate() : null);
                duplicate.setAmountWithoutTax(duplicate.getAmountWithoutTax() != null ? duplicate.getAmountWithoutTax().negate() : null);
                duplicate.setAmountWithTax(duplicate.getAmountWithTax() != null ? duplicate.getAmountWithTax().negate() : null);
                duplicate.setRawAmountWithTax(duplicate.getRawAmountWithTax() != null ? duplicate.getRawAmountWithTax().negate() : null);
                duplicate.setRawAmountWithoutTax(duplicate.getRawAmountWithoutTax() != null ? duplicate.getRawAmountWithoutTax().negate() : null);
                duplicate.setTransactionalAmountTax(duplicate.getTransactionalAmountTax() != null ? duplicate.getTransactionalAmountTax().negate() : null);
                duplicate.setTransactionalAmountWithoutTax(duplicate.getTransactionalAmountWithoutTax() != null ? duplicate.getTransactionalAmountWithoutTax().negate() : null);
                duplicate.setTransactionalAmountWithTax(duplicate.getTransactionalAmountWithTax() != null ? duplicate.getTransactionalAmountWithTax().negate() : null);
                ratedTransaction.setPendingDuplicatesToNegate(ratedTransaction.getPendingDuplicatesToNegate() - 1);
            }else{
                ratedTransaction.setPendingDuplicates(ratedTransaction.getPendingDuplicates() - 1);
            }
            duplicate.setOriginRatedTransaction(ratedTransaction);
            ratedTransactionService.create(duplicate);

        }
    }
}
