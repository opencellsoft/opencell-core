package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RevenueRecognitionRuleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.RevenueRecognitionRulesRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RevenueRecognitionRuleRsImpl extends BaseRs implements RevenueRecognitionRulesRs {

    @Inject
    RevenueRecognitionRuleApi revenueRecognitionRuleApi;

    @Override
    public ActionStatus create(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            revenueRecognitionRuleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            revenueRecognitionRuleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            revenueRecognitionRuleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public RevenueRecognitionRuleDtoResponse find(String revenueRecognitionRuleCode) {
        RevenueRecognitionRuleDtoResponse result = new RevenueRecognitionRuleDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            result.setRevenueRecognitionRuleDto(revenueRecognitionRuleApi.find(revenueRecognitionRuleCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus remove(String revenueRecognitionRuleCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            revenueRecognitionRuleApi.remove(revenueRecognitionRuleCode);

        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public RevenueRecognitionRuleDtosResponse list() {
        RevenueRecognitionRuleDtosResponse result = new RevenueRecognitionRuleDtosResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            result.setRevenueRecognitionRules(revenueRecognitionRuleApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            revenueRecognitionRuleApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            revenueRecognitionRuleApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}