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
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
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

@Stateless
public class RatedTransactionService extends
		PersistenceService<RatedTransaction> {

	@EJB
	private SubscriptionService subscriptionService;

	@EJB
	private InvoiceAgregateService invoiceAgregateService;

	@EJB
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@EJB
	private CustomerAccountService customerAccountService;

	private Logger logger = Logger.getLogger(RatedTransactionService.class
			.getName());

	@SuppressWarnings("unchecked")
	public List<RatedTransaction> getRatedTransactionsInvoiced(
			UserAccount userAccount) {
		if (userAccount == null || userAccount.getWallet() == null) {
			return null;
		}
		return (List<RatedTransaction>) getEntityManager()
				.createQuery(
						"from "
								+ RatedTransaction.class.getSimpleName()
								+ " where wallet=:wallet and invoice is not null order by usageDate desc")
				.setParameter("wallet", userAccount.getWallet())
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public ConsumptionDTO getConsumption(Subscription subscription,
			String infoType, Integer billingCycle, boolean sumarizeConsumption)
			throws IncorrectSusbcriptionException {

		Date lastBilledDate = null;
		ConsumptionDTO consumptionDTO = new ConsumptionDTO();

		// If billing has been run already, use last billing date plus a day as
		// filtering FROM value
		// Otherwise leave it null, so it wont be included in a query
		if (subscription.getUserAccount().getBillingAccount().getBillingRun() != null) {
			lastBilledDate = subscription.getUserAccount().getBillingAccount()
					.getBillingRun().getEndDate();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(lastBilledDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			lastBilledDate = calendar.getTime();

		}

		if (sumarizeConsumption) {

			QueryBuilder qb = new QueryBuilder(
					"select sum(amount1WithTax), sum(usageAmount) from "
							+ RatedTransaction.class.getSimpleName());
			qb.addCriterionEntity("subscription", subscription);
			qb.addCriterion("subUsageCode1", "=", infoType, false);
			qb.addCriterionDateRangeFromTruncatedToDay("usageDate",
					lastBilledDate);
			String baseSql = qb.getSqlString();

			// Summarize invoiced transactions
			String sql = baseSql + " and status='BILLED'";

			Query query = getEntityManager().createQuery(sql);

			for (Entry<String, Object> param : qb.getParams().entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}

			Object[] results = (Object[]) query.getSingleResult();

			consumptionDTO.setAmountCharged((BigDecimal) results[0]);
			consumptionDTO
					.setConsumptionCharged(((Long) results[1]).intValue());

			// Summarize not invoiced transactions
			sql = baseSql + " and status<>'BILLED'";

			query = getEntityManager().createQuery(sql);

			for (Entry<String, Object> param : qb.getParams().entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}

			results = (Object[]) query.getSingleResult();

			consumptionDTO.setAmountUncharged((BigDecimal) results[0]);
			consumptionDTO.setConsumptionUncharged(((Long) results[1])
					.intValue());

		} else {

			QueryBuilder qb = new QueryBuilder(
					"select sum(amount1WithTax), sum(usageAmount), groupingId, case when status='BILLED' then 'true' else 'false' end from "
							+ RatedTransaction.class.getSimpleName());
			qb.addCriterionEntity("subscription", subscription);
			qb.addCriterion("subUsageCode1", "=", infoType, false);
			qb.addCriterionDateRangeFromTruncatedToDay("usageDate",
					lastBilledDate);
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
					consumptionDTO
							.setIncomingNationalConsumptionCharged(consumption);
					// } else if (roaming && !upload) {
					// consumptionDTO.setIncomingRoamingConsumptionCharged(consumption);
					// } else if (!roaming && upload) {
					// consumptionDTO.setOutgoingNationalConsumptionCharged(consumption);
					// } else {
					// consumptionDTO.setOutgoingRoamingConsumptionCharged(consumption);
					// }

					consumptionDTO.setConsumptionCharged(consumptionDTO
							.getConsumptionCharged() + consumption);
					consumptionDTO.setAmountCharged(consumptionDTO
							.getAmountCharged().add(amount));

				} else {
					// if (!roaming && !upload) {
					consumptionDTO
							.setIncomingNationalConsumptionUncharged(consumption);
					// } else if (roaming && !upload) {
					// consumptionDTO.setIncomingRoamingConsumptionUncharged(consumption);
					// } else if (!roaming && upload) {
					// consumptionDTO.setOutgoingNationalConsumptionUncharged(consumption);
					// } else {
					// consumptionDTO.setOutgoingRoamingConsumptionUncharged(consumption);
					// }
					consumptionDTO.setConsumptionUncharged(consumptionDTO
							.getConsumptionUncharged() + consumption);
					consumptionDTO.setAmountUncharged(consumptionDTO
							.getAmountUncharged().add(amount));
				}
			}
		}

		return consumptionDTO;

	}

	@SuppressWarnings("unchecked")
	public void sumbillingRunAmounts(BillingRun billingRun,
			List<BillingAccount> billingAccounts,
			RatedTransactionStatusEnum status, boolean entreprise) {

		QueryBuilder qb = new QueryBuilder(
				"select sum(amountWithoutTax),sum(amountWithTax),sum(amountTax) from RatedTransaction c");
		qb.addCriterionEnum("c.status", status);
		qb.addBooleanCriterion("c.doNotTriggerInvoicing", false);
		qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
		qb.addSql("c.invoice is null");
		String ids = "";
		String sep = "";
		for (BillingAccount ba : billingAccounts) {
			ids = ids + sep + ba.getId();
			sep = ",";
		}
		qb.addSql("c.billingAccount.id in (" + ids + ")");

		List<Object[]> ratedTransactions = qb.getQuery(getEntityManager())
				.getResultList();
		Object[] ratedTrans = ratedTransactions.size() > 0 ? ratedTransactions
				.get(0) : null;
		if (ratedTrans != null) {
			billingRun.setPrAmountWithoutTax((BigDecimal) ratedTrans[0]);
			billingRun
					.setPrAmountWithTax(entreprise ? (BigDecimal) ratedTrans[1]
							: (BigDecimal) ratedTrans[0]);
			billingRun.setPrAmountTax((BigDecimal) ratedTrans[2]);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void createInvoiceAndAgregates(BillingRun billingRun,
			BillingAccount billingAccount, Invoice invoice)
			throws BusinessException {
		boolean entreprise = billingRun.getProvider().isEntreprise();

		BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;
		for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
			WalletInstance wallet = userAccount.getWallet();

			CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root from = cq.from(RatedTransaction.class);
			Path<Long> invoiceSubCategoryPath = from.get("invoiceSubCategory")
					.get("id");

			Expression<BigDecimal> amountWithoutTax = cb.sum(from
					.get("amountWithoutTax"));
			Expression<BigDecimal> amountWithTax = cb.sum(from
					.get("amountWithTax"));
			Expression<BigDecimal> amountTax = cb.sum(from.get("amountTax"));
			Expression<BigDecimal> quantity = cb.sum(from.get("quantity"));

			CriteriaQuery<Object[]> select = cq.multiselect(
					invoiceSubCategoryPath, amountWithoutTax, amountWithTax,
					amountTax, quantity);
			// Grouping
			cq.groupBy(invoiceSubCategoryPath);
			// Restrictions (I don't really understand what you're querying)
			Predicate pStatus = cb.equal(from.get("status"),
					RatedTransactionStatusEnum.OPEN);
			Predicate pWallet = cb.equal(from.get("wallet"), wallet);
			Predicate pAmoutWithoutTax = null;
			if (!billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
				pAmoutWithoutTax = cb.notEqual(from.get("amountWithoutTax"),
						BigDecimal.ZERO);
			}

			Predicate pdoNotTriggerInvoicing = cb.isFalse(from
					.get("doNotTriggerInvoicing"));
			Predicate pInvoice = cb.isNull(from.get("invoice"));
			if (!billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
				cq.where(pStatus, pWallet, pAmoutWithoutTax,
						pdoNotTriggerInvoicing, pInvoice);
			} else {
				cq.where(pStatus, pWallet, pdoNotTriggerInvoicing, pInvoice);
			}

			List<InvoiceAgregate> invoiceAgregateFList = new ArrayList<InvoiceAgregate>();
			List<Object[]> invoiceSubCats = getEntityManager().createQuery(cq)
					.getResultList();

			Map<Long, CategoryInvoiceAgregate> catInvoiceAgregateMap = new HashMap<Long, CategoryInvoiceAgregate>();
			Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

			SubCategoryInvoiceAgregate biggestSubCat = null;
			BigDecimal biggestAmount = new BigDecimal("-100000000");

			for (Object[] object : invoiceSubCats) {
				logger.info("amountWithoutTax=" + object[1] + "amountWithTax"
						+ object[2] + "amountTax" + object[3]);
				Long invoiceSubCategoryId = (Long) object[0];
				InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService
						.findById(invoiceSubCategoryId);
				Tax tax = null;
				for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory
						.getInvoiceSubcategoryCountries()) {
					if (invoicesubcatCountry
							.getTradingCountry()
							.getCountryCode()
							.equalsIgnoreCase(
									invoice.getBillingAccount()
											.getTradingCountry()
											.getCountryCode())) {
						tax = invoicesubcatCountry.getTax();
					}
				}

				SubCategoryInvoiceAgregate invoiceAgregateF = new SubCategoryInvoiceAgregate();
				invoiceAgregateF.setAuditable(billingRun.getAuditable());
				invoiceAgregateF.setProvider(billingRun.getProvider());
				invoiceAgregateF.setInvoice(invoice);
				invoiceAgregateF.setBillingRun(billingRun);
				invoiceAgregateF.setWallet(wallet);
				invoiceAgregateF.setAccountingCode(invoiceSubCategory
						.getAccountingCode());
				invoiceAgregateF.setSubCategoryTax(tax);
				fillAgregates(invoiceAgregateF, wallet);
				int itemNumber = invoiceAgregateF.getItemNumber() != null ? invoiceAgregateF
						.getItemNumber() + 1 : 1;
				invoiceAgregateF.setItemNumber(itemNumber);

				invoiceAgregateF.setAmountWithoutTax((BigDecimal) object[1]);
				invoiceAgregateF.setAmountWithTax((BigDecimal) object[2]);
				invoiceAgregateF.setAmountTax((BigDecimal) object[3]);
				invoiceAgregateF.setQuantity((BigDecimal) object[4]);
				invoiceAgregateF.setProvider(billingRun.getProvider());
				invoiceAgregateFList.add(invoiceAgregateF);
				// end agregate F

				if (!entreprise) {
					nonEnterprisePriceWithTax = nonEnterprisePriceWithTax
							.add((BigDecimal) object[2]);
				}

				// start agregate T
				TaxInvoiceAgregate invoiceAgregateT = null;
				Long taxId = tax.getId();
				if (taxInvoiceAgregateMap.containsKey(taxId)) {
					invoiceAgregateT = taxInvoiceAgregateMap.get(taxId);
				} else {
					invoiceAgregateT = new TaxInvoiceAgregate();
					invoiceAgregateT.setAuditable(billingRun.getAuditable());
					invoiceAgregateT.setProvider(billingRun.getProvider());
					invoiceAgregateT.setInvoice(invoice);
					invoiceAgregateT.setBillingRun(billingRun);
					invoiceAgregateT.setTax(tax);
					invoiceAgregateT.setAccountingCode(tax.getAccountingCode());

					taxInvoiceAgregateMap.put(taxId, invoiceAgregateT);
				}
				if (tax.getPercent().compareTo(BigDecimal.ZERO) == 0) {
					invoiceAgregateT.addAmountWithoutTax(invoiceAgregateF
							.getAmountWithoutTax());
					invoiceAgregateT.addAmountWithTax(invoiceAgregateF
							.getAmountWithTax());
					invoiceAgregateT.addAmountTax(invoiceAgregateF
							.getAmountTax());
				}
				fillAgregates(invoiceAgregateT, wallet);
				if (invoiceAgregateF.getSubCategoryTax().getPercent()
						.compareTo(BigDecimal.ZERO) != 0) {
					invoiceAgregateT.setTaxPercent(invoiceAgregateF
							.getSubCategoryTax().getPercent());
				}
				invoiceAgregateT.setProvider(billingRun.getProvider());

				if (invoiceAgregateT.getId() == null) {
					invoiceAgregateService.create(invoiceAgregateT);
				}

				invoiceAgregateF.setSubCategoryTax(tax);
				invoiceAgregateF.setInvoiceSubCategory(invoiceSubCategory);

				// start agregate R
				CategoryInvoiceAgregate invoiceAgregateR = null;
				Long invoiceCategoryId = invoiceSubCategory
						.getInvoiceCategory().getId();
				if (catInvoiceAgregateMap.containsKey(invoiceCategoryId)) {
					invoiceAgregateR = catInvoiceAgregateMap
							.get(invoiceCategoryId);
				} else {
					invoiceAgregateR = new CategoryInvoiceAgregate();
					invoiceAgregateR.setAuditable(billingRun.getAuditable());
					invoiceAgregateR.setProvider(billingRun.getProvider());

					invoiceAgregateR.setInvoice(invoice);
					invoiceAgregateR.setBillingRun(billingRun);
					catInvoiceAgregateMap.put(invoiceCategoryId,
							invoiceAgregateR);
				}

				fillAgregates(invoiceAgregateR, wallet);
				if (invoiceAgregateR.getId() == null) {
					invoiceAgregateService.create(invoiceAgregateR);
				}

				invoiceAgregateR.setInvoiceCategory(invoiceSubCategory
						.getInvoiceCategory());
				invoiceAgregateR.setProvider(billingRun.getProvider());
				invoiceAgregateF.setCategoryInvoiceAgregate(invoiceAgregateR);
				invoiceAgregateF.setTaxInvoiceAgregate(invoiceAgregateT);
				// end agregate R

				// round the amount without Tax
				// compute the largest subcategory

				// first we round the amount without tax

				logger.info("subcat "
						+ invoiceAgregateF.getAccountingCode()
						+ " ht="
						+ invoiceAgregateF.getAmountWithoutTax()
						+ " ->"
						+ invoiceAgregateF.getAmountWithoutTax().setScale(2,
								RoundingMode.HALF_UP));
				invoiceAgregateF.setAmountWithoutTax(invoiceAgregateF
						.getAmountWithoutTax()
						.setScale(2, RoundingMode.HALF_UP));
				// add it to taxAggregate and CategoryAggregate
				if (invoiceAgregateF.getSubCategoryTax().getPercent()
						.compareTo(BigDecimal.ZERO) != 0) {
					TaxInvoiceAgregate taxInvoiceAgregate = taxInvoiceAgregateMap
							.get(invoiceAgregateF.getSubCategoryTax().getId());
					taxInvoiceAgregate.addAmountWithoutTax(invoiceAgregateF
							.getAmountWithoutTax());
					logger.info("  tax "
							+ invoiceAgregateF.getTaxInvoiceAgregate()
									.getTaxPercent() + " ht ->"
							+ taxInvoiceAgregate.getAmountWithoutTax());
				}
				invoiceAgregateF.getCategoryInvoiceAgregate()
						.addAmountWithoutTax(
								invoiceAgregateF.getAmountWithoutTax());
				logger.info("  cat "
						+ invoiceAgregateF.getCategoryInvoiceAgregate().getId()
						+ " ht ->"
						+ invoiceAgregateF.getCategoryInvoiceAgregate()
								.getAmountWithoutTax());
				if (invoiceAgregateF.getAmountWithoutTax().compareTo(
						biggestAmount) > 0) {
					biggestAmount = invoiceAgregateF.getAmountWithoutTax();
					biggestSubCat = invoiceAgregateF;
				}

				invoiceAgregateService.create(invoiceAgregateF);
			}

			// compute the tax
			for (Map.Entry<Long, TaxInvoiceAgregate> taxCatMap : taxInvoiceAgregateMap
					.entrySet()) {
				TaxInvoiceAgregate taxCat = taxCatMap.getValue();
				if (taxCat.getTax().getPercent().compareTo(BigDecimal.ZERO) != 0) {
					// then compute the tax
					taxCat.setAmountTax(taxCat.getAmountWithoutTax()
							.multiply(taxCat.getTaxPercent())
							.divide(new BigDecimal("100")));
					// then round the tax
					taxCat.setAmountTax(taxCat.getAmountTax().setScale(2,
							RoundingMode.HALF_UP));

					// and compute amount with tax
					taxCat.setAmountWithTax(taxCat.getAmountWithoutTax()
							.add(taxCat.getAmountTax())
							.setScale(2, RoundingMode.HALF_UP));
					logger.info("  tax2 ht ->" + taxCat.getAmountWithoutTax());
				} else {
					// compute the percent
					if (taxCat.getAmountTax() != null
							&& taxCat.getAmount() != null
							&& taxCat.getAmount().compareTo(BigDecimal.ZERO) != 0) {
						taxCat.setTaxPercent(taxCat.getAmountTax()
								.divide(taxCat.getAmount())
								.multiply(new BigDecimal("100"))
								.setScale(2, RoundingMode.HALF_UP));
					} else {
						taxCat.setTaxPercent(BigDecimal.ZERO);
					}
				}

			}

			for (Map.Entry<Long, TaxInvoiceAgregate> tax : taxInvoiceAgregateMap
					.entrySet()) {
				TaxInvoiceAgregate taxInvoiceAgregate = tax.getValue();
				invoice.addAmountTax(taxInvoiceAgregate.getAmountTax()
						.setScale(2, RoundingMode.HALF_UP));
				invoice.addAmountWithoutTax(taxInvoiceAgregate
						.getAmountWithoutTax()
						.setScale(2, RoundingMode.HALF_UP));
				invoice.addAmountWithTax(taxInvoiceAgregate.getAmountWithTax()
						.setScale(2, RoundingMode.HALF_UP));
			}

			if (!entreprise && biggestSubCat != null) {
				// TODO log those steps
				BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice
						.getAmountWithTax());
				logger.info("delta= " + nonEnterprisePriceWithTax + " - "
						+ invoice.getAmountWithTax() + "=" + delta);
				biggestSubCat.setAmountWithoutTax(biggestSubCat
						.getAmountWithoutTax().add(delta)
						.setScale(2, RoundingMode.HALF_UP));

				TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap
						.get(biggestSubCat.getSubCategoryTax().getId());
				logger.info("  tax3 ht ->"
						+ invoiceAgregateT.getAmountWithoutTax());
				invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT
						.getAmountWithoutTax().add(delta)
						.setScale(2, RoundingMode.HALF_UP));
				logger.info("  tax4 ht ->"
						+ invoiceAgregateT.getAmountWithoutTax());
				CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat
						.getCategoryInvoiceAgregate();
				invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR
						.getAmountWithoutTax().add(delta)
						.setScale(2, RoundingMode.HALF_UP));

				invoice.setAmountWithoutTax(invoice.getAmountWithoutTax()
						.add(delta).setScale(2, RoundingMode.HALF_UP));
				invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(2,
						RoundingMode.HALF_UP));
				BigDecimal balance = customerAccountService
						.customerAccountBalanceDue(null, invoice
								.getBillingAccount().getCustomerAccount()
								.getCode(), invoice.getDueDate());

				if (balance == null) {
					throw new BusinessException(
							"account balance calculation failed");
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

	}

	private void fillAgregates(InvoiceAgregate invoiceAgregate,
			WalletInstance wallet) {
		invoiceAgregate.setBillingAccount(wallet.getUserAccount()
				.getBillingAccount());
		invoiceAgregate.setUserAccount(wallet.getUserAccount());
		int itemNumber = invoiceAgregate.getItemNumber() != null ? invoiceAgregate
				.getItemNumber() + 1 : 1;
		invoiceAgregate.setItemNumber(itemNumber);
	}

	public void updateRatedTransactions(BillingRun billingRun,
			BillingAccount billingAccount, Invoice invoice) {
		String statment = "UPDATE RatedTransaction r SET r.billingRun=:billingRun,invoice=:invoice,status=:newStatus where r.invoice is null and r.status=:status and r.doNotTriggerInvoicing=:invoicing and r.billingAccount=:billingAccount";
		if (!billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
			statment += " and r.amountWithoutTax<>:zeroValue ";
		}

		Query query = getEntityManager().createQuery(statment);
		query.setParameter("billingRun", billingRun);
		query.setParameter("invoice", invoice);
		query.setParameter("newStatus", RatedTransactionStatusEnum.BILLED);
		query.setParameter("status", RatedTransactionStatusEnum.OPEN);
		query.setParameter("invoicing", false);
		if (!billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
			query.setParameter("zeroValue", BigDecimal.ZERO);
		}

		query.setParameter("billingAccount", billingAccount);

		query.executeUpdate();

	}

	public Boolean isBillingAccountBillable(BillingRun billingRun,
			Long billingAccountId) {

		QueryBuilder qb = new QueryBuilder("from RatedTransaction c");
		qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.OPEN);
		qb.addCriterionEntity("c.billingAccount.id", billingAccountId);
		qb.addBooleanCriterion("c.doNotTriggerInvoicing", false);
		if (!billingRun.getProvider().isDisplayFreeTransacInInvoice()) {
			qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
		}
		qb.addSql("c.invoice is null");

		@SuppressWarnings("unchecked")
		List<RatedTransaction> ratedTransactions = qb.getQuery(
				getEntityManager()).getResultList();
		return ratedTransactions.size() > 0 ? true : false;

	}

	public List<RatedTransaction> getRatedTransactions(WalletInstance wallet,
			Invoice invoice, InvoiceSubCategory invoiceSubCategory) {

		QueryBuilder qb = new QueryBuilder("from RatedTransaction c");
		qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
		qb.addCriterionEntity("c.wallet", wallet);
		qb.addCriterionEntity("c.invoice", invoice);
		qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);

		@SuppressWarnings("unchecked")
		List<RatedTransaction> ratedTransactions = qb.getQuery(
				getEntityManager()).getResultList();
		return ratedTransactions;

	}

	@SuppressWarnings("unchecked")
	public void billingAccountTotalAmounts(BillingAccount billingAccount,
			boolean entreprise) {

		QueryBuilder qb = new QueryBuilder(
				"select sum(amountWithoutTax),sum(amountWithTax),sum(amountTax) from RatedTransaction c");
		qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.OPEN);
		qb.addBooleanCriterion("c.doNotTriggerInvoicing", false);
		if (!billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
			qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
		}

		qb.addCriterionEntity("c.billingAccount", billingAccount);
		qb.addSql("c.invoice is null");

		List<Object[]> ratedTransactions = qb.getQuery(getEntityManager())
				.getResultList();
		Object[] ratedTrans = ratedTransactions.size() > 0 ? ratedTransactions
				.get(0) : null;
		if (ratedTrans != null) {
			billingAccount.setBrAmountWithoutTax((BigDecimal) ratedTrans[0]);
			billingAccount
					.setBrAmountWithTax(entreprise ? (BigDecimal) ratedTrans[1]
							: (BigDecimal) ratedTrans[0]);
		}

	}
}