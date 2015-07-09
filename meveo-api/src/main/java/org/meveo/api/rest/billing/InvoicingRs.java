package org.meveo.api.rest.billing;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/billing/invoicing")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface InvoicingRs extends IBaseRs {

	@POST
	@Path("/createBillingRun")
	ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto);
	
	@POST
	@Path("/getBillingRunInfo")
	GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId);
	
	@POST
	@Path("/getBillingAccountListInRun")
	GetBillingAccountListInRunResponseDto getBillingAccountListInRun( Long billingRunId);
	

}
