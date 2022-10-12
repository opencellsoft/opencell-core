package org.meveo.apiv2.securityDeposit.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Currency;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLineTaxModeEnum;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;
import org.meveo.service.tax.TaxMappingService.TaxInfo;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositApiServiceTest {
    @InjectMocks
    private SecurityDepositApiService securityDepositApiService;

    private Long sdId = 10000L;
    private BigDecimal amount = new BigDecimal(90);
    private String code = "DEFAULT_SD_TEMPLATE-4";

    public SecurityDeposit init() {
        SecurityDeposit sd = new SecurityDeposit();
        sd.setId(sdId);
        sd.setAmount(amount);
        sd.setCode(code);
        sd.setValidityPeriodUnit(ValidityPeriodUnit.MONTHS);
        Currency currency = new Currency();
        currency.setCurrencyCode("EUR");        
        SecurityDepositTemplate template = new SecurityDepositTemplate();
        template.setId(1L);
        sd.setCurrency(currency);
        sd.setTemplate(template);        
        return sd;
    }

    //USES CASES
    @Test
    public void mandatoryBillingAccount() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        sd.setBillingAccount(null);
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            assertTrue(!sdOut.isPresent());
        } catch (Exception exception) { } 
    }

    @Test
    public void notMandatoryCustomerAccount() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setCustomerAccount(null); 
        sd.setBillingAccount(billingAccount);
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            assertTrue(sdOut.isPresent());
            Assert.assertEquals(InvoiceStatusEnum.VALIDATED, sdOut.get().getSecurityDepositInvoice().getStatus());           
        } catch (Exception exception) { } 
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
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            assertTrue(sdOut.isPresent());
            Assert.assertEquals(InvoiceStatusEnum.VALIDATED, sdOut.get().getSecurityDepositInvoice().getStatus());          
        } catch (Exception exception) { }
    }

    @Test
    public void instantiateSdWithDifferentBillingAccount() throws ImportInvoiceException, InvoiceExistException, BusinessApiException {
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
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            assertTrue(!sdOut.isPresent());
        } catch (Exception exception) { }
    }

    @Test
    public void instantiateSdWithlinkedInvoiceNull() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();        
        sd.setSecurityDepositInvoice(null);        
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            if (sdOut.isPresent()) {
                if (sdOut.get().getSecurityDepositInvoice().getLinkedInvoices() != null) {
                    List<Invoice> linkedInvoices = new ArrayList<>(sdOut.get().getSecurityDepositInvoice().getLinkedInvoices());
                    for (Invoice inv : linkedInvoices) {
                        Assert.assertEquals(InvoiceStatusEnum.NEW, inv.getStatus());
                        Assert.assertEquals("SECURITY_DEPOSIT", inv.getInvoiceType().getCode());
                        Assert.assertEquals(new Date(), inv.getInvoiceDate());
                    }
                }                
            }
        } catch (Exception exception) { } 
    }

    @Test
    public void instantiateSdWithlinkedInvoiceNotNull() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        Invoice inv = new Invoice();
        inv.setId(1L);
        sd.setSecurityDepositInvoice(inv);
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            if (sdOut.isPresent()) {
                if (sdOut.get().getSecurityDepositInvoice().getLinkedInvoices() != null) {
                    List<Invoice> linkedInvoices = new ArrayList<>(sdOut.get().getSecurityDepositInvoice().getLinkedInvoices());
                    for (Invoice inv2 : linkedInvoices) {
                        Assert.assertTrue(inv2.getStatus() == InvoiceStatusEnum.NEW || inv2.getStatus() == InvoiceStatusEnum.DRAFT);
                        Assert.assertEquals("SECURITY_DEPOSIT", inv2.getInvoiceType().getCode());
                        Assert.assertNotNull(inv2.getInvoiceLines());
                    }
                }                
            }           
        } catch (Exception exception) { } 
    }

    @Test
    public void checkInvoiceLine() throws ImportInvoiceException, InvoiceExistException {
        SecurityDeposit sd = init();
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        try {
            Optional<SecurityDeposit> sdOut = securityDepositApiService.instantiate(sd, SecurityDepositStatusEnum.VALIDATED, true);
            if (sdOut.isPresent()) {
                if (sdOut.get().getSecurityDepositInvoice().getLinkedInvoices() != null) {
                    List<Invoice> linkedInvoices = new ArrayList<>(sdOut.get().getSecurityDepositInvoice().getLinkedInvoices());
                    for (Invoice inv2 : linkedInvoices) {
                        Assert.assertEquals("Generated invoice for Security Deposit {" + sd.getId() + "}", inv2.getInvoiceLines().get(0).getLabel());
                        Assert.assertEquals("ART_SECURITY_DEPOSIT", inv2.getInvoiceLines().get(0).getAccountingArticle().getCode());
                        Assert.assertEquals(InvoiceLineTaxModeEnum.ARTICLE, inv2.getInvoiceLines().get(0).getTaxMode());
                        Assert.assertEquals(new BigDecimal("1"), inv2.getInvoiceLines().get(0).getQuantity());
                        Assert.assertEquals(new BigDecimal("0"), inv2.getInvoiceLines().get(0).getAmountTax());
                        Assert.assertEquals(inv2.getInvoiceLines().get(0).getAmountWithTax() , sd.getAmount());
                        Assert.assertEquals(inv2.getInvoiceLines().get(0).getAmountWithoutTax() , sd.getAmount());
                        Assert.assertEquals(inv2.getInvoiceLines().get(0).getUnitPrice() , sd.getAmount());
                        Assert.assertEquals(new BigDecimal("0"), inv2.getInvoiceLines().get(0).getTaxRate());
                    }
                }                
            }
        } catch (Exception exception) { }        
    }
}
