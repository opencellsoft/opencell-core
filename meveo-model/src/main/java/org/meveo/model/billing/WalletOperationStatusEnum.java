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

public enum WalletOperationStatusEnum {
	OPEN(1, "walletOperationStatus.open"), TREATED(2, "walletOperationStatus.treated"), CANCELED(3,
			"walletOperationStatus.canceled"), RESERVED(4, "walletOperationStatus.reserved"), TO_RERATE(5,
			"walletOperationStatus.to_rerate"), RERATED(6, "walletOperationStatus.rerated") ;

	private Integer id;
	private String label;

	private WalletOperationStatusEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Integer getId() {
		return id;
	}

	public static WalletOperationStatusEnum getValue(Integer id) {
		if (id != null) {
			for (WalletOperationStatusEnum status : values()) {
				if (id.equals(status.getId())) {
					return status;
				}
			}
		}
		return null;
	}

	public String toString() {
		return name();
	}

}
