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

package org.meveo.service.script.account;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
public interface AccountScriptInterface extends ScriptInterface {

	/**
	 * Called after Account entity creation
	 * 
	 * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=AccountEntity and CONTEXT_SELLER=The current seller..
	 * @throws BusinessException business exception.
	 */
    void createAccount(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after Account entity update.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=AccountEntity and CONTEXT_SELLER=The current seller..
     * @throws BusinessException business exception.
     */
    void updateAccount(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after either Billing or User account termination.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=BillingAccount or a UserAccount.
     * @throws BusinessException business exception.
     */
    void terminateAccount(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after closing of a Customer Account.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=CustomerAccount.
     * @throws BusinessException business exception.
     */
    void closeAccount(Map<String, Object> methodContext) throws BusinessException;
}