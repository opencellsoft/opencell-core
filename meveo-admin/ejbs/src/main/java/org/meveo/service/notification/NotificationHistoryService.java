package org.meveo.service.notification;

import java.util.Date;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.IAuditable;
import org.meveo.model.IEntity;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class NotificationHistoryService extends PersistenceService<NotificationHistory> {

    public NotificationHistory create(Notification notification, IEntity e, String result, NotificationHistoryStatusEnum status) throws BusinessException {
        NotificationHistory history = new NotificationHistory();
        if (e instanceof IAuditable) {
            Auditable auditable = new Auditable();
            auditable.setCreated(new Date());
            auditable.setCreator(((IAuditable) e).getAuditable().getCreator());
            history.setAuditable(auditable);
        }
        history.setNotification(notification);
        history.setEntityClassName(e.getClass().getName());
        history.setSerializedEntity(e.getId() == null ? e.toString() : e.getId().toString());
        history.setResult(result);
        history.setStatus(status);
        history.setProvider(notification.getProvider());
        super.create(history);

        return history;

    }

}
