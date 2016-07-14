package org.meveo.api.ws.wf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.wf.WFTransitionResponseDto;
import org.meveo.api.ws.IBaseWs;

@WebService
public interface WFTransitionWs extends IBaseWs {
 
	    @WebMethod
	    ActionStatus create(@WebParam(name = "wfTransitionDto") WFTransitionDto postData);
	    
	    @WebMethod
	    ActionStatus update(@WebParam(name = "wfTransitionDto") WFTransitionDto postData);
	    
	    @WebMethod
	    ActionStatus createOrUpdate(@WebParam(name = "wfTransitionDto") WFTransitionDto postData);
	    
	    @WebMethod
	    WFTransitionResponseDto find(@WebParam(name = "dunningPlanCode") String dunningPlanCode ,
	    		@WebParam(name = "dunningLevelFrom") String dunningLevelFrom, 
	    		@WebParam(name = "dunningLevelTo") String dunningLevelTo);
	    
	    @WebMethod
	    ActionStatus remove(@WebParam(name = "dunningPlanCode") String dunningPlanCode ,
	    		@WebParam(name = "dunningLevelFrom") String dunningLevelFrom, 
	    		@WebParam(name = "dunningLevelTo") String dunningLevelTo);
	    
}
