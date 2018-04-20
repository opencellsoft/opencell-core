package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.FilterApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.GetFilterResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.FilterWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "FilterWs", endpointInterface = "org.meveo.api.ws.FilterWs")
@Interceptors({ WsRestApiInterceptor.class })
public class FilterWsImpl extends BaseWs implements FilterWs {

    @Inject
    private FilterApi filterApi;

    @Override
    public ActionStatus createOrUpdateFilter(FilterDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            filterApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetFilterResponseDto findFilter(String code) {
        GetFilterResponseDto result = new GetFilterResponseDto();

        try {
            result.setFilter(filterApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enableFilter(String code) {
        ActionStatus result = new ActionStatus();

        try {
            filterApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableFilter(String code) {
        ActionStatus result = new ActionStatus();

        try {
            filterApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}