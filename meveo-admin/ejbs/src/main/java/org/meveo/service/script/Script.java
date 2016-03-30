package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.admin.User;

public abstract class Script implements ScriptInterface {

    public static String RESULT_GUI_OUTCOME = "GUI_OUTCOME";
    public static String RESULT_GUI_MESSAGE_KEY = "GUI_MESSAGE_KEY";
    public static String RESULT_GUI_MESSAGE = "GUI_MESSAGE";
    public static String RESULT_VALUE = "RESULT_VALUE";
    public static String CONTEXT_ENTITY = "CONTEXT_ENTITY";

    @Override
    public void init(Map<String, Object> methodContext, User user) throws BusinessException {

    }

    @Override
    public void execute(Map<String, Object> methodContext, User user) throws BusinessException {

    }

    @Override
    public void finalize(Map<String, Object> methodContext, User user) throws BusinessException {

    }

    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }
}