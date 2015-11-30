package org.meveo.api.rest.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.meveo.api.CountryApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.CountryRs;

/**
 * @see {@link org.meveo.api.rest.CountryWs}.
 * 
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/country", tags = "tradingCountry")
public class CountryRsImpl extends BaseRs implements CountryRs {

	@Inject
	private CountryApi countryApi;

	/***
	 * Creates an instance of @see TradingCountry base on @see Country.
	 * 
	 * @param countryDto
	 * @return @see ActionStatus
	 */
	@Override
	@ApiOperation(value = "This function allows to creates a tradingCountry base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created", response = ActionStatus.class)
	public ActionStatus create(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryApi.create(countryDto, getCurrentUser());
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
	@ApiOperation(value = "Function use to modify a country. Same input parameter as create. The country and tradingCountry are created if they don't exists. The operation fails if the tradingCountry is null.", response = GetCountryResponse.class)
	public GetCountryResponse find(@ApiParam(value = "country code") @QueryParam("countryCode") String countryCode) {
		GetCountryResponse result = new GetCountryResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCountry(countryApi.find(countryCode, getCurrentUser().getProvider()));
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
	@ApiOperation(value = "This function does not delete a country but the tradingCountry associated to it.", response = ActionStatus.class)
	public ActionStatus remove(@ApiParam(value = "country code") @PathParam("countryCode") String countryCode,
			@ApiParam(value = "currency code") @PathParam("currencyCode") String currencyCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryApi.remove(countryCode, currencyCode, getCurrentUser().getProvider());
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
	@ApiOperation(value = "Updates trading country. Creates trading currency if it doesn't exists", response = ActionStatus.class)
	public ActionStatus update(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryApi.update(countryDto, getCurrentUser());
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
	@ApiOperation(value = "Create or update a country if it doesn't exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryApi.createOrUpdate(countryDto, getCurrentUser());
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
