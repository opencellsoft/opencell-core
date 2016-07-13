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
import org.meveo.api.rest.security.RSSecured;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 4, 2016 4:05:47 AM
 *
 */
@Path("/communication/meveoInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface MeveoInstanceRs extends IBaseRs {

	/**
	 * create a meveoInstance by dto
	 * @param meveoInstanceDto
	 * @return
	 */
	@POST
    @Path("/")
    ActionStatus create(MeveoInstanceDto meveoInstanceDto);

	/**
	 * update a meveoInstance by dto
	 * @param meveoInstanceDto
	 * @return
	 */
    @PUT
    @Path("/")
    ActionStatus update(MeveoInstanceDto meveoInstanceDto);

    /**
     * find a meveoInstance by code
     * @param code
     * @return
     */
    @GET
    @Path("/")
    MeveoInstanceResponseDto find(@QueryParam("code") String code);

    /**
     * remove a meveoInstance by code
     * @param code
     * @return
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * list meveoInstances
     * @return
     */
    @GET
    @Path("/list")
    MeveoInstancesResponseDto list();

    /**
     * createOrUpdate a meveoInstance by dto
     * @param meveoInstanceDto
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(MeveoInstanceDto meveoInstanceDto);
}

