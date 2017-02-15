package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.PermissionApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PermissionResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.PermissionRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class PermissionRsImpl extends BaseRs implements PermissionRs {

    @Inject
    private PermissionApi permissionApi;

    @Override
    public PermissionResponseDto list() {
        PermissionResponseDto result = new PermissionResponseDto();
        try {
            result.setPermissionsDto(permissionApi.list());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

}
