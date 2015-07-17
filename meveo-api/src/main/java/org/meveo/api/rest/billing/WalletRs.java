package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billing/wallet")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface WalletRs extends IBaseRs {

	@POST
	@Path("/balance/current")
	ActionStatus currentBalance(WalletBalanceDto postData);

	@POST
	@Path("/balance/reserved")
	ActionStatus reservedBalance(WalletBalanceDto postData);

	@POST
	@Path("/balance/open")
	ActionStatus openBalance(WalletBalanceDto postData);

	@POST
	@Path("/reservation")
	ActionStatus createReservation(WalletReservationDto postData);

	@PUT
	@Path("/reservation")
	ActionStatus updateReservation(WalletReservationDto postData);

	@DELETE
	@Path("/reservation/{reservationId:[0-9]+}")
	ActionStatus cancelReservation(@PathParam("reservationId") Long reservationId);

	@POST
	@Path("/reservation/confirm")
	ActionStatus confirmReservation(WalletReservationDto postData);

	@POST
	@Path("/operation")
	ActionStatus createOperation(WalletOperationDto postData);

	@POST
	@Path("/operation/find")
	FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData);

}