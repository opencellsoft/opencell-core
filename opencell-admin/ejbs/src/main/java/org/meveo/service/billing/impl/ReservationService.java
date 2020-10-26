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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.OfferTemplate;
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
    private CounterInstanceService counterInstanceService;

    // FIXME: rethink this service in term of prepaid wallets
    public Long createReservation(String sellerCode, String offerCode, String userAccountCode, Date subscriptionDate, Date expiryDate, BigDecimal creditLimit, String param1,
            String param2, String param3, boolean isAmountWithTax) throws BusinessException {

        // #1 Check the credit limit (servicesSum + getCurrentAmount) & return
        // error if KO.
        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, subscriptionDate);

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
        if (userAccount == null) {
            throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
        }

        Seller seller = sellerService.findByCode(sellerCode);
        if (seller == null) {
            throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
        }

        if (userAccount.getBillingAccount() == null) {
            throw new BusinessException("UserAccount with code=" + userAccountCode + " does not have a billingAccount set.");
        }

        if (offerTemplate == null) {
            throw new BusinessException("OfferTemplate with code=" + offerCode + " for date " + subscriptionDate + " does not exists.");
        }

        if (offerTemplate.getOfferServiceTemplates() == null || offerTemplate.getOfferServiceTemplates().size() < 1) {
            throw new BusinessException("OfferTemplate doesn't have linked serviceTemplate.");
        }

        if (expiryDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 30);
            expiryDate = cal.getTime();
        }

        BigDecimal servicesSum = walletReservationService.computeServicesSum(offerTemplate, seller, userAccount, subscriptionDate, param1, param2, param3, new BigDecimal(1),
            isAmountWithTax);

        BigDecimal ratedAmount = walletReservationService.computeRatedAmount(seller, userAccount, subscriptionDate, isAmountWithTax);

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
        reservation.setAuditable(auditable);
        reservation.setStatus(ReservationStatus.OPEN);
        reservation.setUserAccount(userAccount);
        reservation.setReservationDate(new Date());
        reservation.setExpiryDate(expiryDate);
        reservation.setWallet(wallet);
        if (isAmountWithTax) {
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
        walletReservation.setSubscriptionDate(null);
        walletReservation.setOperationDate(new Date());
        walletReservation.setParameter1(param1);
        walletReservation.setParameter2(param2);
        walletReservation.setParameter3(param3);
        walletReservation.setChargeInstance(null);
        walletReservation.setSeller(seller);
        walletReservation.setWallet(wallet);
        walletReservation.setQuantity(new BigDecimal(1));
        walletReservation.setStartDate(null);
        walletReservation.setEndDate(null);
        walletReservation.setCurrency(currency.getCurrency());
        if (isAmountWithTax) {
            walletReservation.setAmountWithTax(servicesSum);
        } else {
            walletReservation.setAmountWithoutTax(servicesSum);
        }

        walletReservationService.create(walletReservation);

        // #4 Return the reservationId.
        return reservation.getId();
    }

    public void updateReservation(Long reservationId, String sellerCode, String offerCode, String userAccountCode, Date subscriptionDate, Date expiryDate, BigDecimal creditLimit,
            String param1, String param2, String param3, boolean isAmountWithTax) throws BusinessException {

        // #1 Check the credit limit (servicesSum + getCurrentAmount) & return
        // error if KO.
        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, subscriptionDate);

        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
        if (userAccount == null) {
            throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
        }

        Seller seller = sellerService.findByCode(sellerCode);
        if (seller == null) {
            throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
        }

        if (userAccount.getBillingAccount() == null) {
            throw new BusinessException("UserAccount with code=" + userAccountCode + " does not have a billingAccount set.");
        }

        if (offerTemplate == null) {
            throw new BusinessException("OfferTemplate with code=" + offerCode + " for date " + subscriptionDate + " does not exists.");
        }

        if (offerTemplate.getOfferServiceTemplates() == null || offerTemplate.getOfferServiceTemplates().size() < 1) {
            throw new BusinessException("OfferTemplate doesn't have linked serviceTemplate.");
        }

        if (expiryDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 30);
            expiryDate = cal.getTime();
        }

        BigDecimal servicesSum = walletReservationService.computeServicesSum(offerTemplate, seller, userAccount, subscriptionDate, param1, param2, param3, new BigDecimal(1),
            isAmountWithTax);

        BigDecimal ratedAmount = walletReservationService.computeRatedAmount(seller, userAccount, subscriptionDate, isAmountWithTax);

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
        if (isAmountWithTax) {
            reservation.setAmountWithTax(spentCredit);
            walletReservationService.updateSpendCredit(reservationId, servicesSum, true);
        } else {
            reservation.setAmountWithoutTax(spentCredit);
            walletReservationService.updateSpendCredit(reservationId, servicesSum, false);
        }
    }

    public void cancelReservation(Reservation reservation) throws BusinessException {

        // #1 Check that the reservation is OPEN, else KO.
        if (reservation.getStatus() != ReservationStatus.OPEN) {
            throw new BusinessException("Reservation with id=" + reservation.getId() + " is not " + ReservationStatus.OPEN);
        }

        // #2 Set to CANCELLED the status of all the walletOperations linked to
        // reservation.
        walletReservationService.updateReservationStatus(reservation.getId(), WalletOperationStatusEnum.CANCELED);

        // #3 Set to CANCELLED the status of reservation.
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cancelPrepaidReservationInNewTransaction(Reservation reservation) throws BusinessException {
        cancelPrepaidReservation(reservation);
    }

    public void cancelPrepaidReservation(Reservation reservation) throws BusinessException {
        // set to OPEN all reserved operation, this is different from postpaid
        // reservation process
        @SuppressWarnings("unchecked")
        List<WalletReservation> ops = getEntityManager().createNamedQuery("WalletReservation.listByReservationId").setParameter("reservationId", reservation.getId())
            .getResultList();

        for (WalletReservation wo : ops) {
            wo.changeStatus(WalletOperationStatusEnum.CANCELED);            
            walletCacheContainerProvider.updateBalance(wo);
        }

        // restore all counters values
        if (reservation.getCounterPeriodValues().size() > 0) {

            for (Entry<Long, BigDecimal> periodInfo : reservation.getCounterPeriodValues().entrySet()) {
                counterInstanceService.incrementCounterValue(periodInfo.getKey(), periodInfo.getValue(), reservation);
            }
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    public void confirmPrepaidReservation(Reservation reservation) throws BusinessException {
        // set to OPEN all reserved operation, this is different from postpaid
        // reservation process
        @SuppressWarnings("unchecked")
        List<WalletReservation> ops = getEntityManager().createNamedQuery("WalletReservation.listByReservationId").setParameter("reservationId", reservation.getId())
            .getResultList();

        for (WalletReservation wo : ops) {
            if (wo.getStatus() != WalletOperationStatusEnum.OPEN) {
                wo.changeStatus(WalletOperationStatusEnum.OPEN);
                if(wo.getChargeInstance()!=null && wo.getChargeInstance().getCounterInstances()!=null) {
                	for(CounterInstance counterInstance: wo.getChargeInstance().getCounterInstances()) {
                		CounterPeriod counterPeriod = counterInstanceService.getCounterPeriod(counterInstance, wo.getOperationDate());
                		counterInstanceService.accumulatorCounterPeriodValue(counterPeriod, wo, null, false);
                	}
                }
            }
            walletCacheContainerProvider.updateBalance(wo);
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    public BigDecimal confirmReservation(Long reservationId, String sellerCode, String offerCode, Date subscriptionDate, Date terminationDate, String param1, String param2,
            String param3, boolean isAmountWithTax) throws BusinessException {

        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("Reservation with id=" + reservationId + " does not exists.");
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, subscriptionDate);
        if (offerTemplate == null) {
            throw new BusinessException("OfferTemplate with code " + offerCode + " for date " + subscriptionDate + " does not exists.");
        }

        // #1 Check that the reservation is OPEN, else KO.
        if (reservation.getStatus() != ReservationStatus.OPEN) {
            throw new BusinessException("Reservation with id=" + reservationId + " is not " + ReservationStatus.OPEN);
        }

        Seller seller = sellerService.findByCode(sellerCode);
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
        subscription.setSeller(seller);
        subscriptionService.create(subscription);

        reservation.setSubscription(subscription);

        // #5 Return for info, servicesSum - reservedAmount. Basically the
        // difference in amount when the credit is reserved and on the actual
        // date of the subscription.
        BigDecimal servicesSum = walletReservationService.computeServicesSum(offerTemplate, seller, reservation.getUserAccount(), subscriptionDate, param1, param2, param3,
            new BigDecimal(1), isAmountWithTax);

        BigDecimal ratedAmount = walletReservationService.computeRatedAmount(seller, reservation.getUserAccount(), subscriptionDate, isAmountWithTax);

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

    public int updateExpiredReservation() {
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE " + Reservation.class.getName() + " r SET r.status=:expiredStatus WHERE r.status=:openStatus AND r.expiryDate<:expiryDate ");

        try {
            return getEntityManager().createQuery(sb.toString()).setParameter("expiredStatus", ReservationStatus.EXPIRED).setParameter("openStatus", ReservationStatus.OPEN)
                .setParameter("expiryDate", new Date())

                .executeUpdate();
        } catch (Exception e) {
            log.error("failed to update expired reservation", e);
            return 0;
        }
    }

}
