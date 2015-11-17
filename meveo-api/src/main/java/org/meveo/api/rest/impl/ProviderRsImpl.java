package org.meveo.api.rest.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.ProviderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.ProviderRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/provider")
public class ProviderRsImpl extends BaseRs implements ProviderRs {

	@Inject
	private ProviderApi providerApi;

	@Override
	@ApiOperation(value = "Create a provider")
	@ApiResponses(value = { @ApiResponse(code = 302, message = "Provider already exists"),
			@ApiResponse(code = 404, message = "Missing entity parameter/s"),
			@ApiResponse(code = 412, message = "Missing or incorrect parameters"),
			@ApiResponse(code = 400, message = "Failed to associate a custom field instance to an entity"),
			@ApiResponse(code = 500, message = "Generic exception") })
	public Response create(ProviderDto postData) {
		Response.ResponseBuilder responseBuilder = null;
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			providerApi.create(postData, getCurrentUser());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@Override
	@ApiOperation(value = "Find provider by providerCode")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Provider not found"),
			@ApiResponse(code = 412, message = "Missing or incorrect parameters"),
			@ApiResponse(code = 500, message = "Generic exception") })
	public Response find(@QueryParam("providerCode") String providerCode) {
		Response.ResponseBuilder responseBuilder = null;
		GetProviderResponse result = new GetProviderResponse();

		try {
			result.setProvider(providerApi.find(providerCode));
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@ApiOperation(value = "Update a provider given a code")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Provider does not exists or missing entity parameter/s"),
			@ApiResponse(code = 412, message = "Missing or incorrect parameters"),
			@ApiResponse(code = 400, message = "Failed to associate a custom field instance to an entity"),
			@ApiResponse(code = 500, message = "Generic exception") })
	public Response update(ProviderDto postData) {
		Response.ResponseBuilder responseBuilder = null;
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			providerApi.update(postData, getCurrentUser());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@Override
	@ApiOperation(value = "Returns list of trading countries, currencies and languages")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Generic exception") })
	public Response findTradingConfiguration() {
		Response.ResponseBuilder responseBuilder = null;
		GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

		try {
			result = providerApi.getTradingConfiguration(getCurrentUser());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@Override
	@ApiOperation(value = "Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Generic exception") })
	public Response findInvoicingConfiguration() {
		Response.ResponseBuilder responseBuilder = null;
		GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

		try {
			result = providerApi.getInvoicingConfiguration(getCurrentUser());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@Override
	@ApiOperation(value = "Returns list of customer brands, categories and titles")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Generic exception") })
	public Response findCustomerConfiguration() {
		Response.ResponseBuilder responseBuilder = null;
		GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

		try {
			result = providerApi.getCustomerConfigurationResponse(getCurrentUser());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@Override
	@ApiOperation(value = "Returns list of payment method and credit categories")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Generic exception") })
	public Response findCustomerAccountConfiguration() {
		Response.ResponseBuilder responseBuilder = null;
		GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

		try {
			result = providerApi.getCustomerAccountConfigurationResponseDto(getCurrentUser().getProvider());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

	@Override
	@ApiOperation(value = "Create or update a provider if it doesn't exists")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Missing entity parameter/s"),
			@ApiResponse(code = 412, message = "Missing or incorrect parameters"),
			@ApiResponse(code = 400, message = "Failed to associate a custom field instance to an entity"),
			@ApiResponse(code = 500, message = "Generic exception") })
	public Response createOrUpdate(ProviderDto postData) {
		Response.ResponseBuilder responseBuilder = null;
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			providerApi.createOrUpdate(postData, getCurrentUser());
			responseBuilder = Response.ok();
			responseBuilder.entity(result);
		} catch (MeveoApiException e) {
			responseBuilder = createResponseFromMeveoApiException(e, result);
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result);
		}

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}
}
