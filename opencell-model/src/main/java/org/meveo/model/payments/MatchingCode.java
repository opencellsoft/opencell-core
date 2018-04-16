/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "ar_matching_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_matching_code_seq"), })
public class MatchingCode extends EnableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "code", length = 255)
    @Size(max = 255)
    private String code;

    @Column(name = "matching_type")
    @Enumerated(EnumType.STRING)
    private MatchingTypeEnum matchingType;

    @Column(name = "matching_date")
    @Temporal(TemporalType.DATE)
    private Date matchingDate;

    @OneToMany(mappedBy = "matchingCode", cascade = CascadeType.ALL)
    private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

    @Column(name = "matching_amount_credit", precision = 23, scale = 12)
    private BigDecimal matchingAmountCredit;

    @Column(name = "matching_amount_debit", precision = 23, scale = 12)
    private BigDecimal matchingAmountDebit;

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
