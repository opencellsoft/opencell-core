package org.meveo.service.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.DatePeriod;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.RecurringChargeInstance;
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
                wo.setOperationDate((Date) invocation.getArguments()[2]);
                wo.setQuantity((BigDecimal) invocation.getArguments()[3]);
                wo.setStartDate((Date) invocation.getArguments()[6]);
                wo.setEndDate((Date) invocation.getArguments()[7]);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), true, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), true, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 1, 0, 0, 0), false, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(null, DateUtils.newDate(2019, 5, 16, 0, 0, 0), false, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), true, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 16, 0, 0, 0), false, true);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, false, false, null);

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

    // TEST TERMINATION OF RECURRING CHARGES

    // Test when charge is applied in advance

    @Test
    public void test_terminateReccuringCharge_cycleForward_existingCharge_terminateOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, false, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_terminateReccuringCharge_cycleForward_existingCharge_terminateOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, true, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_terminateReccuringCharge_cycleForward_existingCharge_terminateMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, false, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_terminateReccuringCharge_cycleForward_existingCharge_terminateMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, true, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    // Test when charge is applied at the end of a period

    @Test
    public void test_terminateReccuringCharge_cycleEnd_existingCharge_terminateOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, false, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_terminateReccuringCharge_cycleEnd_existingCharge_terminateOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 1, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, true, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_terminateReccuringCharge_cycleEnd_existingCharge_terminateMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, false, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_terminateReccuringCharge_cycleEnd_existingCharge_terminateMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 5, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);
        chargeInstance.setTerminationDate(DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, true, null);

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 6, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(-50d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(-100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    // TEST APPLY END AGREEMENT TO RECURRING CHARGES

    // Test when charge is applied in advance

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, false, DateUtils.newDate(2019, 4, 1, 0, 0, 0));

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
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, true, DateUtils.newDate(2019, 4, 1, 0, 0, 0));

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
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0));

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
    public void test_endAgreementReccuringCharge_cycleForward_existingCharge_endAggreementMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), true, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, true, DateUtils.newDate(2019, 3, 16, 0, 0, 0));

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

    // Test when charge is applied at the end of a cycle

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementOnPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, false, DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementOnPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, true, DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 5, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementMidPeriod_noProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, false, DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isNull();
    }

    @Test
    public void test_endAgreementReccuringCharge_cycleEnd_existingCharge_endAggreementMidPeriod_wProrata() {

        RecurringChargeInstance chargeInstance = getChargeInstance(DateUtils.newDate(2019, 2, 1, 0, 0, 0), DateUtils.newDate(2018, 5, 1, 0, 0, 0), false, false);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, true, DateUtils.newDate(2019, 3, 16, 0, 0, 0));

        assertThat(chargeInstance.getChargeDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getChargedToDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(chargeInstance.getNextChargeDate()).isEqualTo(DateUtils.newDate(2019, 4, 1, 0, 0, 0));

        assertThat(wos.size()).isEqualTo(2);

        assertThat(wos.get(0).getQuantity().doubleValue()).isEqualTo(100d);
        assertThat(wos.get(0).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getStartDate()).isEqualTo(DateUtils.newDate(2019, 2, 1, 0, 0, 0));
        assertThat(wos.get(0).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(0).getFullRatingPeriod()).isNull();

        assertThat(wos.get(1).getQuantity().doubleValue()).isEqualTo(50d);
        assertThat(wos.get(1).getOperationDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getStartDate()).isEqualTo(DateUtils.newDate(2019, 3, 1, 0, 0, 0));
        assertThat(wos.get(1).getEndDate()).isEqualTo(DateUtils.newDate(2019, 3, 16, 0, 0, 0));
        assertThat(wos.get(1).getFullRatingPeriod()).isEqualTo(new DatePeriod(DateUtils.newDate(2019, 3, 1, 0, 0, 0), DateUtils.newDate(2019, 4, 1, 0, 0, 0)));
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

}
