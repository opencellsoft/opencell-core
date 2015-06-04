package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CommDto;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.CommunicationWs;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.communication.MeveoInstanceStatusEnum;
import org.meveo.service.communication.impl.MeveoInstanceService;

/**
 * @author Nasseh
 **/
@WebService(serviceName = "CommunicationWs", endpointInterface = "org.meveo.api.ws.CommunicationWs")
@Interceptors({ LoggingInterceptor.class })
public class CommunicationWsImpl extends BaseWs implements CommunicationWs {

	@Inject
	private MeveoInstanceService meveoInstanceService;

	@Override
	public ActionStatus communicate(CommDto commDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		log.debug("REQUEST={}", commDto);
		try{
			MeveoInstance meveoInstance = meveoInstanceService.findByCode(commDto.getMeveoInstanceCode());
			if(meveoInstance != null){
				if(meveoInstance.getStatus()==MeveoInstanceStatusEnum.UNKNOWN){
					//TODO:  tigger an InboudCommunicationEvent 
				}else{
					//TODO: update instance ??
				}
			}
		} catch (Exception e) {
			result.setErrorCode(e.getMessage());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} 
		log.debug("RESPONSE={}", result);
		return result;
	}



}
