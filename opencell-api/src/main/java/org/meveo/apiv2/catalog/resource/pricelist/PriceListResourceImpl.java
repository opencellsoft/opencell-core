package org.meveo.apiv2.catalog.resource.pricelist;

import org.meveo.apiv2.catalog.service.PriceListApiService;
import org.meveo.model.pricelist.PriceListStatusEnum;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Map;

public class PriceListResourceImpl implements PriceListResource {

    @Inject
    private PriceListApiService priceListApiService;

    @Override
    public Response updateStatus(String priceListCode, PriceListStatusEnum newStatus) {
        priceListApiService.updateStatus(priceListCode, newStatus);
        Map<String, String> lReturn = Map.of("status", "SUCCESS");
        return Response.ok(lReturn).build();
    }
}
