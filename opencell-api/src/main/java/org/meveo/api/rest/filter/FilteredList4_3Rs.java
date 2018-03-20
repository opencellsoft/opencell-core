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

import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.rest.IBaseRs;

@Path("/filteredList4_3")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface FilteredList4_3Rs extends IBaseRs {

	@Path("/")
    @GET
    public Response list(@QueryParam("filter") String filter, @QueryParam("firstRow") Integer firstRow, @QueryParam("numberOfRows") Integer numberOfRows);

    @Path("/xmlInput")
    @POST
    public Response listByXmlInput(FilteredListDto postData);

    /**
     * Execute a search in Elastic Search on all fields (_all field)
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return Request processing status
     */
    @Path("/search")
    @GET
    public Response search(@QueryParam("classnamesOrCetCodes") String[] classnamesOrCetCodes, @QueryParam("query") String query, @QueryParam("from") Integer from, @QueryParam("size") Integer size);

    /**
     * Execute a search in Elastic Search on given fields for given values. Query values by field are passed in extra query parameters in a form of fieldName=valueToMatch
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @param info provides request URI information
     * @return Request processing status
     */
    @Path("/searchByField")
    @GET
    public Response searchByField(@QueryParam("classnamesOrCetCodes") String[] classnamesOrCetCodes, @QueryParam("from") Integer from, @QueryParam("size") Integer size, @Context UriInfo info);
}