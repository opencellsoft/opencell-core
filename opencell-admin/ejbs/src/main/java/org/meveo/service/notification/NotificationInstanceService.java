/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.notification;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.CounterInstanceService;

/**
 * A foundation service class to manage CRUD operations on Notification entity subclasses
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Notification subclass
 */
public abstract class NotificationInstanceService<T extends Notification> extends BusinessService<T> {

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private NotificationCacheContainerProvider notificationCacheContainerProvider;

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @Override
    public void create(T notification) throws BusinessException {
        // Instantiate a counter instance if counter template is provided
        try {
            manageCounterInstantiation(notification);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        super.create(notification);
        notificationCacheContainerProvider.addNotificationToCache(notification);
    }

    @Override
    public T update(T notification) throws BusinessException {
        // Instantiate a counter instance if counter template is provided
        try {
            manageCounterInstantiation(notification);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        notification = super.update(notification);
        notificationCacheContainerProvider.updateNotificationInCache(notification);
        return notification;
    }

    @Override
    public void remove(T notification) throws BusinessException {

        notificationHistoryService.deleteHistory(notification);
        super.remove(notification);
        notificationCacheContainerProvider.removeNotificationFromCache(notification);
    }

    @Override
    public T disable(T notification) throws BusinessException {
        notification = super.disable(notification);
        notificationCacheContainerProvider.removeNotificationFromCache(notification);
        return notification;
    }

    @Override
    public T enable(T notification) throws BusinessException {
        notification = super.enable(notification);
        // case when the entity was created as disabled
        notificationCacheContainerProvider.removeNotificationFromCache(notification);
        notificationCacheContainerProvider.addNotificationToCache(notification);
        return notification;
    }

    /**
     * Instantiate a counter instance if counter template is provided for the first time or has changed. Remove counter instance if counter template was removed
     * 
     * @param entity Entity being saved or updated
     * @throws BusinessException business exception
     */
    protected void manageCounterInstantiation(T entity) throws BusinessException {

        // Remove counter instance if counter is no longer associated to a notification
        if (entity.getCounterTemplate() == null && entity.getCounterInstance() != null) {
            counterInstanceService.remove(entity.getCounterInstance());

            // Instantiate a a counter instance if new template was specified or it was changed
        } else if (entity.getCounterTemplate() != null && (entity.getCounterInstance() == null
                || (entity.getCounterInstance() != null && !entity.getCounterTemplate().getId().equals(entity.getCounterInstance().getCounterTemplate().getId())))) {
            counterInstanceService.counterInstanciation(entity, entity.getCounterTemplate());
        }
    }
}
