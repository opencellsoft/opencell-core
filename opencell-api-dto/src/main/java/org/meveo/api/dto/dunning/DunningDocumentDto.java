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

package org.meveo.api.dto.dunning;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentDto;

/**
 * DTO equivalent of DunningDocument entity.
 * 
 * @author abdelmounaim akadid
 */
@XmlRootElement(name = "DunningDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningDocumentDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1897384994086770497L;

    /**
     * DunningDocument Id
     */
    protected String dunningDocumentId;

    /**
     * Customer account Code
     */
    protected String customerAccountCode;

    /**
     * Subscription Code
     */
    protected String subscriptionCode;

    /** List of payments. */
    @XmlElementWrapper
    @XmlElement(name = "payment", required = true)
    protected List<PaymentDto> payments = new ArrayList<PaymentDto>();

    /** The tax aggregates */
    @XmlElementWrapper
    @XmlElement(name = "dueInvoice", required = true)
    protected List<InvoiceDto> dueInvoices = new ArrayList<InvoiceDto>();

    /**
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * @param customerAccountCode the customerAccountCode to set
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * @return the subscriptionCode
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * @param subscriptionCode the subscriptionCode to set
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * @return the payments
     */
    public List<PaymentDto> getPayments() {
        return payments;
    }

    /**
     * @param payments the payments to set
     */
    public void setPayments(List<PaymentDto> payments) {
        this.payments = payments;
    }

    /**
     * @return the dueInvoices
     */
    public List<InvoiceDto> getDueInvoices() {
        return dueInvoices;
    }

    /**
     * @param dueInvoices the dueInvoices to set
     */
    public void setDueInvoices(List<InvoiceDto> dueInvoices) {
        this.dueInvoices = dueInvoices;
    }

    /**
     * @return the dunningDocumentId
     */
    public String getDunningDocumentId() {
        return dunningDocumentId;
    }

    /**
     * @param dunningDocumentId the dunningDocumentId to set
     */
    public void setDunningDocumentId(String dunningDocumentId) {
        this.dunningDocumentId = dunningDocumentId;
    }

}