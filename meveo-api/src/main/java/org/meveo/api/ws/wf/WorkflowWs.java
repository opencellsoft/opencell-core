package org.meveo.api.ws.wf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.wf.WorkflowResponseDto;
import org.meveo.api.dto.wf.WorkflowsResponseDto;
import org.meveo.api.ws.IBaseWs;

@WebService
public interface WorkflowWs extends IBaseWs {
 
	    @WebMethod
	    ActionStatus create(@WebParam(name = "workflow") WorkflowDto postData);

	    @WebMethod
	    ActionStatus update(@WebParam(name = "workflow") WorkflowDto postData);

	    @WebMethod
	    ActionStatus createOrUpdate(@WebParam(name = "workflow") WorkflowDto postData);

	    @WebMethod
	    WorkflowResponseDto find(@WebParam(name = "workflowCode") String workflowCode);

	    @WebMethod
	    ActionStatus remove(@WebParam(name = "workflowCode") String workflowCode);
	    
	    @WebMethod
	    WorkflowsResponseDto list();

}
