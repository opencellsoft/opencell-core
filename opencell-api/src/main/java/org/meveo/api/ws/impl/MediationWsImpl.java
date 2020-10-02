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

package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;

import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.MediationWs;
import org.meveo.commons.utils.StringUtils;

/**
 * Mediation related API WS implementation
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 *
 */
@WebService(serviceName = "MediationWs", endpointInterface = "org.meveo.api.ws.MediationWs")
@Interceptors({ WsRestApiInterceptor.class })
@Deprecated
public class MediationWsImpl extends BaseWs implements MediationWs {

    @Inject
    private MediationApi mediationApi;

    @Override
    public ActionStatus registerCdrList(CdrListDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            HttpServletRequest req = getHttpServletRequest();

            String ip = StringUtils.isBlank(req.getHeader("x-forwarded-for")) ? req.getRemoteAddr() : req.getHeader("x-forwarded-for");
            postData.setIpAddress(ip);
            mediationApi.registerCdrList(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ChargeCDRResponseDto chargeCdr(String cdr, boolean isVirtual, boolean rateTriggeredEdr, boolean returnWalletOperations, Integer maxDepth) {

        try {
            ChargeCDRDto chargeCDRDto = new ChargeCDRDto(cdr, getHttpServletRequest().getRemoteAddr(), isVirtual,  rateTriggeredEdr,  returnWalletOperations,  maxDepth);
            return mediationApi.chargeCdr(chargeCDRDto);
        } catch (Exception e) {
            ChargeCDRResponseDto result = new ChargeCDRResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public CdrReservationResponseDto reserveCdr(String cdr) {
        CdrReservationResponseDto result = new CdrReservationResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            CdrReservationResponseDto response = mediationApi.reserveCdr(cdr, getHttpServletRequest().getRemoteAddr());
            double availableQuantity = response.getAvailableQuantity();
            if (availableQuantity == 0) {
                result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
                result.getActionStatus().setMessage("INSUFICIENT_BALANCE");
            } else if (availableQuantity > 0) {
                result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
                result.getActionStatus().setMessage("NEED_LOWER_QUANTITY");
                result.setAvailableQuantity(availableQuantity);
            }
            result.setAvailableQuantity(availableQuantity);
            result.setReservationId(response.getReservationId());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus confirmReservation(PrepaidReservationDto reservation) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            mediationApi.confirmReservation(reservation, getHttpServletRequest().getRemoteAddr());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelReservation(PrepaidReservationDto reservation) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            mediationApi.cancelReservation(reservation, getHttpServletRequest().getRemoteAddr());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus notifyOfRejectedCdrs(CdrListDto cdrList) {
        ActionStatus result = new ActionStatus();

        try {
            mediationApi.notifyOfRejectedCdrs(cdrList);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}