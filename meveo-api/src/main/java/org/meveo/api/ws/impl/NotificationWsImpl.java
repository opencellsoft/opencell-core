package org.meveo.api.ws.impl;

import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.NotificationWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "NotificationWs", endpointInterface = "org.meveo.api.ws.NotificationWs")
@Interceptors({ LoggingInterceptor.class })
public class NotificationWsImpl extends BaseWs implements NotificationWs {

	@Override
	public ActionStatus createNotification(NotificationDto postData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus updateNotification(NotificationDto postData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus findNotification(String notificationCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus removeNotification(String notificationCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus createWebHookNotification(WebhookNotificationDto postData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus updateWebHookNotification(WebhookNotificationDto postData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus findWebHookNotification(String notificationCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionStatus removeWebHookNotification(String notificationCode) {
		// TODO Auto-generated method stub
		return null;
	}

}
