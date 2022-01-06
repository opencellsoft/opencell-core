package org.meveo.service.securityDeposit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.admin.Currency;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.SecurityTemplateStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.securityDeposit.impl.SecurityDepositTemplateService;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositTemplateServiceTest {

    @InjectMocks
    private SecurityDepositTemplateService securityDepositTemplateService;

    @Test
    public void createSecurityDepositTemplateService()
    {
        SecurityDepositTemplateService businessService = Mockito.spy(securityDepositTemplateService);
        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setAllowValidityPeriod(true);
        securityDepositTemplate.setMinAmount(BigDecimal.valueOf(100));
        securityDepositTemplate.setMinAmount(BigDecimal.valueOf(1000));
        Mockito.doNothing().when((BusinessService)businessService).create(securityDepositTemplate);
        businessService.create(securityDepositTemplate);


    }

    @Test
    public void updateSecurityDepositTemplateService()
    {
        SecurityDepositTemplateService businessService = Mockito.spy(securityDepositTemplateService);
        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setAllowValidityPeriod(true);
        securityDepositTemplate.setMinAmount(BigDecimal.valueOf(100));
        securityDepositTemplate.setMinAmount(BigDecimal.valueOf(1000));
        Mockito.doReturn(null).when((BusinessService)businessService).update(securityDepositTemplate);
        businessService.update(securityDepositTemplate);

    }

    @Test
    public void checkSecurityDepositTemplateService_currency_null()
    {


        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setCurrency(null);
        try {
            securityDepositTemplateService.checkParameters(securityDepositTemplate);
        }catch (Exception exception)
        {
            Assert.assertTrue(exception instanceof EntityDoesNotExistsException);
            Assert.assertEquals("currency does not exist.", exception.getMessage());
        }

    }

@Test
    public void checkSecurityDepositTemplateService_noAllowTrue()
    {


        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setCurrency(new Currency());
        securityDepositTemplate.setAllowValidityDate(false);
        securityDepositTemplate.setAllowValidityPeriod(false);

        try {
            securityDepositTemplateService.checkParameters(securityDepositTemplate);
        }catch (Exception exception)
        {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals("At least allowValidityDate or allowValidityPeriod need to be checked", exception.getMessage());
        }

    }

    
    @Test
    public void checkSecurityDepositTemplateService_minAmount_exceed_maxAmount()
    {


        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setCurrency(new Currency());
        securityDepositTemplate.setAllowValidityDate(true);
        securityDepositTemplate.setAllowValidityPeriod(true);
        securityDepositTemplate.setMinAmount(BigDecimal.TEN);
        securityDepositTemplate.setMaxAmount(BigDecimal.ONE);

        try {
            securityDepositTemplateService.checkParameters(securityDepositTemplate);
        }catch (Exception exception)
        {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals("The min amount cannot exceed the max amount", exception.getMessage());
        }

    }

    @Test
    public void updateStatus(){
        SecurityDepositTemplateService businessService = Mockito.spy(securityDepositTemplateService);
        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setStatus(SecurityTemplateStatusEnum.ACTIVE);
        Mockito.doReturn(securityDepositTemplate).when((BusinessService)businessService).findById(Mockito.any());
        Mockito.doReturn(securityDepositTemplate).when((BusinessService)businessService).update(Mockito.any());

        businessService.updateStatus(Collections.singleton(1L), "DRAFT");
    }

     @Test
    public void checkStatusTransition_archived_to_active(){
        SecurityDepositTemplate securityDepositTemplate = new SecurityDepositTemplate();
        securityDepositTemplate.setStatus(SecurityTemplateStatusEnum.ARCHIVED);
        try {
        securityDepositTemplateService.checkStatusTransition(securityDepositTemplate,  SecurityTemplateStatusEnum.ACTIVE);
        }catch (Exception exception)
        {
            Assert.assertTrue(exception instanceof BusinessException);
            Assert.assertEquals("cannot activate an archived security deposit template", exception.getMessage());
        }

    }


}
