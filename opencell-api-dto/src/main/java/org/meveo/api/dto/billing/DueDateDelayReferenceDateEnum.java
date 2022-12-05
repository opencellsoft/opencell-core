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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * The Enum DueDateDelayReferenceDateEnum.
 *
 * @author Edward P. Legaspi
 * @since 13 Sep 2017
 */
@XmlEnum
public enum DueDateDelayReferenceDateEnum {

    /** The invoice date. */
    @XmlEnumValue("INVOICE_DATE")
    INVOICE_DATE("#{ (mv:addToDate(invoice.invoiceDate, 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

    /** The invoice generation date. */
    @XmlEnumValue("INVOICE_GENERATION_DATE")
    INVOICE_GENERATION_DATE("#{ (mv:addToDate(invoice.auditable.created, 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

    /** The end of month invoice date. */
    @XmlEnumValue("END_OF_MONTH_INVOICE_DATE")
    END_OF_MONTH_INVOICE_DATE("#{ (mv:addToDate(mv:getEndOfMonth(invoice.invoiceDate), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

    /** The next month invoice date. */
    @XmlEnumValue("NEXT_MONTH_INVOICE_DATE")
    NEXT_MONTH_INVOICE_DATE("#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.invoiceDate), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

    /** The end of month invoice generation date. */
    @XmlEnumValue("END_OF_MONTH_INVOICE_GENERATION_DATE")
    END_OF_MONTH_INVOICE_GENERATION_DATE("#{ (mv:addToDate(mv:getEndOfMonth(invoice.auditable.created), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }"), //

    /** The next month invoice generation date. */
    @XmlEnumValue("NEXT_MONTH_INVOICE_GENERATION_DATE")
    NEXT_MONTH_INVOICE_GENERATION_DATE(
            "#{ (mv:addToDate(mv:getStartOfNextMonth(invoice.auditable.created), 5, %d).getTime() - invoice.invoiceDate.getTime()) / 24 / 3600 / 1000 }");

    /** The el. */
    private String el;

    /**
     * Instantiates a new due date delay reference date enum.
     *
     * @param el the el
     */
    private DueDateDelayReferenceDateEnum(String el) {
        this.el = el;
    }

    /**
     * Gets the el.
     *
     * @return the el
     */
    public String getEl() {
        return el;
    }

    /**
     * Evaluate number of days.
     *
     * @param numberOfDays the number of days
     * @return the string
     */
    public String evaluateNumberOfDays(int numberOfDays) {
        return String.format(getEl(), numberOfDays);
    }

    /**
     * Guest expression.
     *
     * @param expression the expression
     * @return the due date delay reference date enum
     */
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

    /**
     * Guest number of days.
     *
     * @param dueDateDelayReferenceDate the due date delay reference date
     * @param expression the expression
     * @return the int
     */
    public static int guestNumberOfDays(DueDateDelayReferenceDateEnum dueDateDelayReferenceDate, String expression) {
        int result = 0;

        expression = expression.replaceAll("\\s+", "");

        if (dueDateDelayReferenceDate.equals(INVOICE_DATE)) {
            String preMatch = "mv:addToDate(invoice.invoiceDate,5,";
            String postMatch = ").getTime()";
            result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(), expression.indexOf(postMatch)));
        } else if (dueDateDelayReferenceDate.equals(INVOICE_GENERATION_DATE)) {
            String preMatch = "mv:addToDate(invoice.auditable.created,5,";
            String postMatch = ").getTime()";
            result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(), expression.indexOf(postMatch)));
        } else if (dueDateDelayReferenceDate.equals(END_OF_MONTH_INVOICE_DATE)) {
            String preMatch = "mv:addToDate(mv:getEndOfMonth(invoice.invoiceDate),5,";
            String postMatch = ").getTime()";
            result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(), expression.indexOf(postMatch)));
        } else if (dueDateDelayReferenceDate.equals(NEXT_MONTH_INVOICE_DATE)) {
            String preMatch = "mv:getStartOfNextMonth(invoice.invoiceDate),5,";
            String postMatch = ").getTime()";
            result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(), expression.indexOf(postMatch)));
        } else if (dueDateDelayReferenceDate.equals(END_OF_MONTH_INVOICE_GENERATION_DATE)) {
            String preMatch = "mv:addToDate(mv:getEndOfMonth(invoice.auditable.created),5,";
            String postMatch = ").getTime()";
            result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(), expression.indexOf(postMatch)));
        } else if (dueDateDelayReferenceDate.equals(NEXT_MONTH_INVOICE_GENERATION_DATE)) {
            String preMatch = "mv:addToDate(mv:getStartOfNextMonth(invoice.auditable.created),5,";
            String postMatch = ").getTime()";
            result = Integer.parseInt(expression.substring(expression.indexOf(preMatch) + preMatch.length(), expression.indexOf(postMatch)));
        }

        return result;
    }

}