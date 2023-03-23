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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Matching between Account operations information
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "code" })
@Table(name = "ar_matching_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_matching_code_seq"), })
public class MatchingCode extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Code
     */
    @Column(name = "code", length = 255)
    @Size(max = 255)
    private String code;

    /**
     * Match type
     */
    @Column(name = "matching_type")
    @Enumerated(EnumType.STRING)
    private MatchingTypeEnum matchingType;

    /**
     * Date that matching was done
     */
    @Column(name = "matching_date")
    @Temporal(TemporalType.DATE)
    private Date matchingDate;

    /**
     * Matched account operations and amounts
     */
    @OneToMany(mappedBy = "matchingCode", cascade = CascadeType.ALL)
    private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

    /**
     * Total matched amount credit
     */
    @Column(name = "matching_amount_credit", precision = 23, scale = 12)
    private BigDecimal matchingAmountCredit;

    /**
     * Total matched amount debit
     */
    @Column(name = "matching_amount_debit", precision = 23, scale = 12)
    private BigDecimal matchingAmountDebit;

    /**
     * Total transactional matched amount credit
     */
    @Column(name = "transactional_matching_amount_credit", precision = 23, scale = 12)
    private BigDecimal transactionalMatchingAmountCredit;

    /**
     * Total transactional matched amount debit
     */
    @Column(name = "transactional_matching_amount_debit", precision = 23, scale = 12)
    private BigDecimal transactionalMatchingAmountDebit;
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getMatchingAmountCredit() {
        return matchingAmountCredit;
    }

    public void setMatchingAmountCredit(BigDecimal matchingAmountCredit) {
        this.matchingAmountCredit = matchingAmountCredit;
    }

    public BigDecimal getMatchingAmountDebit() {
        return matchingAmountDebit;
    }

    public void setMatchingAmountDebit(BigDecimal matchingAmountDebit) {
        this.matchingAmountDebit = matchingAmountDebit;
    }

    public MatchingTypeEnum getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(MatchingTypeEnum matchingType) {
        this.matchingType = matchingType;
    }

    public Date getMatchingDate() {
        return matchingDate;
    }

    public void setMatchingDate(Date matchingDate) {
        this.matchingDate = matchingDate;
    }

    public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
        this.matchingAmounts = matchingAmounts;
    }

    public List<MatchingAmount> getMatchingAmounts() {
        return matchingAmounts;
    }
    
    public BigDecimal getTransactionalMatchingAmountCredit() {
        return transactionalMatchingAmountCredit;
    }

    public void setTransactionalMatchingAmountCredit(BigDecimal transactionalMatchingAmountCredit) {
        this.transactionalMatchingAmountCredit = transactionalMatchingAmountCredit;
    }

    public BigDecimal getTransactionalMatchingAmountDebit() {
        return transactionalMatchingAmountDebit;
    }

    public void setTransactionalMatchingAmountDebit(BigDecimal transactionalMatchingAmountDebit) {
        this.transactionalMatchingAmountDebit = transactionalMatchingAmountDebit;
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

        MatchingCode other = (MatchingCode) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}
