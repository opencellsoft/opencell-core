package org.meveo.api.rest.notification.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.response.notification.GetJobTriggerResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.notification.JobTriggerApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.JobTriggerRs;

/**
 * @author Tyshan Shi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class JobTriggerRsImpl extends BaseRs implements JobTriggerRs {

    @Inject
    private JobTriggerApi jobTriggerApi;

    @Override
    public ActionStatus create(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus update(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetJobTriggerResponseDto find(String notificationCode) {
        GetJobTriggerResponseDto result = new GetJobTriggerResponseDto();

        try {
            result.setJobTriggerDto(jobTriggerApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
