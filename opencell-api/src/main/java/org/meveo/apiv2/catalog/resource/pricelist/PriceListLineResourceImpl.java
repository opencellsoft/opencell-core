package org.meveo.apiv2.catalog.resource.pricelist;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.catalog.PriceListLineDto;
import org.meveo.apiv2.catalog.service.pricelist.PriceListLineApiService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class PriceListLineResourceImpl implements PriceListLineResource {

    @Inject
    private PriceListLineApiService priceListLineApiService;

    public Response create(PriceListLineDto postDto) {
        ActionStatus actionStatus = new ActionStatus();
        actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        actionStatus.setEntityId(priceListLineApiService.create(postDto));
        return Response.ok(actionStatus).build();
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
