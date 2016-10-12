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

import org.meveo.api.dto.FilterDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/filteredList")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface FilteredListRs extends IBaseRs {

	/**
     * Execute a filter to retrieve a list of entities
     * 
     * @param filter - if the code is set we lookup the filter in DB, else we parse the inputXml to create a transient filter
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return
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
     * @return
     */
    @Path("/search")
    @GET
    public Response search(@QueryParam("classnamesOrCetCodes") String[] classnamesOrCetCodes, @QueryParam("query") String query, @QueryParam("from") Integer from,
            @QueryParam("size") Integer size);

    /**
     * Execute a search in Elastic Search on given fields for given values. Query values by field are passed in extra query parameters in a form of fieldName=valueToMatch
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return
     */
    @Path("/searchByField")
    @GET
    public Response searchByField(@QueryParam("classnamesOrCetCodes") String[] classnamesOrCetCodes, @QueryParam("from") Integer from, @QueryParam("size") Integer size,
            @Context UriInfo info);

    /**
     * Clean and reindex Elastic Search repository
     * 
     * @return
     */
    @Path("/reindex")
    @GET
    public Response reindex();
}