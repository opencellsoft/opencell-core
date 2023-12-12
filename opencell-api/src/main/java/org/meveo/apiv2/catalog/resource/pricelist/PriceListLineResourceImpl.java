package org.meveo.apiv2.catalog.resource.pricelist;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.catalog.PriceListLineDto;
import org.meveo.apiv2.catalog.service.pricelist.PriceListLineApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class PriceListLineResourceImpl implements PriceListLineResource {

    @Inject
    private PriceListLineApiService priceListLineApiService;

    public Response create(PriceListLineDto postDto) {
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        Long entityId = priceListLineApiService.create(postDto);
        actionStatus.setEntityId(entityId);
        return Response.created(LinkGenerator.getUriBuilderFromResource(PriceListLineResource.class, entityId)
                        .build())
                .entity(actionStatus)
                .build();
    }

    @Override
    public Response update(Long priceListLineId, PriceListLineDto postDto) {
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        actionStatus.setEntityId(priceListLineApiService.update(priceListLineId, postDto));
        return Response.ok(actionStatus).build();
    }

    @Override
    public Response delete(Long priceListLineId) {
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        priceListLineApiService.delete(priceListLineId);
        return Response.ok(actionStatus).build();
    }
}
