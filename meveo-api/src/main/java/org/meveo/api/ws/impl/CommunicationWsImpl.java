package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.communication.CommunicationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.CommunicationWs;

/**
 * @author Nasseh
 **/
@WebService(serviceName = "CommunicationWs", endpointInterface = "org.meveo.api.ws.CommunicationWs")
@Interceptors({ LoggingInterceptor.class })
public class CommunicationWsImpl extends BaseWs implements CommunicationWs {

	@Inject
	private CommunicationApi communicationApi;

	@Override
	public ActionStatus inboundCommunication(CommunicationRequestDto communicationRequest) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			communicationApi.inboundCommunication(communicationRequest);
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while updating notification ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while updating notification ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}



}
