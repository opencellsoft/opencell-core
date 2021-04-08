package org.meveo.service.billing;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IDiscountable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.ApplicableEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscountPlanInstanceServiceTest {

    @Spy
    @InjectMocks
    private DiscountPlanInstanceService discountPlanInstanceService;

    @Mock
    private DiscountPlanService discountPlanService;

    @Before
    public void setup() {
        doNothing().when(discountPlanInstanceService).create(any(DiscountPlanInstance.class), any(DiscountPlan.class));
    }

    @Test(expected = BusinessException.class)
    public void test_draft_cannot_be_instantiated() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        BillingAccount ba = mock(BillingAccount.class);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.DRAFT, false);
        discountPlanInstanceService.instantiateDiscountPlan(ba, dp, null);
    }

    @Test
    public void test_only_active_in_use_instantiation() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        BillingAccount ba = mock(BillingAccount.class);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.ACTIVE, false);
        ba = (BillingAccount) discountPlanInstanceService.instantiateDiscountPlan(ba, dp, null);
        assertNotNull(ba);
    }

    @Test(expected = BusinessException.class)
    public void test_subscription_instantiation_throw_exception() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        Subscription subscription = new Subscription();
        OfferTemplate offerTemplate = new OfferTemplate();
        DiscountPlan offerDiscountPlan = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.ACTIVE, false);
        offerDiscountPlan.setCode("DISC_PLAN");
        offerTemplate.setAllowedDiscountPlans(List.of(offerDiscountPlan));
        subscription.setOffer(offerTemplate);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.PROMO_CODE, DiscountPlanStatusEnum.ACTIVE, false);
        discountPlanInstanceService.instantiateDiscountPlan(subscription, dp, null);

    }

    @Test
    public void test_subscription_instantiation() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArgument(0);
            }
        });
        Subscription subscription = new Subscription();
        OfferTemplate offerTemplate = new OfferTemplate();
        DiscountPlan offerDiscountPlan = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.ACTIVE, false);
        offerTemplate.setAllowedDiscountPlans(List.of(offerDiscountPlan));
        subscription.setOffer(offerTemplate);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.PROMO_CODE, DiscountPlanStatusEnum.ACTIVE, false);
        IDiscountable discountable = discountPlanInstanceService.instantiateDiscountPlan(subscription, dp, null);
        assertNotNull(discountable);
        assertEquals(discountable.getClass(), Subscription.class);

    }

    @Test
    public void test_application_filter_el_true() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocationOnMock) throws Throwable {
                DiscountPlan dp = invocationOnMock.getArgument(0);
                dp.setApplicationFilterEL("#{true}");
                return dp;
            }
        });
        BillingAccount ba = mock(BillingAccount.class);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.ACTIVE, false);
        dp.setApplicationFilterEL("#{true}");
        IDiscountable discountable = discountPlanInstanceService.instantiateDiscountPlan(ba, dp, null);
        assertNotNull(discountable);
    }

    @Test(expected = BusinessException.class)
    public void test_application_filter_el_false() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocationOnMock) throws Throwable {
                DiscountPlan dp = invocationOnMock.getArgument(0);
                dp.setApplicationFilterEL("#{false}");
                return dp;
            }
        });
        BillingAccount ba = mock(BillingAccount.class);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.ACTIVE, false);
        IDiscountable discountable = discountPlanInstanceService.instantiateDiscountPlan(ba, dp, null);
    }

    @Test(expected = BusinessException.class)
    public void test_incompatible_discounts() {
        when(discountPlanService.refreshOrRetrieve(any(DiscountPlan.class))).thenAnswer(new Answer<DiscountPlan>() {

            @Override
            public DiscountPlan answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });
        BillingAccount ba = new BillingAccount();
        DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
        discountPlanInstance.setBillingAccount(ba);
        DiscountPlan dp = getDiscountPlan(DiscountPlanTypeEnum.OFFER, DiscountPlanStatusEnum.ACTIVE, false);
        discountPlanInstance.setDiscountPlan(dp);
        ba.setDiscountPlanInstances(List.of(discountPlanInstance));
        dp.setIncompatibleDiscountPlans(List.of(dp));
        IDiscountable discountable = discountPlanInstanceService.instantiateDiscountPlan(ba, dp, null);
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

}
