package org.meveo.api.dto.payment;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.model.payments.PaymentGatewayRumSequence;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class PaymentGatewayRumSequenceDto extends BusinessEntityDto {

	private static final long serialVersionUID = 2351834404897787123L;

	private GenericSequenceDto genericSequence;

	private String paymentGateway;

	public PaymentGatewayRumSequenceDto() {

	}

	public PaymentGatewayRumSequenceDto(PaymentGatewayRumSequence rumSequence) {
		paymentGateway = rumSequence.getPaymentGateway().getCode();
		genericSequence = new GenericSequenceDto(rumSequence.getGenericSequence());
	}

	public GenericSequenceDto getGenericSequence() {
		return genericSequence;
	}

	public void setGenericSequence(GenericSequenceDto genericSequence) {
		this.genericSequence = genericSequence;
	}

	public String getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

}
