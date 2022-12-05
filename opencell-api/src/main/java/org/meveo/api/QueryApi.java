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

package org.meveo.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.QueryResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.service.base.QueryService;

/**
 * Provides API methods to retrieve data based on HQL query.
 *
 * @author Tony Alejandro
 * @version %I%, %G%
 * @since 5.1
 * @lastModifiedVersion 5.1
 */
@Stateless
public class QueryApi extends BaseApi {

    private static Integer RESULT_SET_MAX_SIZE = 1000;

    @Inject
    private QueryService queryService;

    private final String[] KEYWORD_BLACKLIST = { "into ", "create ", "delete ", "drop ", "declare ", "update ", "nchar ", "exec " };

    /**
     * Checks if the query fragment contains any of the blacklisted SQL keywords.
     *
     * @param queryFragment the query fragment to be checked for blacklisted words.
     * @throws MeveoApiException when a black-listed keyword is used
     */
    private void validateQueryFragment(Object queryFragment) throws MeveoApiException {

        if (queryFragment != null) {
            String fragment = queryFragment.toString().toLowerCase();
            boolean found = Arrays.stream(KEYWORD_BLACKLIST).anyMatch((keyword) -> fragment.contains(keyword));
            if (found) {
                throw new MeveoApiException("Invalid keyword was used on query.");
            }
        }
    }

    /**
     * Retrieves a data list based on an HQL query.
     *
     * @param query - Search criteria. An HQL query that retrieves the list of entities. It only allows HQL queries<br>
     *        that starts with "from" and does not contain the keyword "into", otherwise, will throw an error.
     * @param alias - alias name for the main entity that was used in the query.<br>
     *        e.g. if the query is "FROM Customer cust", then the alias should be "cust"
     * @param fields - comma delimited fields. allows nested field names.
     * @param params - a map of parameters that will be passed into the HQL query
     * @param offset - from record number
     * @param limit - number of records to retrieve
     * @param sortBy - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder - sort order.
     * @param groupBy - Grouping - group by clause, allow to use aggregation funciton like sum, avg, count.
     * 
     * @return QueryResponse object
     * @throws MeveoApiException when query does not start with "from" or when query fails
     */
    public QueryResponse list(String query, String alias, String fields, Map<String, Object> params, String offset, String limit, String sortBy, String sortOrder, String groupBy)
            throws MeveoApiException {

        if (StringUtils.isEmpty(query)) {
            missingParameters.add("query");
        }

        if (StringUtils.isEmpty(alias)) {
            missingParameters.add("alias");
        }

        handleMissingParameters();

        if (!query.toLowerCase().startsWith("from")) {
            throw new MeveoApiException("Query must start with \"from\"");
        }

        validateQueryFragment(query);
        validateQueryFragment(alias);
        validateQueryFragment(fields);
        validateQueryFragment(sortBy);
        validateQueryFragment(groupBy);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            validateQueryFragment(entry.getValue());
        }

        // these are the default query parameter values
        String entityFields = "id";
        int start = 0;
        int maxRows = 20;

        if (!StringUtils.isEmpty(fields)) {
            entityFields = fields;
        }

        if (StringUtils.isNumeric(offset)) {
            start = Integer.parseInt(offset);
        }

        if (StringUtils.isNumeric(limit)) {
            maxRows = Integer.parseInt(limit);
            maxRows = maxRows > RESULT_SET_MAX_SIZE ? RESULT_SET_MAX_SIZE : maxRows;
        }

        if (StringUtils.isEmpty(sortBy)) {
            sortBy = entityFields.split(",")[0].trim();
        }

        sortOrder = sortOrder != null ? sortOrder.toUpperCase() : null;
        sortOrder = "DESCENDING".equals(sortOrder) ? "DESC" : "ASC";

        String json = "";
        try {
            List<Map<String, Object>> rows = queryService.executeQuery(query, alias, entityFields, params, start, maxRows, sortBy, sortOrder, groupBy);
            json = JsonUtils.toJson(rows, false);
        } catch (Throwable e) {
            throw new MeveoApiException(e);
        }

        int count;
        try {
            count = queryService.count(query, alias, entityFields, params);
        } catch (Throwable e) {
            throw new MeveoApiException(e);
        }

        QueryResponse response = new QueryResponse();
        ActionStatus status = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        PagingAndFiltering paging = new PagingAndFiltering();
        paging.setFields(fields);
        paging.setTotalNumberOfRecords(count);
        paging.setOffset(start);
        paging.setLimit(maxRows);
        paging.setSortBy(sortBy);
        paging.setSortOrder("DESC".equalsIgnoreCase(sortOrder) ? PagingAndFiltering.SortOrder.DESCENDING : PagingAndFiltering.SortOrder.ASCENDING);

        response.setActionStatus(status);
        response.setPaging(paging);
        response.setResult(json);

        return response;
    }
}
