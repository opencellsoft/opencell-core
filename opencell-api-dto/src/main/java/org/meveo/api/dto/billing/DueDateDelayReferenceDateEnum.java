package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Edward P. Legaspi
 * @created 13 Sep 2017
 */
@XmlEnum
public enum DueDateDelayReferenceDateEnum {

	@XmlEnumValue("INVOICE_DATE")
	INVOICE_DATE(
			"#{ (mv:addToDate(invoice.invoiceDate, 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

	@XmlEnumValue("INVOICE_GENERATION_DATE")
	INVOICE_GENERATION_DATE(
			"#{ (mv:addToDate(invoice.auditable.created, 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

	@XmlEnumValue("END_OF_MONTH_INVOICE_DATE")
	END_OF_MONTH_INVOICE_DATE(
			"#{ (mv:addToDate(mv:getEndOfMonth(invoice.invoiceDate), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

	@XmlEnumValue("NEXT_MONTH_INVOICE_DATE")
	NEXT_MONTH_INVOICE_DATE(
			"#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.invoiceDate), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

	@XmlEnumValue("END_OF_MONTH_INVOICE_GENERATION_DATE")
	END_OF_MONTH_INVOICE_GENERATION_DATE(
			"#{ (mv:addToDate(mv:getEndOfMonth(invoice.auditable.created), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

	@XmlEnumValue("NEXT_MONTH_INVOICE_GENERATION_DATE")
	NEXT_MONTH_INVOICE_GENERATION_DATE(
			"#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.auditable.created), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }");

	private String el;

	private DueDateDelayReferenceDateEnum(String el) {
		this.el = el;
	}

	public String getEl() {
		return el;
	}

	public String evaluateNumberOfDays(int numberOfDays) {
		return String.format(getEl(), numberOfDays);
	}

	public static DueDateDelayReferenceDateEnum guestExpression(String expression) {
		DueDateDelayReferenceDateEnum result = null;

		expression = expression.replaceAll("\\s+", "");

		if (expression.contains("mv:getEndOfMonth(invoice.invoiceDate)")) {
			result = END_OF_MONTH_INVOICE_DATE;
		} else if (expression.contains("mv:getStartOfNextMonth(invoice.invoiceDate)")) {
			result = NEXT_MONTH_INVOICE_DATE;
		} else if (expression.contains("mv:getEndOfMonth(invoice.auditable.created)")) {
			result = END_OF_MONTH_INVOICE_GENERATION_DATE;
		} else if (expression.contains("mv:getStartOfNextMonth(invoice.auditable.created)")) {
			result = NEXT_MONTH_INVOICE_GENERATION_DATE;
		} else if (expression.contains("mv:addToDate(invoice.invoiceDate")) {
			result = INVOICE_DATE;
		} else if (expression.contains("mv:addToDate(invoice.auditable.created")) {
			result = INVOICE_GENERATION_DATE;
		}

		return result;
	}

	public static int guestNumberOfDays(DueDateDelayReferenceDateEnum dueDateDelayReferenceDate, String expression) {
		int result = 0;

		expression = expression.replaceAll("\\s+", "");

		if (dueDateDelayReferenceDate.equals(INVOICE_DATE)) {
			String preMatch = "mv:addToDate(invoice.invoiceDate,5,";
			String postMatch = ").getTime()";
			result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(),
					expression.indexOf(postMatch)));
		} else if (dueDateDelayReferenceDate.equals(INVOICE_GENERATION_DATE)) {
			String preMatch = "mv:addToDate(invoice.auditable.created,5,";
			String postMatch = ").getTime()";
			result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(),
					expression.indexOf(postMatch)));
		} else if (dueDateDelayReferenceDate.equals(END_OF_MONTH_INVOICE_DATE)) {
			String preMatch = "mv:addToDate(mv:getEndOfMonth(invoice.invoiceDate),5,";
			String postMatch = ").getTime()";
			result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(),
					expression.indexOf(postMatch)));
		} else if (dueDateDelayReferenceDate.equals(NEXT_MONTH_INVOICE_DATE)) {
			String preMatch = "mv:getStartOfNextMonth(invoice.invoiceDate),5,";
			String postMatch = ").getTime()";
			result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(),
					expression.indexOf(postMatch)));
		} else if (dueDateDelayReferenceDate.equals(END_OF_MONTH_INVOICE_GENERATION_DATE)) {
			String preMatch = "mv:addToDate(mv:getEndOfMonth(invoice.auditable.created),5,";
			String postMatch = ").getTime()";
			result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(),
					expression.indexOf(postMatch)));
		} else if (dueDateDelayReferenceDate.equals(NEXT_MONTH_INVOICE_GENERATION_DATE)) {
			String preMatch = "mv:addToDate(mv:getStartOfNextMonth(invoice.auditable.created),5,";
			String postMatch = ").getTime()";
			result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(),
					expression.indexOf(postMatch)));
		}

		return result;
	}

}
