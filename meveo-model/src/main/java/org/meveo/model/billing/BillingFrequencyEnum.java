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

import org.meveo.model.IdentifiableEnum;

/**
 * Billing frequency.
 */
public enum BillingFrequencyEnum implements IdentifiableEnum {

	DAILY(1, "billingFrequency.daily"), 
	WEEKLY(2, "billingFrequency.weekly"), 
	BIMONTHLY(3, "billingFrequency.bimonthly"), 
	MONTHLY(4, "billingFrequency.monthly"), 
	QUARTERLY(5, "billingFrequency.quarterly"), 
	BIANNUAL(6, "billingFrequency.biannual"), 
	ANNUAL(7, "billingFrequency.annual");

	private Integer id;
	private String label;

	BillingFrequencyEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return this.label;
	}

	public static BillingFrequencyEnum getValue(Integer id) {
		if (id != null) {
			for (BillingFrequencyEnum status : values()) {
				if (id.equals(status.getId())) {
					return status;
				}
			}
		}
		return null;
	}

}
