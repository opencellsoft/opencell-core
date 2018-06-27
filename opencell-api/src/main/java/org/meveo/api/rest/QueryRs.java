package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.dto.response.QueryResponse;

/**
 * Allows HQL queries to be called directly.
 *
 * @author Tony Alejandro
 * @version %I%, %G%
 * @since 5.1
 * @lastModifiedVersion 5.1
 */
@Path("/query")
@Consumes({ MediaType.APPLICATION_JSON})
@Produces({ MediaType.APPLICATION_JSON})
public interface QueryRs {

    /**
     * THIS IS A TEMPORARY API FOR DYNAMIC PORTAL USE ONLY.  IT MAY BE REMOVED AT ANY TIME.
     *
     * @param params Contains all query parameters passed. Will be parsed for the following parameters:<br />
     *        query - Search criteria. An HQL query that retrieves the list of entities. It only allows HQL queries<br />
     *        that start with "from" and does not contain the keyword "into", otherwise, will throw an error.<br />
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
    @GET
    @Path("/")
    QueryResponse list(@Context UriInfo params);

}
