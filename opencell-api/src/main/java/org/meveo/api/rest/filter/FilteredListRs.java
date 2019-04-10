package org.meveo.api.rest.filter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.elasticsearch.search.sort.SortOrder;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Provides APIs for conducting Full Text Search.
 *
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 * @author Tony Alejandro
 * @lastModifiedVersion 5.0
 **/
@Path("/filteredList")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface FilteredListRs extends IBaseRs {

    /**
     * Execute a filter to retrieve a list of entities
     *
     * @param filter - if the code is set we lookup the filter in DB, else we parse the inputXml to create a transient filter
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return Response
     */
    @Path("/listByFilter")
    @POST
    public Response listByFilter(FilterDto filter, @QueryParam("from") Integer from, @QueryParam("size") Integer size);

    /**
     * Execute a search in Elastic Search on all fields (_all field)
     *
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField Pagination - field used to sort the results
     * @param sortOrder Pagination - ASC or DESC order of the results
     * @return Response object that contains JSON results in String format
     */
    @Path("/search")
    @GET
    public Response search(@QueryParam("classnamesOrCetCodes") String[] classnamesOrCetCodes, @QueryParam("query") String query, @QueryParam("from") Integer from,
            @QueryParam("size") Integer size, @QueryParam("sortField") String sortField, @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Execute a search in Elastic Search on given fields for given values. Query values by field are passed in extra query parameters in a form of fieldName=valueToMatch
     *
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField Pagination - field used to sort the results
     * @param sortOrder Pagination - ASC or DESC order of the results
     * @param info provides request URI information
     * @return Response object that contains JSON results in String format
     */
    @Path("/searchByField")
    @GET
    public Response searchByField(@QueryParam("classnamesOrCetCodes") String[] classnamesOrCetCodes, @QueryParam("from") Integer from, @QueryParam("size") Integer size,
            @QueryParam("sortField") String sortField, @QueryParam("sortOrder") SortOrder sortOrder, @Context UriInfo info);

    /**
     * Clean and reindex Elastic Search repository
     *
     * @return Request processing status
     */
    @Path("/reindex")
    @GET
    public Response reindex();

    /**
     * Execute a search in Elastic Search on all fields (_all field) and all entity types
     *
     * Deprecated in v. 6.2. Use /search instead
     *
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param category search by category that is directly taken from the name of the entity found in entityMapping. property of elasticSearchConfiguration.json. e.g. Customer,
     *        CustomerAccount, AccountOperation, etc. See elasticSearchConfiguration.json entityMapping keys for a list of categories.
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param sortField Pagination - field used to sort the results
     * @param sortOrder Pagination - ASC or DESC order of the results
     * @return Response object that contains JSON results in String format
     */
    @Path("/fullSearch")
    @GET
    @Deprecated
    public Response fullSearch(@QueryParam("query") String query, @QueryParam("category") String category, @QueryParam("from") Integer from, @QueryParam("size") Integer size,
            @QueryParam("sortField") String sortField, @QueryParam("sortOrder") SortOrder sortOrder);
}