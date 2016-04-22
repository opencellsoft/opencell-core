package org.meveo.service.notification;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IAuditable;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class NotificationHistoryService extends PersistenceService<NotificationHistory> {

    public NotificationHistory create(Notification notification, IEntity e, String result, NotificationHistoryStatusEnum status) throws BusinessException {
        NotificationHistory history = new NotificationHistory();
        history.setNotification(notification);
        history.setEntityClassName(e.getClass().getName());
        history.setSerializedEntity(e.getId() == null ? e.toString() : e.getId().toString());
        history.setResult(result);
        history.setStatus(status);
        history.setProvider(notification.getProvider());
        User currentUser = null;
        if (e instanceof IAuditable && ((IAuditable) e).getAuditable() != null) {
            currentUser = ((IAuditable) e).getAuditable().getCreator();
        } else {
            currentUser = getCurrentUser();
        }
        super.create(history, currentUser); // AKK was with history.getProvider()

        return history;

    }
}