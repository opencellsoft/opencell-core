package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CardTokenResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DoPaymentResponseDto extends BaseResponse{

	private static final long serialVersionUID = 1L;
	private String transactionId;
	private String paymentStatus;
	
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

	@Override
	public String toString() {
		return "DoPaymentResponseDto [transactionId=" + transactionId + ", paymentStatus=" + paymentStatus + "]";
	}
	

	

}
