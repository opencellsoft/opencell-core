package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;

/**
 * Mediation related API WS interface
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@WebService
public interface MediationWs extends IBaseWs {

    /**
     * Accepts a list of CDR line. This CDR is parsed and created as EDR. CDR is same format use in mediation job
     * 
     * @param postData String of CDR
     * @return Request processing status
     */
    @WebMethod
    ActionStatus registerCdrList(@WebParam(name = "cdrList") CdrListDto postData);

    /**
     * Same as registerCdrList, but at the same process rate the EDR created
     * 
     * @param cdr String of CDR
     * @return Request processing status
     */
    @WebMethod
    ActionStatus chargeCdr(@WebParam(name = "cdr") String cdr);

    /**
     * Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity
     * (PREPAID_RESRV_DELAY_MS)
     * 
     * @param cdr String of CDR
     * @return Available quantity and reservationID is returned
     */
    @WebMethod
    CdrReservationResponseDto reserveCdr(@WebParam(name = "cdr") String cdr);

    /**
     * Confirms the reservation
     * 
     * @param reservation Prepaid reservation's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus confirmReservation(@WebParam(name = "reservation") PrepaidReservationDto reservation);

    /**
     * Cancels the reservation
     * 
     * @param reservation Prepaid reservation's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus cancelReservation(@WebParam(name = "reservation") PrepaidReservationDto reservation);

    /**
     * Notify of rejected CDRs
     * 
     * @param cdrList A list of rejected CDR lines (can be as json format string instead of csv line)
     * @return Request processing status
     */
    @WebMethod
    ActionStatus notifyOfRejectedCdrs(@WebParam(name = "cdrList") CdrListDto cdrList);
}