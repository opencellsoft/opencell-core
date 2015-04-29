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
package org.meveo.model.catalog;

public enum UsageChgTemplateEnum {

	INTEGER(1, "UsageChgTemplateEnum.integer"), DECIMAL(2,
			"UsageChgTemplateEnum.decimal"), HOURMINSEC(3,
			"UsageChgTemplateEnum.hourMinSec");

	private Integer id;
	private String label;

	UsageChgTemplateEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static UsageChgTemplateEnum getValue(Integer id) {
		if (id != null) {
			for (UsageChgTemplateEnum type : values()) {
				if (id.equals(type.getId())) {
					return type;
				}
			}
		}
		
		return null;
	}

}
