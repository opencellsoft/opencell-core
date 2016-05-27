package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.admin.User;

public abstract class Script implements ScriptInterface {

    /**
     * GUI redirection after entity custom action execution
     */
    public static String RESULT_GUI_OUTCOME = "GUI_OUTCOME";

    /**
     * A key of a message to show after entity custom action execution
     */
    public static String RESULT_GUI_MESSAGE_KEY = "GUI_MESSAGE_KEY";

    /**
     * A message to show after entity custom action execution
     */
    public static String RESULT_GUI_MESSAGE = "GUI_MESSAGE";

    /**
     * Script return value
     */
    public static String RESULT_VALUE = "RESULT_VALUE";

    /**
     * Entity, on which script acts on
     */
    public static String CONTEXT_ENTITY = "CONTEXT_ENTITY";

    /**
     * Parent entity of an entity, on which script acts on
     */
    public static String CONTEXT_PARENT_ENTITY = "CONTEXT_PARENT_ENTITY";

    /**
     * Entity custom action's code
     */
    public static String CONTEXT_ACTION = "CONTEXT_ACTION";

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