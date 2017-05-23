package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.communication.CommunicationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.CommunicationWs;

/**
 * @author Nasseh
 **/
@WebService(serviceName = "CommunicationWs", endpointInterface = "org.meveo.api.ws.CommunicationWs")
@Interceptors({ WsRestApiInterceptor.class })
public class CommunicationWsImpl extends BaseWs implements CommunicationWs {

    @Inject
    private CommunicationApi communicationApi;

    @Override
    public ActionStatus inboundCommunication(CommunicationRequestDto communicationRequest) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            communicationApi.inboundCommunication(communicationRequest);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
