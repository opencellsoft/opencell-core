package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;

public interface ScriptInterface {

    public void init(Map<String, Object> methodContext, Provider provider, User user) throws BusinessException;

    public void execute(Map<String, Object> methodContext, Provider provider, User user) throws BusinessException;

    public void finalize(Map<String, Object> methodContext, Provider provider, User user) throws BusinessException;
}