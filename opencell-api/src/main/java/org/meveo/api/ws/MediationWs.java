package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;

@WebService
public interface MediationWs extends IBaseWs {

	@WebMethod
	ActionStatus registerCdrList(@WebParam(name = "cdrList") CdrListDto postData);

	@WebMethod
	ActionStatus chargeCdr(@WebParam(name = "cdr") String cdr);

	@WebMethod
	CdrReservationResponseDto reserveCdr(@WebParam(name = "cdr") String cdr);

	@WebMethod
	ActionStatus confirmReservation(@WebParam(name = "reservation") PrepaidReservationDto reservation);

	@WebMethod
	ActionStatus cancelReservation(@WebParam(name = "reservation") PrepaidReservationDto reservation);
}
