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

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.WalletInstance;
import org.meveo.service.base.PersistenceService;

@Stateless
public class InvoiceAgregateService extends PersistenceService<InvoiceAgregate> {

	public BigDecimal findTotalAmountByWalletSubCat(WalletInstance wallet, InvoiceSubCategory invoiceSubCategory,
			Invoice invoice) {
		QueryBuilder qb = new QueryBuilder("select sum(amountWithoutTax) from "
				+ SubCategoryInvoiceAgregate.class.getSimpleName());
		
		qb.addCriterionEntity("invoiceSubCategory", invoiceSubCategory);
		qb.addCriterionEntity("wallet", wallet);
		qb.addCriterionEntity("invoice", invoice);
		qb.addBooleanCriterion("discountAggregate", false);
		try {
			BigDecimal result = (BigDecimal) qb.getQuery(getEntityManager()).getSingleResult();
			return result;
		} catch (NoResultException e) {
			return BigDecimal.ZERO;
		}

	}

	public Object[] findTotalAmountsForDiscountAggregates(Invoice invoice) {
		QueryBuilder qb = new QueryBuilder("select sum(amountWithoutTax),sum(amountTax),sum(amountWithTax) from "
				+ SubCategoryInvoiceAgregate.class.getSimpleName());
		qb.addBooleanCriterion("discountAggregate", true);
		try {
			Object[] result = (Object[]) qb.getQuery(getEntityManager()).getSingleResult();
			return result;
		} catch (NoResultException e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<SubCategoryInvoiceAgregate> findDiscountAggregates(Invoice invoice) {
		QueryBuilder qb = new QueryBuilder(SubCategoryInvoiceAgregate.class, "s");
		qb.addBooleanCriterion("s.discountAggregate", true);
		qb.addCriterionEntity("s.invoice", invoice);
		List<SubCategoryInvoiceAgregate> result = (List<SubCategoryInvoiceAgregate>) qb.getQuery(getEntityManager())
				.getResultList();
		return result;

	}

	@SuppressWarnings({ "unchecked" })
	public List<? extends InvoiceAgregate> listByInvoiceAndType(Invoice invoice, String type) {
		QueryBuilder qb = new QueryBuilder("from " + InvoiceAgregate.class.getSimpleName()
				+ " i WHERE i.invoice=:invoice AND i.class=:clazz");

		Query query = qb.getQuery(getEntityManager());
		query.setParameter("invoice", invoice);
		query.setParameter("clazz", type);

		try {
			return (List<InvoiceAgregate>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
