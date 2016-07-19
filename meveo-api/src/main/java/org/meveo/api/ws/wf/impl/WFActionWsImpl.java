package org.meveo.api.ws.wf.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.wf.WFActionApi;
import org.meveo.api.ws.impl.BaseWs;
import org.meveo.api.ws.wf.WFActionWs;

@WebService(serviceName = "WFActionWs", endpointInterface = "org.meveo.api.ws.wf.WFActionWs")
@Interceptors({ WsRestApiInterceptor.class })
public class WFActionWsImpl extends BaseWs implements WFActionWs {

	@Inject
	private WFActionApi wfActionApi;

	@Override
	public ActionStatus create(WFActionDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			wfActionApi.create(postData, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}


		return result;
	}

	@Override
	public ActionStatus update(WFActionDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			wfActionApi.update(postData, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(WFActionDto postData) {
		// TODO Auto-generated method stub
		return null;
	}



}