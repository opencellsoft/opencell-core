package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.ConvertedPricePlanVersion;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.base.PersistenceService;

/**
 * Persistence service for entity ConvertedPricePlanVersion
 * 
 * @author anas
 *
 */
@Stateless
public class ConvertedPricePlanVersionService extends PersistenceService<ConvertedPricePlanVersion> {

	public ConvertedPricePlanVersion findByPricePlanVersionAndCurrency(PricePlanMatrixVersion ppmv, TradingCurrency tradingCurrency) {
		Query query = getEntityManager().createNamedQuery("ConvertedPricePlanVersion.getByPricePlanVersionAndCurrency");
		query.setParameter("ppmv", ppmv);
		query.setParameter("tradingCurrency", tradingCurrency);
		
		try {
			return (ConvertedPricePlanVersion) query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
            log.debug("ConvertedPricePlanVersion found for ppmv: {}, tradingCurrency {}.", ppmv.getId(), tradingCurrency.getId());
            return null;
        }
	}
}
