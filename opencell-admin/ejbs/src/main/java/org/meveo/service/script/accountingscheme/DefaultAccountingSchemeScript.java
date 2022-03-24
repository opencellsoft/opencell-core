package org.meveo.service.script.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.accountingscheme.AccountingJournalEntryService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless
public class DefaultAccountingSchemeScript extends Script {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    AccountingJournalEntryService accountingJournalEntryService = (AccountingJournalEntryService) getServiceInterface(AccountingJournalEntryService.class.getSimpleName());
    AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());
    OCCTemplateService occTemplateService = (OCCTemplateService) getServiceInterface(OCCTemplateService.class.getSimpleName());

    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("EXECUTE context {}", context);

        Long aoId = (Long) context.get(Script.CONTEXT_ENTITY);
        AccountOperation accountOperation = accountOperationService.findById(aoId);

        if (accountOperation == null) {
            log.warn("No AccountOperation found with [id={}]", aoId);
            context.put(Script.RESULT_VALUE, Collections.emptyList());
            return;
        }

        log.info("Process AccountOperation {}", accountOperation);

        // Get OCCTemplate by AccountOperation code
        OCCTemplate occT = occTemplateService.findByCode(accountOperation.getCode());

        if (occT == null) {
            log.warn("No OCCTemplate found for AccountingOperation [id={} - code={}]", accountOperation.getId(), accountOperation.getCode());
            context.put(Script.RESULT_VALUE, Collections.emptyList());
            return;
        }

        List<JournalEntry> journalEntries = accountingJournalEntryService.createFromAccountOperation(accountOperation, occT);

        context.put(Script.RESULT_VALUE, journalEntries);

    }
}
