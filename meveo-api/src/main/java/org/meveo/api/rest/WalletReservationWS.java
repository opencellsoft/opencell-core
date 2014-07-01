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
import org.meveo.api.dto.WalletReservationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.crm.impl.ProviderService;
import org.slf4j.Logger;

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
	private Logger log;

	@Inject
	private ReservationService reservationService;

	@Inject
	private ProviderService providerService;

	@POST
	@Path("/reserve")
	public ActionStatus reserve(WalletReservationDto walletReservation)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		Provider provider = providerService.findByCode(walletReservation
				.getProviderCode());
		if (provider == null) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
			log.error("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
		} else {
			try {
				reservationService.reserveCredit(provider,
						walletReservation.getSellerCode(),
						walletReservation.getOfferCode(),
						walletReservation.getUserAccountCode(),
						walletReservation.getSubscriptionDate(),
						walletReservation.getExpirationDate(),
						walletReservation.getCreditLimit(),
						walletReservation.getParam1(),
						walletReservation.getParam2(),
						walletReservation.getParam3());
			} catch (BusinessException e) {
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(e.getMessage());
			}
		}

		return result;
	}

	@POST
	@Path("/cancel")
	public ActionStatus cancel(Long reservationId) {
		ActionStatus result = new ActionStatus();

		try {
			reservationService.cancelCredit(reservationId);
		} catch (BusinessException e) {
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
