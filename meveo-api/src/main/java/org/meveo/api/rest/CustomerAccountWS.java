package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType; 

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.CustomerAccountApi;


@Path("/customerAccount")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CustomerAccountWS {

 
	@Inject
	private CustomerAccountApi customerAccountapi;

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}
	
	
	@GET
	@Path("/")
	public ActionStatus getCustomerAccount(String customerAccountCode,String providerCode) throws Exception  {
	ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
         try{	 
        customerAccountapi.getCustomerAccount(customerAccountCode, providerCode);	 
	} catch (BusinessException e) {
		result.setStatus(ActionStatusEnum.FAIL);
		result.setMessage(e.getMessage());
		e.printStackTrace();
	} catch (Exception e) {
		result.setStatus(ActionStatusEnum.FAIL);
		result.setMessage(e.getMessage());
		e.printStackTrace();
	}
		return result;
	}

}
