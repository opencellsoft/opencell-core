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

package org.meveo.api.tunnel;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.tunnel.TunnelCustomizationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.subscriptionTunnel.TunnelCustomization;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class TunnelCustomizationApi extends BaseCrudApi<TunnelCustomization, TunnelCustomizationDto> {


    @Override
    public TunnelCustomization create(TunnelCustomizationDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }

    @Override
    public TunnelCustomization update(TunnelCustomizationDto dtoData) throws MeveoApiException, BusinessException {
        return null;
    }

    /**
     * Populate entity with fields from DTO entity
     *
     * @param entity Entity to populate
     * @param dto DTO entity object to populate from
     **/
    private void dtoToEntity(TunnelCustomization entity, TunnelCustomizationDto dto) {

        if(dto.getRgpd() != null) {
            entity.setRgpd(convertMultiLanguageToMapOfValues(dto.getRgpd(), null));
        }
        if (dto.getTermsAndConditions() != null) {
            entity.setTermsAndConditions(convertMultiLanguageToMapOfValues(dto.getTermsAndConditions(), null));
        }
        if (dto.getOrderValidationMsg() != null) {
            entity.setOrderValidationMsg(convertMultiLanguageToMapOfValues(dto.getOrderValidationMsg(), null));
        }
        if (dto.getSignatureMsg() != null) {
            entity.setSignatureMsg(convertMultiLanguageToMapOfValues(dto.getSignatureMsg(), null));
        }
        if (dto.getAnalytics() != null) {
            entity.setAnalytics(dto.getAnalytics());
        }
        if (dto.getTheme() != null) {

        }
    }
}
