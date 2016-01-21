package org.meveo.api.rest.custom.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CustomEntityApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityActionScriptDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplatesResponseDto;
import org.meveo.api.dto.response.EntityActionScriptResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.custom.EntityCustomizationRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/entityCustomization", tags = "entityCustomization")
public class EntityCustomizationRsImpl extends BaseRs implements EntityCustomizationRs {

    @Inject
    private CustomEntityApi customEntityApi;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Override
    @ApiOperation(value = "Define a new custom entity template including fields and applicable actions", response = ActionStatus.class)
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createEntityTemplate(dto, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Update custom entity template definition", response = ActionStatus.class)
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.updateEntityTemplate(dto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Remove custom entity template definition given its code", response = ActionStatus.class)
    public ActionStatus removeEntityTemplate(@ApiParam(value = "Custom entity template code") String customEntityTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.removeEntityTemplate(customEntityTemplateCode, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Get custom entity template definition including its fields and applicable actions", response = CustomEntityTemplateResponseDto.class)
    public CustomEntityTemplateResponseDto findEntityTemplate(@ApiParam(value = "Custom entity template code") String customEntityTemplateCode) {
        CustomEntityTemplateResponseDto result = new CustomEntityTemplateResponseDto();

        try {
            result.setCustomEntityTemplate(customEntityApi.findEntityTemplate(customEntityTemplateCode, getCurrentUser()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Define new or update existing custom entity template definition", response = ActionStatus.class)
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createOrUpdateEntityTemplate(dto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "List custom entity template definitions", response = CustomEntityTemplatesResponseDto.class)
    public CustomEntityTemplatesResponseDto listEntityTemplates(@ApiParam(value = "An optional and partial custom entity template code ") String code) {

        CustomEntityTemplatesResponseDto result = new CustomEntityTemplatesResponseDto();

        try {
            result.setCustomEntityTemplates(customEntityApi.listCustomEntityTemplates(code, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;

    }

    @Override
    @ApiOperation(value = "Customize a standard Meveo entity definition by adding fields and/or custom actions", response = ActionStatus.class)
    public ActionStatus customizeEntity(EntityCustomizationDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.customizeEntity(dto, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;

    }

    @Override
    @ApiOperation(value = "Get customizations made on a standard Meveo entity given its class")
    public EntityCustomizationResponseDto findEntityCustomizations(@ApiParam(value = "Standard Meveo entity class name") String customizedEntityClass) {

        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityApi.findEntityCustomizations(customizedEntityClass, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Define a new custom field", response = ActionStatus.class)
    public ActionStatus createField(CustomFieldTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.create(dto, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Update existing custom field definition", response = ActionStatus.class)
    public ActionStatus updateField(CustomFieldTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.update(dto, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Remove custom field definition given its code and entity it applies to", response = ActionStatus.class)
    public ActionStatus removeField(@ApiParam(value = "Custom field template code") String customFieldTemplateCode,
            @ApiParam(value = "Entity custom field applies to") String appliesTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.remove(customFieldTemplateCode, appliesTo, getCurrentUser().getProvider());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Get custom field definition", response = GetCustomFieldTemplateReponseDto.class)
    public GetCustomFieldTemplateReponseDto findField(@ApiParam(value = "Custom field template code") String customFieldTemplateCode,
            @ApiParam(value = "Entity custom field applies to") String appliesTo) {
        GetCustomFieldTemplateReponseDto result = new GetCustomFieldTemplateReponseDto();

        try {
            result.setCustomFieldTemplate(customFieldTemplateApi.find(customFieldTemplateCode, appliesTo, getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Define new or update existing custom field definition", response = ActionStatus.class)
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.createOrUpdate(dto, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Define a new entity action", response = ActionStatus.class)
    public ActionStatus createAction(EntityActionScriptDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.create(dto, null, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Update existing entity action definition", response = ActionStatus.class)
    public ActionStatus updateAction(EntityActionScriptDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.update(dto, null, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Remove entity action definition given its code and entity it applies to", response = ActionStatus.class)
    public ActionStatus removeAction(@ApiParam(value = "Entity action code") String actionCode, @ApiParam(value = "Entity that action applies to") String appliesTo) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.removeEntityAction(actionCode, appliesTo, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Get entity action definition", response = GetCustomFieldTemplateReponseDto.class)
    public EntityActionScriptResponseDto findAction(@ApiParam(value = "Entity action code") String actionCode, @ApiParam(value = "Entity that action applies to") String appliesTo) {

        EntityActionScriptResponseDto result = new EntityActionScriptResponseDto();

        try {
            result.setEntityAction(scriptInstanceApi.findEntityAction(actionCode, appliesTo, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    @ApiOperation(value = "Define new or update existing entity action definition", response = ActionStatus.class)
    public ActionStatus createOrUpdateAction(EntityActionScriptDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.createOrUpdate(dto, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }
}