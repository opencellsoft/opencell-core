package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.notification.EmailNotificationApi;
import org.meveo.api.notification.NotificationApi;
import org.meveo.api.notification.WebHookApi;
import org.meveo.api.ws.NotificationWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "NotificationWs", endpointInterface = "org.meveo.api.ws.NotificationWs")
@Interceptors({ WsRestApiInterceptor.class })
public class NotificationWsImpl extends BaseWs implements NotificationWs {

    @Inject
    private NotificationApi notificationApi;

    @Inject
    private WebHookApi webhookNotificationApi;

    @Inject
    private EmailNotificationApi emailNotificationApi;

    @Override
    public ActionStatus createNotification(NotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateNotification(NotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetNotificationResponseDto findNotification(String notificationCode) {
        GetNotificationResponseDto result = new GetNotificationResponseDto();

        try {
            result.setNotificationDto(notificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeNotification(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createWebHookNotification(WebHookDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            webhookNotificationApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateWebHookNotification(WebHookDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            webhookNotificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetWebHookNotificationResponseDto findWebHookNotification(String notificationCode) {
        GetWebHookNotificationResponseDto result = new GetWebHookNotificationResponseDto();

        try {
            result.setWebhookDto(webhookNotificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeWebHookNotification(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            webhookNotificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createEmailNotification(EmailNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailNotificationApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateEmailNotification(EmailNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailNotificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetEmailNotificationResponseDto findEmailNotification(String notificationCode) {
        GetEmailNotificationResponseDto result = new GetEmailNotificationResponseDto();

        try {
            result.setEmailNotificationDto(emailNotificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeEmailNotification(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailNotificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public NotificationHistoriesResponseDto listNotificationHistory() {
        NotificationHistoriesResponseDto result = new NotificationHistoriesResponseDto();

        try {
            result.setNotificationHistories(notificationApi.listNotificationHistory());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public InboundRequestsResponseDto listInboundRequest() {
        InboundRequestsResponseDto result = new InboundRequestsResponseDto();

        try {
            result.setInboundRequests(notificationApi.listInboundRequest());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateEmailNotification(EmailNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailNotificationApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateNotification(NotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            notificationApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateWebHookNotification(WebHookDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            webhookNotificationApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
