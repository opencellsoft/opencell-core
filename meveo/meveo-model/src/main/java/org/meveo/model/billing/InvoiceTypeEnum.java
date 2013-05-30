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
