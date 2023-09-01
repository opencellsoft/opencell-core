package org.meveo.apiv2.ordering.resource.ooq;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.ordering.services.ooq.OpenOrderQuoteApi;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

@Interceptors({ WsRestApiInterceptor.class })
public class OpenOrderQuoteResourceImpl implements OpenOrderQuoteResource {

    @Inject
    private OpenOrderQuoteApi openOrderQuoteApi;


    @Override
    public Response create(OpenOrderQuoteDto dto) {
        Long createdOOQId = openOrderQuoteApi.create(dto);

        ActionStatus createdStatus = new ActionStatus();
        createdStatus.setStatus(ActionStatusEnum.SUCCESS);
        createdStatus.setEntityId(createdOOQId);

        return Response.created(LinkGenerator.getUriBuilderFromResource(OpenOrderQuoteResource.class, createdOOQId).build())
                .entity(createdStatus).build();
    }

    @Override
    public Response duplicate(Long iqOOQ) {
        Long updatedOOQId = openOrderQuoteApi.duplicate(iqOOQ);

        ActionStatus updatedStatus = new ActionStatus();
        updatedStatus.setStatus(ActionStatusEnum.SUCCESS);
        updatedStatus.setEntityId(updatedOOQId);

        return Response.ok().entity(updatedStatus).build();
    }

    @Override
    public Response update(Long id, OpenOrderQuoteDto dto) {
        Long updatedOOQId = openOrderQuoteApi.update(id, dto);

        ActionStatus updatedStatus = new ActionStatus();
        updatedStatus.setStatus(ActionStatusEnum.SUCCESS);
        updatedStatus.setEntityId(updatedOOQId);

        return Response.ok().entity(updatedStatus).build();
    }

    @Override
    public Response changeStatus(String code, OpenOrderQuoteStatusEnum status) {
        return Response
                .ok()
                .entity(openOrderQuoteApi.changeStatus(code, status))
                .build();
    }
}