package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;

public interface ScriptInterface {

    public void init(Map<String, Object> methodContext, User user) throws BusinessException;

    public void execute(Map<String, Object> methodContext, User user) throws BusinessException;

    public void finalize(Map<String, Object> methodContext, User user) throws BusinessException;
}