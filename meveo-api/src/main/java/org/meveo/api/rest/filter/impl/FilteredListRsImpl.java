package org.meveo.api.rest.filter.impl;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.index.FullTextSearchApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.filter.FilteredListRs;
import org.meveo.api.rest.impl.BaseRs;
import org.slf4j.Logger;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredListRsImpl extends BaseRs implements FilteredListRs {

    @Inject
    private Logger log;

    @Inject
    private FilteredListApi filteredListApi;

    @Inject
    private FullTextSearchApi fullTextSearchApi;

    public Response listByFilter(FilterDto filter, Integer firstRow, Integer numberOfRows) {
        Response.ResponseBuilder responseBuilder = null;
        FilteredListResponseDto result = new FilteredListResponseDto();

        try {
            String searchResults = filteredListApi.listByFilter(filter, firstRow, numberOfRows, getCurrentUser());
            result.setSearchResults(searchResults);
            responseBuilder = Response.ok();
            responseBuilder.entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION
                    : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        return responseBuilder.build();
    }

    public Response search(String[] classnamesOrCetCodes, String query, Integer from, Integer size) {
        Response.ResponseBuilder responseBuilder = null;

        try {
            String searchResults = fullTextSearchApi.search(classnamesOrCetCodes, query, from, size, getCurrentUser());
            FilteredListResponseDto result = new FilteredListResponseDto();
            result.setSearchResults(searchResults);
            responseBuilder = Response.status(Response.Status.OK).entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION
                    : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
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

            String searchResults = fullTextSearchApi.search(classnamesOrCetCodes, queryValues, from, size, getCurrentUser());
            FilteredListResponseDto result = new FilteredListResponseDto();
            result.setSearchResults(searchResults);
            responseBuilder = Response.status(Response.Status.OK).entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION
                    : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response reindex() {
        Response.ResponseBuilder responseBuilder = null;

        try {
            fullTextSearchApi.cleanAndReindex(getCurrentUser());
            responseBuilder = Response.status(Response.Status.OK).entity(new ActionStatus(ActionStatusEnum.SUCCESS, ""));

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION
                    : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }
}