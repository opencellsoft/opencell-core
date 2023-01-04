package org.meveo.service.script;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author are
 *
 */
public class CompareInvoiceAmountScript extends Script {

	private static final long serialVersionUID = -3842955629518523594L;

	private static final Logger LOG = LoggerFactory.getLogger(CompareInvoiceAmountScript.class);
	
	@Override
	public void execute(Map<String, Object> context) {
		context.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			LOG.info("{}={}", entry.getKey(), entry.getValue());
		});
		
		
		Invoice invoice = (Invoice) context.get(Script.CONTEXT_ENTITY);
		if(invoice == null) {
			throw new MissingParameterException(Script.CONTEXT_ENTITY);
		}
		LOG.info("Invoice amoutWithoutTax={}, amountWithTax={}", invoice.getAmountWithoutTax(), invoice.getAmountWithTax());

		String withOrWithTaxParameter = (String) context.get("withOrWithoutTax");
		BigDecimal value = (BigDecimal) context.get("value");
		String operator = (String) context.get("operator");
		if("=".equals(operator)) {
			operator = "==";
		}
		
		boolean result = ValueExpressionWrapper.evaluateToBoolean("#{invoice.amount" + StringUtils.capitalize(withOrWithTaxParameter) + " " + operator + " " + value + "}",
				new HashMap<Object, Object>(context));

		context.put(Script.INVOICE_VALIDATION_STATUS, result ? InvoiceValidationStatusEnum.VALID : (InvoiceValidationStatusEnum) context.get(Script.RESULT_VALUE));
	}

}
