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
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

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
import java.util.stream.Collectors;

/**
 * @since 13
 */
@Stateless
public class AccountingSchemesJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = -8747049868178914722L;

    @Inject
    private AccountOperationService accountOperationService;
    @Inject
    private ScriptInstanceService scriptInstanceService;
    @Inject
    private OCCTemplateService occTemplateService;
    @Inject
    private JournalEntryService journalEntryService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, null, this::executeScript, null, null, null);
    }

    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        Boolean onlyClosedPeriods = (Boolean) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "onlyClosedPeriods");

        List<AccountOperation> accountOperations = accountOperationService.findAoByStatus(onlyClosedPeriods, AccountOperationStatus.POSTED, AccountOperationStatus.EXPORT_FAILED);

        if (accountOperations == null) {
            log.warn("No AccountOperation found with onlyClosedPeriods={}", onlyClosedPeriods);
            return Optional.of(new SynchronizedIterator<>(Collections.emptyList()));
        }

        return Optional.of(new SynchronizedIterator<>(accountOperations.stream().map(AccountOperation::getId).collect(Collectors.toList())));
    }

    private void executeScript(List<Long> idAOs, JobExecutionResultImpl jobExecutionResult) throws BusinessException {
        List<AccountOperation> accountOperations = accountOperationService.findByIds(idAOs);

        Optional.ofNullable(accountOperations).orElse(Collections.emptyList()).forEach(accountOperation -> {
                    OCCTemplate occT = null;
                    try {
                        // Find OOC Template
                        occT = occTemplateService.findByCode(accountOperation.getCode());

                        if (occT == null) {
                            log.warn("No OCCTemplate found for AccountOperation [id={}]...skip AccountingSchemesJob process for this instance", accountOperation.getId());
                            return;
                        }

                        if (occT.getAccountingScheme() == null) {
                            log.warn("Ignored account operation (id={}, type={}, code={}): no scheme set",
                                    accountOperation.getId(), accountOperation.getType(), accountOperation.getCode());
                            throw new BusinessException("No scheme set for account operation type = " + occT.getCode());
                        }

                        ScriptInterface script = findScript(accountOperation, occT.getAccountingScheme());

                        Map<String, Object> methodContext = new HashMap<>();
                        methodContext.put(Script.CONTEXT_ENTITY, accountOperation);
                        methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                        methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);

                        script.execute(methodContext);

                        List<JournalEntry> createdEntries = (List<JournalEntry>) methodContext.get(Script.RESULT_VALUE);
                log.info("Process {} JournalEntry for AO={}, OCC={}, ASCH={}", createdEntries == null ? 0 : createdEntries.size(), accountOperation.getId(), occT.getId(), occT.getAccountingScheme().getCode());

                        accountOperation.setStatus(AccountOperationStatus.EXPORTED);
                        accountOperationService.update(accountOperation);
                        journalEntryService.assignMatchingCodeToJournalEntries(accountOperation, createdEntries);

                    } catch (BusinessException e) {
                        String error = "Ignored account operation (id=" + accountOperation.getId() + ", type=" + accountOperation.getType() + ") : " + e.getMessage();
                        jobExecutionResult.registerError(error);
                        accountOperationService.updateStatusInNewTransaction(List.of(accountOperation), AccountOperationStatus.EXPORT_FAILED, e.getMessage());
                        throw new BusinessException(e);
                    } catch (Exception e) {
                        log.error("Error during process AO={} - {}", accountOperation.getId(), e.getMessage());
                        log.debug("Error during process AO={} - {}", accountOperation.getId(), e.getMessage(), e);
                jobExecutionResult.registerError(buildTechnicalError(accountOperation, (occT == null ? "UNDEFINED" : occT.getAccountingScheme().getScriptInstance().getCode()), e));
                        accountOperationService.updateStatusInNewTransaction(List.of(accountOperation), AccountOperationStatus.EXPORT_FAILED, e.getMessage());
                        throw new BusinessException(e);
                    }

                });

    }

    private ScriptInterface findScript(AccountOperation ao, AccountingScheme as) {
        try {
            return scriptInstanceService.getScriptInstance(as.getScriptInstance().getCode());
        } catch (BusinessException e) {
            log.error("Error during loading script by code={} | {}", as.getScriptInstance().getCode(), e.getMessage(), e);
            accountOperationService.updateStatusInNewTransaction(List.of(ao), AccountOperationStatus.EXPORT_FAILED,
                    "Error during loading script by code=" + as.getScriptInstance().getCode());
            throw new BusinessException(buildTechnicalError(ao, as.getScriptInstance().getCode(), e));
        }

    }

    private String buildTechnicalError(AccountOperation ao, String scriptCode, Exception e) {
        return "Account operation (id=" + ao.getId() + ", type=" + ao.getType() + ")" + " couldn't be processed by accounting scheme (code=" + scriptCode + "): " + e.getMessage();
    }

}
