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

package org.meveo.service.quote;

import java.util.Date;
import java.util.List;

import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.Subscription;

public class QuoteInvoiceInfo {

    private String quoteCode;
    private List<String> cdrs;
    private Subscription subscription;
    private List<ProductInstance> productInstances;
    private Date fromDate;
    private Date toDate;

    public QuoteInvoiceInfo(String quoteCode, List<String> cdrs, Subscription subscription, List<ProductInstance> productInstances, Date fromDate, Date toDate) {
        super();
        this.quoteCode = quoteCode;
        this.cdrs = cdrs;
        this.subscription = subscription;
        this.productInstances = productInstances;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getQuoteCode() {
        return quoteCode;
    }

    public List<String> getCdrs() {
        return cdrs;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public List<ProductInstance> getProductInstances() {
        return productInstances;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        return String.format("QuoteInvoiceInfo [quoteCode=%s, fromDate=%s, toDate=%s]", quoteCode, fromDate, toDate);
    }
}