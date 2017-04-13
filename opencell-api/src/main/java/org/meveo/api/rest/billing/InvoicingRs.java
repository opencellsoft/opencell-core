package org.meveo.api.rest.billing;

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

@Path("/billing/invoicing")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoicingRs extends IBaseRs {

    /**
     * Create a new billing run
     * 
     * @param createBillingRunDto The billing run's data
     * @return Request processing status
     */
    @POST
    @Path("/createBillingRun")
    ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto);


    /**
     * Search for a billing run info with a given Id 
     * 
     * @param billingRunId The billing run's Id
     * @return The billing run info
     */
    @POST
    @Path("/getBillingRunInfo")
    GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId);

    /**
     * Returns the list of billable billing accounts of a billing run
     * 
     * @param billingRunId The billing run id
     * @return A list of billing accounts
     */
    @POST
    @Path("/getBillingAccountListInRun")
    GetBillingAccountListInRunResponseDto getBillingAccountListInRun(Long billingRunId);

    /**
     * Returns the pre-invoicing report for a given billing run Id
     * 
     * @param billingRunId The billing run id
     * @return A pre-invoicing reports
     */
    @POST
    @Path("/getPreInvoicingReport")
    GetPreInvoicingReportsResponseDto getPreInvoicingReport(Long billingRunId);

    /**
     * Returns the post-invoicing report for a given billing run Id
     * 
     * @param billingRunId The billing run id
     * @return A post-invoicing reports
     */
    @POST
    @Path("/getPostInvoicingReport")
    GetPostInvoicingReportsResponseDto getPostInvoicingReport(Long billingRunId);

    /**
     * Depending on the status of the billing run, produce the pre-invoicing report, the post-Invoicing report or validates a billing run. Sets the next invoice date of a billing
     * account to the next calendar date.
     * 
     * @param billingRunId The billing run id
     * @return Request processing status 
     */
    @POST
    @Path("/validateBillingRun")
    ActionStatus validateBillingRun(Long billingRunId);

    /**
     * Cancels a billing run. Sets RatedTransaction.status associated to billing run to OPEN. Remove aggregates and invoice associated to the billing run. Set
     * billingAccount.billingRun to null.
     * 
     * @param billingRunId Billing run id
     * @return Request processing status 
     */
    @POST
    @Path("/cancelBillingRun")
    ActionStatus cancelBillingRun(Long billingRunId);

}
