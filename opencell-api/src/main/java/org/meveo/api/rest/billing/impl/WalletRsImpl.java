/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.billing.impl;

import org.meveo.api.billing.WalletApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;
import org.meveo.api.dto.response.billing.WalletBalanceResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.WalletRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.WalletTemplate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * Wallet operation and balance related REST API
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class WalletRsImpl extends BaseRs implements WalletRs {

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
    public FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(postData, new PagingAndFiltering(null, null, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FindWalletOperationsResponseDto listOperationsGet(String query, 
            String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder, Boolean withRTs) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder), withRTs);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FindWalletOperationsResponseDto list( Boolean withRTs ) {
        try {
            return walletApi.listGetAll(null, GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering(), withRTs );
        } catch (Exception e) {
            FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public FindWalletOperationsResponseDto listOperationsPost(PagingAndFiltering pagingAndFiltering, Boolean withRTs) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(null, pagingAndFiltering, withRTs);
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
}
