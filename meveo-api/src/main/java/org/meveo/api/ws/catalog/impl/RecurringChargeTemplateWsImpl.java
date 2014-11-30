package org.meveo.api.ws.catalog.impl;

import javax.inject.Inject;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.RecurringChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.catalog.RecurringChargeTemplateWs;
import org.meveo.api.ws.impl.BaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "RecurringChargeTemplateWs", endpointInterface = "org.meveo.api.ws.catalog.RecurringChargeTemplateWs")
public class RecurringChargeTemplateWsImpl extends BaseWs implements
		RecurringChargeTemplateWs {

	@Inject
	private RecurringChargeTemplateApi recurringChargeTemplateApi;

	@Override
	public ActionStatus create(RecurringChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			recurringChargeTemplateApi.create(postData, getCurrentUser());
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
	public GetRecurringChargeTemplateResponse find(
			String recurringChargeTemplateCode) {
		GetRecurringChargeTemplateResponse result = new GetRecurringChargeTemplateResponse();

		try {
			result.setRecurringChargeTemplate(recurringChargeTemplateApi
					.find(recurringChargeTemplateCode, getCurrentUser()
							.getProvider()));
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
	public ActionStatus update(RecurringChargeTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			recurringChargeTemplateApi.update(postData, getCurrentUser());
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
	public ActionStatus remove(String recurringChargeTemplateCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			recurringChargeTemplateApi.remove(recurringChargeTemplateCode,
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
