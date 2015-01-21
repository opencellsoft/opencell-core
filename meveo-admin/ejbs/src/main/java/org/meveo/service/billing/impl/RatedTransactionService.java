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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
public class RatedTransactionService extends PersistenceService<RatedTransaction> {

	@Inject
	private Logger log;

	@Inject
	private InvoiceAgregateService invoiceAgregateService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private CustomerAccountService customerAccountService;

	public List<RatedTransaction> getRatedTransactionsInvoiced(UserAccount userAccount) {
		if (userAccount == null || userAccount.getWallet() == null) {
			return null;
		}
		return (List<RatedTransaction>) getEntityManager()
				.createNamedQuery("RatedTransaction.listInvoiced", RatedTransaction.class)
				.setParameter("wallet", userAccount.getWallet()).getResultList();
	}

	@SuppressWarnings("unchecked")
	// FIXME: edward please use Named queries
	public ConsumptionDTO getConsumption(Subscription subscription, String infoType, Integer billingCycle,
			boolean sumarizeConsumption) throws IncorrectSusbcriptionException {

		Date lastBilledDate = null;
		ConsumptionDTO consumptionDTO = new ConsumptionDTO();

		// If billing has been run already, use last billing date plus a day as
		// filtering FROM value
		// Otherwise leave it null, so it wont be included in a query
		if (subscription.getUserAccount().getBillingAccount().getBillingRun() != null) {
			lastBilledDate = subscription.getUserAccount().getBillingAccount().getBillingRun().getEndDate();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(lastBilledDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			lastBilledDate = calendar.getTime();

		}

		if (sumarizeConsumption) {

			QueryBuilder qb = new QueryBuilder("select sum(amount1WithTax), sum(usageAmount) from "
					+ RatedTransaction.class.getSimpleName());
			qb.addCriterionEntity("subscription", subscription);
			qb.addCriterion("subUsageCode1", "=", infoType, false);
			qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
			String baseSql = qb.getSqlString();

			// Summarize invoiced transactions
			String sql = baseSql + " and status='BILLED'";

			Query query = getEntityManager().createQuery(sql);

			for (Entry<String, Object> param : qb.getParams().entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}

			Object[] results = (Object[]) query.getSingleResult();

			consumptionDTO.setAmountCharged((BigDecimal) results[0]);
			consumptionDTO.setConsumptionCharged(((Long) results[1]).intValue());

			// Summarize not invoiced transactions
			sql = baseSql + " and status<>'BILLED'";

			query = getEntityManager().createQuery(sql);

			for (Entry<String, Object> param : qb.getParams().entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}

			results = (Object[]) query.getSingleResult();

			consumptionDTO.setAmountUncharged((BigDecimal) results[0]);
			consumptionDTO.setConsumptionUncharged(((Long) results[1]).intValue());

		} else {

			QueryBuilder qb = new QueryBuilder(
					"select sum(amount1WithTax), sum(usageAmount), groupingId, case when status='BILLED' then 'true' else 'false' end from "
							+ RatedTransaction.class.getSimpleName());
			qb.addCriterionEntity("subscription", subscription);
			qb.addCriterion("subUsageCode1", "=", infoType, false);
			qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
			qb.addSql("groupingId is not null");
			String sql = qb.getSqlString()
					+ " group by groupingId, case when status='BILLED' then 'true' else 'false' end";

			Query query = getEntityManager().createQuery(sql);

			for (Entry<String, Object> param : qb.getParams().entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}

			List<Object[]> results = (List<Object[]>) query.getResultList();

			for (Object[] result : results) {

				BigDecimal amount = (BigDecimal) result[0];
				int consumption = ((Long) result[1]).intValue();
				boolean charged = Boolean.parseBoolean((String) result[3]);
				// boolean roaming =
				// RatedTransaction.translateGroupIdToRoaming(groupId);
				// boolean upload =
				// RatedTransaction.translateGroupIdToUpload(groupId);

				if (charged) {

					// if (!roaming && !upload) {
					consumptionDTO.setIncomingNationalConsumptionCharged(consumption);
					// } else if (roaming && !upload) {
					// consumptionDTO.setIncomingRoamingConsumptionCharged(consumption);
					// } else if (!roaming && upload) {
					// consumptionDTO.setOutgoingNationalConsumptionCharged(consumption);
					// } else {
					// consumptionDTO.setOutgoingRoamingConsumptionCharged(consumption);
					// }

					consumptionDTO.setConsumptionCharged(consumptionDTO.getConsumptionCharged() + consumption);
					consumptionDTO.setAmountCharged(consumptionDTO.getAmountCharged().add(amount));

				} else {
					// if (!roaming && !upload) {
					consumptionDTO.setIncomingNationalConsumptionUncharged(consumption);
					// } else if (roaming && !upload) {
					// consumptionDTO.setIncomingRoamingConsumptionUncharged(consumption);
					// } else if (!roaming && upload) {
					// consumptionDTO.setOutgoingNationalConsumptionUncharged(consumption);
					// } else {
					// consumptionDTO.setOutgoingRoamingConsumptionUncharged(consumption);
					// }
					consumptionDTO.setConsumptionUncharged(consumptionDTO.getConsumptionUncharged() + consumption);
					consumptionDTO.setAmountUncharged(consumptionDTO.getAmountUncharged().add(amount));
				}
			}
		}

		return consumptionDTO;

	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void createInvoiceAndAgregates(BillingAccount billingAccount, Invoice invoice, User currentUser)
			throws BusinessException {
		boolean entreprise = billingAccount.getProvider().isEntreprise();

		BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;

		for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
			WalletInstance wallet = userAccount.getWallet();

			// TODO : use Named queries
			CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root from = cq.from(RatedTransaction.class);
			Path<Long> invoiceSubCategoryPath = from.get("invoiceSubCategory").get("id");

			Expression<BigDecimal> amountWithoutTax = cb.sum(from.get("amountWithoutTax"));
			Expression<BigDecimal> amountWithTax = cb.sum(from.get("amountWithTax"));
			Expression<BigDecimal> amountTax = cb.sum(from.get("amountTax"));
			Expression<BigDecimal> quantity = cb.sum(from.get("quantity"));

			CriteriaQuery<Object[]> select = cq.multiselect(invoiceSubCategoryPath, amountWithoutTax, amountWithTax,
					amountTax, quantity);
			// Grouping
			cq.groupBy(invoiceSubCategoryPath);
			// Restrictions (I don't really understand what you're querying)
			Predicate pStatus = cb.equal(from.get("status"), RatedTransactionStatusEnum.OPEN);
			Predicate pWallet = cb.equal(from.get("wallet"), wallet);
			Predicate pAmoutWithoutTax = null;
			if (!billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
				pAmoutWithoutTax = cb.notEqual(from.get("amountWithoutTax"), BigDecimal.ZERO);
			}

			Predicate pdoNotTriggerInvoicing = cb.isFalse(from.get("doNotTriggerInvoicing"));
			Predicate pInvoice = cb.isNull(from.get("invoice"));
			if (!billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
				cq.where(pStatus, pWallet, pAmoutWithoutTax, pdoNotTriggerInvoicing, pInvoice);
			} else {
				cq.where(pStatus, pWallet, pdoNotTriggerInvoicing, pInvoice);
			}

			List<InvoiceAgregate> invoiceAgregateFList = new ArrayList<InvoiceAgregate>();
			List<Object[]> invoiceSubCats = getEntityManager().createQuery(cq).getResultList();

			Map<Long, CategoryInvoiceAgregate> catInvoiceAgregateMap = new HashMap<Long, CategoryInvoiceAgregate>();
			Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

			SubCategoryInvoiceAgregate biggestSubCat = null;
			BigDecimal biggestAmount = new BigDecimal("-100000000");

			for (Object[] object : invoiceSubCats) {
				log.info("amountWithoutTax=" + object[1] + "amountWithTax" + object[2] + "amountTax" + object[3]);
				Long invoiceSubCategoryId = (Long) object[0];
				InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(invoiceSubCategoryId);
				Tax tax = null;
				for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory
						.getInvoiceSubcategoryCountries()) {
					if (invoicesubcatCountry.getTradingCountry().getCountryCode()
							.equalsIgnoreCase(invoice.getBillingAccount().getTradingCountry().getCountryCode())) {
						tax = invoicesubcatCountry.getTax();
					}
				}

				SubCategoryInvoiceAgregate invoiceAgregateF = new SubCategoryInvoiceAgregate();
				invoiceAgregateF.setAuditable(billingAccount.getAuditable());
				invoiceAgregateF.setProvider(billingAccount.getProvider());
				invoiceAgregateF.setInvoice(invoice);
				invoiceAgregateF.setBillingRun(billingAccount.getBillingRun());
				invoiceAgregateF.setWallet(wallet);
				invoiceAgregateF.setAccountingCode(invoiceSubCategory.getAccountingCode());
				invoiceAgregateF.setSubCategoryTax(tax);
				fillAgregates(invoiceAgregateF, wallet);
				int itemNumber = invoiceAgregateF.getItemNumber() != null ? invoiceAgregateF.getItemNumber() + 1 : 1;
				invoiceAgregateF.setItemNumber(itemNumber);

				invoiceAgregateF.setAmountWithoutTax((BigDecimal) object[1]);
				invoiceAgregateF.setAmountWithTax((BigDecimal) object[2]);
				invoiceAgregateF.setAmountTax((BigDecimal) object[3]);
				invoiceAgregateF.setQuantity((BigDecimal) object[4]);
				invoiceAgregateF.setProvider(billingAccount.getProvider());
				invoiceAgregateFList.add(invoiceAgregateF);
				// end agregate F

				if (!entreprise) {
					nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add((BigDecimal) object[2]);
				}

				// start agregate T
				TaxInvoiceAgregate invoiceAgregateT = null;
				Long taxId = tax.getId();

				if (taxInvoiceAgregateMap.containsKey(taxId)) {
					invoiceAgregateT = taxInvoiceAgregateMap.get(taxId);
				} else {
					invoiceAgregateT = new TaxInvoiceAgregate();
					invoiceAgregateT.setAuditable(billingAccount.getAuditable());
					invoiceAgregateT.setProvider(billingAccount.getProvider());
					invoiceAgregateT.setInvoice(invoice);
					invoiceAgregateT.setBillingRun(billingAccount.getBillingRun());
					invoiceAgregateT.setTax(tax);
					invoiceAgregateT.setAccountingCode(tax.getAccountingCode());

					taxInvoiceAgregateMap.put(taxId, invoiceAgregateT);
				}

				if (tax.getPercent().compareTo(BigDecimal.ZERO) == 0) {
					invoiceAgregateT.addAmountWithoutTax(invoiceAgregateF.getAmountWithoutTax());
					invoiceAgregateT.addAmountWithTax(invoiceAgregateF.getAmountWithTax());
					invoiceAgregateT.addAmountTax(invoiceAgregateF.getAmountTax());
				}

				fillAgregates(invoiceAgregateT, wallet);
				if (invoiceAgregateF.getSubCategoryTax().getPercent().compareTo(BigDecimal.ZERO) != 0) {
					invoiceAgregateT.setTaxPercent(invoiceAgregateF.getSubCategoryTax().getPercent());
				}
				invoiceAgregateT.setProvider(billingAccount.getProvider());

				if (invoiceAgregateT.getId() == null) {
					invoiceAgregateService.create(invoiceAgregateT, currentUser, currentUser.getProvider());
				}

				invoiceAgregateF.setSubCategoryTax(tax);
				invoiceAgregateF.setInvoiceSubCategory(invoiceSubCategory);

				// start agregate R
				CategoryInvoiceAgregate invoiceAgregateR = null;
				Long invoiceCategoryId = invoiceSubCategory.getInvoiceCategory().getId();
				if (catInvoiceAgregateMap.containsKey(invoiceCategoryId)) {
					invoiceAgregateR = catInvoiceAgregateMap.get(invoiceCategoryId);
				} else {
					invoiceAgregateR = new CategoryInvoiceAgregate();
					invoiceAgregateR.setAuditable(billingAccount.getAuditable());
					invoiceAgregateR.setProvider(billingAccount.getProvider());

					invoiceAgregateR.setInvoice(invoice);
					invoiceAgregateR.setBillingRun(billingAccount.getBillingRun());
					catInvoiceAgregateMap.put(invoiceCategoryId, invoiceAgregateR);
				}

				fillAgregates(invoiceAgregateR, wallet);
				if (invoiceAgregateR.getId() == null) {
					invoiceAgregateService.create(invoiceAgregateR, currentUser, currentUser.getProvider());
				}

				invoiceAgregateR.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
				invoiceAgregateR.setProvider(billingAccount.getProvider());
				invoiceAgregateF.setCategoryInvoiceAgregate(invoiceAgregateR);
				invoiceAgregateF.setTaxInvoiceAgregate(invoiceAgregateT);
				// end agregate R

				// round the amount without Tax
				// compute the largest subcategory

				// first we round the amount without tax

				log.info("subcat " + invoiceAgregateF.getAccountingCode() + " ht="
						+ invoiceAgregateF.getAmountWithoutTax() + " ->"
						+ invoiceAgregateF.getAmountWithoutTax().setScale(2, RoundingMode.HALF_UP));
				invoiceAgregateF.setAmountWithoutTax(invoiceAgregateF.getAmountWithoutTax().setScale(2,
						RoundingMode.HALF_UP));
				// add it to taxAggregate and CategoryAggregate
				if (invoiceAgregateF.getSubCategoryTax().getPercent().compareTo(BigDecimal.ZERO) != 0) {
					TaxInvoiceAgregate taxInvoiceAgregate = taxInvoiceAgregateMap.get(invoiceAgregateF
							.getSubCategoryTax().getId());
					taxInvoiceAgregate.addAmountWithoutTax(invoiceAgregateF.getAmountWithoutTax());
					log.info("  tax " + invoiceAgregateF.getTaxInvoiceAgregate().getTaxPercent() + " ht ->"
							+ taxInvoiceAgregate.getAmountWithoutTax());
				}
				invoiceAgregateF.getCategoryInvoiceAgregate().addAmountWithoutTax(
						invoiceAgregateF.getAmountWithoutTax());
				invoiceAgregateF.getCategoryInvoiceAgregate().addAmountWithTax(invoiceAgregateF.getAmountWithTax());
				log.info("  cat " + invoiceAgregateF.getCategoryInvoiceAgregate().getId() + " ht ->"
						+ invoiceAgregateF.getCategoryInvoiceAgregate().getAmountWithoutTax());
				if (invoiceAgregateF.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
					biggestAmount = invoiceAgregateF.getAmountWithoutTax();
					biggestSubCat = invoiceAgregateF;
				}

				invoiceAgregateService.create(invoiceAgregateF, currentUser, currentUser.getProvider());
			}

			// compute the tax
			for (Map.Entry<Long, TaxInvoiceAgregate> taxCatMap : taxInvoiceAgregateMap.entrySet()) {
				TaxInvoiceAgregate taxCat = taxCatMap.getValue();
				if (taxCat.getTax().getPercent().compareTo(BigDecimal.ZERO) != 0) {
					// then compute the tax
					taxCat.setAmountTax(taxCat.getAmountWithoutTax().multiply(taxCat.getTaxPercent())
							.divide(new BigDecimal("100")));
					// then round the tax
					taxCat.setAmountTax(taxCat.getAmountTax().setScale(2, RoundingMode.HALF_UP));

					// and compute amount with tax
					taxCat.setAmountWithTax(taxCat.getAmountWithoutTax().add(taxCat.getAmountTax())
							.setScale(2, RoundingMode.HALF_UP));
					log.info("  tax2 ht ->" + taxCat.getAmountWithoutTax());
				} else {
					// compute the percent
					if (taxCat.getAmountTax() != null && taxCat.getAmount() != null
							&& taxCat.getAmount().compareTo(BigDecimal.ZERO) != 0) {
						taxCat.setTaxPercent(taxCat.getAmountTax().divide(taxCat.getAmount())
								.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
					} else {
						taxCat.setTaxPercent(BigDecimal.ZERO);
					}
				}

			}

			for (Map.Entry<Long, TaxInvoiceAgregate> tax : taxInvoiceAgregateMap.entrySet()) {
				TaxInvoiceAgregate taxInvoiceAgregate = tax.getValue();
				invoice.addAmountTax(taxInvoiceAgregate.getAmountTax().setScale(2, RoundingMode.HALF_UP));
				invoice.addAmountWithoutTax(taxInvoiceAgregate.getAmountWithoutTax().setScale(2, RoundingMode.HALF_UP));
				invoice.addAmountWithTax(taxInvoiceAgregate.getAmountWithTax().setScale(2, RoundingMode.HALF_UP));
			}
			BigDecimal balance=BigDecimal.ZERO;
			if (!entreprise && biggestSubCat != null) {
				// TODO log those steps
				BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
				log.info("delta= " + nonEnterprisePriceWithTax + " - " + invoice.getAmountWithTax() + "=" + delta);
				biggestSubCat.setAmountWithoutTax(biggestSubCat.getAmountWithoutTax().add(delta)
						.setScale(2, RoundingMode.HALF_UP));

				TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(biggestSubCat.getSubCategoryTax()
						.getId());
				log.info("  tax3 ht ->" + invoiceAgregateT.getAmountWithoutTax());
				invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT.getAmountWithoutTax().add(delta)
						.setScale(2, RoundingMode.HALF_UP));
				log.info("  tax4 ht ->" + invoiceAgregateT.getAmountWithoutTax());
				CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
				invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR.getAmountWithoutTax().add(delta)
						.setScale(2, RoundingMode.HALF_UP));

				invoice.setAmountWithoutTax(invoice.getAmountWithoutTax().add(delta).setScale(2, RoundingMode.HALF_UP));
				invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(2, RoundingMode.HALF_UP));
				balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount()
						.getCustomerAccount().getCode(), invoice.getDueDate());

