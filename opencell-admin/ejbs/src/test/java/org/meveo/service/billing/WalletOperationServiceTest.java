package org.meveo.service.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.DatePeriod;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OverrideProrataEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.MonthEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.rating.RatingResult;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class WalletOperationServiceTest {

    @Spy
    @InjectMocks
    private WalletOperationService walletOperationService;

    @Mock
    private RatingService ratingService;

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

        when(ratingService.rateChargeAndTriggerEDRs(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean(), anyBoolean())).thenAnswer(new Answer<RatingResult>() {
            public RatingResult answer(InvocationOnMock invocation) throws Throwable {

                WalletOperation wo = new WalletOperation();
                wo.setChargeInstance((ChargeInstance) invocation.getArguments()[0]);
                wo.setOperationDate((Date) invocation.getArguments()[1]);
                wo.setQuantity((BigDecimal) invocation.getArguments()[2]);
                wo.setStartDate((Date) invocation.getArguments()[5]);
                wo.setEndDate((Date) invocation.getArguments()[6]);
                wo.setFullRatingPeriod((DatePeriod) invocation.getArgument(7));

                RatingResult ratingResult = new RatingResult();
                ratingResult.setWalletOperation(wo);

                return ratingResult;
            }
        });

        doAnswer(new Answer<List<WalletOperation>>() {
            public List<WalletOperation> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList((WalletOperation) invocation.getArguments()[0]);
            }
        }).when(walletOperationService).chargeWalletOperation(any());

    }

    // TEST REGULAR RECURRING CHARGES

    // Test cases when charge is applied in advance. Test first charge.

    @Test
    public void test_applyReccuringCharge_cycleForward_firstCharge_subscribedOnPeriodStart_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleForward_firstCharge_subscribedOnPeriodStart_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleForward_firstCharge_subscribedMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), true, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleForward_firstCharge_subscribedMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), true, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2019, 6, 1, 0, 0, 0)));

    }

    // Test cases when charge is applied at the end of the period. Test first charge.

    @Test
    public void test_applyReccuringCharge_cycleEnd_firstCharge_subscribedOnPeriodStart_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleEnd_firstCharge_subscribedOnPeriodStart_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleEnd_firstCharge_subscribedMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), false, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleEnd_firstCharge_subscribedMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), false, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2019, 6, 1, 0, 0, 0)));
    }

    // Test cases when charge is applied in advance. Charges have been applied previously.

    @Test
    public void test_applyReccuringCharge_cycleForward_existingCharge_subscribedOnPeriodStart_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleForward_existingCharge_subscribedOnPeriodStart_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleForward_existingCharge_subscribedMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleForward_existingCharge_subscribedMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    // Test cases when charge is applied at the end of the period. Charges have been applied previously.

    @Test
    public void test_applyReccuringCharge_cycleEnd_existingCharge_subscribedOnPeriodStart_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleEnd_existingCharge_subscribedOnPeriodStart_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleEnd_existingCharge_subscribedMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, false, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_applyReccuringCharge_cycleEnd_existingCharge_subscribedMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, true, false, null, null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 7, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();
    }

    // TEST CALENDAR CHANGE

    // Test that calendar change in mid period
    @Test
    public void test_applyReccuringCharge_cycleForward_existingCharge_calendarChange() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 4, 1, 0, 0, 0), DateUtils.newDate(2018, 4, 1, 0, 0, 0), true, false, false, null, null);

        CalendarYearly newCalendar = new CalendarYearly();
        List<DayInYear> days = new ArrayList<DayInYear>();

        for (int i = 1; i <= 12; i = i + 2) {
            DayInYear day = new DayInYear();
            day.setMonth(MonthEnum.getValue(i));
            day.setDay(1);
            days.add(day);
        }
        newCalendar.setDays(days);

        when(ratingService.rateChargeAndTriggerEDRs(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean(), anyBoolean())).thenAnswer(new Answer<RatingResult>() {
            public RatingResult answer(InvocationOnMock invocation) throws Throwable {

                WalletOperation wo = new WalletOperation();
                wo.setChargeInstance((ChargeInstance) invocation.getArguments()[0]);
                wo.setOperationDate((Date) invocation.getArguments()[1]);
                wo.setQuantity((BigDecimal) invocation.getArguments()[2]);
                wo.setStartDate((Date) invocation.getArguments()[5]);
                wo.setEndDate(DateUtils.parseDateWithPattern("2019-07-01", DateUtils.DATE_PATTERN));
                wo.setFullRatingPeriod(getPeriod("2019-05-01", "2019-07-01"));

                ((RecurringChargeInstance) wo.getChargeInstance()).setCalendar(newCalendar);
                if (wo.isApplyInAdvance()) {
                    ((RecurringChargeInstance) wo.getChargeInstance()).advanceChargeDates(wo.getOperationDate(), wo.getFullRatingPeriod().getTo(), wo.getFullRatingPeriod().getTo());
                } else {
                    ((RecurringChargeInstance) wo.getChargeInstance()).advanceChargeDates(wo.getFullRatingPeriod().getTo(),
                        walletOperationService.getRecurringPeriodEndDate(((RecurringChargeInstance) wo.getChargeInstance()), wo.getFullRatingPeriod().getTo()), wo.getFullRatingPeriod().getTo());
                }

                RatingResult ratingResult = new RatingResult();
                ratingResult.setWalletOperation(wo);

                return ratingResult;
            }
        });

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(getPeriod("2019-05-01", "2019-07-01"));
    }

    // Test that calendar change in mid period
    @Test
    public void test_applyReccuringCharge_cycleEnd_existingCharge_calendarChange() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 4, 1, 0, 0, 0), DateUtils.newDate(2018, 4, 1, 0, 0, 0), false, false, false, null, null);

        CalendarYearly newCalendar = new CalendarYearly();
        List<DayInYear> days = new ArrayList<DayInYear>();

        for (int i = 1; i <= 12; i = i + 2) {
            DayInYear day = new DayInYear();
            day.setMonth(MonthEnum.getValue(i));
            day.setDay(1);
            days.add(day);
        }
        newCalendar.setDays(days);

        when(ratingService.rateChargeAndTriggerEDRs(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean(), anyBoolean())).thenAnswer(new Answer<RatingResult>() {
            public RatingResult answer(InvocationOnMock invocation) throws Throwable {

                WalletOperation wo = new WalletOperation();
                wo.setChargeInstance((ChargeInstance) invocation.getArguments()[0]);
                wo.setOperationDate(DateUtils.parseDateWithPattern("2019-07-01", DateUtils.DATE_PATTERN));
                wo.setQuantity((BigDecimal) invocation.getArguments()[2]);
                wo.setStartDate((Date) invocation.getArguments()[5]);
                wo.setEndDate(DateUtils.parseDateWithPattern("2019-07-01", DateUtils.DATE_PATTERN));
                wo.setFullRatingPeriod(getPeriod("2019-05-01", "2019-07-01"));

                ((RecurringChargeInstance) wo.getChargeInstance()).setCalendar(newCalendar);
                if (wo.isApplyInAdvance()) {
                    ((RecurringChargeInstance) wo.getChargeInstance()).advanceChargeDates(wo.getOperationDate(), wo.getFullRatingPeriod().getTo(), wo.getFullRatingPeriod().getTo());
                } else {
                    ((RecurringChargeInstance) wo.getChargeInstance()).advanceChargeDates(wo.getFullRatingPeriod().getTo(),
                        walletOperationService.getRecurringPeriodEndDate(((RecurringChargeInstance) wo.getChargeInstance()), wo.getFullRatingPeriod().getTo()), wo.getFullRatingPeriod().getTo());
                }

                RatingResult ratingResult = new RatingResult();
                ratingResult.setWalletOperation(wo);

                return ratingResult;
            }
        });

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 8, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(1);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(getPeriod("2019-05-01", "2019-07-01"));
    }

    // TEST RERATING OF TERMINATED RECURRING CHARGES - from chargedToDate to the terminationDate exclusive

    // Test when charge is applied in advance - just termination date

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedOnPeriod_noProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getTerminationDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedOnPeriod_noProrata_noApplyAgreement_firstChargeWasNotFullPeriod() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 10, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getTerminationDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().setScale(2, RoundingMode.HALF_UP)).isEqualTo(new BigDecimal(67.86d).setScale(2, RoundingMode.HALF_UP));// 19/28 days
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 10, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 10, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2019, 2, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedOnPeriod_wProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getTerminationDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedMidPeriod_noProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getTerminationDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedMidPeriod_wProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getTerminationDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    // Test when charge is applied in advance - Agreement was applied

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedOnPeriod_noProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getServiceInstance().getEndAgreementDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedOnPeriod_wProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getServiceInstance().getEndAgreementDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedMidPeriod_noProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getServiceInstance().getEndAgreementDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReccuringCharge_cycleForward_to_terminatedMidPeriod_wProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING, false, chargeInstance.getServiceInstance().getEndAgreementDate(), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    // TEST RERATING OF TERMINATED RECURRING CHARGES - from chargedToDate to the terminationDate exclusive

    // Test when charge is applied in advance - just termination date

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToOnPeriod_noProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2018, 11, 10, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        // Will reimburse period 2019/1/1 to 2019/3/1
        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToOnPeriod_wProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 1, 10, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToMidPeriod_noProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 1, 10, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToMidPeriod_wProrata_noApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 1, 10, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    // Test when charge is applied in advance - Agreement was applied

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToPeriod_noProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        // Will reimburse period 2019/1/1 to 2019/3/1
        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToPeriod_wProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        // Will reimburse period 2019/1/1 to 2019/3/1
        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToMidPeriod_noProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToMidPeriod_wProrata_withApplyAgreement() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    // Test when charge is applied in advance - Charge to Date on Termination value was applied

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToPeriod_noProrata_withChargeToDateOnTermination() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 1, 25, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        // Will reimburse period 2019/1/1 to 2019/3/1
        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToPeriod_wProrata_withChargeToDateOnTermination() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 1, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 1, 25, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        // Will reimburse period 2019/1/1 to 2019/3/1
        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 1, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToMidPeriod_noProrata_withChargeToDateOnTermination() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 1, 25, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_rerateReimburseReccuringCharge_cycleForward_reimburseToMidPeriod_wProrata_withChargeToDateOnTermination() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 1, 10, 0, 0, 0),
            DateUtils.newDate(2019, 1, 15, 0, 0, 0));
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 1, 25, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    // TEST REIMBURSEMENT OF TERMINATED RECURRING CHARGES

    // Test when charge is applied in advance

    @Test
    public void test_reimburseReccuringCharge_cycleForward_existingCharge_terminateOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleForward_existingCharge_terminateOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleForward_existingCharge_terminateMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleForward_existingCharge_terminateMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    // Test when charge is applied at the end of a period

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_existingCharge_terminateOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_existingCharge_terminateOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, true, DateUtils.newDate(2019, 3, 1, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_existingCharge_terminateMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_existingCharge_terminateMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, true, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    // TEST REIMBURSEMENT OF TERMINATED RECURRING CHARGES with CHARGE to DATE ON TERMINATION

    // Test when charge is applied in advance

    @Test
    public void test_reimburseReccuringCharge_cycleForward_chargeToDateOnTermination_terminateOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleForward_chargeToDateOnTermination_terminateOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleForward_chargeToDateOnTermination_terminateMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleForward_chargeToDateOnTermination_terminateMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    // Test when charge is applied at the end of a period

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_chargeToDateOnTermination_terminateOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_chargeToDateOnTermination_terminateOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, true, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_chargeToDateOnTermination_terminateMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_reimburseReccuringCharge_cycleEnd_chargeToDateOnTermination_terminateMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, true, DateUtils.newDate(2019, 2, 1, 0, 0, 0), null);
        chargeInstance.setChargeToDateOnTermination(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    // TEST APPLY END AGREEMENT TO RECURRING CHARGES

    // Test when charge is applied in advance

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 4, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 4, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, false, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false, true, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    // Test when charge is applied at the end of a cycle

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 4, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, true, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 4, 1, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, false, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false, true, DateUtils.newDate(2019, 2, 10, 0, 0, 0),
            DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0), null, false);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(chargeInstance.getTerminationDate());
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
    }

    private RecurringChargeInstance getChargeInstance(Date chargedToDate, Date subscriptionDate, boolean isCycleForward, boolean prorateSubscription, boolean prorateTermination, Date terminationDate,
            Date endAgreementDate) {

        RecurringChargeTemplate chargeTemplate = new RecurringChargeTemplate();
        chargeTemplate.setSubscriptionProrata(prorateSubscription);
        chargeTemplate.setTerminationProrata(prorateTermination);

        RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
        chargeInstance.setApplyInAdvance(isCycleForward);
        chargeInstance.setRecurringChargeTemplate(chargeTemplate);
        chargeInstance.setCalendar(calendar);
        chargeInstance.setChargedToDate(chargedToDate);
        chargeInstance.setSubscriptionDate(subscriptionDate);
        chargeInstance.setQuantity(BigDecimal.valueOf(100d));

        chargeInstance.setServiceInstance(new ServiceInstance());

        if (terminationDate != null) {
            chargeInstance.setTerminationDate(terminationDate);
            chargeInstance.setChargeToDateOnTermination(terminationDate);
            chargeInstance.setStatus(InstanceStatusEnum.TERMINATED);

            SubscriptionTerminationReason terminationReason = new SubscriptionTerminationReason();
            terminationReason.setApplyAgreement(endAgreementDate != null);
            terminationReason.setOverrideProrata(OverrideProrataEnum.NO_OVERRIDE);
            chargeInstance.getServiceInstance().setSubscriptionTerminationReason(terminationReason);
            if (endAgreementDate != null) {
                chargeInstance.getServiceInstance().setEndAgreementDate(endAgreementDate);
                chargeInstance.setChargeToDateOnTermination(endAgreementDate);
            }
        }
        return chargeInstance;
    }

    private DatePeriod getPeriod(String date1, String date2) {
        return new DatePeriod(date1 != null ? DateUtils.parseDateWithPattern(date1, DateUtils.DATE_PATTERN) : null, date2 != null ? DateUtils.parseDateWithPattern(date2, DateUtils.DATE_PATTERN) : null);
    }
}
