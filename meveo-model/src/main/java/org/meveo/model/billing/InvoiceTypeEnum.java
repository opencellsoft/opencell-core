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

/**
 * Invoice type.
 */
public enum InvoiceTypeEnum {

	COMMERCIAL(1, "invoiceType.commercial"), 
	SELF_BILLED(2, "invoiceType.selfBilled"), 
	PROFORMA(3, "invoiceType.proforma"), 
	CORRECTED(4, "invoiceType.corrected"), 
	CREDIT_NOTE(5, "invoiceType.creditNote"), 
	DEBIT_NOTE(6, "invoiceType.debitNote"), 
	CREDIT_NOTE_ADJUST(7, "invoiceType.creditNoteAdjust"), 
	DEBIT_NODE_ADJUST(8, "invoiceType.debitNodeAdjust"), 
	SELF_BILLED_CREDIT_NOTE(9, "invoiceType.selfBilledCreditNote");

	private Integer id;
	private String label;

	private InvoiceTypeEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return this.label;
	}

	public static InvoiceTypeEnum getValue(Integer id) {
		if (id != null) {
			for (InvoiceTypeEnum status : values()) {
				if (id.equals(status.getId())) {
					return status;
				}
			}
		}
		return null;
	}
}
