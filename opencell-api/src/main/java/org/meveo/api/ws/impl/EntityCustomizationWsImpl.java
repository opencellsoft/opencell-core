package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.CustomEntityInstanceApi;
import org.meveo.api.CustomEntityTemplateApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.EntityCustomActionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
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
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.EntityCustomizationWs;

/**
 * @author Andrius Karpavicius
 **/
@WebService(serviceName = "EntityCustomizationWs", endpointInterface = "org.meveo.api.ws.EntityCustomizationWs")
@Interceptors({ WsRestApiInterceptor.class })
public class EntityCustomizationWsImpl extends BaseWs implements EntityCustomizationWs {

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CustomEntityTemplateApi customEntityTemplateApi;

    @Inject
    private CustomEntityInstanceApi customEntityInstanceApi;

    @Inject
    private EntityCustomActionApi entityCustomActionApi;

    @Override
    public ActionStatus createField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.create(postData, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.update(postData, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeField(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.remove(customFieldTemplateCode, appliesTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomFieldTemplateReponseDto findField(String customFieldTemplateCode, String appliesTo) {
        GetCustomFieldTemplateReponseDto result = new GetCustomFieldTemplateReponseDto();

        try {
            result.setCustomFieldTemplate(customFieldTemplateApi.find(customFieldTemplateCode, appliesTo));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customFieldTemplateApi.createOrUpdate(postData, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomEntityTemplateResponseDto findEntityTemplate(String code) {

        CustomEntityTemplateResponseDto result = new CustomEntityTemplateResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomEntityTemplate(customEntityTemplateApi.find(code));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeEntityTemplate(String code) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.removeEntityTemplate(code);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;

    }

    @Override
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.create(postData);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.updateEntityTemplate(postData);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.createOrUpdate(postData);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public CustomEntityInstanceResponseDto findCustomEntityInstance(String cetCode, String code) {

        CustomEntityInstanceResponseDto result = new CustomEntityInstanceResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomEntityInstance(customEntityInstanceApi.find(cetCode, code));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCustomEntityInstance(String cetCode, String code) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityInstanceApi.remove(cetCode, code);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityInstanceApi.create(dto);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityInstanceApi.update(dto);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityInstanceApi.createOrUpdate(dto);

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    /**
     * Define a new entity action
     * 
     * @param dto The entity custom action dto
     * @return The action status dto
     */
    @Override
    public ActionStatus createAction(EntityCustomActionDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.create(dto, null);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * Update existing entity action definition
     * 
     * @param dto The entity custom action dto
     * @return The action status dto
     */
    @Override
    public ActionStatus updateAction(EntityCustomActionDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.update(dto, null);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * Remove entity action definition given its code and entity it applies to
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return The action status dto
     */
    @Override
    public ActionStatus removeAction(String actionCode, String appliesTo) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.remove(actionCode, appliesTo);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * Get entity action definition
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return The entity action definition dto
     */
    @Override
    public EntityCustomActionResponseDto findAction(String actionCode, String appliesTo) {

        EntityCustomActionResponseDto result = new EntityCustomActionResponseDto();

        try {
            result.setEntityAction(entityCustomActionApi.find(actionCode, appliesTo));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    /**
     * Define new or update existing entity action definition
     * 
     * @param dto The entity custom action dto
     * @return The action status dto
     */
    @Override
    public ActionStatus createOrUpdateAction(EntityCustomActionDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.createOrUpdate(dto, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus customizeEntity(EntityCustomizationDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityTemplateApi.customizeEntity(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;

    }

    /**
     * Get customizations made on a standard Meveo entity given its class
     * 
     * @param customizedEntityClass Standard Meveo entity class name
     * @return The entity customization response dto
     */
    @Override
    public EntityCustomizationResponseDto findEntityCustomizations(String customizedEntityClass) {

        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityTemplateApi.findEntityCustomizations(customizedEntityClass));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public BusinessEntityResponseDto listBusinessEntityForCFVByCode(String code, String wildcode) {
        BusinessEntityResponseDto result = new BusinessEntityResponseDto();

        try {
            result.setBusinessEntities(customEntityTemplateApi.listBusinessEntityForCFVByCode(code, wildcode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public CustomEntityTemplatesResponseDto listEntityTemplates(String customEntityTemplateCode) {
        CustomEntityTemplatesResponseDto result = new CustomEntityTemplatesResponseDto();

        try {
            result.setCustomEntityTemplates(customEntityTemplateApi.listCustomEntityTemplates(customEntityTemplateCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public EntityCustomizationResponseDto listELFiltered(String appliesTo, String entityCode) {
        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityTemplateApi.listELFiltered(appliesTo, entityCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

	@Override
	public ActionStatus executeAction(String actionCode, String appliesTo, String entityCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			result.setMessage(entityCustomActionApi.execute(actionCode, appliesTo, entityCode));
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}
}