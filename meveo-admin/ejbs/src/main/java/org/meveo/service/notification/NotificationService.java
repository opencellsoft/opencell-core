package org.meveo.service.notification;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
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

    /**
     * Get a list of notifications to populate a cache
     * 
     * @return A list of active notifications
     */
    public List<Notification> getNotificationsForCache() {
        return getEntityManager().createNamedQuery("Notification.getNotificationsForCache", Notification.class).getResultList();
    }
    
    @Inject
    private NotificationCacheContainerProvider notificationCacheContainerProvider;

    @Override
    public void create(Notification notification, User creator, Provider provider) {
        super.create(notification, creator, provider);
        notificationCacheContainerProvider.addNotificationToCache(notification);
    }

    @Override
    public Notification update(Notification notification, User updater) {
        notification = super.update(notification, updater);
        notificationCacheContainerProvider.updateNotificationInCache(notification);
        return notification;
    }

    @Override
    public void remove(Notification notification) {
        super.remove(notification);
        notificationCacheContainerProvider.removeNotificationFromCache(notification);
    }

    @Override
    public Notification disable(Notification notification) {
        notification = super.disable(notification);
        notificationCacheContainerProvider.removeNotificationFromCache(notification);
        return notification;
    }

    @Override
    public Notification enable(Notification notification) {
        notification = super.enable(notification);
        notificationCacheContainerProvider.addNotificationToCache(notification);
        return notification;
    }
}
