package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.services.OpenOrderTemplateApiService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Map;


public class  OpenOrderTemplateResourceImpl  implements OpenOrderTemplateResource{

    @Inject
    private OpenOrderTemplateApiService openOrderTemplateApiService;



    @Override
    public Response createOpenOrderTemplate(OpenOrderTemplateInput openOrderTemplateInput){

        openOrderTemplateApiService.create(openOrderTemplateInput);
        return Response.ok().entity(Map.of("status", "SUCCESS", "message", "open Order template has been created with success")).build();
    }




    @Override
    public Response updateOpenOrderTemplate( String code, OpenOrderTemplateInput openOrderTemplateInput){
        openOrderTemplateApiService.update(code, openOrderTemplateInput);
        return Response.ok().entity(Map.of("status", "SUCCESS", "message", "open Order template has been updated with success")).build();
    }

    @Override
    public Response disableOpenOrderTemplate(String code) {
        openOrderTemplateApiService.disableOpenOrderTemplate(code);
        return Response.ok().build();
    }


}
