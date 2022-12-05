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
import java.util.Date;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.CalendarService;

/**
 * Service class for WalletReservation entity
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0.1
 */
@Stateless
public class WalletReservationService extends PersistenceService<WalletReservation> {

    @Inject
    private RealtimeChargingService realtimeChargingService;

    @Inject
    private CalendarService calendarService;

    /**
     * Balance calculation type
     */
    private enum BalanceTypeEnum {

        /**
         * Available balance - open or reserved
         */
        CURRENT,

        /**
         * Reserved balance
         */
        RESERVED,

        /**
         * Open balance
         */
        OPEN;
    }

    /**
     * Calculate available balance (open or reserved) of prepaid wallets. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and
     * only one of them should be provided. walletId and walletCode parameters are mutually exclusive and are optional.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date for balance calculation
     * @param endDate End date for balance calculation
     * @param walletId Wallet identifier - optional
     * @param walletCode Wallet code - optional
     * @return A current balance
     */
    public Amounts getCurrentBalance(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate, Long walletId, String walletCode) {

        return getBalanceAmount(seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, walletId, walletCode, BalanceTypeEnum.CURRENT);
    }

    /**
     * Calculate reserved balance of prepaid wallets. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and only one of them
     * should be provided. walletId and walletCode parameters are mutually exclusive and are optional.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date for balance calculation
     * @param endDate End date for balance calculation
     * @param walletId Wallet identifier - optional
     * @param walletCode Wallet code - optional
     * @return A reserved balance
     */
    public Amounts getReservedBalance(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate, Long walletId, String walletCode) {

        return getBalanceAmount(seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, walletId, walletCode, BalanceTypeEnum.RESERVED);
    }

    /**
     * Calculate open balance of prepaid wallets. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and only one of them should
     * be provided. walletId and walletCode parameters are mutually exclusive and are optional.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date for balance calculation
     * @param endDate End date for balance calculation
     * @param walletId Wallet identifier - optional
     * @param walletCode Wallet code - optional
     * @return An open balance
     */
    public Amounts getOpenBalance(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate, Long walletId, String walletCode) {

        return getBalanceAmount(seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, walletId, walletCode, BalanceTypeEnum.OPEN);
    }

    public void updateReservationStatus(Long reservationId, WalletOperationStatusEnum status) {
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE WalletReservation w SET w.status=:status, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");

        try {
            getEntityManager().createQuery(sb.toString()).setParameter("updated", new Date()).setParameter("status", status).setParameter("reservationId", reservationId)
                .executeUpdate();
        } catch (Exception e) {
            log.error("failed to update reservation status", e);
        }
    }

    public BigDecimal getSpentCredit(Seller seller, OfferTemplate offerTemplate, UserAccount userAccount, Date subscriptionDate, String param1, String param2, String param3,
            BigDecimal quantity, boolean isWithTax) throws BusinessException {

        BigDecimal servicesSum = computeServicesSum(offerTemplate, seller, userAccount, subscriptionDate, param1, param2, param3, quantity, isWithTax);

        BigDecimal ratedAmount = computeRatedAmount(seller, userAccount, subscriptionDate, isWithTax);

        BigDecimal spentCredit = servicesSum.add(ratedAmount);

        return spentCredit;
    }

    public BigDecimal computeRatedAmount(Seller seller, UserAccount userAccount, Date subscriptionDate, boolean isWithTax) {
        Date startDate = null;
        Date endDate = null;

        Calendar cal = calendarService.findByCode(paramBeanFactory.getInstance().getProperty("default.calendar.monthly", "MONTHLY"));
        cal = CalendarService.initializeCalendar(cal, subscriptionDate, userAccount);

        startDate = cal.previousCalendarDate(subscriptionDate);
        endDate = cal.nextCalendarDate(subscriptionDate);

        BigDecimal ratedAmount = getCurrentBalance(seller, null, null, null, userAccount, startDate, endDate, null, null).getAmount(isWithTax);

        return ratedAmount;
    }

    public BigDecimal computeServicesSum(OfferTemplate offerTemplate, Seller seller, UserAccount userAccount, Date subscriptionDate, String param1, String param2, String param3,
            BigDecimal quantity, boolean isWithTax) throws BusinessException {
        BigDecimal servicesSum = new BigDecimal(0);

        for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
            servicesSum = servicesSum.add(realtimeChargingService.getActivationServicePrice(seller, userAccount.getBillingAccount(), st.getServiceTemplate(), subscriptionDate,
                offerTemplate, quantity, param1, param2, param3, isWithTax));
        }

