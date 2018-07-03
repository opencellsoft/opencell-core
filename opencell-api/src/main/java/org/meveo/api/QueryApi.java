package org.meveo.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sun.org.apache.bcel.internal.generic.NEW;
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

    @Inject
    private QueryService queryService;

    private final String[] KEYWORD_BLACKLIST = { "into ", "create ", "delete ", "drop ", "declare ", "update ", "nchar ", "exec " };

    /**
     * Checks if the query fragment contains any of the blacklisted SQL keywords.
     *
     * @param queryFragment the query fragment to be checked for blacklisted words.
     * @throws MeveoApiException
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
     * @param query - Search criteria. An HQL query that retrieves the list of entities. It only allows HQL queries<br />
     *        that starts with "from" and does not contain the keyword "into", otherwise, will throw an error.
     * @param alias - alias name for the main entity that was used in the query.<br />
     *        e.g. if the query is "FROM Customer cust", then the alias should be "cust"
     * @param fields - comma delimited fields. allows nested field names.
     * @param params - a map of parameters that will be passed into the HQL query
     * @param offset - from record number
     * @param limit - number of records to retrieve
     * @param sortBy - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder - sort order.
     * 
     * @return
     * @throws MeveoApiException
     */
    public QueryResponse list(String query, String alias, String fields, Map<String, Object> params, String offset, String limit, String sortBy, String sortOrder)
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
            maxRows = maxRows > 100 ? 100 : maxRows;
        }

        if (StringUtils.isEmpty(sortBy)) {
            sortBy = entityFields.split(",")[0].trim();
        }

        sortOrder = sortOrder != null ? sortOrder.toUpperCase() : null;
        sortOrder = "DESCENDING".equals(sortOrder) ? "DESC" : "ASC";

        String json = "";
        try {
            List<Map<String, Object>> rows = queryService.executeQuery(query, alias, entityFields, params, start, maxRows, sortBy, sortOrder);
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
