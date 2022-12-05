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

package org.meveo.api.rest.billing.impl;

import java.util.List;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CdrDto;
import org.meveo.api.dto.billing.CdrErrorDto;
import org.meveo.api.dto.billing.CdrErrorListDto;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.billing.ProcessCDRResponseDto;
import org.meveo.api.dto.billing.ProcessCdrDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.MediationRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.rating.CDR;

/**
 * Mediation related API REST implementation
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 *
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class MediationRsImpl extends BaseRs implements MediationRs {

    @Inject
    private MediationApi mediationApi;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ActionStatus registerCdrList(CdrListDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            String ip = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
            postData.setIpAddress(ip);
            List<CdrErrorDto> cdrErrorDtos = mediationApi.registerCdrList(postData);
            if (!cdrErrorDtos.isEmpty()) {
                return new CdrErrorListDto(ActionStatusEnum.FAIL, "error while creating CDRs", cdrErrorDtos);
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    @Override
    public ProcessCDRResponseDto processCdrList(List<Long> cdrIds) {
        try {
            ProcessCDRResponseDto processCDRResponseDto = new ProcessCDRResponseDto();
            List<ProcessCdrDto> processCdrList = mediationApi.processCdrList(cdrIds);
            processCDRResponseDto.setListProcessCdrDto(processCdrList);
            processCDRResponseDto.setActionStatus(new ActionStatus());
            return processCDRResponseDto;
        } catch (Exception e) {
            ProcessCDRResponseDto result = new ProcessCDRResponseDto();
            ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.FAIL, null);
            result.setActionStatus(actionStatus);
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public ChargeCDRResponseDto chargeCdr(String cdr, boolean isVirtual, boolean rateTriggeredEdr, Integer maxDepth, boolean returnEDRs, boolean returnWalletOperations, boolean returnWalletOperationDetails,
            boolean returnCounters, boolean generateRTs) {

        try {
            ChargeCDRDto chargeCDRDto = new ChargeCDRDto(cdr, httpServletRequest.getRemoteAddr(), isVirtual, rateTriggeredEdr, maxDepth, returnEDRs, returnWalletOperations, returnWalletOperationDetails, returnCounters, generateRTs);
            ChargeCDRResponseDto responseDto = mediationApi.chargeCdr(chargeCDRDto);
            responseDto.setActionStatus(new ActionStatus());
            return responseDto;

        } catch (Exception e) {
            ChargeCDRResponseDto result = new ChargeCDRResponseDto();
            ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.FAIL, null);
            result.setActionStatus(actionStatus);
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public CdrReservationResponseDto reserveCdr(String cdr) {
        CdrReservationResponseDto result = new CdrReservationResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            CdrReservationResponseDto response = mediationApi.reserveCdr(cdr, httpServletRequest.getRemoteAddr());
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
    public ActionStatus confirmReservation(PrepaidReservationDto reservationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            mediationApi.confirmReservation(reservationDto, httpServletRequest.getRemoteAddr());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelReservation(PrepaidReservationDto reservationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            mediationApi.cancelReservation(reservationDto, httpServletRequest.getRemoteAddr());
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

    @Override
    public ActionStatus createCDR(CdrDto cdrDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
           CDR cdr = mediationApi.createCdr(cdrDto, httpServletRequest.getRemoteAddr());
           result.setEntityId(cdr.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}