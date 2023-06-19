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
package org.meveo.api.dto.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The Class RefundDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Refund")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The type. */
    private String type;
    
    /** The description. */
    private String description;
    
    /** The payment method. */
    private PaymentMethodEnum paymentMethod;
    
    /** The occ template code. */
    private String occTemplateCode;
    
    /** The amount. */
    private BigDecimal amount;
    
    /** The customer account code. */
    private String customerAccountCode;
    
    /** The reference. */
    private String reference;
    
    /** The bank lot. */
    private String bankLot;
    
    /** The deposit date. */
    private Date depositDate;
    
    /** The bank collection date. */
    private Date bankCollectionDate;
    
    /** The due date. */
    private Date dueDate;
    
    /** The transaction date. */
    private Date transactionDate;
    
    /** The list OCC referencefor matching. */
    @Deprecated
    private List<String> listOCCReferenceforMatching;
    
    /** The list of AO ids for matching. */
	private List<Long> listAoIdsForMatching;
    
    /** The is to matching. */
    private boolean isToMatching;

    /** The is to matching. */
    private boolean isManualRefund;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the occ template code.
     *
     * @return the occ template code
     */
    public String getOccTemplateCode() {
        return occTemplateCode;
    }

    /**
     * Sets the occ template code.
     *
     * @param occTemplateCode the new occ template code
     */
    public void setOccTemplateCode(String occTemplateCode) {
        this.occTemplateCode = occTemplateCode;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the customer account code.
     *
     * @return the customer account code
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * Sets the customer account code.
     *
     * @param customerAccountCode the new customer account code
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     *
     * @param reference the new reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the bank lot.
     *
     * @return the bank lot
     */
    public String getBankLot() {
        return bankLot;
    }

    /**
     * Sets the bank lot.
     *
     * @param bankLot the new bank lot
     */
    public void setBankLot(String bankLot) {
        this.bankLot = bankLot;
    }

    /**
     * Gets the deposit date.
     *
     * @return the deposit date
     */
    public Date getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the deposit date.
     *
     * @param depositDate the new deposit date
     */
    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    /**
     * Gets the bank collection date.
     *
     * @return the bank collection date
     */
    public Date getBankCollectionDate() {
        return bankCollectionDate;
    }

    /**
     * Sets the bank collection date.
     *
     * @param bankCollectionDate the new bank collection date
     */
    public void setBankCollectionDate(Date bankCollectionDate) {
        this.bankCollectionDate = bankCollectionDate;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the transaction date.
     *
     * @return the transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transaction date.
     *
     * @param transactionDate the new transaction date
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the list OCC referencefor matching.
     *
     * @return the list OCC referencefor matching
     */
    public List<String> getListOCCReferenceforMatching() {
        return listOCCReferenceforMatching;
    }

    /**
     * Sets the list OCC referencefor matching.
     *
     * @param listOCCReferenceforMatching the new list OCC referencefor matching
     */
    public void setListOCCReferenceforMatching(List<String> listOCCReferenceforMatching) {
        this.listOCCReferenceforMatching = listOCCReferenceforMatching;
    }

    /**
     * Checks if is to matching.
     *
     * @return true, if is to matching
     */
    public boolean isToMatching() {
        return isToMatching;
    }

    /**
     * Sets the to matching.
     *
     * @param isToMatching the new to matching
     */
    public void setToMatching(boolean isToMatching) {
        this.isToMatching = isToMatching;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    /**
     * Gets the list of AO ids for matching.
     *
     * @return listOCCReferenceforMatching the list of AO ids for matching.
     */
	public List<Long> getListAoIdsForMatching() {
		return listAoIdsForMatching;
	}

    /**
     * Sets the list of AO ids for matching.
     *
     * @param listOCCReferenceforMatching the new list of AO ids for matching.
     */
	public void setListAoIdsForMatching(List<Long> listAoIdsForMatching) {
		this.listAoIdsForMatching = listAoIdsForMatching;
	}

    public boolean isManualRefund() {
        return isManualRefund;
    }

    public void setManualRefund(boolean manualRefund) {
        isManualRefund = manualRefund;
    }

    @Override
    public String toString() {
        return "RefundDto [type=" + type + ", description=" + description + ", paymentMethod=" + paymentMethod + ", occTemplateCode=" + occTemplateCode + ", amount=" + amount
                + ", customerAccountCode=" + customerAccountCode + ", reference=" + reference + ", bankLot=" + bankLot + ", depositDate=" + depositDate + ", bankCollectionDate="
                + bankCollectionDate + ", dueDate=" + dueDate + ", transactionDate=" + transactionDate + ", listOCCReferenceforMatching=" + listOCCReferenceforMatching
                + ", isToMatching=" + isToMatching + ", customFields=" + customFields + "]";
    }

}
