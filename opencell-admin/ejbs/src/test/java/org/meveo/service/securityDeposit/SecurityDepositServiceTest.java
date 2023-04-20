package org.meveo.service.securityDeposit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositPaymentInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositPaymentInput;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositServiceTest {

    @InjectMocks
    private SecurityDepositService securityDepositService;

    @Mock
    private AccountOperationService accountOperationService;

    @Mock
    private EntityManager entityManagerMock;
    
    @Mock
    private EntityManagerWrapper emWrapper;
    
    @Mock
    private MeveoUser currentUser;
    
    @Mock
    private CustomFieldInstanceService customFieldInstanceService;
    
    private Long sdId = 10000L;
    private BigDecimal amount = new BigDecimal(90);
    private String code = "DEFAULT_SD_TEMPLATE-4";

    @Test
    public void FailWhenSecurityDepositDoesntExist() {
        SecurityDepositService businessService = Mockito.spy(securityDepositService);
        Mockito.doReturn(null).when(businessService).findById(-1L);
        try {
            businessService.payInvoices(Long.valueOf(-1), null);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof EntityDoesNotExistsException);
            Assert.assertEquals("security deposit does not exist.", exception.getMessage());
        }

    }


    @Test
    public void FailWhenAccounOperationDoesntExist() {
        SecurityDepositService businessService = Mockito.spy(securityDepositService);
        Mockito.doReturn(new SecurityDeposit()).when(businessService).findById(-1L);
        Mockito.doReturn(null).when(accountOperationService).findById(-1L);
        try {
            businessService.payInvoices(Long.valueOf(-1),
                    ImmutableSecurityDepositPaymentInput.builder()
                            .amount(BigDecimal.ZERO)
                            .accountOperation(ImmutableResource.builder().id(-1L).build()).build());
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof EntityDoesNotExistsException);
            Assert.assertEquals("account operation does not exist.", exception.getMessage());
        }

    }

    @Test
    public void FailWhenSecurityDepositBalanceInsufficient() {

        SecurityDeposit securityDeposit = new SecurityDeposit();
        securityDeposit.setCurrentBalance(BigDecimal.ONE);
        AccountOperation accountOperation = new AccountOperation();

        SecurityDepositService businessService = Mockito.spy(securityDepositService);
        Mockito.doReturn(securityDeposit).when(businessService).findById(-1L);
        Mockito.doReturn(accountOperation).when(accountOperationService).findById(-1L);
        try {
            businessService.payInvoices(Long.valueOf(-1),
                    ImmutableSecurityDepositPaymentInput.builder()
                            .amount(BigDecimal.TEN)
                            .accountOperation(ImmutableResource.builder().id(-1L).build()).build());
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals("The amount to be paid must be less than or equal to the current security deposit balance",
                    exception.getMessage());
        }


    }


    @Test
    public void FailWhenOverPaid() {


        SecurityDeposit securityDeposit = new SecurityDeposit();
        securityDeposit.setCurrentBalance(BigDecimal.valueOf(100L));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(BigDecimal.ONE);
        SecurityDepositService businessService = Mockito.spy(securityDepositService);
        Mockito.doReturn(securityDeposit).when(businessService).findById(-1L);
        Mockito.doReturn(accountOperation).when(accountOperationService).findById(-1L);
        try {
            businessService.payInvoices(Long.valueOf(-1),
                    ImmutableSecurityDepositPaymentInput.builder()
                            .amount(BigDecimal.TEN)
                            .accountOperation(ImmutableResource.builder().id(-1L).build()).build());
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals("The amount to be paid must be less than or equal to the unpaid amount of the invoice",
                    exception.getMessage());
        }

    }

    @Test
    public void failWhenSecurityDepositSubscriptionDoesntMatch() {


        SecurityDepositService businessService = Mockito.spy(securityDepositService);

        SecurityDeposit securityDeposit = new SecurityDeposit();
        securityDeposit.setCurrentBalance(BigDecimal.valueOf(100L));
        Subscription securityDepositSubscription = new Subscription();
        securityDepositSubscription.setId(1L);
        securityDepositSubscription.setCode("1");

        securityDeposit.setSubscription(securityDepositSubscription);
        Mockito.doReturn(securityDeposit).when(businessService).findById(-1L);


        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(BigDecimal.valueOf(100L));
        Invoice accountOperationInvoice = new Invoice();
        Subscription accountOperationSubscription = new Subscription();
        accountOperationSubscription.setId(2L);
        accountOperationSubscription.setCode("2");
        accountOperationInvoice.setSubscription(accountOperationSubscription);
        accountOperation.setInvoices(Collections.singletonList(accountOperationInvoice));
        Mockito.doReturn(accountOperation).when(accountOperationService).findById(-1L);


        try {
            businessService.payInvoices(Long.valueOf(-1),
                    ImmutableSecurityDepositPaymentInput.builder()
                            .amount(BigDecimal.TEN)
                            .accountOperation(ImmutableResource.builder().id(-1L).build()).build());
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals("All invoices should have the same subscription",
                    exception.getMessage());
        }


    }

    @Test
    public void failWhenSecurityDepositServiceInstanceDoesntMatch() {

        SecurityDepositService businessService = Mockito.spy(securityDepositService);

        SecurityDeposit securityDeposit = new SecurityDeposit();
        securityDeposit.setCurrentBalance(BigDecimal.valueOf(100L));
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("1");
        securityDeposit.setSubscription(subscription);

        ServiceInstance securityDepositServiceInstance = new ServiceInstance();
        securityDepositServiceInstance.setId(1L);
        securityDepositServiceInstance.setCode("1");
        securityDeposit.setServiceInstance(securityDepositServiceInstance);

        Mockito.doReturn(securityDeposit).when(businessService).findById(-1L);


        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(BigDecimal.valueOf(100L));
        Invoice accountOperationInvoice = new Invoice();
        ServiceInstance subscriptionServiceInstance = new ServiceInstance();
        subscriptionServiceInstance.setId(2L);
        subscriptionServiceInstance.setCode("2");
        subscription.setServiceInstances(Collections.singletonList(subscriptionServiceInstance));
        accountOperationInvoice.setSubscription(subscription);
        accountOperation.setInvoices(Collections.singletonList(accountOperationInvoice));
        Mockito.doReturn(accountOperation).when(accountOperationService).findById(-1L);


        SecurityDepositPaymentInput input = ImmutableSecurityDepositPaymentInput.builder()
                            .amount(BigDecimal.TEN)
                            .accountOperation(ImmutableResource.builder().id(-1L).build()).build();
        try {
            businessService.payInvoices(Long.valueOf(-1),input);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals("All invoices should have the same serviceInstance",
                    exception.getMessage());
        }


    }

    @Test
    public void testCredit() {
    	
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
    	BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        sd.setCurrentBalance(BigDecimal.valueOf(1000));
        
        CustomerAccount ca = new CustomerAccount();
        sd.setCustomerAccount(ca);
        
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


    	when(securityDepositService.getEntityManager()).thenReturn(entityManagerMock);
    	when(securityDepositService.refreshOrRetrieve(sd)).thenReturn(sd);
    	when(entityManagerMock.merge(any(SecurityDeposit.class))).thenReturn(sd);

    	securityDepositService.credit(sd, input);

    	assertEquals(BigDecimal.valueOf(1100), sd.getCurrentBalance());
    	
    }

    @Test(expected = BusinessException.class)
    public void testCredit_failWhenAmountMoreThanCurrentBalance() {
    	
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
    	BillingAccount billingAccount = new BillingAccount();
        billingAccount.setCode("OAU4494");
        sd.setBillingAccount(billingAccount);
        sd.setCurrentBalance(BigDecimal.valueOf(10));
        
        CustomerAccount ca = new CustomerAccount();
        sd.setCustomerAccount(ca);
        
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


    	when(securityDepositService.getEntityManager()).thenReturn(entityManagerMock);
    	when(securityDepositService.refreshOrRetrieve(sd)).thenReturn(sd);
    	when(entityManagerMock.merge(any(SecurityDeposit.class))).thenReturn(sd);

    	securityDepositService.credit(sd, input);

    	fail("BusinessException should be rised - check on amount to credit");
    	
    }

    @Test
    public void should_createSecurityDepositPaymentAccountOperation_from_SecurityDeposit_And_Amount() {

        //Given
        BigDecimal invoicePaymentAmount = new BigDecimal(10);

        SecurityDeposit securityDeposit = new SecurityDeposit();
        securityDeposit.setCurrentBalance(BigDecimal.valueOf(100L));
        Subscription securityDepositSubscription = new Subscription();
        securityDepositSubscription.setId(1L);
        securityDepositSubscription.setCode("1");

        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(Long.valueOf(1));

        securityDeposit.setCustomerAccount(customerAccount);

        //When
        securityDepositService.createSecurityDepositPaymentAccountOperation(securityDeposit, invoicePaymentAmount);

        ArgumentCaptor<AccountOperation> accountOperationCaptor = ArgumentCaptor.forClass(AccountOperation.class);
        verify(accountOperationService).createAndReturnId(accountOperationCaptor.capture());
        AccountOperation expecTedAccountOperation = accountOperationCaptor.getValue();

        //Then
        Assert.assertEquals(customerAccount.getId(),expecTedAccountOperation.getCustomerAccount().getId());
        Assert.assertEquals(invoicePaymentAmount,expecTedAccountOperation.getAmount());
        Assert.assertEquals(PaymentMethodEnum.CHECK,expecTedAccountOperation.getPaymentMethod());
        Assert.assertEquals(OperationCategoryEnum.CREDIT,expecTedAccountOperation.getTransactionCategory());
        Assert.assertEquals("PAY_SD",expecTedAccountOperation.getCode());

    }


}
