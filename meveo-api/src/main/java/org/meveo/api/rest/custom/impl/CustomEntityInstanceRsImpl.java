package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.CustomEntityInstanceApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.CustomEntityInstanceRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.admin.User;
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomEntityInstanceRsImpl extends BaseRs implements CustomEntityInstanceRs {

    @Inject
    private CustomEntityInstanceApi customEntityInstanceApi;

    @Override
    public ActionStatus create(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            // Check user has <cetCode>/modify permission
            User currentUser = getCurrentUser();
            if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode), "modify")) {
                throw new LoginException("User does not have permission 'modify' on resource '" + CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
            }

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.create(dto, currentUser);

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
    public ActionStatus update(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            // Check user has <cetCode>/modify permission
            User currentUser = getCurrentUser();
            if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode), "modify")) {
                throw new LoginException("User does not have permission 'modify' on resource '" + CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
            }

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.update(dto, currentUser);

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
    public ActionStatus remove(String customEntityTemplateCode, String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            // Check user has <cetCode>/modify permission
            User currentUser = getCurrentUser();
            if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode), "modify")) {
                throw new LoginException("User does not have permission 'modify' on resource '" + CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
            }

            customEntityInstanceApi.remove(customEntityTemplateCode, code, currentUser);

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
    public CustomEntityInstanceResponseDto find(String customEntityTemplateCode, String code) {
        CustomEntityInstanceResponseDto result = new CustomEntityInstanceResponseDto();

        try {
            // Check user has <cetCode>/modify permission
            User currentUser = getCurrentUser();
            if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode), "read")) {
                throw new LoginException("User does not have permission 'modify' on resource '" + CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
            }

            result.setCustomEntityInstance(customEntityInstanceApi.find(customEntityTemplateCode, code, currentUser));

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
    public ActionStatus createOrUpdate(String customEntityTemplateCode, CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            // Check user has <cetCode>/modify permission
            User currentUser = getCurrentUser();
            if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode), "modify")) {
                throw new LoginException("User does not have permission 'modify' on resource '" + CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
            }

            dto.setCetCode(customEntityTemplateCode);
            customEntityInstanceApi.createOrUpdate(dto, currentUser);

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
}