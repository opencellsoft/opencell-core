package org.meveo.service.dwh;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Customer;
import org.meveo.service.base.BaseService;
import org.meveo.service.crm.impl.CustomerService;

/**
 * General Data Protection Regulation (GDPR) service provides a feature that
 * anonymized the stored data.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class GdprService extends BaseService {

	@Inject
	private CustomerService customerService;

	public void anonymize(Customer entity) throws BusinessException {
		String randomCode = UUID.randomUUID().toString();
		customerService.deleteGPDR(entity, randomCode);
		customerService.update(entity);
	}

}
