package org.meveo.api.ws.catalog.impl;

import javax.inject.Inject;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.catalog.OfferTemplateWs;
import org.meveo.api.ws.impl.BaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "OfferTemplateWs", endpointInterface = "org.meveo.api.ws.catalog.OfferTemplateWs")
public class OfferTemplateWsImpl extends BaseWs implements OfferTemplateWs {

	@Inject
	private OfferTemplateApi offerTemplateApi;

	@Override
	public ActionStatus create(OfferTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerTemplateApi.create(postData, getCurrentUser());
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
	public ActionStatus update(OfferTemplateDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerTemplateApi.update(postData, getCurrentUser());
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
	public GetOfferTemplateResponse find(String offerTemplateCode) {
		GetOfferTemplateResponse result = new GetOfferTemplateResponse();

		try {
			result.setOfferTemplate(offerTemplateApi.find(offerTemplateCode,
					getCurrentUser().getProvider()));
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
	public ActionStatus remove(String offerTemplateCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerTemplateApi.remove(offerTemplateCode, getCurrentUser()
					.getProvider());
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
