package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.WalletApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.AmountsDto;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;
import org.meveo.api.dto.response.billing.WalletBalanceResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.WalletWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "WalletWs", endpointInterface = "org.meveo.api.ws.WalletWs")
@Interceptors({ WsRestApiInterceptor.class })
public class WalletWsImpl extends BaseWs implements WalletWs {

    @Inject
    private WalletApi walletApi;

    @Override
    public WalletBalanceResponseDto currentBalance(WalletBalanceDto calculateParameters) {

        WalletBalanceResponseDto result = new WalletBalanceResponseDto();

        try {

            AmountsDto amounts = walletApi.getCurrentAmount(calculateParameters);

            if (calculateParameters.isAmountWithTax() != null) {
                result.getActionStatus().setMessage("" + amounts.getAmount(calculateParameters.isAmountWithTax()));
            }

            result.setAmounts(amounts);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public WalletBalanceResponseDto reservedBalance(WalletBalanceDto calculateParameters) {

        WalletBalanceResponseDto result = new WalletBalanceResponseDto();

        try {

            AmountsDto amounts = walletApi.getReservedAmount(calculateParameters);

            if (calculateParameters.isAmountWithTax() != null) {
                result.getActionStatus().setMessage("" + amounts.getAmount(calculateParameters.isAmountWithTax()));
            }

            result.setAmounts(amounts);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public WalletBalanceResponseDto openBalance(WalletBalanceDto calculateParameters) {

        WalletBalanceResponseDto result = new WalletBalanceResponseDto();

        try {

            AmountsDto amounts = walletApi.getOpenAmount(calculateParameters);

            if (calculateParameters.isAmountWithTax() != null) {
                result.getActionStatus().setMessage("" + amounts.getAmount(calculateParameters.isAmountWithTax()));
            }

            result.setAmounts(amounts);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createReservation(WalletReservationDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.createReservation(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateReservation(WalletReservationDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.updateReservation(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelReservation(Long reservationId) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.cancelReservation(reservationId);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus confirmReservation(WalletReservationDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.confirmReservation(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOperation(WalletOperationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            walletApi.createOperation(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, PagingAndFiltering pagingAndFiltering) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(postData, pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createWalletTemplate(WalletTemplateDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateWalletTemplate(WalletTemplateDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetWalletTemplateResponseDto findWalletTemplate(String walletTemplateCode) {
        GetWalletTemplateResponseDto result = new GetWalletTemplateResponseDto();

        try {
            result.setWalletTemplate(walletApi.find(walletTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeWalletTemplate(String walletTemplateCode) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.remove(walletTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateWalletTemplate(WalletTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            walletApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
