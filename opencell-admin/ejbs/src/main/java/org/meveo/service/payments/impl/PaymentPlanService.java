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
package org.meveo.service.payments.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.plan.PaymentPlan;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.InvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class PaymentPlanService extends BusinessService<PaymentPlan> {

    @Inject
    private InvoiceService invoiceService;

    public Long create(PaymentPlanDto paymentPlanDto, List<AccountOperation> aos, CustomerAccount customerAccount, Date end, BigDecimal remainingAmount) {
        PaymentPlan paymentPlan = new PaymentPlan();

        build(paymentPlan, paymentPlanDto, customerAccount, aos, end, remainingAmount);
        paymentPlan.setStatus(PaymentPlanStatusEnum.DRAFT); // Default status

        super.create(paymentPlan);

        return paymentPlan.getId();

    }

    public Long update(Long id, PaymentPlanDto paymentPlanDto, List<AccountOperation> aos, CustomerAccount customerAccount, Date end, BigDecimal remainingAmount) {
        PaymentPlan paymentPlan = findById(id);

        build(paymentPlan, paymentPlanDto, customerAccount, aos, end, remainingAmount);

        super.update(paymentPlan);

        return paymentPlan.getId();

    }

    private void build(PaymentPlan paymentPlan, PaymentPlanDto paymentPlanDto,
                       CustomerAccount customerAccount, List<AccountOperation> aos,
                       Date end, BigDecimal remainingAmount) {
        paymentPlan.setCode(paymentPlanDto.getCode());
        paymentPlan.setDescription(paymentPlanDto.getDescription());
        paymentPlan.setCustomerAccount(customerAccount);
        paymentPlan.setTargetedAos(aos);

        paymentPlan.setRecurringUnit(paymentPlanDto.getRecurringUnit());
        paymentPlan.setActionOnRemainingAmount(paymentPlanDto.getActionOnRemainingAmount());

        paymentPlan.setStartDate(paymentPlanDto.getStartDate());
        paymentPlan.setEndDate(end);

        paymentPlan.setNumberOfInstallments(paymentPlanDto.getNumberOfInstallments());
        paymentPlan.setAmountPerInstallment(paymentPlanDto.getAmountPerInstallment());
        paymentPlan.setAmountToRecover(paymentPlanDto.getAmountToRecover());
        paymentPlan.setRemainingAmount(remainingAmount == null ? BigDecimal.ZERO : remainingAmount);
    }

    public void activate(PaymentPlan paymentPlan) {
        paymentPlan.setStatus(PaymentPlanStatusEnum.ACTIVE);
        changeInvoicePaymentStatus(paymentPlan, InvoicePaymentStatusEnum.PENDING_PLAN);

        super.update(paymentPlan);

    }

    // Check for different matched AOS, if the Plan shall be moved to COMPLETE status
    public void toComplete(List<Long> aos) {
        if (CollectionUtils.isNotEmpty(aos)) {
            // Find PaymentPlan of AOS
            List<PaymentPlan> pps = getEntityManager().createNamedQuery("PaymentPlan.findByCreatedAos")
                    .setParameter("AOS_ID", aos).getResultList();

            // For each PaymentPlan, get the matching status of the rest of AO
            Optional.ofNullable(pps).orElse(Collections.emptyList())
                    .forEach(paymentPlan -> {
                        List<Long> createdAosId = paymentPlan.getCreatedAos().stream()
                                .map(AccountOperation::getId)
                                .collect(Collectors.toList());

                        createdAosId.removeAll(aos);

                        // Check for PaymentPlan if the status shall be moved to COMPLETE
                        Set<MatchingStatusEnum> aoMatchingStatus = new HashSet<>(getEntityManager().createNamedQuery("PaymentPlan.findOtherLinkedAOSMatchingStatus")
                                .setParameter("AOS_ID", aos)
                                .setParameter("PP_ID", paymentPlan.getId())
                                .getResultList());

                        if (aoMatchingStatus.size() == 1 && aoMatchingStatus.contains(MatchingStatusEnum.L)) {
                            // Change PP status to COMPLETED if all AO are marched
                            paymentPlan.setStatus(PaymentPlanStatusEnum.COMPLETED);
                            super.update(paymentPlan);
                            log.info("PaymentPlan [id={}] passed to {} status", paymentPlan.getId(), paymentPlan.getStatus());

                            // Change Invoice status to PAID
                             changeInvoicePaymentStatus(paymentPlan, InvoicePaymentStatusEnum.PAID);
                        } else {
                            log.info("PaymentPlan [id={}] still in {} due to founded MatchingStatus {} for Aos {}",
                                    paymentPlan.getId(), paymentPlan.getStatus(), aoMatchingStatus, aos);
                        }
                    });
        }

    }

    // When at least one AccountOperation are passed to unmatch, related PaymentPlans shall be moved to ACTIVATE status
    public void toActivate(List<Long> aos) {
        if (CollectionUtils.isEmpty(aos)) {
            return;
        }
        // Find PaymentPlan of AOS
        List<PaymentPlan> pps = getEntityManager().createNamedQuery("PaymentPlan.findByCreatedAos")
                .setParameter("AOS_ID", aos).getResultList();

        Optional.ofNullable(pps).orElse(Collections.emptyList())
                .forEach(paymentPlan -> {
                    if (paymentPlan.getStatus() == PaymentPlanStatusEnum.COMPLETED) {
                        paymentPlan.setStatus(PaymentPlanStatusEnum.ACTIVE);
                        super.update(paymentPlan);
                        log.info("PaymentPlan [id={}] passed to {} status", paymentPlan.getId(), paymentPlan.getStatus());

                         changeInvoicePaymentStatus(paymentPlan, InvoicePaymentStatusEnum.PENDING_PLAN);
                    }
                });

    }

    private void changeInvoicePaymentStatus(PaymentPlan paymentPlan, InvoicePaymentStatusEnum paymentStatus) {
        // Change Invoice status to PAID
        paymentPlan.getTargetedAos().forEach(accountOperation -> {
            if (accountOperation instanceof RecordedInvoice) {
                Invoice inv = ((RecordedInvoice) accountOperation).getInvoice();
                inv.setPaymentStatus(paymentStatus);
                inv.setPaymentStatusDate(new Date());
                inv.setPaymentPlan(paymentPlan);
                invoiceService.update(inv);
                log.info("Invoice '{}' changed to '{}' since all linked AO of PaymentPlan '{}' are matched",
                        inv.getCode(), inv.getPaymentStatus(), paymentPlan.getCode());
            }
        });
    }


}