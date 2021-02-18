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

package org.meveo.service.order;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.quote.Quote;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class OrderService extends BusinessService<Order> {

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    @Inject
    private PaymentMethodService paymentMethodService;

    @Inject
    private UserAccountService userAccountService;

    public Long countNewOrders(Calendar endDate) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(endDate.getTime());
        startDate.add(Calendar.DATE, -1);

        String sqlQuery = "select count(*) from " + Order.class.getName() + " a where a.status = :orderStatus AND a.auditable.created <= :endDate AND a.auditable.created > :startDate";
        Query query = getEntityManager().createQuery(sqlQuery);

        query.setParameter("orderStatus", OrderStatusEnum.ACKNOWLEDGED);
        query.setParameter("endDate", endDate.getTime());
        query.setParameter("startDate", startDate.getTime());
        Long count = (Long) query.getSingleResult();

        return count.longValue();
    }

    public Long countPendingOrders(Calendar startDate) {
        startDate.add(Calendar.DATE, -1);
        Query query = getEntityManager().createQuery("select count(*) from " + Order.class.getName() + " a where a.status = :orderStatus AND a.auditable.created < :startDate");
        query.setParameter("orderStatus", OrderStatusEnum.ACKNOWLEDGED);
        query.setParameter("startDate", startDate.getTime());
        Long count = (Long) query.getSingleResult();

        return count.longValue();
    }

    public Order routeToUserGroup(Order entity, String userGroupCode) throws BusinessException {
        UserHierarchyLevel userHierarchyLevel = userHierarchyLevelService.findByCode(userGroupCode);
        if (userHierarchyLevel == null) {
            log.trace("No UserHierarchyLevel found {}/{}", entity, userGroupCode);
        }
        entity.setRoutedToUserGroup(userHierarchyLevel);
        return this.update(entity);
    }

    @SuppressWarnings("unchecked")
    public List<Order> findByExternalId(String externalId) throws BusinessException {
        Query query = getEntityManager().createQuery("from " + Order.class.getName() + " where externalId = :code ");
        query.setParameter("code", externalId);
        return query.getResultList();
    }

    /**
     * Get a list of order with matching code or external identifier
     * 
     * @param codeOrExternalId Code or external identifier
     * @return A list of orders
     */
    public List<Order> findByCodeOrExternalId(Collection<String> codeOrExternalId) {

        TypedQuery<Order> query = getEntityManager().createNamedQuery("Order.listByCodeOrExternalId", Order.class).setParameter("code", codeOrExternalId);

        return query.getResultList();
    }

    /**
     * Find an order with matching code or external identifier
     * 
     * @param codeOrExternalId Code or external identifier
     * @return Order matched
     */
    public Order findByCodeOrExternalId(String codeOrExternalId) {

        TypedQuery<Order> query = getEntityManager().createNamedQuery("Order.findByCodeOrExternalId", Order.class).setParameter("code", codeOrExternalId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code/externalId {} found", Order.class.getSimpleName(), codeOrExternalId);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code/externalId {} found", Order.class, codeOrExternalId);
            return null;
        }
    }

    @Override
    public void create(Order order) throws BusinessException {
        this.validate(order);
        // Obtain card payment method token id from a payment gateway
        if (order.getPaymentMethod() != null && order.getPaymentMethod() instanceof CardPaymentMethod && ((CardPaymentMethod) order.getPaymentMethod()).getTokenId() == null) {
            UserAccount userAccount = userAccountService.retrieveIfNotManaged(order.getOrderItems().get(0).getUserAccount());
            paymentMethodService.obtainAndSetCardToken((CardPaymentMethod) order.getPaymentMethod(), userAccount.getBillingAccount().getCustomerAccount());
        }
        if (order.getPaymentMethod() != null) {
            order.getPaymentMethod().updateAudit(currentUser);
        }

        order.setOrderNumber(StringUtils.isBlank(order.getExternalId()) ? order.getCode() : order.getExternalId());

        super.create(order);
    }

    @Override
    public Order update(Order order) throws BusinessException {
        this.validate(order);
        // Obtain card payment method token id from a payment gateway
        if (order.getPaymentMethod() != null && order.getPaymentMethod() instanceof CardPaymentMethod && ((CardPaymentMethod) order.getPaymentMethod()).getTokenId() == null) {
            UserAccount userAccount = userAccountService.retrieveIfNotManaged(order.getOrderItems().get(0).getUserAccount());
            paymentMethodService.obtainAndSetCardToken((CardPaymentMethod) order.getPaymentMethod(), userAccount.getBillingAccount().getCustomerAccount());
        }
        if (order.getPaymentMethod() != null) {
            order.getPaymentMethod().updateAudit(currentUser);
        }

        return super.update(order);
    }

    public void validate(Order order) throws BusinessException {
        boolean validateOnExecuteDisabled = ParamBean.getInstance().getProperty("order.validateOnExecute", "false").equalsIgnoreCase("false");
        if (validateOnExecuteDisabled && (order.getOrderItems() == null || order.getOrderItems().isEmpty())) {
            throw new ValidationException("At least one order line item is required");
        }
    }

    /**
     * Return all orders with orderDate more than n years old
     * 
     * @param nYear age of the subscription
     * @return Filtered list of orders
     */
    @SuppressWarnings("unchecked")
    public List<Order> listInactiveOrders(int nYear) {
        QueryBuilder qb = new QueryBuilder(Order.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("orderDate", higherBound, true, false);

        return (List<Order>) qb.getQuery(getEntityManager()).getResultList();
    }

    public Quote getReference(Class<Quote> clazz, Long quoteId) {
        return getEntityManager().getReference(clazz, quoteId);
    }

    @SuppressWarnings("unchecked")
    public List<Order> findOrders(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(Order.class, "o", null);
            qb.addCriterionEntity("o.billingCycle.id", billingCycle.getId());
            return (List<Order>) qb.getQuery(getEntityManager()).getResultList();

        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    /**
     * List orders that are associated with a given billing run
     * 
     * @param billingRun Billing run
     * @return A list of Orders
     */
    public List<Order> findOrders(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("Order.listByBillingRun", Order.class).setParameter("billingRunId", billingRun.getId()).getResultList();
    }
}