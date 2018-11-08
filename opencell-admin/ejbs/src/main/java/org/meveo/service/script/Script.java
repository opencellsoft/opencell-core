package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main script interface implementation
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
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
     * Current user
     */
    public static String CONTEXT_CURRENT_USER = "CONTEXT_CURRENT_USER";

    /**
     * Current provider/tenant
     */
    public static String CONTEXT_APP_PROVIDER = "CONTEXT_APP_PROVIDER";

    
    /**
     * Entity custom action's code
     */
    public static String CONTEXT_ACTION = "CONTEXT_ACTION";

    /**
     * Nb of ok when script is executed by a Job
     */
    public static String JOB_RESULT_NB_OK = "RESULT_NB_OK";

    /**
     * Nb of ko when script is executed by a Job
     */
    public static String JOB_RESULT_NB_KO = "RESULT_NB_KO";

    /**
     * Nb of warn when script is executed by a Job
     */
    public static String JOB_RESULT_NB_WARN = "RESULT_NB_WARN";

    /**
     * Report when script is executed by a Job
     */
    public static String JOB_RESULT_REPORT = "RESULT_REPORT";

    /**
     * Nb of result to process when script is executed by a Job
     */
    public static String JOB_RESULT_TO_PROCESS = "RESULT_TO_PROCESS";

    /**
     * A logger
     */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void finalize(Map<String, Object> methodContext) throws BusinessException {

    }

    /**
     * Get Service class by its name
     * 
     * @param serviceInterfaceName A simple name of a service class (NOT a full classname). E.g. WorkflowService
     * @return Service instance
     */
    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }
}