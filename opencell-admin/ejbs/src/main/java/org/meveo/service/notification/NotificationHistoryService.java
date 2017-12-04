package org.meveo.service.notification;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

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
        history.setEntityClassName(entity.getClass().getName());
        history.setSerializedEntity(entity.getId() == null ? entity.toString() : entity.getId().toString());
        history.setResult(result);
        history.setStatus(status);

        create(history);

        return history;

    }
}