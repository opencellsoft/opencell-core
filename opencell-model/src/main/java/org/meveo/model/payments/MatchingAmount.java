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

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Matching amount among Account operations
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "ar_matching_amount")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_matching_amount_seq"), })
public class MatchingAmount extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Matching information
     */
    @ManyToOne
    @JoinColumn(name = "matching_code_id")
    private MatchingCode matchingCode;

    /**
     * Account operation matched
     */
    @ManyToOne
    @JoinColumn(name = "account_operation_id")
    private AccountOperation accountOperation;

    /**
     * Amount matched
     */
    @Column(name = "matching_amount", precision = 23, scale = 12)
    private BigDecimal matchingAmount;

    public MatchingAmount() {
    }

    public BigDecimal getMatchingAmount() {
        return matchingAmount;
    }

    public void setMatchingAmount(BigDecimal matchingAmount) {
        this.matchingAmount = matchingAmount;
    }

    public void setAccountOperation(AccountOperation accountOperation) {
        this.accountOperation = accountOperation;
    }

    public AccountOperation getAccountOperation() {
        return accountOperation;
    }

    public void setMatchingCode(MatchingCode matchingCode) {
        this.matchingCode = matchingCode;
    }

    public MatchingCode getMatchingCode() {
        return matchingCode;
    }

    @Override
    public int hashCode() {
        return 961 + ("MatchingAmount" + matchingCode).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof MatchingAmount)) {
            return false;
        }

        MatchingAmount other = (MatchingAmount) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (matchingCode != null && accountOperation != null) {
            if (matchingCode.equals(other.getMatchingCode()) && accountOperation.equals(other.getAccountOperation())) {
                return true;
            }
        }
        return false;
    }

}
