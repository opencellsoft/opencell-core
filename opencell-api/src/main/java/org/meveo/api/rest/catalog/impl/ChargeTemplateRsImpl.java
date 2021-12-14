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

package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.GenericChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.ChargeTemplateRs;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ChargeTemplateRsImpl extends BaseRs implements ChargeTemplateRs {

    @Inject
    private GenericChargeTemplateApi chargeTemplateApi;

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

    @Override
    public GetChargeTemplateResponseDto findV2(String chargeTemplateCode) {
        return find(chargeTemplateCode);
    }
    
    @Override
    public ActionStatus updateStatus(String chargeTemplateCode, String status) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	chargeTemplateApi.updateStatus(chargeTemplateCode, status);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public GetChargeTemplateResponseDto duplicateCharge(String chargeTemplateCode) {
        GetChargeTemplateResponseDto result = new GetChargeTemplateResponseDto();

        try {
        	result = chargeTemplateApi.duplicateCharge(chargeTemplateCode);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

}