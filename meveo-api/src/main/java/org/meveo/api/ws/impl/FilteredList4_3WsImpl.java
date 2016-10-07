package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.FilteredList4_3Ws;
import org.slf4j.Logger;

@WebService(serviceName = "FilteredList4_3Ws", endpointInterface = "org.meveo.api.ws.FilteredList4_3Ws")
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredList4_3WsImpl extends BaseWs implements FilteredList4_3Ws {

    @Inject
    private Logger log;

    @Inject
    private FilteredListApi filteredListApi;
    
    @Override
    @Deprecated//since 4.4
    public FilteredListResponseDto list(String filter, Integer firstRow, Integer numberOfRows) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String response = filteredListApi.list(filter, firstRow, numberOfRows, getCurrentUser());
            result.getActionStatus().setMessage(response);
            result.setSearchResults(response);
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    @Deprecated//since 4.4
    public FilteredListResponseDto listByXmlInput(FilteredListDto postData) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String response = filteredListApi.listByXmlInput(postData, getCurrentUser());
            result.getActionStatus().setMessage(response);
            result.setSearchResults(response);
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

}
