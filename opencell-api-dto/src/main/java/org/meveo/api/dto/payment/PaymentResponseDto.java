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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.payments.PaymentStatusEnum;

/**
 * The Class PaymentResponseDto.
 *
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "PaymentResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The payment ID. */
    private String paymentID;
    
    /** The transaction id. */
    private String transactionId;
    
    /** The payment status. */
    private PaymentStatusEnum paymentStatus;
    
    /** The token id. */
    private String tokenId;
    
    /** The error code. */
    private String errorCode;
    
    /** The error message. */
    private String errorMessage;
    
    /** The is new token. */
    private boolean isNewToken;
    
    /** The is ao created. */
    private boolean isAoCreated = false;
    
    /** The is matching created. */
    private boolean isMatchingCreated = false;
    
    /** The code client side. */
    private String codeClientSide;
    
    /** The payment brand. */
    private String paymentBrand;
    
    /** The bank refenrence. */
    private String bankRefenrence;
    
    /** Tha payment/refund AO id. */
   	private Long accountOperationId;

    /**
     * Instantiates a new payment response dto.
     */
    public PaymentResponseDto() {
    }

    /**
     * Gets the transaction id.
     *
     * @return the transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction id.
     *
     * @param transactionId the new transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the payment status.
     *
     * @return the payment status
     */
    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the payment status.
     *
     * @param paymentStatus the new payment status
     */
    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Checks if is new token.
     *
     * @return true, if is new token
     */
    public boolean isNewToken() {
        return isNewToken;
    }

    /**
     * Sets the new token.
     *
     * @param isNewToken the new new token
     */
    public void setNewToken(boolean isNewToken) {
        this.isNewToken = isNewToken;
    }

    /**
     * Gets the token id.
     *
     * @return the token id
     */
    public String getTokenId() {
        return tokenId;
    }

    /**
     * Sets the token id.
     *
     * @param tokenId the new token id
     */
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * Gets the payment ID.
     *
     * @return the payment ID
     */
    public String getPaymentID() {
        return paymentID;
    }

    /**
     * Sets the payment ID.
     *
     * @param paymentID the new payment ID
     */
    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    /**
     * Checks if is ao created.
     *
     * @return true, if is ao created
     */
    public boolean isAoCreated() {
        return isAoCreated;
    }

    /**
     * Sets the ao created.
     *
     * @param isAoCreated the new ao created
     */
    public void setAoCreated(boolean isAoCreated) {
        this.isAoCreated = isAoCreated;
    }

    /**
     * Checks if is matching created.
     *
     * @return true, if is matching created
     */
    public boolean isMatchingCreated() {
        return isMatchingCreated;
    }

    /**
     * Sets the matching created.
     *
     * @param isMatchingCreated the new matching created
     */
    public void setMatchingCreated(boolean isMatchingCreated) {
        this.isMatchingCreated = isMatchingCreated;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the code client side.
     *
     * @return the code client side
     */
    public String getCodeClientSide() {
        return codeClientSide;
    }

    /**
     * Sets the code client side.
     *
     * @param codeClientSide the new code client side
     */
    public void setCodeClientSide(String codeClientSide) {
        this.codeClientSide = codeClientSide;
    }

    /**
     * Gets the payment brand.
     *
     * @return the payment brand
     */
    public String getPaymentBrand() {
        return paymentBrand;
    }

    /**
     * Sets the payment brand.
     *
     * @param paymentBrand the new payment brand
     */
    public void setPaymentBrand(String paymentBrand) {
        this.paymentBrand = paymentBrand;
    }

    /**
     * Gets the bank refenrence.
     *
     * @return the bank refenrence
     */
    public String getBankRefenrence() {
        return bankRefenrence;
    }

    /**
     * Gets the error code.
     *
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Sets the bank refenrence.
     *
     * @param bankRefenrence the new bank refenrence
     */
    public void setBankRefenrence(String bankRefenrence) {
        this.bankRefenrence = bankRefenrence;
    }
    
    

    public Long getAccountOperationId() {
		return accountOperationId;
	}

	public void setAccountOperationId(Long accountOperationId) {
		this.accountOperationId = accountOperationId;
	}

	@Override
    public String toString() {
        return "PayByCardResponseDto [paymentID=" + paymentID + ", transactionId=" + transactionId + ", paymentStatus=" + paymentStatus + ", tokenId=" + tokenId + ", errorCode="
                + errorCode + ", errorMessage=" + errorMessage + ", isNewToken=" + isNewToken + ", isAoCreated=" + isAoCreated + ", isMatchingCreated=" + isMatchingCreated
                + ", codeClientSide=" + codeClientSide + ", paymentBrand=" + paymentBrand + ", bankRefenrence=" + bankRefenrence + ", accountOperationId="+accountOperationId+"]";
    }

}
