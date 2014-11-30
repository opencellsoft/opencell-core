package org.meveo.api.ws.catalog.impl;

import javax.inject.Inject;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.PricePlanApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.catalog.PricePlanWs;
import org.meveo.api.ws.impl.BaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "PricePlanWs", endpointInterface = "org.meveo.api.ws.catalog.PricePlanWs")
public class PricePlanWsImpl extends BaseWs implements PricePlanWs {

	@Inject
	private PricePlanApi pricePlanApi;

	@Override
	public ActionStatus create(PricePlanDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			result.setMessage(String.valueOf(pricePlanApi.create(postData,
					getCurrentUser())));
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
	public ActionStatus update(PricePlanDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			pricePlanApi.update(postData, getCurrentUser());
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
	public GetPricePlanResponse find(Long id) {
		GetPricePlanResponse result = new GetPricePlanResponse();

		try {
			result.setPricePlan(pricePlanApi.find(id));
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
	public ActionStatus remove(Long id) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			pricePlanApi.remove(id);
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
