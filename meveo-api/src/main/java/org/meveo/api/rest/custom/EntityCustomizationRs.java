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
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityActionScriptDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityActionScriptResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Andrius Karpavicius
 **/
@Path("/entityCustomization")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface EntityCustomizationRs extends IBaseRs {

    @POST
    @Path("/entity/")
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto dto);

    @PUT
    @Path("/entity/")
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto dto);

    @DELETE
    @Path("/entity/{customEntityTemplateCode}")
    public ActionStatus removeEntityTemplate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    @GET
    @Path("/entity/{customEntityTemplateCode}")
    public CustomEntityTemplateResponseDto findEntityTemplate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * List custom entity templates.
     * 
     * @param code Custom entity template code
     */
    @GET
    @Path("/entity/list")
    public CustomEntityTemplatesResponseDto listEntityTemplates(@QueryParam("customEntityTemplateCode") String customEntityTemplateCode);

    @POST
    @Path("/cet/createOrUpdate")
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto dto);

    @PUT
    @Path("/customize/")
    public ActionStatus customizeEntity(EntityCustomizationDto dto);

    @GET
    @Path("/customize/{customizedEntityClass}")
    public EntityCustomizationResponseDto findEntityCustomizations(@PathParam("customizedEntityClass") String customizedEntityClass);

    @POST
    @Path("/field/")
    public ActionStatus createField(CustomFieldTemplateDto postData);

    @PUT
    @Path("/field/")
    public ActionStatus updateField(CustomFieldTemplateDto postData);

    @DELETE
    @Path("/field/{customFieldTemplateCode}/{appliesTo}")
    public ActionStatus removeField(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    @GET
    @Path("/field/")
    public GetCustomFieldTemplateReponseDto findField(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode, @QueryParam("appliesTo") String appliesTo);

    @POST
    @Path("/field/createOrUpdate")
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto postData);

    @POST
    @Path("/action/")
    public ActionStatus createAction(EntityActionScriptDto postData);

    @PUT
    @Path("/action/")
    public ActionStatus updateAction(EntityActionScriptDto dto);

    @DELETE
    @Path("/action/{actionCode}/{appliesTo}")
    public ActionStatus removeAction(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo);

    @GET
    @Path("/action/")
    public EntityActionScriptResponseDto findAction(@QueryParam("actionCode") String actionCode, @QueryParam("appliesTo") String appliesTo);

    @POST
    @Path("/action/createOrUpdate")
    public ActionStatus createOrUpdateAction(EntityActionScriptDto dto);

}