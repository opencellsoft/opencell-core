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
package org.meveo.model.billing;

/**
 * Invoice Payment status.
 */
public enum InvoicePaymentStatusEnum {

	/**
	 * invoice has no payment status, no AO created.
	 */
	NONE(1, "invoicePaymentStatusEnum.none"),

	/**
	 * AO created, due date is still in the future
	 */
	PENDING(1, "invoicePaymentStatusEnum.pending"), 

	/**
	 * invoice has no payment status, no AO created.
	 */
	PAID(1, "invoicePaymentStatusEnum.paid"),

	/**
	 * invoice has no payment status, no AO created.
	 */
	PPAID(1, "invoicePaymentStatusEnum.pPaid"),

	/**
	 * invoice has no payment status, no AO created.
	 */
	UNPAID(1, "invoicePaymentStatusEnum.unPaid"),

	/**
	 * invoice has no payment status, no AO created.
	 */
	ABANDONED(1, "invoicePaymentStatusEnum.abandoned"),

	/**
	 * invoice has no payment status, no AO created.
	 */
	REFUNDED(1, "invoicePaymentStatusEnum.refunded"),

	/**
	 * invoice has no payment status, no AO created.
	 */
	DISPUTED(1, "invoicePaymentStatusEnum.disputed");

	private Integer id;
	private String label;

	InvoicePaymentStatusEnum(Integer id, String label) {
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
	 * 
	 * @param id of invoice payment status
	 * @return invoice payment status enum
	 */
	public static InvoicePaymentStatusEnum getValue(Integer id) {
		if (id != null) {
			for (InvoicePaymentStatusEnum status : values()) {
				if (id.equals(status.getId())) {
					return status;
				}
			}
		}
		return null;
	}
}
