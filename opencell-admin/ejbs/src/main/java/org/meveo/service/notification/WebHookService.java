package org.meveo.service.notification;

import javax.ejb.Stateless;

import org.meveo.model.notification.WebHook;

/**
 * A service class to manage CRUD operations on Webhook notification entity
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class WebHookService extends NotificationInstanceService<WebHook> {

}
