package org.meveo.apiv2.accounts.service;

import static org.hamcrest.CoreMatchers.any;
import static org.meveo.apiv2.accounts.ImmutableConsumerInput.builder;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.ParentInput;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.meveo.apiv2.accounts.ImmutableParentInput;

@RunWith(MockitoJUnitRunner.class)
public class AccountsManagementApiServiceTest {

    @Spy
    @InjectMocks
    private AccountsManagementApiService accountsManagementApiService;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserAccountService userAccountService;
    
    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerAccountService customerAccountService;
    
    @Mock
    private BillingAccountService billingAccountService;
    
    @Mock
    private AuditLogService auditLogService;
    
    @Mock
    private FinanceSettingsService financeSettingsService;
    
    @Mock
    @CurrentUser
    private MeveoUser currentUser;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setup() {
        UserAccount ua1 = mock(UserAccount.class);
        ua1.setId(1L);
        ua1.setCode("UA1");
        ua1.setIsConsumer(Boolean.TRUE);

        UserAccount ua2 = mock(UserAccount.class);
        ua2.setId(2L);
        ua2.setCode("UA2");

        UserAccount ua3 = mock(UserAccount.class);
        ua3.setId(3L);
        ua3.setCode("UA3");
        ua3.setIsConsumer(Boolean.FALSE);
        
        Subscription su1 = mock(Subscription.class);
        su1.setCode("SU");
        su1.setDescription("The subscription");
        su1.setUserAccount(ua1);

        Subscription terminatedSU = new Subscription();
        terminatedSU.setId(1L);
        terminatedSU.setCode("TR_SU");
        terminatedSU.setDescription("The subscription");
        terminatedSU.setUserAccount(ua1);
        terminatedSU.setStatus(SubscriptionStatusEnum.RESILIATED);

        when(userAccountService.findById(0L)).thenReturn(null);
        when(subscriptionService.findByCode(eq("TR_SU"), anyList())).thenReturn(terminatedSU);
    }

    @Test(expected = ValidationException.class)
    public void test_transferSubscription_with_consumerInput_null() {
        accountsManagementApiService.transferSubscription(null, null, OpenTransactionsActionEnum.NONE);
    }

    @Test(expected = ValidationException.class)
    public void test_transferSubscription_with_consumerInput_empty() {
        ConsumerInput input = builder().build();
        accountsManagementApiService.transferSubscription(null, input, OpenTransactionsActionEnum.NONE);
    }

    @Test(expected = ValidationException.class)
    public void test_transferSubscription_with_consumerInput_all_filled() {
        ConsumerInput input = builder().consumerId(1L).consumerCode("code").build();
        accountsManagementApiService.transferSubscription(null, input, OpenTransactionsActionEnum.NONE);
    }

    @Test(expected = NotFoundException.class)
    public void test_transferSubscription_with_a_non_existent_ua_id() {
        ConsumerInput input = builder().consumerId(0L).build();
        accountsManagementApiService.transferSubscription(null, input, OpenTransactionsActionEnum.NONE);
    }

    @Test(expected = BusinessApiException.class)
    public void test_transferSubscription_with_a_non_consumer_ua() {
        UserAccount ua = new UserAccount();
        ua.setId(1L);
        ua.setCode("UA1");
        ua.setIsConsumer(Boolean.FALSE);

        ConsumerInput input = builder().consumerId(1L).build();
        when(userAccountService.findById(any())).thenReturn(ua);
        accountsManagementApiService.transferSubscription("TR_SU", input, OpenTransactionsActionEnum.NONE);
    }
    
    @Test
    public void test_transferSubscription_with_a_terminated_sub() {
        expectedEx.expect(ConflictException.class);
        expectedEx.expectMessage("Cannot move a terminated subscription {id=1, code=TR_SU}");

        UserAccount ua =  new UserAccount();
        ua.setId(1L);
        ua.setCode("UA1");
        ua.setIsConsumer(Boolean.TRUE);

        ConsumerInput input = builder().consumerId(1L).build();
        when(userAccountService.findById(any())).thenReturn(ua);
        accountsManagementApiService.transferSubscription("TR_SU", input, OpenTransactionsActionEnum.NONE);
    }
    
    @Test(expected = EntityDoesNotExistsException.class)
    public void test_getAllParentCustomers_with_a_non_existent_customer() {
    	when(customerService.findByCode("code")).thenReturn(null);
        accountsManagementApiService.getAllParentCustomers("code");
    }
    
    @Test
    public void test_getAllParentCustomers_ok() {
    	Customer parent = new Customer();
    	parent.setId(1L);
    	parent.setCode("codeParent");
    	
    	Customer customer = new Customer();
    	customer.setId(2L);
    	customer.setCode("codeCustomer");
    	customer.setParentCustomer(parent);
    	
    	when(customerService.findByCode("codeCustomer")).thenReturn(customer);
        accountsManagementApiService.getAllParentCustomers("codeCustomer");
        Assert.assertTrue("All good", true );
    }
    
    @Test(expected = ValidationException.class)
    public void test_changeCustomerAccountParentAccount_with_parentInput_empty() {
        ParentInput parentInput = ImmutableParentInput.builder().build();
        accountsManagementApiService.changeCustomerAccountParentAccount("codeCA", parentInput);
    }
    
