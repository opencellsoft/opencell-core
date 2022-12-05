package org.meveo.apiv2.payments;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.payments.service.PaymentPlanApi;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlan;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
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
public class PaymentPlanActivateApiTest {

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
    @Mock
    private OtherCreditAndChargeService otherCreditAndChargeService;
    @Mock
    private MatchingCodeService matchingCodeService;

    @Test
    public void activateNominal() throws Exception {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);
        customerAccount.setCode("CUST-CODE");

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(240));
        existingPP.setCode("CODE-PP");
        existingPP.setStatus(PaymentPlanStatusEnum.DRAFT);
        existingPP.setRemainingAmount(BigDecimal.ZERO);
        existingPP.setNumberOfInstallments(2);
        existingPP.setStartDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        existingPP.setTargetedAos(aos);
        existingPP.setCustomerAccount(customerAccount);

        OtherCreditAndCharge creditAo = new OtherCreditAndCharge();
        creditAo.setId(1L);

        Mockito.when(otherCreditAndChargeService.addOCC(any(), any(), any(), any(), any())).thenReturn(creditAo);
        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);
        Mockito.when(matchingCodeService.matchOperations(any(), any(), any(), any())).thenReturn(new MatchingReturnObject());

        paymentPlanApi.activate(1L);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void activatePPNotFoundErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
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
            paymentPlanApi.activate(1L);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No Payment plan found with id 1");
        }

    }

    @Test
    public void updateAmountChangedErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);
        customerAccount.setCode("CUST-CODE");

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
                ActionOnRemainingAmountEnum.FIRST, RecurrenceUnitEnum.MONTH, PaymentPlanStatusEnum.DRAFT,
                10,
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().plusMonths(9).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                customerAccount.getId(), Set.of(1L, 2L));

        List<AccountOperation> aos = new ArrayList<>();
        aos.add(buildAo(1L, customerAccount, new BigDecimal(120), MatchingStatusEnum.O, "A"));
        aos.add(buildAo(2L, customerAccount, new BigDecimal(120), MatchingStatusEnum.P, "B"));

        PaymentPlan existingPP = new PaymentPlan();
        existingPP.setAmountToRecover(new BigDecimal(241));
        existingPP.setCode("CODE-PP");
        existingPP.setStatus(PaymentPlanStatusEnum.DRAFT);
        existingPP.setRemainingAmount(BigDecimal.ZERO);
        existingPP.setNumberOfInstallments(2);
        existingPP.setStartDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        existingPP.setTargetedAos(aos);
        existingPP.setCustomerAccount(customerAccount);

        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        try {
            paymentPlanApi.activate(1L);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Amount to recover must be equal to AOs amount [AmountToRecover=241 - sum AOs amount=240]");
        }

    }


    @Test
    public void activateNotDraftErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
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
            paymentPlanApi.activate(1L);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Cannot activate PaymentPlan with status ACTIVE");
        }

    }

    @Test
    public void startDateBeforeNowErr() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);
        customerAccount.setCode("CUST-CODE");

        PaymentPlanDto dto = buildDto(new BigDecimal(240), new BigDecimal(20), new BigDecimal(40),
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
        existingPP.setStatus(PaymentPlanStatusEnum.DRAFT);
        existingPP.setRemainingAmount(BigDecimal.ZERO);
        existingPP.setNumberOfInstallments(2);
        existingPP.setStartDate(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        existingPP.setTargetedAos(aos);
        existingPP.setCustomerAccount(customerAccount);

        Mockito.when(paymentPlanService.findById(any())).thenReturn(existingPP);

        try {
            paymentPlanApi.activate(1L);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Payment plan cannot start in the past. Please update start date");
        }

    }


    // ************************************************************
    // ************************ TOOLS *****************************
    // ************************************************************

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