package org.meveo.service.notification;

import javax.ejb.Stateless;

import org.meveo.model.notification.EmailNotification;

/**
 * A service class to manage CRUD operations on EmailNotification entity
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class EmailNotificationService extends NotificationInstanceService<EmailNotification> {

}