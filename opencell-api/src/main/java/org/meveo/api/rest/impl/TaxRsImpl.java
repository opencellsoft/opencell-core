package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.TaxApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.TaxRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxRsImpl extends BaseRs implements TaxRs {

    @Inject
    private TaxApi taxApi;

    @Override
    public ActionStatus create(TaxDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(TaxDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTaxResponse find(String taxCode) {
        GetTaxResponse result = new GetTaxResponse();

        try {
            result.setTax(taxApi.find(taxCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String taxCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.remove(taxCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(TaxDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTaxesResponse list() {
        GetTaxesResponse result = new GetTaxesResponse();

        try {
            result.setTaxesDto(taxApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }
}