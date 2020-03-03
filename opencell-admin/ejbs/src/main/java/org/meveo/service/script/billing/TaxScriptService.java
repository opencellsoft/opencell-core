package org.meveo.service.script.billing;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.script.ScriptInstanceService;

/**
 * Takes care of tax related script method invocation.
 * 
 * @author Edward P. Legaspi
 */
@Stateless
public class TaxScriptService implements Serializable {

    private static final long serialVersionUID = 8771932761605219308L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Determine if external tax calculation applies to the given parameters
     * 
     * @param scriptCode Tax script code
     * @param userAccount User account
     * @param seller Seller
     * @param taxClass Tax class
     * @param date Date to determine tax for
     * @return True if tax should be calculated externally
     * @throws BusinessException General business exception
     */
    public boolean isApplicable(String scriptCode, UserAccount userAccount, Seller seller, TaxClass taxClass, Date date) throws BusinessException {
        TaxScriptInterface scriptInterface = (TaxScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(TaxScript.TAX_USER_ACCOUNT, userAccount);
        scriptContext.put(TaxScript.TAX_SELLER, seller);
        scriptContext.put(TaxScript.TAX_TAX_CLASS, taxClass);
        scriptContext.put(TaxScript.TAX_DATE, date);

        return scriptInterface.isApplicable(scriptContext);
    }

    /**
     * Determines applicable taxes from an external web service
     * 
     * @param scriptCode Tax script code
     * @param userAccount User account
     * @param seller Seller
     * @param taxClass Tax class
     * @param date Date to determine tax for
     * @return A list of tax entities
     * @throws BusinessException General business exception
     */
    public List<Tax> computeTaxes(String scriptCode, UserAccount userAccount, Seller seller, TaxClass taxClass, Date date) throws BusinessException {
        TaxScriptInterface scriptInterface = (TaxScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(TaxScript.TAX_USER_ACCOUNT, userAccount);
        scriptContext.put(TaxScript.TAX_SELLER, seller);
        scriptContext.put(TaxScript.TAX_TAX_CLASS, taxClass);
        scriptContext.put(TaxScript.TAX_DATE, date);

        return scriptInterface.computeTaxes(scriptContext);
    }

    /**
     * Creates tax aggregates. Script should also update the tax amounts in all aggregates.
     * 
     * @param scriptCode Tax script code
     * @param invoice Invoice
     * @return A map of tax aggregates with Tax code as a key
     * @throws BusinessException General business exception
     */
    public Map<String, TaxInvoiceAgregate> createTaxAggregates(String scriptCode, Invoice invoice) throws BusinessException {

        TaxScriptInterface scriptInterface = (TaxScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(TaxScript.TAX_INVOICE, invoice);

        return scriptInterface.createTaxAggregates(scriptContext);
    }
}