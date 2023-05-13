package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.TradingDiscountPlanItem;
import org.meveo.service.base.PersistenceService;

/**
 * Persistence service for entity TradingDiscountPlanItem
 */
@Stateless
public class TradingDiscountPlanItemService extends PersistenceService<TradingDiscountPlanItem> {

	public TradingDiscountPlanItem findByDiscountPlanItemAndCurrency(DiscountPlanItem discountPlanItem, TradingCurrency tradingCurrency) {
		Query query = getEntityManager().createNamedQuery("TradingDiscountPlanItem.getByDiscountPlanItemAndCurrency");
		query.setParameter("discountPlanItem", discountPlanItem);
		query.setParameter("tradingCurrency", tradingCurrency);
		
		try {
			return (TradingDiscountPlanItem) query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
	}
    
}
