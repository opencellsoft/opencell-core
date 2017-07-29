package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.BusinessEntityResponseDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityCustomActionResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;

/**
 * @author Andrius Karpavicius
 **/
@WebService
public interface EntityCustomizationWs extends IBaseWs {

    // Custom entity templates

    @WebMethod
    public ActionStatus createEntityTemplate(@WebParam(name = "customEntityTemplate") CustomEntityTemplateDto postData);

    @WebMethod
    public ActionStatus updateEntityTemplate(@WebParam(name = "customEntityTemplate") CustomEntityTemplateDto postData);

    @WebMethod
    public CustomEntityTemplateResponseDto findEntityTemplate(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus removeEntityTemplate(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus createOrUpdateEntityTemplate(@WebParam(name = "customEntityTemplate") CustomEntityTemplateDto postData);

    // Custom entity instances

    @WebMethod
    public CustomEntityInstanceResponseDto findCustomEntityInstance(@WebParam(name = "cetCode") String cetCode, @WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus removeCustomEntityInstance(@WebParam(name = "cetCode") String cetCode, @WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus createCustomEntityInstance(@WebParam(name = "customEntityInstance") CustomEntityInstanceDto dto);

    @WebMethod
    public ActionStatus updateCustomEntityInstance(@WebParam(name = "customEntityInstance") CustomEntityInstanceDto dto);

    @WebMethod
    public ActionStatus createOrUpdateCustomEntityInstance(@WebParam(name = "customEntityInstance") CustomEntityInstanceDto dto);

    // Custom fields

    @WebMethod
    public ActionStatus createField(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    @WebMethod
    public ActionStatus updateField(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    @WebMethod
    public ActionStatus removeField(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    public GetCustomFieldTemplateReponseDto findField(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    public ActionStatus createOrUpdateField(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    // Entity actions

    @WebMethod
    public ActionStatus createAction(@WebParam(name = "entityAction") EntityCustomActionDto dto);

    @WebMethod
    public ActionStatus updateAction(@WebParam(name = "entityAction") EntityCustomActionDto dto);

    @WebMethod
    public ActionStatus removeAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    public EntityCustomActionResponseDto findAction(@WebParam(name = "actionCode") String actionCode, @WebParam(name = "appliesTo") String appliesTo);

    @WebMethod
    public ActionStatus createOrUpdateAction(@WebParam(name = "entityAction") EntityCustomActionDto dto);

    @WebMethod
    public ActionStatus customizeEntity(@WebParam(name = "entityCustomization") EntityCustomizationDto dto);

    @WebMethod
    public EntityCustomizationResponseDto findEntityCustomizations(@WebParam(name = "customizedEntityClass") String customizedEntityClass);

    @WebMethod
	BusinessEntityResponseDto listBusinessEntityForCFVByCode(@WebParam(name = "code") String code, @WebParam(name = "wildcode") String wildcode);
    
	@WebMethod
	CustomEntityTemplatesResponseDto listEntityTemplates(@WebParam(name = "customEntityTemplateCode") String customEntityTemplateCode);
	
	@WebMethod
	EntityCustomizationResponseDto listELFiltered(@WebParam(name = "appliesTo") String appliesTo, @WebParam(name = "entityCode") String entityCode);

	@WebMethod
	ActionStatus executeAction(@WebParam(name = "actionCode") String actionCode,
			@WebParam(name = "appliesTo") String appliesTo, @WebParam(name = "entityCode") String entityCode);
	
}