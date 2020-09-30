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
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The Class PaymentDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentDto extends BaseEntityDto {

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
    
    /** The list OCC reference for matching. */
    @Deprecated
    private List<String> listOCCReferenceforMatching;
    
    /** The list AO ids for matching. */
    private List<Long> listAoIdsForMatching;
    
    /** The is to matching. */
    private boolean isToMatching;
    
    /** The payment order. */
    private String paymentOrder;
    
    /** The fees. */
    private BigDecimal fees = BigDecimal.ZERO;
    
    /** The comment. */
    private String comment;
    
    private String paymentInfo;// IBAN for direct debit

    /** The payment info 1. */
    private String paymentInfo1;// bank code

    /** The payment info 2. */
    private String paymentInfo2;// code guichet

    /** The payment info 3. */
    private String paymentInfo3;// Num compte

    /** The payment info 4. */
    private String paymentInfo4;// RIB

    /** The payment info 5. */
    private String paymentInfo5;// bankName

    /** The payment info 6. */
    private String paymentInfo6;// bic

    /** The custom fields. */
    private CustomFieldsDto customFields;
    
    /**
     * Instantiates a new payment dto.
     */
    public PaymentDto() {
    }
    
    /**
     * Instantiates a new payment dto.
     * 
     * @param payment Payment
     */
    public PaymentDto(Payment payment) {
        this.setReference(payment.getReference());
        this.setAmount(payment.getAmount());
        this.setDueDate(payment.getDueDate());
        this.setTransactionDate(payment.getTransactionDate());
        this.setDescription(payment.getDescription());
        this.setPaymentMethod(payment.getPaymentMethod());
        this.setType(payment.getType());
        this.setPaymentInfo(payment.getPaymentInfo());
        this.setPaymentInfo1(payment.getPaymentInfo1());
        this.setPaymentInfo1(payment.getPaymentInfo2());
        this.setPaymentInfo3(payment.getPaymentInfo3());
        this.setPaymentInfo4(payment.getPaymentInfo4());
        this.setPaymentInfo5(payment.getPaymentInfo5());
        this.setPaymentInfo6(payment.getPaymentInfo6());        
        this.setComment(payment.getComment());       
    }


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
     * Gets the payment order.
     *
     * @return the paymentOrder
     */
    public String getPaymentOrder() {
        return paymentOrder;
    }

    /**
     * Sets the payment order.
     *
     * @param paymentOrder the paymentOrder to set
     */
    public void setPaymentOrder(String paymentOrder) {
        this.paymentOrder = paymentOrder;
    }

    /**
     * Gets the fees.
     *
     * @return the fees
     */
    public BigDecimal getFees() {
        return fees;
    }

    /**
     * Sets the fees.
     *
     * @param fees the fees to set
     */
    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
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
	
	

    public String getPaymentInfo() {
		return paymentInfo;
	}

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

	@Override
    public String toString() {
        return "PaymentDto [type=" + type + ", description=" + description + ", paymentMethod=" + paymentMethod + ", occTemplateCode=" + occTemplateCode + ", amount=" + amount
                + ", customerAccountCode=" + customerAccountCode + ", reference=" + reference + ", bankLot=" + bankLot + ", depositDate=" + depositDate + ", bankCollectionDate="
                + bankCollectionDate + ", dueDate=" + dueDate + ", transactionDate=" + transactionDate + ", listOCCReferenceforMatching=" + listOCCReferenceforMatching
                + ", isToMatching=" + isToMatching + ", paymentOrder=" + paymentOrder + ", fees=" + fees + ", comment=" + comment + ", customFields=" + customFields + "]";
    }
}
