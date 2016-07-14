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
		String sqlQuery = "SELECT COUNT(p.id) FROM " + ProductTemplate.class.getName() + " p WHERE p.disabled = "
				+ status;
		Query query = getEntityManager().createQuery(sqlQuery);
		result = query.getFirstResult();
		return result;
	}

	public long productTemplateAlmostExpiredCount() {
		long result = 0;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7);
		String sqlQuery = "SELECT COUNT(p.id) FROM " + ProductTemplate.class.getName() + " p WHERE p.validTo <= '"
				+ c.getTime().toString()+"'";
		Query query = getEntityManager().createQuery(sqlQuery);
		result = query.getFirstResult();
		return result;
	}
}
