package org.meveo.api.rest.job.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.GetTimerEntityResponseDto;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.job.TimerEntityRs;

/**
 * 
 * @author Manu Liwanag
 * 
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TimerEntityRsImpl extends BaseRs implements TimerEntityRs {

    @Inject
    private TimerEntityApi timerEntityApi;

    @Override
    public ActionStatus create(TimerEntityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            timerEntityApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(TimerEntityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            timerEntityApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(TimerEntityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            timerEntityApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTimerEntityResponseDto find(String timerEntityCode) {
        GetTimerEntityResponseDto result = new GetTimerEntityResponseDto();
        try {
            result.setTimerEntity(timerEntityApi.find(timerEntityCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

}
