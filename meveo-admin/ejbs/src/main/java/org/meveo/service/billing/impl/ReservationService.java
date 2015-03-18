/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Edward P. Legaspi
 **/
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

	@Inject
	private WalletService walletService;
    
	@Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;
    	
    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

	// FIXME: rethink this service in term of prepaid wallets
	public Long createReservation(Provider provider, String sellerCode, String offerCode, String userAccountCode,
			Date subscriptionDate, Date expiryDate, BigDecimal creditLimit, String param1, String param2,
			String param3, boolean amountWithTax) throws BusinessException {

		// #1 Check the credit limit (servicesSum + getCurrentAmount) & return
		// error if KO.
		OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, provider);

		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		if (userAccount.getBillingAccount() == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode
					+ " does not have a billingAccount set.");
		}

		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with code=" + offerCode + " does not exists.");
		}

		if (offerTemplate.getServiceTemplates() == null || offerTemplate.getServiceTemplates().size() < 1) {
			throw new BusinessException("OfferTemplate doesn't have linked serviceTemplate.");
		}

		if (expiryDate == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 30);
			expiryDate = cal.getTime();
		}

		BigDecimal servicesSum = walletReservationService.computeServicesSum(offerTemplate, userAccount,
				subscriptionDate, param1, param2, param3, new BigDecimal(1));

		BigDecimal ratedAmount = walletReservationService.computeRatedAmount(provider, seller, userAccount,
				subscriptionDate);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		if (spentCredit.compareTo(creditLimit) > 0) {
			log.debug("Credit limit exceeded for seller code={}", seller.getCode());
			throw new BusinessException("Credit limit exception for seller with code=" + seller.getCode());
		}

		WalletInstance wallet = walletService.findByUserAccount(userAccount);

		// #2 Create a reservation (store UA), status=OPEN.
		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		Reservation reservation = new Reservation();
		reservation.setProvider(provider);
		reservation.setAuditable(auditable);
		reservation.setStatus(ReservationStatus.OPEN);
		reservation.setUserAccount(userAccount);
		reservation.setReservationDate(new Date());
		reservation.setExpiryDate(expiryDate);
		reservation.setWallet(wallet);
		if (amountWithTax) {
			reservation.setAmountWithTax(spentCredit);
		} else {
			reservation.setAmountWithoutTax(spentCredit);
		}

		// #3 Create the reserved wallet operation. Not associated to charge,
		// status=RESERVED, associated to the reservation, amount=servicesSum.
		TradingCurrency currency = userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency();
		WalletReservation walletReservation = new WalletReservation();
		walletReservation.setCode(sellerCode + "_" + userAccountCode + "_" + offerCode);
		walletReservation.setReservation(reservation);
		walletReservation.setStatus(WalletOperationStatusEnum.RESERVED);
		walletReservation.setSubscriptionDate(null);
		walletReservation.setOperationDate(new Date());
		walletReservation.setParameter1(param1);
		walletReservation.setParameter2(param2);
		walletReservation.setParameter3(param3);
		walletReservation.setChargeInstance(null);
		walletReservation.setSeller(userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
		walletReservation.setWallet(wallet);
		walletReservation.setQuantity(new BigDecimal(1));
		walletReservation.setStartDate(null);
		walletReservation.setEndDate(null);
		walletReservation.setCurrency(currency.getCurrency());
		walletReservation.setProvider(provider);
		if (amountWithTax) {
			walletReservation.setAmountWithTax(servicesSum);
		} else {
			walletReservation.setAmountWithoutTax(servicesSum);
		}

		walletReservationService.create(walletReservation, null, provider);

		// #4 Return the reservationId.
		return reservation.getId();
	}

	public void updateReservation(Long reservationId, Provider provider, String sellerCode, String offerCode,
			String userAccountCode, Date subscriptionDate, Date expiryDate, BigDecimal creditLimit, String param1,
			String param2, String param3, boolean amountWithTax) throws BusinessException {
		updateReservation(getEntityManager(), reservationId, provider, sellerCode, offerCode, userAccountCode,
				subscriptionDate, expiryDate, creditLimit, param1, param2, param3, amountWithTax);
	}

	public void updateReservation(EntityManager em, Long reservationId, Provider provider, String sellerCode,
			String offerCode, String userAccountCode, Date subscriptionDate, Date expiryDate, BigDecimal creditLimit,
			String param1, String param2, String param3, boolean amountWithTax) throws BusinessException {

		// #1 Check the credit limit (servicesSum + getCurrentAmount) & return
		// error if KO.
		OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, provider);

		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		if (userAccount.getBillingAccount() == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode
					+ " does not have a billingAccount set.");
		}

		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with code=" + offerCode + " does not exists.");
		}

		if (offerTemplate.getServiceTemplates() == null || offerTemplate.getServiceTemplates().size() < 1) {
			throw new BusinessException("OfferTemplate doesn't have linked serviceTemplate.");
		}

		if (expiryDate == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 30);
			expiryDate = cal.getTime();
		}

		BigDecimal servicesSum = walletReservationService.computeServicesSum(offerTemplate, userAccount,
				subscriptionDate, param1, param2, param3, new BigDecimal(1));

		BigDecimal ratedAmount = walletReservationService.computeRatedAmount(provider, seller, userAccount,
				subscriptionDate);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		if (spentCredit.compareTo(creditLimit) > 0) {
			log.debug("Credit limit exceeded for seller code={}", seller.getCode());
			throw new BusinessException("Credit limit exception for seller with code=" + seller.getCode());
		}

		// #2 Create a reservation (store UA), status=OPEN.
		Reservation reservation = findById(reservationId);
		if (reservation == null) {
			log.error("Reservation with id={} does not exists.", reservationId);
			throw new BusinessException("Reservation with id=" + reservationId + " does not exists.");
		}

		WalletInstance wallet = walletService.findByUserAccount(userAccount);

		reservation.getAuditable().setUpdated(new Date());
		reservation.setStatus(ReservationStatus.OPEN);
		reservation.setUserAccount(userAccount);
		reservation.setReservationDate(new Date());
		reservation.setExpiryDate(expiryDate);
		reservation.setWallet(wallet);
		if (amountWithTax) {
			reservation.setAmountWithTax(spentCredit);
			walletReservationService.updateSpendCredit(reservationId, servicesSum, true);
		} else {
			reservation.setAmountWithoutTax(spentCredit);
			walletReservationService.updateSpendCredit(reservationId, servicesSum, false);
		}
	}

	public void cancelReservation(Reservation reservation) throws BusinessException {
		cancelReservation(getEntityManager(), reservation);
	}

	public void cancelReservation(EntityManager em, Reservation reservation) throws BusinessException {

		// #1 Check that the reservation is OPEN, else KO.
		if (reservation.getStatus() != ReservationStatus.OPEN) {
			throw new BusinessException("Reservation with id=" + reservation.getId() + " is not "
					+ ReservationStatus.OPEN);
		}

		// #2 Set to CANCELLED the status of all the walletOperations linked to
		// reservation.
		walletReservationService.updateReservationStatus(reservation.getId(), WalletOperationStatusEnum.CANCELED);

		// #3 Set to CANCELLED the status of reservation.
		reservation.setStatus(ReservationStatus.CANCELLED);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cancelPrepaidReservationInNewTransaction(Reservation reservation) throws BusinessException {
		cancelPrepaidReservation(reservation);
	}

	public void cancelPrepaidReservation(Reservation reservation) throws BusinessException {
		// set to OPEN all reserved operation, this is different from postpaid
		// reservation process
		@SuppressWarnings("unchecked")
		List<WalletReservation> ops = getEntityManager().createNamedQuery("WalletReservation.listByReservationId")
				.setParameter("reservationId", reservation.getId()).getResultList();
		for (WalletReservation op : ops) {
			op.getAuditable().setUpdated(new Date());
			op.setStatus(WalletOperationStatusEnum.CANCELED);
			walletCacheContainerProvider.updateBalanceCache(op);
		}

		// restore all counters values
		if (reservation.getCounterPeriodValues().size() > 0) {
		    ratingCacheContainerProvider.restoreCounters(reservation.getCounterPeriodValues());
		}

		reservation.setStatus(ReservationStatus.CANCELLED);
	}

	public void confirmPrepaidReservation(Reservation reservation) throws BusinessException {
		// set to OPEN all reserved operation, this is different from postpaid
		// reservation process
		@SuppressWarnings("unchecked")
		List<WalletReservation> ops = getEntityManager().createNamedQuery("WalletReservation.listByReservationId")
				.setParameter("reservationId", reservation.getId()).getResultList();
		for (WalletReservation op : ops) {
			op.getAuditable().setUpdated(new Date());
			op.setStatus(WalletOperationStatusEnum.OPEN);
			walletCacheContainerProvider.updateBalanceCache(op);
		}
		reservation.setStatus(ReservationStatus.CONFIRMED);
	}

	public BigDecimal confirmReservation(Long reservationId, Provider provider, String sellerCode, String offerCode,
			Date subscriptionDate, Date terminationDate, String param1, String param2, String param3)
			throws BusinessException {
		return confirmReservation(getEntityManager(), reservationId, provider, sellerCode, offerCode, subscriptionDate,
				terminationDate, param1, param2, param3);
	}

	public BigDecimal confirmReservation(EntityManager em, Long reservationId, Provider provider, String sellerCode,
			String offerCode, Date subscriptionDate, Date terminationDate, String param1, String param2, String param3)
			throws BusinessException {
		Reservation reservation = findById(reservationId);
		if (reservation == null) {
			throw new BusinessException("Reservation with id=" + reservationId + " does not exists.");
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, provider);
		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with id=" + offerCode + " does not exists.");
		}

		// #1 Check that the reservation is OPEN, else KO.
		if (reservation.getStatus() != ReservationStatus.OPEN) {
			throw new BusinessException("Reservation with id=" + reservationId + " is not " + ReservationStatus.OPEN);
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		if (reservation.getUserAccount() == null) {
			throw new BusinessException("Reservation with id=" + reservationId + " doesn't have userAccount set.");
		}

		// #2 Set to TREATED all the walletOperations linked to reservation.
		walletReservationService.updateReservationStatus(reservationId, WalletOperationStatusEnum.TREATED);

		// #3 Set to CONFIRMED the reservation.
		reservation.setStatus(ReservationStatus.CONFIRMED);

		// #4 Create the subscription, set subscriptionId in reservation.
		Subscription subscription = new Subscription();
		subscription.setCode(sellerCode + "_" + reservation.getUserAccount().getCode() + "_" + offerCode);
		subscription.setOffer(offerTemplate);
		subscription.setUserAccount(reservation.getUserAccount());
		subscription.setStatusDate(new Date());
		subscription.setSubscriptionDate(subscriptionDate);
		subscription.setTerminationDate(terminationDate);
		subscriptionService.create(subscription, null, provider);

		reservation.setSubscription(subscription);

		// #5 Return for info, servicesSum - reservedAmount. Basically the
		// difference in amount when the credit is reserved and on the actual
		// date of the subscription.
		BigDecimal servicesSum = walletReservationService.computeServicesSum(offerTemplate,
				reservation.getUserAccount(), subscriptionDate, param1, param2, param3, new BigDecimal(1));

		BigDecimal ratedAmount = walletReservationService.computeRatedAmount(provider, seller,
				reservation.getUserAccount(), subscriptionDate);

		if (servicesSum != null && ratedAmount != null) {
			return servicesSum.subtract(ratedAmount);
		} else {
			if (servicesSum != null && ratedAmount == null) {
				return servicesSum;
			} else if (servicesSum == null && ratedAmount != null) {
				return ratedAmount;
			} else {
				return new BigDecimal(0);
			}
		}
	}

	public int updateExpiredReservation(Provider provider) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE " + Reservation.class.getName()
				+ " r SET r.status=:expiredStatus WHERE r.status=:openStatus AND r.expiryDate<:expiryDate");

		try {
			return getEntityManager().createQuery(sb.toString())
					.setParameter("expiredStatus", ReservationStatus.EXPIRED)
					.setParameter("openStatus", ReservationStatus.OPEN).setParameter("expiryDate", new Date())
					.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0;
		}
	}

}
