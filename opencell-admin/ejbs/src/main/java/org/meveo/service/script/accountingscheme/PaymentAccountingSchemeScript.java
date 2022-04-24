package org.meveo.service.script.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.Payment;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.Script;

import java.util.Map;

public class PaymentAccountingSchemeScript extends Script {

    private JournalEntryService journalEntryService = (JournalEntryService) getServiceInterface(JournalEntryService.class.getSimpleName());
    private OCCTemplateService occTemplateService = (OCCTemplateService) getServiceInterface(OCCTemplateService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("{} EXECUTE context {}", this.getClass().getCanonicalName(), context);

        AccountOperation ao = (AccountOperation) context.get(Script.CONTEXT_ENTITY);

        if (!(ao instanceof Payment)) {
            log.error("AccountOperation {} is not Payment type, abort processing", ao.getId());
            throw new BusinessException("AccountOperation " + ao.getId() + " is not Payment type");
        }

        Payment paymentAo = (Payment) ao;

        log.info("Process Payment {}", paymentAo.getId());

        OCCTemplate occT = occTemplateService.findByCode(ao.getCode());
        journalEntryService.validateOccTForAccountingScheme(ao, occT, false);

        context.put(Script.RESULT_VALUE, journalEntryService.createFromPayment(paymentAo, occT));

    }

}
