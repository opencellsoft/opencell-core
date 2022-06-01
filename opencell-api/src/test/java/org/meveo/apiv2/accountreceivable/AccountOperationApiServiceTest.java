package org.meveo.apiv2.accountreceivable;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.*;
import org.junit.runner.RunWith;
import org.meveo.apiv2.AcountReceivable.*;
import org.meveo.apiv2.AcountReceivable.CustomerAccount;
import org.meveo.model.payments.*;
import org.meveo.service.payments.impl.*;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class AccountOperationApiServiceTest {

    @InjectMocks
    private AccountOperationApiService accountOperationApiService;

    @Mock
    private AccountOperationService accountOperationService;

    @Mock
    private CustomerAccountService customerAccountService;

    @Before
    public void setUp() {
        org.meveo.model.payments.CustomerAccount customerAccount = new org.meveo.model.payments.CustomerAccount();
        customerAccount.setId(1L);
        customerAccount.setCode("1L");
        AccountOperation accountOperation = new AccountOperation();
        AccountOperation updatedAO = new AccountOperation();
        accountOperation.setId(1L);
        updatedAO.setId(1L);
        updatedAO.setCustomerAccount(customerAccount);
        Mockito.when(customerAccountService.findById(1L)).thenReturn(customerAccount);
        Mockito.when(accountOperationService.findById(1L)).thenReturn(accountOperation);
        Mockito.when(accountOperationService.update(any())).thenReturn(updatedAO);
    }

    @Test
    public void shouldAssignAccountOperationToCustomerAccount() {
        CustomerAccount customerAccount = ImmutableCustomerAccount.builder().id(1L).code("CODE").build();
        AccountOperation updatedAO =
                accountOperationApiService.assignAccountOperation(1L, customerAccount).get();
        assertEquals(1L, updatedAO.getCustomerAccount().getId().longValue());
    }

    @Test(expected = NotFoundException.class)
    public void shouldFailToAssignAccountOperationToCustomerAccountAccountOperationNotFound() {
        CustomerAccount customerAccount = ImmutableCustomerAccount.builder().id(1L).code("CODE").build();
        Mockito.when(accountOperationService.findById(1L)).thenReturn(null);
        accountOperationApiService.assignAccountOperation(1L, customerAccount).get();
    }

    @Test(expected = NotFoundException.class)
    public void shouldFailToAssignAccountOperationCustomerAccountNotFound() {
        CustomerAccount customerAccount = ImmutableCustomerAccount.builder().id(1L).code("CODE").build();
        Mockito.when(customerAccountService.findById(1L)).thenReturn(null);
        accountOperationApiService.assignAccountOperation(1L, customerAccount).get();
    }
}