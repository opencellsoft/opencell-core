package org.meveo.api.rest.catalog.impl;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OfferTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OfferTemplateRsImpl extends BaseRs implements OfferTemplateRs {

    @Inject
    private OfferTemplateApi offerTemplateApi;

    @Override
    public ActionStatus create(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    @Override
    public GetOfferTemplateResponseDto find(String offerTemplateCode, Date validFrom, Date validTo) {
        GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();

        try {
            result.setOfferTemplate(offerTemplateApi.find(offerTemplateCode, validFrom, validTo));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetListOfferTemplateResponseDto list(String code, Date validFrom, Date validTo) {
        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();

        try {
            result.setOfferTemplates(offerTemplateApi.list(code, validFrom, validTo));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String offerTemplateCode, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.remove(offerTemplateCode, validFrom, validTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
