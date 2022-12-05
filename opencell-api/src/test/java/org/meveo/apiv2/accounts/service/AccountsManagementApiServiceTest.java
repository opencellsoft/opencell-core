package org.meveo.apiv2.accounts.service;

import static org.hamcrest.CoreMatchers.any;
import static org.meveo.apiv2.accounts.ImmutableConsumerInput.builder;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountsManagementApiServiceTest {

    @Spy
    @InjectMocks
    private AccountsManagementApiService accountsManagementApiService;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserAccountService userAccountService;

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
}