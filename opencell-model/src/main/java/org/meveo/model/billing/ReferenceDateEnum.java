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
 * What reference date to use when calculating the next invoicing date with an invoice calendar as in: BillingCycle.calendar.nextCalendarDate(referenceDate)
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public enum ReferenceDateEnum {
    /**
     * Today
     */
    TODAY,

    /**
     * Next invoice date set on billing account, subscription or order
     */
    NEXT_INVOICE_DATE,

    /**
     * Last transaction date as specified in Billing run
     */
    LAST_TRANSACTION_DATE,

    END_DATE;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
