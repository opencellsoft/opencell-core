package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.FilteredList4_3Ws;

@WebService(serviceName = "FilteredList4_3Ws", endpointInterface = "org.meveo.api.ws.FilteredList4_3Ws")
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredList4_3WsImpl extends BaseWs implements FilteredList4_3Ws {

    @Inject
    private FilteredListApi filteredListApi;

    @Override
    @Deprecated // since 4.4
    public FilteredListResponseDto list(String filter, Integer firstRow, Integer numberOfRows) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String response = filteredListApi.list(filter, firstRow, numberOfRows);
            result.getActionStatus().setMessage(response);
            result.setSearchResults(response);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    @Deprecated // since 4.4
    public FilteredListResponseDto listByXmlInput(FilteredListDto postData) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String response = filteredListApi.listByXmlInput(postData);
            result.getActionStatus().setMessage(response);
            result.setSearchResults(response);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}