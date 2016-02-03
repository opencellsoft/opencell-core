package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CustomEntityApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.custom.CustomEntityInstanceRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.admin.User;
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class CustomEntityInstanceRsImpl extends BaseRs implements CustomEntityInstanceRs {

    @Inject
    private CustomEntityApi customEntityApi;

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
            customEntityApi.createEntityInstance(dto, currentUser);

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
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
            customEntityApi.updateEntityInstance(dto, currentUser);

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
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

            customEntityApi.removeEntityInstance(customEntityTemplateCode, code, currentUser);

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
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

            result.setCustomEntityInstance(customEntityApi.findEntityInstance(customEntityTemplateCode, code, currentUser));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
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
            customEntityApi.createOrUpdateEntityInstance(dto, currentUser);
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }
}