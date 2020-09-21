package org.meveo.api.rest.notification.impl;

import static java.util.Objects.nonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

import org.meveo.api.dto.notification.SMSInfoDto;
import org.meveo.api.dto.response.notification.SMSInfoResponseDTO;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.SMSNotification;

import org.meveo.service.notification.sms.Communication;
import org.meveo.service.notification.sms.SMSService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SMSNotificationImpl extends BaseRs implements SMSNotification {

    @Inject
    private SMSService smsService;

    @Override
    public Response send(SMSInfoDto smsDTO) throws MeveoApiException {
        String[] result = smsDTO.getTargetType().split("\\|");
        var to = result[0];
        var targetType = entityFrom(result[1]);
        Communication communication = new Communication(smsDTO.getCode(), to, smsDTO.getBody(), targetType);
        SMSInfoResponseDTO response = smsService.send(communication);
        return Response.status(nonNull(response.getErrorCode()) ? BAD_REQUEST : OK)
                .entity(response)
                .build();
    }

    private String entityFrom(String targetType) {
        var result = "";
        switch (targetType) {
            case "S" :
                result = "Seller";
                break;
            case "C" :
                result = "Customer";
                break;
            case "BA" :
                result = "BillingAccount";
                break;
            case "CA" :
                result = "CustomerAccount";
                break;
            case "UA" :
                result = "UserAccount";
                break;
        }
        return result;
    }
}