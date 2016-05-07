package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/revenueRecognitionRule")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface RevenueRecognitionRulesRs extends IBaseRs {
	
	    @POST
	    @Path("/")
	    ActionStatus create(RevenueRecognitionRuleDto postData);

	    @PUT
	    @Path("/")
	    ActionStatus update(RevenueRecognitionRuleDto postData);

	    @GET
	    @Path("/")
	    RevenueRecognitionRuleDtoResponse find(@QueryParam("revenueRecognitionRuleCode") String revenueRecognitionRuleCode);

	    @POST
	    @Path("/createOrUpdate")
	    ActionStatus createOrUpdate(RevenueRecognitionRuleDto postData);
	    		
	    @DELETE
	    @Path("/{revenueRecognitionRuleCode}")
	    ActionStatus remove(@PathParam("revenueRecognitionRuleCode") String revenueRecognitionRuleCode);

	    @POST
	    @Path("/list")
	    RevenueRecognitionRuleDtosResponse list();

}
