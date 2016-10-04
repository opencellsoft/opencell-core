package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.DiscountPlanItemApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.DiscountPlanItemRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DiscountPlanItemRsImpl extends BaseRs implements DiscountPlanItemRs {

    @Inject
    private DiscountPlanItemApi discountPlanItemApi;

    @Override
    public ActionStatus create(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.create(postData, getCurrentUser());
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
    public ActionStatus update(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.update(postData, getCurrentUser());
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
    public DiscountPlanItemResponseDto find(String discountPlanItemCode) {
        DiscountPlanItemResponseDto result = new DiscountPlanItemResponseDto();

        try {
            result.setDiscountPlanItem(discountPlanItemApi.find(discountPlanItemCode, getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String discountPlanItemCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.remove(discountPlanItemCode, getCurrentUser());
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
    public ActionStatus createOrUpdate(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.createOrUpdate(postData, getCurrentUser());
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
    public DiscountPlanItemsResponseDto list() {
        DiscountPlanItemsResponseDto result = new DiscountPlanItemsResponseDto();

        try {
            result.setDiscountPlanItems(discountPlanItemApi.list(getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

}
