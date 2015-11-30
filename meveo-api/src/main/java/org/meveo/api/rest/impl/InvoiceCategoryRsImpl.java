package org.meveo.api.rest.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.InvoiceCategoryApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.InvoiceCategoryRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/invoiceCategory", tags = "invoiceCategory")
public class InvoiceCategoryRsImpl extends BaseRs implements InvoiceCategoryRs {

	@Inject
	private InvoiceCategoryApi invoiceCategoryApi;

	@Override
	@ApiOperation(value = "This function create a new invoice category. Description per language can be defined", response = ActionStatus.class)
	public ActionStatus create(InvoiceCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceCategoryApi.create(postData, getCurrentUser());
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
	@ApiOperation(value = "This function update an invoice category", response = ActionStatus.class)
	public ActionStatus update(InvoiceCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceCategoryApi.update(postData, getCurrentUser());
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
	@ApiOperation(value = "Find an invoice category given a code", response = GetInvoiceCategoryResponse.class)
	public GetInvoiceCategoryResponse find(@ApiParam(value = "invoice category code") String invoiceCategoryCode) {
		GetInvoiceCategoryResponse result = new GetInvoiceCategoryResponse();

		try {
			result.setInvoiceCategory(invoiceCategoryApi.find(invoiceCategoryCode, getCurrentUser().getProvider()));
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
	@ApiOperation(value = "Remove an invoice sub-category given a code", response = ActionStatus.class)
	public ActionStatus remove(@ApiParam(value = "invoice category code") String invoiceCategoryCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceCategoryApi.remove(invoiceCategoryCode, getCurrentUser().getProvider());
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
	@ApiOperation(value = "Create or update a invoice category if it doesn't exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(InvoiceCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceCategoryApi.createOrUpdate(postData, getCurrentUser());
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
