package org.meveo.service.script.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptUtils;

public class ValidateSubscriptionAgeScript extends Script {

	private static final long serialVersionUID = 1L;

	private InvoiceLineService invoiceLineService = (InvoiceLineService) getServiceInterface(InvoiceLineService.class.getSimpleName());

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);

		if (invoice == null) {
			log.warn("No Invoice passed as CONTEXT_ENTITY");
			throw new BusinessException("No Invoice passed as CONTEXT_ENTITY");
		}

		String operator = ScriptUtils.buildOperator(String.valueOf(context.get("operator")), false);
		List<Date> referenceDates = invoiceLineService.getCustomSubscriptionAge(invoice.getId(), buildReferenceDateExpression(String.valueOf(context.get("referenceDate"))));
		
		if (referenceDates == null || referenceDates.isEmpty()) {
			context.put(Script.INVOICE_VALIDATION_STATUS, true);
		} else {
			boolean result = referenceDates.stream().allMatch(dt -> ValueExpressionWrapper.evaluateToBoolean("#{" + invoice.getInvoiceDate().getTime() + " " + operator + " " + buildLimitDate(dt, (Integer) context.get("age")) + "}", new HashMap<Object, Object>(context)));
			context.put(Script.INVOICE_VALIDATION_STATUS, result);
		}
	}

	private long buildLimitDate(Date referenceDate, Integer age) {
		Calendar c = Calendar.getInstance();
		c.setTime(referenceDate);
		c.add(Calendar.DATE, age);
		return c.getTime().getTime();
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
