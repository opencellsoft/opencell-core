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
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.payments.InstallmentAccountOperation;
import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentPlanService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class PaymentPlanApi extends BaseApi {

    @Inject
    private PaymentPlanService paymentPlanService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private ProviderService providerService;

    public Long create(PaymentPlanDto paymentPlanDto) {
        Provider provider = providerService.getProvider();

        // dto validation
        if (!provider.isPaymentPlan()) {
            throw new BusinessApiException("PaymentPlan not allowed");
        }

        if (paymentPlanDto.getNumberOfInstallments() <= 0) {
            throw new BusinessApiException("Number of installments must be greater than 0");
        }

        // 1- Remove duplicated AOs IDs if exists : done by field type java.util.Set

        // 1.1- Find CustomerAccount
        CustomerAccount customerAccount = customerAccountService.findById(paymentPlanDto.getCustomerAccount());

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException("No CustomerAccount found with id " + paymentPlanDto.getCustomerAccount());
        }

        // 2- List AO by customerAccount ID and AO IDs
        List<Long> aoIds = paymentPlanDto.getInstallmentAccountOperations().stream()
                .map(InstallmentAccountOperation::getId)
                .collect(Collectors.toList());
        List<AccountOperation> aos = accountOperationService.findByCustomerAccount(aoIds, paymentPlanDto.getCustomerAccount());

        List<Long> foundedAoIds = aos.stream().map(AccountOperation::getId).collect(Collectors.toList());

        // 3- If existing AOs in database are less than inputs: throw exception indicating missing AOs IDs: (missing AOs on database for customerAccount {customerAccountID}: {missingIDs})
        if (foundedAoIds.size() != aoIds.size()) {
            List<Long> diffs = new ArrayList<>(aoIds);
            diffs.removeAll(foundedAoIds);
            throw new EntityDoesNotExistsException("Missing AOs for customerAccount " + paymentPlanDto.getCustomerAccount() + " : " + diffs);
        }

        BigDecimal sumAmoutAos = BigDecimal.ZERO;

        for (AccountOperation ao : aos) {
            // 4- For all AccoutingOperations :

            // 4.1- Amount > 0
            if (ao.getUnMatchingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessApiException("AcccountOperation '" + ao.getCode() + "' amount should be greater than 0");
            }

            // 4.2- MatchingStatus in ("O","P")
            if (!(ao.getMatchingStatus() == MatchingStatusEnum.O || ao.getMatchingStatus() == MatchingStatusEnum.P)) {
                throw new BusinessApiException("AcccountOperation '" + ao.getCode() + "' have an invalid matching status. Expected O or P given " + ao.getMatchingStatus());
            }

            sumAmoutAos = sumAmoutAos.add(ao.getUnMatchingAmount());
        }

        // 5- Sum of AO amounts must be equals to 'amountToRecover'
        if (sumAmoutAos.compareTo(paymentPlanDto.getAmountToRecover()) != 0) {
            throw new BusinessApiException("Amount to recover must be equal to AOs amount [AmountToRecover=" + paymentPlanDto.getAmountToRecover() + " - sum AOs amount=" + sumAmoutAos + "]");
        }

        // 6- 'amountToRecover' must be between minimumAllowedOriginalReceivableAmount and maximumAllowedOriginalReceivableAmount
        if (paymentPlanDto.getAmountToRecover().compareTo(provider.getPaymentPlanPolicy().getMinAllowedReceivableAmount()) < 0) {
            throw new BusinessApiException("Amount to recover '" + paymentPlanDto.getAmountToRecover() + "' must be greater than MinAllowedReceivableAmount '" + provider.getPaymentPlanPolicy().getMinAllowedReceivableAmount() + "'");
        }

        if (paymentPlanDto.getAmountToRecover().compareTo(provider.getPaymentPlanPolicy().getMaxAllowedReceivableAmount()) > 0) {
            throw new BusinessApiException("Amount to recover '" + paymentPlanDto.getAmountToRecover() + "' must be less than MaxAllowedReceivableAmount '" + provider.getPaymentPlanPolicy().getMaxAllowedReceivableAmount() + "'");
        }

        // 7- check that: amountToRecover = (amountPerInstallment* numberOfInstallments) + remaining
        BigDecimal expectedAmount = paymentPlanDto.getAmountPerInstallment().multiply(BigDecimal.valueOf(paymentPlanDto.getNumberOfInstallments())).add(paymentPlanDto.getRemainingAmount());
        if (!Objects.equals(paymentPlanDto.getAmountToRecover(), expectedAmount)) {
            throw new BusinessApiException("Amount to recover '" + paymentPlanDto.getAmountToRecover() + "' must be equal '" + expectedAmount + "'");
        }

        // 8- check that numberOfInstallments is less than the maximumPaymentPlanDuration
        if (paymentPlanDto.getNumberOfInstallments() > provider.getPaymentPlanPolicy().getMaxPaymentPlanDuration()) {
            throw new BusinessApiException("Number of installments '" + paymentPlanDto.getNumberOfInstallments() + "' must be less than MaxPaymentPlanDuration '" + provider.getPaymentPlanPolicy().getMaxPaymentPlanDuration() + "'");
        }

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

        return paymentPlanService.create(paymentPlanDto, aos, customerAccount, end);
    }


}