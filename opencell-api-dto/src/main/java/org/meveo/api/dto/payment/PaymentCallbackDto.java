package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.PaymentStatusEnum;


/**
 * The Class PaymentCallbackDto.
 */
@XmlRootElement(name = "PaymentCallbackDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentCallbackDto {
	
	/** The payment reference. */
	private String paymentReference;
	
	/** The payment status. */
	private PaymentStatusEnum paymentStatus;
	
	/** The error code. */
	private String errorCode;
	
	/** The error message. */
	private String errorMessage;
	
	/**
	 * Instantiates a new payment callback dto.
	 */
	public PaymentCallbackDto() {
	}

	/**
	 * Gets the payment reference.
	 *
	 * @return the paymentReference
	 */
	public String getPaymentReference() {
		return paymentReference;
	}

	/**
	 * Sets the payment reference.
	 *
	 * @param paymentReference the new paymentReference
	 */
	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
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
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 *
	 * @param errorCode the new error code
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
	
	
}
