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
package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * UnMatchingAmount of Account operations
 *
 * @since 15.0
 */
@Entity
@Table(name = "ar_unmatching_amount")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_unmatching_amount_seq"),})
public class UnMatchingAmount extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "unmatching_code_id")
    private UnMatchingCode unMatchingCode;

    @ManyToOne
    @JoinColumn(name = "account_operation_id")
    private AccountOperation accountOperation;

    @Column(name = "unmatching_amount", precision = 23, scale = 12)
    private BigDecimal unmatchingAmount;

    @Column(name = "transactional_unmatching_amount", precision = 23, scale = 12)
    private BigDecimal transactionalUnmatchingAmount;

    public UnMatchingAmount() {
    }

    public UnMatchingAmount(MatchingAmount matchingAmount, UnMatchingCode unMatchingCode) {
        this.accountOperation = matchingAmount.getAccountOperation();
        this.unmatchingAmount = matchingAmount.getMatchingAmount();
        this.transactionalUnmatchingAmount = matchingAmount.getTransactionalMatchingAmount();
        this.unMatchingCode = unMatchingCode;
    }

    public UnMatchingCode getUnMatchingCode() {
        return unMatchingCode;
    }

    public void setUnMatchingCode(UnMatchingCode unMatchingCode) {
        this.unMatchingCode = unMatchingCode;
    }

    public AccountOperation getAccountOperation() {
        return accountOperation;
    }

    public void setAccountOperation(AccountOperation accountOperation) {
        this.accountOperation = accountOperation;
    }

    public BigDecimal getUnmatchingAmount() {
        return unmatchingAmount;
    }

    public void setUnmatchingAmount(BigDecimal unmatchingAmount) {
        this.unmatchingAmount = unmatchingAmount;
    }

    public BigDecimal getTransactionalUnmatchingAmount() {
        return transactionalUnmatchingAmount;
    }

    public void setTransactionalUnmatchingAmount(BigDecimal transactionalUnmatchingAmount) {
        this.transactionalUnmatchingAmount = transactionalUnmatchingAmount;
    }

    @Override
    public int hashCode() {
        return 961 + ("UnmatchingAmount" + unMatchingCode).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof UnMatchingAmount)) {
            return false;
        }

        UnMatchingAmount other = (UnMatchingAmount) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (unMatchingCode != null && accountOperation != null) {
            if (unMatchingCode.equals(other.getUnMatchingCode()) && accountOperation.equals(other.getAccountOperation())) {
                return true;
            }
        }
        return false;
    }


}
