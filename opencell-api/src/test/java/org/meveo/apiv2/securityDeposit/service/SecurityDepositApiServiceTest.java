package org.meveo.apiv2.securityDeposit.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositApiServiceTest {

	@InjectMocks
    private SecurityDepositApiService securityDepositApiService;

    @Mock
    private SecurityDepositService securityDepositServiceMock;

    @Mock
    private InvoiceService invoiceServiceMock;

    @Mock
    private EntityManager entityManagerMock;

    @Mock
    private InvoiceApiService invoiceApiServiceMock;

    @Mock
    private ServiceSingleton serviceSingletonMock;

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

    @Test
    public void testRefund() throws MissingParameterException, EntityDoesNotExistsException, BusinessException, ImportInvoiceException, InvoiceExistException, IOException {
    	SecurityDeposit sd = init();
    	BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        sd.setCurrentBalance(BigDecimal.ONE);

        Invoice adjustmentInvoiceMock = new Invoice();
        
    	when(securityDepositServiceMock.refreshOrRetrieve(sd)).thenReturn(sd);
    	when(invoiceServiceMock.createBasicInvoice(any(BasicInvoice.class))).thenReturn(adjustmentInvoiceMock);
    	when(invoiceServiceMock.getEntityManager()).thenReturn(entityManagerMock);
    
    	securityDepositApiService.refund(sd, "motif", SecurityDepositOperationEnum.REFUND_SECURITY_DEPOSIT, SecurityDepositStatusEnum.REFUNDED, "REFUND");
    	
    	assertNotNull(sd.getSecurityDepositAdjustment());
    }
}
