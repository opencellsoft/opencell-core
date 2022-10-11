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

package org.meveo.api.billing;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.CdrErrorDto;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.billing.ProcessCdrDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ImmutableCdrListInput;
import org.meveo.apiv2.billing.ImmutableChargeCdrListInput;
import org.meveo.apiv2.billing.ProcessCdrListModeEnum;
import org.meveo.apiv2.billing.ProcessCdrListResult;
import org.meveo.apiv2.billing.service.MediationApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRAlreadyProcessedException;
import org.meveo.service.notification.DefaultObserver;

/**
 * API for CDR processing and mediation handling in general
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class MediationApi extends BaseApi {

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private DefaultObserver defaultObserver;

    @Inject
    private MediationApiService mediationApiService;

    /**
     * Register EDRS
     * 
     * @param postData String of CDRs. This CDR is parsed and created as EDR. CDR is same format use in mediation job
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<CdrErrorDto> registerCdrList(CdrListDto postData) throws MeveoApiException, BusinessException {
        List<String> cdrLines = postData.getCdr();
        if (cdrLines == null || cdrLines.size() == 0) {
            missingParameters.add("cdr");
        }

        handleMissingParameters();
        List<CdrErrorDto> errors = new ArrayList<>();

        CdrListInput cdrListInput = ImmutableCdrListInput.builder().addAllCdrs(postData.getCdr()).mode(ProcessCdrListModeEnum.PROCESS_ALL).isReturnEDRs(true).build();
        ProcessCdrListResult processCdrListResult = mediationApiService.registerCdrList(cdrListInput, postData.getIpAddress());

        for (ChargeCDRResponseDto processedCdr : processCdrListResult.getChargedCDRs()) {
            if (processedCdr.getError() != null) {
                errors.add(new CdrErrorDto(processedCdr.getError().getCdr(), processedCdr.getError().getErrorMessage()));
            }
        }

        return errors;
    }
    
    /**
     * Process CDRs to create EDRs
     *
     * @param cdrIds
     * @throws MeveoApiException
     * @throws BusinessException
     * @throws CDRAlreadyProcessedException 
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<ProcessCdrDto> processCdrList(List<Long> cdrIds) throws MeveoApiException, BusinessException, CDRAlreadyProcessedException {
        List<ProcessCdrDto> processCdrDtoList = new ArrayList<>();
        List<CDR> cdrs = mediationApiService.processCdrList(cdrIds);
        for(CDR cdr : cdrs) {
            processCdrDtoList.add(new ProcessCdrDto(cdr));
        }
        return processCdrDtoList;
    }

    /**
     * Register and rate EDRS
     *
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ChargeCDRResponseDto chargeCdr(ChargeCDRDto chargeCDRDto) throws MeveoApiException, BusinessException {
        String cdrLine = chargeCDRDto.getCdr();
        if (StringUtils.isBlank(cdrLine)) {
            missingParameters.add("cdr");
        }

        handleMissingParameters();

        ChargeCdrListInput cdrListInput = ImmutableChargeCdrListInput.builder().addCdrs(chargeCDRDto.getCdr()).mode(ProcessCdrListModeEnum.PROCESS_ALL).isVirtual(chargeCDRDto.isVirtual())
            .isRateTriggeredEdr(chargeCDRDto.isRateTriggeredEdr()).maxDepth(chargeCDRDto.getMaxDepth()).isReturnEDRs(chargeCDRDto.isReturnEDRs()).isReturnWalletOperations(chargeCDRDto.isReturnWalletOperations())
            .isReturnWalletOperationDetails(chargeCDRDto.isReturnWalletOperationDetails()).isReturnCounters(chargeCDRDto.isReturnCounters()).isGenerateRTs(chargeCDRDto.isGenerateRTs()).build();
        ProcessCdrListResult processCdrListResult = mediationApiService.chargeCdrList(cdrListInput, chargeCDRDto.getIp());

        ChargeCDRResponseDto chargeCDRResponseDto = null;
        for (ChargeCDRResponseDto processedCdr : processCdrListResult.getChargedCDRs()) {
            if (processedCdr!=null && processedCdr.getError() != null) {
                throw new MeveoApiException(processedCdr.getError().getErrorMessage());
            } else {
                chargeCDRResponseDto = processedCdr;
            }
        }
        if(chargeCDRResponseDto != null && processCdrListResult.getCounterPeriods() != null)
        	chargeCDRResponseDto.getCounterPeriods().addAll(processCdrListResult.getCounterPeriods());

        return chargeCDRResponseDto;

    }

    /**
     * Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity (PREPAID_RESRV_DELAY_MS)
     * 
     * @param cdrLine String of CDR
     * @param ip where request came from
     * @return Available quantity and reservationID is returned. if the reservation succeed then returns -1, else returns the available quantity for this cdr
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    public CdrReservationResponseDto reserveCdr(String cdrLine, String ip) throws MeveoApiException, BusinessException {
        CdrReservationResponseDto result = new CdrReservationResponseDto();
        // TODO: if insufficient balance retry with lower quantity
        result.setAvailableQuantity(-1);

        if (StringUtils.isBlank(cdrLine)) {
            missingParameters.add("cdr");
        }

        handleMissingParameters();

        CdrListInput cdrListInput = ImmutableCdrListInput.builder().addCdrs(cdrLine).mode(ProcessCdrListModeEnum.PROCESS_ALL).build();
        ProcessCdrListResult processCdrListResult = mediationApiService.reserveCdrList(cdrListInput, ip);

        for (ChargeCDRResponseDto processedCdr : processCdrListResult.getChargedCDRs()) {
            if (processedCdr.getError() != null) {
                throw new MeveoApiException(processedCdr.getError().getErrorMessage());
            } else {
                result.setReservationId(processedCdr.getReservationIds().get(0));
            }
        }
        return result;
    }

    /**
     * Confirms the reservation
     * 
     * @param reservationDto Prepaid reservation's data
     * @param ip where request came from
     * @throws MeveoApiException Meveo api exception.
     */
    public void confirmReservation(PrepaidReservationDto reservationDto, String ip) throws MeveoApiException {
        if (reservationDto.getReservationId() > 0) {
            try {
                Reservation reservation = reservationService.findById(reservationDto.getReservationId());
                if (reservation == null) {
                    throw new BusinessException("CANNOT_FIND_RESERVATION");
                }
                if (reservation.getStatus() != ReservationStatus.OPEN) {
                    throw new BusinessException("RESERVATION_NOT_OPEN");
                }
                log.debug("compare dto qty {} and reserved qty {}", reservationDto.getConsumedQuantity().toPlainString(), reservation.getQuantity().toPlainString());
                if (reservationDto.getConsumedQuantity().compareTo(reservation.getQuantity()) == 0) {
                    reservationService.confirmPrepaidReservation(reservation);
                } else if (reservationDto.getConsumedQuantity().compareTo(reservation.getQuantity()) < 0) {
                    reservationService.cancelPrepaidReservation(reservation);
                    EDR edr = reservation.getOriginEdr();
                    edr.setQuantity(reservationDto.getConsumedQuantity());
                    rateUsage(edr, new ChargeCDRDto());
                } else {
                    throw new BusinessException("CONSUMPTION_OVER_QUANTITY_RESERVED");
                }
//                try {
//                    if (timers.containsKey(reservation.getId())) {
//                        Timer timer = timers.get(reservation.getId());
//                        timer.cancel();
//                        timers.remove(reservation.getId());
//                        log.debug("Canceled expiry timer for reservation {}, remains {} active timers", reservation.getId(), timers.size());
//                    }
//                } catch (Exception e1) {
//                }
            } catch (BusinessException e) {
                log.error("Failed to confirm reservation ", e);
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            missingParameters.add("reservation");
            handleMissingParameters();
        }
    }

    private void rateUsage(EDR edr, ChargeCDRDto chargeCDRDto) throws MeveoApiException {

        try {
            usageRatingService.rateUsage(edr, chargeCDRDto.isVirtual(), chargeCDRDto.isRateTriggeredEdr(), chargeCDRDto.getMaxDepth(), 0, null, false);

        } catch (InsufficientBalanceException e) {
            log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
            throw new MeveoApiException(MeveoApiErrorCodeEnum.INSUFFICIENT_BALANCE, e.getMessage());

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
            throw new MeveoApiException(MeveoApiErrorCodeEnum.RATING_REJECT, e.getMessage());

        } catch (BusinessException e) {
            log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, e.getMessage());
        }
    }

    /**
     * Cancels the reservation
     * 
     * @param reservationDto Prepaid reservation's data
     * @param ip where request came from
     * @throws MeveoApiException Meveo api exception.
     */
    public void cancelReservation(PrepaidReservationDto reservationDto, String ip) throws MeveoApiException {
        if (reservationDto.getReservationId() > 0) {
            try {
                Reservation reservation = reservationService.findById(reservationDto.getReservationId());
                if (reservation == null) {
                    throw new BusinessException("CANNOT_FIND_RESERVATION");
                }
                if (reservation.getStatus() != ReservationStatus.OPEN) {
                    throw new BusinessException("RESERVATION_NOT_OPEN");
                }
                reservationService.cancelPrepaidReservation(reservation);
            } catch (BusinessException e) {
                log.error("Failed to cancel reservation ", e);
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            missingParameters.add("reservation");
            handleMissingParameters();
        }
    }

    /**
     * Notify of rejected CDRs - for each of rejected lines, trigger a notification
     * 
     * @param cdrList A list of rejected CDR lines (can be as json format string instead of csv line)
     */
    @Asynchronous
    public void notifyOfRejectedCdrs(CdrListDto cdrList) {

        for (String cdrLine : cdrList.getCdr()) {

            CDR cdr = new CDR();
            cdr.setLine(cdrLine);
            try {
                defaultObserver.cdrRejected(cdr);
            } catch (Exception e) {
                log.error("Failed to notify of rejected CDR {}", cdr, e);
            }
        }
    }
}
