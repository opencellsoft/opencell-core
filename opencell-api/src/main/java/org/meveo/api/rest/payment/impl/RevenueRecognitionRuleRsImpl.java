/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.payment.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RevenueRecognitionRuleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.RevenueRecognitionRulesRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.RevenueRecognitionRule;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RevenueRecognitionRuleRsImpl extends BaseRs implements RevenueRecognitionRulesRs {

    @Inject
    RevenueRecognitionRuleApi revenueRecognitionRuleApi;

    @Override
    public ActionStatus create(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(revenueRecognitionRule.getCode());
            }
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
            RevenueRecognitionRule revenueRecognitionRule = revenueRecognitionRuleApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(revenueRecognitionRule.getCode());
            }
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