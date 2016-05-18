package org.meveo.api.rest.script.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.script.ServiceModelScriptResponseDto;
import org.meveo.api.dto.script.ServiceModelScriptDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.script.ServiceModelScriptRs;
import org.meveo.api.script.ServiceModelScriptApi;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ServiceModelScriptRsImpl extends BaseRs implements ServiceModelScriptRs {

    @Inject
    private ServiceModelScriptApi serviceModelScriptApi;

    @Override
    public ActionStatus create(ServiceModelScriptDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            serviceModelScriptApi.create(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Error when create {}. {}", this.getClass().getSimpleName(), e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Unknown exception when create {}. {}", this.getClass().getSimpleName(), e);
        }

        return result;
    }

    @Override
    public ActionStatus update(ServiceModelScriptDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            serviceModelScriptApi.update(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Error when update {}. {}", this.getClass().getSimpleName(), e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Unknown exception when update {}. {}", this.getClass().getSimpleName(), e);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ServiceModelScriptDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceModelScriptApi.createOrUpdate(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus delete(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            serviceModelScriptApi.delete(code, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Error when delete {}. {}", this.getClass().getSimpleName(), e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("Unknown exception when delete {}. {}", this.getClass().getSimpleName(), e);
        }

        return result;
    }

    @Override
    public ServiceModelScriptResponseDto get(String code) {
        ServiceModelScriptResponseDto result = new ServiceModelScriptResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setServiceModelScript(serviceModelScriptApi.get(code, getCurrentUser().getProvider()));
        } catch (Exception e) {
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("Error when get {}. {}", this.getClass().getSimpleName(), e);
        }

        return result;
    }

}
