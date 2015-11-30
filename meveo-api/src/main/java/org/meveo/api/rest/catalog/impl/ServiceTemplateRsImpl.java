package org.meveo.api.rest.catalog.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.catalog.ServiceTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/catalog/serviceTemplate", tags = "serviceTemplate")
public class ServiceTemplateRsImpl extends BaseRs implements ServiceTemplateRs {

	@Inject
	private ServiceTemplateApi serviceTemplateApi;

	@Override
	@ApiOperation(value = "Function to create a service template", response = ActionStatus.class)
	public ActionStatus create(ServiceTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			serviceTemplateApi.create(postData, getCurrentUser());
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
	@ApiOperation(value = "Function to update a service template given a code", response = ActionStatus.class)
	public ActionStatus update(ServiceTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			serviceTemplateApi.update(postData, getCurrentUser());
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
	@ApiOperation(value = "Function to find a service template given a code", response = GetServiceTemplateResponseDto.class)
	public GetServiceTemplateResponseDto find(@ApiParam("service template code") String serviceTemplateCode) {
		GetServiceTemplateResponseDto result = new GetServiceTemplateResponseDto();

		try {
			result.setServiceTemplate(serviceTemplateApi.find(serviceTemplateCode, getCurrentUser().getProvider()));
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
	@ApiOperation(value = "Function to remove a service template given a code", response = ActionStatus.class)
	public ActionStatus remove(@ApiParam("service template code") String serviceTemplateCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			serviceTemplateApi.remove(serviceTemplateCode, getCurrentUser().getProvider());
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
	@ApiOperation(value = "Creates a service template or update if already exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(ServiceTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			serviceTemplateApi.createOrUpdate(postData, getCurrentUser());
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
