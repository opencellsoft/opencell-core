package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.ActionPlanItemDto;
import org.meveo.api.dto.response.payment.ActionPlanItemResponseDto;
import org.meveo.model.payments.DunningLevelEnum;

@WebService
public interface ActionPlanItemWs extends IBaseWs {
 
	 @WebMethod
	    ActionStatus create(@WebParam(name = "actionPlanItem") ActionPlanItemDto postData);
	    
	    @WebMethod
	    ActionStatus update(@WebParam(name = "actionPlanItem") ActionPlanItemDto postData);
	    
	    @WebMethod
	    ActionStatus createOrUpdate(@WebParam(name = "actionPlanItem") ActionPlanItemDto postData);
	    
	    @WebMethod
	    ActionPlanItemResponseDto find(@WebParam(name = "dunningPlanCode") String dunningPlanCode ,
	    		@WebParam(name = "itemOrder") Integer itemOrder, 
	    		@WebParam(name = "dunningLevel") DunningLevelEnum dunningLevel);
	    
	    @WebMethod
	    ActionStatus remove(@WebParam(name = "dunningPlanCode") String dunningPlanCode ,
	    		@WebParam(name = "itemOrder") Integer itemOrder, 
	    		@WebParam(name = "dunningLevel") DunningLevelEnum dunningLevel);
	    
}
