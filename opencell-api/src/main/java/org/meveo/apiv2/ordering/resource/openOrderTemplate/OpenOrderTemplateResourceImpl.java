package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.services.OpenOrderTemplateApiService;
import org.meveo.service.order.OpenOrderTemplateService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;


public class  OpenOrderTemplateResourceImpl  implements OpenOrderTemplateResource{

    @Inject
    private OpenOrderTemplateApiService openOrderTemplateApiService;



    public Response createOpenOrderTemplate(OpenOrderTemplateInput openOrderTemplateInput){

        openOrderTemplateApiService.create(openOrderTemplateInput);
        return Response.ok().build();
    }




    public Response updateOpenOrderTemplate( String code, OpenOrderTemplateInput openOrderTemplateInput){
        openOrderTemplateApiService.update(code, openOrderTemplateInput);
        return Response.ok().build();
    }

}
