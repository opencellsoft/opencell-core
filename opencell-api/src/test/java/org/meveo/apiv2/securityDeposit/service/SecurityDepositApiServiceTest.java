package org.meveo.apiv2.securityDeposit.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.payment.PaymentApi;
import org.meveo.apiv2.billing.BasicInvoice;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositCreditInput;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

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

    @Mock
    private AccountOperationService accountOperationServiceMock;

    @Mock
    private PaymentApi paymentApiMock;

    @Mock
    private PaymentService paymentServiceMock;

    @Mock
    private AuditLogService auditLogServiceMock;

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
    	assertEquals(InvoiceStatusEnum.VALIDATED, sd.getSecurityDepositAdjustment().getStatus());
    }

    @Test
    public void testCredit() throws BusinessException, ImportInvoiceException, InvoiceExistException, IOException, MeveoApiException, NoAllOperationUnmatchedException, UnbalanceAmountException {
    	SecurityDeposit sd = init();
    	BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        sd.setCurrentBalance(BigDecimal.ONE);
        
    	when(securityDepositServiceMock.refreshOrRetrieve(sd)).thenReturn(sd);
    	when(securityDepositServiceMock.findById(anyLong())).thenReturn(sd);
    	when(paymentApiMock.createPayment(any(PaymentDto.class))).thenReturn(1L);
    	
    	SecurityDepositCreditInput input = ImmutableSecurityDepositCreditInput.builder()
    										.amountToCredit(BigDecimal.valueOf(100))
    										.bankLot("@today-opencell.admin")
    										.customerAccountCode("customerAccountCode")
    										.isToMatching(true)
    										.occTemplateCode("CRD_SD")
    										.paymentInfo("pi1")
    										.paymentInfo1("pi1")
    										.paymentInfo2("pi2")
    										.paymentInfo3("pi3")
    										.paymentInfo4("pi4")
    										.paymentInfo5("pi5")
    										.paymentMethod(PaymentMethodEnum.CHECK)
    										.reference("ref")
    										.build();
    	
    	SecurityDeposit result = securityDepositApiService.credit(1L, input);
    	assertNotNull(result);
    }
}
