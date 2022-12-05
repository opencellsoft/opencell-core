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

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import org.meveo.api.QueryApi;
import org.meveo.api.dto.response.QueryResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.QueryRs;

/**
 * Allows HQL queries to be called directly.
 *
 * @author Tony Alejandro
 * @version %I%, %G%
 * @since 5.1
 * @lastModifiedVersion 5.1
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class QueryRsImpl extends BaseRs implements QueryRs {

    @Inject
    private QueryApi queryApi;

    /**
     * THIS IS A TEMPORARY API FOR DYNAMIC PORTAL USE ONLY.  IT MAY BE REMOVED AT ANY TIME.
     *
     * @param params Contains all query parameters passed. Will be parsed for the following parameters:<br>
     *        query - Search criteria. An HQL query that retrieves the list of entities. It only allows HQL queries<br>
     *        that starts with "from" and does not contain the keyword "into", otherwise, will throw an error.<br>
     *        alias - alias name for the main entity that was used in the query.<br>
     *        e.g. if the query is "FROM Customer cust", then the alias should be "cust"<br>
     *        fields - comma delimited fields. allows nested field names.<br>
     *        offset - Pagination - from record number<br>
     *        limit - Pagination - number of records to retrieve<br>
     *        sortBy - Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.<br>
     *        sortOrder - Sorting - sort order.<br>
     *        groupBy - Grouping - group by clause, allow to use aggregation funciton like sum, avg, count.<br>
     *
     *        all other parameters will be used as query parameters to the HQL
     *
     * @return QueryResponse object that contains the status, pagination, and the result in json string form.
     */
    @Override
    public QueryResponse list(UriInfo params) {

        MultivaluedMap<String, String> parameters = params.getQueryParameters();
        QueryResponse response = new QueryResponse();

        if (parameters != null && !parameters.isEmpty()) {

            String query = parameters.getFirst("query");
            String alias = parameters.getFirst("alias");
            String fields = parameters.getFirst("fields");
            String offset = parameters.getFirst("offset");
            String limit = parameters.getFirst("limit");
            String sortBy = parameters.getFirst("sortBy");
            String sortOrder = parameters.getFirst("sortOrder");
            String groupBy = parameters.getFirst("groupBy");
            
            Map<String, Object> queryParams = new HashMap<>();

            parameters.keySet().forEach((key) -> {
                if (!"query".equals(key) && !"alias".equals(key) && !"fields".equals(key) && !"offset".equals(key) && !"limit".equals(key) && !"sortBy".equals(key)
                        && !"sortOrder".equals(key) && !"groupBy".equals(key)) {
                    queryParams.put(key, parameters.getFirst(key));
                }
            });

            try {
                response = queryApi.list(query, alias, fields, queryParams, offset, limit, sortBy, sortOrder, groupBy);
            } catch (MeveoApiException e) {
                processException(e, response.getActionStatus());
            }

        }

        return response;
    }

}
