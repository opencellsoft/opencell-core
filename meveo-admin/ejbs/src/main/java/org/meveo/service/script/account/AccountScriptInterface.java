package org.meveo.service.script.account;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.service.script.ScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
public interface AccountScriptInterface extends ScriptInterface {

    void createAccount(Map<String, Object> methodContext, User user) throws BusinessException;

    void updateAccount(Map<String, Object> methodContext, User user) throws BusinessException;

    void terminateAccount(Map<String, Object> methodContext, User user) throws BusinessException;

    void closeAccount(Map<String, Object> methodContext, User user) throws BusinessException;
}