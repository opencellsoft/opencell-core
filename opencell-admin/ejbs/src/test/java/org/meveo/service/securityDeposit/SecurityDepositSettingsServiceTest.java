package org.meveo.service.securityDeposit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.securityDeposit.SecurityDepositSettings;
import org.meveo.service.base.BusinessService;
import org.meveo.service.securityDeposit.impl.SecurityDepositSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositSettingsServiceTest {

    @InjectMocks
    private SecurityDepositSettingsService securityDepositSettingsService;

    @Test
    public void createSecurityDepositSettingsService()
    {
        SecurityDepositSettingsService businessService = Mockito.spy(securityDepositSettingsService);
        SecurityDepositSettings securityDepositSettings = new SecurityDepositSettings();
        securityDepositSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ONE);
        securityDepositSettings.setMaxAmountPerCustomer(BigDecimal.ONE);
        Mockito.doNothing().when((BusinessService)businessService).create(securityDepositSettings);
        businessService.create(securityDepositSettings);


    }

    @Test
    public void updateSecurityDepositSettingsService()
    {
        SecurityDepositSettingsService businessService = Mockito.spy(securityDepositSettingsService);
        SecurityDepositSettings securityDepositSettings = new SecurityDepositSettings();
        securityDepositSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ONE);
        securityDepositSettings.setMaxAmountPerCustomer(BigDecimal.ONE);
        Mockito.doReturn(null).when((BusinessService)businessService).update(securityDepositSettings);
        businessService.update(securityDepositSettings);

    }

    @Test
    public void checkSecurityDepositSettingsService_MaxAmountPerSecurityDeposit_0()
    {


        SecurityDepositSettings securityDepositSettings = new SecurityDepositSettings();
        securityDepositSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ZERO);
        securityDepositSettings.setMaxAmountPerCustomer(BigDecimal.ONE);
        try {
            securityDepositSettingsService.checkParameters(securityDepositSettings);
        }catch (Exception exception)
        {
            Assert.assertTrue(exception instanceof InvalidParameterException);
        }



    }


    @Test
    public void checkSecurityDepositSettingsService_MaxAmountPerCustomer_0()
    {


        SecurityDepositSettings securityDepositSettings = new SecurityDepositSettings();
        securityDepositSettings.setMaxAmountPerSecurityDeposit(BigDecimal.ONE);
        securityDepositSettings.setMaxAmountPerCustomer(BigDecimal.ZERO);
        try {
            securityDepositSettingsService.checkParameters(securityDepositSettings);
        }catch (Exception exception)
        {
            Assert.assertTrue(exception instanceof InvalidParameterException);
        }



    }
}
