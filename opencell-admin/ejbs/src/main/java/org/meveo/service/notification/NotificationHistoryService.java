package org.meveo.service.notification;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.event.IEvent;
import org.meveo.model.IEntity;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author phung
 *
 */
@Stateless
public class NotificationHistoryService extends PersistenceService<NotificationHistory> {

    @Inject
    private GenericNotificationService notificationService;

    /**
     * @param notification notification which will put on history
     * @param entityOrEvent entity or event
     * @param result result of notification
     * @param status status of notification history status.
     * @return notification history
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public NotificationHistory create(Notification notification, Object entityOrEvent, String result, NotificationHistoryStatusEnum status) throws BusinessException {
        IEntity entity = null;
        if (entityOrEvent instanceof IEntity) {
            entity = (IEntity) entityOrEvent;
        } else if (entityOrEvent instanceof IEvent) {
            entity = ((IEvent) entityOrEvent).getEntity();
        }

        NotificationHistory history = new NotificationHistory();
        history.setNotification(getEntityManager().getReference(Notification.class, notification.getId()));
        if (entity != null) {
            history.setEntityClassName(entity.getClass().getName());
            history.setSerializedEntity(entity.getId() == null ? entity.toString() : entity.getId().toString());
        }
        history.setResult(result);
        history.setStatus(status);

        create(history);

        return history;
    }

    /**
     * Count Notification history records which date is older then a given date and belong to a given notification (optional)
     * 
     * @param notificationCode Notification code (optional)
     * @param date Date to check
     * @return A number of Notification history records which date is older then a given date
     */
    public long countHistoryToDelete(String notificationCode, Date date) {

        long result = 0;

        if (notificationCode == null) {
            result = getEntityManager().createNamedQuery("NotificationHistory.countHistoryToPurgeByDate", Long.class).setParameter("date", date).getSingleResult();
        } else {
            Notification notification = notificationService.findByCode(notificationCode);
            if (notification == null) {
                log.error("No notification by code {} was found. No notification history will be removed.", notificationCode);
                return 0;
            }
            result = getEntityManager().createNamedQuery("NotificationHistory.countHistoryToPurgeByDateAndNotification", Long.class).setParameter("date", date)
                .setParameter("notification", notification).getSingleResult();
        }
        return result;
    }

    /**
     * Remove Notification history records which date is older than a given date and belong to a given notification (optional)
     * 
     * @param notificationCode Notification code (optional)
     * @param date Date to check
     * @return A number of Notification history records that were removed
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteHistory(String notificationCode, Date date) {
        log.debug("Removing Notification history of notification {} which date is older then a {} date", notificationCode == null ? "ALL" : notificationCode, date);

        long itemsDeleted = 0;

        if (notificationCode == null) {
            itemsDeleted = getEntityManager().createNamedQuery("NotificationHistory.purgeHistoryByDate").setParameter("date", date).executeUpdate();

        } else {
            Notification notification = notificationService.findByCode(notificationCode);
            if (notification == null) {
                log.error("No notification by code {} was found. No notification history will be removed.", notificationCode);
                return 0;
            }
            itemsDeleted = getEntityManager().createNamedQuery("NotificationHistory.purgeHistoryByDateAndNotification").setParameter("date", date)
                .setParameter("notification", notification).executeUpdate();
        }

        log.info("Removed {} Notification history records of notification {} which date is older then a {} date", itemsDeleted, notificationCode == null ? "ALL" : notificationCode,
            date);

        return itemsDeleted;
    }

    /**
     * Delete notification history of a given notification
     * 
     * @param notification Notification
     */
    public void deleteHistory(Notification notification) {
        getEntityManager().createNamedQuery("NotificationHistory.deleteHistoryByNotification").setParameter("notification", notification).executeUpdate();
    }
}