package org.meveo.api.rest.custom.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CustomEntityApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.GetCustomEntityInstanceResponseDto;
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
@Api(value = "/customEntityInstance", tags = "customEntityInstance")
public class CustomEntityInstanceRsImpl extends BaseRs implements CustomEntityInstanceRs {

	@Inject
	private CustomEntityApi customEntityApi;

	@Override
	@ApiOperation(value = "")
	public ActionStatus create(String customEntityTemplateCode, CustomEntityInstanceDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			// Check user has <cetCode>/modify permission
			User currentUser = getCurrentUser();
			if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode),
					"modify")) {
				throw new LoginException("User does not have permission 'modify' on resource '"
						+ CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
			}

			postData.setCetCode(customEntityTemplateCode);
			customEntityApi.createEntityInstance(postData, currentUser);

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
	@ApiOperation(value = "")
	public ActionStatus update(String customEntityTemplateCode, CustomEntityInstanceDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			// Check user has <cetCode>/modify permission
			User currentUser = getCurrentUser();
			if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode),
					"modify")) {
				throw new LoginException("User does not have permission 'modify' on resource '"
						+ CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
			}

			postData.setCetCode(customEntityTemplateCode);
			customEntityApi.updateEntityInstance(postData, currentUser);

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
	@ApiOperation(value = "")
	public ActionStatus remove(String customEntityTemplateCode, String customEntityInstanceCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			// Check user has <cetCode>/modify permission
			User currentUser = getCurrentUser();
			if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode),
					"modify")) {
				throw new LoginException("User does not have permission 'modify' on resource '"
						+ CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
			}

			customEntityApi.removeEntityInstance(customEntityTemplateCode, customEntityInstanceCode,
					currentUser.getProvider());

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
	@ApiOperation(value = "")
	public GetCustomEntityInstanceResponseDto find(String customEntityTemplateCode, String customEntityInstanceCode) {
		GetCustomEntityInstanceResponseDto result = new GetCustomEntityInstanceResponseDto();

		try {
			// Check user has <cetCode>/modify permission
			User currentUser = getCurrentUser();
			if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode),
					"read")) {
				throw new LoginException("User does not have permission 'modify' on resource '"
						+ CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
			}

			result.setCustomEntityInstance(customEntityApi.findEntityInstance(customEntityTemplateCode,
					customEntityInstanceCode, currentUser.getProvider()));

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
	@ApiOperation(value = "")
	public ActionStatus createOrUpdate(String customEntityTemplateCode, CustomEntityInstanceDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			// Check user has <cetCode>/modify permission
			User currentUser = getCurrentUser();
			if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode),
					"modify")) {
				throw new LoginException("User does not have permission 'modify' on resource '"
						+ CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
			}

			postData.setCetCode(customEntityTemplateCode);
			customEntityApi.createOrUpdateEntityInstance(postData, currentUser);
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
