package org.meveo.apiv2.dunning.template;

import org.meveo.apiv2.dunning.DunningTemplate;
import org.meveo.service.payments.impl.DunningTemplateService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class DunningTemplateResourceImpl implements DunningTemplateResource{
    @Inject
    private DunningTemplateService dunningTemplateService;
    @Override
    public Response createDunningTemplate(DunningTemplate dunningTemplate) {
        org.meveo.model.dunning.DunningTemplate dunningTemplateEntity = dunningTemplate.toEntity();
        dunningTemplateService.create(dunningTemplateEntity);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Template successfully created\"},\"id\": "+dunningTemplateEntity.getId()+"} ")
                .build();
    }
}
