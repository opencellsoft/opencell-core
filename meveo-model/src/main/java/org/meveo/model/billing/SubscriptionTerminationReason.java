/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
