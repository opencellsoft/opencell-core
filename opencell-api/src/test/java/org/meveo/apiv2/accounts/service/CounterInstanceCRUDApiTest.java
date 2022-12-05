package org.meveo.apiv2.accounts.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.accounts.CounterInstanceDto;
import org.meveo.apiv2.billing.CounterPeriodDto;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CounterInstanceCRUDApiTest {

    @InjectMocks
    private AccountsManagementApiService apiService;
    @Mock
    private CounterTemplateService counterTemplateService;
    @Mock
    private CustomerAccountService customerAccountService;
    @Mock
    private BillingAccountService billingAccountService;
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ServiceInstanceService serviceInstanceService;
    @Mock
    private ChargeInstanceService chargeInstanceService;
    @Mock
    private CounterInstanceService counterInstanceService;
    @Mock
    private CounterPeriodService counterPeriodService;

    // ************************************************************
    // *********************** CREATE *****************************
    // ************************************************************

    @Test
    public void createNominal() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                "BILL", "USER", "SUBSC", "SRV", Set.of("USAGE", "AUTRE"),
                Set.of(buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                        Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                ));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.when(billingAccountService.findByCode(any())).thenReturn(new BillingAccount());
        Mockito.when(userAccountService.findByCode(any())).thenReturn(new UserAccount());
        Mockito.when(subscriptionService.findByCode(any())).thenReturn(new Subscription());
        Mockito.when(serviceInstanceService.findByCode(any())).thenReturn(new ServiceInstance());
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());
        Mockito.when(chargeInstanceService.findByCode("AUTRE")).thenReturn(new RecurringChargeInstance());
        Mockito.doNothing().when(counterPeriodService).create(any());

        apiService.createCounterInstance(dto);

    }

    @Test
    public void createNominalPeriodWithoutType() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(buildCounterPeriodDto(null,
                        Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                ));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void codeTemplateNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "NOT_FOUND",
                null, null, null, null, null, null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(null);

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void custAccountNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "NOT_FOUND",
                null, null, null, null, null, null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void billAccountNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                "NOT_FOUND", null, null, null, null, null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void userAccountNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, "NOT_FOUND", null, null, null, null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void subNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, "NOT_FOUND", null, null, null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void serviceInstanceNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, "NOT_FOUND", null, null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void chargeNotExistErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, Set.of("NOT_FOUND"), null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());

        apiService.createCounterInstance(dto);

    }

    @Test
    public void mandatoriesFieldCheckErr() {
        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", null,
                null, null, null, null, null, null);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "At least one of those value is mandatory : customerAccountCode, billingAccountCode, userAccountCode, subscriptionCode, serviceInstanceCode");
        }

    }

    @Test
    public void invalidPeriodDatesErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid period dates : Start must be before End [start=" + formatDate(period.getPeriodStartDate()) + " - end=" + formatDate(period.getPeriodEndDate()) + "]");
        }

    }

    @Test
    public void periodOveralappingDates1Err() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(11).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates1BisErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(11).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates2Err() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates2BisErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates3Err() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(14).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates3BisErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(14).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates4Err() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(11).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates4BisErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(11).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates5Err() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(14).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(8).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates5BisErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(14).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(8).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates6Err() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void periodOveralappingDates6BisErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterPeriodDto period2 = buildCounterPeriodDto(CounterTypeEnum.USAGE_AMOUNT,
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("OVH", "OVH Counter TU", "TEMP1", "CUSTO",
                null, null, null, null, null,
                Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(customerAccountService.findByCode(any())).thenReturn(new CustomerAccount());
        Mockito.doNothing().when(counterPeriodService).create(any());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    private String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    // ************************************************************
    // ************************ TOOLS *****************************
    // ************************************************************

    private CounterInstanceDto buildDto(String code, String description, String counterTemplateCode, String customerAccountCode, String billingAccountCode, String userAccountCode,
                                        String subscriptionCode, String serviceInstanceCode, Set<String> chargeInstances, Set<CounterPeriodDto> counterPeriods) {

        return new CounterInstanceDto() {

            @Override
            public String getCode() {
                return code;
            }

            @Nullable
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getCounterTemplateCode() {
                return counterTemplateCode;
            }

            @Nullable
            @Override
            public String getCustomerAccountCode() {
                return customerAccountCode;
            }

            @Nullable
            @Override
            public String getBillingAccountCode() {
                return billingAccountCode;
            }

            @Nullable
            @Override
            public String getUserAccountCode() {
                return userAccountCode;
            }

            @Nullable
            @Override
            public String getSubscriptionCode() {
                return subscriptionCode;
            }

            @Nullable
            @Override
            public String getServiceInstanceCode() {
                return serviceInstanceCode;
            }

            @Nullable
            @Override
            public Set<String> getChargeInstances() {
                return chargeInstances;
            }

            @Nullable
            @Override
            public Set<CounterPeriodDto> getCounterPeriods() {
                return counterPeriods;
            }
        };

    }

    private CounterPeriodDto buildCounterPeriodDto(CounterTypeEnum type, Date start, Date end) {
        return new CounterPeriodDto() {
            @Override
            public String getCode() {
                return "P001";
            }

            @Nullable
            @Override
            public CounterTypeEnum getCounterType() {
                return type;
            }

            @Nullable
            @Override
            public BigDecimal getLevel() {
                return BigDecimal.ONE;
            }

            @Override
            public Date getPeriodStartDate() {
                return start;
            }

            @Override
            public Date getPeriodEndDate() {
                return end;
            }

            @Nullable
            @Override
            public BigDecimal getValue() {
                return BigDecimal.TEN;
            }

            @Nullable
            @Override
            public Map<String, BigDecimal> getAccumulatedValues() {
                Map<String, BigDecimal> values = new HashMap<>();
                values.put("Zero", BigDecimal.ZERO);
                values.put("One", BigDecimal.ONE);
                return values;
            }
        };
    }

}