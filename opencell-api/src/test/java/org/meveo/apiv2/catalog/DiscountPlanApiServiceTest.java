package org.meveo.apiv2.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.catalog.service.DiscountPlanApiService;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.generic.services.SearchResult;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.rating.RatingResult;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.inject.Inject;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscountPlanApiServiceTest {

    @Spy
    @InjectMocks
    private GenericApiLoadService loadService;

    @Spy
    @InjectMocks
    private DiscountPlanApiService DiscountPlanApiService;

    @Mock
    private GenericApiPersistenceDelegate persistenceDelegate;

    @Mock
    private GenericApiAlteringService genericApiAlteringService;

    @Mock
    private EntityManagerWrapper entityManagerWrapper;

    @Mock
    private EntityManager entityManager;

    @Before
    public void setup() {
        when(persistenceDelegate.list(any(), any())).thenAnswer(new Answer<SearchResult>() {

            @Override
            public SearchResult answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<DiscountPlan> discountPlans = new ArrayList<>();
                discountPlans.add(getDiscountPlan(1, DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.DRAFT, false));
                discountPlans.add(getDiscountPlan(2, DiscountPlanTypeEnum.PROMO_CODE, DiscountPlanStatusEnum.ACTIVE, false));
                discountPlans.add(getDiscountPlan(3, null, DiscountPlanStatusEnum.IN_USE, true));
                SearchResult result = new SearchResult(discountPlans, discountPlans.size());
                return result;
            }
        });
        when(persistenceDelegate.find(any(), any(), any())).thenAnswer(new Answer<IEntity>() {

            @Override
            public IEntity answer(InvocationOnMock invocation) throws Throwable {
                Class arg = invocation.getArgument(0);
                if (arg.equals(DiscountPlan.class)) {
                    DiscountPlan dp = getDiscountPlan(1, DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.DRAFT, false);
                    dp.setId(1L);
                    return dp;
                } else {
                    DiscountPlanItem dpi = getDiscountPlanItem();
                    return dpi;
                }
            }
        });
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
        when(entityManagerWrapper.getEntityManager().find(any(), any())).thenAnswer(new Answer<IEntity>() {

            @Override
            public IEntity answer(InvocationOnMock invocation) throws Throwable {
                Class arg = invocation.getArgument(0);
                if (arg.equals(DiscountPlan.class)) {
                    DiscountPlan dp = getDiscountPlan(1, DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.DRAFT, false);
                    dp.setId(1L);
                    return dp;
                } else {
                    DiscountPlanItem dpi = getDiscountPlanItem();
                    return dpi;
                }
            }
        });
        when(persistenceDelegate.update(any(), any())).thenAnswer(new Answer<IEntity>() {

            @Override
            public IEntity answer(InvocationOnMock invocationOnMock) throws Throwable {
                DiscountPlan dp = getDiscountPlan(1, DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.DRAFT, false);
                dp.setId(1L);
                return dp;
            }
        });
    }

    private DiscountPlanItem getDiscountPlanItem() {
        DiscountPlanItem dpi = new DiscountPlanItem();
        DiscountPlan dp = new DiscountPlan();
        dp.setId(1L);
        dpi.setDiscountPlan(dp);
        dpi.setDiscountValue(BigDecimal.ONE);
        dpi.setCode("DPI");
        dpi.setDiscountPlanItemType(DiscountPlanItemTypeEnum.FIXED);
        return dpi;
    }

    private DiscountPlan getDiscountPlan(Integer index, DiscountPlanTypeEnum type, DiscountPlanStatusEnum status, boolean nullDates) {
        DiscountPlan discountPlan = new DiscountPlan();
        discountPlan.setCode("DP_" + index);
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

    @Test
    public void test_get_all_discount_plan() throws JsonProcessingException {

        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        String jsonResponse = loadService.findPaginatedRecords(true, DiscountPlan.class, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals(3, map.get("total"));
        List<Map<String, Object>> discountPlans = (List<Map<String, Object>>) map.get("data");

        assertEquals("OFFER", discountPlans.get(0).get("discountPlanType"));
        assertEquals("DRAFT", discountPlans.get(0).get("status"));
        assertEquals(0, discountPlans.get(0).get("initialQuantity"));
        assertEquals(0, discountPlans.get(0).get("usedQuantity"));
        assertNotNull(discountPlans.get(0).get("startDate"));
        assertNotNull(discountPlans.get(0).get("endDate"));

        assertEquals("PROMO_CODE", discountPlans.get(1).get("discountPlanType"));
        assertEquals("ACTIVE", discountPlans.get(1).get("status"));
        assertEquals(0, discountPlans.get(1).get("initialQuantity"));
        assertEquals(0, discountPlans.get(1).get("usedQuantity"));
        assertNotNull(discountPlans.get(1).get("startDate"));
        assertNotNull(discountPlans.get(1).get("endDate"));

        assertNull(discountPlans.get(2).get("discountPlanType"));
        assertEquals("IN_USE", discountPlans.get(2).get("status"));
        assertEquals(0, discountPlans.get(2).get("initialQuantity"));
        assertEquals(0, discountPlans.get(2).get("usedQuantity"));
        assertNull(discountPlans.get(2).get("startDate"));
        assertNull(discountPlans.get(2).get("endDate"));
    }

    @Test
    public void test_find_discount_by_id() throws JsonProcessingException {

        Map<String, Object> filter = new HashMap<>();
        filter.put("status", "DRAFT");
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Optional<String> jsonResponse = loadService.findByClassNameAndId(true, DiscountPlan.class, 1L, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(jsonResponse.get(), Map.class);
        Map<String, Object> discountPlan = (Map<String, Object>) data.get("data");
        assertEquals("OFFER", discountPlan.get("discountPlanType"));
        assertEquals("DRAFT", discountPlan.get("status"));
        assertEquals(0, discountPlan.get("initialQuantity"));
        assertEquals(0, discountPlan.get("usedQuantity"));
        assertNotNull(discountPlan.get("startDate"));
        assertNotNull(discountPlan.get("endDate"));

    }

    @Test(expected = InvalidParameterException.class)
    public void test_create_dicount_plan_with_empty_body() {
        Optional<Long> id = DiscountPlanApiService.create("");
        assertNotNull(id);
    }

    @Test
    public void test_create_discount_plan_with_dto() throws JsonProcessingException {
        String dto = "{\n" + "    \"code\": \"DP_DISCOUNT_PLAN\",\n" + "    \"discountPlanType\":\"OFFER\"\n" + "}";
        Optional<Long> id = DiscountPlanApiService.create(dto);
        assertNotNull(id);
    }

    @Test
    public void test_update_dicount_plan_with_dto() throws JsonProcessingException {
        String dto = "{\"code\": \"DP_DISCOUNT_PLAN\",\"discountPlanType\":\"OFFER\"}";
        Optional<Long> id = DiscountPlanApiService.update(1L, dto);
        assertNotNull(id);
    }

    @Test
    public void test_delete_dicount_plan_with_dto() throws JsonProcessingException {
        String result = DiscountPlanApiService.delete(1L);
        assertNotNull(result);
    }

    @Test
    public void test_expire_discount_plan() {
        Optional<Long> id = DiscountPlanApiService.expire(1L);
        assertNotNull(id);
    }

    @Test
    public void test_create_discount_plan_item_with_dto() throws JsonProcessingException {
        String dto = " {\n" + "            \"code\": \"DPI_DISC_PLAN_DRAFT\",\n" + "            \"discountPlan\": {\n" + "                \"id\": 43\n" + "            },\n"
                + "            \"expressionEl\": \"#{ true }\",\n" + "            \"discountValue\": 50.000000000000,\n" + "            \"discountValueEL\": \"#{ 50 }\",\n"
                + "            \"discountPlanItemType\": \"PERCENTAGE\"\n" + "        }";
        Optional<Long> id = DiscountPlanApiService.createItem(dto);
        assertNotNull(id);
    }

    @Test
    public void test_update_discount_plan_item_with_dto() throws JsonProcessingException {
        String dto =
                " {\n" + "            \"code\": \"DPI_DISC_PLAN_DRAFT\",\n" + "            \"expressionEl\": \"#{ true }\",\n" + "            \"discountValue\": 50.000000000000,\n"
                        + "            \"discountValueEL\": \"#{ 50 }\",\n" + "            \"discountPlanItemType\": \"PERCENTAGE\"\n" + "        }";
        Optional<Long> id = DiscountPlanApiService.updateItem(43L, dto);
        assertNotNull(id);
    }

    @Test
    public void test_delete_dicount_plan_item() throws JsonProcessingException {
        String result = DiscountPlanApiService.deleteItem(1L);
        assertNotNull(result);
    }

    @Test
    public void test_find_discount_plan_item_by_id() throws JsonProcessingException {

        Map<String, Object> filter = new HashMap<>();
        filter.put("status", "DRAFT");
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Optional<String> jsonResponse = loadService.findByClassNameAndId(true, DiscountPlanItem.class, 1L, searchConfig, null, null, 1L);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(jsonResponse.get(), Map.class);
        Map<String, Object> discountPlanItem = (Map<String, Object>) data.get("data");
        assertEquals(1, discountPlanItem.get("discountValue"));
        assertEquals("FIXED", discountPlanItem.get("discountPlanItemType"));
        assertEquals("DPI", discountPlanItem.get("code"));
        assertEquals(false, discountPlanItem.get("allowToNegate"));

    }
}
