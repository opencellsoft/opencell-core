package org.meveo.service.payments.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.DunningAction;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceCategory;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class DunningActionServiceTest {

    @InjectMocks
    private DunningActionService dunningActionService;

    @Mock
    private ScriptInstanceService scriptInstanceService;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private BaseEntityService dunningLevelBusinessService;

    @Test(expected = EntityDoesNotExistsException.class)
    public void createWithEmailNotFoundTemplateTest() {
        DunningAction dunningAction = new DunningAction();
        EmailTemplate actionNotificationTemplate = new EmailTemplate();
        actionNotificationTemplate.setId(1L);
        dunningAction.setActionNotificationTemplate(actionNotificationTemplate);
        Mockito.when(emailTemplateService.findById(1L)).thenReturn(null);
        dunningActionService.create(dunningAction);
    }

    @Test
    public void createWithEmailTemplateTest() {
        DunningActionService businessService = Mockito.spy(dunningActionService);
        DunningAction dunningAction = new DunningAction();
        EmailTemplate actionNotificationTemplate = new EmailTemplate();
        EmailTemplate fetchedActionNotificationTemplate = new EmailTemplate();
        actionNotificationTemplate.setId(1L);
        dunningAction.setActionNotificationTemplate(actionNotificationTemplate);
        Mockito.when(emailTemplateService.findById(1L)).thenReturn(fetchedActionNotificationTemplate);
        Mockito.doNothing().when((BusinessService)businessService).create(dunningAction);
        businessService.create(dunningAction);
        Assert.assertEquals(fetchedActionNotificationTemplate, dunningAction.getActionNotificationTemplate());
    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void createWithNotExistingDunningLevelTest() {
        DunningAction dunningAction = new DunningAction();
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1L);
        dunningAction.setRelatedLevels(Arrays.asList(dunningLevel));
        Mockito.when(dunningLevelBusinessService.tryToFindByEntityClassAndId(DunningLevel.class,1L)).thenReturn(null);
        dunningActionService.create(dunningAction);
    }

    @Test(expected = BadRequestException.class)
    public void updateScriptIsRequiredExceptionWhenTypeIsScriptTest() throws BadRequestException {
        DunningAction dunningAction = new DunningAction();
        dunningAction.setActionType(ActionTypeEnum.SCRIPT);
        dunningActionService.update(dunningAction);
    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void updateEntityDoesNotExistWhenScriptIdIsProvidedTest() {
        DunningAction dunningAction = new DunningAction();
        dunningAction.setActionType(ActionTypeEnum.SCRIPT);
        ScriptInstance scriptInstance = new ScriptInstance();
        scriptInstance.setId(1L);
        dunningAction.setScriptInstance(scriptInstance);
        dunningActionService.update(dunningAction);
    }

    @Test(expected = BusinessApiException.class)
    public void updateEntityDoesNotExistWhenScriptIdIsProvidedButCategoryIsNotDUNNING_ACTIONTest() {
        DunningAction dunningAction = new DunningAction();
        dunningAction.setActionType(ActionTypeEnum.SCRIPT);
        ScriptInstance scriptInstance = new ScriptInstance();
        Mockito.when(scriptInstanceService.findById(1L, Arrays.asList("scriptInstanceCategory"))).thenReturn(scriptInstance);
        scriptInstance.setId(1L);
        ScriptInstanceCategory scriptInstanceCategory = new ScriptInstanceCategory();
        scriptInstanceCategory.setCode("NOT_DUNNING_ACTION");
        scriptInstance.setScriptInstanceCategory(scriptInstanceCategory);
        dunningAction.setScriptInstance(scriptInstance);
        dunningActionService.update(dunningAction);
    }

}
