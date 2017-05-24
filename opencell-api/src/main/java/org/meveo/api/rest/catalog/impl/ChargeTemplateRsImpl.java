package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.ChargeTemplateApi;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.ChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ChargeTemplateRsImpl extends BaseRs implements ChargeTemplateRs {

    @Inject
    private ChargeTemplateApi chargeTemplateApi;

    @Override
    public GetChargeTemplateResponseDto find(String chargeTemplateCode) {
        GetChargeTemplateResponseDto result = new GetChargeTemplateResponseDto();

        try {
            result.setChargeTemplate(chargeTemplateApi.find(chargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}