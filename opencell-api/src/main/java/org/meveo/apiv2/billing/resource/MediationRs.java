package org.meveo.apiv2.billing.resource;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.billing.ChargeCDRListResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Mediation related API REST interface
 *
 * @lastModifiedVersion willBeSetLater
 *
 * @author Andrius Karpavicius
 */
@Path("/mediation/cdrs")
@Tag(name = "Mediation", description = "@%Mediation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MediationRs extends IBaseRs {

    /**
     * Same as registerCdrList, but at the same process rate the EDR created
     *
     * @param cdrs list of String of CDR
     * @param isVirtual Boolean for the virtual option
     * @param rateTriggeredEdr Boolean for rate Triggered Edr
     * @param returnWalletOperations return Wallet Operations option
     * @param maxDepth Integer of the max Depth
     * @return Request processing status
     */
    @POST
    @Path("/chargeCdrList")
    @Operation(summary = "Accepts a list of CDR lines, parses them, creates EDRs and rates them. . CDR is same format use in mediation job", description = "Accepts a list of CDR lines, parses them, creates EDRs and rates them. . CDR is same format use in mediation job", operationId = "    POST_Mediation_chargeCdrList", responses = {
            @ApiResponse(description = " Request processing status ", content = @Content(schema = @Schema(implementation = ChargeCDRListResponseDto.class))) })
    ChargeCDRListResponseDto chargeCdrList(List<String> cdrs, @QueryParam("isVirtual") boolean isVirtual, @QueryParam("rateTriggeredEdr") boolean rateTriggeredEdr,
                                           @QueryParam("returnWalletOperations") boolean returnWalletOperations, @QueryParam("maxDepth") Integer maxDepth);

}

