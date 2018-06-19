package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OfferTemplateCategoryRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OfferTemplateCategoryRsImpl extends BaseRs implements OfferTemplateCategoryRs {

    @Inject
    private OfferTemplateCategoryApi offerTemplateCategoryApi;

    @Override
    public ActionStatus create(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateCategoryResponseDto find(String offerTemplateCategoryCode) {
        GetOfferTemplateCategoryResponseDto result = new GetOfferTemplateCategoryResponseDto();

        try {
            result.setOfferTemplateCategory(offerTemplateCategoryApi.find(offerTemplateCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus delete(String offerTemplateCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.remove(offerTemplateCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            offerTemplateCategoryApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            offerTemplateCategoryApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}