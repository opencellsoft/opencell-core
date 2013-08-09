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
package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "BILLING_SUBSCRIP_TERMIN_REASON", uniqueConstraints = @UniqueConstraint(columnNames = {
		"PROVIDER_ID", "CODE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_SUB_TERM_REASON_SEQ")
public class SubscriptionTerminationReason extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "APPLY_AGREEMENT")
	private boolean applyAgreement;

	@Column(name = "APPLY_REIMBURSMENT")
	private boolean applyReimbursment;

	@Column(name = "APPLY_TERMINATION_CHARGES")
	private boolean applyTerminationCharges;

	public String getCode() {
		return code;
	}

	public void setCode(String reasonCode) {
		this.code = reasonCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isApplyAgreement() {
		return applyAgreement;
	}

	public void setApplyAgreement(boolean applyAgreement) {
		this.applyAgreement = applyAgreement;
	}

	public boolean isApplyReimbursment() {
		return applyReimbursment;
	}

	public void setApplyReimbursment(boolean applyReimbursment) {
		this.applyReimbursment = applyReimbursment;
	}

	public boolean isApplyTerminationCharges() {
		return applyTerminationCharges;
	}

	public void setApplyTerminationCharges(boolean applyTerminationCharges) {
		this.applyTerminationCharges = applyTerminationCharges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;

		SubscriptionTerminationReason other = (SubscriptionTerminationReason) obj;
		if (other.getId() == getId())
			return true;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
