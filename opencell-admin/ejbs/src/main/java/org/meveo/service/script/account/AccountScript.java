package org.meveo.service.script.account;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.module.ModuleScript;

public class AccountScript extends ModuleScript implements AccountScriptInterface {

    /**
     * seller constant.
     */
    public static String CONTEXT_SELLER = "CONTEXT_SELLER";

    /**
     * account hierachy constant.
     */
    public static String CONTEXT_ACCOUNT_HIERARCHY_DTO = "CONTEXT_ACCOUNT_HIERARCHY_DTO";

    @Override
    public void createAccount(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void updateAccount(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void terminateAccount(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void closeAccount(Map<String, Object> methodContext) throws BusinessException {

    }
}