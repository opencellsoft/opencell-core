package org.meveo.service.cpq;

import java.util.List;

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


	@SuppressWarnings("unchecked")
	public List<OfferComponent> findByCode(String offerCode, String productCode) {
		try {
			return (List<OfferComponent>) this.getEntityManager()
										.createNamedQuery("OfferComponent.findByOfferTEmplateAndProduct")
											.setParameter("offerCode", offerCode)
											.setParameter("productCode", productCode)
												.getResultList();
		}catch(NoResultException e) {
			return null;
		}
	}
}