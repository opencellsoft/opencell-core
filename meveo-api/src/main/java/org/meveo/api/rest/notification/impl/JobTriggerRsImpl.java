package org.meveo.api.rest.notification.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.response.notification.GetJobTriggerResponseDto;
import org.meveo.api.exception.MeveoApiException;
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
            jobTriggerApi.create(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while creating job trigger ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while creating job trigger ", e);
        }

        return result;
    }

    @Override
    public ActionStatus update(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.update(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while updating job trigger ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while updating job trigger ", e);
        }

        return result;
    }

    @Override
    public GetJobTriggerResponseDto find(String notificationCode) {
        GetJobTriggerResponseDto result = new GetJobTriggerResponseDto();

        try {
            result.setJobTriggerDto(jobTriggerApi.find(notificationCode, getCurrentUser()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("error occurred while getting job trigger ", e);
        } catch (Exception e) {
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("error generated while getting job trigger ", e);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.remove(notificationCode, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while removing job trigger ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while removing job trigger ", e);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.createOrUpdate(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while creating job trigger ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while creating  job trigger ", e);
        }

        return result;
    }
}
