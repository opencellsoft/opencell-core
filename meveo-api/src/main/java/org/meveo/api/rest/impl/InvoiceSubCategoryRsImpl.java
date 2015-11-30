package org.meveo.api.rest.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.InvoiceSubCategoryApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.InvoiceSubCategoryRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/invoiceSubCategory", tags = "invoiceSubCategory")
public class InvoiceSubCategoryRsImpl extends BaseRs implements InvoiceSubCategoryRs {

	@Inject
	private InvoiceSubCategoryApi invoiceSubCategoryApi;

	@Override
	@ApiOperation(value = "This function create a new invoice sub-category. Description per language can be defined", response = ActionStatus.class)
	public ActionStatus create(InvoiceSubCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceSubCategoryApi.create(postData, getCurrentUser());
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
	@ApiOperation(value = "This function update an invoice sub-category", response = ActionStatus.class)
	public ActionStatus update(InvoiceSubCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceSubCategoryApi.update(postData, getCurrentUser());
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
	@ApiOperation(value = "Find an invoice sub-category given a code", response = GetInvoiceSubCategoryResponse.class)
	public GetInvoiceSubCategoryResponse find(@ApiParam(value = "invoice sub category code") String code) {
		GetInvoiceSubCategoryResponse result = new GetInvoiceSubCategoryResponse();

		try {
			result.setInvoiceSubCategory(invoiceSubCategoryApi.find(code, getCurrentUser().getProvider()));
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
	public ActionStatus remove(@ApiParam(value = "invoice sub category code") String invoiceSubCategoryCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceSubCategoryApi.remove(invoiceSubCategoryCode, getCurrentUser().getProvider());
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
	@ApiOperation(value = "Create or update a invoice sub category if it doesn't exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(InvoiceSubCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			invoiceSubCategoryApi.createOrUpdate(postData, getCurrentUser());
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
