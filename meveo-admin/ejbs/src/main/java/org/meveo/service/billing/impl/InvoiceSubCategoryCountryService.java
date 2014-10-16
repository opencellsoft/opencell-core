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
package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
@LocalBean
public class InvoiceSubCategoryCountryService extends
		PersistenceService<InvoiceSubcategoryCountry> {

	public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(
			Long invoiceSubCategoryId, Long countryId) {
		return findInvoiceSubCategoryCountry(getEntityManager(),
				invoiceSubCategoryId, countryId);
	}

	@SuppressWarnings("unchecked")
	public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(
			EntityManager em, Long invoiceSubCategoryId, Long countryId) {
		try {
			QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class,
					"i");
			qb.addCriterionEntity("i.invoiceSubCategory.id",
					invoiceSubCategoryId);
			qb.addCriterionEntity("i.tradingCountry.id", countryId);

			List<InvoiceSubcategoryCountry> InvoiceSubcategoryCountries = qb
					.getQuery(em).getResultList();
			return InvoiceSubcategoryCountries.size() > 0 ? InvoiceSubcategoryCountries
					.get(0) : null;
		} catch (NoResultException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public InvoiceSubcategoryCountry findByTaxId(EntityManager em, Tax tax) {
		QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "a");
		qb.addCriterionEntity("tax", tax);

		Query query = qb.getQuery(em);
		query.setMaxResults(1);

		return (InvoiceSubcategoryCountry) query.getSingleResult();
	}
}
