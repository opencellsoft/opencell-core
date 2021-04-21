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

import org.meveo.api.CountryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.TradingCountriesResponseDto;
import org.meveo.api.dto.response.GetTradingCountryResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CountryRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * 
 * @author Edward P. Legaspi
 * 
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CountryRsImpl extends BaseRs implements CountryRs {

    @Inject
    private CountryApi countryApi;

    @Override
    public TradingCountriesResponseDto list() {
        TradingCountriesResponseDto result = new TradingCountriesResponseDto();
        result.setPaging( GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering() );

        try {
            result.setTradingCountries( countryApi.list() );
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

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
    public GetTradingCountryResponse find(@QueryParam("countryCode") String countryCode) {
        GetTradingCountryResponse result = new GetTradingCountryResponse();
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
    public ActionStatus remove(@PathParam("countryCode") String countryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.remove(countryCode, "");
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

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}