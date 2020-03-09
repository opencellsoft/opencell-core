/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

    private static final long serialVersionUID = -7676724555991823006L;

    /**
     * Parameter User account
     */
    public static final String TAX_USER_ACCOUNT = "TAX_USER_ACCOUNT";

    /**
     * Parameter Invoice
     */
    public static final String TAX_INVOICE = "TAX_INVOICE";

    /**
     * Parameter Seller
     */
    public static final String TAX_SELLER = "TAX_SELLER";

    /**
     * Parameter Tax class
     */
    public static final String TAX_TAX_CLASS = "TAX_TAX_CLASS";

    /**
     * Parameter Date
     */
    public static final String TAX_DATE = "TAX_DATE";

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