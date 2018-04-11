package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.DiscountPlanItemApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
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
            discountPlanItemApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DiscountPlanItemResponseDto find(String discountPlanItemCode) {
        DiscountPlanItemResponseDto result = new DiscountPlanItemResponseDto();

        try {
            result.setDiscountPlanItem(discountPlanItemApi.find(discountPlanItemCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String discountPlanItemCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.remove(discountPlanItemCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DiscountPlanItemsResponseDto list() {
        DiscountPlanItemsResponseDto result = new DiscountPlanItemsResponseDto();

        try {
            result.setDiscountPlanItems(discountPlanItemApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            discountPlanItemApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            discountPlanItemApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}