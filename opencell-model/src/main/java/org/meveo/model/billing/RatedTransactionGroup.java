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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.meveo.model.admin.Seller;
import org.meveo.model.payments.PaymentMethod;

/**
 * Rated transactions split for invoicing based on Billing account, seller and invoice type
 * 
 * @author Andrius Karpavicius
 */
@SuppressWarnings("serial")
public class RatedTransactionGroup implements Serializable {

    /**
     * Billing account
     */
    private BillingAccount billingAccount;
    /**
     * Billing cycle
     */
    private BillingCycle billingCycle;

    /**
     * Seller
     */
    private Seller seller;

    /**
     * Invoice type
     */
    private InvoiceType invoiceType;

    /**
     * Are these prepaid transactions
     */
    private boolean prepaid;

    /**
     * A unique invoice identifier while processing aggregates. In a form of billingAccount.id_seller.id_invoiceType.id_isPrepaid
     */
    private String invoiceKey;

    /**
     * Which payment method used
     */
    private PaymentMethod paymentMethod;

    /**
     * Rated transactions
     */
    private List<RatedTransaction> ratedTransactions = new ArrayList<RatedTransaction>();

    public RatedTransactionGroup() {

    }

    /**
     * Constructor
     * 
     * @param billingAccount Billing account
     * @param seller Seller
     * @param billingCycle Billing cycle
     * @param invoiceType Invoice type
     * @param prepaid Is this for prepaid transactions
     */
    public RatedTransactionGroup(BillingAccount billingAccount, Seller seller, BillingCycle billingCycle, InvoiceType invoiceType, boolean prepaid) {
        this.billingAccount = billingAccount;
        this.seller = seller;
        this.billingCycle = billingCycle;
        this.invoiceType = invoiceType;
        this.prepaid = prepaid;
    }

    /**
     * Constructor.
     *
     * @param billingAccount Billing account
     * @param seller         Seller
     * @param billingCycle   Billing cycle
     * @param invoiceType    Invoice type
     * @param prepaid        Is this for prepaid transactions
     * @param invoiceKey     invoice key
     * @param paymentMethod  Payment method
     */
    public RatedTransactionGroup(BillingAccount billingAccount, Seller seller, BillingCycle billingCycle, InvoiceType invoiceType, boolean prepaid, String invoiceKey,
            PaymentMethod paymentMethod) {

        this(billingAccount, seller, billingCycle, invoiceType, prepaid);
        this.invoiceKey = invoiceKey;
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return Billing account
     */
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    /**
     * @param billingAccount Billing account
     */
    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    /**
     * @return Seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Seller
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * @return Rated transactions
     */
    public List<RatedTransaction> getRatedTransactions() {
        return ratedTransactions;
    }

    /**
     * @param ratedTransactions Rated transactions
     */
    public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    /**
     * @return Billing cycle
     */
    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    /**
     * @param billingCycle Billing cycle
     */
    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * @return Invoice type
     */
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    /**
     * @param invoiceType Invoice type
     */
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * @return Are these prepaid transactions
     */
    public boolean isPrepaid() {
        return prepaid;
    }

    /**
     * @return A unique invoice identifier while processing aggregates. In a form of billingAccount.id_seller.id_invoiceType.id_isPrepaid
     */
    public String getInvoiceKey() {

        if (invoiceKey == null) {
            invoiceKey = billingAccount.getId() + "_" + seller.getId() + "_" + invoiceType.getId() + "_" + prepaid + "_" + getPaymentMethod().getId();
        }
        return invoiceKey;
    }

    public void setInvoiceKey(String invoiceKey) {
        this.invoiceKey = invoiceKey;
    }

    public PaymentMethod getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        }
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}