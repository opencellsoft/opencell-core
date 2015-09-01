package org.meveo.api.rest.communication.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.communication.CommunicationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.communication.CommunicationRs;
import org.meveo.api.rest.impl.BaseRs;


@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class CommunicationRsImpl extends BaseRs implements CommunicationRs {
	
	@Inject
	CommunicationApi communicationApi;

	@Override
	public ActionStatus inboundCommunication(CommunicationRequestDto communicationRequestDto)  {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			communicationApi.inboundCommunication(communicationRequestDto);
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
