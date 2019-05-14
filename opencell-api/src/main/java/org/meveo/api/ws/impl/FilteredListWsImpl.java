package org.meveo.api.ws.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.elasticsearch.search.sort.SortOrder;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.index.FullTextSearchApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.FilteredListWs;

@WebService(serviceName = "FilteredListWs", endpointInterface = "org.meveo.api.ws.FilteredListWs")
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredListWsImpl extends BaseWs implements FilteredListWs {

    @Inject
    private FilteredListApi filteredListApi;

    @Inject
    private FullTextSearchApi fullTextSearchApi;

    @Override
    public FilteredListResponseDto listByFilter(FilterDto filter, Integer firstRow, Integer numberOfRows, Map<String, String> parameters) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String searchResults = filteredListApi.listByFilter(filter, firstRow, numberOfRows, parameters);
            result.setSearchResults(searchResults);
        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FilteredListResponseDto search(String[] classnamesOrCetCodes, String query, Integer from, Integer size, String sortField, SortOrder sortOrder) {

        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String searchResults = fullTextSearchApi.search(classnamesOrCetCodes, query, from, size, sortField, sortOrder);
            result.setSearchResults(searchResults);
        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FilteredListResponseDto searchByField(String[] classnamesOrCetCodes, Map<String, String> query, Integer from, Integer size, String sortField, SortOrder sortOrder) {

        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String searchResults = fullTextSearchApi.search(classnamesOrCetCodes, query, from, size, sortField, sortOrder);
            result.setSearchResults(searchResults);
        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus reindex() {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fullTextSearchApi.cleanAndReindex();
        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }
}