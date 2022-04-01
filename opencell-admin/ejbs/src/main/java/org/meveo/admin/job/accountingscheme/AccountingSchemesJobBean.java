package org.meveo.admin.job.accountingscheme;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.IteratorBasedJobBean;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.AccountingScheme;
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
import java.util.stream.Collectors;

/**
 * @since 13
 */
@Stateless
public class AccountingSchemesJobBean extends IteratorBasedJobBean<Long> {

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

    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        Boolean onlyClosedPeriods = (Boolean) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "onlyClosedPeriods");

        List<AccountOperation> accountOperations = accountOperationService.findAoByStatus(onlyClosedPeriods, AccountOperationStatus.POSTED, AccountOperationStatus.EXPORT_FAILED);

        if (accountOperations == null) {
            log.warn("No AccountOperation found witch onlyClosedPeriods={}", onlyClosedPeriods);
            return Optional.of(new SynchronizedIterator<>(Collections.emptyList()));
        }

        return Optional.of(new SynchronizedIterator<>(accountOperations.stream()
                .map(AccountOperation::getId).collect(Collectors.toList())));
    }

    private void executeScript(List<Long> idAOs, JobExecutionResultImpl jobExecutionResult) throws BusinessException {
        List<AccountOperation> accountOperations = accountOperationService.findByIds(idAOs);

        Optional.ofNullable(accountOperations).orElse(Collections.emptyList())
                .forEach(accountOperation -> {
                    // Find OOC Template
                    OCCTemplate occT = occTemplateService.findByCode(accountOperation.getCode());

                    if (occT == null) {
                        log.warn("No OCCTemplate found for AccountOperation [id={}]...skip AccountingSchemesJob process for this instance", accountOperation.getId());
                        return;
                    }

                    if (occT.getAccountingScheme() == null) {
                        log.warn("Ignored account operation (id={}, type={}, code={}): no scheme set",
                                accountOperation.getId(), accountOperation.getType(), accountOperation.getCode());
                        accountOperationService.updateStatusInNewTransaction(List.of(accountOperation), AccountOperationStatus.EXPORT_FAILED);
                        markAsError("Ignored account operation (id=" + accountOperation.getId() + ", type=" + accountOperation.getType() + ")" +
                                        ": no scheme set for account operation type (id=" + occT.getId() + ", code=" + occT.getCode() + ")",
                                jobExecutionResult);
                    }

                    ScriptInterface script = findScript(accountOperation, occT.getAccountingScheme(), jobExecutionResult);

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

    private void markAsError(String error, JobExecutionResultImpl jobExecutionResult) {
        jobExecutionResult.registerError(error);
        throw new BusinessException(error);
    }

    private ScriptInterface findScript(AccountOperation ao, AccountingScheme as, JobExecutionResultImpl jobExecutionResult) {
        try {
            return scriptInstanceService.getScriptInstance(as.getScriptInstance().getCode());
        } catch (BusinessException e) {
            log.error("Error during loading script by code={} | {}", as.getScriptInstance().getCode(), e.getMessage(), e);
            accountOperationService.updateStatusInNewTransaction(List.of(ao), AccountOperationStatus.EXPORT_FAILED);
            jobExecutionResult.registerError("Account operation (id=" + ao.getId() + ", type=" + ao.getType() + ")" +
                    " couldn't be processed by accounting scheme (code=" + as.getScriptInstance().getCode() + "): " + e.getMessage());
            throw new BusinessException(e);
        }

    }

}
