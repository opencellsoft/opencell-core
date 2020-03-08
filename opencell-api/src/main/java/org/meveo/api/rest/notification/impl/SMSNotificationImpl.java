package org.meveo.api.rest.notification.impl;

import static java.util.Objects.nonNull;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import org.meveo.api.dto.response.notification.SMSInfoResponseDTO;
import org.meveo.api.dto.notification.SMSInfoDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.SMSNotification;
import org.meveo.service.notification.sms.SMSInfo;
import org.meveo.service.notification.sms.SMSService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SMSNotificationImpl extends BaseRs implements SMSNotification {

    @Inject
    SMSService smsService;

    @Override
    public Response send(SMSInfoDto sms) throws MeveoApiException {
        SMSInfoResponseDTO response = smsService.send(toSmsInfo(sms));
        return Response.status(nonNull(response.getErrorCode()) ? BAD_REQUEST : OK)
                .entity(response)
                .build();
    }

    private SMSInfo toSmsInfo(SMSInfoDto sms) {
        SMSInfo smsInfo = new SMSInfo();
        smsInfo.setCustomerCode(sms.getCustomerCode());
        smsInfo.setBody(sms.getBody());
        return smsInfo;
    }
}