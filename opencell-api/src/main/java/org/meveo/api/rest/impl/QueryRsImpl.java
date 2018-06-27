package org.meveo.api.rest.impl;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

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
     * @param params Contains all query parameters passed. Will be parsed for the following parameters:<br />
     *        query - Search criteria. An HQL query that retrieves the list of entities. It only allows HQL queries<br />
     *        that starts with "from" and does not contain the keyword "into", otherwise, will throw an error.<br />
     *        alias - alias name for the main entity that was used in the query.<br />
     *        e.g. if the query is "FROM Customer cust", then the alias should be "cust"<br />
     *        fields - comma delimited fields. allows nested field names.<br />
     *        offset - Pagination - from record number<br />
     *        limit - Pagination - number of records to retrieve<br />
     *        orderBy - Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.<br />
     *        sortOrder - Sorting - sort order.<br />
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
            String orderBy = parameters.getFirst("orderBy");
            String sortOrder = parameters.getFirst("sortOrder");

            Map<String, Object> queryParams = new HashMap<>();

            parameters.keySet().forEach((key) -> {
                if (!"query".equals(key) && !"alias".equals(key) && !"fields".equals(key) && !"offset".equals(key) && !"limit".equals(key) && !"orderBy".equals(key)
                        && !"sortOrder".equals(key)) {
                    queryParams.put(key, parameters.getFirst(key));
                }
            });

            try {
                response = queryApi.list(query, alias, fields, queryParams, offset, limit, orderBy, sortOrder);
            } catch (MeveoApiException e) {
                processException(e, response.getActionStatus());
            }

        }

        return response;
    }

}
