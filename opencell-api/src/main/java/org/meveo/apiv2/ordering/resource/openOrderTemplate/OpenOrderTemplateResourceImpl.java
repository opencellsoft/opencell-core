package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.services.OpenOrderTemplateApiService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class  OpenOrderTemplateResourceImpl  implements OpenOrderTemplateResource{

    @Inject
    private OpenOrderTemplateApiService openOrderTemplateApiService;



    @Override
    public Response createOpenOrderTemplate(OpenOrderTemplateInput openOrderTemplateInput){

        checkParameters(openOrderTemplateInput);
        OpenOrderTemplateInput openOrderTemplate = openOrderTemplateApiService.create(openOrderTemplateInput);
        return buildResponse(openOrderTemplate);
    }

    private void checkParameters(OpenOrderTemplateInput openOrderTemplateInput) {
        if(openOrderTemplateInput.getTemplateName() == null || openOrderTemplateInput.getTemplateName().isEmpty() )
            throw new InvalidParameterException("The following fields are required: Template name");
        if(openOrderTemplateInput.getOpenOrderType() == null  )
            throw new InvalidParameterException("The following fields are required: Open order type");
    }


    @Override
    public Response updateOpenOrderTemplate( String code, OpenOrderTemplateInput openOrderTemplateInput){
        checkParameters(openOrderTemplateInput);
        OpenOrderTemplateInput openOrderTemplate =  openOrderTemplateApiService.update(code, openOrderTemplateInput);
         return buildResponse(openOrderTemplate);
    }

    @Override
    public Response disableOpenOrderTemplate(String code) {
        openOrderTemplateApiService.disableOpenOrderTemplate(code);
        return Response.ok().build();
    }

    private Response buildResponse(OpenOrderTemplateInput openOrderTemplateInput) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("openOrderTemplate", openOrderTemplateInput);
        return Response.ok().entity(response).build();
    }


}
