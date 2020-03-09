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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Mediation related API REST interface
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@Path("/billing/mediation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
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
    ActionStatus registerCdrList(CdrListDto postData);

    /**
     * Same as registerCdrList, but at the same process rate the EDR created
     * 
     * @param cdr String of CDR
     * @param isVirtual Boolean for the virtual option
     * @param rateTriggeredEdr Boolean for rate Triggered Edr
     * @param returnWalletOperations return Wallet Operations option
     * @param maxDepth Interger of the max Depth
     * @return Request processing status
     */
    @POST
    @Path("/chargeCdr")
    ChargeCDRResponseDto chargeCdr(String cdr, @QueryParam("isVirtual") boolean isVirtual, @QueryParam("rateTriggeredEdr") boolean rateTriggeredEdr, @QueryParam("returnWalletOperations") boolean returnWalletOperations, @QueryParam("maxDepth") Integer maxDepth);

    /**
     * Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity
     * (PREPAID_RESRV_DELAY_MS)
     * 
     * @param cdr String of CDR
     * @return Available quantity and reservationID is returned
     */
    @POST
    @Path("/reserveCdr")
    CdrReservationResponseDto reserveCdr(String cdr);

    /**
     * Confirms the reservation
     * 
     * @param reservation Prepaid reservation's data
     * @return Request processing status
     */
    @POST
    @Path("/confirmReservation")
    ActionStatus confirmReservation(PrepaidReservationDto reservation);

    /**
     * Cancels the reservation
     * 
     * @param reservation Prepaid reservation's data
     * @return Request processing status
     */
    @POST
    @Path("/cancelReservation")
    ActionStatus cancelReservation(PrepaidReservationDto reservation);

    /**
     * Notify of rejected CDRs
     * 
     * @param cdrList A list of rejected CDR lines (can be as json format string instead of csv line)
     * @return Request processing status
     */
    @POST
    @Path("/notifyOfRejectedCdrs")
    ActionStatus notifyOfRejectedCdrs(CdrListDto cdrList);
}