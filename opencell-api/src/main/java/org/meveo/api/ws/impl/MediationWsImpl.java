package org.meveo.api.ws.impl;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.dto.billing.PrepaidReservationDto;
import org.meveo.api.dto.response.billing.CdrReservationResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.MediationWs;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "MediationWs", endpointInterface = "org.meveo.api.ws.MediationWs")
@Interceptors({ WsRestApiInterceptor.class })
public class MediationWsImpl extends BaseWs implements MediationWs {

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

            String ip = StringUtils.isBlank(req.getHeader("x-forwarded-for")) ? req.getRemoteAddr() : req.getHeader("x-forwarded-for");
            postData.setIpAddress(ip);
            mediationApi.registerCdrList(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus chargeCdr(String cdr) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            mediationApi.chargeCdr(cdr, req.getRemoteAddr());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CdrReservationResponseDto reserveCdr(String cdr) {
        CdrReservationResponseDto result = new CdrReservationResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            CdrReservationResponseDto response = mediationApi.reserveCdr(cdr, req.getRemoteAddr());
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
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus confirmReservation(PrepaidReservationDto reservation) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            mediationApi.confirmReservation(reservation, req.getRemoteAddr());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelReservation(PrepaidReservationDto reservation) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            mediationApi.cancelReservation(reservation, req.getRemoteAddr());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
