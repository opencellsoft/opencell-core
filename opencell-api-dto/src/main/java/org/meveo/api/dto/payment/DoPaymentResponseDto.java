package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CardTokenResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DoPaymentResponseDto extends BaseResponse{

	private static final long serialVersionUID = 1L;
	private String paymentID;
	private String transactionId;
	private String paymentStatus;
	private String tokenId;
	private boolean isNewToken;
	private boolean isAoCreated = false;
	private boolean isMatchingCreated = false;
	
	public DoPaymentResponseDto(){}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
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

	@Override
	public String toString() {
		return "DoPaymentResponseDto [paymentID=" + paymentID + ", transactionId=" + transactionId + ", paymentStatus="
				+ paymentStatus + ", tokenId=" + tokenId + ", isNewToken=" + isNewToken + ", isAoCreated=" + isAoCreated
				+ ", isMatchingCreated=" + isMatchingCreated + "]";
	}	
}
