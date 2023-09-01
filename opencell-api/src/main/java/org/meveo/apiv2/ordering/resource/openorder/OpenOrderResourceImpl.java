package org.meveo.apiv2.ordering.resource.openorder;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.ordering.resource.oo.OpenOrderDto;
import org.meveo.apiv2.ordering.services.OpenOrderApiService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Interceptors({ WsRestApiInterceptor.class })
public class OpenOrderResourceImpl implements OpenOrderResource {

    @Inject
    private OpenOrderApiService openOrderApiService;

    @Override
    public Response updateOpenOrder(String code, OpenOrderDto OpenOrderDto) {
        OpenOrderDto openOrderDto = openOrderApiService.update(code, OpenOrderDto);
        return buildResponse(openOrderDto);
    }

    @Override
    public Response cancelOpenOrder(String code, OpenOrderDto OpenOrderDto) {
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