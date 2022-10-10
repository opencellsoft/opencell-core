package org.meveo.apiv2.securityDeposit.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
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
    public SecurityDeposit init() {
        SecurityDeposit sd = new SecurityDeposit();
        sd.setId(sdId);
        sd.setAmount(amount);
        sd.setCode(code);
        sd.setValidityPeriodUnit(ValidityPeriodUnit.MONTHS);
        Currency currency = new Currency();
        currency.setCurrencyCode("EUR");        
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setCode("OAU4494");
        SecurityDepositTemplate template = new SecurityDepositTemplate();
        template.setId(1L);
        sd.setCurrency(currency);
        sd.setCustomerAccount(customerAccount);        
        sd.setTemplate(template);
        
        return sd;
    }
    
    @Test
    public void instantiateSdWithlinkedInvoiceNull() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();        
        sd.setSecurityDepositInvoice(null);        
        Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd);
        assertTrue(sdOut.isPresent());
        Assert.assertTrue(sdOut.get().getSecurityDepositInvoice().getLinkedInvoices() != null);
    }
    
    @Test
    public void instantiateSdWithlinkedInvoiceNotNull() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        Invoice inv = new Invoice();
        inv.setId(1L);
        sd.setSecurityDepositInvoice(inv);        
        Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd);
        assertTrue(sdOut.isPresent());
        Assert.assertEquals(sdOut.get().getSecurityDepositInvoice().getStatus(), InvoiceStatusEnum.VALIDATED);
    }
    
    @Test
    public void instantiateSdWithBillingAccount() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        
        Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd);
        assertTrue(sdOut.isPresent());
        Assert.assertEquals(sdOut.get().getSecurityDepositInvoice().getStatus(), InvoiceStatusEnum.VALIDATED);
    }
    
    @Test
    public void instantiateSdWithSameBillingAccount() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        
        Invoice inv = new Invoice();
        inv.setId(1L);
        inv.setBillingAccount(billingAccount);
        sd.setSecurityDepositInvoice(inv);    
        
        Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd);
        assertTrue(sdOut.isPresent());
        Assert.assertEquals(sdOut.get().getSecurityDepositInvoice().getLinkedInvoices(), InvoiceStatusEnum.VALIDATED);
    }
    
    @Test
    public void instantiateSdWithDifferentBillingAccount() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        BillingAccount billingAccount0 = new BillingAccount();
        billingAccount0.setCode("OAU4495");
        BillingAccount billingAccount1 = new BillingAccount();
        billingAccount1.setCode("OAU4494");        
        sd.setBillingAccount(billingAccount0);        
        Invoice inv = new Invoice();
        inv.setId(1L);
        inv.setBillingAccount(billingAccount1);
        sd.setSecurityDepositInvoice(inv);        
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof BusinessApiException);
            Assert.assertEquals("Linked invoice should be a SECURITY_DEPOSIT", exception.getMessage());
        }        
    }
    
}
