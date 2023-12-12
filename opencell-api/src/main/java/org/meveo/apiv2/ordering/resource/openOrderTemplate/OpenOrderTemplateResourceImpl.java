package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.services.OpenOrderTemplateApiService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Interceptors({ WsRestApiInterceptor.class })
public class OpenOrderTemplateResourceImpl implements OpenOrderTemplateResource {

    @Inject
    private OpenOrderTemplateApiService openOrderTemplateApiService;

    @Override
    public Response createOpenOrderTemplate(OpenOrderTemplateInput openOrderTemplateInput) {

        OpenOrderTemplateInput openOrderTemplate = openOrderTemplateApiService.create(openOrderTemplateInput);
        return buildResponse(openOrderTemplate);
    }

    @Override
    public Response updateOpenOrderTemplate(String code, OpenOrderTemplateInput openOrderTemplateInput) {
        OpenOrderTemplateInput openOrderTemplate = openOrderTemplateApiService.update(code, openOrderTemplateInput);
        return buildResponse(openOrderTemplate);
    }

    @Override
    public Response disableOpenOrderTemplate(String code) {
        openOrderTemplateApiService.disableOpenOrderTemplate(code);
        return Response.ok().build();
    }

    @Override
    public Response changeStatusOpenOrderTemplate(String code, String status) {
        openOrderTemplateApiService.changeStatusOpenOrderTemplate(code, status);
        return Response.ok().build();
    }
    
    private Response buildResponse(OpenOrderTemplateInput openOrderTemplateInput) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status", "SUCCESS"));
        response.put("openOrderTemplate", openOrderTemplateInput);
        return Response.ok().entity(response).build();
    }
}