package org.meveo.api.ws.impl;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.EdrDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.MediationWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "MediationWs", endpointInterface = "org.meveo.api.ws.MediationWs")
@Interceptors({ LoggingInterceptor.class })
public class MediationWsImpl extends BaseWs implements MediationWs {

	@Inject
	private MediationApi mediationApi;

	@Resource
	private WebServiceContext wsContext;

	@Override
	public ActionStatus create(EdrDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			MessageContext mc = wsContext.getMessageContext();
			HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);

			postData.setIpAddress(req.getRemoteAddr());
			mediationApi.create(postData, getCurrentUser());
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
