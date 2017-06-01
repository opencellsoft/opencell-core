package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;

import org.meveo.api.catalog.OneShotChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OneShotChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OneShotChargeTemplateRsImpl extends BaseRs implements OneShotChargeTemplateRs {

    @Inject
    private OneShotChargeTemplateApi oneShotChargeTemplateApi;

    @POST
    public ActionStatus create(OneShotChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(OneShotChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(@QueryParam("languageCode") String languageCode, @QueryParam("countryCode") String countryCode,
            @QueryParam("currencyCode") String currencyCode, @QueryParam("sellerCode") String sellerCode, @QueryParam("date") String date) {

        OneShotChargeTemplateWithPriceListDto result = new OneShotChargeTemplateWithPriceListDto();

        try {
            result.setOneShotChargeTemplateDtos(
                oneShotChargeTemplateApi.listWithPrice(languageCode, countryCode, currencyCode, sellerCode, DateUtils.parseDateWithPattern(date, "yyyy-MM-dd")));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetOneShotChargeTemplateResponseDto find(String oneShotChargeTemplateCode) {
        GetOneShotChargeTemplateResponseDto result = new GetOneShotChargeTemplateResponseDto();

        try {
            result.setOneShotChargeTemplate(oneShotChargeTemplateApi.find(oneShotChargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String oneShotChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.remove(oneShotChargeTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OneShotChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
