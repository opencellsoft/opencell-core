package org.meveo.service.payments.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.payments.PaymentGatewayRumSequence;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceSingleton;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@Stateless
public class PaymentGatewayRumSequenceService extends BusinessService<PaymentGatewayRumSequence> {

	@Inject
	private ServiceSingleton serviceSingleton;
	
	public PaymentGatewayRumSequence getNextNumber(PaymentGatewayRumSequence rumSequence) {
		return serviceSingleton.getPaymentGatewayRumSequenceNumber(rumSequence);		
	}

}
