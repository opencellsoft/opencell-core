package org.meveo.service.script;

import java.util.Map;

import org.meveo.model.billing.InvoiceValidationStatusEnum;

public class InvoiceValidationScript extends Script {
	

	public void markRejectInvoice(Map<String, Object> methodContext, String rejectReason) {
		methodContext.put(INVOICE_VALIDATION_STATUS, InvoiceValidationStatusEnum.REJECTED);
		methodContext.put(INVOICE_VALIDATION_REASON, rejectReason);
	}

	public void markSuspectInvoice(Map<String, Object> methodContext, String rejectReason) {
		methodContext.put(INVOICE_VALIDATION_STATUS, InvoiceValidationStatusEnum.SUSPECT);
		methodContext.put(INVOICE_VALIDATION_REASON, rejectReason);
	}
	
}