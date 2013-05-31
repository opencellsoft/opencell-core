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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_MATCHING_CODE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_MATCHING_CODE_SEQ")
public class MatchingCode extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "CODE")
	private String code;

	@Column(name = "MATCHING_TYPE")
	@Enumerated(EnumType.STRING)
	private MatchingTypeEnum matchingType;

	@Column(name = "MATCHING_DATE")
	@Temporal(TemporalType.DATE)
	private Date matchingDate;

	@OneToMany(mappedBy = "matchingCode", cascade = CascadeType.ALL)
	private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

	@Column(name = "MATCHING_AMOUNT_CREDIT", precision = 23, scale = 12)
	private BigDecimal matchingAmountCredit;

	@Column(name = "MATCHING_AMOUNT_DEBIT", precision = 23, scale = 12)
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		MatchingCode other = (MatchingCode) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
