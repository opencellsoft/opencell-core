package org.meveo.apiv2.dunning.action;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningAction;
import org.meveo.apiv2.dunning.ImmutableDunningAction;
import org.meveo.apiv2.dunning.service.GlobalSettingsVerifier;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.service.payments.impl.DunningActionService;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningActionImpl implements DunningActionResource{

    @Inject
    private DunningActionService dunningActionService;

    @Inject
    private GlobalSettingsVerifier globalSettingsVerifier;

    @Override
    public Response getDunningAction(String code) {
        org.meveo.model.dunning.DunningAction dunningAction = dunningActionService.findByCode(code, Arrays.asList("relatedLevels"));
        if(dunningAction == null) {
            throw new EntityDoesNotExistsException("dunning action with code "+code+" does not exist.");
        }
        return Response.ok()
                    .entity(ImmutableDunningAction.builder().build().toDunningAction(dunningAction)).build();
    }

    @Override
    public Response createDunningAction(DunningAction dunningAction) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningAction dunningActionEntity = dunningActionService.findByCode(dunningAction.getCode());
        if(dunningActionEntity != null) {
            throw new EntityAlreadyExistsException("dunning action with code "+dunningAction.getCode()+" already exist.");
        }
        org.meveo.model.dunning.DunningAction dunningActionToCreate = dunningAction.toEntity();
        dunningActionService.create(dunningActionToCreate);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"the Dunning Action successfully created\"},\"id\":"+dunningActionToCreate.getId()+"} ").build();
    }

    @Override
    public Response updateDunningAction(Long dunningActionId, DunningAction dunningAction) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningAction dunningActionToUpdate = dunningActionService.findById(dunningActionId, Arrays.asList("relatedLevels"));
        if(dunningActionToUpdate == null) {
            throw new EntityDoesNotExistsException("dunning action with id "+dunningActionId+" does not exist.");
        }
        org.meveo.model.dunning.DunningAction newDunningAction = dunningAction.toEntity();
        updatePropertyIfNotNull(newDunningAction.getCode(), code -> dunningActionToUpdate.setCode(code));
        updatePropertyIfNotNull(newDunningAction.getDescription(), description -> dunningActionToUpdate.setDescription(description));
        
        
        if(ActionModeEnum.AUTOMATIC.equals(newDunningAction.getActionMode())) {
        	if(newDunningAction.getActionChannel() != null && !ActionChannelEnum.EMAIL.equals(newDunningAction.getActionChannel())) {
                throw new BusinessApiException("The only action channel for the automatic mode is Email.");
        	}    	
        	dunningActionToUpdate.setActionChannel(ActionChannelEnum.EMAIL);
    	}else{
            updatePropertyIfNotNull(newDunningAction.getActionChannel(), actionChannelEnum -> dunningActionToUpdate.setActionChannel(actionChannelEnum));
        }
        
        updatePropertyIfNotNull(newDunningAction.getActionType(), actionTypeEnum -> dunningActionToUpdate.setActionType(actionTypeEnum));
        updatePropertyIfNotNull(newDunningAction.getActionMode(), actionModeEnum -> dunningActionToUpdate.setActionMode(actionModeEnum));
        updatePropertyIfNotNull(newDunningAction.getRelatedLevels(), relatedLevels -> dunningActionToUpdate.setRelatedLevels(relatedLevels));
        updatePropertyIfNotNull(newDunningAction.getScriptInstance(), scriptInstance -> dunningActionToUpdate.setScriptInstance(scriptInstance));
        updatePropertyIfNotNull(newDunningAction.getActionNotificationTemplate(), actionNotificationTemplate -> dunningActionToUpdate.setActionNotificationTemplate(actionNotificationTemplate));
        updatePropertyIfNotNull(newDunningAction.getAssignedTo(), assignedTo -> dunningActionToUpdate.setAssignedTo(assignedTo));
        dunningActionService.update(dunningActionToUpdate);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
    }

    @Override
    public Response deleteDunningAction(Long dunningActionId) {
        globalSettingsVerifier.checkActivateDunning();
        org.meveo.model.dunning.DunningAction dunningActionToDelete = dunningActionService.findById(dunningActionId);
        if(dunningActionToDelete == null) {
            throw new EntityDoesNotExistsException("dunning action with id "+dunningActionId+" does not exist.");
        }
        dunningActionService.remove(dunningActionId);
        return Response.ok().build();
    }

    public <T> void updatePropertyIfNotNull(T dunningActionProperty, Consumer<T> consumer){
        if(dunningActionProperty != null){
            consumer.accept(dunningActionProperty);
        }
    }


}
