package org.meveo.api.dto.response.payment;

import org.meveo.api.dto.payment.PaymentGatewayRumSequenceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class PaymentGatewayRumSequenceResponseDto extends BaseResponse {

	private static final long serialVersionUID = -4277560466649039599L;

	private PaymentGatewayRumSequenceDto paymentGatewayRumSequence;

	public PaymentGatewayRumSequenceDto getPaymentGatewayRumSequence() {
		return paymentGatewayRumSequence;
	}

	public void setPaymentGatewayRumSequence(PaymentGatewayRumSequenceDto paymentGatewayRumSequence) {
		this.paymentGatewayRumSequence = paymentGatewayRumSequence;
	}
}
