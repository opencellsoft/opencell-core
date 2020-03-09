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

package org.meveo.api.rest.filter.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.elasticsearch.search.sort.SortOrder;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.index.FullTextSearchApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.filter.FilteredListRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;

/**
 * Provides APIs for conducting Full Text Search.
 *
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 * @author Tony Alejandro
 * @lastModifiedVersion 5.0
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredListRsImpl extends BaseRs implements FilteredListRs {

    @Inject
    private FilteredListApi filteredListApi;

    @Inject
    private FullTextSearchApi fullTextSearchApi;

    public Response listByFilter(FilterDto filter, Integer firstRow, Integer numberOfRows) {
        Response.ResponseBuilder responseBuilder = null;
        FilteredListResponseDto result = new FilteredListResponseDto();

        try {
            String searchResults = filteredListApi.listByFilter(filter, firstRow, numberOfRows);
            result.setSearchResults(searchResults);
            responseBuilder = Response.ok();
            responseBuilder.entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL,
                e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        return responseBuilder.build();
    }

    public Response search(String[] classnamesOrCetCodes, String query, Integer from, Integer size, String sortField, SortOrder sortOrder) {
        Response.ResponseBuilder responseBuilder = null;

        try {
            String searchResults = fullTextSearchApi.search(classnamesOrCetCodes, query, from, size, sortField, sortOrder);
            FilteredListResponseDto result = new FilteredListResponseDto();
            result.setSearchResults(searchResults);
            responseBuilder = Response.status(Response.Status.OK).entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL,
                e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    public Response searchByField(String[] classnamesOrCetCodes, Integer from, Integer size, String sortField, SortOrder sortOrder, UriInfo info) {
        Response.ResponseBuilder responseBuilder = null;

        try {

            // Construct query values from query parameters
            Map<String, String> queryValues = new HashMap<>();
            MultivaluedMap<String, String> params = info.getQueryParameters();
            for (String paramKey : params.keySet()) {
                if (!paramKey.equals("classnamesOrCetCodes") && !paramKey.equals("from") && !paramKey.equals("size") && !paramKey.equals("pretty")) {
                    queryValues.put(paramKey, params.getFirst(paramKey));
                }
            }

            String searchResults = fullTextSearchApi.search(classnamesOrCetCodes, queryValues, from, size, sortField, sortOrder);
            FilteredListResponseDto result = new FilteredListResponseDto();
            result.setSearchResults(searchResults);
            responseBuilder = Response.status(Response.Status.OK).entity(result);

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL,
                e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    public Response reindex() {
        Response.ResponseBuilder responseBuilder = null;

        try {
            fullTextSearchApi.cleanAndReindex();
            responseBuilder = Response.status(Response.Status.OK).entity(new ActionStatus(ActionStatusEnum.SUCCESS, ""));

        } catch (MeveoApiException e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL,
                e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, e.getMessage()));
        }

        Response response = responseBuilder.build();
        log.debug("RESPONSE={}", response.getEntity());
        return response;
    }

    @Override
    @Deprecated
    public Response fullSearch(String query, String category, Integer from, Integer size, String sortField, SortOrder sortOrder) {

        try {

            boolean noCategory = StringUtils.isBlank(category);
            boolean noQuery = StringUtils.isBlank(query);

            if (noCategory && noQuery) {
                throw new MissingParameterException(Arrays.asList("category", "query"));
            }

            return search(category != null ? new String[] { category } : null, query, from, size, sortField, sortOrder);

        } catch (MeveoApiException e) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));

            Response response = responseBuilder.build();
            log.debug("RESPONSE={}", response.getEntity());
            return response;
        }
    }
}