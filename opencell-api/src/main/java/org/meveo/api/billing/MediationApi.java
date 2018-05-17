package org.meveo.api.billing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.meveo.admin.parse.csv.CDR;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.notification.DefaultObserver;

/**
 * API for CDR processing and mediation handling in general
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
    private SubscriptionService subscriptionService;

    @Inject
    private DefaultObserver defaultObserver;

    Map<Long, Timer> timers = new HashMap<Long, Timer>();

    /**
     * Accepts a list of CDR line. This CDR is parsed and created as EDR. CDR is same format use in mediation job
     * 
     * @param postData String of CDRs
     */
    public void registerCdrList(CdrListDto postData) throws MeveoApiException, BusinessException {

        List<String> cdr = postData.getCdr();
        if (cdr != null && cdr.size() > 0) {
            try {
                cdrParsingService.initByApi(currentUser.getUserName(), postData.getIpAddress());
            } catch (BusinessException e1) {
                log.error("failed to init by api ");
                throw new MeveoApiException(e1.getMessage());
            }

            try {
                for (String line : cdr) {
                    List<EDR> edrs = cdrParsingService.getEDRList(line, CDRParsingService.CDR_ORIGIN_API);
                    for (EDR edr : edrs) {
                        log.debug("edr={}", edr);
                        edrService.create(edr);
                    }
                }
            } catch (CDRParsingException e) {
                log.error("Error parsing cdr={}", e);
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            if (cdr == null || cdr.size() == 0) {
                missingParameters.add("cdr");
            }

            handleMissingParameters();
        }
    }

    /**
     * Same as registerCdrList, but at the same process rate the EDR created
     * 
     * @param ip where request came from
     * 
     * @param cdr String of CDR
     */
    public void chargeCdr(String cdr, String ip) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(cdr)) {
            try {
                cdrParsingService.initByApi(currentUser.getUserName(), ip);
            } catch (BusinessException e1) {
                log.error("failed to init by api");
                throw new MeveoApiException(e1.getMessage());
            }
            List<EDR> edrs;
            try {
                edrs = cdrParsingService.getEDRList(cdr, CDRParsingService.CDR_ORIGIN_API);
                for (EDR edr : edrs) {
                    log.debug("edr={}", edr);
                    edr.setSubscription(subscriptionService.findById(edr.getSubscription().getId(), Arrays.asList("offer")));
                    edrService.create(edr);
                    try {
                        usageRatingService.rateUsageWithinTransaction(edr, false);
                        if (edr.getStatus() == EDRStatusEnum.REJECTED) {
                            log.error("edr rejected={}", edr.getRejectReason());
                            throw new MeveoApiException(edr.getRejectReason());
                        }
                    } catch (BusinessException e) {
                        if (e instanceof InsufficientBalanceException) {
                            log.error("edr rejected={}", edr.getRejectReason());
                            throw new MeveoApiException(MeveoApiErrorCodeEnum.INSUFFICIENT_BALANCE, e.getMessage());
                        } else {
                            log.error("Exception rating edr={}", e);
                            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, e.getMessage());
                        }
                    }
                }
            } catch (CDRParsingException e) {
                log.error("Error parsing cdr={}", e.getRejectionCause());
                throw new MeveoApiException(e.getRejectionCause().toString());
            }
        } else {
            missingParameters.add("cdr");
            handleMissingParameters();
        }
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
     * Allows the user to reserve a CDR, this will create a new reservation entity attached to a wallet operation. A reservation has expiration limit save in the provider entity
     * (PREPAID_RESRV_DELAY_MS)
     * 
     * @param cdr String of CDR
     * @param ip where request came from
     * @return Available quantity and reservationID is returned. if the reservation succeed then returns -1, else returns the available quantity for this cdr
     */
    public CdrReservationResponseDto reserveCdr(String cdr, String ip) throws MeveoApiException, BusinessException {
        CdrReservationResponseDto result = new CdrReservationResponseDto();
        // TODO: if insufficient balance retry with lower quantity
        result.setAvailableQuantity(-1);
        if (!StringUtils.isBlank(cdr)) {
            try {
                cdrParsingService.initByApi(currentUser.getUserName(), ip);
            } catch (BusinessException e1) {
                log.error("failed to init by api");
                throw new MeveoApiException(e1.getMessage());
            }
            List<EDR> edrs;
            try {
                edrs = cdrParsingService.getEDRList(cdr, CDRParsingService.CDR_ORIGIN_API);
                for (EDR edr : edrs) {
                    log.debug("edr={}", edr);
                    edrService.create(edr);
                    try {
                        Reservation reservation = usageRatingService.reserveUsageWithinTransaction(edr);
                        if (edr.getStatus() == EDRStatusEnum.REJECTED) {
                            log.error("edr rejected={}", edr.getRejectReason());
                            throw new MeveoApiException(edr.getRejectReason());
                        }
                        result.setReservationId(reservation.getId());
                        // schedule cancellation at expiry
                        TimerConfig timerConfig = new TimerConfig();
                        Object[] objs = { reservation.getId(), currentUser };
                        timerConfig.setInfo(objs);
                        Timer timer = timerService.createSingleActionTimer(appProvider.getPrepaidReservationExpirationDelayinMillisec(), timerConfig);
                        timers.put(reservation.getId(), timer);
                    } catch (BusinessException e) {
                        log.error("Exception rating edr={}", e);
                        if ("INSUFFICIENT_BALANCE".equals(e.getMessage())) {
                            throw new MeveoApiException(MeveoApiErrorCodeEnum.INSUFFICIENT_BALANCE, e.getMessage());
                        } else {
                            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, e.getMessage());
                        }

                    }
                }
            } catch (CDRParsingException e) {
                log.error("Error parsing cdr={}", e.getRejectionCause());
                throw new MeveoApiException(e.getRejectionCause().toString());
            }
        } else {
            missingParameters.add("cdr");
            handleMissingParameters();
        }
        return result;
    }

    /**
     * Confirms the reservation
     * 
     * @param reservationDto Prepaid reservation's data
     * @param ip where request came from
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
                    try {
                        usageRatingService.rateUsageWithinTransaction(edr, false);
                        if (edr.getStatus() == EDRStatusEnum.REJECTED) {
                            log.error("edr rejected={}", edr.getRejectReason());
                            throw new MeveoApiException(edr.getRejectReason());
                        }
                    } catch (BusinessException e) {
                        if (e instanceof InsufficientBalanceException) {
                            log.error("edr rejected={}", edr.getRejectReason());
                            throw new MeveoApiException(MeveoApiErrorCodeEnum.INSUFFICIENT_BALANCE, e.getMessage());
                        } else {
                            log.error("Exception rating edr={}", e);
                            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, e.getMessage());
                        }
                    }
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

    /**
     * Cancels the reservation
     * 
     * @param reservationDto Prepaid reservation's data
     * @param ip where request came from
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
