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
 * Base class for external tax scripts.
 * 
 * @author Edward P. Legaspi
 */
public class TaxScript extends ModuleScript implements TaxScriptInterface {

    /**
     * Parameter User account
     */
    public static final String TAX_USER_ACCOUNT = "TAX_USER_ACCOUNT";

    /**
     * Parameter Invoice
     */
    public static final String TAX_INVOICE = "TAX_INVOICE";

    /**
     * Parameter invoice subcategory
     */
    public static final String TAX_INVOICE_SUB_CAT = "TAX_INVOICE_SUB_CAT";

    /**
     * Checks if this script is applicable to the context parameter.
     */
    @Override
    public boolean isApplicable(Map<String, Object> methodContext) throws BusinessException {
        return false;
    }

    /**
     * Computes the tax.
     */
    @Override
    public List<Tax> computeTaxes(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }

    @Override
    public Map<String, TaxInvoiceAgregate> createTaxAggregates(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }

    /**
     * Validates the account. Return base64 encoding of user and password.
     * 
     * @param username External system username
     * @param password External system password
     * @return Base64 encoding of user and password
     * @throws BusinessException The business exception
     */
    protected String validateAccount(String username, String password) throws BusinessException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new BusinessException("Invalid accountId:licenseKey");
        }

        return BaseEncoding.base64().encode(username.concat(":").concat(password).getBytes());
    }

}
