package org.meveo.service.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.CounterInstanceService;

@Stateless
public abstract class NotificationInstanceService<T extends Notification> extends BusinessService<T> {

    @Inject
    CounterInstanceService counterInstanceService;

    @Override
    public void create(T entity) throws BusinessException {

        // Instantiate a counter instance if counter template is provided
        manageCounterInstantiation(entity);
        super.create(entity);
    }

    @Override
    public T update(T entity) {

        // Instantiate a counter instance if counter template is provided
        try {
            manageCounterInstantiation(entity);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        return super.update(entity);
    }

    /**
     * Instantiate a counter instance if counter template is provided for the first time or has changed. Remove counter instance if counter template was removed
     * 
     * @param entity Entity being saved or updated
     * @throws BusinessException
     */
    protected void manageCounterInstantiation(T entity) throws BusinessException {

        // Remove counter instance if counter is no longer associated to a notification
        if (entity.getCounterTemplate() == null && entity.getCounterInstance() != null) {
            counterInstanceService.remove(entity.getCounterInstance());

            // Instantiate a a counter instance if new template was specified or it was changed
        } else if (entity.getCounterTemplate() != null
                && (entity.getCounterInstance() == null || (entity.getCounterInstance() != null && !entity.getCounterTemplate().getId().equals(
                    entity.getCounterInstance().getCounterTemplate().getId())))) {
            counterInstanceService.counterInstanciation(entity, entity.getCounterTemplate(), getCurrentUser());
        }
    }
}
