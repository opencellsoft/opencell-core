package org.meveo.service.script.billing;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.service.script.module.ModuleScript;

import com.google.common.io.BaseEncoding;

/**
 * @author Edward P. Legaspi 16
 * @created 16 Aug 2017
 */
public class TaxScript extends ModuleScript implements TaxScriptInterface {

	public static final String TAX_USER_ACCOUNT = "TAX_USER_ACCOUNT";
	public static final String TAX_INVOICE = "TAX_INVOICE";
	public static final String TAX_INVOICE_SUB_CAT = "TAX_INVOICE_SUB_CAT";
	public static final String TAX_CAT_INV_AGGREGATE_MAP = "TAX_CAT_INV_AGGREGATE_MAP";

	@Override
	public boolean isApplicable(Map<String, Object> methodContext) throws BusinessException {
		return false;
	}

	@Override
	public List<Tax> computeTaxes(Map<String, Object> methodContext) throws BusinessException {
		return null;
	}

	@Override
	public Map<Long, TaxInvoiceAgregate> computeTaxAggregateMap(Map<String, Object> methodContext)
			throws BusinessException {
		return null;
	}
	
	protected String validateAccount(String username, String password) throws BusinessException {		
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new BusinessException("Invalid accountId:licenseKey");
		}
		
		String account = BaseEncoding.base64().encode(username.concat(":").concat(password).getBytes());
		
		return account;
	}

}
