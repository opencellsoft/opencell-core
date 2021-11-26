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

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRListResponseDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto.CdrError;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.notification.DefaultObserver;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    Map<Long, Timer> timers = new HashMap<Long, Timer>();

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    @EJB
    private MediationApi thisNewTX;

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

        CSVCDRParser cdrParser = cdrParsingService.getCDRParser(currentUser.getUserName(), postData.getIpAddress());

        try {
            for (String line : cdrLines) {
                CDR cdr = cdrParser.parseCDR(line);
                cdrParsingService.createEdrs(cdr);
            }
        } catch (CDRParsingException e) {
            log.error("Error parsing cdr={}", e);
            throw new MeveoApiException(e.getMessage());
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
        CSVCDRParser cdrParser = cdrParsingService.getCDRParser(currentUser.getUserName(), null);

        try {
            CDR cdr = cdrParser.parseCDR(cdrLine);
            List<EDR> edrs = cdrParsingService.getEDRList(cdr);
            List<WalletOperation> walletOperations = new ArrayList<>();
            for (EDR edr : edrs) {
                log.debug("edr={}", edr);
                edr.setSubscription(edr.getSubscription());
                if (!chargeCDRDto.isVirtual()) {
                    edrService.create(edr);
                }
                List<WalletOperation> wo = rateUsage(edr, chargeCDRDto.isVirtual(), chargeCDRDto.isRateTriggeredEdr(), chargeCDRDto.getMaxDepth());
                if (wo != null) {
                    walletOperations.addAll(wo);
                }
            }

            return createChargeCDRResultDto(edrs, walletOperations, chargeCDRDto.isReturnWalletOperationDetails(), false, true);

        } catch (CDRParsingException e) {
            log.error("Error parsing cdr={}", e.getRejectionCause());
            throw new MeveoApiException(e.getRejectionCause().toString());
        }
    }

    /**
     * Register and rate EDRS
     *
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ChargeCDRListResponseDto chargeCdrList(ChargeCDRDto chargeCDRDto) throws MeveoApiException, BusinessException {

        if (chargeCDRDto.getCdrs() == null || chargeCDRDto.getCdrs().isEmpty()) {
            missingParameters.add("cdrs");
        }

        handleMissingParameters();

        ChargeCDRListResponseDto cdrListResult = new ChargeCDRListResponseDto(chargeCDRDto.getCdrs().size());

        CSVCDRParser cdrParser = cdrParsingService.getCDRParser(currentUser.getUserName(), null);

//        int nbThreads = Runtime.getRuntime().availableProcessors();
        int nbThreads = 1;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads);
        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        final SynchronizedIterator<String> cdrLineIterator = new SynchronizedIterator<String>(chargeCDRDto.getCdrs());

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName("MediationApi" + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                processCDRsInTx(cdrLineIterator, cdrParser, chargeCDRDto.isVirtual(), chargeCDRDto.isRateTriggeredEdr(), chargeCDRDto.getMaxDepth(), chargeCDRDto.isReturnWalletOperationDetails(), chargeCDRDto.isReturnWalletOperations(), chargeCDRDto.isReturnEDRs(), cdrListResult);

            });
        }

        for (Runnable task : tasks) {
            futures.add(executor.submit(task));
        }

        // Wait for all async methods to finish
        for (Future future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
//                wasKilled = true;

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                log.error("Failed to execute Mediation API async method", cause);
            }
        }

        return cdrListResult;
    }

    /**
     * Process CDRs in a new transaction
     * 
     * @param cdrLineIterator Iterator over CDRs to process
     * @param cdrParser CDR parser
     * @param isVirtual Is this is virtual charging - neither CDR, EDR nor wallet operations will be persisted
     * @param rateTriggeredEdrs In case of rating, shall Triggered EDRs be rated as well
     * @param maxDepth Max depth to rate of triggered EDRs
     * @param returnWalletOperationDetails Shall wallet operation details be returned
     * @param returnWalletOperations Shall wallet operation ids be returned
     * @param cdrProcessingResult CDR processing result tracking
     */
