/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_MATCHING_AMOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_MATCHING_AMOUNT_SEQ")
public class MatchingAmount extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "MATCHING_CODE_ID")
	private MatchingCode matchingCode;

	@ManyToOne
	@JoinColumn(name = "ACCOUNT_OPERATION_ID")
	private AccountOperation accountOperation;

	@Column(name = "MATCHING_AMOUNT", precision = 23, scale = 12)
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((matchingCode == null) ? 0 : matchingCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		MatchingAmount other = (MatchingAmount) obj;

		if (matchingCode != null && accountOperation != null) {
			if (matchingCode.equals(other.getMatchingCode())
					&& accountOperation.equals(other.getAccountOperation())) {
				return true;
			}
		}
		return false;
	}

}
