package org.meveo.service.catalog.impl;

import java.util.Set;

import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.model.catalog.TradingPricePlanMatrixLine;
import org.meveo.service.base.PersistenceService;

@Stateless
public class TradingPricePlanMatrixLineService extends PersistenceService<TradingPricePlanMatrixLine> {

	public void disableOrEnableAllTradingPricePlanMatrixLine(Set<Long> ids, boolean enable) {
		if(CollectionUtils.isNotEmpty(ids)) {
			this.getEntityManager().createNamedQuery("TradingPricePlanMatrixLine.enableOrDisable")
									.setParameter("enable", enable)
									.setParameter("ids", ids).executeUpdate();
		}
	}

}
