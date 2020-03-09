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

package org.meveo.api.dto.billing;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Edward P. Legaspi
 * @since 14 Sep 2017
 */
public class DueDateDelayReferenceDateEnumTest {

	@Test
	public void testMatch() {
		Assert.assertEquals(DueDateDelayReferenceDateEnum.INVOICE_DATE, DueDateDelayReferenceDateEnum.guestExpression(
				"#{ (mv:addToDate(invoice.invoiceDate, 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"));

		Assert.assertEquals(DueDateDelayReferenceDateEnum.INVOICE_GENERATION_DATE,
				DueDateDelayReferenceDateEnum.guestExpression(
						"#{ (mv:addToDate(invoice.auditable.created, 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"));

		Assert.assertEquals(DueDateDelayReferenceDateEnum.END_OF_MONTH_INVOICE_DATE,
				DueDateDelayReferenceDateEnum.guestExpression(
						"#{ (mv:addToDate(mv:getEndOfMonth(invoice.invoiceDate), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"));

		Assert.assertEquals(DueDateDelayReferenceDateEnum.NEXT_MONTH_INVOICE_DATE,
				DueDateDelayReferenceDateEnum.guestExpression(
						"#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.invoiceDate), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"));

		Assert.assertEquals(DueDateDelayReferenceDateEnum.END_OF_MONTH_INVOICE_GENERATION_DATE,
				DueDateDelayReferenceDateEnum.guestExpression(
						"#{ (mv:addToDate(mv:getEndOfMonth(invoice.auditable.created), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"));

		Assert.assertEquals(DueDateDelayReferenceDateEnum.NEXT_MONTH_INVOICE_GENERATION_DATE,
				DueDateDelayReferenceDateEnum.guestExpression(
						"#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.auditable.created), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"));
	}

	@Test
	public void testGetNumberOfDays() {
		DueDateDelayReferenceDateEnum dueDateDelayReferenceDate = DueDateDelayReferenceDateEnum.INVOICE_DATE;
		Assert.assertEquals(15, DueDateDelayReferenceDateEnum.guestNumberOfDays(dueDateDelayReferenceDate,
				"#{ (mv:addToDate(invoice.invoiceDate, 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"),
				15);

		dueDateDelayReferenceDate = DueDateDelayReferenceDateEnum.INVOICE_GENERATION_DATE;
		Assert.assertEquals(15, DueDateDelayReferenceDateEnum.guestNumberOfDays(dueDateDelayReferenceDate,
				"#{ (mv:addToDate(invoice.auditable.created, 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"),
				15);

		dueDateDelayReferenceDate = DueDateDelayReferenceDateEnum.END_OF_MONTH_INVOICE_DATE;
		Assert.assertEquals(15, DueDateDelayReferenceDateEnum.guestNumberOfDays(dueDateDelayReferenceDate,
				"#{ (mv:addToDate(mv:getEndOfMonth(invoice.invoiceDate), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"),
				15);

		dueDateDelayReferenceDate = DueDateDelayReferenceDateEnum.NEXT_MONTH_INVOICE_DATE;
		Assert.assertEquals(15, DueDateDelayReferenceDateEnum.guestNumberOfDays(dueDateDelayReferenceDate,
				"#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.invoiceDate), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"),
				15);

		dueDateDelayReferenceDate = DueDateDelayReferenceDateEnum.END_OF_MONTH_INVOICE_GENERATION_DATE;
		Assert.assertEquals(15, DueDateDelayReferenceDateEnum.guestNumberOfDays(dueDateDelayReferenceDate,
				"#{ (mv:addToDate(mv:getEndOfMonth(invoice.auditable.created), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"),
				15);

		dueDateDelayReferenceDate = DueDateDelayReferenceDateEnum.NEXT_MONTH_INVOICE_GENERATION_DATE;
		Assert.assertEquals(15, DueDateDelayReferenceDateEnum.guestNumberOfDays(dueDateDelayReferenceDate,
				"#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.auditable.created), 5, 15).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"),
				15);
	}

}
