package org.meveo.service.notification;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.BusinessService;

@Stateless
public class NotificationService extends BusinessService<Notification> {

	@SuppressWarnings("unchecked")
	public List<Notification> listAll() {
		QueryBuilder qb = new QueryBuilder(Notification.class, "d");
		qb.addBooleanCriterion("disabled", false);
		return qb.getQuery(getEntityManager()).getResultList();
	}

}
