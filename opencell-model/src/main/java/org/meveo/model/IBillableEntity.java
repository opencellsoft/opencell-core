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
package org.meveo.model;

import java.math.BigDecimal;
import java.util.List;

import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.cpq.commercial.InvoiceLine;

public interface IBillableEntity extends IEntity {

    BillingRun getBillingRun();

    void setBillingRun(BillingRun billingRun);

    void setMinRatedTransactions(List<RatedTransaction> ratedTransactions);

    List<RatedTransaction> getMinRatedTransactions();

    BigDecimal getTotalInvoicingAmountWithoutTax();

    void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax);

    BigDecimal getTotalInvoicingAmountWithTax();

    void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax);

    BigDecimal getTotalInvoicingAmountTax();

    void setTotalInvoicingAmountTax(BigDecimal totalInvoicingAmountTax);

    String getCode();

    BillingCycle getBillingCycle();

    List<InvoiceLine> getMinInvoiceLines();

    void setMinInvoiceLines(List<InvoiceLine> invoiceLines);
}