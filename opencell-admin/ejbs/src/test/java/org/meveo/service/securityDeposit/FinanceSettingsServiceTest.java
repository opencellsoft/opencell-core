package org.meveo.service.securityDeposit;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FinanceSettingsServiceTest {

    @InjectMocks
    private FinanceSettingsService financeSettingsService;

    @Test
    public void createFinanceSettingsService() {
        FinanceSettingsService businessService = Mockito.spy(financeSettingsService);
        FinanceSettings financeSettings = new FinanceSettings();
        financeSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ONE);
        financeSettings.setMaxAmountPerCustomer(BigDecimal.ONE);
        Mockito.doNothing().when((BusinessService) businessService).create(financeSettings);
        businessService.create(financeSettings);

    }

    @Test
    public void updateFinanceSettingsService() {
        FinanceSettingsService businessService = Mockito.spy(financeSettingsService);
        FinanceSettings financeSettings = new FinanceSettings();
        financeSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ONE);
        financeSettings.setMaxAmountPerCustomer(BigDecimal.ONE);
        Mockito.doReturn(null).when((BusinessService) businessService).update(financeSettings);
        businessService.update(financeSettings);

    }

    @Test
    public void checkFinanceSettingsService_MaxAmountPerSecurityDeposit_0() {

        FinanceSettings financeSettings = new FinanceSettings();
        financeSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ZERO);
        financeSettings.setMaxAmountPerCustomer(BigDecimal.ONE);
        try {
            financeSettingsService.checkParameters(financeSettings);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
        }

    }

    @Test
    public void checkFinanceSettingsService_MaxAmountPerCustomer_0() {

        FinanceSettings financeSettings = new FinanceSettings();
        financeSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ONE);
        financeSettings.setMaxAmountPerCustomer(BigDecimal.ZERO);
        try {
            financeSettingsService.checkParameters(financeSettings);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
        }
    }
}
