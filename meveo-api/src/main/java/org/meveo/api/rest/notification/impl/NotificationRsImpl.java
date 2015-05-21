package org.meveo.api.rest.notification.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.response.notification.GetNotificationResponseDto;
import org.meveo.api.dto.response.notification.ListInboundRequestResponseDto;
import org.meveo.api.dto.response.notification.ListNotificationHistoryResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.notification.NotificationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.NotificationRs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class NotificationRsImpl extends BaseRs implements NotificationRs {

	@Inject
	private Logger log;

	@Inject
	private NotificationApi notificationApi;

	@Override
	public ActionStatus create(NotificationDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			notificationApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while creating notification ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while creating notification ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus update(NotificationDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			notificationApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while updating notification ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while updating notification ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public GetNotificationResponseDto find(String notificationCode) {
		GetNotificationResponseDto result = new GetNotificationResponseDto();

		try {
			result.setNotificationDto(notificationApi.find(notificationCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error occurred while getting notification ",e);
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error generated while getting notification ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus remove(String notificationCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			notificationApi.remove(notificationCode, getCurrentUser().getProvider());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while removing notification ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while removing notification ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ListNotificationHistoryResponseDto listNotificationHistory() {
		ListNotificationHistoryResponseDto result = new ListNotificationHistoryResponseDto();

		try {
			result.setNotificationHistories(notificationApi.listNotificationHistory(getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error occurred while getting list notification history",e);
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error generated while getting list notification history ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ListInboundRequestResponseDto listInboundRequest() {
		ListInboundRequestResponseDto result = new ListInboundRequestResponseDto();

		try {
			result.setInboundRequests(notificationApi.listInboundRequest(getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error occured while getting list inbound request response ",e);
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("error generated while getting list inbound request response ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
