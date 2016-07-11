package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.dto.response.payment.DunningPlanResponseDto;
import org.meveo.api.dto.response.payment.DunningPlansResponseDto;

@WebService
public interface DunningPlanWs extends IBaseWs {
 
	    @WebMethod
	    ActionStatus create(@WebParam(name = "dunningPlan") DunningPlanDto postData);

	    @WebMethod
	    ActionStatus update(@WebParam(name = "dunningPlan") DunningPlanDto postData);

	    @WebMethod
	    ActionStatus createOrUpdate(@WebParam(name = "dunningPlan") DunningPlanDto postData);

	    @WebMethod
	    DunningPlanResponseDto find(@WebParam(name = "dunningPlanCode") String dunningPlanCode);

	    @WebMethod
	    ActionStatus remove(@WebParam(name = "dunningPlanCode") String dunningPlanCode);
	    
	    @WebMethod
	    DunningPlansResponseDto list();

}
