package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.response.GetTerminationReasonResponse;

@Path("/terminationReason")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TerminationReasonRs extends IBaseRs {

    /**
     * Create a new termination reason.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(TerminationReasonDto postData);

    /**
     * Update an existing termination reason.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(TerminationReasonDto postData);

    /**
     * Create new or update an existing termination reason with a given code.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(TerminationReasonDto postData);

    /**
     * Remove an existing termination reason with a given code.
     * 
     * @param terminationReasonCode The termination reason's code
     * @return Request processing status
     */
    @Path("/{terminationReasonCode}")
    @DELETE
    ActionStatus remove(@PathParam("terminationReasonCode") String terminationReasonCode);

    /**
     * Find a termination reason with a given code.
     * 
     * @param code The termination reason's code
     * @return found termination reason
     */
    @Path("/")
    @GET
    GetTerminationReasonResponse find(@QueryParam("terminationReasonCode") String code);

    /**
     * List of termination reasons.
     * 
     * @return A list of termination reasons
     */
    @Path("/list")
    @GET
    GetTerminationReasonResponse list();

}
