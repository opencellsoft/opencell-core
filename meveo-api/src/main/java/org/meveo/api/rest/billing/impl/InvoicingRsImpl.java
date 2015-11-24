package org.meveo.api.rest.billing.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.billing.InvoicingApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.billing.InvoicingRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/billing/invoicing", tags = "invoicing")
public class InvoicingRsImpl extends BaseRs implements InvoicingRs {

	@Inject
	private InvoicingApi invoicingApi;

	@Override
	@ApiOperation(value = "Creates billing run", response = ActionStatus.class)
	public ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {

			invoicingApi.createBillingRun(createBillingRunDto, getCurrentUser());

		} catch (MissingParameterException mpe) {
			result.setErrorCode(mpe.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.setErrorCode(ednep.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.setErrorCode(bae.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(bae.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Creates billing run", response = GetBillingRunInfoResponseDto.class)
	public GetBillingRunInfoResponseDto getBillingRunInfo(@ApiParam(value = "billing run id") Long billingRunId) {
		GetBillingRunInfoResponseDto result = new GetBillingRunInfoResponseDto();
		log.info("getBillingRunInfo request={}", billingRunId);
		try {

			result.setBillingRunDto(invoicingApi.getBillingRunInfo(billingRunId, getCurrentUser()));

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getBillingRunInfo Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Returns the list of billable billing accounts of a billing run", response = GetBillingAccountListInRunResponseDto.class, responseContainer = "List")
	public GetBillingAccountListInRunResponseDto getBillingAccountListInRun(
			@ApiParam(value = "billing run id") Long billingRunId) {
		GetBillingAccountListInRunResponseDto result = new GetBillingAccountListInRunResponseDto();
		log.info("getBillingAccountListInRun request={}", billingRunId);
		try {

			result.setBillingAccountsDto(invoicingApi.getBillingAccountListInRun(billingRunId, getCurrentUser()));

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getBillingAccountListInRun Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Returns the PreInvoicingReport entity", response = GetPreInvoicingReportsResponseDto.class)
	public GetPreInvoicingReportsResponseDto getPreInvoicingReport(@ApiParam(value = "billing run id") Long billingRunId) {
		GetPreInvoicingReportsResponseDto result = new GetPreInvoicingReportsResponseDto();
		log.info("getPreInvoicingReport request={}", billingRunId);
		try {

			result.setPreInvoicingReportsDTO(invoicingApi.getPreInvoicingReport(billingRunId, getCurrentUser()));

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());
		} catch (BusinessException be) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(be.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getPreInvoicingReport Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Returns the PostInvoicingReport entity", response = GetPostInvoicingReportsResponseDto.class)
	public GetPostInvoicingReportsResponseDto getPostInvoicingReport(
			@ApiParam(value = "billing run id") Long billingRunId) {
		GetPostInvoicingReportsResponseDto result = new GetPostInvoicingReportsResponseDto();
		log.info("getPreInvoicingReport request={}", billingRunId);
		try {

			result.setPostInvoicingReportsDTO(invoicingApi.getPostInvoicingReport(billingRunId, getCurrentUser()));

		} catch (MissingParameterException mpe) {
			result.getActionStatus().setErrorCode(mpe.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.getActionStatus().setErrorCode(ednep.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.getActionStatus().setErrorCode(bae.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(bae.getMessage());
		} catch (BusinessException be) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(be.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		log.info("getPostInvoicingReport Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Validates a billing run. Sets the next invoice date of a billing account to the next calendar date.")
	public ActionStatus validateBillingRun(@ApiParam(value = "billing run id") Long billingRunId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		log.info("validateBillingRun request={}", billingRunId);
		try {

			invoicingApi.validateBillingRun(billingRunId, getCurrentUser());

		} catch (MissingParameterException mpe) {
			result.setErrorCode(mpe.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.setErrorCode(ednep.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.setErrorCode(bae.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(bae.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		log.info("validateBillingRun Response={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Cancels a billing run", response = ActionStatus.class, notes = "Sets RatedTransaction.status associated to billingRun to OPEN. Remove aggregates and invoice associated to the billingRun. Set billingAccount.billingRun to null.")
	public ActionStatus cancelBillingRun(@ApiParam(value = "billing run id") Long billingRunId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		log.info("cancelBillingRun request={}", billingRunId);
		try {

			invoicingApi.cancelBillingRun(billingRunId, getCurrentUser());

		} catch (MissingParameterException mpe) {
			result.setErrorCode(mpe.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(mpe.getMessage());
		} catch (EntityDoesNotExistsException ednep) {
			result.setErrorCode(ednep.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(ednep.getMessage());
		} catch (BusinessApiException bae) {
			result.setErrorCode(bae.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(bae.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		log.info("cancelBillingRun Response={}", result);
		return result;
	}

}
