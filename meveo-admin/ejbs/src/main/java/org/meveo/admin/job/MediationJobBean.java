package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class MediationJobBean {

	@Inject
	private EdrService edrService;

	@Inject
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createEdr(EDR edr, User currentUser) throws BusinessException {
		edrService.create(em, edr, currentUser, currentUser.getProvider());
	}

}
