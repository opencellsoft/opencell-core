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

import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.plan.PaymentPlan;
import org.meveo.model.payments.plan.PaymentPlanStatusEnum;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class PaymentPlanService extends BusinessService<PaymentPlan> {

    public Long create(PaymentPlanDto paymentPlanDto, List<AccountOperation> aos, CustomerAccount customerAccount, Date end) {
        PaymentPlan paymentPlan = new PaymentPlan();

        build(paymentPlan, paymentPlanDto, customerAccount, aos, end);
        paymentPlan.setStatus(PaymentPlanStatusEnum.DRAFT); // Default status

        super.create(paymentPlan);

        return paymentPlan.getId();

    }

    public Long update(Long id, PaymentPlanDto paymentPlanDto, List<AccountOperation> aos, CustomerAccount customerAccount, Date end) {
        PaymentPlan paymentPlan = findById(id);

        build(paymentPlan, paymentPlanDto, customerAccount, aos, end);

        super.update(paymentPlan);

        return paymentPlan.getId();

    }

    private void build(PaymentPlan paymentPlan, PaymentPlanDto paymentPlanDto, CustomerAccount customerAccount, List<AccountOperation> aos, Date end) {
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
        paymentPlan.setRemainingAmount(paymentPlanDto.getRemainingAmount() == null ? BigDecimal.ZERO : paymentPlanDto.getRemainingAmount());
    }


}