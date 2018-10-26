package org.meveo.service.script.billing;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;

/**
 * @author Edward P. Legaspi
 */
public interface TaxScriptInterface {

	/**
	 * Returns a boolean value that when true retrieves a list of taxes from
	 * external web service.
	 * 
	 * @param methodContext values: userAccount, invoice, invoiceSubCategory
	 * @return
	 * @throws BusinessException
	 */
	boolean isApplicable(Map<String, Object> methodContext) throws BusinessException;

	/**
	 * Retrieves a list of taxes from an external web service.
	 * 
	 * @param methodContext values: userAccount, invoice, invoiceSubCategory
	 * @return
	 * @throws BusinessException
	 */
	List<Tax> computeTaxes(Map<String, Object> methodContext) throws BusinessException;
	
	/**
	 * Computes the tax aggregate map.
	 * 
	 * @param methodContext values: invoice, Map&lt;Long, CategoryInvoiceAgregate&gt; catInvoiceAgregateMap, SubCategoryInvoiceAgregate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, TaxInvoiceAgregate> computeTaxAggregateMap(Map<String, Object> methodContext) throws BusinessException;
}
