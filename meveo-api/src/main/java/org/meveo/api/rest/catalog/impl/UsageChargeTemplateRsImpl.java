package org.meveo.api.rest.catalog.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Set;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.UsageChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.catalog.UsageChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/catalog/usageChargeTemplate", tags = "usageChargeTemplate")
public class UsageChargeTemplateRsImpl extends BaseRs implements UsageChargeTemplateRs {

	@Inject
	private UsageChargeTemplateApi usageChargeTemplateApi;

	@Override
	@ApiOperation(value = "Function to create a usage charge", response = ActionStatus.class)
	public ActionStatus create(UsageChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			usageChargeTemplateApi.create(postData, getCurrentUser());
		} catch (EJBTransactionRolledbackException e) {
			Throwable t = e.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) (t);
				Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
				String errMsg = "";
				for (ConstraintViolation<?> cv : violations) {
					errMsg += cv.getPropertyPath() + " " + cv.getMessage() + ",";
				}
				errMsg = errMsg.substring(0, errMsg.length() - 1);
				result.setErrorCode(MeveoApiErrorCode.INVALID_PARAMETER);
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(errMsg);
			} else {
				result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(e.getMessage());
			}
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
	@ApiOperation(value = "Function to update a usage charge given a code", response = ActionStatus.class)
	public ActionStatus update(UsageChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			usageChargeTemplateApi.update(postData, getCurrentUser());
		} catch (EJBTransactionRolledbackException e) {
			Throwable t = e.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) (t);
				Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
				String errMsg = "";
				for (ConstraintViolation<?> cv : violations) {
					errMsg += cv.getPropertyPath() + " " + cv.getMessage() + ",";
				}
				errMsg = errMsg.substring(0, errMsg.length() - 1);
				result.setErrorCode(MeveoApiErrorCode.INVALID_PARAMETER);
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(errMsg);
			} else {
				result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(e.getMessage());
			}
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
	@ApiOperation(value = "Function to find a usage charge given a code", response = GetUsageChargeTemplateResponseDto.class)
	public GetUsageChargeTemplateResponseDto find(
			@ApiParam(value = "usage charge template code") String usageChargeTemplateCode) {
		GetUsageChargeTemplateResponseDto result = new GetUsageChargeTemplateResponseDto();

		try {
			result.setUsageChargeTemplate(usageChargeTemplateApi.find(usageChargeTemplateCode, getCurrentUser()
					.getProvider()));
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
	@ApiOperation(value = "Function to remove a usage charge given a code", response = ActionStatus.class)
	public ActionStatus remove(@ApiParam(value = "usage charge template code") String usageChargeTemplateCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			usageChargeTemplateApi.remove(usageChargeTemplateCode, getCurrentUser().getProvider());
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
	@ApiOperation(value = "Creates a one shot charge template or update if already exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(UsageChargeTemplateDto postData) {

		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			usageChargeTemplateApi.createOrUpdate(postData, getCurrentUser());
		} catch (EJBTransactionRolledbackException e) {
			Throwable t = e.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) (t);
				Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
				String errMsg = "";
				for (ConstraintViolation<?> cv : violations) {
					errMsg += cv.getPropertyPath() + " " + cv.getMessage() + ",";
				}
				errMsg = errMsg.substring(0, errMsg.length() - 1);
				result.setErrorCode(MeveoApiErrorCode.INVALID_PARAMETER);
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(errMsg);
			} else {
				result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(e.getMessage());
			}
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