    @Test(expected = ValidationException.class)
    public void test_changeCustomerAccountParentAccount_incompatible_data_parentInput() {
        ParentInput parentInput = ImmutableParentInput.builder().parentId(1L).parentCode("CODE01").build();
        
        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        ca.setCode("codeCA");
        when(customerAccountService.findByCode(any(), any())).thenReturn(ca);
        
        Customer cust = new Customer();
        cust.setId(1L);
        cust.setCode("codeCustomer");
        when(customerService.findById(any(), any())).thenReturn(cust);
        
        accountsManagementApiService.changeCustomerAccountParentAccount("codeCA", parentInput);
    }
    
    @Test(expected = BusinessApiException.class)
    public void test_changeCustomerAccountParentAccount_huge_entity() {
        ParentInput parentInput = ImmutableParentInput.builder().parentId(1L).parentCode("codeCustomer").markOpenWalletOperationsToRerate(true).build();
        
        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        ca.setCode("codeCA");
        when(customerAccountService.findByCode(any(), any())).thenReturn(ca);
        when(customerAccountService.findById(any(), any())).thenReturn(ca);
        
        Customer cust = new Customer();
        cust.setId(1L);
        cust.setCode("codeCustomer");
        ca.setCustomer(cust);
        when(customerService.findById(any(), any())).thenReturn(cust);
        
        Mockito.doReturn(ca).when(customerAccountService).update(ca);
        
        Mockito.doNothing().when(auditLogService).create(any());
        
        when(financeSettingsService.isEntityWithHugeVolume("WalletOperation")).thenReturn(true);
        
        accountsManagementApiService.changeCustomerAccountParentAccount("codeCA", parentInput);
    }
    
    public void test_changeCustomerAccountParentAccount_ok() {
        ParentInput parentInput = ImmutableParentInput.builder().parentId(1L).parentCode("codeCustomer").build();
        
        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        ca.setCode("codeCA");
        when(customerAccountService.findByCode(any(), any())).thenReturn(ca);
        when(customerAccountService.findById(any(), any())).thenReturn(ca);
        
        Customer cust = new Customer();
        cust.setId(1L);
        cust.setCode("codeCustomer");
        ca.setCustomer(cust);
        when(customerService.findById(any(), any())).thenReturn(cust);
        
        Mockito.doReturn(ca).when(customerAccountService).update(ca);
        
        Mockito.doNothing().when(auditLogService).create(any());
        
        accountsManagementApiService.changeCustomerAccountParentAccount("codeCA", parentInput);
        Assert.assertTrue("All good", true );
    }
    
    @Test(expected = ValidationException.class)
    public void test_changeBillingAccountParentAccount_with_parentInput_empty() {
        ParentInput parentInput = ImmutableParentInput.builder().build();
        accountsManagementApiService.changeBillingAccountParentAccount("codeBA", parentInput);
    }
    
    @Test(expected = ValidationException.class)
    public void test_changeBillingAccountParentAccount_incompatible_data_parentInput() {
        ParentInput parentInput = ImmutableParentInput.builder().parentId(1L).parentCode("CODE01").build();
        
        BillingAccount ba = new BillingAccount();
        ba.setId(1L);
        ba.setCode("codeBA");
        when(billingAccountService.findByCode(any())).thenReturn(ba);
        
        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        ca.setCode("codeCA");
        when(customerAccountService.findById(any(), any())).thenReturn(ca);
        
        accountsManagementApiService.changeBillingAccountParentAccount("codeBA", parentInput);
    }
    
    @Test(expected = BusinessApiException.class)
    public void test_changeBillingAccountParentAccount_huge_entity() {
        ParentInput parentInput = ImmutableParentInput.builder().parentId(1L).parentCode("codeCA").markOpenWalletOperationsToRerate(true).build();
        
        BillingAccount ba = new BillingAccount();
        ba.setId(1L);
        ba.setCode("codeBA");
        when(billingAccountService.findByCode(any())).thenReturn(ba);
        when(billingAccountService.findById(any())).thenReturn(ba);
        
        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        ca.setCode("codeCA");
        ba.setCustomerAccount(ca);
        when(customerAccountService.findById(any(), any())).thenReturn(ca);
        
        Mockito.doReturn(ba).when(billingAccountService).update(ba);
        Mockito.doNothing().when(subscriptionService).removePaymentMethodLink(ba);

        
        Mockito.doNothing().when(auditLogService).create(any());
        
        when(financeSettingsService.isEntityWithHugeVolume("WalletOperation")).thenReturn(true);
        
        accountsManagementApiService.changeBillingAccountParentAccount("codeBA", parentInput);
    }
    
    public void test_changeBillingAccountParentAccount_ok() {
        ParentInput parentInput = ImmutableParentInput.builder().parentId(1L).parentCode("codeCA").build();
        
        BillingAccount ba = new BillingAccount();
        ba.setId(1L);
        ba.setCode("codeBA");
        when(billingAccountService.findByCode(any())).thenReturn(ba);
        when(billingAccountService.findById(any())).thenReturn(ba);
        
        CustomerAccount ca = new CustomerAccount();
        ca.setId(1L);
        ca.setCode("codeCA");
        ba.setCustomerAccount(ca);
        when(customerAccountService.findById(any(), any())).thenReturn(ca);
        
        Mockito.doReturn(ba).when(billingAccountService).update(ba);
        Mockito.doNothing().when(subscriptionService).removePaymentMethodLink(ba);
        
        Mockito.doNothing().when(auditLogService).create(any());
        
        accountsManagementApiService.changeBillingAccountParentAccount("codeBA", parentInput);
        Assert.assertTrue("All good", true );
    }
}