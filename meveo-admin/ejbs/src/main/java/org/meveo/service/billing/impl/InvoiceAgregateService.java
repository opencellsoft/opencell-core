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

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
public class InvoiceAgregateService extends PersistenceService<InvoiceAgregate> {

	
	public BigDecimal findTotalAmountByWalletSubCat(WalletInstance wallet,
			InvoiceSubCategory invoiceSubCategory, Provider provider,Invoice invoice) {
		QueryBuilder qb = new QueryBuilder("select sum(amountWithoutTax) from "
				+ SubCategoryInvoiceAgregate.class.getSimpleName());
		qb.addCriterionEntity("provider", provider);
		qb.addCriterionEntity("invoiceSubCategory", invoiceSubCategory);
		qb.addCriterionEntity("wallet", wallet);
		qb.addCriterionEntity("invoice", invoice);
		qb.addBooleanCriterion("discountAggregate", false);
		try {
			BigDecimal result = (BigDecimal) qb.getQuery(getEntityManager())
					.getSingleResult();
			return result;
		} catch (NoResultException e) {
			return BigDecimal.ZERO;
		}

	}
	
	
	public Object[] findTotalAmountsForDiscountAggregates(Invoice invoice) {
		QueryBuilder qb = new QueryBuilder("select sum(amountWithoutTax),sum(amountTax),sum(amountWithTax) from "
				+ SubCategoryInvoiceAgregate.class.getSimpleName());
		qb.addCriterionEntity("provider", invoice.getProvider());
		qb.addBooleanCriterion("discountAggregate", true);
		qb.addCriterionEntity("invoice", invoice);
		try {
			Object[] result = (Object[]) qb.getQuery(getEntityManager())
					.getSingleResult();
			return result;
		} catch (NoResultException e) {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<SubCategoryInvoiceAgregate> findDiscountAggregates(Invoice invoice) {
		QueryBuilder qb = new QueryBuilder("from "
				+ SubCategoryInvoiceAgregate.class.getSimpleName());
		qb.addCriterionEntity("provider", invoice.getProvider());
		qb.addBooleanCriterion("discountAggregate", true);
		qb.addCriterionEntity("invoice", invoice);
		List<SubCategoryInvoiceAgregate> result = (List<SubCategoryInvoiceAgregate>) qb.getQuery(getEntityManager())
					.getResultList();
		return result;

	}


}
