package org.meveo.service.catalog.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class BundleTemplateService extends BusinessService<BundleTemplate> {
	
	public long bundleTemplateActiveCount(boolean status) {
		long result = 0;

		Query query;

		if (status) {
			query = getEntityManager().createNamedQuery("BundleTemplate.countActive");
		} else {
			query = getEntityManager().createNamedQuery("BundleTemplate.countDisabled");
		}

		result = (long) query.getSingleResult();
		return result;
	}

	public long productTemplateAlmostExpiredCount() {
		long result = 0;
		String sqlQuery = "SELECT COUNT(*) FROM " + BundleTemplate.class.getName()
				+ " p WHERE DATE_PART('day',p.validTo - '"
				+ DateUtils.formatDateWithPattern(new Date(), "yyyy-MM-dd hh:mm:ss") + "') <= 7";
		Query query = getEntityManager().createQuery(sqlQuery);
		result = (long) query.getSingleResult();
		return result;
	}
}
