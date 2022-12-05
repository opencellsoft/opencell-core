/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.billing;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CdrDto;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.billing.ProcessCDRResponseDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Mediation related API REST interface
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@Path("/billing/mediation")
@Tag(name = "Mediation", description = "@%Mediation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MediationRs extends IBaseRs {

    /**
     * Accepts a list of CDR line. This CDR is parsed and created as EDR. CDR is same format use in mediation job
     * 
     * @param postData String of CDR
     * @return Request processing status
     */
    @POST
    @Path("/registerCdrList")
    @Operation(summary = " Accepts a list of CDR line. This CDR is parsed and created as EDR. CDR is same format use in mediation job  ", description = " Accepts a list of CDR line. This CDR is parsed and created as EDR. CDR is same format use in mediation job  ", operationId = "    POST_Mediation_registerCdrList", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    ActionStatus registerCdrList(CdrListDto postData);

    /**
     * Parse CDR, convert to EDR and rate it
     * 
     * @param cdr String of CDR
     * @param isVirtual Boolean for the virtual option
     * @param rateTriggeredEdr Boolean for rate Triggered Edr
     * @param maxDepth Interger of the max Depth
     * @param returnEDRs Return EDR ids
     * @param returnWalletOperations return Wallet Operation IDs
     * @param returnWalletOperationDetails return Wallet Operation details
     * @param returnCounters Return counters that were updated information
     * @param generateRTs generate automatically RTs.
     * @return Request processing status
     */
    @POST
    @Path("/chargeCdr")
    @Operation(summary = " Same as registerCdrList, but at the same process rate the EDR created  ", description = " Same as registerCdrList, but at the same process rate the EDR created  ", operationId = "    POST_Mediation_chargeCdr", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ChargeCDRResponseDto.class))) })
    ChargeCDRResponseDto chargeCdr(String cdr, @QueryParam("isVirtual") boolean isVirtual, @QueryParam("rateTriggeredEdr") boolean rateTriggeredEdr, @QueryParam("maxDepth") Integer maxDepth,
            @QueryParam("returnEDRs") boolean returnEDRs, @QueryParam("returnWalletOperations") boolean returnWalletOperations, @QueryParam("returnWalletOperationDetails") boolean returnWalletOperationDetails,
            @QueryParam("returnCounters") boolean returnCounters, @QueryParam("generateRTs") boolean generateRTs);

    /**
     * Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity (PREPAID_RESRV_DELAY_MS)
     * 
     * @param cdr String of CDR
     * @return Available quantity and reservationID is returned
     */
    @POST
    @Path("/reserveCdr")
    @Operation(summary = " Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation", description = " Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity (PREPAID_RESRV_DELAY_MS)  ", operationId = "    POST_Mediation_reserveCdr", responses = {
            @ApiResponse(description = " Available quantity and reservationID is ed ", content = @Content(schema = @Schema(implementation = CdrReservationResponseDto.class))) })
    CdrReservationResponseDto reserveCdr(String cdr);

    /**
     * Confirms the reservation
     * 
     * @param reservation Prepaid reservation's data
     * @return Request processing status
     */
    @POST
    @Path("/confirmReservation")
    @Operation(summary = " Confirms the reservation  ", description = " Confirms the reservation  ", operationId = "    POST_Mediation_confirmReservation", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    ActionStatus confirmReservation(PrepaidReservationDto reservation);

    /**
     * Cancels the reservation
     * 
     * @param reservation Prepaid reservation's data
     * @return Request processing status
     */
    @POST
    @Path("/cancelReservation")
    @Operation(summary = " Cancels the reservation  ", description = " Cancels the reservation  ", operationId = "    POST_Mediation_cancelReservation", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    ActionStatus cancelReservation(PrepaidReservationDto reservation);

    /**
     * Notify of rejected CDRs
     * 
     * @param cdrList A list of rejected CDR lines (can be as json format string instead of csv line)
     * @return Request processing status
     */
    @POST
    @Path("/notifyOfRejectedCdrs")
    @Operation(summary = " Notify of rejected CDRs  ", description = " Notify of rejected CDRs  ", operationId = "    POST_Mediation_notifyOfRejectedCdrs", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    ActionStatus notifyOfRejectedCdrs(CdrListDto cdrList);

    /**
     * Convert CDRs to EDRs
     * 
     * @param cdrIds A list of CDR ids to be processed
     * @return Request processing status
     */
    @POST
    @Path("/processCdrList")
    @Operation(summary = " Convert CDRs to EDRs ", description = " Convert CDRs to EDRs ", operationId = "    POST_Mediation_processCdrList", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ActionStatus.class))) })
    ProcessCDRResponseDto processCdrList(List<Long> cdrIds);
    
    @POST
    @Path("/createCDR")
    ActionStatus createCDR(CdrDto cdrDto);
}
