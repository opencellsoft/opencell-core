package org.meveo.api.billing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.slf4j.Logger;

@Stateless
public class MediationApi extends BaseApi {

	@Inject
	private Logger log;

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

	Map<Long, Timer> timers = new HashMap<Long, Timer>();

	public void registerCdrList(CdrListDto postData, User currentUser) throws MeveoApiException {

		if (postData.getCdr() != null && postData.getCdr().size() > 0) {
			try {
				cdrParsingService.initByApi(currentUser.getUserName(), postData.getIpAddress());
			} catch (BusinessException e1) {
				log.error(e1.getMessage());
				throw new MeveoApiException(e1.getMessage());
			}

			try {
				for (String line : postData.getCdr()) {
					List<EDR> edrs = cdrParsingService.getEDRList(line, currentUser.getProvider());
					for (EDR edr : edrs) {
						log.debug("edr={}", edr);
						edrService.create(edr, currentUser, currentUser.getProvider());
					}
				}
			} catch (CDRParsingException e) {
				log.error("Error parsing cdr={}", e.getMessage());
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			if (postData.getCdr() == null || postData.getCdr().size() == 0) {
				missingParameters.add("cdr");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void chargeCdr(String cdr, User user, String ip) throws MeveoApiException {
		if (!StringUtils.isBlank(cdr)) {
			try {
				cdrParsingService.initByApi(user.getUserName(), ip);
			} catch (BusinessException e1) {
				log.error(e1.getMessage());
				throw new MeveoApiException(e1.getMessage());
			}
			List<EDR> edrs;
			try {
				edrs = cdrParsingService.getEDRList(cdr, user.getProvider());
				for (EDR edr : edrs) {
					log.debug("edr={}", edr);
					edrService.create(edr, user, user.getProvider());
					try {
						usageRatingService.rateUsageWithinTransaction(edr, user);
						if (edr.getStatus() == EDRStatusEnum.REJECTED) {
							log.error("edr rejected={}", edr.getRejectReason());
							throw new MeveoApiException(edr.getRejectReason());
						}
					} catch (BusinessException e) {
						log.error("Exception rating edr={}", e.getMessage());
						e.printStackTrace();
						if ("INSUFFICIENT_BALANCE".equals(e.getMessage())) {
							throw new MeveoApiException(MeveoApiErrorCode.INSUFFICIENT_BALANCE, e.getMessage());
						} else {
							throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, e.getMessage());
						}

					}
				}
			} catch (CDRParsingException e) {
				log.error("Error parsing cdr={}", e.getRejectionCause());
				throw new MeveoApiException(e.getRejectionCause().toString());
			}
		} else {
			missingParameters.add("cdr");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	@Timeout
	private void reservationExpired(Timer timer) {
		Object[] objs = (Object[]) timer.getInfo();
		try {
			Reservation reservation = reservationService.findById((Long) objs[0]);
			reservationService.cancelPrepaidReservationInNewTransaction(reservation);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	// if the reservation succeed then returns -1, else returns the available
	// quantity for this cdr
	public CdrReservationResponseDto reserveCdr(String cdr, User user, String ip) throws MeveoApiException {
		CdrReservationResponseDto result = new CdrReservationResponseDto();
		// TODO: if insufficient balance retry with lower quantity
		result.setAvailableQuantity(-1);
		if (!StringUtils.isBlank(cdr)) {
			try {
				cdrParsingService.initByApi(user.getUserName(), ip);
			} catch (BusinessException e1) {
				log.error(e1.getMessage());
				throw new MeveoApiException(e1.getMessage());
			}
			List<EDR> edrs;
			try {
				edrs = cdrParsingService.getEDRList(cdr, user.getProvider());
				for (EDR edr : edrs) {
					log.debug("edr={}", edr);
					edrService.create(edr, user, user.getProvider());
					try {
						Reservation reservation = usageRatingService.reserveUsageWithinTransaction(edr, user);
						if (edr.getStatus() == EDRStatusEnum.REJECTED) {
							log.error("edr rejected={}", edr.getRejectReason());
							throw new MeveoApiException(edr.getRejectReason());
						}
						result.setReservationId(reservation.getId());
						// schedule cancellation at expiry
						TimerConfig timerConfig = new TimerConfig();
						Object[] objs = { reservation.getId(), user };
						timerConfig.setInfo(objs);
						Timer timer = timerService.createSingleActionTimer(user.getProvider()
								.getPrepaidReservationExpirationDelayinMillisec(), timerConfig);
						timers.put(reservation.getId(), timer);
					} catch (BusinessException e) {
						log.error("Exception rating edr={}", e.getMessage());
						if ("INSUFFICIENT_BALANCE".equals(e.getMessage())) {
							throw new MeveoApiException(MeveoApiErrorCode.INSUFFICIENT_BALANCE, e.getMessage());
						} else {
							throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, e.getMessage());
						}

					}
				}
			} catch (CDRParsingException e) {
				log.error("Error parsing cdr={}", e.getRejectionCause());
				throw new MeveoApiException(e.getRejectionCause().toString());
			}
		} else {
			missingParameters.add("cdr");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		return result;
	}

	public void confirmReservation(PrepaidReservationDto reservationDto, User user, String ip) throws MeveoApiException {
		if (reservationDto.getReservationId() > 0) {
			try {
				Reservation reservation = reservationService.findById(reservationDto.getReservationId(),
						user.getProvider());
				if (reservation == null) {
					throw new BusinessException("CANNOT_FIND_RESERVATION");
				}
				if (reservation.getProvider().getId() != user.getProvider().getId()) {
					throw new BusinessException("NOT_YOUR_RESERVATION");
				}
				if (reservation.getStatus() != ReservationStatus.OPEN) {
					throw new BusinessException("RESERVATION_NOT_OPEN");
				}
				log.debug("compare dto qty {} and reserved qty {}", reservationDto.getConsumedQuantity()
						.toPlainString(), reservation.getQuantity().toPlainString());
				if (reservationDto.getConsumedQuantity().compareTo(reservation.getQuantity()) == 0) {
					reservationService.confirmPrepaidReservation(reservation);
				} else if (reservationDto.getConsumedQuantity().compareTo(reservation.getQuantity()) < 0) {
					reservationService.cancelPrepaidReservation(reservation);
					EDR edr = reservation.getOriginEdr();
					edr.setQuantity(reservationDto.getConsumedQuantity());
					try {
						usageRatingService.rateUsageWithinTransaction(edr, user);
						if (edr.getStatus() == EDRStatusEnum.REJECTED) {
							log.error("edr rejected={}", edr.getRejectReason());
							throw new MeveoApiException(edr.getRejectReason());
						}
					} catch (BusinessException e) {
						log.error("Exception rating edr={}", e.getMessage());
						if ("INSUFFICIENT_BALANCE".equals(e.getMessage())) {
							throw new MeveoApiException(MeveoApiErrorCode.INSUFFICIENT_BALANCE, e.getMessage());
						} else {
							throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, e.getMessage());
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
						log.debug("Canceled expiry timer for reservation {}, remains {} active timers",
								reservation.getId(), timers.size());
					}
				} catch (Exception e1) {
				}
			} catch (BusinessException e) {
				log.error(e.getMessage());
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			missingParameters.add("reservation");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void cancelReservation(PrepaidReservationDto reservationDto, User user, String ip) throws MeveoApiException {
		if (reservationDto.getReservationId() > 0) {
			try {
				Reservation reservation = reservationService.findById(reservationDto.getReservationId(),
						user.getProvider());
				if (reservation == null) {
					throw new BusinessException("CANNOT_FIND_RESERVATION");
				}
				if (reservation.getProvider().getId() != user.getProvider().getId()) {
					throw new BusinessException("NOT_YOUR_RESERVATION");
				}
				if (reservation.getStatus() != ReservationStatus.OPEN) {
					throw new BusinessException("RESERVATION_NOT_OPEN");
				}
				reservationService.cancelPrepaidReservation(reservation);
			} catch (BusinessException e) {
				e.printStackTrace();
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			missingParameters.add("reservation");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
