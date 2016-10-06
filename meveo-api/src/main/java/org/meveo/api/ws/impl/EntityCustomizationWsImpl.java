package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.CustomEntityApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.EntityCustomActionApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.EntityCustomActionResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.EntityCustomizationWs;
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * @author Andrius Karpavicius
 **/
@WebService(serviceName = "EntityCustomizationWs", endpointInterface = "org.meveo.api.ws.EntityCustomizationWs")
@Interceptors({ WsRestApiInterceptor.class })
public class EntityCustomizationWsImpl extends BaseWs implements EntityCustomizationWs {

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CustomEntityApi customEntityApi;

    @Inject
    private EntityCustomActionApi entityCustomActionApi;

    @Override
    public ActionStatus createField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.create(postData, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus updateField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.update(postData, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus removeField(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.remove(customFieldTemplateCode, appliesTo, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public GetCustomFieldTemplateReponseDto findField(String customFieldTemplateCode, String appliesTo) {
        GetCustomFieldTemplateReponseDto result = new GetCustomFieldTemplateReponseDto();

        try {
            result.setCustomFieldTemplate(customFieldTemplateApi.find(customFieldTemplateCode, appliesTo, getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customFieldTemplateApi.createOrUpdate(postData, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public CustomEntityTemplateResponseDto findEntityTemplate(String code) {

        CustomEntityTemplateResponseDto result = new CustomEntityTemplateResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomEntityTemplate(customEntityApi.findEntityTemplate(code, getCurrentUser()));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeEntityTemplate(String code) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.removeEntityTemplate(code, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;

    }

    @Override
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createEntityTemplate(postData, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.updateEntityTemplate(postData, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createOrUpdateEntityTemplate(postData, getCurrentUser());

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
            result.setCustomEntityInstance(customEntityApi.findEntityInstance(cetCode, code, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(cetCode), "read")));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCustomEntityInstance(String cetCode, String code) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.removeEntityInstance(cetCode, code, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(cetCode), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createEntityInstance(dto, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(dto.getCetCode()), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.updateEntityInstance(dto, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(dto.getCetCode()), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createOrUpdateEntityInstance(dto, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(dto.getCetCode()), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        return result;
    }

    /**
     * Define a new entity action
     * 
     * @param dto
     * @return
     */
    @Override
    public ActionStatus createAction(EntityCustomActionDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.create(dto, null, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Update existing entity action definition
     * 
     * @param dto
     * @return
     */
    @Override
    public ActionStatus updateAction(EntityCustomActionDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.update(dto, null, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Remove entity action definition given its code and entity it applies to
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return
     */
    @Override
    public ActionStatus removeAction(String actionCode, String appliesTo) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.removeEntityAction(actionCode, appliesTo, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Get entity action definition
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return
     */
    @Override
    public EntityCustomActionResponseDto findAction(String actionCode, String appliesTo) {

        EntityCustomActionResponseDto result = new EntityCustomActionResponseDto();

        try {
            result.setEntityAction(entityCustomActionApi.findEntityAction(actionCode, appliesTo, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Define new or update existing entity action definition
     * 
     * @param dto
     * @return
     */
    @Override
    public ActionStatus createOrUpdateAction(EntityCustomActionDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            entityCustomActionApi.createOrUpdate(dto, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus customizeEntity(EntityCustomizationDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.customizeEntity(dto, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;

    }

    /**
     * Get customizations made on a standard Meveo entity given its class
     * 
     * @param customizedEntityClass Standard Meveo entity class name
     */
    @Override
    public EntityCustomizationResponseDto findEntityCustomizations(String customizedEntityClass) {

        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityApi.findEntityCustomizations(customizedEntityClass, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

}