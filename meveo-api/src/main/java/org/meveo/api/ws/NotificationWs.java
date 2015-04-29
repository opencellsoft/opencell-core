package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.dto.response.notification.ListInboundRequestResponseDto;
import org.meveo.api.dto.response.notification.ListNotificationHistoryResponseDto;

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
	GetNotificationResponseDto findNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeNotification(@WebParam(name = "notificationCode") String notificationCode);

	// webHook

	@WebMethod
	ActionStatus createWebHookNotification(@WebParam(name = "notification") WebhookNotificationDto postData);

	@WebMethod
	ActionStatus updateWebHookNotification(@WebParam(name = "notification") WebhookNotificationDto postData);

	@WebMethod
	GetWebHookNotificationResponseDto findWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

	// email

	@WebMethod
	ActionStatus createEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

	@WebMethod
	ActionStatus updateEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

	@WebMethod
	GetEmailNotificationResponseDto findEmailNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeEmailNotification(@WebParam(name = "notificationCode") String notificationCode);

	// history

	@WebMethod
	ListNotificationHistoryResponseDto listNotificationHistory();

	@WebMethod
	ListInboundRequestResponseDto listInboundRequest();

}
