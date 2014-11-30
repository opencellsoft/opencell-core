package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.ws.rs.QueryParam;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.ProviderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.ProviderWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "ProviderWs", endpointInterface = "org.meveo.api.ws.ProviderWs")
public class ProviderWsImpl extends BaseWs implements ProviderWs {

	@Inject
	private ProviderApi providerApi;

	@Override
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

		return result;
	}

	@Override
	public GetProviderResponse find(
			@QueryParam("providerCode") String providerCode) {
		GetProviderResponse result = new GetProviderResponse();

		try {
			result.setProvider(providerApi.find(providerCode));
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

		return result;
	}

}
