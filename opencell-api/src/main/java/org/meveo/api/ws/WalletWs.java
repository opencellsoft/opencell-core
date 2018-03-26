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
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;
import org.meveo.api.dto.response.billing.WalletBalanceResponseDto;

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

    /**
     * Gets the current (open or reserved) wallet balance amount at a given level and date period. In wallet operation, status='OPEN OR RESERVED'.
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Request processing status and balance amounts
     */
    @WebMethod
    WalletBalanceResponseDto currentBalance(@WebParam(name = "walletBalance") WalletBalanceDto calculateParameters);

    /**
     * Gets the reserved wallet balance amount at a given level and date period. In wallet operation, status='RESERVED'.
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Request processing status and balance amounts
     */
    @WebMethod
    WalletBalanceResponseDto reservedBalance(@WebParam(name = "walletBalance") WalletBalanceDto calculateParameters);

    /**
     * Gets the open wallet balance amount at a given level and date period. In wallet operation, status='OPEN'.
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Request processing status and balance amounts status
     */
    @WebMethod
    WalletBalanceResponseDto openBalance(@WebParam(name = "walletBalance") WalletBalanceDto calculateParameters);

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

    /**
     * List wallet operations matching a given criteria
     * 
     * @param postData Search criteria. Deprecated in v.4.7.2. Use pagingAndFiltering instead
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return
     */
    @WebMethod
    FindWalletOperationsResponseDto findOperations(@Deprecated @WebParam(name = "findWalletOperations") FindWalletOperationsDto postData,
            @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);
}