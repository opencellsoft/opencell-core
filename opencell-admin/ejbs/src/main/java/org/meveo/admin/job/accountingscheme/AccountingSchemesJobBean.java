package org.meveo.admin.job.accountingscheme;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.IteratorBasedJobBean;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.*;

/**
 * @since 13
 */
@Stateless
public class AccountingSchemesJobBean extends IteratorBasedJobBean<AccountOperation> {

    @Inject
    private AccountOperationService accountOperationService;
    @Inject
    private ScriptInstanceService scriptInstanceService;
    @Inject
    private OCCTemplateService occTemplateService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::executeScript, null, null);
    }

    private Optional<Iterator<AccountOperation>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        Boolean onlyClosedPeriods = (Boolean) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "onlyClosedPeriods");

        List<AccountOperation> accountOperations = accountOperationService.findAoByStatus(onlyClosedPeriods, AccountOperationStatus.POSTED, AccountOperationStatus.EXPORT_FAILED);

        if (accountOperations == null) {
            log.warn("No AccountOperation found witch onlyClosedPeriods={}", onlyClosedPeriods);
            return Optional.of(new SynchronizedIterator<>(Collections.emptyList()));
        }

        return Optional.of(new SynchronizedIterator<>(accountOperations));
    }

    private void executeScript(List<AccountOperation> accountOperations, JobExecutionResultImpl jobExecutionResult) throws BusinessException {
        Optional.ofNullable(accountOperations).orElse(Collections.emptyList())
                .forEach(accountOperation -> {
                    // Find OOC Template
                    OCCTemplate occT = occTemplateService.findByCode(accountOperation.getCode());

                    if (occT == null) {
                        log.warn("No OCCTemplate found for AccountOperation [id={}]...skip AccountingSchemesJob process for this instance", accountOperation.getId());
                        return;
                        // throw new BusinessException("No OCCTemplate found for AccountOperation id=" + accountOperation.getId());
                    }

                    if (occT.getAccountingScheme() == null) {
                        log.warn("Ignored account operation (id={}, type={}, code={}): no scheme set",
                                accountOperation.getId(), accountOperation.getType(), accountOperation.getCode());
                        accountOperationService.updateStatusInNewTransaction(Arrays.asList(accountOperation), AccountOperationStatus.EXPORT_FAILED);
                        throw new BusinessException("No scheme found for OCCTemplate id=" + occT.getId());
                    }

                    ScriptInterface script = scriptInstanceService.getScriptInstance(occT.getAccountingScheme().getCode());

                    if (script == null) {
                        log.warn("No Script linked to AccountingScheme with code={}", occT.getAccountingScheme().getCode());
                        return;
                    }

                    Map<String, Object> methodContext = new HashMap<>();
                    methodContext.put(Script.CONTEXT_ENTITY, accountOperation);
                    methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);

                    script.execute(methodContext);

                    List<JournalEntry> createdEntries = (List<JournalEntry>) methodContext.get(Script.RESULT_VALUE);
                    log.info("Process {} JournalEntry for AO={}, OCC={}, ASCH={}",
                            createdEntries.size(), accountOperation.getId(), occT.getId(), occT.getAccountingScheme().getCode());
                    if (!createdEntries.isEmpty()) {
                        accountOperation.setStatus(AccountOperationStatus.EXPORTED);
                    } else {
                        accountOperation.setStatus(AccountOperationStatus.EXPORT_FAILED);
                    }

                    accountOperationService.update(accountOperation);

                });
    }

}