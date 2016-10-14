package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;

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

	@WebMethod
	ActionStatus createOrUpdateNotification(@WebParam(name = "notification") NotificationDto postData);

	// webHook

	@WebMethod
	ActionStatus createWebHookNotification(@WebParam(name = "notification") WebHookDto postData);

	@WebMethod
	ActionStatus updateWebHookNotification(@WebParam(name = "notification") WebHookDto postData);

	@WebMethod
	GetWebHookNotificationResponseDto findWebHookNotification(
			@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus createOrUpdateWebHookNotification(@WebParam(name = "notification") WebHookDto postData);

	// email

	@WebMethod
	ActionStatus createEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

	@WebMethod
	ActionStatus updateEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

	@WebMethod
	GetEmailNotificationResponseDto findEmailNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus removeEmailNotification(@WebParam(name = "notificationCode") String notificationCode);

	@WebMethod
	ActionStatus createOrUpdateEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

	// history

	@WebMethod
	NotificationHistoriesResponseDto listNotificationHistory();

	@WebMethod
	InboundRequestsResponseDto listInboundRequest();

}
