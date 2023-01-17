package org.meveo.service.script;

import java.util.Map;

import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceValidationScript extends Script {
	
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void execute(Map<String, Object> context) {
        log.info(">>> InvoiceValidationScript Method context >>>");
        context.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            log.info("{}={}", entry.getKey(), entry.getValue());
        });
        markRejectInvoice(context, "test");
    }   
	

	public void markRejectInvoice(Map<String, Object> methodContext, String rejectReason) {
		
		Object entity = methodContext.get(Script.CONTEXT_ENTITY);
		
		log.info("entity={}",entity!=null?((Invoice)entity).getId():null);
		methodContext.put(INVOICE_VALIDATION_STATUS, InvoiceValidationStatusEnum.REJECTED);
		methodContext.put(INVOICE_VALIDATION_REASON, rejectReason);
	}

	public void markSuspectInvoice(Map<String, Object> methodContext, String rejectReason) {
		methodContext.put(INVOICE_VALIDATION_STATUS, InvoiceValidationStatusEnum.SUSPECT);
		methodContext.put(INVOICE_VALIDATION_REASON, rejectReason);
	}
	
}