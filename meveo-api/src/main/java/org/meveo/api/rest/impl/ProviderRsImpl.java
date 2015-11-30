package org.meveo.api.rest.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

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
@Api(value = "/provider", tags = "provider")
public class ProviderRsImpl extends BaseRs implements ProviderRs {

	@Inject
	private ProviderApi providerApi;

	@Override
	@ApiOperation(value = "Create a provider", response = ActionStatus.class)
	public ActionStatus create(ProviderDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			providerApi.create(postData, getCurrentUser());
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
	@ApiOperation(value = "Find provider by providerCode", response = GetProviderResponse.class)
	public GetProviderResponse find(@ApiParam(value = "provider code") @QueryParam("providerCode") String providerCode) {
		GetProviderResponse result = new GetProviderResponse();

		try {
			result.setProvider(providerApi.find(providerCode));
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
	@ApiOperation(value = "Update a provider given a code", response = ActionStatus.class)
	public ActionStatus update(ProviderDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			providerApi.update(postData, getCurrentUser());
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
	@ApiOperation(value = "Returns list of trading countries, currencies and languages", response = GetTradingConfigurationResponseDto.class)
	public GetTradingConfigurationResponseDto findTradingConfiguration() {
		GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

		try {
			result = providerApi.getTradingConfiguration(getCurrentUser());
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
	@ApiOperation(value = "Returns list of invoicing configuration (calendars, taxes, invoice categories, invoice sub categories, billing cycles and termination reasons", response = GetInvoicingConfigurationResponseDto.class)
	public GetInvoicingConfigurationResponseDto findInvoicingConfiguration() {
		GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

		try {
			result = providerApi.getInvoicingConfiguration(getCurrentUser());
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
	@ApiOperation(value = "Returns list of customer brands, categories and titles", response = GetCustomerConfigurationResponseDto.class)
	public GetCustomerConfigurationResponseDto findCustomerConfiguration() {
		GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

		try {
			result = providerApi.getCustomerConfigurationResponse(getCurrentUser());
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
	@ApiOperation(value = "Returns list of payment method and credit categories", response = GetCustomerAccountConfigurationResponseDto.class)
	public GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration() {
		GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

		try {
			result = providerApi.getCustomerAccountConfigurationResponseDto(getCurrentUser().getProvider());
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
	@ApiOperation(value = "Create or update a provider if it doesn't exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(ProviderDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			providerApi.createOrUpdate(postData, getCurrentUser());
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
