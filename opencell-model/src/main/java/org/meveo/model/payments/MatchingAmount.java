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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;

@Entity
@Table(name = "ar_matching_amount")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_matching_amount_seq"), })
public class MatchingAmount extends EnableEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "matching_code_id")
    private MatchingCode matchingCode;

    @ManyToOne
    @JoinColumn(name = "account_operation_id")
    private AccountOperation accountOperation;

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
