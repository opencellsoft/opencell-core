package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
	@Path("/create")
	public ActionStatus create(WalletReservationDto walletReservation)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		try {
			walletReservationApi.create(walletReservation);
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}
	
	@POST
	@Path("/update")
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

	@POST
	@Path("/cancel")
	public ActionStatus cancel(Long reservationId) {
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
	public ActionStatus confirm(Long reservationId) {
		ActionStatus result = new ActionStatus();

		return result;
	}

}
