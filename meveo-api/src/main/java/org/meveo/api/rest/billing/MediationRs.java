package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/billing/mediation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface MediationRs extends IBaseRs {

	@POST
	@Path("/registerCdrList")
	ActionStatus registerCdrList(CdrListDto postData);

	@POST
	@Path("/chargeCdr")
	ActionStatus chargeCdr(String cdr);

	@POST
	@Path("/reserveCdr")
	CdrReservationResponseDto reserveCdr(String cdr);

	@POST
	@Path("/confirmReservation")
	ActionStatus confirmReservation(PrepaidReservationDto reservationDto);

	@POST
	@Path("/cancelReservation")
	ActionStatus cancelReservation(PrepaidReservationDto reservationDto);
}
