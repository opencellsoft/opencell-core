package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.account.SellerApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.SellerRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SellerRsImpl extends BaseRs implements SellerRs {

    @Inject
    private SellerApi sellerApi;

    @Override
    public ActionStatus create(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.create(postData, getCurrentUser());
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
    public ActionStatus update(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.update(postData, getCurrentUser());
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
    public GetSellerResponse find(String sellerCode) {
        GetSellerResponse result = new GetSellerResponse();

        try {
            result.setSeller(sellerApi.find(sellerCode, getCurrentUser()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String sellerCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.remove(sellerCode, getCurrentUser());
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
    public SellerResponseDto list() {
        SellerResponseDto result = new SellerResponseDto();

        try {
            result.setSellers(sellerApi.list(getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public SellerCodesResponseDto listSellerCodes() {
        SellerCodesResponseDto result = new SellerCodesResponseDto();

        try {
            result = sellerApi.listSellerCodes(getCurrentUser().getProvider());
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            sellerApi.createOrUpdate(postData, getCurrentUser());
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

}
