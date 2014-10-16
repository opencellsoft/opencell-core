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

public enum OperationTypeEnum {

	CREDIT(1, "operationTypeEnum.credit"), 
	DEBIT(2, "operationTypeEnum.debit");

	private String label;
	private Integer id;

	OperationTypeEnum(Integer id, String label) {
		this.label = label;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static OperationTypeEnum getValue(Integer id) {
		if (id != null) {
			for (OperationTypeEnum type : values()) {
				if (id.equals(type.id)) {
					return type;
				}
			}
		}
		return null;
	}

}
