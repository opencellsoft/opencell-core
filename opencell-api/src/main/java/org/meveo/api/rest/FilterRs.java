package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.GetFilterResponseDto;

/**
 * @author Tyshan Shi
 * 
 **/
@Path("/filter")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface FilterRs extends IBaseRs {

    /**
     * Create new or update an existing filter with a given code
     * 
     * @param postData The filter's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    public ActionStatus createOrUpdate(FilterDto postData);

    /**
     * Find a filter with a given code
     *
     * @param filterCode The job instance's code
     * @return
     */
    @Path("/")
    @GET
    public GetFilterResponseDto find(@QueryParam("filterCode") String filterCode);
}
