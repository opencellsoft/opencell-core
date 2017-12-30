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
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

/**
 * @author Edward P. Legaspi
 **/
@Path("/scriptInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ScriptInstanceRs extends IBaseRs {

    /**
     * Create a new script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ScriptInstanceReponseDto create(ScriptInstanceDto postData);

    /**
     * Update an existing script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ScriptInstanceReponseDto update(ScriptInstanceDto postData);

    /**
     * Remove an existing script instance with a given code .
     * 
     * @param scriptInstanceCode The script instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{scriptInstanceCode}")
    ActionStatus remove(@PathParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Find a script instance with a given code.
     * 
     * @param scriptInstanceCode The script instance's code
     * @return script instance
     */
    @GET
    @Path("/")
    GetScriptInstanceResponseDto find(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Create new or update an existing script instance with a given code.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData);
}
