package org.meveo.service.order;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.payments.CardPaymentMethod;
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

        String sqlQuery = "select count(*) from " + Order.class.getName()
                + " a where a.status = :orderStatus AND a.auditable.created <= :endDate AND a.auditable.created > :startDate";
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

    public Order findByCodeOrExternalId(String codeOrExternalId) {
        Order order = null;
        Query query = getEntityManager().createQuery("from " + Order.class.getName() + " a left join fetch a.billingRun where (a.code = :code OR  a.externalId = :code) ");
        query.setParameter("code", codeOrExternalId);
        try {
            order = (Order) query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code/externalId {} found", Order.class.getSimpleName(), codeOrExternalId);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code/externalId {} found", Order.class, codeOrExternalId);
            return null;
        }
        return order;
    }

    @Override
    public void create(Order order) throws BusinessException {

        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new ValidationException("At least one order line item is required");
        }

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

        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new ValidationException("At least one order line item is required");
        }

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
    
    /**
     * Return all orders with now - orderDate date &gt; n years.
     * @param nYear age of the subscription
     * @return Filtered list of orders
     */
    @SuppressWarnings("unchecked")
	public List<Order> listInactiveOrders(int nYear) {
    	QueryBuilder qb = new QueryBuilder(Order.class, "e");
    	Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);
    	
    	qb.addCriterionDateRangeToTruncatedToDay("orderDate", higherBound);
    	
    	return (List<Order>) qb.getQuery(getEntityManager()).getResultList();
    }

	public void bulkDelete(List<Order> inactiveOrders) throws BusinessException {
		for (Order e : inactiveOrders) {
			remove(e);
		}
	}
}