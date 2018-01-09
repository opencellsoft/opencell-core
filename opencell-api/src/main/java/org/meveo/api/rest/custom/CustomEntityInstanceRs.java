package org.meveo.api.rest.custom;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Andrius Karpavicius
 **/
@Path("/customEntityInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomEntityInstanceRs extends IBaseRs {

    /**
     * Create a new custom entity instance using a custom entity template.
     *
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}")
    ActionStatus create(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Update an existing custom entity instance using a custom entity template
     * 
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @PUT
    @Path("/{customEntityTemplateCode}")
    ActionStatus update(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Remove an existing custom entity instance with a given code from a custom entity template given by code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code The custom entity instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customEntityTemplateCode}/{code}")
    ActionStatus remove(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * Find a #### with a given (exemple) code .
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code The custom entity instance's code
     * @return Return a customEntityInstance
     */
    @GET
    @Path("/{customEntityTemplateCode}/{code}")
    CustomEntityInstanceResponseDto find(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * List custom entity instances.
     * 
     * @param customEntityTemplateCode The custom entity instance's code
     * @return A list of custom entity instances
     */
    @GET
    @Path("/list/{customEntityTemplateCode}")
    CustomEntityInstancesResponseDto list(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Create new or update an existing custom entity instance with a given code.
     * 
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode code of custome entity template.
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/createOrUpdate")
    ActionStatus createOrUpdate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);
}