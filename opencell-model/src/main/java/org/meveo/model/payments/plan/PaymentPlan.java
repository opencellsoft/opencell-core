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
package org.meveo.model.payments.plan;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.RecurrenceUnitEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ar_payment_plan")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "ar_payment_plan_seq")})
public class PaymentPlan extends BusinessEntity {

    @Column(name = "amount_to_recover", nullable = false)
    public BigDecimal amountToRecover;

    @Column(name = "amount_per_installment", nullable = false)
    public BigDecimal amountPerInstallment;

    @Column(name = "remaining_amount")
    public BigDecimal remainingAmount = BigDecimal.ZERO;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "action_on_remaining_amount", nullable = false)
    public ActionOnRemainingAmountEnum actionOnRemainingAmount = ActionOnRemainingAmountEnum.FIRST;

    @Column(name = "number_of_installments", nullable = false)
    public Integer numberOfInstallments;

    @Column(name = "start_date", nullable = false)
    public Date startDate;

    @Column(name = "end_date", nullable = false)
    public Date endDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "recurring_unit", nullable = false)
    public RecurrenceUnitEnum recurringUnit = RecurrenceUnitEnum.MONTH;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    public PaymentPlanStatusEnum status = PaymentPlanStatusEnum.DRAFT;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ar_payment_plan_created_aos",
            joinColumns = @JoinColumn(name = "payment_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "account_operation_id"))
    private List<AccountOperation> createdAos = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ar_payment_plan_targeted_aos",
            joinColumns = @JoinColumn(name = "payment_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "account_operation_id"))
    private List<AccountOperation> targetedAos = new ArrayList<>();

    /**
     * Customer account for account operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

    public BigDecimal getAmountToRecover() {
        return amountToRecover;
    }

    public void setAmountToRecover(BigDecimal amountToRecover) {
        this.amountToRecover = amountToRecover;
    }

    public BigDecimal getAmountPerInstallment() {
        return amountPerInstallment;
    }

    public void setAmountPerInstallment(BigDecimal amountPerInstallment) {
        this.amountPerInstallment = amountPerInstallment;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public ActionOnRemainingAmountEnum getActionOnRemainingAmount() {
        return actionOnRemainingAmount;
    }

    public void setActionOnRemainingAmount(ActionOnRemainingAmountEnum actionOnRemainingAmount) {
        this.actionOnRemainingAmount = actionOnRemainingAmount;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public RecurrenceUnitEnum getRecurringUnit() {
        return recurringUnit;
    }

    public void setRecurringUnit(RecurrenceUnitEnum recurringUnit) {
        this.recurringUnit = recurringUnit;
    }

    public PaymentPlanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PaymentPlanStatusEnum status) {
        this.status = status;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public List<AccountOperation> getCreatedAos() {
        return createdAos;
    }

    public void setCreatedAos(List<AccountOperation> createdAos) {
        this.createdAos = createdAos;
    }

    public List<AccountOperation> getTargetedAos() {
        return targetedAos;
    }

    public void setTargetedAos(List<AccountOperation> targetedAos) {
        this.targetedAos = targetedAos;
    }
}
