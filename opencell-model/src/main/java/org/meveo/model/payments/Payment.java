/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.payments;

import org.meveo.model.dunning.DunningDocument;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue(value = "P")
public class Payment extends AccountOperation {

    private static final long serialVersionUID = 1L;

    /**
     * Payment method
     */
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    /**
     * Additional payment information - // IBAN for direct debit
     */
    @Column(name = "payment_info", length = 255)
    @Size(max = 255)
    private String paymentInfo;

    /**
     * Additional payment information - bank code
     */
    @Column(name = "payment_info1", length = 255)
    @Size(max = 255)
    private String paymentInfo1;

    /**
     * Additional payment information - Code box/code guichet
     */
    @Column(name = "payment_info2", length = 255)
    @Size(max = 255)
    private String paymentInfo2;

    /**
     * Additional payment information - Account number
     */
    @Column(name = "payment_info3", length = 255)
    @Size(max = 255)
    private String paymentInfo3;

    /**
     * Additional payment information - RIB
     */
    @Column(name = "payment_info4", length = 255)
    @Size(max = 255)
    private String paymentInfo4;

    /**
     * Additional payment information - Bank name
     */
    @Column(name = "payment_info5", length = 255)
    @Size(max = 255)
    private String paymentInfo5;

    /**
     * Additional payment information - BIC
     */
    @Column(name = "payment_info6", length = 255)
    @Size(max = 255)
    private String paymentInfo6;

    /**
     * Bank LOT number
     */
    @Column(name = "bank_lot", columnDefinition = "text")
    private String bankLot;

    /**
     * Bank reference
     */
    @Column(name = "bank_reference", length = 255)
    @Size(max = 255)
    private String bankReference;

    /**
     * Deposit timestamp
     */
    @Column(name = "deposit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date depositDate;

    /**
     * Bank collection timestamp
     */
    @Column(name = "bank_collection_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankCollectionDate;

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
     * get paymentMethod
     * @return paymentMethod
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * set paymentMethod
     * @param paymentMethod paymentMethod
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * get payment info
     * @return
     */
    public String getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * set payement info
     * @param paymentInfo
     */
    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getPaymentInfo1() {
        return paymentInfo1;
    }

    public void setPaymentInfo1(String paymentInfo1) {
        this.paymentInfo1 = paymentInfo1;
    }

    public String getPaymentInfo2() {
        return paymentInfo2;
    }

    public void setPaymentInfo2(String paymentInfo2) {
        this.paymentInfo2 = paymentInfo2;
    }

    public String getPaymentInfo3() {
        return paymentInfo3;
    }

    public void setPaymentInfo3(String paymentInfo3) {
        this.paymentInfo3 = paymentInfo3;
    }

    public String getPaymentInfo4() {
        return paymentInfo4;
    }

    public void setPaymentInfo4(String paymentInfo4) {
        this.paymentInfo4 = paymentInfo4;
    }

    public String getPaymentInfo5() {
        return paymentInfo5;
    }

    public void setPaymentInfo5(String paymentInfo5) {
        this.paymentInfo5 = paymentInfo5;
    }

    public String getPaymentInfo6() {
        return paymentInfo6;
    }

    public void setPaymentInfo6(String paymentInfo6) {
        this.paymentInfo6 = paymentInfo6;
    }

    public String getBankLot() {
        return bankLot;
    }

    public void setBankLot(String bankLot) {
        this.bankLot = bankLot;
    }

    public String getBankReference() {
        return bankReference;
    }

    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    public Date getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    public Date getBankCollectionDate() {
        return bankCollectionDate;
    }

    public void setBankCollectionDate(Date bankCollectionDate) {
        this.bankCollectionDate = bankCollectionDate;
    }

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
