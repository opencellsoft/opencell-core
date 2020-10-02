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

package org.meveo.api.ws.wf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.wf.WorkflowHistoryResponseDto;
import org.meveo.api.dto.wf.WorkflowResponseDto;
import org.meveo.api.dto.wf.WorkflowsResponseDto;
import org.meveo.api.ws.IBaseWs;

@WebService
@Deprecated
public interface WorkflowWs extends IBaseWs {

    @WebMethod
    public ActionStatus create(@WebParam(name = "workflow") WorkflowDto postData);

    @WebMethod
    public ActionStatus update(@WebParam(name = "workflow") WorkflowDto postData);

    @WebMethod
    public ActionStatus createOrUpdate(@WebParam(name = "workflow") WorkflowDto postData);

    @WebMethod
    public WorkflowResponseDto find(@WebParam(name = "workflowCode") String workflowCode);

    @WebMethod
    public ActionStatus remove(@WebParam(name = "workflowCode") String workflowCode);

    @WebMethod
    public WorkflowsResponseDto list();

    /**
     * Enable a Workflow by its code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @WebMethod
    public ActionStatus enable(@WebParam(name = "code") String code);

    /**
     * Disable a Workflow by its code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disable(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus execute(@WebParam(name = "baseEntityName") String baseEntityName, @WebParam(name = "entityInstanceCode") String entityInstanceCode,
            @WebParam(name = "workflowCode") String workflowCode);

    @WebMethod
    public WorkflowsResponseDto findByEntity(@WebParam(name = "baseEntityName") String baseEntityName);

    @WebMethod
    public WorkflowHistoryResponseDto findHistory(@WebParam(name = "entityInstanceCode") String entityInstanceCode, @WebParam(name = "workflowCode") String workflowCode,
            @WebParam(name = "fromStatus") String fromStatus, @WebParam(name = "toStatus") String toStatus);
}
