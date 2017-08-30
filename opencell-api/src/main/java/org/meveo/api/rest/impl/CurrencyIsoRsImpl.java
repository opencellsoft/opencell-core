package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CurrencyIsoApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.dto.response.GetCurrenciesIsoResponse;
import org.meveo.api.dto.response.GetCurrencyIsoResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CurrencyIsoRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CurrencyIsoRsImpl extends BaseRs implements CurrencyIsoRs {

    @Inject
    private CurrencyIsoApi currencyIsoApi;

    @Override
    public ActionStatus create(CurrencyIsoDto currencyIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.create(currencyIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCurrencyIsoResponse find(String languageCode) {
        GetCurrencyIsoResponse result = new GetCurrencyIsoResponse();

        try {
            result.setCurrency(currencyIsoApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CurrencyIsoDto currencyIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.update(currencyIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(CurrencyIsoDto currencyIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyIsoApi.createOrUpdate(currencyIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

	@Override
	public GetCurrenciesIsoResponse list() {
		GetCurrenciesIsoResponse result = new GetCurrenciesIsoResponse();

        try {
            result.setCurrencies(currencyIsoApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
	}

}
