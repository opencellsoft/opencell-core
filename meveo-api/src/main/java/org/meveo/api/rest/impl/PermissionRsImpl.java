package org.meveo.api.rest.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.PermissionApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PermissionResponseDto;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.PermissionRs;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/permission", tags = "permission")
public class PermissionRsImpl extends BaseRs implements PermissionRs {

	@Inject
	private PermissionApi permissionApi;

	@Override
	@ApiOperation(value = "")
	public PermissionResponseDto list() {
		PermissionResponseDto result = new PermissionResponseDto();
		try {
			result.setPermissionsDto(permissionApi.list(getCurrentUser().getProvider()));
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
