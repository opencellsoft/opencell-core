package org.meveo.apiv2.catalog.resource.pricelist;

import org.meveo.apiv2.catalog.PriceListLineDto;
import org.meveo.apiv2.catalog.service.pricelist.PriceListLineApiService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Map;

@Stateless
public class PriceListLineResourceImpl implements PriceListLineResource {

    @Inject
    private PriceListLineApiService priceListLineApiService;

    public Response create(PriceListLineDto postDto) {
        Long entityId = priceListLineApiService.create(postDto);
        return Response.ok(Map.of("entityId", entityId)).build();
    }

    @Override
    public Response update(PriceListLineDto postDto) {
        return null;
    }

    @Override
    public Response delete(String priceListLineCode) {
        return null;
    }
}
