package org.meveo.apiv2.ordering.resource.openOrder;

import org.meveo.apiv2.ordering.resource.oo.OpenOrderDto;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.services.OpenOrderApiService;
import org.meveo.apiv2.ordering.services.OpenOrderTemplateApiService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpenOrderResourceImpl implements OpenOrderResource {

    @Inject
    private OpenOrderApiService openOrderApiService;

    @Override
    public Response updateOpenOrder(String code, OpenOrderDto OpenOrderDto) {
        OpenOrderDto openOrderDto = openOrderApiService.update(code, OpenOrderDto);
        return buildResponse(openOrderDto);
    }

    @Override
    public Response cancelOpenOrderTemplate(String code, OpenOrderDto OpenOrderDto) {
        OpenOrderDto openOrderDto = openOrderApiService.cancel(code, OpenOrderDto);
        return buildResponse(openOrderDto);
    }

    private Response buildResponse(OpenOrderDto openOrderDto) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("openOrder", openOrderDto);
        return Response.ok().entity(response).build();
    }
}