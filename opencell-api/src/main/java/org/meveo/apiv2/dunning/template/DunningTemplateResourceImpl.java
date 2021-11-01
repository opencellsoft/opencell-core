package org.meveo.apiv2.dunning.template;

import org.meveo.api.exception.EntityDoesNotExistsException;
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

    @Override
    public Response deleteDunningTemplate(Long dunningTemplateId) {
        org.meveo.model.dunning.DunningTemplate dunningTemplate = dunningTemplateService.findById(dunningTemplateId);
        if(dunningTemplate == null) {
            throw new EntityDoesNotExistsException("dunning Template with id "+dunningTemplateId+" does not exist.");
        }
        dunningTemplateService.remove(dunningTemplateId);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Template successfully deleted\"},\"id\": "+dunningTemplateId+"} ")
                .build();
    }

    @Override
    public Response duplicateDunningTemplate(Long dunningTemplateId) {
        org.meveo.model.dunning.DunningTemplate dunningTemplate = dunningTemplateService.findById(dunningTemplateId);
        if(dunningTemplate == null) {
            throw new EntityDoesNotExistsException("dunning Template with id "+dunningTemplateId+" does not exist.");
        }
        dunningTemplateService.duplicate(dunningTemplate);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Template successfully duplicated\"},\"id\": "+dunningTemplate.getId()+"} ")
                .build();
    }
}
