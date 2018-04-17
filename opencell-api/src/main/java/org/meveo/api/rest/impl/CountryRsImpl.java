package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.meveo.api.CountryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CountryRs;

/**
 * 
 * @author Edward P. Legaspi
 * 
 * @deprecated will be renammed to TradingCountryRsImpl
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CountryRsImpl extends BaseRs implements CountryRs {

    @Inject
    private CountryApi countryApi;

    /***
     * Creates an instance of @see TradingCountry base on @see Country.
     * 
     * @param countryDto the data transfer object for Country
     * @return Request processing status
     */
    @Override
    public ActionStatus create(CountryDto countryDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.create(countryDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCountryResponse find(@QueryParam("countryCode") String countryCode) {
        GetCountryResponse result = new GetCountryResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCountry(countryApi.find(countryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(@PathParam("countryCode") String countryCode, @PathParam("currencyCode") String currencyCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.remove(countryCode, currencyCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CountryDto countryDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.update(countryDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(CountryDto countryDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.createOrUpdate(countryDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
