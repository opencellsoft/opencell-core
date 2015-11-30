package org.meveo.api.rest.catalog.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.OneShotChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.catalog.OneShotChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/catalog/oneShotChargeTemplate", tags = "oneShotChargeTemplate")
public class OneShotChargeTemplateRsImpl extends BaseRs implements OneShotChargeTemplateRs {

	@Inject
	private OneShotChargeTemplateApi oneShotChargeTemplateApi;

	@POST
	@ApiOperation(value = "Function to create a one shot charge", response = ActionStatus.class)
	public ActionStatus create(OneShotChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			oneShotChargeTemplateApi.create(postData, getCurrentUser());
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
	@ApiOperation(value = "Function to update a one shot charge given a code", response = ActionStatus.class)
	public ActionStatus update(OneShotChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			oneShotChargeTemplateApi.update(postData, getCurrentUser());
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
	@ApiOperation(value = "Function to list a one shot charge given a country, seller code and date", response = OneShotChargeTemplateWithPriceListDto.class, responseContainer = "List")
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(
			@ApiParam(value = "language code") @QueryParam("languageCode") String languageCode,
			@ApiParam(value = "country code") @QueryParam("countryCode") String countryCode,
			@ApiParam(value = "currency code") @QueryParam("currencyCode") String currencyCode,
			@ApiParam(value = "seller code") @QueryParam("sellerCode") String sellerCode,
			@ApiParam(value = "date") @QueryParam("date") String date) throws MeveoApiException {

		Date subscriptionDate = DateUtils.parseDateWithPattern(date, "yyyy-MM-dd");

		return oneShotChargeTemplateApi.listWithPrice(languageCode, countryCode, currencyCode, sellerCode,
				subscriptionDate, getCurrentUser());
	}

	@Override
	@ApiOperation(value = "Function to find a one shot charge given a code", response = GetOneShotChargeTemplateResponseDto.class)
	public GetOneShotChargeTemplateResponseDto find(
			@ApiParam(value = "one shot charge template code") String oneShotChargeTemplateCode) {
		GetOneShotChargeTemplateResponseDto result = new GetOneShotChargeTemplateResponseDto();

		try {
			result.setOneShotChargeTemplate(oneShotChargeTemplateApi.find(oneShotChargeTemplateCode, getCurrentUser()
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
	@ApiOperation(value = "Function to remove a one shot charge given a code", response = ActionStatus.class)
	public ActionStatus remove(@ApiParam(value = "one shot charge template code") String oneShotChargeTemplateCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			oneShotChargeTemplateApi.remove(oneShotChargeTemplateCode, getCurrentUser().getProvider());
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
	@ApiOperation(value = "Creates a recurring charge template or update if already exists", response = ActionStatus.class)
	public ActionStatus createOrUpdate(OneShotChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			oneShotChargeTemplateApi.createOrUpdate(postData, getCurrentUser());
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
