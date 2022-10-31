package org.meveo.service.securityDeposit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositPaymentInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositPaymentInput;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SecurityDepositServiceTest {

    @InjectMocks
    private SecurityDepositService securityDepositService;

    @Mock
    private AccountOperationService accountOperationService;

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
    public void testU() {


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
        Assert.assertEquals(expecTedAccountOperation.getCustomerAccount().getId(), customerAccount.getId());
        Assert.assertEquals(expecTedAccountOperation.getAmount(), invoicePaymentAmount);
        Assert.assertEquals(expecTedAccountOperation.getPaymentMethod(), PaymentMethodEnum.CHECK);
        Assert.assertEquals(expecTedAccountOperation.getTransactionCategory(), OperationCategoryEnum.CREDIT);
        Assert.assertEquals(expecTedAccountOperation.getCode(), "PAY_SD");

    }



}
