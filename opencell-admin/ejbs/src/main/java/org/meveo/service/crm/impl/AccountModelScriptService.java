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

package org.meveo.service.crm.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.Seller;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.account.AccountScript;
import org.meveo.service.script.account.AccountScriptInterface;

@Stateless
public class AccountModelScriptService implements Serializable {

    private static final long serialVersionUID = -5209560989584270634L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    // Interface methods
    public void createAccount(String scriptCode, Seller seller, AccountEntity account, CRMAccountHierarchyDto postData) throws BusinessException {
        AccountScriptInterface scriptInterface = (AccountScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, account);
        scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
        scriptContext.put(AccountScript.CONTEXT_ACCOUNT_HIERARCHY_DTO,postData);
        scriptInterface.createAccount(scriptContext);
    }

    public void updateAccount(String scriptCode, Seller seller, AccountEntity account,CRMAccountHierarchyDto postData) throws BusinessException {
        AccountScriptInterface scriptInterface = (AccountScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, account);
        scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
        scriptContext.put(AccountScript.CONTEXT_ACCOUNT_HIERARCHY_DTO,postData);
        scriptInterface.updateAccount(scriptContext);
    }

    public void terminateAccount(String scriptCode, Seller seller, AccountEntity account,CRMAccountHierarchyDto postData) throws BusinessException {
        AccountScriptInterface scriptInterface = (AccountScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, account);
        scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
        scriptContext.put(AccountScript.CONTEXT_ACCOUNT_HIERARCHY_DTO,postData);
        scriptInterface.terminateAccount(scriptContext);
    }

    public void closeAccount(String scriptCode, Seller seller, AccountEntity account,CRMAccountHierarchyDto postData) throws BusinessException {
        AccountScriptInterface scriptInterface = (AccountScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, account);
        scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
        scriptContext.put(AccountScript.CONTEXT_ACCOUNT_HIERARCHY_DTO,postData);
        scriptInterface.closeAccount(scriptContext);
    }

}
