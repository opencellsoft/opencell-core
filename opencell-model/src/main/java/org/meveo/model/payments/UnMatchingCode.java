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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @since 15.0
 */
@Entity
@Table(name = "ar_unmatching_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_unmatching_code_seq"),})
public class UnMatchingCode extends AuditableEntity {

    @Column(name = "unmatching_date")
    @Temporal(TemporalType.DATE)
    private Date unmatchingDate;

    @OneToMany(mappedBy = "unMatchingCode", cascade = CascadeType.ALL)
    private List<UnMatchingAmount> unmatchingAmounts = new ArrayList<>();

    @Column(name = "unmatching_amount_credit", precision = 23, scale = 12)
    private BigDecimal unmatchingAmountCredit;

    @Column(name = "unmatching_amount_debit", precision = 23, scale = 12)
    private BigDecimal unmatchingAmountDebit;

    @Column(name = "transactional_unmatching_amount_credit", precision = 23, scale = 12)
    private BigDecimal transactionalUnmatchingAmountCredit;

    @Column(name = "transactional_unmatching_amount_debit", precision = 23, scale = 12)
    private BigDecimal transactionalUnmatchingAmountDebit;

    public UnMatchingCode() {
    }

    public UnMatchingCode(MatchingCode matchingCode) {
        this.unmatchingDate = new Date();
        this.unmatchingAmountCredit = matchingCode.getMatchingAmountCredit();
        this.unmatchingAmountDebit = matchingCode.getMatchingAmountDebit();
        this.transactionalUnmatchingAmountCredit = matchingCode.getTransactionalMatchingAmountCredit();
        this.transactionalUnmatchingAmountDebit = matchingCode.getTransactionalMatchingAmountDebit();
    }

    public Date getUnmatchingDate() {
        return unmatchingDate;
    }

    public void setUnmatchingDate(Date unmatchingDate) {
        this.unmatchingDate = unmatchingDate;
    }

    public List<UnMatchingAmount> getUnmatchingAmounts() {
        return unmatchingAmounts;
    }

    public void setUnmatchingAmounts(List<UnMatchingAmount> unmatchingAmounts) {
        this.unmatchingAmounts = unmatchingAmounts;
    }

    public BigDecimal getUnmatchingAmountCredit() {
        return unmatchingAmountCredit;
    }

    public void setUnmatchingAmountCredit(BigDecimal unmatchingAmountCredit) {
        this.unmatchingAmountCredit = unmatchingAmountCredit;
    }

    public BigDecimal getUnmatchingAmountDebit() {
        return unmatchingAmountDebit;
    }

    public void setUnmatchingAmountDebit(BigDecimal unmatchingAmountDebit) {
        this.unmatchingAmountDebit = unmatchingAmountDebit;
    }

    public BigDecimal getTransactionalUnmatchingAmountCredit() {
        return transactionalUnmatchingAmountCredit;
    }

    public void setTransactionalUnmatchingAmountCredit(BigDecimal transactionalUnmatchingAmountCredit) {
        this.transactionalUnmatchingAmountCredit = transactionalUnmatchingAmountCredit;
    }

    public BigDecimal getTransactionalUnmatchingAmountDebit() {
        return transactionalUnmatchingAmountDebit;
    }

    public void setTransactionalUnmatchingAmountDebit(BigDecimal transactionalUnmatchingAmountDebit) {
        this.transactionalUnmatchingAmountDebit = transactionalUnmatchingAmountDebit;
    }

    @Override
    public int hashCode() {
        return 961 + ("MatchingCode" + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof MatchingCode)) {
            return false;
        }

        UnMatchingCode other = (UnMatchingCode) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }


}
