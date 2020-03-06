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

package org.meveo.service.script.wf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.service.script.RunTimeLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WorkflowTypeClass
public class WFTypeScript<E extends BusinessEntity> extends WorkflowType<E> implements Serializable, WFTypeScriptInterface {

    private static final long serialVersionUID = -9103159611108102999L;

    /**
     * A logger
     */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * A logger to replace with when running script in test mode (from GUI), so logs can be returned/visible to the end user
     */
    protected RunTimeLogger logTest = new RunTimeLogger(this.getClass());

    public WFTypeScript() {
        super();
    }

    public WFTypeScript(E e) {
        super(e);
    }

    @Override
    public void init(Map<String, Object> methodContext) throws BusinessException {
 
    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void terminate(Map<String, Object> methodContext) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }

    @Override
    public List<String> getStatusList() {
        return null;
    }

    @Override
    public void changeStatus(String newStatus) throws BusinessException {

    }

    @Override
    public String getActualStatus() {
        return null;
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