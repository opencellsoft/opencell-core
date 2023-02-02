package org.meveo.apiv2.accounts.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.accounts.CounterInstanceDto;
import org.meveo.apiv2.billing.CounterPeriodDto;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateMappingService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
    @Mock
    private ProductChargeTemplateMappingService productChargeTemplateMappingService;

    // ************************************************************
    // *********************** CREATE *****************************
    // ************************************************************

    @Test
    public void createNominal() {
        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                "BILL", "USER", "SUBSC", "SRV", "USAGE",
                Set.of(buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                        Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                ));

        CounterTemplate ct = new CounterTemplate();
        ct.setCounterLevel(CounterTemplateLevel.SU);

        ServiceInstance si1 = new ServiceInstance();
        si1.setId(1L);
        si1.setStatusDate(DateUtils.fromLocalDate(LocalDate.now().minusMonths(1)));
        ServiceInstance si2 = new ServiceInstance();
        si2.setId(2L);
        si2.setStatusDate(DateUtils.fromLocalDate(LocalDate.now().minusDays(15)));
        ServiceInstance si3 = new ServiceInstance();
        si3.setId(3L);
        si3.setStatusDate(DateUtils.fromLocalDate(LocalDate.now().minusDays(10)));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(ct);
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(si1, si2, si3));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void codeTemplateNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "NOT_FOUND",
                null, null, "ABC", "ABC", "USAGE", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(null);

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void custAccountNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "NOT_FOUND",
                null, "ABC", "ABC", "ABC", "USAGE", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void billAccountNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                "NOT_FOUND", "ABC", "ABC", "ABC", "USAGE", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void userAccountNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "NOT_FOUND", "ABC", "ABC", "USAGE", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void subNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "ABC", "NOT_FOUND", "ABC", "USAGE", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void serviceInstanceNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "UA", "ABC", "NOT_FOUND", "USAGE", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void chargeNotExistErr() {
        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "ABC", "ABC", "ABC", "NOT_FOUND", null);

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());

        apiService.createCounterInstance(dto);

    }

    @Test
    public void invalidProductAndCharge() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period));

        UsageChargeInstance charge = new UsageChargeInstance();

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(false);
        //Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "ChargeInstance with [type=" + charge.getChargeType() + ", code=" + charge.getCode()
                    + "] is not linked to Product [code=" + null
                    + "] and CounterTemplate [code=" + null + "]");
        }

    }

    @Test
    public void invalidPeriodDatesErr() {
        CounterPeriodDto period = buildCounterPeriodDto(CounterTypeEnum.NOTIFICATION,
                Date.from(LocalDate.now().minusMonths(10).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid period dates : Start must be before End [start=" + formatDate(period.getStartDate()) + " - end=" + formatDate(period.getEndDate()) + "]");
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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period, period2));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

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

        CounterInstanceDto dto = buildDto("TEMP1", "CUSTO",
                null, "USER", "SUBSC", "SRV", "USAGE", Set.of(period2, period));

        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(new CounterTemplate());
        Mockito.when(serviceInstanceService.findByCodeAndCodeSubscription(any(), any())).thenReturn(List.of(new ServiceInstance(), new ServiceInstance()));
        CounterInstance mockNewInstance = new CounterInstance();
        mockNewInstance.setId(1L);
        Mockito.when(productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(any(), any(), any())).thenReturn(true);
        Mockito.when(counterInstanceService.counterInstanciationWithoutForceCommit(any(), any(), any(), eq(false))).thenReturn(mockNewInstance);
        Mockito.when(chargeInstanceService.findByCode("USAGE")).thenReturn(new UsageChargeInstance());

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("No overlapping should occur between counter Date Periods"));
        }

    }

    @Test
    public void mandatoryCounterTemplate() {
        CounterInstanceDto dto = buildDto(null, "NOT_FOUND",
                null, null, "ABC", "ABC", "USAGE", null);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "CounterTemplate code is mandatory");
        }

    }

    @Test
    public void mandatoryProductCode() {
        CounterInstanceDto dto = buildDto("ABC", "NOT_FOUND",
                null, null, "ABC", null, "USAGE", null);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Product code is mandatory");
        }

    }

    @Test
    public void mandatorySubscriptionCode() {
        CounterInstanceDto dto = buildDto("ABC", "NOT_FOUND",
                null, null, null, "ABC", "USAGE", null);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Subscription code is mandatory");
        }

    }

    @Test
    public void mandatoryChargeCode() {
        CounterInstanceDto dto = buildDto("ABC", "NOT_FOUND",
                null, null, "ABC", "ABC", null, null);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Charges are mandatory");
        }

    }

    @Test
    public void mandatoryCA() {
        CounterInstanceDto dto = buildDto("ABC", null,
                null, null, "ABC", "ABC", "USAGE", null);

        CounterTemplate ct = new CounterTemplate();
        ct.setCounterLevel(CounterTemplateLevel.CA);
        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(ct);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "CustomerAccount code is mandatory");
        }

    }

    @Test
    public void mandatoryBA() {
        CounterInstanceDto dto = buildDto("ABC", "null",
                null, null, "ABC", "ABC", "USAGE", null);

        CounterTemplate ct = new CounterTemplate();
        ct.setCounterLevel(CounterTemplateLevel.BA);
        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(ct);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "BillingAccount code is mandatory");
        }

    }

    @Test
    public void mandatoryUA() {
        CounterInstanceDto dto = buildDto("ABC", "null",
                "null", null, "ABC", "ABC", "USAGE", null);

        CounterTemplate ct = new CounterTemplate();
        ct.setCounterLevel(CounterTemplateLevel.UA);
        Mockito.when(counterTemplateService.findByCode(any())).thenReturn(ct);

        try {
            apiService.createCounterInstance(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "UserAccount code is mandatory");
        }

    }

    private String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    // ************************************************************
    // ************************ TOOLS *****************************
    // ************************************************************
    private CounterInstanceDto buildDto(String counterTemplateCode, String customerAccountCode, String billingAccountCode, String userAccountCode,
                                        String subscriptionCode, String serviceInstanceCode, String chargeInstanceCode, Set<CounterPeriodDto> counterPeriods) {

        return new CounterInstanceDto() {

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
            public String getProductCode() {
                return serviceInstanceCode;
            }

            @Nullable
            @Override
            public String getChargeInstanceCode() {
                return chargeInstanceCode;
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
            public Date getStartDate() {
                return start;
            }

            @Override
            public Date getEndDate() {
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