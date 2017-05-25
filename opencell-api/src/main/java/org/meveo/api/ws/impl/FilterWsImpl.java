package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.FilterApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
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

}
