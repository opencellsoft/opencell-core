package org.meveo.service.billing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.MonthEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.util.ApplicationProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class RecurringChargeInstanceServiceTest {

    @Spy
    @InjectMocks
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Mock
    private WalletOperationService walletOperationService;

    @Mock
    @Rejected
    private Event<Serializable> rejectededChargeProducer;

    @Mock
    private ParamBeanFactory paramBeanFactory;

    @Mock
    @ApplicationProvider
    protected Provider appProvider;

    @Captor
    private ArgumentCaptor<RecurringChargeInstance> rciCaptor;

    private CalendarYearly calendar = null;

    @Before
    public void setUp() {

        calendar = new CalendarYearly();

        List<DayInYear> days = new ArrayList<DayInYear>();

        for (int i = 1; i <= 12; i++) {
            DayInYear day = new DayInYear();
            day.setMonth(MonthEnum.getValue(i));
            day.setDay(1);
            days.add(day);
        }
        calendar.setDays(days);

        when(walletOperationService.getRecurringPeriod(any(), any())).thenCallRealMethod();
        when(walletOperationService.isApplyInAdvance(any())).thenCallRealMethod();
        when(walletOperationService.applyReccuringCharge(any(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean())).thenAnswer(new Answer<List<WalletOperation>>() {
            @SuppressWarnings("deprecation")
            public List<WalletOperation> answer(InvocationOnMock invocation) throws Throwable {

                RecurringChargeInstance chargeInstance = (RecurringChargeInstance) invocation.getArguments()[0];
                chargeInstance.getNextChargeDate().setMonth(chargeInstance.getNextChargeDate().getMonth() + 1);
                return Arrays.asList(new WalletOperation());
            }
        });

        ParamBean paramBean = mock(ParamBean.class);
        when(paramBean.getProperty(any(), any())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[1];
            }
        });

        when(paramBeanFactory.getInstance()).thenReturn(paramBean);

        doAnswer(new Answer<RecurringChargeInstance>() {
            public RecurringChargeInstance answer(InvocationOnMock invocation) throws Throwable {
                return (RecurringChargeInstance) invocation.getArguments()[0];
            }
        }).when(recurringChargeInstanceService).updateNoCheck(any());

    }

    private RecurringChargeInstance getChargeInstance(Date chargedToDate, Date subscriptionDate, boolean isCycleForward, boolean prorateSubscription) {

        RecurringChargeTemplate chargeTemplate = new RecurringChargeTemplate();
        chargeTemplate.setSubscriptionProrata(prorateSubscription);

        RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
        chargeInstance.setApplyInAdvance(isCycleForward);
        chargeInstance.setRecurringChargeTemplate(chargeTemplate);
        chargeInstance.setCalendar(calendar);
        chargeInstance.setChargedToDate(chargedToDate);
        chargeInstance.setSubscriptionDate(subscriptionDate);
        chargeInstance.setQuantity(BigDecimal.valueOf(100d));

        return chargeInstance;
    }

    // -- TEST first charge application

    @Test
    public void test_applyRecurringCharge_cycleForward_firstCharge_inFuture() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 4, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(0)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_firstCharge_inFuture() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 4, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(0)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_firstCharge_onSubscriptionDate() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(0)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_firstCharge_onSubscriptionDate_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        verify(walletOperationService, times(1)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_firstCharge_onSubscriptionDate() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(0)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_firstCharge_onSubscriptionDate_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        verify(walletOperationService, times(0)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_firstCharge_afterSubscriptionDate() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(2)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_firstCharge_afterSubscriptionDate_inclusve() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), true, false);

        verify(walletOperationService, times(3)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_firstCharge_afterSubscriptionDate() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(1)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_firstCharge_afterSubscriptionDate_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), true, false);

        verify(walletOperationService, times(2)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    // -- TEST existing subscriptions

    @Test
    public void test_applyRecurringCharge_cycleForward_matchPeriod() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(2)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_matchPeriod_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), true, false);

        verify(walletOperationService, times(3)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_notMatchPeriod() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 16, 0, 0, 0), false, false);

        verify(walletOperationService, times(3)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleForward_notMatchPeriod_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 16, 0, 0, 0), true, false);

        verify(walletOperationService, times(3)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_matchPeriod() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), false, false);

        verify(walletOperationService, times(1)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_matchPeriod_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 1, 0, 0, 0), true, false);

        verify(walletOperationService, times(2)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_notMatchPeriod() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 16, 0, 0, 0), false, false);

        verify(walletOperationService, times(2)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }

    @Test
    public void test_applyRecurringCharge_cycleEnd_notMatchPeriod_inclusive() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, false);
        chargeInstance.setChargeDate(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        chargeInstance.setNextChargeDate(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        recurringChargeInstanceService.applyRecurringCharge(chargeInstance, DateUtils.newDate(2019, 7, 16, 0, 0, 0), true, false);

        verify(walletOperationService, times(2)).applyReccuringCharge(rciCaptor.capture(), any(), anyBoolean(), anyBoolean(), any(), any(), anyBoolean());
    }
}