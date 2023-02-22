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

package org.meveo.api.rest.impl;

import org.meveo.api.CurrencyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.billing.ExchangeRateDto;
import org.meveo.api.dto.response.GetTradingCurrencyResponse;
import org.meveo.api.dto.response.TradingCurrenciesResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CurrencyRs;
import org.meveo.api.rest.admin.impl.FileUploadForm;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

/**
 * @author Edward P. Legaspi
 * 
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CurrencyRsImpl extends BaseRs implements CurrencyRs {

    @Inject
    private CurrencyApi currencyApi;

	@Override
    public TradingCurrenciesResponseDto list() {
        TradingCurrenciesResponseDto result = new TradingCurrenciesResponseDto();
        result.setPaging( GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering() );

        try {
            result.setTradingCurrencies( currencyApi.list() );
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus create(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            CurrencyDto resultDto = currencyApi.create(postData);
            result.setEntityId(resultDto.getId());

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTradingCurrencyResponse find(String languageCode) {
        GetTradingCurrencyResponse result = new GetTradingCurrencyResponse();

        try {
            result.setCurrency(currencyApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            CurrencyDto currencyDto = currencyApi.createOrUpdate(postData);
            result.setEntityId(currencyDto.getId());
            result.setEntityCode(currencyDto.getCode());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus addFunctionalCurrency(CurrencyDto postData) {
        return currencyApi.addFunctionalCurrency(postData);
    }

    @Override
    public Response addExchangeRate(org.meveo.api.dto.ExchangeRateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            Long idEntity = currencyApi.addExchangeRate(postData);
            result.setEntityId(idEntity);
        } catch (MeveoApiException e) {
            return errorResponse(e, result);
        } catch (Exception e) {
            processException(e, result);
        } 
        return Response.ok(result).build();
    }

    @Override
    public ActionStatus updateExchangeRate(Long id, ExchangeRateDto postData) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.updateExchangeRate(id, postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    public ActionStatus removeExchangeRateById(Long id) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.removeExchangeRateById(id);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

	@Override
	public ActionStatus importExchangeRate(FileUploadForm exchangeRateForm) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            List<String> message = currencyApi.importExchangeRate(exchangeRateForm);
            result.setMessage(message.toString());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
	}

}