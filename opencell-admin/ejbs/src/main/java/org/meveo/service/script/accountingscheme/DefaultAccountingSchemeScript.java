package org.meveo.service.script.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultAccountingSchemeScript extends Script {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    JournalEntryService journalEntryService = (JournalEntryService) getServiceInterface(JournalEntryService.class.getSimpleName());
    AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(JournalEntryService.class.getSimpleName());

    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("EXECUTE context {}", context);

        Long aoId = (Long) context.get(Script.CONTEXT_ENTITY);
        AccountOperation accountOperation = accountOperationService.findById(aoId);

        if (accountOperation == null) {
            log.info("No AccountOperation found with id:{}", aoId);
            context.put(Script.RESULT_VALUE, Collections.emptyList());
            return;
        }

        log.info("Process AccountOperation {}", accountOperation);

        OCCTemplate occT = new OCCTemplate(); // FIXME get ccTemplate from AccountOperation

        List<JournalEntry> journalEntries = journalEntryService.createFromAccountOperation(accountOperation, occT);

        context.put(Script.RESULT_VALUE, journalEntries);

    }
}
