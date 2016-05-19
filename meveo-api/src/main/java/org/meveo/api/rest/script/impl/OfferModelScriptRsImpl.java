package org.meveo.api.rest.script.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.script.OfferModelScriptResponseDto;
import org.meveo.api.dto.script.OfferModelScriptDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.script.OfferModelScriptRs;
import org.meveo.api.script.OfferModelScriptApi;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OfferModelScriptRsImpl extends BaseRs implements OfferModelScriptRs {

    @Inject
    private OfferModelScriptApi offerModelScriptApi;

    @Override
    public ActionStatus create(OfferModelScriptDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            offerModelScriptApi.create(postData, getCurrentUser());
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
    public ActionStatus update(OfferModelScriptDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            offerModelScriptApi.update(postData, getCurrentUser());
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
    public ActionStatus createOrUpdate(OfferModelScriptDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerModelScriptApi.createOrUpdate(postData, getCurrentUser());
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
            offerModelScriptApi.delete(code, getCurrentUser());
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
    public OfferModelScriptResponseDto get(String code) {
        OfferModelScriptResponseDto result = new OfferModelScriptResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setOfferModelScript(offerModelScriptApi.get(code, getCurrentUser().getProvider()));
        } catch (Exception e) {
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("Error when get {}. {}", this.getClass().getSimpleName(), e);
        }

        return result;
    }

}
