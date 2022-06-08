package org.meveo.apiv2.payments;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.payments.service.PaymentPlanApi;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentPlanPolicy;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentPlanService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Nonnull;
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
public class PaymentPlanCreateApiTest {

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

    @Test
    public void createNominal() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        // (BigDecimal amountToRecover, BigDecimal amountPerInstallment, BigDecimal remainingAmount,
        // ActionOnRemainingAmountEnum action, RecurrenceUnitEnum unit, PaymentPlanStatusEnum status,
        // Integer numberOfInstallments,
        // Date start, Date end,
        // Long customerId, Set<Long> idAos)
        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2023, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.create(dto, aos, customerAccount, Date.from(LocalDate.of(2023, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))).thenReturn(1L);

        paymentPlanApi.create(dto);

    }

    @Test
    public void createEmptyNonMandatoryFieldNominal() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, null,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null,
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);
        Mockito.when(paymentPlanService.create(dto, aos, customerAccount, Date.from(LocalDate.of(2023, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))).thenReturn(1L);

        paymentPlanApi.create(dto);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void customerNotExistErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(-1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(null);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.lenient().when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No CustomerAccount found with id -1");
        }

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void invalidAoErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), new HashSet<>());

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Missing AOs on for customerAccount 1 : [3,4]");
        }

    }

    @Test
    public void policyPaymentPlanDisabledErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.lenient().when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, false));
        Mockito.lenient().when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "PaymentPlan not allowed");
        }

    }

    @Test
    public void numberInstallmentsErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                0,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.lenient().when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.lenient().when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Number of installments must be greater than 0");
        }

    }

    @Test
    public void amountToRecoverErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover must be equal to AOs amount [AmountToRecover=123 - sum AOs amount=200]");
        }

    }

    @Test
    public void aoAmountLess0ErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(-1), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' amount should be greater than 0");
        }

    }

    @Test
    public void aoAmountEqual0ErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(0), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' amount should be greater than 0");
        }

    }

    @Test
    public void aoStatusNotExpectedErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(123), new BigDecimal(123), new BigDecimal(123),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                2,
                Date.from(LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(100), MatchingStatusEnum.C,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(100), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 10, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "AcccountOperation 'A' have an invalid matching status. Expected O or P given C");
        }
    }

    @Test
    public void aoSumAmountErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(500), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover must be equal to AOs amount [AmountToRecover=240 - sum AOs amount=620]");
        }

    }

    @Test
    public void aoAmountToRecoverLessMinPolicyErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(250), new BigDecimal(1000), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover '240' must be greater than MinAllowedReceivableAmount '250'");
        }

    }

    @Test
    public void aoAmountToRecoverGreaterMinPolicyErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(200), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover '240' must be less than MaxAllowedReceivableAmount '200'");
        }

    }

    @Test
    public void amountNotConsistentErrCode() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(400),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover '240' must be equal '600'");
        }

    }

    @Test
    public void numberInstallmentsErrCode() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2022, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 9, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Number of installments '10' must be less than MaxPaymentPlanDuration '9'");
        }

    }

    @Test
    public void invalidEndDateErrCase() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.of(2022, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2025, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O,"A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P,"B"));

        Mockito.when(customerAccountService.findById(any())).thenReturn(customerAccount);
        Mockito.when(providerService.getProvider()).thenReturn(buildProvider(new BigDecimal(10), new BigDecimal(1000), 360, true));
        Mockito.when(accountOperationService.findByCustomerAccount(any(), any())).thenReturn(aos);

        try {
            paymentPlanApi.create(dto);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid end date '2025-03-01', correct end date is '2023-03-01'");
        }

    }

    private PaymentPlanDto buildDto(BigDecimal amountToRecover, BigDecimal amountPerInstallment, BigDecimal remainingAmount,
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
            public BigDecimal getAmountToRecover() {
                return amountToRecover;
            }

            @Override
            public BigDecimal getAmountPerInstallment() {
                return amountPerInstallment;
            }

            @Override
            public BigDecimal getRemainingAmount() {
                return remainingAmount;
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
            public Set<InstallmentAccountOperation> getInstallmentAccountOperations() {
                return aos;
            }

            @Override
            public Long getCustomerAccount() {
                return customerId;
            }
        };

    }

    private Provider buildProvider(BigDecimal minAllowed, BigDecimal maxAllowed, Integer maxPayPlan, boolean isPaymentPlan) {
        Provider p = new Provider();
        PaymentPlanPolicy pp = new PaymentPlanPolicy();
        p.setPaymentPlanPolicy(pp);
        p.setPaymentPlan(isPaymentPlan);

        pp.setMinAllowedReceivableAmount(minAllowed);
        pp.setMaxAllowedReceivableAmount(maxAllowed);
        pp.setMaxPaymentPlanDuration(maxPayPlan);

        return p;
    }

    private AccountOperation buildAo(Long id, CustomerAccount customerAccount, BigDecimal amout, MatchingStatusEnum status, String code) {
        AccountOperation ao = new AccountOperation();
        ao.setId(id);
        ao.setCode(code);
        ao.setCustomerAccount(customerAccount);
        ao.setUnMatchingAmount(amout);
        ao.setMatchingStatus(status);

        return ao;
    }

}