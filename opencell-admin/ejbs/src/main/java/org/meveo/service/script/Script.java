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

package org.meveo.service.script;

import java.io.Serializable;
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
public abstract class Script implements Serializable, ScriptInterface {

    private static final long serialVersionUID = 790175592043841856L;

    /**
     * GUI redirection after entity custom action execution
     */
    public static final String RESULT_GUI_OUTCOME = "GUI_OUTCOME";

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
    public static final String RESULT_VALUE = "RESULT_VALUE";

    /**
     * Entity or event on which script acts on in case of fired notification
     */
    public static final String CONTEXT_ENTITY_OR_EVENT = "entityOrEvent";

    /**
     * Entity, on which script acts on
     */
    public static final String CONTEXT_ENTITY = "CONTEXT_ENTITY";

    /**
     * Parent entity of an entity, on which script acts on
     */
    public static String CONTEXT_PARENT_ENTITY = "CONTEXT_PARENT_ENTITY";

    /**
     * Record, that script process
     */
    public static String CONTEXT_RECORD = "record";

    /**
     * Current user
     */
    public static final String CONTEXT_CURRENT_USER = "CONTEXT_CURRENT_USER";

    /**
     * Current provider/tenant
     */
    public static final String CONTEXT_APP_PROVIDER = "CONTEXT_APP_PROVIDER";

    /**
     * Entity custom action's code
     */
    public static final String CONTEXT_ACTION = "CONTEXT_ACTION";

    /**
     * Nb of ok when script is executed by a Job
     */
    public static final String JOB_RESULT_NB_OK = "RESULT_NB_OK";

    /**
     * Nb of ko when script is executed by a Job
     */
    public static final String JOB_RESULT_NB_KO = "RESULT_NB_KO";

    /**
     * Nb of warn when script is executed by a Job
     */
    public static final String JOB_RESULT_NB_WARN = "RESULT_NB_WARN";

    /**
     * Report when script is executed by a Job
     */
    public static final String JOB_RESULT_REPORT = "RESULT_REPORT";

    /**
     * Nb of result to process when script is executed by a Job
     */
    public static final String JOB_RESULT_TO_PROCESS = "RESULT_TO_PROCESS";

    /**
     * The job execution result.
     */
    public static final String JOB_EXECUTION_RESULT = "JobExecutionResult";
    
    /**
     * The invoice validation status.
     */
	public static final String INVOICE_VALIDATION_STATUS = "InvoiceValidation.STATUS";
	
	/**
     * The invoice validation reject reason.
     */
	public static final String INVOICE_VALIDATION_REASON = "InvoiceValidation.REASON";

    /**
     * A logger
     */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * A logger to replace with when running script in test mode (from GUI), so logs can be returned/visible to the end user
     */
    protected RunTimeLogger logTest = new RunTimeLogger(this.getClass());

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {

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

    /**
     * Get log messages related to script execution (test mode run only)
     * 
     * @return Log messages
     */
    public String getLogMessages() {
        return logTest.getLog();
    }
}