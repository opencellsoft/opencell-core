package org.meveo.api.ws.catalog.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.OneShotChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.catalog.OneShotChargeTemplateWs;
import org.meveo.api.ws.impl.BaseWs;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "OneShotChargeTemplateWs", endpointInterface = "org.meveo.api.ws.catalog.OneShotChargeTemplateWs")
public class OneShotChargeTemplateWsImpl extends BaseWs implements
		OneShotChargeTemplateWs {

	@Inject
	private OneShotChargeTemplateApi oneShotChargeTemplateApi;
	
	@Inject
	private Logger log;

	@POST
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

		return result;
	}

	@Override
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

		return result;
	}

	@Override
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(
			@QueryParam("languageCode") String languageCode,
			@QueryParam("countryCode") String countryCode,
			@QueryParam("currencyCode") String currencyCode,
			@QueryParam("sellerCode") String sellerCode,
			@QueryParam("date") String date) {

		Date subscriptionDate = DateUtils.parseDateWithPattern(date,
				"yyyy-MM-dd");

		try {
			return oneShotChargeTemplateApi.listWithPrice(languageCode,
					countryCode, currencyCode, sellerCode, subscriptionDate,
					getCurrentUser());
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public GetOneShotChargeTemplateResponse find(
			String oneShotChargeTemplateCode) {
		GetOneShotChargeTemplateResponse result = new GetOneShotChargeTemplateResponse();

		try {
			result.setOneShotChargeTemplate(oneShotChargeTemplateApi.find(
					oneShotChargeTemplateCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(
					MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ActionStatus remove(String oneShotChargeTemplateCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			oneShotChargeTemplateApi.remove(oneShotChargeTemplateCode,
					getCurrentUser().getProvider());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
