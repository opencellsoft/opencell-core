package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class MediationJobBean {

	@Inject
	private EdrService edrService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createEdr(EDR edr) throws BusinessException {
		edrService.create(edr);
	}

}
