package org.meveo.api.rest.billing;

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
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
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
	
	/**
	 * Returns the list of billable billing accounts of a billing run
	 * @param billingRunId Billing run id
	 * @return
	 */
	@POST
	@Path("/getBillingAccountListInRun")
	GetBillingAccountListInRunResponseDto getBillingAccountListInRun( Long billingRunId);
	
	@POST
	@Path("/getPreInvoicingReport")
    GetPreInvoicingReportsResponseDto getPreInvoicingReport(@WebParam(name = "billingRunId") Long billingRunId);
	
	@POST
	@Path("/getPostInvoicingReport")
	GetPostInvoicingReportsResponseDto getPostInvoicingReport(@WebParam(name = "billingRunId") Long billingRunId);
	
	/**
	 * Depending on the status of the billing run, produce the preinvoicing report, the postInvoicing report or validates a billing run. Sets the next invoice date of a billing account to the next calendar date.
	 * @param billingRunId Billing run id
	 * @param nbRuns Number of billing accounts in batch processed by each thread when computing the post invoicing report (by default 100)
	 * @param waitingMillis Nb of millisecond between thread poll (default 1000)
	 * @return
	 */
	@POST
	@Path("/validateBillingRun")
	ActionStatus validateBillingRun(@WebParam(name = "billingRunId") Long billingRunId,@WebParam(name = "nbRuns") Long nbRuns,@WebParam(name = "waitingMillis") Long waitingMillis);
	
	/**
	 * Cancels a billing run. Sets RatedTransaction.status associated to billingRun to OPEN. Remove aggregates and invoice associated to the billingRun. Set billingAccount.billingRun to null.
	 * @param billingRunId Billing run id
	 * @return
	 */
	@POST
	@Path("/cancelBillingRun")
	ActionStatus cancelBillingRun(@WebParam(name = "billingRunId") Long billingRunId);
	

}
