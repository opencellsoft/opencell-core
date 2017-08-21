package org.meveo.service.script.billing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @created 16 Aug 2017
 */
@Singleton
@Startup
public class TaxScriptService implements Serializable {

	private static final long serialVersionUID = 8771932761605219308L;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	public boolean isApplicable(String scriptCode, UserAccount userAccount, Invoice invoice,
			InvoiceSubCategory invoiceSubCategory) throws BusinessException {
		TaxScriptInterface scriptInterface = (TaxScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(TaxScript.TAX_USER_ACCOUNT, userAccount);
		scriptContext.put(TaxScript.TAX_INVOICE, invoice);
		scriptContext.put(TaxScript.TAX_INVOICE_SUB_CAT, invoiceSubCategory);

		return scriptInterface.isApplicable(scriptContext);
	}

	public List<Tax> computeTaxes(String scriptCode, UserAccount userAccount, Invoice invoice,
			InvoiceSubCategory invoiceSubCategory) throws BusinessException {
		TaxScriptInterface scriptInterface = (TaxScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(TaxScript.TAX_USER_ACCOUNT, userAccount);
		scriptContext.put(TaxScript.TAX_INVOICE, invoice);
		scriptContext.put(TaxScript.TAX_INVOICE_SUB_CAT, invoiceSubCategory);

		return scriptInterface.computeTaxes(scriptContext);
	}

	public Map<String, TaxInvoiceAgregate> computeTaxAggregateMap(String scriptCode, Invoice invoice,
			Map<Long, CategoryInvoiceAgregate> catInvoiceAgregateMap) throws BusinessException {
		TaxScriptInterface scriptInterface = (TaxScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(TaxScript.TAX_INVOICE, invoice);
		scriptContext.put(TaxScript.TAX_CAT_INV_AGGREGATE_MAP, catInvoiceAgregateMap);

		return scriptInterface.computeTaxAggregateMap(scriptContext);
	}

}
