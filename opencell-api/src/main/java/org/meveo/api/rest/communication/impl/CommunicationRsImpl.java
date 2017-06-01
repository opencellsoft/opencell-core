package org.meveo.api.rest.communication.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.communication.CommunicationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.communication.CommunicationRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CommunicationRsImpl extends BaseRs implements CommunicationRs {

    @Inject
    CommunicationApi communicationApi;

    @Override
    public ActionStatus inboundCommunication(CommunicationRequestDto communicationRequestDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            communicationApi.inboundCommunication(communicationRequestDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}