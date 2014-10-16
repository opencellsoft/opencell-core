/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.service.base.PersistenceService;

/**
 * InvoiceCategory service implementation.
 * 
 * @author Ignas Lelys
 * @created Dec 15, 2010
 * 
 */
@Stateless
@LocalBean
public class InvoiceCategoryService extends PersistenceService<InvoiceCategory> {

	public InvoiceCategory findByCode(EntityManager em, String code) {
		if (code == null) {
			return null;
		}

		QueryBuilder qb = new QueryBuilder(InvoiceCategory.class, "c");
		qb.addCriterion("code", "=", code, false);

		try {
			return (InvoiceCategory) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
