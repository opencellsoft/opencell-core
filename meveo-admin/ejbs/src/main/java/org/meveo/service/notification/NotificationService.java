package org.meveo.service.notification;

import java.util.List;

import org.meveo.model.notification.Notification;
import org.meveo.service.base.BusinessService;

public class NotificationService extends BusinessService<Notification> {

	@SuppressWarnings("unchecked")
	public List<Notification> listAll() {
		return getEntityManager().createQuery("From "+Notification.class.getName()).getResultList();
	}

}
