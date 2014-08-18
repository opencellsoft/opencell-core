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
import org.meveo.api.WalletBalanceApi;
import org.meveo.api.dto.WalletBalanceDTO;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.security.WSSecured;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Path("/walletBalance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@WSSecured
public class WalletBalanceWS extends BaseWS {

	@Inject
	private WalletBalanceApi walletBalanceApi;

	@POST
	@Path("/current")
	public ActionStatus currentBalance(WalletBalanceDTO walletBalance)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		try {
			result.setMessage(""
					+ walletBalanceApi.getCurrentAmount(walletBalance));
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@POST
	@Path("/reserved")
	public ActionStatus reservedBalance(WalletBalanceDTO walletBalance)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		try {
			result.setMessage(""
					+ walletBalanceApi.getReservedAmount(walletBalance));
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@POST
	@Path("/open")
	public ActionStatus openBalance(WalletBalanceDTO walletBalance)
			throws MeveoApiException, BusinessException {
		ActionStatus result = new ActionStatus();

		try {
			result.setMessage(""
					+ walletBalanceApi.getOpenAmount(walletBalance));
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
