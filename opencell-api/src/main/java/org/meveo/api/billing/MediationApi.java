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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.mediation.Access;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.InvalidAccessException;
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

    @Resource
    private TimerService timerService;

    @Inject
    private CDRParsingService cdrParsingService;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private DefaultObserver defaultObserver;
    
    @Inject
    private CDRService cdrService;

    Map<Long, Timer> timers = new HashMap<Long, Timer>();

    /**
     * Register EDRS
     * 
     * @param postData String of CDRs. This CDR is parsed and created as EDR. CDR is same format use in mediation job
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    public void registerCdrList(CdrListDto postData) throws MeveoApiException, BusinessException {
        List<String> cdrLines = postData.getCdr();
        if (cdrLines == null || cdrLines.size() == 0) {
            missingParameters.add("cdr");
        }

        handleMissingParameters();
        ICdrParser cdrParser = cdrParsingService.getCDRParser(null);
        CDR cdr = null;        
        try {
            ListIterator<String> cdrIterator = cdrLines.listIterator();
            while (cdrIterator.hasNext()) {
                cdr = cdrParser.parseByApi(cdrIterator.next(), currentUser.getUserName(), postData.getIpAddress());
                if(cdr.getRejectReason() != null) {
                    log.error("Failed to process a CDR line: {} error {}", cdr != null ? cdr.getLine() : null, cdr.getRejectReason());
                    cdr.setStatus(CDRStatusEnum.ERROR);
                    createOrUpdateCdr(cdr);
                } else {
                    List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
                    List<EDR> edrs = cdrParser.convertCdrToEdr(cdr,accessPoints);       
                    cdrParsingService.createEdrs(edrs,cdr);
                }                
            }
        } catch (Exception e) {
            String errorReason = e.getMessage();
            if (e instanceof CDRParsingException) {
                log.error("Failed to process a CDR line: {} error {}", cdr != null ? cdr.getLine() : null, errorReason);
            } else {
                log.error("Failed to process a CDR line: {} error {}", cdr != null ? cdr.getLine() : null, errorReason, e);
            }
            cdr.setStatus(CDRStatusEnum.ERROR);
            createOrUpdateCdr(cdr);
        }
    }

    /**
     * Register and rate EDRS
     *
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    public ChargeCDRResponseDto chargeCdr(ChargeCDRDto chargeCDRDto) throws MeveoApiException, BusinessException {
        String cdrLine = chargeCDRDto.getCdr();
        if (StringUtils.isBlank(cdrLine)) {
            missingParameters.add("cdr");
        }

        handleMissingParameters();
        ICdrParser cdrParser = cdrParsingService.getCDRParser(null);
        boolean persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));
        CDR cdr = null;
        try {
            cdr = cdrParser.parseByApi(cdrLine, currentUser.getUserName(), chargeCDRDto.getIp());
            if(cdr == null) {
                return null;
            }
            if(cdr.getRejectReason() != null) {
                log.error("Error parsing cdr={}", cdr.getRejectReason()); 
                cdr.setStatus(CDRStatusEnum.ERROR);
                if(cdr.getRejectReasonException() != null && cdr.getRejectReasonException() instanceof InvalidAccessException) {
                    throw new CDRParsingException(cdr, CDRRejectionCauseEnum.INVALID_ACCESS, cdr.getRejectReason());
                } else {
                    throw new CDRParsingException(cdr, CDRRejectionCauseEnum.INVALID_FORMAT, cdr.getRejectReason());
                }               
            }

            List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
            List<EDR> edrs = cdrParser.convertCdrToEdr(cdr,accessPoints);                        
            List<WalletOperation> walletOperations = new ArrayList<>();
            for (EDR edr : edrs) {               
                log.debug("edr={}", edr);
                edr.setSubscription(edr.getSubscription());
                if (!chargeCDRDto.isVirtual()) {
                    edrService.create(edr);
                }
                List<WalletOperation> wo = rateUsage(edr, chargeCDRDto);
                if (wo != null) {
                    walletOperations.addAll(wo);
                }
            }

            return createChargeCDRResultDto(walletOperations, chargeCDRDto.isReturnWalletOperations());                    
        } catch (CDRParsingException e) {
            log.error("Error parsing cdr={}", e.getRejectionCause());
            throw new MeveoApiException(e.getRejectionCause().toString());
        } finally {
            if (persistCDR && cdr != null) {
                cdrService.create(cdr);
            }
        }
    }

    private ChargeCDRResponseDto createChargeCDRResultDto(List<WalletOperation> walletOperations, boolean returnWalletOperations) {

        ChargeCDRResponseDto result = new ChargeCDRResponseDto();
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal amountWithoutTax = BigDecimal.ZERO;
        BigDecimal amountTax = BigDecimal.ZERO;
        for (WalletOperation walletOperation : walletOperations) {
            if (returnWalletOperations) {
                WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                result.getWalletOperations().add(walletOperationDto);
            }
            amountWithTax = amountWithTax.add(walletOperation.getAmountWithTax() != null ? walletOperation.getAmountWithTax() : BigDecimal.ZERO);
            amountWithoutTax = amountWithoutTax.add(walletOperation.getAmountWithoutTax() != null ? walletOperation.getAmountWithoutTax() : BigDecimal.ZERO);
            amountTax = amountTax.add(walletOperation.getAmountTax() != null ? walletOperation.getAmountTax() : BigDecimal.ZERO);
        }
        if (returnWalletOperations) {
            result.setWalletOperationCount(result.getWalletOperations().size());
        }
        result.setAmountTax(amountTax);
        result.setAmountWithoutTax(amountWithoutTax);
        result.setAmountWithTax(amountWithTax);
        return result;
    }

    @Timeout
    private void reservationExpired(Timer timer) {
        Object[] objs = (Object[]) timer.getInfo();
        try {
        	   Reservation reservation = reservationService.findById((Long) objs[0]);
               if(!ReservationStatus.CONFIRMED.equals(reservation.getStatus())) {
               	reservationService.cancelPrepaidReservationInNewTransaction(reservation);
               }
        } catch (BusinessException e) {
            log.error("Failed to cancel Prepaid Reservation In New Transaction", e);
        }
    }

    /**
     * Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity
     * (PREPAID_RESRV_DELAY_MS)
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

        ICdrParser cdrParser = cdrParsingService.getCDRParser(null);
        CDR cdr = null;
        List<EDR> edrs;
        try {           
            cdr = cdrParser.parseByApi(cdrLine, currentUser.getUserName(), ip); 
            if(cdr.getRejectReason() != null) {
                log.error("cdr =" + (cdr != null ? cdr.getLine() : "") + ": " + cdr.getRejectReason());
                throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, cdr.getRejectReason());
            }
            List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
            edrs = cdrParser.convertCdrToEdr(cdr,accessPoints);
            for (EDR edr : edrs) {
                edrService.create(edr);
                try {
                    Reservation reservation = usageRatingService.reserveUsageWithinTransaction(edr);
                    if (edr.getRatingRejectionReason() != null) {
                        throw new MeveoApiException(edr.getRatingRejectionReason());
                    }
                    result.setReservationId(reservation.getId());
                    // schedule cancellation at expiry
                    TimerConfig timerConfig = new TimerConfig();
                    Object[] objs = { reservation.getId(), currentUser };
                    timerConfig.setInfo(objs);
                    Timer timer = timerService.createSingleActionTimer(appProvider.getPrepaidReservationExpirationDelayinMillisec(), timerConfig);
                    timers.put(reservation.getId(), timer);

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
        } catch (CDRParsingException e) {
            log.error("Error parsing cdr={}", e.getRejectionCause());
            throw new MeveoApiException(e.getRejectionCause().toString());
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
                try {
                    if (timers.containsKey(reservation.getId())) {
                        Timer timer = timers.get(reservation.getId());
                        timer.cancel();
                        timers.remove(reservation.getId());
                        log.debug("Canceled expiry timer for reservation {}, remains {} active timers", reservation.getId(), timers.size());
                    }
                } catch (Exception e1) {
                }
            } catch (BusinessException e) {
                log.error("Failed to confirm reservation ", e);
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            missingParameters.add("reservation");
            handleMissingParameters();
        }
    }

    private List<WalletOperation> rateUsage(EDR edr, ChargeCDRDto chargeCDRDto) throws MeveoApiException {

        List<WalletOperation> walletOperations = null;
        try {
            walletOperations = usageRatingService.rateUsageWithinTransaction(edr, chargeCDRDto.isVirtual(), chargeCDRDto.isRateTriggeredEdr(), chargeCDRDto.getMaxDepth(), 0);

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

        return walletOperations;
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
    
    /**
     * Save the cdr if the configuration property mediation.persistCDR is true.
     *
     * @param cdr the cdr
     */
    private void createOrUpdateCdr(CDR cdr) {
        boolean persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));
        if(cdr != null && persistCDR) {
            if(cdr.getId() == null) {
                cdrService.create(cdr);
            } else {
                cdrService.update(cdr);
            }                       
        }
    }
}
