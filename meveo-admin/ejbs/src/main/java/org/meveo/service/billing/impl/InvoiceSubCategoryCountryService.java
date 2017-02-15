/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.TradingCountry;
import org.meveo.service.base.PersistenceService;

@Stateless
public class InvoiceSubCategoryCountryService extends
		PersistenceService<InvoiceSubcategoryCountry> {

    @SuppressWarnings("unchecked")
    public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(String invoiceSubCategoryCode, Long countryId) {

        try {
            QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "i");
            qb.addCriterion("invoiceSubCategory.code", "=", invoiceSubCategoryCode, true);
            qb.addCriterion("tradingCountry.id", "=", countryId, true);
            

            List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = qb.getQuery(getEntityManager()).getResultList();
            return invoiceSubcategoryCountries.size() > 0 ? invoiceSubcategoryCountries.get(0) : null;
        } catch (NoResultException ex) {
            log.warn("failed to find invoice SubCategory Country", ex);
        }

        return null;
    }
    
    @SuppressWarnings("unchecked")
    public InvoiceSubcategoryCountry findInvoiceSubCategoryCountry(Long invoiceSubCategoryId, Long countryId) {

        try {
            QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class, "i");
            qb.addCriterion("invoiceSubCategory.id", "=", invoiceSubCategoryId, true);
            qb.addCriterion("tradingCountry.id", "=", countryId, true);
            

            List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = qb.getQuery(getEntityManager()).getResultList();
            return invoiceSubcategoryCountries.size() > 0 ? invoiceSubcategoryCountries.get(0) : null;
        } catch (NoResultException ex) {
            log.warn("failed to find invoice SubCategory Country", ex);
        }

        return null;
    }

	public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(
			InvoiceSubCategory invoiceSubCategory,
			TradingCountry tradingCountry) {
		return findByInvoiceSubCategoryAndCountry(invoiceSubCategory,
				tradingCountry, null);
	}

	public InvoiceSubcategoryCountry findByInvoiceSubCategoryAndCountry(
			InvoiceSubCategory invoiceSubCategory,
			TradingCountry tradingCountry, List<String> fetchFields) {
		QueryBuilder qb = new QueryBuilder(InvoiceSubcategoryCountry.class,
				"ic", fetchFields);
		qb.addCriterionEntity("ic.tradingCountry", tradingCountry);
		qb.addCriterionEntity("ic.invoiceSubCategory", invoiceSubCategory);

		try {
			return (InvoiceSubcategoryCountry) qb.getQuery(getEntityManager())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}