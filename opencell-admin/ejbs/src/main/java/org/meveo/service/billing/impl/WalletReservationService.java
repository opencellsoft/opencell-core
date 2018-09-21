package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

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
         * Current balance - open or reserved
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
     * Calculate current balance. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and only one of them should be provided.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date for balance calculation
     * @param endDate End date for balance calculation
     * @return A current balance
     */
    public Amounts getCurrentBalance(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate) {

        return getBalanceAmount(seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, BalanceTypeEnum.CURRENT);
    }

    /**
     * Calculate reserved balance. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and only one of them should be provided.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date for balance calculation
     * @param endDate End date for balance calculation
     * @return A reserved balance
     */
    public Amounts getReservedBalance(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate) {

        return getBalanceAmount(seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, BalanceTypeEnum.RESERVED);
    }

    /**
     * Calculate open balance. Seller, customer, customer account, billing account and user account parameters are mutually exclusive and only one of them should be provided.
     * 
     * @param seller Seller
     * @param customer Customer
     * @param customerAccount Customer account
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param startDate Start date for balance calculation
     * @param endDate End date for balance calculation
     * @return An open balance
     */
    public Amounts getOpenBalance(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate) {

        return getBalanceAmount(seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, BalanceTypeEnum.OPEN);
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
        cal.setInitDate(subscriptionDate);
        startDate = cal.previousCalendarDate(subscriptionDate);
        endDate = cal.nextCalendarDate(subscriptionDate);

        BigDecimal ratedAmount = getCurrentBalance(seller, null, null, null, userAccount, startDate, endDate).getAmount(isWithTax);

        return ratedAmount;
    }

    public BigDecimal computeServicesSum(OfferTemplate offerTemplate, Seller seller, UserAccount userAccount, Date subscriptionDate, String param1, String param2, String param3,
            BigDecimal quantity, boolean isWithTax) throws BusinessException {
        BigDecimal servicesSum = new BigDecimal(0);

        for (OfferServiceTemplate st : offerTemplate.getOfferServiceTemplates()) {
            servicesSum = servicesSum.add(realtimeChargingService.getActivationServicePrice(seller, userAccount.getBillingAccount(), st.getServiceTemplate(), subscriptionDate,
                offerTemplate.getCode(), quantity, param1, param2, param3, isWithTax));
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
     * @param amountWithTax Amount with tax
     * @param mode Balance type
     * @return balance amount.
     */
    private Amounts getBalanceAmount(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate, BalanceTypeEnum mode) {

        Amounts result = new Amounts(BigDecimal.ZERO, BigDecimal.ZERO);

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
            strQuery.append("select new org.meveo.model.billing.Amounts(SUM(r.amountWithTax), SUM(r.amountWithoutTax)) from WalletOperation r " + "WHERE 1=1 ");

            if (startDate != null) {
                strQuery.append("AND r.operationDate>=:startDate ");
            }
            if (endDate != null) {
                strQuery.append("AND r.operationDate<:endDate ");
            }
            if (mode == BalanceTypeEnum.CURRENT) {
                strQuery.append("AND (r.status=:open OR r.status=:reserved) ");
            } else if (mode == BalanceTypeEnum.RESERVED) {
                strQuery.append("AND (r.status=:reserved) ");
            } else if (mode == BalanceTypeEnum.OPEN) {
                strQuery.append("AND (r.status=:open) ");
            }

            // + "AND (r.status=:open OR r.status=:treated) "
            switch (level) {
            case BILLING_ACCOUNT:
                strQuery.append("AND r.wallet.userAccount.billingAccount=:billingAccount ");
                break;
            case CUSTOMER:
                strQuery.append("AND r.wallet.userAccount.billingAccount.customerAccount.customer=:customer ");
                break;
            case CUSTOMER_ACCOUNT:
                strQuery.append("AND r.wallet.userAccount.billingAccount.customerAccount=:customerAccount ");
                break;
            case PROVIDER:
                break;
            case SELLER:
                strQuery.append("AND r.wallet.userAccount.billingAccount.customerAccount.customer.seller=:seller ");
                break;
            case USER_ACCOUNT:
                strQuery.append("AND r.wallet.userAccount=:userAccount ");
                break;
            default:
                break;
            }

            TypedQuery<Amounts> query = getEntityManager().createQuery(strQuery.toString(), Amounts.class);

            if (mode == BalanceTypeEnum.CURRENT) {
                query.setParameter("open", WalletOperationStatusEnum.OPEN);
                query.setParameter("reserved", WalletOperationStatusEnum.RESERVED);
            } else if (mode == BalanceTypeEnum.RESERVED) {
                query.setParameter("reserved", WalletOperationStatusEnum.RESERVED);
            } else if (mode == BalanceTypeEnum.OPEN) {
                query.setParameter("open", WalletOperationStatusEnum.OPEN);
            }
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
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