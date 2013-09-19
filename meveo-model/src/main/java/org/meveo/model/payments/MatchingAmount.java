/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
