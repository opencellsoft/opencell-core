/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.payments;

public enum DunningLevelEnum {

	R0(1, "DunningLevelEnum.R0"), R1(2, "DunningLevelEnum.R1"), R2(3, "DunningLevelEnum.R2"), R3(4, "DunningLevelEnum.R3"), R4(5, "DunningLevelEnum.R4"),
	R5(6, "DunningLevelEnum.R5"), R6(7, "DunningLevelEnum.R6");

	private Integer id;
	private String label;

	DunningLevelEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}

	public static DunningLevelEnum getValue(Integer id) {
		if (id != null) {
			for (DunningLevelEnum status : values()) {
				if (status.getId().intValue() == id.intValue()) {
					return status;
				}
			}
		}
		return null;
	}
}
