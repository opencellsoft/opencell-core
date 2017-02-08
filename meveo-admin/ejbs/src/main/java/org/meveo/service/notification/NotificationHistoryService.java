package org.meveo.service.notification;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.meveo.admin.exception.BusinessException;
import org.meveo.event.IEvent;
import org.meveo.model.IAuditable;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class NotificationHistoryService extends PersistenceService<NotificationHistory> {

    @EJB
    private NotificationHistoryService self;

    public NotificationHistory create(Notification notification, Object entityOrEvent, String result, NotificationHistoryStatusEnum status) throws BusinessException {
        IEntity entity = null;
        if (entityOrEvent instanceof IEntity) {
            entity = (IEntity) entityOrEvent;
        } else if (entityOrEvent instanceof IEvent) {
            entity = ((IEvent) entityOrEvent).getEntity();
        }

        NotificationHistory history = new NotificationHistory();
        history.setNotification(notification);
        history.setEntityClassName(entity.getClass().getName());
        history.setSerializedEntity(entity.getId() == null ? entity.toString() : entity.getId().toString());
        history.setResult(result);
        history.setStatus(status);
        history.setProvider(notification.getProvider());
        User currentUser = null;
        if (entity instanceof IAuditable && ((IAuditable) entity).getAuditable() != null) {
            currentUser = ((IAuditable) entity).getAuditable().getCreator();
        } else {
            currentUser = getCurrentUser();
        }
        self.createHistory(history, currentUser); // AKK was with history.getProvider()

        return history;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createHistory(NotificationHistory history, User currentUser) throws BusinessException {
        super.create(history, currentUser);
    }
}