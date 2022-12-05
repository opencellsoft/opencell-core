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

package org.meveo.model.dwh;

import java.io.Serializable;

import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;

import jakarta.persistence.Column;

/**
 * Stores the summary of records to be deleted as specified by GDPR configuration.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
// @Entity
// @Cacheable
// @Table(name = "dwh_gdpr_erasure_summary")
// @GenericGenerator(name = "ID_GENERATOR", strategy =
// "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
// @Parameter(name = "sequence_name", value = "dwh_gdpr_erasure_summary_seq"),
// })
public class GdprErasureSummary extends BaseEntity implements Serializable, IEntity {

    /**
     * Number of Subscriptions affected
     */
    @Column(name = "subscription_count")
    private int subscriptionCount;

    /**
     * Number of Orders affected
     */
    @Column(name = "order_count")
    private int orderCount;

    /**
     * Number of Invoices affected
     */
    @Column(name = "invoice_count")
    private int invoiceCount;

    /**
     * Number of Account operations affected
     */
    @Column(name = "account_op_count")
    private int accountOperationCount;

    /**
     * Number of unpaid Account operations affected
     */
    @Column(name = "unpaid_account_op_count")
    private int unpaidAccountOperationCount;

    /**
     * Number of customer prospects affected
     */
    @Column(name = "customer_prospect_count")
    private int customerProspectCount;

    /**
     * Number of communications affected
     */
    @Column(name = "mailing_count")
    private int mailingCount;

    public int getSubscriptionCount() {
        return subscriptionCount;
    }

    public void setSubscriptionCount(int subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(int invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public int getAccountOperationCount() {
        return accountOperationCount;
    }

    public void setAccountOperationCount(int accountOperationCount) {
        this.accountOperationCount = accountOperationCount;
    }

    public int getUnpaidAccountOperationCount() {
        return unpaidAccountOperationCount;
    }

    public void setUnpaidAccountOperationCount(int unpaidAccountOperationCount) {
        this.unpaidAccountOperationCount = unpaidAccountOperationCount;
    }

    public int getCustomerProspectCount() {
        return customerProspectCount;
    }

    public void setCustomerProspectCount(int customerProspectCount) {
        this.customerProspectCount = customerProspectCount;
    }

    public int getMailingCount() {
        return mailingCount;
    }

    public void setMailingCount(int mailingCount) {
        this.mailingCount = mailingCount;
    }

}
