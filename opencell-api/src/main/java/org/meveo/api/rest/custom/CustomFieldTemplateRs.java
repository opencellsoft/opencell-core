package org.meveo.api.rest.custom;

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
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/customFieldTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomFieldTemplateRs extends IBaseRs {

    /**
     * Define a new custom field
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(CustomFieldTemplateDto postData);

    /**
     * Update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CustomFieldTemplateDto postData);

    /**
     * Remove custom field definition given its code and entity it applies to
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @DELETE
    @Path("/{customFieldTemplateCode}/{appliesTo}")
    ActionStatus remove(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get custom field definition
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return instance of GetCustomFieldTemplateReponseDto
     */
    @GET
    @Path("/")
    GetCustomFieldTemplateReponseDto find(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomFieldTemplateDto postData);

    /**
     * Enable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/{customFieldTemplateCode}/{appliesTo}/enable")
    ActionStatus enable(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Disable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/{customFieldTemplateCode}/{appliesTo}/disable")
    ActionStatus disable(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

}