package org.meveo.service.script.accountingscheme;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DefaultAccountingSchemeScript extends Script {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private JournalEntryService journalEntryService = (JournalEntryService) getServiceInterface(JournalEntryService.class.getSimpleName());
    private OCCTemplateService occTemplateService = (OCCTemplateService) getServiceInterface(OCCTemplateService.class.getSimpleName());

    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("EXECUTE context {}", context);

        AccountOperation ao = (AccountOperation) context.get(Script.CONTEXT_ENTITY);

        if (ao == null) {
            log.warn("No AccountOperation passed as CONTEXT_ENTITY");
            throw new BusinessException("No AccountOperation passed as CONTEXT_ENTITY");
        }

        log.info("Process AccountOperation {}", ao);

        // Get OCCTemplate by AccountOperation code
        OCCTemplate occT = (OCCTemplate) Hibernate.unproxy(occTemplateService.findByCode(ao.getCode()));

        if (occT == null) {
            log.warn("No OCCTemplate found for AccountingOperation [id={}]", ao.getId());
            throw new BusinessException("No OCCTemplate found for AccountingOperation id=" + ao.getId());
        }

        if (occT.getAccountingCode() == null) {
            log.warn("Mandatory AccountingCode not found for OCCTemplate id={}", occT.getId());
            throw new BusinessException("Mandatory AccountingCode not found for OCCTemplate id=" + occT.getId());
        }

        if (occT.getContraAccountingCode() == null) {
            log.warn("Mandatory ContraAccountingCode not found for OCCTemplate id={}", occT.getId());
            throw new BusinessException("Mandatory AccountingCode not found for OCCTemplate id=" + occT.getId());
        }

        List<JournalEntry> journalEntries = journalEntryService.createFromAccountOperation(ao, occT);

        context.put(Script.RESULT_VALUE, journalEntries);

    }

}
