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
package org.meveo.model.payments;

import org.meveo.model.dunning.DunningDocument;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@DiscriminatorValue(value = "P")
public class Payment extends AccountOperation {

    private static final long serialVersionUID = 1L;

    /**
     * if a payment is done as part of a dunning process
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_document_id")
    private DunningDocument dunningDocument;

    /**
     * Number assigned by the Operator bank
     */
    @Column(name = "payment_order")
    private String paymentOrder;

    /**
     * Amount of financial expenses exluded in the amount
     */
    @Column(name = "payment_fees")
    private BigDecimal fees = BigDecimal.ZERO;

    /**
     * Comments Text free if litigation or special conditions
     */
    @Column(name = "comment", columnDefinition = "LONGTEXT")
    private String comment;

    /**
     * get the  associated dunning doc if exists
     * @return dunningDocument
     */
    public DunningDocument getDunningDocument() {
        return dunningDocument;
    }

    /**
     * set the dunning doc of the payment
     * @param dunningDocument
     */
    public void setDunningDocument(DunningDocument dunningDocument) {
        this.dunningDocument = dunningDocument;
    }

    /**
     * @return the paymentOrder
     */
    public String getPaymentOrder() {
        return paymentOrder;
    }

    /**
     * @param paymentOrder the paymentOrder to set
     */
    public void setPaymentOrder(String paymentOrder) {
        this.paymentOrder = paymentOrder;
    }

    /**
     * @return the fees
     */
    public BigDecimal getFees() {
        return fees;
    }

    /**
     * @param fees the fees to set
     */
    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

}
