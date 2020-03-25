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
     * @param methodContext values: userAccount, seller, tax class, date
     * @return True if tax should be calculated externally
     * @throws BusinessException General business exception
     */
    boolean isApplicable(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Determines applicable taxes from an external web service
     * 
     * @param methodContext values: userAccount, seller, tax class, date
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
