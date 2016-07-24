package org.meveo.service.catalog.impl;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.BusinessService;

@Stateless
public class ProductTemplateService extends BusinessService<ProductTemplate> {

	public long productTemplateActiveCount(boolean status) {
		long result = 0;

		Query query;

		if (status) {
			query = getEntityManager().createNamedQuery("ProductTemplate.countActive");
		} else {
			query = getEntityManager().createNamedQuery("ProductTemplate.countDisabled");
		}

		result = (long) query.getSingleResult();
		return result;
	}

	public long productTemplateAlmostExpiredCount() {
		long result = 0;
		Calendar c = Calendar.getInstance();
		String sqlQuery = "SELECT COUNT(*) FROM " + ProductTemplate.class.getName()
				+ " p WHERE DATE_PART('day',p.validTo - '" + c.getTime().toString() + "') <= 7";
		Query query = getEntityManager().createQuery(sqlQuery);
		result = (long) query.getSingleResult();
		return result;
	}
}
