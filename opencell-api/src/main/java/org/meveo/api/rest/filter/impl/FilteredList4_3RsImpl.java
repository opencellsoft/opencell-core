package org.meveo.api.rest.filter.impl;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.filter.FilteredList4_3Api;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.filter.FilteredList4_3Rs;
import org.meveo.api.rest.impl.BaseRs;
import org.slf4j.Logger;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredList4_3RsImpl extends BaseRs implements FilteredList4_3Rs {

    @Inject
    private FilteredList4_3Api filteredListApi;

    @Override
    public Response list(String filter, Integer firstRow, Integer numberOfRows) {
        Response.ResponseBuilder responseBuilder = null;
        FilteredListResponseDto result = new FilteredListResponseDto();

        try {
            String response = filteredListApi.list(filter, firstRow, numberOfRows);
            result.getActionStatus().setMessage(response);
            responseBuilder = Response.ok();
            responseBuilder.entity(result);

        } catch (Exception e) {
            log.debug("RESPONSE={}", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }

        return responseBuilder.build();
    }

    @Override
    public Response listByXmlInput(FilteredListDto postData) {
        Response.ResponseBuilder responseBuilder = null;
        FilteredListResponseDto result = new FilteredListResponseDto();

        try {
            String response = filteredListApi.listByXmlInput(postData);
            result.getActionStatus().setMessage(response);
            responseBuilder = Response.ok();
            responseBuilder.entity(result);

        } catch (Exception e) {
            log.debug("RESPONSE={}", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }

        return responseBuilder.build();
    }

    public Response search(String[] classnamesOrCetCodes, String query, Integer from, Integer size) {
        Response.ResponseBuilder responseBuilder = null;

        try {
            String response = filteredListApi.search(classnamesOrCetCodes, query, from, size);
            FilteredListResponseDto result = new FilteredListResponseDto();
            result.getActionStatus().setMessage(response);
            result.setSearchResults(response);
            responseBuilder = Response.status(Response.Status.OK).entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    public Response searchByField(String[] classnamesOrCetCodes, Integer from, Integer size, UriInfo info) {
        Response.ResponseBuilder responseBuilder = null;

        try {

            // Construct query values from query parameters
            Map<String, String> queryValues = new HashMap<>();
            MultivaluedMap<String, String> params = info.getQueryParameters();
            for (String paramKey : params.keySet()) {
                if (!paramKey.equals("classnames") && !paramKey.equals("from") && !paramKey.equals("size") && !paramKey.equals("pretty")) {
                    queryValues.put(paramKey, params.getFirst(paramKey));
                }
            }

            String response = filteredListApi.search(classnamesOrCetCodes, queryValues, from, size);
            FilteredListResponseDto result = new FilteredListResponseDto();
            result.getActionStatus().setMessage(response);
            result.setSearchResults(response);
            responseBuilder = Response.status(Response.Status.OK).entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

}