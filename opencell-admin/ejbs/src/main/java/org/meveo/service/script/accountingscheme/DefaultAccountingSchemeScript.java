package org.meveo.service.script.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;

import java.util.List;
import java.util.Map;

public class DefaultAccountingSchemeScript extends Script {

    private JournalEntryService journalEntryService = (JournalEntryService) getServiceInterface(JournalEntryService.class.getSimpleName());
    private OCCTemplateService occTemplateService = (OCCTemplateService) getServiceInterface(OCCTemplateService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("DefaultAccountingSchemeScript EXECUTE context {}", context);

        AccountOperation ao = (AccountOperation) context.get(Script.CONTEXT_ENTITY);

        if (ao == null) {
            log.warn("No AccountOperation passed as CONTEXT_ENTITY");
            throw new BusinessException("No AccountOperation passed as CONTEXT_ENTITY");
        }

        log.info("Process AccountOperation {}", ao);

        // Get OCCTemplate by AccountOperation code
        OCCTemplate occT = occTemplateService.findByCode(ao.getCode());
        journalEntryService.validateOccTForAccountingScheme(ao, occT, true, false);

        List<JournalEntry> journalEntries = journalEntryService.createFromAccountOperation(ao, occT);

        context.put(Script.RESULT_VALUE, journalEntries);

    }

}
