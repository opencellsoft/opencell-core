package org.meveo.api.rest.notification.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.response.notification.GetNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.notification.NotificationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.NotificationRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class NotificationRsImpl extends BaseRs implements NotificationRs {

    @Inject
    private NotificationApi notificationApi;

    @Override
    public ActionStatus create(NotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(NotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetNotificationResponseDto find(String notificationCode) {
        GetNotificationResponseDto result = new GetNotificationResponseDto();

        try {
            result.setNotificationDto(notificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.remove(notificationCode);
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
    public ActionStatus createOrUpdate(NotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
