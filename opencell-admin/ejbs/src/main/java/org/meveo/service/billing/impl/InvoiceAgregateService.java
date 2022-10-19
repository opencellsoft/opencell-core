/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.WalletInstance;
import org.meveo.service.base.PersistenceService;

@Stateless
public class InvoiceAgregateService extends PersistenceService<InvoiceAgregate> {

	public BigDecimal findTotalAmountByWalletSubCat(WalletInstance wallet, InvoiceSubCategory invoiceSubCategory, Invoice invoice) {
		QueryBuilder qb = new QueryBuilder("select sum(amountWithoutTax) from " + SubCategoryInvoiceAgregate.class.getSimpleName());

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
		QueryBuilder qb = new QueryBuilder("select sum(amountWithoutTax),sum(amountTax),sum(amountWithTax) from " + SubCategoryInvoiceAgregate.class.getSimpleName());
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
		List<SubCategoryInvoiceAgregate> result = (List<SubCategoryInvoiceAgregate>) qb.getQuery(getEntityManager()).getResultList();
		return result;

	}

	@SuppressWarnings({ "unchecked" })
	public List<? extends InvoiceAgregate> listByInvoiceAndType(Invoice invoice, String type) {
		QueryBuilder qb = new QueryBuilder("from " + InvoiceAgregate.class.getSimpleName() + " i WHERE i.invoice=:invoice AND i.class=:clazz");

		Query query = qb.getQuery(getEntityManager());
		query.setParameter("invoice", invoice);
		query.setParameter("clazz", type);

		try {
			return (List<InvoiceAgregate>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Sets invoice to null of the given list of InvoiceAggregate id.
	 *
	 * @param ids list of InvoiceAggregate ids
	 */
	public void setInvoiceToNull(List<Long> ids) {
		String stringQuery = "UPDATE InvoiceAgregate SET invoice=null WHERE id IN (:ids)";

		Query query = getEntityManager().createQuery(stringQuery);
		query.setParameter("ids", ids);
		query.executeUpdate();
	}

	/**
	 * Sets invoice to null of the InvoiceAggregate with the given id.
	 *
	 * @param id InvoiceAggregate ids
	 */
	public void setInvoiceToNull(Long id) {
		String stringQuery = "UPDATE InvoiceAgregate SET invoice=null WHERE id=:id";

		Query query = getEntityManager().createQuery(stringQuery);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	/**
	 * Delete invoiceAgregates associated to a billing run
	 *
	 * @param billingRun Billing run
	 */
	public void deleteInvoiceAgregates(BillingRun billingRun) {
		getEntityManager().createNamedQuery("SubCategoryInvoiceAgregate.deleteByBR").setParameter("billingRunId", billingRun.getId()).executeUpdate();
		List<Long> ids = getEntityManager().createNamedQuery("InvoiceAggregate.fetchInvoiceAggregateByBR")
									.setParameter("billingRunId", billingRun.getId())
									.getResultList();
		if (!ids.isEmpty()) {
			getEntityManager().createNamedQuery("SubCategoryInvoiceAggregate.removeInvoiceAggregateReferences")
					.setParameter("categoryInvoiceAggregateIds", ids)
					.executeUpdate();
		}
		getEntityManager().createNamedQuery("InvoiceAgregate.deleteByBR").setParameter("billingRunId", billingRun.getId()).executeUpdate();
	}

	/**
	 * Retrun the total discount amounts grouped by billing account for a billing run.
	 *
	 * @param billingRun the billing run
	 * @return a map of discount amounts grouped by billing account.
	 */
	public List<Object[]> getTotalDiscountAmountByBR(BillingRun billingRun) {
		return getEntityManager().createNamedQuery("SubCategoryInvoiceAgregate.sumAmountsDiscountByBillingAccount")
				.setParameter("billingRunId", billingRun.getId()).getResultList();
	}

	/**
	 * Delete invoice's agregate.
	 *
	 * @param invoicesIds invoices Id
	 */
	public void deleteInvoiceAgregates(Collection<Long> invoicesIds) {
		getEntityManager().createNamedQuery("SubCategoryInvoiceAgregate.deleteByInvoiceIds").setParameter("invoicesIds", invoicesIds).executeUpdate();
		getEntityManager().createNamedQuery("InvoiceAgregate.deleteByInvoiceIds").setParameter("invoicesIds", invoicesIds).executeUpdate();

	}

	public void deleteInvoiceAggregates(Long invoiceId) {
		getEntityManager().createNamedQuery("InvoiceAgregate.deleteByInvoiceId").setParameter("invoiceId", invoiceId).executeUpdate();

	}
}
