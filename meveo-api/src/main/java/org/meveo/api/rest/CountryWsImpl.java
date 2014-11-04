package org.meveo.api.rest;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.meveo.api.CountryServiceApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.exception.CountryDoesNotExistsException;
import org.meveo.api.exception.CurrencyDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.exception.TradingCountryAlreadyExistsException;
import org.meveo.api.exception.TradingCountryDoesNotExistsException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.commons.utils.ParamBean;

/**
 * Web service for managing {@link org.meveo.model.billing.Country} and
 * {@link org.meveo.model.billing.TradingCountry}.
 * 
 * @author Edward P. Legaspi
 **/
@Interceptors({ LoggingInterceptor.class })
// @WSSecured
public class CountryWsImpl extends BaseWs implements CountryWs {

	@Inject
	private ParamBean paramBean;

	@Inject
	private CountryServiceApi countryServiceApi;

	/***
	 * Creates an instance of @see TradingCountry base on @see Country.
	 * 
	 * @param countryDto
	 * @return @see ActionStatus
	 */
	@Override
	public ActionStatus create(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryDto.setCurrentUser(currentUser);
			countryServiceApi.create(countryDto);
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (CurrencyDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.CURRENCY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (TradingCountryAlreadyExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.TRADING_COUNTRY_ALREADY_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public GetCountryResponse find(@QueryParam("countryCode") String countryCode) {
		GetCountryResponse result = new GetCountryResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCountry(countryServiceApi.find(countryCode));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ActionStatus remove(@PathParam("countryCode") String countryCode,
			@PathParam("currencyCode") String currencyCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		Long providerId = Long.valueOf(paramBean.getProperty(
				"asp.api.providerId", "1"));

		try {
			countryServiceApi.remove(countryCode, currencyCode, providerId);
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (TradingCountryDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.TRADING_COUNTRY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (CurrencyDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.CURRENCY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ActionStatus update(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryDto.setCurrentUser(currentUser);
			countryServiceApi.update(countryDto);
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (CountryDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.COUNTRY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (CurrencyDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.CURRENCY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (TradingCountryDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.TRADING_COUNTRY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
