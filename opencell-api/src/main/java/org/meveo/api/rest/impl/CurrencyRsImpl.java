package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CurrencyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.response.GetCurrencyResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CurrencyRs;

/**
 * @author Edward P. Legaspi
 * 
 * @deprecated will be renammed to TradingCurrencyRsImpl
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CurrencyRsImpl extends BaseRs implements CurrencyRs {

    @Inject
    private CurrencyApi currencyApi;

    @Override
    public ActionStatus create(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCurrencyResponse find(String languageCode) {
        GetCurrencyResponse result = new GetCurrencyResponse();

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
            currencyApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
