package org.meveo.service.script.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptUtils;

public class ValidateCustomerAgeScript extends Script {

	private static final long serialVersionUID = 1L;

	private BillingAccountService billingAccountService = (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);

		if (invoice == null) {
			log.warn("No Invoice passed as CONTEXT_ENTITY");
			throw new BusinessException("No Invoice passed as CONTEXT_ENTITY");
		}

		String operator = ScriptUtils.buildOperator(String.valueOf(context.get("operator")), false);
		Date referenceDate = billingAccountService.getDateCustomerAge(invoice.getBillingAccount().getId(), buildReferenceDateExpression(String.valueOf(context.get("referenceDate"))));
		long limitDate = buildLimitDate(referenceDate, (Integer) context.get("age"));

		
		boolean result = ValueExpressionWrapper.evaluateToBoolean("#{" + invoice.getInvoiceDate().getTime() + " " + operator + " " + limitDate + "}", new HashMap<Object, Object>(context));

		context.put(Script.INVOICE_VALIDATION_STATUS, result);
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
		case "Customer creation":
			referenceDateExpression = "ba.customerAccount.customer.auditable.created";
			break;
		case "Customer Account creation":
			referenceDateExpression = "ba.customerAccount.auditable.created";
			break;
		case "Billing Account creation":
			referenceDateExpression = "ba.auditable.created";
			break;
		case "Billing Account subscription":
			referenceDateExpression = "ba.subscriptionDate";
			break;
		default:
			referenceDateExpression = "ba.subscriptionDate";
			break;
		}
		return referenceDateExpression;
	}

}
