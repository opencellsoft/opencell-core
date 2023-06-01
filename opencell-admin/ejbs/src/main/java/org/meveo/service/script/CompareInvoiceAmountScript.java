
package org.meveo.service.script;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.ValueExpressionWrapper;

public class CompareInvoiceAmountScript extends Script {

	private static final long serialVersionUID = -3842955629518523594L;

	@Override
	public void execute(Map<String, Object> context) {
		Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);
		if(invoice == null) {
			throw new MissingParameterException(Script.CONTEXT_ENTITY);
		}

		String withOrWithTaxParameter = (String) context.get("withOrWithoutTax");
		BigDecimal value = (BigDecimal) context.get("value");
		String operator = ScriptUtils.buildOperator(String.valueOf(context.get("operator")), false);
		Map<Object, Object> contextMap = new HashMap<Object, Object>();
		contextMap.put("invoice", invoice);
		
		boolean result = ValueExpressionWrapper.evaluateToBoolean(
				"#{invoice.amount" + StringUtils.camelcase(withOrWithTaxParameter) + ".longValue() " + operator + " " + value + "}", contextMap);

		context.put(Script.INVOICE_VALIDATION_STATUS, result);
	}

}
