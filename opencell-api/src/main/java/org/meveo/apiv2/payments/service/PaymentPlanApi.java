/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.payments.service;

import org.meveo.api.BaseApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.payments.InstallmentAccountOperation;
import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecurrenceUnitEnum;
import org.meveo.model.payments.plan.PaymentPlan;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
import org.meveo.service.payments.impl.PaymentPlanService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PaymentPlanApi extends BaseApi {

    private static final String OCC_PPL_INSTALLMENT = "PPL_INSTALLMENT";
    private static final String OCC_PPL_CREATION = "PPL_CREATION";

    @Inject
    private PaymentPlanService paymentPlanService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private ProviderService providerService;

    @Inject
    private OtherCreditAndChargeService otherCreditAndChargeService;

    @Inject
    private MatchingCodeService matchingCodeService;

    public Long create(PaymentPlanDto paymentPlanDto) {
        // Prepare object use in validation/process
        CustomerAccount customerAccount = customerAccountService.findById(paymentPlanDto.getCustomerAccount());

        List<Long> aoIds = paymentPlanDto.getTargetedAos().stream()
                .map(InstallmentAccountOperation::getId)
                .collect(Collectors.toList());

        // List AO by customerAccount ID and AO IDs
        List<AccountOperation> aos = accountOperationService.findByCustomerAccount(aoIds, paymentPlanDto.getCustomerAccount());

        validate(paymentPlanDto, customerAccount, aoIds, aos, true);

        // if endDate is given, check that value is correct and throw exception if not. If null, calculate it. endDate=startDate.addMonths(numberOfInstallments-1)
        Date end = getPPEndDate(paymentPlanDto);

        BigDecimal remainingAmount = validateAndGetRemaining(paymentPlanDto);

        return paymentPlanService.create(paymentPlanDto, aos, customerAccount, end, remainingAmount);
    }

    public Long update(Long id, PaymentPlanDto paymentPlanDto) {
        // Prepare object use in validation/process
        CustomerAccount customerAccount = customerAccountService.findById(paymentPlanDto.getCustomerAccount());

        List<Long> aoIds = paymentPlanDto.getTargetedAos().stream()
                .map(InstallmentAccountOperation::getId)
                .collect(Collectors.toList());

        // List AO by customerAccount ID and AO IDs
        List<AccountOperation> aos = accountOperationService.findByCustomerAccount(aoIds, paymentPlanDto.getCustomerAccount());

        // Find payment plan by given id
        PaymentPlan existingPP = paymentPlanService.findById(id);

        if (existingPP == null) {
            throw new EntityDoesNotExistsException("No Payment plan found with id " + id);
        }

        PaymentPlan ppWithSameCode = paymentPlanService.findByCode(paymentPlanDto.getCode());
        if (ppWithSameCode != null && !ppWithSameCode.getId().equals(id)) {
            throw new EntityAlreadyExistsException(PaymentPlan.class, paymentPlanDto.getCode());
        }

        if (existingPP.getAmountToRecover().compareTo(paymentPlanDto.getAmountToRecover()) != 0) {
            throw new BusinessApiException("Payment plan amount should not be updated");
        }

        if (existingPP.getStatus() != PaymentPlanStatusEnum.DRAFT && !existingPP.getCode().equalsIgnoreCase(paymentPlanDto.getCode())) {
            throw new BusinessApiException("Payment plan code should not be updated");
        }

        validate(paymentPlanDto, customerAccount, aoIds, aos, false);

        Date end = getPPEndDate(paymentPlanDto);

        // Calculate remainingAmount
        BigDecimal remainingAmount = validateAndGetRemaining(paymentPlanDto);

        return paymentPlanService.update(id, paymentPlanDto, aos, customerAccount, end, remainingAmount);
    }

    public void delete(Long id) {
        // Find payment plan by given id
        PaymentPlan existingPP = paymentPlanService.findById(id);

        if (existingPP == null) {
            throw new EntityDoesNotExistsException("No Payment plan found with id " + id);
        }

        if (existingPP.getStatus() != PaymentPlanStatusEnum.DRAFT) {
            throw new BusinessApiException("Cannot remove PaymentPlan with status " + existingPP.getStatus());
        }

        paymentPlanService.remove(id);
    }

    public void activate(Long id) {
        // Find payment plan
        PaymentPlan paymentPlan = paymentPlanService.findById(id);

        if (paymentPlan == null) {
            throw new EntityDoesNotExistsException("No Payment plan found with id " + id);
        }

        if (paymentPlan.getStatus() != PaymentPlanStatusEnum.DRAFT) {
            throw new BusinessApiException("Cannot activate PaymentPlan with status " + paymentPlan.getStatus());
        }

        if (paymentPlan.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
            throw new BusinessApiException("Payment plan cannot start in the past. Please update start date");
        }

        validateAOs(paymentPlan.getAmountToRecover(), paymentPlan.getTargetedAos());

        // PaymentPlan status must move to 'ACTIVE'
        paymentPlan.setStatus(PaymentPlanStatusEnum.ACTIVE);

        // An AO operation must be created, of type PPC with an amount equal to amountToRecover.
        OtherCreditAndCharge aoPPC = otherCreditAndChargeService.addOCC(OCC_PPL_CREATION,
                "PPC AO for PaymentPlan " + paymentPlan.getCode(),
                paymentPlan.getCustomerAccount(),
                paymentPlan.getAmountToRecover(),
                paymentPlan.getStartDate());

        // All creationAccountOperations must be matched with this new AO.
        List<Long> aosToMatch = new ArrayList<>();
        aosToMatch.add(aoPPC.getId()); // CREDIT in first
        aosToMatch.addAll(paymentPlan.getTargetedAos().stream()
                .map(AccountOperation::getId)
                .collect(Collectors.toList()));

        try {
            matchingCodeService.matchOperations(
                    paymentPlan.getCustomerAccount().getId(),
                    paymentPlan.getCustomerAccount().getCode(),
                    aosToMatch, aosToMatch.get(aosToMatch.size() - 1));
        } catch (Exception e) {
            throw new BusinessApiException("PaymentPlan Creation : Matching action is failed : " + e.getMessage());
        }

        // A list of AOs of type PPI is created, and linked to the PaymentPlan using this process:
        MathContext rounding = new MathContext(12, RoundingMode.DOWN);
        BigDecimal remaningToProcess = paymentPlan.getRemainingAmount() != null ? paymentPlan.getRemainingAmount() : BigDecimal.ZERO;
        BigDecimal ppiUnitAmount = paymentPlan.getAmountToRecover().subtract(remaningToProcess)
                .divide(BigDecimal.valueOf(paymentPlan.getNumberOfInstallments()), rounding)
                .setScale(2, RoundingMode.DOWN);

        List<AccountOperation> newAoPPIs = new ArrayList<>();

        for (int i = 0; i < paymentPlan.getNumberOfInstallments(); i++) {
            // PPI AO
            OtherCreditAndCharge aoPPI = otherCreditAndChargeService.addOCC(OCC_PPL_INSTALLMENT,
                    "PPI AO for PaymentPlan " + paymentPlan.getCode(),
                    paymentPlan.getCustomerAccount(),
                    ppiUnitAmount.add(remaningToProcess),
                    buildFromRecurrenceUnit(paymentPlan.getStartDate(), paymentPlan.getRecurringUnit(), i));

            // Init remaning to process after the first save...the next iteration shall have 0
            remaningToProcess = BigDecimal.ZERO;

            newAoPPIs.add(aoPPI);

        }

        // Update PaymentPlan
        paymentPlan.setCreatedAos(newAoPPIs);
        paymentPlanService.activate(paymentPlan);

    }

    private void validate(PaymentPlanDto dto, CustomerAccount customerAccount, List<Long> aoIds, List<AccountOperation> aos, boolean isCreation) {
        Provider provider = providerService.getProvider();

        // dto validation
        if (!provider.isPaymentPlan()) {
            throw new BusinessApiException("PaymentPlan not allowed");
        }

        if (isCreation && paymentPlanService.findByCode(dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(PaymentPlan.class, dto.getCode());
        }

        if (dto.getNumberOfInstallments() <= 0) {
            throw new BusinessApiException("Number of installments must be greater than 0");
        }

        if (dto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
            throw new BusinessApiException("Payment plan cannot start in the past. Please update start date");
        }

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException("No CustomerAccount found with id " + dto.getCustomerAccount());
        }

        List<Long> foundedAoIds = aos.stream().map(AccountOperation::getId).collect(Collectors.toList());

        // If existing AOs in database are less than inputs: throw exception indicating missing AOs IDs: (missing AOs on database for customerAccount {customerAccountID}: {missingIDs})
        if (foundedAoIds.size() != aoIds.size()) {
            List<Long> diffs = new ArrayList<>(aoIds);
            diffs.removeAll(foundedAoIds);
            throw new EntityDoesNotExistsException("Missing AOs for customerAccount " + dto.getCustomerAccount() + " : " + diffs);
        }

        validateAOs(dto.getAmountToRecover(), aos);

        if (dto.getAmountPerInstallment().compareTo(provider.getPaymentPlanPolicy().getMinInstallmentAmount()) < 0) {
            throw new BusinessApiException("Amount per installment '" + dto.getAmountPerInstallment() + "' must be greater than MinInstallmentAmount '" + provider.getPaymentPlanPolicy().getMinInstallmentAmount() + "'");
        }

        // 'amountToRecover' must be between minimumAllowedOriginalReceivableAmount and maximumAllowedOriginalReceivableAmount
        if (dto.getAmountToRecover().compareTo(provider.getPaymentPlanPolicy().getMinAllowedReceivableAmount()) < 0) {
            throw new BusinessApiException("Amount to recover '" + dto.getAmountToRecover() + "' must be greater than MinAllowedReceivableAmount '" + provider.getPaymentPlanPolicy().getMinAllowedReceivableAmount() + "'");
        }

        if (dto.getAmountToRecover().compareTo(provider.getPaymentPlanPolicy().getMaxAllowedReceivableAmount()) >= 0) {
            throw new BusinessApiException("Amount to recover '" + dto.getAmountToRecover() + "' must be less than MaxAllowedReceivableAmount '" + provider.getPaymentPlanPolicy().getMaxAllowedReceivableAmount() + "'");
        }

        // check that numberOfInstallments is less than the maximumPaymentPlanDuration
        if (dto.getNumberOfInstallments() > provider.getPaymentPlanPolicy().getMaxPaymentPlanDuration()) {
            throw new BusinessApiException("Number of installments '" + dto.getNumberOfInstallments() + "' must be less than MaxPaymentPlanDuration '" + provider.getPaymentPlanPolicy().getMaxPaymentPlanDuration() + "'");
        }

        // check that: amountToRecover = (amountPerInstallment * numberOfInstallments) + remaining
        BigDecimal expectedAmount = dto.getAmountPerInstallment().multiply(BigDecimal.valueOf(dto.getNumberOfInstallments()));
        if (dto.getAmountToRecover().compareTo(expectedAmount) < 0) {
            throw new BusinessApiException("Amount to recover '" + dto.getAmountToRecover() + "' must be greater then '" + expectedAmount + "'");
        }

    }

    private void validateAOs(BigDecimal amountToRecover, List<AccountOperation> aos) {
        BigDecimal sumAmoutAos = BigDecimal.ZERO;

        for (AccountOperation ao : aos) {
            // For all AccoutingOperations :

            // Amount > 0
            if (ao.getUnMatchingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessApiException("AcccountOperation '" + ao.getCode() + "' amount should be greater than 0");
            }

            // MatchingStatus in ("O","P")
            if (!(ao.getMatchingStatus() == MatchingStatusEnum.O || ao.getMatchingStatus() == MatchingStatusEnum.P)) {
                throw new BusinessApiException("AcccountOperation '" + ao.getCode() + "' have an invalid matching status. Expected O or P given " + ao.getMatchingStatus());
            }

            // AO must be DEBIT
            if (ao.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
                throw new BusinessApiException("AcccountOperation '" + ao.getCode() + "' should be DEBIT");
            }

            // Avoid to add plan over AOS PPL_INSTALLMENT
            if ("OCC".equals(ao.getType()) && "PPL_INSTALLMENT".equals(ao.getCode())) {
                throw new BusinessApiException("AcccountOperation '" + ao.getCode() + "' with type OCC, cannot be part of a Payment plan");
            }

            sumAmoutAos = sumAmoutAos.add(ao.getUnMatchingAmount());
        }

        // Sum of AO amounts must be equals to 'amountToRecover'
        if (sumAmoutAos.compareTo(amountToRecover) != 0) {
            throw new BusinessApiException("Amount to recover must be equal to AOs amount [AmountToRecover=" + amountToRecover + " - sum AOs amount=" + sumAmoutAos + "]");
        }
    }

    private Date buildFromRecurrenceUnit(Date source, RecurrenceUnitEnum recurrenceUnit, int toAdd) {
        LocalDate toProcess = source.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (recurrenceUnit == RecurrenceUnitEnum.DAY) {
            toProcess = toProcess.plusDays(toAdd);
        } else if (recurrenceUnit == RecurrenceUnitEnum.MONTH) {
            toProcess = toProcess.plusMonths(toAdd);
        }
        return Date.from(toProcess.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getPPEndDate(PaymentPlanDto paymentPlanDto) {
        // if endDate is given, check that value is correct and throw exception if not. If null, calculate it. endDate=startDate.addMonths(numberOfInstallments-1)
        LocalDate start = paymentPlanDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date end = paymentPlanDto.getEndDate();
        if (end == null) {
            end = Date.from(start.plusMonths(paymentPlanDto.getNumberOfInstallments() - 1L).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            LocalDate endDate = paymentPlanDto.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate expectedDate = start.plusMonths(paymentPlanDto.getNumberOfInstallments() - 1L);
            if (!endDate.equals(expectedDate)) {
                throw new BusinessApiException("Invalid end date '" + DateUtils.formatAsDate(paymentPlanDto.getEndDate()) + "', correct end date is '" + expectedDate + "'");
            }
        }
        return end;
    }

    private BigDecimal validateAndGetRemaining(PaymentPlanDto paymentPlanDto) {
        // Calculate remainingAmount
        BigDecimal expectedAmount = paymentPlanDto.getAmountPerInstallment().multiply(BigDecimal.valueOf(paymentPlanDto.getNumberOfInstallments()));
        BigDecimal remainingAmount = paymentPlanDto.getAmountToRecover().subtract(expectedAmount);

        if (remainingAmount.compareTo(paymentPlanDto.getAmountPerInstallment()) >= 0) {
            throw new BusinessApiException("Remaining amount '" + remainingAmount + "' should be less than or equals Amount per installment '" + paymentPlanDto.getAmountPerInstallment() + "'");
        }

        return remainingAmount.setScale(2, RoundingMode.HALF_UP);
    }


}