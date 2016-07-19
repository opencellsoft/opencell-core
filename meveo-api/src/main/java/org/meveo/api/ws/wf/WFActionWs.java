package org.meveo.api.ws.wf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.ws.IBaseWs;

@WebService
public interface WFActionWs extends IBaseWs {
 
	 @WebMethod
	    ActionStatus create(@WebParam(name = "wfAction") WFActionDto postData);
	    
	    @WebMethod
	    ActionStatus update(@WebParam(name = "wfAction") WFActionDto postData);
	    
	    @WebMethod
	    ActionStatus createOrUpdate(@WebParam(name = "wfAction") WFActionDto postData);
	    
	   //TODO add methodes available in api
}
