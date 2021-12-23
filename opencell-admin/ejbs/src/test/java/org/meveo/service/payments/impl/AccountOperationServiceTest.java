package org.meveo.service.payments.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.*;
import org.meveo.service.script.ScriptInstanceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountOperationServiceTest {
    @InjectMocks
    private AccountOperationService accountOperationService;

    @Mock
    private Provider appProvider;

    @Test(expected = BusinessException.class)
    public void selectedPaymentMethodDoesNotBelongToTheAccountOperationCustomerAccount() {
        AccountOperation accountOperation = new AccountOperation();
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setPaymentMethods(Arrays.asList(new CheckPaymentMethod(), new CashPaymentMethod()));
        accountOperation.setCustomerAccount(customerAccount);
        Date paymentDate = Date.from(LocalDate.now().plusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        accountOperationService.createDeferralPayments(accountOperation, PaymentMethodEnum.CARD, paymentDate);
    }

    @Test(expected = BusinessException.class)
    public void paymentExceedsAppProviderMaxDelay() {
        AccountOperation accountOperation = new AccountOperation();
        when(appProvider.getMaximumDelay()).thenReturn(1);
        Date paymentDate = Date.from(LocalDate.now().plusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        accountOperationService.createDeferralPayments(accountOperation, null, paymentDate);
    }

    @Test(expected = BusinessException.class)
    public void paymentExceedsAppProviderTheConfiguredMaximumDeferralPerInvoice() {
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setPaymentDeferralCount(1);
        Date paymentDate = Date.from(LocalDate.now().plusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        accountOperationService.createDeferralPayments(accountOperation, null, paymentDate);
    }

    @Test(expected = BusinessException.class)
    public void thePaymentDatePlusThreeDaysMustNotBeSaturdayOrSunday() {
        AccountOperation accountOperation = new AccountOperation();
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setPaymentMethods(Arrays.asList(new DDPaymentMethod(), new CashPaymentMethod()));
        accountOperation.setCustomerAccount(customerAccount);
        accountOperation.setPaymentDeferralCount(0);
        when(appProvider.getMaximumDeferralPerInvoice()).thenReturn(1);
        Date paymentDate = Date.from(LocalDate.of(1989, 8, 20).minusDays(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        accountOperationService.createDeferralPayments(accountOperation, PaymentMethodEnum.DIRECTDEBIT, paymentDate);
    }

    @Test
    public void successfullyCreateDeferralPayments() {
        AccountOperation accountOperation = new AccountOperation();
        AccountOperationService spy = spy(accountOperationService);
        doReturn(accountOperation).when(spy).update(accountOperation);
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setPaymentMethods(Arrays.asList(new DDPaymentMethod(), new CashPaymentMethod()));
        accountOperation.setCustomerAccount(customerAccount);
        accountOperation.setPaymentDeferralCount(0);
        when(appProvider.getMaximumDeferralPerInvoice()).thenReturn(1);
        Date paymentDate = Date.from(LocalDate.of(1989, 8, 20).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        spy.createDeferralPayments(accountOperation, PaymentMethodEnum.DIRECTDEBIT, paymentDate);
        assertEquals(accountOperation.getPaymentMethod(), PaymentMethodEnum.DIRECTDEBIT);
        assertEquals(accountOperation.getPaymentDeferralCount(), Integer.valueOf(1));
        assertEquals(accountOperation.getCollectionDate(),paymentDate);
        assertEquals(accountOperation.getPaymentAction(),PaymentActionEnum.PENDING_PAYMENT);
    }
}