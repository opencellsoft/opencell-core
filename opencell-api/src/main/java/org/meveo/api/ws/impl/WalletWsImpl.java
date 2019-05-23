package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.RatedTransactionApi;
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
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.dto.response.billing.WalletBalanceResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.WalletWs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.WalletTemplate;

/**
 * Wallet operation and balance related Webservices API
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@WebService(serviceName = "WalletWs", endpointInterface = "org.meveo.api.ws.WalletWs")
@Interceptors({ WsRestApiInterceptor.class })
public class WalletWsImpl extends BaseWs implements WalletWs {

    @Inject
    private WalletApi walletApi;

    @Inject
    private RatedTransactionApi ratedTransactionApi;

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
            WalletOperation walletOperation = walletApi.createOperation(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(walletOperation.getCode());
            }
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
            WalletTemplate walletTemplate = walletApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(walletTemplate.getCode());
            }
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
            WalletTemplate walletTemplate = walletApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(walletTemplate.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public RatedTransactionListResponseDto listRatedTransactions(PagingAndFiltering pagingAndFiltering) {
        try {
            return ratedTransactionApi.list(pagingAndFiltering);
        } catch (Exception e) {
            RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public ActionStatus cancelRatedTransactions(PagingAndFiltering pagingAndFiltering) {

        ActionStatus actionStatus = new ActionStatus();

        try {
            ratedTransactionApi.cancelRatedTransactions(pagingAndFiltering);
            actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
            actionStatus.setStatus(ActionStatusEnum.FAIL);
            actionStatus.setMessage(e.getLocalizedMessage());
            processException(e, actionStatus);
        }

        return actionStatus;
    }
}