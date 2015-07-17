package org.meveo.api.rest.billing.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.billing.MediationRs;
import org.meveo.api.rest.impl.BaseRs;
import org.slf4j.Logger;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class MediationRsImpl extends BaseRs implements MediationRs {

	@Inject
	private Logger log;

	@Inject
	private MediationApi mediationApi;

	@Context
	private HttpServletRequest httpServletRequest;

	@Override
	public ActionStatus registerCdrList(CdrListDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			postData.setIpAddress(httpServletRequest.getRemoteAddr());
			mediationApi.registerCdrList(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus chargeCdr(String cdr) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			mediationApi.chargeCdr(cdr, getCurrentUser(), httpServletRequest.getRemoteAddr());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public CdrReservationResponseDto reserveCdr(String cdr) {
		CdrReservationResponseDto result = new CdrReservationResponseDto();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		try {
			CdrReservationResponseDto response = mediationApi.reserveCdr(cdr, getCurrentUser(),
					httpServletRequest.getRemoteAddr());
			double availableQuantity = response.getAvailableQuantity();
			if (availableQuantity == 0) {
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage("INSUFICIENT_BALANCE");
			} else if (availableQuantity > 0) {
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage("NEED_LOWER_QUANTITY");
				result.setAvailableQuantity(availableQuantity);
			}
			result.setAvailableQuantity(availableQuantity);
			result.setReservationId(response.getReservationId());
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus confirmReservation(PrepaidReservationDto reservationDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			mediationApi.confirmReservation(reservationDto, getCurrentUser(), httpServletRequest.getRemoteAddr());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus cancelReservation(PrepaidReservationDto reservationDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			mediationApi.cancelReservation(reservationDto, getCurrentUser(), httpServletRequest.getRemoteAddr());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
