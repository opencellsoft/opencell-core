package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.payments.PaymentStatusEnum;

@XmlRootElement(name = "PaymentResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentResponseDto extends BaseResponse{

	private static final long serialVersionUID = 1L;
	private String paymentID;
	private String transactionId;
	private PaymentStatusEnum paymentStatus;
	private String tokenId;
	private String errorCode;
	private String errorMessage;
	private boolean isNewToken;
	private boolean isAoCreated = false;
	private boolean isMatchingCreated = false;
	private String codeClientSide;
	private String paymentBrand;
	private String bankRefenrence;
	
	
	public PaymentResponseDto(){}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public PaymentStatusEnum getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public boolean isNewToken() {
		return isNewToken;
	}

	public void setNewToken(boolean isNewToken) {
		this.isNewToken = isNewToken;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}

	public boolean isAoCreated() {
		return isAoCreated;
	}

	public void setAoCreated(boolean isAoCreated) {
		this.isAoCreated = isAoCreated;
	}

	public boolean isMatchingCreated() {
		return isMatchingCreated;
	}

	public void setMatchingCreated(boolean isMatchingCreated) {
		this.isMatchingCreated = isMatchingCreated;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	

	public String getCodeClientSide() {
		return codeClientSide;
	}

	public void setCodeClientSide(String codeClientSide) {
		this.codeClientSide = codeClientSide;
	}

	public String getPaymentBrand() {
		return paymentBrand;
	}

	public void setPaymentBrand(String paymentBrand) {
		this.paymentBrand = paymentBrand;
	}

	public String getBankRefenrence() {
		return bankRefenrence;
	}
	
	

	/**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setBankRefenrence(String bankRefenrence) {
		this.bankRefenrence = bankRefenrence;
	}

    @Override
    public String toString() {
        return "PayByCardResponseDto [paymentID=" + paymentID + ", transactionId=" + transactionId + ", paymentStatus=" + paymentStatus + ", tokenId=" + tokenId + ", errorCode="
                + errorCode + ", errorMessage=" + errorMessage + ", isNewToken=" + isNewToken + ", isAoCreated=" + isAoCreated + ", isMatchingCreated=" + isMatchingCreated
                + ", codeClientSide=" + codeClientSide + ", paymentBrand=" + paymentBrand + ", bankRefenrence=" + bankRefenrence + "]";
    }

}
