package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.crm.CustomerSequence;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceSingleton;

/**
 * Service for managing customer sequence entity.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class CustomerSequenceService extends BusinessService<CustomerSequence> {

	@Inject
	private ServiceSingleton serviceSingleton;
	
	public CustomerSequence getNextNumber(CustomerSequence customerSequence) {
		return serviceSingleton.getPaymentGatewayRumSequenceNumber(customerSequence);
	}

}
