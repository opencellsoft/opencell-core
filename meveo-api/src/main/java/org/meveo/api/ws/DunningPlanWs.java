package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.elasticsearch.index.engine.Engine.Get;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.DunningPlanDto;

@WebService
public interface DunningPlanWs extends IBaseWs {
 

	    @WebMethod
	    ActionStatus create(@WebParam(name = "dunningPlan") DunningPlanDto postData);

	    @WebMethod
	    ActionStatus update(@WebParam(name = "dunningPlan") DunningPlanDto postData);

	    @WebMethod
	    ActionStatus createOrUpdate(@WebParam(name = "dunningPlan") DunningPlanDto postData);

	    @WebMethod
	    Get find(@WebParam(name = "dunningPlanCode") String dunningPlanCode);

	    @WebMethod
	    ActionStatus remove(@WebParam(name = "dunningPlanCode") String dunningPlanCode);
}
