package org.meveo.api.ws.impl;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.MediationWs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "MediationWs", endpointInterface = "org.meveo.api.ws.MediationWs")
@Interceptors({ LoggingInterceptor.class })
public class MediationWsImpl extends BaseWs implements MediationWs {

	@Inject
	private Logger log;

	@Inject
	private MediationApi mediationApi;

	@Resource
	private WebServiceContext wsContext;

	@Override
	public ActionStatus registerCdrList(CdrListDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			MessageContext mc = wsContext.getMessageContext();
			HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);

			postData.setIpAddress(req.getRemoteAddr());
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
			MessageContext mc = wsContext.getMessageContext();
			HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
			mediationApi.chargeCdr(cdr, getCurrentUser(), req.getRemoteAddr());
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
			MessageContext mc = wsContext.getMessageContext();
			HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
			CdrReservationResponseDto response = mediationApi.reserveCdr(cdr, getCurrentUser(), req.getRemoteAddr());
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
	public ActionStatus confirmReservation(PrepaidReservationDto reservation) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			MessageContext mc = wsContext.getMessageContext();
			HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
			mediationApi.confirmReservation(reservation, getCurrentUser(), req.getRemoteAddr());
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
	public ActionStatus cancelReservation(PrepaidReservationDto reservation) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			MessageContext mc = wsContext.getMessageContext();
			HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
			mediationApi.cancelReservation(reservation, getCurrentUser(), req.getRemoteAddr());
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
