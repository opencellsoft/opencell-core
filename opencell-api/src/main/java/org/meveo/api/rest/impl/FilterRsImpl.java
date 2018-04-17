package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.FilterApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.GetFilterResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.FilterRs;

/**
 * @author Tyshan Shi
 * 
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FilterRsImpl extends BaseRs implements FilterRs {
    @Inject
    private FilterApi filterApi;

    @Override
    public ActionStatus createOrUpdate(FilterDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            filterApi.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetFilterResponseDto find(String filterCode) {
        GetFilterResponseDto result = new GetFilterResponseDto();

        try {
            result.setFilter(filterApi.find(filterCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}
