/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

	@SuppressWarnings("unchecked")
	public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(
			Long invoiceSubCategoryId, Long countryId) {
		try {
			QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class,
					"i");
			qb.addCriterionEntity("i.invoiceSubCategory.id",
					invoiceSubCategoryId);
			qb.addCriterionEntity("i.tradingCountry.id", countryId);

			List<InvoiceSubcategoryCountry> InvoiceSubcategoryCountries = qb
					.getQuery(getEntityManager()).getResultList();
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
