package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.RecurringChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.RecurringChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RecurringChargeTemplateRsImpl extends BaseRs implements RecurringChargeTemplateRs {

    @Inject
    private RecurringChargeTemplateApi recurringChargeTemplateApi;

    @Override
    public ActionStatus create(RecurringChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetRecurringChargeTemplateResponseDto find(String recurringChargeTemplateCode) {
        GetRecurringChargeTemplateResponseDto result = new GetRecurringChargeTemplateResponseDto();

        try {
            result.setRecurringChargeTemplate(recurringChargeTemplateApi.find(recurringChargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(RecurringChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String recurringChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.remove(recurringChargeTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(RecurringChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            recurringChargeTemplateApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            recurringChargeTemplateApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}