package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.service.base.PersistenceService;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * Offer Component type service implementation.
 */

@Stateless
public class OfferComponentService extends
		PersistenceService<OfferComponent> {


	public OfferComponent findByCode(String offerCode, String productCode) {
		try {
			return (OfferComponent) this.getEntityManager()
										.createNamedQuery("OfferComponent.findByOfferTEmplateAndProduct")
											.setParameter("offerCode", offerCode)
											.setParameter("productCode", productCode)
												.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
}