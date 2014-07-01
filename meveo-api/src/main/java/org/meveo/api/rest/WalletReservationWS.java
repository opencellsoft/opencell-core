package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.WalletReservationApi;
import org.meveo.api.dto.WalletReservationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Path("/walletReservation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
public class WalletReservationWS extends BaseWS {

	@Inject
	private WalletReservationApi walletReservationApi;

	@POST
	@Path("/")
	public ActionStatus create(WalletReservationDto walletReservation)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		try {
			result.setMessage(""
					+ walletReservationApi.create(walletReservation));
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(WalletReservationDto walletReservation)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		try {
			walletReservationApi.update(walletReservation);
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@DELETE
	@Path("/{reservationId:[0-9]+}")
	public ActionStatus cancel(@PathParam("reservationId") Long reservationId) {
		ActionStatus result = new ActionStatus();

		try {
			walletReservationApi.cancel(reservationId);
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@POST
	@Path("/confirm")
	public ActionStatus confirm(WalletReservationDto walletReservation) {
		ActionStatus result = new ActionStatus();

		try {
			result.setMessage(""
					+ walletReservationApi.confirm(walletReservation));
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