//    @JpaAmpNewTx
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processCDRsInTx(SynchronizedIterator<String> cdrLineIterator, CSVCDRParser cdrParser, boolean isVirtual, boolean rateTriggeredEdrs, Integer maxDepth, boolean returnWalletOperationDetails,
                boolean returnWalletOperations, boolean returnEDRs, ChargeCDRListResponseDto cdrProcessingResult) {

        while (true) {

            SynchronizedIterator<String>.NextItem<String> nextCDR = cdrLineIterator.nextWPosition();
            if (nextCDR == null) {
                break;
            }
            int position = nextCDR.getPosition();
            String cdrLine = nextCDR.getValue();

            thisNewTX.processEachCDRLineInNewTx(position, cdrParser, cdrLine, isVirtual, rateTriggeredEdrs, maxDepth,
                    returnWalletOperationDetails, returnWalletOperations, returnEDRs, cdrProcessingResult);

        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processEachCDRLineInNewTx(int position, CSVCDRParser cdrParser, String cdrLine, boolean isVirtual, boolean rateTriggeredEdrs,
                                Integer maxDepth, boolean returnWalletOperationDetails, boolean returnWalletOperations,
                                boolean returnEDRs, ChargeCDRListResponseDto cdrProcessingResult){
        try {
            CDR cdr = cdrParser.parseCDR(cdrLine);
            List<EDR> edrs = cdrParsingService.getEDRList(cdr);
            List<WalletOperation> walletOperations = new ArrayList<>();
            for (EDR edr : edrs) {
                log.debug("edr={}", edr);
                edr.setSubscription(edr.getSubscription());
                if (!isVirtual) {
                    edrService.create(edr);
                }
                List<WalletOperation> wo = rateUsage(edr, isVirtual, rateTriggeredEdrs, maxDepth);
                if (wo != null) {
                    walletOperations.addAll(wo);
                }
            }
            cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(edrs, walletOperations, returnWalletOperationDetails, returnWalletOperations, returnEDRs));
            cdrProcessingResult.getStatistics().addSuccess();

        } catch (CDRParsingException e) {
            log.error("Error parsing cdr={}", e.getRejectionCause());

            cdrProcessingResult.getStatistics().addFail();
            cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new CdrError(e.getClass().getSimpleName(), e.getRejectionCause() != null ? e.getRejectionCause().toString() : e.getMessage(), cdrLine)));

        } catch (MeveoApiException e) {
            log.error("Error Code Meveo Api={}", e.getErrorCode());

            cdrProcessingResult.getStatistics().addFail();
            cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new CdrError(e.getErrorCode().toString(), e.getCause() != null ? e.getCause().toString() : e.getMessage(), cdrLine)));

        }
    }

    private ChargeCDRResponseDto createChargeCDRResultDto(List<EDR> edrs, List<WalletOperation> walletOperations,
          boolean returnWalletOperationDetails, boolean returnWalletOperations, boolean returnEDRs) {

        ChargeCDRResponseDto result = new ChargeCDRResponseDto();

        if (edrs.get(0).getId() != null) {
            result.setEdrIds(new ArrayList<Long>(edrs.size()));
            if (returnEDRs) {
                for (EDR edr : edrs) {
                    result.getEdrIds().add(edr.getId());
                }
            }
        }
        if (walletOperations != null) {
            BigDecimal amountWithTax = BigDecimal.ZERO;
            BigDecimal amountWithoutTax = BigDecimal.ZERO;
            BigDecimal amountTax = BigDecimal.ZERO;
            for (WalletOperation walletOperation : walletOperations) {
                if (returnWalletOperationDetails) {
                    WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                    result.getWalletOperations().add(walletOperationDto);
                }
                else {
                    if (returnWalletOperations) {
                        WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                        result.getWalletOperations().add(new WalletOperationDto(walletOperationDto.getWalletId()));
                    }
                }
                amountWithTax = amountWithTax.add(walletOperation.getAmountWithTax() != null ? walletOperation.getAmountWithTax() : BigDecimal.ZERO);
                amountWithoutTax = amountWithoutTax.add(walletOperation.getAmountWithoutTax() != null ? walletOperation.getAmountWithoutTax() : BigDecimal.ZERO);
                amountTax = amountTax.add(walletOperation.getAmountTax() != null ? walletOperation.getAmountTax() : BigDecimal.ZERO);
            }
            if (returnWalletOperationDetails) {
                result.setWalletOperationCount(result.getWalletOperations().size());
            }
            result.setAmountTax(amountTax);
            result.setAmountWithoutTax(amountWithoutTax);
            result.setAmountWithTax(amountWithTax);
        }
        return result;
    }

    @Timeout
    private void reservationExpired(Timer timer) {
        Object[] objs = (Object[]) timer.getInfo();
        try {
            Reservation reservation = reservationService.findById((Long) objs[0]);
            reservationService.cancelPrepaidReservationInNewTransaction(reservation);
        } catch (BusinessException e) {
            log.error("Failed to cancel Prepaid Reservation In New Transaction", e);
        }
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

        CSVCDRParser cdrParser = cdrParsingService.getCDRParser(currentUser.getUserName(), ip);

        List<EDR> edrs;
        try {

            CDR cdr = cdrParser.parseCDR(cdrLine);

            edrs = cdrParsingService.getEDRList(cdr);
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
                    rateUsage(edr, false, false, null);
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

    private List<WalletOperation> rateUsage(EDR edr, boolean isVirtual, boolean rateTriggeredEdrs, Integer maxDepth) throws MeveoApiException {

        List<WalletOperation> walletOperations = null;
        try {
            walletOperations = usageRatingService.rateUsageWithinTransaction(edr, isVirtual, rateTriggeredEdrs, maxDepth, 0);

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
}
