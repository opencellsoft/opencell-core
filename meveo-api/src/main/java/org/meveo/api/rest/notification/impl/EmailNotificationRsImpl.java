package org.meveo.api.rest.notification.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.notification.EmailNotificationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.EmailNotificationRs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class EmailNotificationRsImpl extends BaseRs implements EmailNotificationRs {

	@Inject
	private Logger log;

	@Inject
	private EmailNotificationApi emailNotificationApi;

	@Override
	public ActionStatus create(EmailNotificationDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			emailNotificationApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus update(EmailNotificationDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			emailNotificationApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public GetEmailNotificationResponseDto find(String notificationCode) {
		GetEmailNotificationResponseDto result = new GetEmailNotificationResponseDto();

		try {
			result.setEmailNotificationDto(emailNotificationApi.find(notificationCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus remove(String notificationCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			emailNotificationApi.remove(notificationCode, getCurrentUser().getProvider());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
