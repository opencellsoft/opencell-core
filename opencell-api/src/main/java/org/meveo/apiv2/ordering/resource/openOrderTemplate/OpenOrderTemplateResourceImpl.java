package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.service.order.OpenOrderTemplateService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;


public class  OpenOrderTemplateResourceImpl  implements OpenOrderTemplateResource{

    @Inject
    private OpenOrderTemplateService openOrderTemplateService;

    public Response createOpenOrderTemplate(OpenOrderTemplateInput openOrderTemplateInput){

        openOrderTemplateService.create(null);
        return null;
    }




    public Response updateOpenOrderTemplate( String code, OpenOrderTemplateInput openOrderTemplateInput){
        openOrderTemplateService.update(null);
        return null;
    }

}
