package org.meveo.api.ws.wf.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.wf.WFTransitionResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.wf.WFTransitionApi;
import org.meveo.api.ws.impl.BaseWs;
import org.meveo.api.ws.wf.WFTransitionWs;

@WebService(serviceName = "WFTransitionWs", endpointInterface = "org.meveo.api.ws.wf.WFTransitionWs")
@Interceptors({ WsRestApiInterceptor.class })
public class WFTransitionWsImpl extends BaseWs implements WFTransitionWs {

	@Inject
	private WFTransitionApi wfTransitionApi;

	@Override
	public ActionStatus create(WFTransitionDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			wfTransitionApi.create(postData, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus update(WFTransitionDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			wfTransitionApi.update(postData, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus createOrUpdate(WFTransitionDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			wfTransitionApi.createOrUpdate(postData, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}

		return result;
	}

	@Override
	public WFTransitionResponseDto find(String workflowCode, String fromStatus, String toStatus) {
		WFTransitionResponseDto dunningPlanTransitionResponseDto = new WFTransitionResponseDto();
		try {
			dunningPlanTransitionResponseDto.setWfTransitionDto(wfTransitionApi.find(workflowCode, fromStatus, toStatus, getCurrentUser()));
		} catch (Exception e) {
			super.processException(e, dunningPlanTransitionResponseDto.getActionStatus());
		}

		return dunningPlanTransitionResponseDto;
	}

	@Override
	public ActionStatus remove(String workflowCode, String fromStatus, String toStatus) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			wfTransitionApi.remove(workflowCode, fromStatus, toStatus, getCurrentUser());	        
		} catch (Exception e) {
			super.processException(e, result);
		}

		return result;
	}

}