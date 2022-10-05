package org.meveo.apiv2.securityDeposit.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositApiServiceTest {
    @InjectMocks
    private SecurityDepositApiService securityDepositApiService;
    
    private Long sdId = 10000L;
    private BigDecimal amount = new BigDecimal(90);
    private String code = "DEFAULT_SD_TEMPLATE-4";
    
    @Test
    public void testInstantiate() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = new SecurityDeposit();
        sd.setId(sdId);
        sd.setAmount(amount);
        sd.setCode(code);
        sd.setValidityPeriodUnit(ValidityPeriodUnit.MONTHS);
        Currency currency = new Currency();
        currency.setCurrencyCode("EUR");
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setCode("OAU4494");
        SecurityDepositTemplate template = new SecurityDepositTemplate();
        template.setId(1L);
        sd.setCurrency(currency);
        sd.setCustomerAccount(customerAccount);
        sd.setBillingAccount(billingAccount);
        sd.setTemplate(template);

        Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd);
        assertTrue(sdOut.isPresent());
        Assert.assertEquals(sdOut.get().getSecurityDepositInvoice().getStatus(), InvoiceStatusEnum.VALIDATED);
    }
    
}
