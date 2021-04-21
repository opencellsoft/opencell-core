package org.meveo.apiv2.billing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.billing.service.DiscountPlanInstanceApiService;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.SearchResult;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.IEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.DiscountPlanInstanceStatusEnum;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscountPlanInstanceApiServiceTest {

    @Spy
    @InjectMocks
    private GenericApiLoadService loadService;

    @Spy
    @InjectMocks
    private DiscountPlanInstanceApiService DiscountPlanInstanceApiService;

    @Mock
    private GenericApiPersistenceDelegate persistenceDelegate;

    @Mock
    private GenericApiAlteringService genericApiAlteringService;

    @Mock
    private EntityManagerWrapper entityManagerWrapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private DiscountPlanService discountPlanService;

    @Mock
    private BillingAccountService billingAccountService;

    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        when(persistenceDelegate.list(any(), any())).thenAnswer(new Answer<SearchResult>() {

            @Override
            public SearchResult answer(InvocationOnMock invocation) throws Throwable {
                PaginationConfiguration arg = invocation.getArgument(1);
                List<DiscountPlanInstance> discountPlanInstances = new ArrayList<>();
                if (arg.getFilters() != null && arg.getFilters().containsKey("billingAccount.id")) {
                    discountPlanInstances.add(getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum.APPLIED, false));
                    discountPlanInstances.add(getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum.ACTIVE, false));
                    discountPlanInstances.add(getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum.IN_USE, true));
                } else {
                    discountPlanInstances.add(getDiscountPlanForSubscription(DiscountPlanInstanceStatusEnum.APPLIED, false));
                    discountPlanInstances.add(getDiscountPlanForSubscription(DiscountPlanInstanceStatusEnum.ACTIVE, false));
                    discountPlanInstances.add(getDiscountPlanForSubscription(DiscountPlanInstanceStatusEnum.IN_USE, true));
                }
                SearchResult result = new SearchResult(discountPlanInstances, discountPlanInstances.size());
                return result;
            }
        });
        when(persistenceDelegate.find(any(), any(), any())).thenAnswer(new Answer<IEntity>() {

            @Override
            public IEntity answer(InvocationOnMock invocation) throws Throwable {
                Long arg = invocation.getArgument(1);
                if (arg == 1L) {
                    DiscountPlanInstance dpi = getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum.APPLIED, false);
                    dpi.setId(1L);
                    return dpi;
                } else {
                    DiscountPlanInstance dpi = getDiscountPlanForSubscription(DiscountPlanInstanceStatusEnum.ACTIVE, false);
                    dpi.setId(2L);
                    return dpi;
                }
            }
        });
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
        when(entityManagerWrapper.getEntityManager().find(any(), any())).thenAnswer(new Answer<IEntity>() {

            @Override
            public IEntity answer(InvocationOnMock invocation) throws Throwable {
                Long arg = invocation.getArgument(1);
                if (arg == 1L) {
                    DiscountPlanInstance dpi = getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum.APPLIED, false);
                    dpi.setId(1L);
                    return dpi;
                } else {
                    DiscountPlanInstance dpi = getDiscountPlanForSubscription(DiscountPlanInstanceStatusEnum.APPLIED, false);
                    dpi.setId(2L);
                    return dpi;
                }
            }
        });
        when(persistenceDelegate.update(any(), any())).thenAnswer(new Answer<IEntity>() {

            @Override
            public IEntity answer(InvocationOnMock invocationOnMock) throws Throwable {
                DiscountPlanInstance dpi = getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum.IN_USE, false);
                dpi.setId(1L);
                return dpi;
            }
        });
        when(discountPlanService.findById(any())).thenReturn(getDiscountPlan(DiscountPlanTypeEnum.PROMO_CODE, DiscountPlanStatusEnum.ACTIVE, false));
    }

    private DiscountPlan getDiscountPlan(DiscountPlanTypeEnum type, DiscountPlanStatusEnum status, boolean nullDates) {
        DiscountPlan discountPlan = new DiscountPlan();
        discountPlan.setCode("DP");
        discountPlan.setDiscountPlanType(type);
        discountPlan.setStatus(status);
        discountPlan.setStatusDate(new Date());
        discountPlan.setInitialQuantity(0L);
        if (!nullDates) {
            discountPlan.setStartDate(new Date());
            discountPlan.setEndDate(DateUtils.addMonths(new Date(), 3));
        }
        return discountPlan;
    }

    private DiscountPlanInstance getDiscountPlanForBillingAccount(DiscountPlanInstanceStatusEnum status, boolean nullDates) {
        BillingAccount ba = Mockito.mock(BillingAccount.class);
        DiscountPlan dp = Mockito.mock(DiscountPlan.class);
        DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
        discountPlanInstance.setApplicationCount(0L);
        discountPlanInstance.setStatus(status);
        discountPlanInstance.setStatusDate(new Date());
        discountPlanInstance.setDiscountPlan(dp);
        discountPlanInstance.setBillingAccount(ba);
        if (!nullDates) {
            discountPlanInstance.setStartDate(new Date());
            discountPlanInstance.setEndDate(DateUtils.addMonths(new Date(), 3));
        }
        return discountPlanInstance;
    }

    private DiscountPlanInstance getDiscountPlanForSubscription(DiscountPlanInstanceStatusEnum status, boolean nullDates) {
        Subscription sub = mock(Subscription.class);
        DiscountPlan dp = Mockito.mock(DiscountPlan.class);
        DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
        discountPlanInstance.setApplicationCount(0L);
        discountPlanInstance.setStatus(status);
        discountPlanInstance.setStatusDate(new Date());
        discountPlanInstance.setDiscountPlan(dp);
        discountPlanInstance.setSubscription(sub);
        if (!nullDates) {
            discountPlanInstance.setStartDate(new Date());
            discountPlanInstance.setEndDate(DateUtils.addMonths(new Date(), 3));
        }
        return discountPlanInstance;
    }

    @Test
    public void test_get_all_discount_plan_by_billingAccount() throws JsonProcessingException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("billingAccount.id", 1);
        PaginationConfiguration searchConfig = new PaginationConfiguration(1, 10, filters, "text", List.of("status", "discountPlan"));
        String jsonResponse = loadService.findPaginatedRecords(true, DiscountPlanInstance.class, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals(3, map.get("total"));
        List<Map<String, Object>> discountPlans = (List<Map<String, Object>>) map.get("data");

        assertEquals("APPLIED", discountPlans.get(0).get("status"));
        assertEquals(0, discountPlans.get(0).get("applicationCount"));
        assertNotNull(discountPlans.get(0).get("billingAccount"));
        assertNotNull(discountPlans.get(0).get("discountPlan"));
        assertNotNull(discountPlans.get(0).get("startDate"));
        assertNotNull(discountPlans.get(0).get("endDate"));
        assertNull(discountPlans.get(2).get("subscription"));

        assertEquals("ACTIVE", discountPlans.get(1).get("status"));
        assertEquals(0, discountPlans.get(1).get("applicationCount"));
        assertNotNull(discountPlans.get(1).get("billingAccount"));
        assertNotNull(discountPlans.get(1).get("discountPlan"));
        assertNotNull(discountPlans.get(1).get("startDate"));
        assertNotNull(discountPlans.get(1).get("endDate"));
        assertNull(discountPlans.get(2).get("subscription"));

        assertEquals("IN_USE", discountPlans.get(2).get("status"));
        assertEquals(0, discountPlans.get(2).get("applicationCount"));
        assertNotNull(discountPlans.get(2).get("billingAccount"));
        assertNotNull(discountPlans.get(2).get("discountPlan"));
        assertNull(discountPlans.get(2).get("startDate"));
        assertNull(discountPlans.get(2).get("endDate"));
        assertNull(discountPlans.get(2).get("subscription"));
    }

    @Test
    public void test_get_all_discount_plan_by_subscription() throws JsonProcessingException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("subscription.id", 1);
        PaginationConfiguration searchConfig = new PaginationConfiguration(1, 10, filters, "text", List.of("status", "discountPlan"));
        String jsonResponse = loadService.findPaginatedRecords(true, DiscountPlanInstance.class, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals(3, map.get("total"));
        List<Map<String, Object>> discountPlans = (List<Map<String, Object>>) map.get("data");

        assertEquals("APPLIED", discountPlans.get(0).get("status"));
        assertEquals(0, discountPlans.get(0).get("applicationCount"));
        assertNull(discountPlans.get(0).get("billingAccount"));
        assertNotNull(discountPlans.get(0).get("discountPlan"));
        assertNotNull(discountPlans.get(0).get("startDate"));
        assertNotNull(discountPlans.get(0).get("endDate"));
        assertNotNull(discountPlans.get(2).get("subscription"));

        assertEquals("ACTIVE", discountPlans.get(1).get("status"));
        assertEquals(0, discountPlans.get(1).get("applicationCount"));
        assertNull(discountPlans.get(1).get("billingAccount"));
        assertNotNull(discountPlans.get(1).get("discountPlan"));
        assertNotNull(discountPlans.get(1).get("startDate"));
        assertNotNull(discountPlans.get(1).get("endDate"));
        assertNotNull(discountPlans.get(2).get("subscription"));

        assertEquals("IN_USE", discountPlans.get(2).get("status"));
        assertEquals(0, discountPlans.get(2).get("applicationCount"));
        assertNull(discountPlans.get(2).get("billingAccount"));
        assertNotNull(discountPlans.get(2).get("discountPlan"));
        assertNull(discountPlans.get(2).get("startDate"));
        assertNull(discountPlans.get(2).get("endDate"));
        assertNotNull(discountPlans.get(2).get("subscription"));
    }

    @Test
    public void test_find_discount_instance_billing_account_by_id() throws JsonProcessingException {

        Map<String, Object> filters = new HashMap<>();
        filters.put("billingAccount.id", 1L);
        PaginationConfiguration searchConfig = new PaginationConfiguration(1, 10, filters, "text", List.of("status", "discountPlan"));
        Optional<String> jsonResponse = loadService.findByClassNameAndId(true, DiscountPlanInstance.class, 1L, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(jsonResponse.get(), Map.class);
        Map<String, Object> discountPlanInstance = (Map<String, Object>) data.get("data");
        assertEquals("APPLIED", discountPlanInstance.get("status"));
        assertEquals(0, discountPlanInstance.get("applicationCount"));
        assertNotNull(discountPlanInstance.get("billingAccount"));
        assertNotNull(discountPlanInstance.get("discountPlan"));
        assertNotNull(discountPlanInstance.get("startDate"));
        assertNotNull(discountPlanInstance.get("endDate"));
        assertNull(discountPlanInstance.get("subscription"));

    }

    @Test
    public void test_find_discount_instance_subscription_by_id() throws JsonProcessingException {

        Map<String, Object> filters = new HashMap<>();
        filters.put("subscription.id", 1L);
        PaginationConfiguration searchConfig = new PaginationConfiguration(1, 10, filters, "text", List.of("status", "discountPlan"));
        Optional<String> jsonResponse = loadService.findByClassNameAndId(true, DiscountPlanInstance.class, 2L, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(jsonResponse.get(), Map.class);
        Map<String, Object> discountPlanInstance = (Map<String, Object>) data.get("data");
        assertEquals("ACTIVE", discountPlanInstance.get("status"));
        assertEquals(0, discountPlanInstance.get("applicationCount"));
        assertNull(discountPlanInstance.get("billingAccount"));
        assertNotNull(discountPlanInstance.get("discountPlan"));
        assertNotNull(discountPlanInstance.get("startDate"));
        assertNotNull(discountPlanInstance.get("endDate"));
        assertNotNull(discountPlanInstance.get("subscription"));

    }

    @Test
    public void test_create_discount_plan_instance_billing_account_with_dto() throws JsonProcessingException {
        when(billingAccountService.instantiateDiscountPlan(any(), any())).thenReturn(mock(BillingAccount.class));
        String dto = "{\n" + "    \"discountPlan\": {\n" + "        \"id\": 43\n" + "    },\n" + "    \"status\": \"ACTIVE\",\n" + "    \"applicationCount\": 5\n" + "}";
        BillingAccount ba = mock(BillingAccount.class);
        Optional<Long> id = DiscountPlanInstanceApiService.create(ba, dto);
        assertNotNull(id);
    }

    @Test
    public void test_update_dicount_plan_instance_billing_account_with_dto() throws JsonProcessingException {
        String dto = "{\n" + "    \"discountPlan\": {\n" + "        \"id\": 43\n" + "    },\n" + "    \"status\": \"ACTIVE\",\n" + "    \"applicationCount\": 5\n" + "}";
        BillingAccount ba = mock(BillingAccount.class);
        Optional<Long> id = DiscountPlanInstanceApiService.update(ba, 1L, dto);
        assertNotNull(id);
    }

    @Test
    public void test_delete_dicount_plan_instance_billing_account_with_dto() throws JsonProcessingException {
        BillingAccount ba = mock(BillingAccount.class);
        String result = DiscountPlanInstanceApiService.delete(ba, 1L);
        assertNotNull(result);
    }

    @Test
    public void test_expire_discount_plan_instance_billing_account() {
        BillingAccount ba = mock(BillingAccount.class);
        Optional<Long> id = DiscountPlanInstanceApiService.expire(ba, 1L);
        assertNotNull(id);
    }

    @Test
    public void test_create_discount_plan_instance_subscription_with_dto() throws JsonProcessingException {
        when(subscriptionService.instantiateDiscountPlan(any(), any())).thenReturn(mock(Subscription.class));
        String dto = "{\n" + "    \"discountPlan\": {\n" + "        \"id\": 43\n" + "    },\n" + "    \"status\": \"ACTIVE\",\n" + "    \"applicationCount\": 5\n" + "}";
        Subscription sub = mock(Subscription.class);
        Optional<Long> id = DiscountPlanInstanceApiService.create(sub, dto);
        assertNotNull(id);
    }

    @Test
    public void test_update_dicount_plan_subscription_with_dto() throws JsonProcessingException {
        String dto = "{\n" + "    \"discountPlan\": {\n" + "        \"id\": 43\n" + "    },\n" + "    \"status\": \"ACTIVE\",\n" + "    \"applicationCount\": 5\n" + "}";
        Subscription sub = mock(Subscription.class);
        Optional<Long> id = DiscountPlanInstanceApiService.update(sub, 2L, dto);
        assertNotNull(id);
    }

    @Test
    public void test_delete_dicount_plan_instance_subscription_with_dto() throws JsonProcessingException {
        Subscription sub = mock(Subscription.class);
        String result = DiscountPlanInstanceApiService.delete(sub, 2L);
        assertNotNull(result);
    }

    @Test
    public void test_expire_discount_plan_instance_subscription() {
        Subscription sub = mock(Subscription.class);
        Optional<Long> id = DiscountPlanInstanceApiService.expire(sub, 2L);
        assertNotNull(id);
    }

}
