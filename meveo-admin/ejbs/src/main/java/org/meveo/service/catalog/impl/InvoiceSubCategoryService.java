/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.service.base.PersistenceService;

/**
 * InvoiceSubCategory service implementation.
 * 
 */
@Stateless
public class InvoiceSubCategoryService extends
		PersistenceService<InvoiceSubCategory> {

	public InvoiceSubCategory findByCode(EntityManager em, String code) {
		QueryBuilder qb = new QueryBuilder(InvoiceSubCategory.class, "sc");
		qb.addCriterion("code", "=", code, false);

		return (InvoiceSubCategory) qb.getQuery(em).getSingleResult();
	}

	public InvoiceSubCategory findByCode(String code) {
		QueryBuilder qb = new QueryBuilder(InvoiceSubCategory.class, "sc");
		qb.addCriterion("code", "=", code, false);

		return (InvoiceSubCategory) qb.getQuery(getEntityManager())
				.getSingleResult();
	}

}
