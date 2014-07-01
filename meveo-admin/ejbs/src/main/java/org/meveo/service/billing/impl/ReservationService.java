package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;

@Stateless
public class ReservationService extends PersistenceService<Reservation> {

	@Inject
	private WalletReservationService walletReservationService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private SellerService sellerService;

	public Long createReservation(EntityManager em, Provider provider,
			String sellerCode, String offerCode, String userAccountCode,
			Date subscriptionDate, Date expiryDate, BigDecimal creditLimit,
			String param1, String param2, String param3)
			throws BusinessException {

		// #1 Check the credit limit (servicesSum + getCurrentAmount) & return
		// error if KO.
		OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
				offerCode, provider);

		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		if (userAccount.getBillingAccount() == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not have a billingAccount set.");
		}

		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with code=" + offerCode
					+ " does not exists.");
		}

		if (offerTemplate.getServiceTemplates() == null
				|| offerTemplate.getServiceTemplates().size() < 1) {
			throw new BusinessException(
					"OfferTemplate doesn't have linked serviceTemplate.");
		}

		if (expiryDate == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 30);
			expiryDate = cal.getTime();
		}

		BigDecimal spentCredit = walletReservationService.getSpentCredit(em,
				provider, seller, offerTemplate, userAccount, subscriptionDate,
				param1, param2, param3);

		if (spentCredit.compareTo(creditLimit) > 0) {
			log.debug("Credit limit exceeded for seller code={}",
					seller.getCode());
			throw new BusinessException(
					"Credit limit exception for seller with code="
							+ seller.getCode());
		}

		// #2 Create a reservation (store UA), status=OPEN.
		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		Reservation reservation = new Reservation();
		reservation.setAuditable(auditable);
		reservation.setStatus(ReservationStatus.OPEN);
		reservation.setUserAccount(userAccount);
		reservation.setReservationDate(new Date());
		reservation.setExpiryDate(expiryDate);
		reservation.setWallet(userAccount.getWallet());
		reservation.setAmountWithoutTax(spentCredit);

		// #3 Create the reserved wallet operation. Not associated to charge,
		// status=RESERVED, associated to the reservation, amount=servicesSum.
		TradingCurrency currency = userAccount.getBillingAccount()
				.getCustomerAccount().getTradingCurrency();
		WalletReservation walletReservation = new WalletReservation();
		walletReservation.setCode(sellerCode + "_" + userAccountCode + "_"
				+ offerCode + "_" + reservation.getId());
		walletReservation.setReservation(reservation);
		walletReservation.setStatus(WalletOperationStatusEnum.RESERVED);

		walletReservation.setSubscriptionDate(null);
		walletReservation.setOperationDate(new Date());
		walletReservation.setParameter1(param1);
		walletReservation.setParameter2(param2);
		walletReservation.setParameter3(param3);
		walletReservation.setChargeInstance(null);
		walletReservation.setSeller(userAccount.getBillingAccount()
				.getCustomerAccount().getCustomer().getSeller());
		walletReservation.setWallet(userAccount.getWallet());
		walletReservation.setQuantity(new BigDecimal(1));
		walletReservation.setStartDate(null);
		walletReservation.setEndDate(null);
		walletReservation.setCurrency(currency.getCurrency());
		walletReservation.setProvider(provider);
		walletReservationService.create(em, walletReservation, null, provider);

		// #4 Return the reservationId.
		return reservation.getId();
	}

	public void updateReservation(EntityManager em, Long reservationId,
			Provider provider, String sellerCode, String offerCode,
			String userAccountCode, Date subscriptionDate, Date expiryDate,
			BigDecimal creditLimit, String param1, String param2, String param3)
			throws BusinessException {

		// #1 Check the credit limit (servicesSum + getCurrentAmount) & return
		// error if KO.
		OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
				offerCode, provider);

		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		if (userAccount.getBillingAccount() == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not have a billingAccount set.");
		}

		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with code=" + offerCode
					+ " does not exists.");
		}

		if (offerTemplate.getServiceTemplates() == null
				|| offerTemplate.getServiceTemplates().size() < 1) {
			throw new BusinessException(
					"OfferTemplate doesn't have linked serviceTemplate.");
		}

		if (expiryDate == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 30);
			expiryDate = cal.getTime();
		}

		BigDecimal spentCredit = walletReservationService.getSpentCredit(em,
				provider, seller, offerTemplate, userAccount, subscriptionDate,
				param1, param2, param3);

		if (spentCredit.compareTo(creditLimit) > 0) {
			log.debug("Credit limit exceeded for seller code={}",
					seller.getCode());
			throw new BusinessException(
					"Credit limit exception for seller with code="
							+ seller.getCode());
		}

		// #2 Create a reservation (store UA), status=OPEN.
		Reservation reservation = findById(em, reservationId);
		if (reservation == null) {
			log.error("Reservation with id={} does not exists.", reservationId);
			throw new BusinessException("Reservation with id=" + reservationId
					+ " does not exists.");
		}

		Auditable auditable = new Auditable();
		auditable.setUpdated(new Date());
		reservation.setAuditable(auditable);
		reservation.setStatus(ReservationStatus.OPEN);
		reservation.setUserAccount(userAccount);
		reservation.setReservationDate(new Date());
		reservation.setExpiryDate(expiryDate);
		reservation.setWallet(userAccount.getWallet());
		reservation.setAmountWithoutTax(spentCredit);
	}

	public void cancelReservation(EntityManager em, Long reservationId)
			throws BusinessException {
		Reservation reservation = findById(reservationId);
		if (reservation == null) {
			throw new BusinessException("Reservation with id=" + reservationId
					+ " does not exists.");
		}

		// #1 Check that the reservation is OPEN, else KO.
		if (reservation.getStatus() != ReservationStatus.OPEN) {
			throw new BusinessException("Reservation with id=" + reservationId
					+ " is not " + ReservationStatus.OPEN);
		}

		// #2 Set to CANCELLED the status of all the walletOperations linked to
		// reservation.
		walletReservationService.updateReservationStatus(em, reservationId,
				WalletOperationStatusEnum.CANCELED);

		// #3 Set to CANCELLED the status of reservation.
		reservation.setStatus(ReservationStatus.CANCELLED);
	}

	public BigDecimal confirmCredit(EntityManager em, Provider provider,
			Seller seller, Long reservationId, String offerId,
			String userAccountCode, Date subscriptionDate,
			Date terminationDate, String param1, String param2, String param3)
			throws BusinessException {
		Reservation reservation = findById(em, reservationId);
		if (reservation == null) {
			throw new BusinessException("Reservation with id=" + reservationId
					+ " does not exists.");
		}

		UserAccount userAccount = userAccountService.findByCode(em,
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(em,
				offerId, provider);
		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with id=" + offerId
					+ " does not exists.");
		}

		// #1 Check that the reservation is OPEN, else KO.
		if (reservation.getStatus() != ReservationStatus.OPEN) {
			throw new BusinessException("Reservation with id=" + reservationId
					+ " is not " + ReservationStatus.OPEN);
		}

		// #2 Set to TREATED all the walletOperations linked to reservation.
		walletReservationService.updateReservationStatus(em, reservationId,
				WalletOperationStatusEnum.TREATED);

		// #3 Set to CONFIRMED the reservation.
		reservation.setStatus(ReservationStatus.CONFIRMED);

		// #4 Create the subscription, set subscriptionId in reservation.
		Subscription subscription = new Subscription();
		subscription.setOffer(offerTemplate);
		subscription.setUserAccount(userAccount);
		subscription.setStatusDate(new Date());
		subscription.setSubscriptionDate(subscriptionDate);
		subscription.setTerminationDate(terminationDate);
		subscriptionService.create(em, subscription, null, provider);

		reservation.setSubscription(subscription);

		// #5 Return for info, servicesSum - reservedAmount. Basically the
		// difference in amount when the credit is reserved and on the actual
		// date of the subscription.
		BigDecimal spentCredit = walletReservationService.getSpentCredit(em,
				provider, seller, offerTemplate, userAccount, subscriptionDate,
				param1, param2, param3);

		if (reservation.getAmountWithoutTax() != null && spentCredit != null) {
			return spentCredit.subtract(spentCredit);
		} else {
			if (reservation.getAmountWithoutTax() != null) {
				return reservation.getAmountWithoutTax();
			}
			if (spentCredit != null) {
				return spentCredit;
			}
			return new BigDecimal(0);
		}
	}

}
