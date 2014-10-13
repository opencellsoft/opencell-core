package org.meveo.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.WalletReservationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Reservation;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.ReservationService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WalletReservationApi extends BaseApi {

	@Inject
	private ReservationService reservationService;

	public Long create(WalletReservationDto walletReservation, Provider provider)
			throws MeveoApiException {

		if (!StringUtils.isBlank(walletReservation.getSellerCode())
				&& !StringUtils.isBlank(walletReservation.getOfferCode())
				&& !StringUtils.isBlank(walletReservation.getUserAccountCode())
				&& walletReservation.getSubscriptionDate() != null
				&& walletReservation.getCreditLimit() != null) {

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
						walletReservation.getParam3(),
						walletReservation.isAmountWithTax());
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(walletReservation.getSellerCode())) {
				missingFields.add("sellerCode");
			}
			if (StringUtils.isBlank(walletReservation.getOfferCode())) {
				missingFields.add("offerCode");
			}
			if (StringUtils.isBlank(walletReservation.getUserAccountCode())) {
				missingFields.add("userAccountCode");
			}
			if (walletReservation.getSubscriptionDate() == null) {
				missingFields.add("subscriptionDate");
			}
			if (walletReservation.getCreditLimit() == null) {
				missingFields.add("creditLimit");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void update(WalletReservationDto walletReservation, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(walletReservation.getSellerCode())
				&& !StringUtils.isBlank(walletReservation.getOfferCode())
				&& !StringUtils.isBlank(walletReservation.getUserAccountCode())
				&& walletReservation.getSubscriptionDate() != null
				&& walletReservation.getCreditLimit() != null) {

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
						walletReservation.getParam3(),
						walletReservation.isAmountWithTax());
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (walletReservation.getReservationId() == null) {
				missingFields.add("reservationId");
			}
			if (StringUtils.isBlank(walletReservation.getSellerCode())) {
				missingFields.add("sellerCode");
			}
			if (StringUtils.isBlank(walletReservation.getOfferCode())) {
				missingFields.add("offerCode");
			}
			if (StringUtils.isBlank(walletReservation.getUserAccountCode())) {
				missingFields.add("userAccountCode");
			}
			if (walletReservation.getSubscriptionDate() == null) {
				missingFields.add("subscriptionDate");
			}
			if (walletReservation.getCreditLimit() == null) {
				missingFields.add("creditLimit");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void cancel(Long reservationId, Provider provider)
			throws MeveoApiException {
		Reservation reservation = reservationService.findById(reservationId);
		if (reservation == null) {
			throw new MeveoApiException("Reservation with id=" + reservationId
					+ " does not exists.");
		} else if (!reservation.getProvider().getCode()
				.equals(provider.getCode())) {
			throw new MeveoApiException("Reservation with id=" + reservationId
					+ " does not belong to current provider.");
		}

		try {
			reservationService.cancelReservation(em, reservation);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}
	}

	public BigDecimal confirm(WalletReservationDto walletReservation,
			Provider provider) throws MeveoApiException {
		if (walletReservation.getReservationId() != null
				&& !StringUtils.isBlank(walletReservation.getSellerCode())
				&& !StringUtils.isBlank(walletReservation.getOfferCode())
				&& walletReservation.getSubscriptionDate() != null
				&& walletReservation.getCreditLimit() != null) {

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

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (walletReservation.getReservationId() == null) {
				missingFields.add("reservationId");
			}
			if (StringUtils.isBlank(walletReservation.getSellerCode())) {
				missingFields.add("sellerCode");
			}
			if (StringUtils.isBlank(walletReservation.getOfferCode())) {
				missingFields.add("offerCode");
			}
			if (walletReservation.getSubscriptionDate() == null) {
				missingFields.add("subscriptionDate");
			}
			if (walletReservation.getCreditLimit() == null) {
				missingFields.add("creditLimit");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}
}
