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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.parse.csv.MEVEOCdrReader;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.CdrDto;
import org.meveo.api.dto.billing.CdrErrorDto;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.billing.ProcessCdrDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ImmutableCdrListInput;
import org.meveo.apiv2.billing.ImmutableChargeCdrListInput;
import org.meveo.apiv2.billing.ProcessingModeEnum;
import org.meveo.apiv2.billing.ProcessCdrListResult;
import org.meveo.apiv2.billing.service.MediationApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.medina.impl.CDRAlreadyProcessedException;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.DuplicateException;
import org.meveo.service.notification.DefaultObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    @Inject
    private AccessService accessService;
    
    @Inject
    private CDRService cdrService;
    @Inject
    private EdrService edrService;
    
    private static final ThreadLocal<MessageDigest> messageDigest = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                Logger log = LoggerFactory.getLogger(MEVEOCdrReader.class);
                log.error("No message digest of type SHA-256", e);
                return null;
            }
        }        
    };
    
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

        CdrListInput cdrListInput = ImmutableCdrListInput.builder().addAllCdrs(postData.getCdr()).mode(ProcessingModeEnum.PROCESS_ALL).isReturnEDRs(true).build();
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
     * @param cdrIds A list of CDR ids to process
     * @return A list of processed CDR information
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

        ChargeCdrListInput cdrListInput = ImmutableChargeCdrListInput.builder().addCdrs(chargeCDRDto.getCdr()).mode(ProcessingModeEnum.PROCESS_ALL).isVirtual(chargeCDRDto.isVirtual())
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

        CdrListInput cdrListInput = ImmutableCdrListInput.builder().addCdrs(cdrLine).mode(ProcessingModeEnum.PROCESS_ALL).build();
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
    
    public CDR createCdr(CdrDto dto, String ip) throws DuplicateException {
        if(dto.getEventDate() == null) {
            missingParameters.add("eventDate");
        }
        if(dto.getQuantity() == null || BigDecimal.ZERO == dto.getQuantity()) {
            missingParameters.add("quantity");
        }
        if(StringUtils.isBlank(dto.getAccessCode())) {
            missingParameters.add("accessCode");
        }
        if(StringUtils.isBlank(dto.getParameter1())) {
            missingParameters.add("parameter1");
        }
        handleMissingParameters();
        
        CDR cdr = new CDR();
        if(CollectionUtils.isEmpty(accessService.getActiveAccessByUserId(dto.getAccessCode()))) {
            throw new EntityDoesNotExistsException("No Access point found for accessCode : " + dto.getAccessCode());
        }
        populateFromDto(dto, cdr);
        cdr.setStatus(CDRStatusEnum.OPEN);
        cdr.setStatusDate(new Date());
        cdr.setOriginBatch(ip);
        String cdrToCsv = cdr.toCsv();
        cdr.setOriginRecord(getOriginRecordFromApi(cdrToCsv));
        cdr.setLine(cdrToCsv);
        
        if (edrService.isDuplicateFound(cdr.getOriginRecord())) {
            throw new DuplicateException(cdr);
        }
        
        cdrService.create(cdr);
        return cdr;
    }
    
    private void populateFromDto(CdrDto dto, CDR cdr) {

        cdr.setEventDate(dto.getEventDate());
        cdr.setQuantity(dto.getQuantity());
        cdr.setAccessCode(dto.getAccessCode());
        cdr.setParameter1(dto.getParameter1());
        
        checkAndSetValueParams(dto, cdr);
        checkAndSetValueDateParam(dto, cdr);
        checkAndValueDecimaParam(dto, cdr);
        
    }
    
    private void checkAndSetValueParams(CdrDto dto, CDR cdr) {
        if(dto.getParameter2() != null)
            cdr.setParameter2(dto.getParameter2());
        if(dto.getParameter3() != null)
            cdr.setParameter3(dto.getParameter3());
        if(dto.getParameter4() != null)
            cdr.setParameter4(dto.getParameter4());
        if(dto.getParameter5() != null)
             cdr.setParameter5(dto.getParameter5());
        if(dto.getParameter6() != null)
            cdr.setParameter6(dto.getParameter6());
        if(dto.getParameter7() != null)
            cdr.setParameter7(dto.getParameter7());
        if(dto.getParameter8() != null)
             cdr.setParameter8(dto.getParameter8());
        if(dto.getParameter9() != null)
             cdr.setParameter9(dto.getParameter9());
    }
    private void checkAndSetValueDateParam(CdrDto dto, CDR cdr) {
        if(dto.getDateParam1() != null)
            cdr.setDateParam1(dto.getDateParam1());
        if(dto.getDateParam2() != null)
            cdr.setDateParam2(dto.getDateParam2());
        if(dto.getDateParam3() != null)
            cdr.setDateParam3(dto.getDateParam3());
        if(dto.getDateParam4() != null)
            cdr.setDateParam4(dto.getDateParam4());
        if(dto.getDateParam5() != null)
            cdr.setDateParam5(dto.getDateParam5());
    	
    }
    
    private void checkAndValueDecimaParam(CdrDto dto, CDR cdr) {
        if(dto.getDecimalParam1() != null)
            cdr.setDecimalParam1(dto.getDecimalParam1());
        if(dto.getDecimalParam2() != null)
            cdr.setDecimalParam2(dto.getDecimalParam2());
        if(dto.getDecimalParam3() != null)
            cdr.setDecimalParam3(dto.getDecimalParam3());
        if(dto.getDecimalParam4() != null)
            cdr.setDecimalParam4(dto.getDecimalParam4());
        if(dto.getDecimalParam5() != null)
            cdr.setDecimalParam5(dto.getDecimalParam5());
        if(dto.getExtraParam() != null)
            cdr.setExtraParameter(dto.getExtraParam());
    }
    
    private String getOriginRecordFromApi(String cdr) {
        MessageDigest md = messageDigest.get();
        if (md != null) {
            md.reset();
            md.update(cdr.getBytes(StandardCharsets.UTF_8));
            final byte[] resultByte = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < resultByte.length; ++i) {
                sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        }

        return null;
    }
}