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
