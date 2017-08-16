package org.meveo.service.script.billing;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Tax;
import org.meveo.service.script.module.ModuleScript;

/**
 * @author Edward P. Legaspi 16
 * @created 16 Aug 2017
 */
public class TaxScript extends ModuleScript implements TaxScriptInterface {

	public static final String TAX_USER_ACCOUNT = "TAX_USER_ACCOUNT";
	public static final String TAX_INVOICE = "TAX_INVOICE";
	public static final String TAX_INVOICE_SUB_CAT = "TAX_INVOICE_SUB_CAT";

	@Override
	public boolean isApplicable(Map<String, Object> methodContext) throws BusinessException {
		return false;
	}

	@Override
	public List<Tax> computeTaxes(Map<String, Object> methodContext) throws BusinessException {
		return null;
	}

	@Override
	public List<Tax> computeTaxAggregateMap(Map<String, Object> methodContext) throws BusinessException {
		return null;
	}

}
