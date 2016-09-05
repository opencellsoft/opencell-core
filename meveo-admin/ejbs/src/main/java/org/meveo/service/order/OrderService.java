package org.meveo.service.order;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.order.Order;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.service.base.BusinessService;

@Stateless
public class OrderService extends BusinessService<Order> {

	public Long countNewOrders(Calendar endDate) {

		Calendar startDate = endDate;
		startDate.add(Calendar.DATE, -1);
		Query query = getEntityManager().createQuery(
				"select count(*) from " + Order.class.getName()
						+ " a where a.status = :orderStatus AND a.auditable.created <= :endDate AND a.auditable.created > :startDate");
		query.setParameter("orderStatus", OrderStatusEnum.ACKNOWLEDGED);
		query.setParameter("endDate", endDate.getTime());
		query.setParameter("startDate", startDate.getTime());
		Long count = (Long) query.getSingleResult();

		return count.longValue();
	}

	public Long countPendingOrders(Calendar startDate, Calendar endDate) {
		startDate.add(Calendar.DATE, -1);
		Query query = getEntityManager().createQuery(
				"select count(*) from " + Order.class.getName()
						+ " a where a.status = :orderStatus AND a.auditable.created <= :startDate");
		query.setParameter("orderStatus", OrderStatusEnum.ACKNOWLEDGED);
		query.setParameter("startDate", startDate.getTime());
		Long count = (Long) query.getSingleResult();

		return count.longValue();
	}
}