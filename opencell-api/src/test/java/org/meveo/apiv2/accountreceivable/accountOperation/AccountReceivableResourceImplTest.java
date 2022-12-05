package org.meveo.apiv2.accountreceivable.accountOperation;

import static java.util.Optional.of;
import static jakarta.ws.rs.core.Response.Status.OK;
import static jakarta.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.junit.runner.RunWith;
import org.meveo.apiv2.AcountReceivable.*;
import org.meveo.apiv2.AcountReceivable.CustomerAccount;
import org.meveo.apiv2.accountreceivable.*;
import org.meveo.model.payments.*;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.ws.rs.core.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountReceivableResourceImplTest {

    @InjectMocks
    private AccountReceivableResourceImpl accountOperationResource;

    @Mock
    private AccountOperationApiService accountOperationApiService;

    @Test
    public void shouldAssignAccountOperation() {
        CustomerAccount customerAccount = ImmutableCustomerAccount.builder().id(1L).code("CODE01").build();
        CustomerAccountInput customerAccountInput = ImmutableCustomerAccountInput.builder()
                .customerAccount(customerAccount)
                .build();
        AccountOperation accountOperation = new AccountOperation();
        org.meveo.model.payments.CustomerAccount entity = new org.meveo.model.payments.CustomerAccount();
        entity.setId(1L);
        entity.setCode("CODE01");
        accountOperation.setCustomerAccount(entity);
        accountOperation.setId(40L);
        Mockito.when(accountOperationApiService
                .assignAccountOperation(40L, customerAccount)).thenReturn(of(accountOperation));
        Response response = accountOperationResource.assignAccountOperation(40L, customerAccountInput);
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(accountOperation, response.getEntity());
    }

    @Test
    public void shouldFailToAssignAccountOperationMissingParams() {
        CustomerAccountInput customerAccountInput = ImmutableCustomerAccountInput.builder().build();
        Response response = accountOperationResource.assignAccountOperation(1L, customerAccountInput);
        assertEquals(PRECONDITION_FAILED.getStatusCode(), response.getStatus());
        assertEquals(response.getEntity(),
                "{\"actionStatus\":{\"status\":\"FAILED\",\"message\":\"Missing customer account parameters\"}}");
    }
}