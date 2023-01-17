package org.meveo.service.catalog.impl;

import java.util.Set;

import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.model.catalog.ConvertedPricePlanMatrixLine;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ConvertedPricePlanMatrixLineService extends PersistenceService<ConvertedPricePlanMatrixLine> {

	public void disableOrEnableAllConvertedPricePlanMatrixLine(Set<Long> ids, boolean enable) {
		if(CollectionUtils.isNotEmpty(ids)) {
			this.getEntityManager().createNamedQuery("ConvertedPricePlanMatrixLine.enableOrDisable")
									.setParameter("enable", enable)
									.setParameter("ids", ids).executeUpdate();
		}
	}

}