				if (balance == null) {
					throw new BusinessException("account balance calculation failed");
				}
			}
			BigDecimal netToPay = BigDecimal.ZERO;
			if (entreprise) {
				netToPay = invoice.getAmountWithTax();
			} else {
				netToPay = invoice.getAmountWithTax().add(balance);
			}
			invoice.setNetToPay(netToPay);
		}
	}

	private void fillAgregates(InvoiceAgregate invoiceAgregate, WalletInstance wallet) {
		invoiceAgregate.setBillingAccount(wallet.getUserAccount().getBillingAccount());
		invoiceAgregate.setUserAccount(wallet.getUserAccount());
		int itemNumber = invoiceAgregate.getItemNumber() != null ? invoiceAgregate.getItemNumber() + 1 : 1;
		invoiceAgregate.setItemNumber(itemNumber);
	}

	public Boolean isBillingAccountBillable(BillingAccount billingAccount) {
		TypedQuery<Long> q = null;

		if (billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
			q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoincedDisplayFree", Long.class);
		} else {
			q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoinced", Long.class);
		}

		long count = q.setParameter("billingAccount", billingAccount).getSingleResult();

		return count > 0 ? true : false;
	}

	public List<RatedTransaction> getRatedTransactions(WalletInstance wallet, Invoice invoice,
			InvoiceSubCategory invoiceSubCategory) {
		long startDate = System.currentTimeMillis();
		QueryBuilder qb = new QueryBuilder("from RatedTransaction c");
		qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
		qb.addCriterionEntity("c.wallet", wallet);
		qb.addCriterionEntity("c.invoice", invoice);
		qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);

		if (!invoice.getProvider().isDisplayFreeTransacInInvoice()) {
			qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
		}

		qb.addOrderCriterion("c.walletOperationId", true);

		@SuppressWarnings("unchecked")
		List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

		log.info("getRatedTransactions time: " + (System.currentTimeMillis() - startDate));

		return ratedTransactions;

	}

}
