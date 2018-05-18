package org.meveo.service.notification;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.service.base.BusinessService;

/**
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 */
@Stateless
public class NotificationService extends BusinessService<ScriptNotification> {

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
    public void create(ScriptNotification scriptNotification) throws BusinessException {
        super.create(scriptNotification);
        notificationCacheContainerProvider.addNotificationToCache(scriptNotification);
    }

    /**
     * Update scriptNotification v5.0: adding notification to cache only when notification is active
     * 
     * @param scriptNotification scriptNotification
     * @return scriptNotification scriptNotification
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    @Override
    public ScriptNotification update(ScriptNotification scriptNotification) throws BusinessException {
        scriptNotification = super.update(scriptNotification);
        notificationCacheContainerProvider.removeNotificationFromCache(scriptNotification);
        if (scriptNotification.isActive()) {
            notificationCacheContainerProvider.addNotificationToCache(scriptNotification);
        }
        return scriptNotification;
    }

    @Override
    public void remove(ScriptNotification scriptNotification) throws BusinessException {
        super.remove(scriptNotification);
        notificationCacheContainerProvider.removeNotificationFromCache(scriptNotification);
    }

    @Override
    public ScriptNotification disable(ScriptNotification scriptNotification) throws BusinessException {
        scriptNotification = super.disable(scriptNotification);
        notificationCacheContainerProvider.removeNotificationFromCache(scriptNotification);
        return scriptNotification;
    }

    @Override
    public ScriptNotification enable(ScriptNotification scriptNotification) throws BusinessException {
        scriptNotification = super.enable(scriptNotification);
        // case when the entity was created as disabled
        notificationCacheContainerProvider.removeNotificationFromCache(scriptNotification);
        notificationCacheContainerProvider.addNotificationToCache(scriptNotification);
        return scriptNotification;
    }
}
