package org.meveo.service.catalog.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
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
    
    public List<ConvertedPricePlanVersion> getListConvertedPricePlanVersionByPpmvId(Long ppmvId) {
        try {
            QueryBuilder qb = new QueryBuilder(ConvertedPricePlanVersion.class, "b", null);
            qb.addCriterion("b.pricePlanMatrixVersion.id", "=", ppmvId, true);
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }
        return null;
    }
    
    public List<Map<String, Object>> getPPVWithCPPVByPpmvId(List<Long> ppmvIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ppmvIds);
        String query =
                "SELECT v.id as ppvId, v.label as ppvLabel, v.pricePlanMatrix.id as ppmId, v.price as ppvPrice, c.id as ppvcId," +
                " c.convertedPrice as ppvCPrice, c.rate as rate, a.currencyCode as cCurrencyCode, v.isMatrix as ppvIsMatrix, c.useForBillingAccounts as useForBA " +
                " FROM PricePlanMatrixVersion v " +
                " LEFT JOIN ConvertedPricePlanVersion c on c.pricePlanMatrixVersion.id = v.id " +
                " LEFT JOIN TradingCurrency t on t.id = c.tradingCurrency.id " +        
                " LEFT JOIN Currency a on t.currency.id = a.id " +
                " WHERE v.id in (:ids)";
        return getSelectQueryAsMap(query, params);
    }
}