        return servicesSum;
    }

    public void updateSpendCredit(Long reservationId, BigDecimal amount, boolean amountWithTax) {
        StringBuilder sb = new StringBuilder();

        if (amountWithTax) {
            sb.append("UPDATE WalletReservation w SET w.amountWithTax=:amount, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");
        } else {
            sb.append("UPDATE WalletReservation w SET w.amountWithoutTax=:amount, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");
        }

        try {
            getEntityManager().createQuery(sb.toString()).setParameter("updated", new Date()).setParameter("amount", amount).setParameter("reservationId", reservationId)
                .executeUpdate();
        } catch (Exception e) {
            log.error("failed to updateSpendCredit", e);
        }
    }

    /**
     * Get wallet operation balance. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and only one of them should be provided.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date
     * @param endDate End date
     * @param walletId Wallet identifier
     * @param walletCode Wallet code
     * @param mode Balance type
     * @return balance amount.
     */
    private Amounts getBalanceAmount(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate, Long walletId, String walletCode, BalanceTypeEnum mode) {

        Amounts result = new Amounts();

        LevelEnum level = LevelEnum.PROVIDER;

        if (userAccount != null) {
            level = LevelEnum.USER_ACCOUNT;
        } else if (billingAccount != null) {
            level = LevelEnum.BILLING_ACCOUNT;
        } else if (customerAccount != null) {
            level = LevelEnum.CUSTOMER_ACCOUNT;
        } else if (customer != null) {
            level = LevelEnum.CUSTOMER;
        } else if (seller != null) {
            level = LevelEnum.SELLER;
        }

        try {
            StringBuilder strQuery = new StringBuilder();
            strQuery.append("select new org.meveo.model.billing.Amounts(SUM(r.amountWithoutTax), SUM(r.amountWithTax)) from WalletOperation r WHERE 1=1 ");

            if (startDate != null) {
                strQuery.append(" AND r.operationDate>=:startDate ");
            }
            if (endDate != null) {
                strQuery.append(" AND r.operationDate<:endDate ");
            }
            if (mode == BalanceTypeEnum.CURRENT) {
                strQuery.append(" AND (r.status='OPEN' OR r.status='RESERVED' OR r.status='TREATED') ");
            } else if (mode == BalanceTypeEnum.RESERVED) {
                strQuery.append(" AND r.status='RESERVED' ");
            } else if (mode == BalanceTypeEnum.OPEN) {
                strQuery.append(" AND (r.status='OPEN' OR r.status='TREATED')  ");
            }
            if (walletId != null) {
                strQuery.append(" AND r.wallet.id=:walletId ");
            } else {
                strQuery.append(" AND r.wallet.walletTemplate.walletType='PREPAID' ");
            }
            if (walletCode != null) {
                strQuery.append(" AND r.wallet.code =:walletCode ");
            }

            switch (level) {
            case BILLING_ACCOUNT:
                strQuery.append(" AND r.wallet.userAccount.billingAccount=:billingAccount ");
                break;
            case CUSTOMER:
                strQuery.append(" AND r.wallet.userAccount.billingAccount.customerAccount.customer=:customer ");
                break;
            case CUSTOMER_ACCOUNT:
                strQuery.append(" AND r.wallet.userAccount.billingAccount.customerAccount=:customerAccount ");
                break;
            case PROVIDER:
                break;
            case SELLER:
                strQuery.append(" AND r.seller=:seller ");
                break;
            case USER_ACCOUNT:
                strQuery.append(" AND r.wallet.userAccount=:userAccount ");
                break;
            default:
                break;
            }

            TypedQuery<Amounts> query = getEntityManager().createQuery(strQuery.toString(), Amounts.class);

            if (walletId != null) {
                query.setParameter("walletId", walletId);
            }
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            if (walletCode != null) {
                query.setParameter("walletCode", walletCode);
            }

            switch (level) {
            case BILLING_ACCOUNT:
                query.setParameter("billingAccount", billingAccount);
                break;
            case CUSTOMER:
                query.setParameter("customer", customer);
                break;
            case CUSTOMER_ACCOUNT:
                query.setParameter("customerAccount", customerAccount);
                break;
            case PROVIDER:
                break;
            case SELLER:
                query.setParameter("seller", seller);
                break;
            case USER_ACCOUNT:
                query.setParameter("userAccount", userAccount);
                break;
            default:
                break;
            }

            result = query.getSingleResult();

        } catch (Exception e) {
            log.error("Failed to get balance amount ", e);
        }

        return result;
    }
}