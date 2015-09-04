package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface WalletWs extends IBaseWs {
	
	@WebMethod
	ActionStatus createWalletTemplate(@WebParam(name = "walletTemplate") WalletTemplateDto postData);
	
	@WebMethod
	ActionStatus updateWalletTemplate(@WebParam(name = "walletTemplate") WalletTemplateDto postData);
	
	@WebMethod
	ActionStatus createOrUpdateWalletTemplate(@WebParam(name = "walletTemplate") WalletTemplateDto postData);
	
	@WebMethod
	GetWalletTemplateResponseDto findWalletTemplate(@WebParam(name = "walletTemplateCode") String walletTemplateCode);
	
	@WebMethod
	ActionStatus removeWalletTemplate(@WebParam(name = "walletTemplateCode") String walletTemplateCode);

	@WebMethod
	ActionStatus currentBalance(@WebParam(name = "walletBalance") WalletBalanceDto postData);

	@WebMethod
	ActionStatus reservedBalance(@WebParam(name = "walletBalance") WalletBalanceDto postData);

	@WebMethod
	ActionStatus openBalance(@WebParam(name = "walletBalance") WalletBalanceDto postData);

	@WebMethod
	ActionStatus createReservation(@WebParam(name = "walletReservation") WalletReservationDto postData);

	@WebMethod
	ActionStatus updateReservation(@WebParam(name = "walletReservation") WalletReservationDto postData);

	@WebMethod
	ActionStatus cancelReservation(@WebParam(name = "reservationId") Long reservationId);

	@POST
	@Path("/reservation/confirm")
	ActionStatus confirmReservation(@WebParam(name = "walletReservation") WalletReservationDto postData);

	@WebMethod
	ActionStatus createOperation(@WebParam(name = "walletOperation") WalletOperationDto postData);

	@WebMethod
	FindWalletOperationsResponseDto findOperations(@WebParam(name = "findWalletOperations") FindWalletOperationsDto postData);

}
