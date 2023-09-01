package org.meveo.apiv2.dunning.template;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningTemplate;
import org.meveo.apiv2.dunning.service.GlobalSettingsVerifier;
import org.meveo.service.payments.impl.DunningTemplateService;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningTemplateResourceImpl implements DunningTemplateResource{
    @Inject
    private GlobalSettingsVerifier globalSettingsVerifier;

    @Inject
    private DunningTemplateService dunningTemplateService;
    @Override
    public Response createDunningTemplate(DunningTemplate dunningTemplate) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningTemplate dunningTemplateEntity = dunningTemplate.toEntity();
        dunningTemplateService.create(dunningTemplateEntity);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Template successfully created\"},\"id\": "+dunningTemplateEntity.getId()+"} ")
                .build();
    }

    @Override
    public Response deleteDunningTemplate(Long dunningTemplateId) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningTemplate dunningTemplate = dunningTemplateService.findById(dunningTemplateId);
        if(dunningTemplate == null) {
            throw new EntityDoesNotExistsException("dunning Template with id "+dunningTemplateId+" does not exist.");
        }
        try {
        	dunningTemplateService.remove(dunningTemplateId);
        }catch (Exception exception) {
        	if (ExceptionUtils.indexOfThrowable(exception, org.hibernate.exception.ConstraintViolationException.class) > -1) {
        		throw new DeleteReferencedEntityException(DunningTemplate.class, dunningTemplateId);
        	} else {
        		throw new BusinessApiException(exception);
        	} 
        }
        return Response.ok()
        		.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Template successfully deleted\"},\"id\": "+dunningTemplateId+"} ")
        		.build();
    }

    @Override
    public Response duplicateDunningTemplate(Long dunningTemplateId) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningTemplate dunningTemplate = dunningTemplateService.findById(dunningTemplateId);
        if(dunningTemplate == null) {
            throw new EntityDoesNotExistsException("dunning Template with id "+dunningTemplateId+" does not exist.");
        }
        dunningTemplateService.duplicate(dunningTemplate);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Template successfully duplicated\"},\"id\": "+dunningTemplate.getId()+"} ")
                .build();
    }

    @Override
    public Response updateDunningTemplate(Long dunningTemplateId, DunningTemplate dunningTemplate) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningTemplate dunningTemplateEntity = dunningTemplateService.findById(dunningTemplateId);
        if(dunningTemplateEntity == null) {
            throw new EntityDoesNotExistsException("dunning Template with id "+dunningTemplateId+" does not exist.");
        }
        org.meveo.model.dunning.DunningTemplate dunningTemplateUpdatedInfos = dunningTemplate.toEntity();
        String newCode = dunningTemplateUpdatedInfos.getCode();
        if(newCode != null && !newCode.isBlank()){
            dunningTemplateEntity.setCode(newCode);
        }
        String description = dunningTemplateUpdatedInfos.getDescription();
        if(description != null && !description.isBlank()){
            dunningTemplateEntity.setDescription(description);
        }
        if(dunningTemplateUpdatedInfos.getLanguage() != null){
            dunningTemplateEntity.setLanguage(dunningTemplateUpdatedInfos.getLanguage());
        }
        dunningTemplateEntity.setActive(dunningTemplateUpdatedInfos.isActive());
        if(dunningTemplateUpdatedInfos.getChannel()!=null){
            dunningTemplateEntity.setChannel(dunningTemplateUpdatedInfos.getChannel());
        }
        if(dunningTemplateUpdatedInfos.getSubject()!=null && !dunningTemplateUpdatedInfos.getSubject().isBlank()){
            dunningTemplateEntity.setSubject(dunningTemplateUpdatedInfos.getSubject());
        }
        if(dunningTemplateUpdatedInfos.getHtmlContent()!=null && !dunningTemplateUpdatedInfos.getHtmlContent().isBlank()){
            dunningTemplateEntity.setHtmlContent(dunningTemplateUpdatedInfos.getHtmlContent());
        }
        dunningTemplateService.update(dunningTemplateEntity);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
    }
}
