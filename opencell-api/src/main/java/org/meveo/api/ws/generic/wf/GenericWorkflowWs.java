package org.meveo.api.ws.generic.wf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowResponseDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowsResponseDto;
import org.meveo.api.dto.response.generic.wf.WorkflowInsHistoryResponseDto;
import org.meveo.api.ws.IBaseWs;

@WebService
public interface GenericWorkflowWs extends IBaseWs {

    @WebMethod
    public ActionStatus create(@WebParam(name = "workflow") GenericWorkflowDto postData);

    @WebMethod
    public ActionStatus update(@WebParam(name = "workflow") GenericWorkflowDto postData);

    @WebMethod
    public ActionStatus createOrUpdate(@WebParam(name = "workflow") GenericWorkflowDto postData);

    @WebMethod
    public GenericWorkflowResponseDto find(@WebParam(name = "workflowCode") String workflowCode);

    @WebMethod
    public ActionStatus remove(@WebParam(name = "workflowCode") String workflowCode);

    @WebMethod
    public GenericWorkflowsResponseDto list();

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
    public GenericWorkflowsResponseDto findByEntity(@WebParam(name = "baseEntityName") String baseEntityName);

    @WebMethod
    public WorkflowInsHistoryResponseDto findHistory(@WebParam(name = "entityInstanceCode") String entityInstanceCode, @WebParam(name = "workflowCode") String workflowCode,
            @WebParam(name = "fromStatus") String fromStatus, @WebParam(name = "toStatus") String toStatus);
}
