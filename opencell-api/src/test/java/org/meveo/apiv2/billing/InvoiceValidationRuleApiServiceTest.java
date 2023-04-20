package org.meveo.apiv2.billing;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.api.invoice.InvoiceValidationRulesApiService;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.InvoiceValidationRulesService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceValidationRuleApiServiceTest {

    @InjectMocks
    private InvoiceValidationRulesApiService invoiceValidationRulesApiService;

    @Mock
    private InvoiceValidationRulesService invoiceValidationRulesService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void should_Create_InvoiceValidationRule() {

        // Given
        InvoiceValidationRule invoiceValidationRule = new InvoiceValidationRule();
        InvoiceType invoiceType = new InvoiceType();
        invoiceValidationRule.setInvoiceType(invoiceType);
        ScriptInstance scriptInstance = new ScriptInstance();
        scriptInstance.setCode("code.script.validation");
        invoiceValidationRule.setPriority(1);
        invoiceValidationRule.setValidationScript(scriptInstance);
        invoiceValidationRule.setCode("CODE");
        invoiceValidationRule.setDescription("DESCRIPTION");

        // When
        invoiceValidationRulesApiService.create(invoiceValidationRule);

        ArgumentCaptor<InvoiceValidationRule> invoiceValidationRuleArgumentCaptor = ArgumentCaptor.forClass(InvoiceValidationRule.class);
        verify(invoiceValidationRulesService).create(invoiceValidationRuleArgumentCaptor.capture());
        InvoiceValidationRule expecTedInvoiceRuleToCreate = invoiceValidationRuleArgumentCaptor.getValue();

        // Then
        Assert.assertEquals(invoiceValidationRule.getPriority(), expecTedInvoiceRuleToCreate.getPriority());
        Assert.assertEquals(invoiceValidationRule.getValidationScript(), expecTedInvoiceRuleToCreate.getValidationScript());
        Assert.assertEquals(invoiceValidationRule.getCode(), expecTedInvoiceRuleToCreate.getCode());
        Assert.assertEquals(invoiceValidationRule.getDescription(), expecTedInvoiceRuleToCreate.getDescription());

    }

    @Test
    public void should_Update_InvoiceValidationRule() {

        // Given
        InvoiceValidationRule invoiceValidationRule = new InvoiceValidationRule();
        invoiceValidationRule.setId(1L);
        InvoiceType invoiceType = new InvoiceType();
        invoiceValidationRule.setInvoiceType(invoiceType);
        ScriptInstance scriptInstance = new ScriptInstance();
        scriptInstance.setCode("code.script.validation");
        invoiceValidationRule.setPriority(1);
        invoiceValidationRule.setValidationScript(scriptInstance);
        invoiceValidationRule.setCode("CODE");
        invoiceValidationRule.setDescription("DESCRIPTION");

        // When
        invoiceValidationRulesApiService.update(invoiceValidationRule.getId(), invoiceValidationRule);

        ArgumentCaptor<InvoiceValidationRule> invoiceValidationRuleArgumentCaptor = ArgumentCaptor.forClass(InvoiceValidationRule.class);
        verify(invoiceValidationRulesService).update(invoiceValidationRuleArgumentCaptor.capture());
        InvoiceValidationRule expecTedInvoiceRuleToCreate = invoiceValidationRuleArgumentCaptor.getValue();

        // Then
        Assert.assertEquals(invoiceValidationRule.getPriority(), expecTedInvoiceRuleToCreate.getPriority());
        Assert.assertEquals(invoiceValidationRule.getValidationScript(), expecTedInvoiceRuleToCreate.getValidationScript());
        Assert.assertEquals(invoiceValidationRule.getCode(), expecTedInvoiceRuleToCreate.getCode());
        Assert.assertEquals(invoiceValidationRule.getDescription(), expecTedInvoiceRuleToCreate.getDescription());

    }

}
