package org.meveo.api;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.WalletReservationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.ReservationService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WalletReservationApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private ReservationService reservationService;

	public Long create(WalletReservationDto walletReservation)
			throws MeveoApiException {
		Provider provider = providerService.findByCode(walletReservation
				.getProviderCode());
		if (provider == null) {
			log.error("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
			throw new MeveoApiException("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
		} else {
			try {
				return reservationService.createReservation(em, provider,
						walletReservation.getSellerCode(),
						walletReservation.getOfferCode(),
						walletReservation.getUserAccountCode(),
						walletReservation.getSubscriptionDate(),
						walletReservation.getExpirationDate(),
						walletReservation.getCreditLimit(),
						walletReservation.getParam1(),
						walletReservation.getParam2(),
						walletReservation.getParam3());
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		}
	}

	public void update(WalletReservationDto walletReservation)
			throws MeveoApiException {
		Provider provider = providerService.findByCode(walletReservation
				.getProviderCode());
		if (provider == null) {
			log.error("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
			throw new MeveoApiException("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
		} else {
			try {
				reservationService.updateReservation(em,
						walletReservation.getReservationId(), provider,
						walletReservation.getSellerCode(),
						walletReservation.getOfferCode(),
						walletReservation.getUserAccountCode(),
						walletReservation.getSubscriptionDate(),
						walletReservation.getExpirationDate(),
						walletReservation.getCreditLimit(),
						walletReservation.getParam1(),
						walletReservation.getParam2(),
						walletReservation.getParam3());
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		}
	}

	public void cancel(Long reservationId) throws MeveoApiException {
		try {
			reservationService.cancelReservation(em, reservationId);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}
	}

	public BigDecimal confirm(WalletReservationDto walletReservation)
			throws MeveoApiException {
		Provider provider = providerService.findByCode(walletReservation
				.getProviderCode());
		if (provider == null) {
			log.error("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
			throw new MeveoApiException("Provider with code="
					+ walletReservation.getProviderCode() + " does not exists.");
		} else {
			try {
				return reservationService.confirmReservation(em,
						walletReservation.getReservationId(), provider,
						walletReservation.getSellerCode(),
						walletReservation.getOfferCode(),
						walletReservation.getSubscriptionDate(),
						walletReservation.getTerminationDate(),
						walletReservation.getParam1(),
						walletReservation.getParam2(),
						walletReservation.getParam3());
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		}
	}

}
