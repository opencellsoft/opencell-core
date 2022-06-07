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

import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.plan.PaymentPlan;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Stateless
public class PaymentPlanService extends PersistenceService<PaymentPlan> {

    public void create(PaymentPlanDto paymentPlanDto, List<AccountOperation> aos, CustomerAccount customerAccount) {
        // To entity
        PaymentPlan paymentPlan = new PaymentPlan();

        paymentPlan.setCustomerAccount(customerAccount);
        paymentPlan.setAccountOperations(aos);

        paymentPlan.setStatus(paymentPlanDto.getStatus());
        paymentPlan.setRecurringUnit(paymentPlanDto.getRecurringUnit());
        paymentPlan.setActionOnRemainingAmount(paymentPlanDto.getActionOnRemainingAmount());

        paymentPlan.setStartDate(paymentPlanDto.getStartDate());

        paymentPlan.setNumberOfInstallments(paymentPlanDto.getNumberOfInstallments());
        paymentPlan.setAmountPerInstallment(paymentPlanDto.getAmountPerInstallment());
        paymentPlan.setAmountToRecover(paymentPlanDto.getAmountToRecover());
        paymentPlan.setRemainingAmount(paymentPlanDto.getRemainingAmount());

        // validation in service layer to avoid updating immuatble DTO
        // if endDate is given, check that value is correct and throw exception if not. If null, calculate it. endDate=startDate.addMonths(numberOfInstallments-1)
        LocalDate start = paymentPlanDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (paymentPlanDto.getEndDate() == null) {
            paymentPlan.setEndDate(Date.from(start.plusMonths(paymentPlanDto.getNumberOfInstallments() - 1L).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            LocalDate end = paymentPlanDto.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (!end.equals(start.plusMonths(paymentPlanDto.getNumberOfInstallments() - 1L))) {
                throw new BusinessApiException("Invalid end date '" + DateUtils.formatAsDate(paymentPlanDto.getEndDate()) + "', which should be equal to startDate.addMonths(numberOfInstallments-1)");
            }

            paymentPlan.setEndDate(paymentPlanDto.getEndDate());
        }

        super.create(paymentPlan);

    }


}