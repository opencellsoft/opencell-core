package org.meveo.admin.matching;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.IteratorBasedJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.AccountOperationService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @since V14
 */
@Stateless
public class AssignMatchingCodeToOldEntriesJobBean extends IteratorBasedJobBean<AccountOperation> {

    @Inject
    private AccountOperationService accountOperationService;
    @Inject
    private JournalEntryService journalEntryService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, null, this::executeProcess, null, null, null);
    }

    private Optional<Iterator<AccountOperation>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        List<AccountOperation> accountOperations = accountOperationService.findAoWithoutMatchingCode();

        if (CollectionUtils.isEmpty(accountOperations)) {
            log.warn("No AccountOperation found with a JournalEntry who has no matching code");
            return Optional.of(new SynchronizedIterator<>(Collections.emptyList()));
        }

        // remove duplicated ao
        Map<Long, AccountOperation> mapAo = new HashMap<>();
        accountOperations.forEach(recordedInvoice ->
                mapAo.put(recordedInvoice.getId(), recordedInvoice)
        );

        return Optional.of(new SynchronizedIterator<>(mapAo.values()));
    }

    private void executeProcess(List<AccountOperation> aos, JobExecutionResultImpl jobExecutionResult) throws BusinessException {
        Optional.ofNullable(aos).orElse(Collections.emptyList())
                .forEach(ao -> journalEntryService.assignMatchingCodeToJournalEntries(ao, null));
    }


}
