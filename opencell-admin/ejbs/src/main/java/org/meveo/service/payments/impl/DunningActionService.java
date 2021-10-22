package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.dunning.DunningAction;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Stateless
public class DunningActionService  extends BusinessService<DunningAction> {
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    private BaseEntityService dunningLevelBusinessService;

    @Override
    public void create(DunningAction dunningAction) throws BusinessException {
        validateScriptInstance(dunningAction);
        validateActionNotificationTemplate(dunningAction);
        validateDunningLevel(dunningAction);
        super.create(dunningAction);
    }

    private void validateDunningLevel(DunningAction dunningAction) {
        if(dunningAction.getRelatedLevels() != null && !dunningAction.getRelatedLevels().isEmpty()){
            dunningAction.setRelatedLevels(dunningAction.getRelatedLevels().stream()
                    .map(dunningLevel -> {
                        DunningLevel byId = (DunningLevel) dunningLevelBusinessService.tryToFindByEntityClassAndId(DunningLevel.class, dunningLevel.getId());
                        if(byId == null){
                            throw new EntityDoesNotExistsException("dunning level with id : "+dunningLevel.getId());
                        }
                        return byId;
                    })
                    .collect(Collectors.toList()));
        }
    }

    private void validateActionNotificationTemplate(DunningAction dunningAction) {
        if(dunningAction.getActionNotificationTemplate() != null){
            Long id = dunningAction.getActionNotificationTemplate().getId();
            EmailTemplate emailTemplateServiceById = emailTemplateService.findById(id);
            if(emailTemplateServiceById == null){
                throw new EntityDoesNotExistsException("actionNotificationTemplate with id "+ id +" does not exist.");
            }
            dunningAction.setActionNotificationTemplate(emailTemplateServiceById);
        }
    }

    private void validateScriptInstance(DunningAction dunningAction) {
        if(ActionTypeEnum.SCRIPT.equals(dunningAction.getActionType()) && dunningAction.getScriptInstance() == null){
            throw new BadRequestException("script instance id is required for Actions type SCRIPT!");
        }else {
            ScriptInstance dunningActionScriptInstance = dunningAction.getScriptInstance();
            if(dunningActionScriptInstance != null){
                ScriptInstance scriptInstance = scriptInstanceService.findById(dunningActionScriptInstance.getId(), Arrays.asList("scriptInstanceCategory"));
                if(scriptInstance == null){
                    throw new EntityDoesNotExistsException("script instance with id :"+ dunningActionScriptInstance.getId()+" does not exist.");
                }
                if(scriptInstance.getScriptInstanceCategory() != null && !"DUNNING_ACTION".equals(scriptInstance.getScriptInstanceCategory().getCode())){
                    throw new BusinessApiException("invalid script instance provided, please provide a script instance with DunningAction Category");
                }
                dunningAction.setScriptInstance(scriptInstance);
            }
        }
    }

    @Override
    public DunningAction update(DunningAction dunningAction) throws BusinessException {
        validateScriptInstance(dunningAction);
        validateActionNotificationTemplate(dunningAction);
        validateDunningLevel(dunningAction);
        return super.update(dunningAction);
    }
}
