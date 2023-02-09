package org.meveo.service.payments.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.dunning.CustomerBalance;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage CustomerBalance entity.
 * It extends {@link PersistenceService} class
 * 
 * @author zelmeliani
 * @version 15.0.0
 *
 */
@Stateless
public class CustomerBalanceService extends BusinessService<CustomerBalance> {

	/**
	 * Get the default CustomerBalance
	 * @return
	 */
	public CustomerBalance getDefaultOne() throws NoResultException, NonUniqueResultException {
		try {
			return getEntityManager().createNamedQuery("CustomerBalance.findDefaultOne", CustomerBalance.class)
			.setParameter("default", true)
			.getSingleResult();
		} catch (NoResultException e) {
	        return null;
	    } catch (NonUniqueResultException e) {
	        throw new BusinessException("there are multiple customer balance as default");
	    }
	}
	
}
