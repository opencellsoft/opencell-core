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
import org.meveo.model.AuditableEntity;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ar_payment_plan")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "ar_payment_plan_seq")})
public class PaymentPlan extends AuditableEntity {

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
    public BigDecimal numberOfInstallments;

    @Column(name = "start_date", nullable = false)
    public LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    public LocalDate endDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "recurring_unit", nullable = false)
    public RecurrenceUnitEnum recurringUnit = RecurrenceUnitEnum.MONTH;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    public PaymentPlanStatusEnum status = PaymentPlanStatusEnum.DRAFT;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ar_payment_plan_acc_operations",
            joinColumns = @JoinColumn(name = "payment_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "account_operation_id"))
    private Set<AccountOperation> accountOperations = new HashSet<>();

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

    public BigDecimal getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(BigDecimal numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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

    public Set<AccountOperation> getAccountOperations() {
        return accountOperations;
    }

    public void setAccountOperations(Set<AccountOperation> accountOperations) {
        this.accountOperations = accountOperations;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

}
