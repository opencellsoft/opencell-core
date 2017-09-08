package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.Paging.SortOrder;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billing/wallet")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface WalletRs extends IBaseRs {

    /**
     * Create a new wallet template
     * 
     * @param postData The wallet template's data
     * @return Request processing status
     */
    @POST
    @Path("/template")
    ActionStatus createWalletTemplate(WalletTemplateDto postData);

    /**
     * Update an existing wallet template
     * 
     * @param postData The wallet template's data
     * @return Request processing status
     */
    @PUT
    @Path("/template")
    ActionStatus updateWalletTemplate(WalletTemplateDto postData);

    /**
     * Remove an existing wallet template with a given code
     * 
     * @param walletTemplateCode The wallet template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/template/{walletTemplateCode}")
    ActionStatus removeWalletTemplate(@PathParam("walletTemplateCode") String walletTemplateCode);

    /**
     * Search for a wallet template with a given code
     * 
     * @param walletTemplateCode The wallet template's code
     * @return A wallet template
     */
    @GET
    @Path("/template")
    GetWalletTemplateResponseDto findWalletTemplate(@QueryParam("walletTemplateCode") String walletTemplateCode);

    /**
     * Gets the current balance amount given provider, seller, user account and date. In wallet operation, status='OPEN OR RESERVED'.
     * 
     * @param postData WalletBalanceDto
     * @return Request processing status
     */
    @POST
    @Path("/balance/current")
    ActionStatus currentBalance(WalletBalanceDto postData);

    /**
     * Gets the reserved balance amount given provider, seller, user account and date. In wallet operation, status='RESERVED'.
     * 
     * @param postData WalletBalanceDto
     * @return Request processing status
     */
    @POST
    @Path("/balance/reserved")
    ActionStatus reservedBalance(WalletBalanceDto postData);

    /**
     * Gets the open balance amount given provider, seller, user account and date. In wallet operation, status='OPEN'.
     * 
     * @param postData WalletBalanceDto
     * @return Request processing status
     */
    @POST
    @Path("/balance/open")
    ActionStatus openBalance(WalletBalanceDto postData);

    /**
     * Create reservation for a given offer, user account, seller, provider and date.
     * 
     * @param postData The reservation's data
     * @return Request processing status
     */
    @POST
    @Path("/reservation")
    ActionStatus createReservation(WalletReservationDto postData);

    /**
     * Updates a reservation. Same as create we just need to pass the id of the reservation.
     * 
     * @param postData The reservation's data
     * @return Request processing status
     */
    @PUT
    @Path("/reservation")
    ActionStatus updateReservation(WalletReservationDto postData);

    /**
     * Cancel a reservation given an id.
     * 
     * @param reservationId The reservation's id
     * @return Request processing status
     */
    @DELETE
    @Path("/reservation/{reservationId:[0-9]+}")
    ActionStatus cancelReservation(@PathParam("reservationId") Long reservationId);

    /**
     * Confirm a reservation given an id.
     * 
     * @param postData The reservation's id
     * @return Request processing status
     */
    @POST
    @Path("/reservation/confirm")
    ActionStatus confirmReservation(WalletReservationDto postData);

    /**
     * Create a new operation
     * 
     * @param postData The operation's data
     * @return Request processing status
     */
    @POST
    @Path("/operation")
    ActionStatus createOperation(WalletOperationDto postData);

    /**
     * Search for an operation with a given (example) code
     * 
     * @param postData The operation's data (FindWalletOperationsDto)
     * @param from Pagination - from record number
     * @param numberOfRows Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return
     */
    @POST
    @Path("/operation/find")
    FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, @QueryParam("from") Integer from, @QueryParam("numberOfRows") Integer numberOfRows,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Create new or update an existing wallet template
     * 
     * @param postData The wallet template's data
     * @return Request processing status
     */
    @POST
    @Path("/template/createOrUpdate")
    ActionStatus createOrUpdateWalletTemplate(WalletTemplateDto postData);
}