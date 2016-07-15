package org.meveo.api.rest.wf.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.response.payment.ActionPlanItemResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.wf.WFActionRs;
import org.meveo.api.wf.WFActionApi;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class WFActionRsImpl extends BaseRs implements WFActionRs {
	@Inject
	private WFActionApi wfActionApi;

	@Override
	public ActionStatus create(WFActionDto wfActionDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	wfActionApi.create(wfActionDto, getCurrentUser());
        } catch (Exception e) {
            super.processException(e, result);
         }

        return result;
	}

	@Override
	public ActionStatus update(WFActionDto wfActionDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	wfActionApi.update(wfActionDto, getCurrentUser());
        } catch (Exception e) {
           super.processException(e, result);
        }

        return result;
	}

	@Override
	public ActionStatus createOrUpdate(WFActionDto wfActionDto) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		 //TODO
         return result;
	}

	@Override
	public ActionPlanItemResponseDto find(WFTransitionDto wfTransitionDto, Integer priority) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus remove(String workflowCode, String fromStatus, String toStatus, Integer priority) {
		// TODO Auto-generated method stub
		return null;
	}

}

