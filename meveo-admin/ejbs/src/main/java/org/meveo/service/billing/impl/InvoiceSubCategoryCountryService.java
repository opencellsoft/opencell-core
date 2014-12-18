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
package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
public class InvoiceSubCategoryCountryService extends
		PersistenceService<InvoiceSubcategoryCountry> {

	public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(
			Long invoiceSubCategoryId, Long countryId) {
		return findInvoiceSubCategoryCountry(getEntityManager(),
				invoiceSubCategoryId, countryId, getCurrentProvider());
	}

	@SuppressWarnings("unchecked")
	public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(
			EntityManager em, Long invoiceSubCategoryId, Long countryId,
			Provider provider) {
		try {
			QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class,
					"i");
			qb.addCriterion("invoiceSubCategory.id", "=",
					invoiceSubCategoryId, true);
			qb.addCriterion("tradingCountry.id", "=", countryId, true);
			qb.addCriterionEntity("provider", provider);

			List<InvoiceSubcategoryCountry> invoiceSubcategoryiountries = qb
					.getQuery(em).getResultList();
			return invoiceSubcategoryiountries.size() > 0 ? invoiceSubcategoryiountries
					.get(0) : null;
		} catch (NoResultException ex) {
			log.warn(ex.getMessage());
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

	public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(
			InvoiceSubCategory invoiceSubCategory,
			TradingCountry tradingCountry, Provider provider) {
		return findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
				tradingCountry, null, provider);
	}

	public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(
			InvoiceSubCategory invoiceSubCategory,
			TradingCountry tradingCountry, List<String> fetchFields,
			Provider provider) {
		QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class,
				"ic", fetchFields, provider);
		qb.addCriterionEntity("ic.tradingCountry", tradingCountry);
		qb.addCriterionEntity("ic.invoiceSubCategory", invoiceSubCategory);
		qb.addCriterionEntity("ic.provider", provider);

		try {
			return (InvoiceSubcategoryCountry) qb.getQuery(getEntityManager())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
