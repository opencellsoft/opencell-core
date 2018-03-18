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
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.BusinessEntityResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityCustomActionResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Andrius Karpavicius
 **/
@Path("/entityCustomization")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface EntityCustomizationRs extends IBaseRs {

    /**
     * Define a new custom entity template including fields and applicable actions
     * 
     * @param dto
     * @return
     */
    @POST
    @Path("/entity/")
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * Update custom entity template definition
     * 
     * @param dto
     * @return
     */
    @PUT
    @Path("/entity/")
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * Remove custom entity template definition given its code
     * 
     * @param customEntityTemplateCode
     * @return
     */
    @DELETE
    @Path("/entity/{customEntityTemplateCode}")
    public ActionStatus removeEntityTemplate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Get custom entity template definition including its fields and applicable actions
     * 
     * @param customEntityTemplateCode
     * @return customEntityTemplateResponseDto
     */
    @GET
    @Path("/entity/{customEntityTemplateCode}")
    public CustomEntityTemplateResponseDto findEntityTemplate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * List custom entity templates.
     * 
     * @param customEntityTemplateCode An optional and partial custom entity template code
     */
    @GET
    @Path("/entity/list")
    public CustomEntityTemplatesResponseDto listEntityTemplates(@QueryParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Define new or update existing custom entity template definition
     * 
     * @param dto
     * @return
     */
    @POST
    @Path("/cet/createOrUpdate")
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto dto);
    
    
    /**
     * To be sure the compatibility of above method we will create a new one.
     * Define new or update existing custom entity template definition
     * 
     * @param dto
     * @return
     */
    @POST
    @Path("/entity/createOrUpdate")
    public ActionStatus createOrUpdateCustumizedEntityTemplate(CustomEntityTemplateDto dto);

    /**
     * Customize a standard Meveo entity definition by adding fields and/or custom actions
     * 
     * @param dto
     * @return
     */
    @PUT
    @Path("/customize/")
    public ActionStatus customizeEntity(EntityCustomizationDto dto);

    /**
     * Get customizations made on a standard Meveo entity given its class
     * 
     * @param customizedEntityClass Standard Meveo entity class name
     * @return
     */
    @GET
    @Path("/customize/{customizedEntityClass}")
    public EntityCustomizationResponseDto findEntityCustomizations(@PathParam("customizedEntityClass") String customizedEntityClass);

    /**
     * Define a new custom field
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/field/")
    public ActionStatus createField(CustomFieldTemplateDto postData);

    /**
     * Update existing custom field definition
     */
    @PUT
    @Path("/field/")
    public ActionStatus updateField(CustomFieldTemplateDto postData);

    /**
     * Remove custom field definition given its code and entity it applies to
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity custom field applies to
     * @return
     */
    @DELETE
    @Path("/field/{customFieldTemplateCode}/{appliesTo}")
    public ActionStatus removeField(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get custom field definition
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity custom field applies to
     * @return
     */
    @GET
    @Path("/field/")
    public GetCustomFieldTemplateReponseDto findField(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing custom field definition
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/field/createOrUpdate")
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto postData);

    /**
     * Define a new entity action
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/action/")
    public ActionStatus createAction(EntityCustomActionDto postData);

    /**
     * Update existing entity action definition
     * 
     * @param dto
     * @return
     */
    @PUT
    @Path("/action/")
    public ActionStatus updateAction(EntityCustomActionDto dto);

    /**
     * Remove entity action definition given its code and entity it applies to
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return
     */
    @DELETE
    @Path("/action/{actionCode}/{appliesTo}")
    public ActionStatus removeAction(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get entity action definition
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return
     */
    @GET
    @Path("/action/")
    public EntityCustomActionResponseDto findAction(@QueryParam("actionCode") String actionCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing entity action definition
     * 
     * @param dto
     * @return
     */
    @POST
    @Path("/action/createOrUpdate")
    public ActionStatus createOrUpdateAction(EntityCustomActionDto dto);

	/**
	 * Returns a List of BusinessEntities given a CustomFieldTemplate code. The
	 * CustomFieldTemplate is pulled from the database and entityClass is use in
	 * query. For example entity class is of type OfferTemplate, then it will
	 * return a list of OfferTemplates.
	 * 
	 * @param code
	 *            - CFT code
	 * @param wildcode
	 *            - code filter
	 * @return
	 */
	@GET
	@Path("/listBusinessEntityForCFVByCode/")
	BusinessEntityResponseDto listBusinessEntityForCFVByCode(@QueryParam("code") String code,
			@QueryParam("wildcode") String wildcode);

	/**
	 * Returns a list of filtered CustomFieldTemplate of an entity. The list of
	 * entity is evaluted againsts the entity with the given code.
	 * 
	 * @param appliesTo
	 *            - the type of entity to which the CFT applies. eg OFFER,
	 *            SERVICE.
	 * @param entityCode
	 *            - code of the entity
	 * @return
	 */
    @GET
    @Path("/entity/listELFiltered")
    public EntityCustomizationResponseDto listELFiltered(@QueryParam("appliesTo") String appliesTo, @QueryParam("entityCode") String entityCode);

    @POST
    @Path("/entity/action/execute/{actionCode}/{appliesTo}/{entityCode}")
	ActionStatus execute(@PathParam("actionCode") String actionCode, @PathParam("appliesTo") String appliesTo,
			@PathParam("entityCode") String entityCode);

}