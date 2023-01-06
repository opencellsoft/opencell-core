package org.meveo.service.script.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptUtils;

public class ValidateSubscriptionAgeScript extends Script {

	private static final long serialVersionUID = 1L;

	private InvoiceLineService invoiceLineService = (InvoiceLineService) getServiceInterface(InvoiceLineService.class.getSimpleName());

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		log.info("ValidateSubscriptionAgeScript EXECUTE context {}", context);

		Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);

		if (invoice == null) {
			log.warn("No Invoice passed as CONTEXT_ENTITY");
			throw new BusinessException("No Invoice passed as CONTEXT_ENTITY");
		}

		log.info("Process ValidateSubscriptionAgeScript {}", invoice);

		long counter = invoiceLineService.getCountBySubscriptionAge(invoice.getId(),
				buildReferenceDateExpression(String.valueOf(context.get("referenceDate"))),
				ScriptUtils.buildOperator(String.valueOf(context.get("operator")), true),
				buildLimitDate(invoice, (Integer) context.get("age")));

		context.put(Script.INVOICE_VALIDATION_STATUS, counter > 0 ? InvoiceValidationStatusEnum.VALID : (InvoiceValidationStatusEnum) context.get(Script.RESULT_VALUE));
		
		log.info("Result Processing ValidateSubscriptionAgeScript {}", context.get(Script.INVOICE_VALIDATION_STATUS));
	}

	private Date buildLimitDate(Invoice invoice, Integer age) {
		Calendar c = Calendar.getInstance();
		c.setTime(invoice.getInvoiceDate());
		c.add(Calendar.DATE, -age);
		return c.getTime();
	}

	private String buildReferenceDateExpression(String referenceDate) {
		String referenceDateExpression;
		switch (referenceDate) {
		case "Subscription creation":
			referenceDateExpression = "il.subscription.auditable.created";
			break;
		case "Subscription date":
			referenceDateExpression = "il.subscription.subscriptionDate";
			break;
		default:
			referenceDateExpression = "il.subscription.subscriptionDate";
			break;
		}
		return referenceDateExpression;
	}

}
