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

    @WebMethod
    public ActionStatus execute(@WebParam(name = "baseEntityName") String baseEntityName, @WebParam(name = "entityInstanceCode") String entityInstanceCode,
            @WebParam(name = "workflowCode") String workflowCode);

    @WebMethod
    public WorkflowsResponseDto findByEntity(@WebParam(name = "baseEntityName") String baseEntityName);

    @WebMethod
    public WorkflowHistoryResponseDto findHistory(@WebParam(name = "entityInstanceCode") String entityInstanceCode, @WebParam(name = "workflowCode") String workflowCode,
            @WebParam(name = "fromStatus") String fromStatus, @WebParam(name = "toStatus") String toStatus);
}
