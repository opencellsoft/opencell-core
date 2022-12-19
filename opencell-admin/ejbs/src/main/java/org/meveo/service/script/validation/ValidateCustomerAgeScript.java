package org.meveo.service.script.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.script.Script;

public class ValidateCustomerAgeScript extends Script {

	private static final long serialVersionUID = 1L;

	private BillingAccountService billingAccountService = (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		log.info("ValidateCustomerAgeScript EXECUTE context {}", context);

		Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);

		if (invoice == null) {
			log.warn("No Invoice passed as CONTEXT_ENTITY");
			throw new BusinessException("No Invoice passed as CONTEXT_ENTITY");
		}

		log.info("Process ValidateCustomerAgeScript {}", invoice);

		long counter = billingAccountService.getCountByCustomerAge(invoice.getBillingAccount().getId(),
				buildReferenceDateExpression(String.valueOf(context.get("referenceDate"))),
				buildOperator(String.valueOf(context.get("operator"))),
				buildLimitDate(invoice, (Integer) context.get("age")));

		context.put(Script.RESULT_VALUE, counter == 0);
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

	private String buildOperator(String operator) {
		String operatorExpression;
		switch (operator) {
		case "≤":
			operatorExpression = "<=";
			break;
		case "≠":
			operatorExpression = "<>";
			break;
		case "≥":
			operatorExpression = ">=";
			break;
		default:
			operatorExpression = ">";
			break;
		}
		return operatorExpression;
	}

}
