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
package org.meveo.model.billing;

public enum BillingRunStatusEnum {

	NEW(1, "BillingRunStatusEnum.new"), 
	WAITING(2, "BillingRunStatusEnum.waiting"), 
	ON_GOING(3, "BillingRunStatusEnum.onGoing"), 
	CANCELED(4, "BillingRunStatusEnum.canceled"),
	TERMINATED(5, "BillingRunStatusEnum.terminated"),
	CONFIRMED(6, "BillingRunStatusEnum.confirmed"),
	VALIDATED(7, "BillingRunStatusEnum.validated");
	
	
	

	private Integer id;
	private String label;

	BillingRunStatusEnum(Integer id, String label) {
		this.id = id;
		this.label = label;

	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Gets enum by its id.
	 */
	public static BillingRunStatusEnum getValue(Integer id) {
		if (id != null) {
			for (BillingRunStatusEnum status : values()) {
				if (id.equals(status.getId())) {
					return status;
				}
			}
		}
		return null;
	}
}
