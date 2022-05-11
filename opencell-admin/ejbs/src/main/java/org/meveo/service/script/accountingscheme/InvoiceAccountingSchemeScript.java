package org.meveo.service.script.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;

import java.util.Map;

public class InvoiceAccountingSchemeScript extends Script {

    private JournalEntryService journalEntryService = (JournalEntryService) getServiceInterface(JournalEntryService.class.getSimpleName());
    private OCCTemplateService occTemplateService = (OCCTemplateService) getServiceInterface(OCCTemplateService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("{} EXECUTE context {}", this.getClass().getCanonicalName(), context);

        AccountOperation ao = (AccountOperation) context.get(Script.CONTEXT_ENTITY);

        journalEntryService.validateAOForInvoiceScheme(ao);

        RecordedInvoice recordedInvoice = (RecordedInvoice) ao;

        log.info("Process RecordedInvoice {}", recordedInvoice.getId());

        OCCTemplate occT = occTemplateService.findByCode(recordedInvoice.getCode());
        journalEntryService.validateOccTForAccountingScheme(recordedInvoice, occT, false, false);

        context.put(Script.RESULT_VALUE, journalEntryService.createFromInvoice(recordedInvoice, occT));


    }

}
