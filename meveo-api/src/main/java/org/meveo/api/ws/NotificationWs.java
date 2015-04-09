package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface NotificationWs extends IBaseWs {

	// notification

	@WebMethod
	ActionStatus createNotification(@WebParam(name = "notification") NotificationDto postData);

	@WebMethod
	ActionStatus updateNotification(@WebParam(name = "notification") NotificationDto postData);

	@WebMethod
	ActionStatus findNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeNotification(@WebParam(name = "notificationCode") String notificationCode);

	// webHook

	@WebMethod
	ActionStatus createWebHookNotification(@WebParam(name = "notification") WebhookNotificationDto postData);

	@WebMethod
	ActionStatus updateWebHookNotification(@WebParam(name = "notification") WebhookNotificationDto postData);

	@WebMethod
	ActionStatus findWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

}
