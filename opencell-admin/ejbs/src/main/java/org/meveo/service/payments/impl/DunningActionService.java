package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.payments.DunningAction;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.Arrays;

@Stateless
public class DunningActionService  extends PersistenceService<DunningAction> {
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Override
    public void create(DunningAction dunningAction) throws BusinessException {
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
        if(dunningAction.getActionNotificationTemplate() != null){
            Long id = dunningAction.getActionNotificationTemplate().getId();
            EmailTemplate emailTemplateServiceById = emailTemplateService.findById(id);
            if(emailTemplateServiceById == null){
                throw new EntityDoesNotExistsException("actionNotificationTemplate with id "+ id +" does not exist.");
            }
            dunningAction.setActionNotificationTemplate(emailTemplateServiceById);
        }
        super.create(dunningAction);
    }
}
