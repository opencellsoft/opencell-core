/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

/**
 * Invoice type.
 */
public enum InvoiceTypeEnum {

	/**
	 * This is the normal invoice.
	 */
	COMMERCIAL(1, "invoiceType.commercial",false), 
	SELF_BILLED(2, "invoiceType.selfBilled",false), 
	PROFORMA(3, "invoiceType.proforma",false), 
	CORRECTED(4, "invoiceType.corrected",false), 
	CREDIT_NOTE(5, "invoiceType.creditNote",false), 
	DEBIT_NOTE(6, "invoiceType.debitNote",false),
	/**
	 * Invoice adjustment.
	 */
	CREDIT_NOTE_ADJUST(7, "invoiceType.creditNoteAdjust",true), 
	DEBIT_NODE_ADJUST(8, "invoiceType.debitNodeAdjust",true), 
	SELF_BILLED_CREDIT_NOTE(9, "invoiceType.selfBilledCreditNote",true);

	private Integer id;
	private String label;
	private boolean isAdjustment;

	private InvoiceTypeEnum(Integer id, String label, boolean isAdjustment) {
		this.id = id;
		this.label = label;
		this.isAdjustment = isAdjustment;
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

	public boolean isAdjustment() {
		return isAdjustment;
	}

	public void setAdjustment(boolean isAdjustment) {
		this.isAdjustment = isAdjustment;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
