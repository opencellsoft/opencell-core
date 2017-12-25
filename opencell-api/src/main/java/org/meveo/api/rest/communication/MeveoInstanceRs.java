package org.meveo.api.rest.communication;

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
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.communication.MeveoInstanceResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 4, 2016 4:05:47 AM
 *
 */
@Path("/communication/meveoInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface MeveoInstanceRs extends IBaseRs {

	/**
	 * Create a meveoInstance by dto.
     *
	 * @param meveoInstanceDto meveo instance
	 * @return action status
	 */
	@POST
    @Path("/")
    ActionStatus create(MeveoInstanceDto meveoInstanceDto);

	/**
	 * Update a meveoInstance by dto
     *
	 * @param meveoInstanceDto
	 * @return
	 */
    @PUT
    @Path("/")
    ActionStatus update(MeveoInstanceDto meveoInstanceDto);

    /**
     * Find a meveoInstance by code
     *
     * @param code
     * @return
     */
    @GET
    @Path("/")
    MeveoInstanceResponseDto find(@QueryParam("code") String code);

    /**
     * Remove a meveoInstance by code
     *
     * @param code
     * @return
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List meveoInstances
     *
     * @return
     */
    @GET
    @Path("/list")
    MeveoInstancesResponseDto list();

    /**
     * CreateOrUpdate a meveoInstance by dto
     *
     * @param meveoInstanceDto
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(MeveoInstanceDto meveoInstanceDto);
}

