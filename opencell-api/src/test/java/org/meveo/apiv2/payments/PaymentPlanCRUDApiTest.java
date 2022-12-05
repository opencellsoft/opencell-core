package org.meveo.apiv2.payments;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.payments.service.PaymentPlanApi;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentPlanPolicy;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlan;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentPlanService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class PaymentPlanCRUDApiTest {

    @InjectMocks
    private PaymentPlanApi paymentPlanApi;
    @Mock
    private AccountOperationService accountOperationService;
    @Mock
    private CustomerAccountService customerAccountService;
    @Mock
    private ProviderService providerService;
    @Mock
    private PaymentPlanService paymentPlanService;

    // ************************************************************
    // *********************** CREATE *****************************
    // ************************************************************

    @Test
    public void createNominal() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        // (BigDecimal amountToRecover, BigDecimal amountPerInstallment, BigDecimal remainingAmount,
        // ActionOnRemainingAmountEnum action, RecurrenceUnitEnum unit, PaymentPlanStatusEnum status,
        // Integer numberOfInstallments,
        // Date start, Date end,
        // Long customerId, Set<Long> idAos)
        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(23),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        paymentPlanApi.create(dto);

    }

    @Test
    public void createEmptyNonMandatoryFieldNominal() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(23),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, null,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null,
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        paymentPlanApi.create(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void customerNotExistErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(-1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(null);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No CustomerAccount found with id -1");
        }

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void invalidAoErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), new HashSet<>());

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Missing AOs on for customerAccount 1 : [3,4]");
        }

    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void ppAlreadyExistErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(-1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(null);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.findByCode(any())).thenReturn(new PaymentPlan());

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "PaymentPlan with code=CODE-PP already exists.");
        }

    }

    @Test
    public void policyPaymentPlanDisabledErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, false));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "PaymentPlan not allowed");
        }

    }

    @Test
    public void startDateBeforeNowErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Payment plan cannot start in the past. Please update start date");
        }

    }

    @Test
    public void creditAoErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        AccountOperation ao1 = buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A");
        ao1.setTransactionCategory(OperationCategoryEnum.CREDIT);
        aos.add(ao1);
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' should be DEBIT");
        }

    }

    @Test
    public void invalidAoTypeErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(200), new BigDecimal(100),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        AccountOperation ao1 = buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "PPL_INSTALLMENT");
        ao1.setTransactionCategory(OperationCategoryEnum.DEBIT);
        ao1.setType("OCC");
        aos.add(ao1);
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'PPL_INSTALLMENT' with type OCC, cannot be part of a Payment plan");
        }

    }

    @Test
    public void numberInstallmentsErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                0,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Number of installments must be greater than 0");
        }

    }

    @Test
    public void amountToRecoverErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover must be equal to AOs amount [AmountToRecover=123 - sum AOs amount=200]");
        }

    }

    @Test
    public void minAmountInstallmentErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(23),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(24), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount per installment '23' must be greater than MinInstallmentAmount '24'");
        }

    }

    @Test
    public void aoAmountLess0Err() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(-1), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' amount should be greater than 0");
        }

    }

    @Test
    public void aoAmountEqual0Err() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(0), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' amount should be greater than 0");
        }

    }

    @Test
    public void aoStatusNotExpectedErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.C, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' have an invalid matching status. Expected O or P given C");
        }
    }

    @Test
    public void aoSumAmountErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(500), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover must be equal to AOs amount [AmountToRecover=240 - sum AOs amount=620]");
        }

    }

    @Test
    public void aoAmountToRecoverLessMinPolicyErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(250), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover '240' must be greater than MinAllowedReceivableAmount '250'");
        }

    }

    @Test
    public void aoAmountToRecoverGreaterMinPolicyErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(200), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover '240' must be less than MaxAllowedReceivableAmount '200'");
        }

    }

    @Test
    public void aoAmountToRecoverGreaterMinPolicyOrEqualsErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(240), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover '240' must be less than MaxAllowedReceivableAmount '240'");
        }

    }

    @Test
    public void numberInstallmentsErrCode() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 9, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Number of installments '10' must be less than MaxPaymentPlanDuration '9'");
        }

    }

    @Test
    public void invalidEndDateErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("Invalid end date"));
        }

    }


    @Test
    public void remainingGreaterThanAmountPerInstallment() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(200), new BigDecimal(10),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, null,
                2,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null,
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P, "B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("Remaining amount '180' should be less than or equals Amount per installment '10'"));
        }

    }

    // ************************************************************
    // *********************** UPDATE *****************************
    // ************************************************************
    @Test
    public void updateNominal() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(23),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));
        existingPP.setCode("CODE-PP");

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        paymentPlanApi.update(1L, dto);

    }

    @Test
    public void updateNominalWithChaingingCode() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(23),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));
        existingPP.setCode("CODE-UPDATE");
        existingPP.setStatus(PaymentPlanStatusEnum.DRAFT);

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(10), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        paymentPlanApi.update(1L, dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void updatePPNotFoundErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.findById(any())).thenReturn(null);

        try {
            paymentPlanApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No Payment plan found with id 1");
        }

    }

    @Test
    public void updateAmountChangedErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(241));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        try {
            paymentPlanApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Payment plan amount should not be updated");
        }

    }

    @Test
    public void updateCodeChangedErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));
        existingPP.setCode("UPDATED-CODE");
        existingPP.setStatus(PaymentPlanStatusEnum.ACTIVE);

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        try {
            paymentPlanApi.update(1L, dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Payment plan code should not be updated");
        }

    }

    // ************************************************************
    // ************************ DELETE ****************************
    // ************************************************************
    @Test
    public void deleteNominal() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));

        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);
        Mockito.doNothing().when(paymentPlanService).remove(1L);

        paymentPlanApi.delete(1L);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void deletePPNotFoundErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));

        Mockito.when(paymentPlanService.findById(any())).thenReturn(null);

        try {
            paymentPlanApi.delete(1L);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No Payment plan found with id 1");
        }

    }

    @Test
    public void deleteNotDraftErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(241));
        existingPP.setStatus(PaymentPlanStatusEnum.ACTIVE);

        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        try {
            paymentPlanApi.delete(1L);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot remove PaymentPlan with status ACTIVE");
        }

    }


    // ************************************************************
    // ************************ TOOLS *****************************
    // ************************************************************

    private PaymentPlanDto buildDto(BigDecimal amountToRecover, BigDecimal amountPerInstallment,
                                    ActionOnRemainingAmountEnum action, RecurrenceUnitEnum unit, PaymentPlanStatusEnum status,
                                    Integer numberOfInstallments,
                                    Date start, Date end,
                                    Long customerId, Set<Long> idAos) {
        Set<InstallmentAccountOperation> aos = new HashSet<>();
        Optional.ofNullable(idAos).orElse(Collections.emptySet())
                .forEach(idAo -> {
                    aos.add(new InstallmentAccountOperation() {
                        @Nonnull
                        @Override
                        public Long getId() {
                            return idAo;
                        }
                    });
                });
        return new PaymentPlanDto() {
            @Override
            public String getCode() {
                return "CODE-PP";
            }

            @Nullable
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public BigDecimal getAmountToRecover() {
                return amountToRecover;
            }

            @Override
            public BigDecimal getAmountPerInstallment() {
                return amountPerInstallment;
            }

            @Override
            public ActionOnRemainingAmountEnum getActionOnRemainingAmount() {
                return action;
            }

            @Override
            public Integer getNumberOfInstallments() {
                return numberOfInstallments;
            }

            @Override
            public Date getStartDate() {
                return start;
            }

            @Override
            public Date getEndDate() {
                return end;
            }

            @Override
            public RecurrenceUnitEnum getRecurringUnit() {
                return unit;
            }

            @Override
            public PaymentPlanStatusEnum getStatus() {
                return status;
            }

            @Override
            public Set<InstallmentAccountOperation> getTargetedAos() {
                return aos;
            }

            @Override
            public Long getCustomerAccount() {
                return customerId;
            }
        };

    }

    private Provider buildProvider(BigDecimal minAllowed, BigDecimal maxAllowed, BigDecimal minInstallmentAllowed, Integer maxPayPlan, boolean isPaymentPlan) {
        Provider p = new Provider();
        PaymentPlanPolicy pp = new PaymentPlanPolicy();
        p.setPaymentPlanPolicy(pp);
        p.setPaymentPlan(isPaymentPlan);

        pp.setMinAllowedReceivableAmount(minAllowed);
        pp.setMinInstallmentAmount(minInstallmentAllowed);
        pp.setMaxAllowedReceivableAmount(maxAllowed);
        pp.setMaxPaymentPlanDuration(maxPayPlan);

        return p;
    }

    private AccountOperation buildAo(Long id, CustomerAccount customerAccount, BigDecimal amount, MatchingStatusEnum status, String code) {
        AccountOperation ao = new AccountOperation();
        ao.setId(id);
        ao.setCode(code);
        ao.setCustomerAccount(customerAccount);
        ao.setUnMatchingAmount(amount);
        ao.setMatchingStatus(status);
        ao.setTransactionCategory(OperationCategoryEnum.DEBIT);

        return ao;
    }

}