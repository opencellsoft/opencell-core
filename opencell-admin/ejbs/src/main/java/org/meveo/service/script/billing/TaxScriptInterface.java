package org.meveo.service.script.billing;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;

/**
 * Script interface for tax calculation related scripts
 * 
 * @author Edward P. Legaspi
 */
public interface TaxScriptInterface {

    /**
     * Determine if external tax calculation applies to the given parameters
     * 
     * @param methodContext values: userAccount, invoice, invoiceSubCategory
     * @return True if tax should be calculated externally
     * @throws BusinessException General business exception
     */
    boolean isApplicable(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Determines applicable taxes from an external web service
     * 
     * @param methodContext values: userAccount, invoice, invoiceSubCategory
     * @return A list of Tax entities
     * @throws BusinessException General business exception
     */
    List<Tax> computeTaxes(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Creates tax aggregates. Script should also update the tax amounts in all aggregates.
     * 
     * @param methodContext values: invoice
     * @return A map of tax aggregates with Tax code as a key
     * @throws BusinessException General business exception
     */
    Map<String, TaxInvoiceAgregate> createTaxAggregates(Map<String, Object> methodContext) throws BusinessException;
}
