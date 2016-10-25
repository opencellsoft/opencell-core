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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
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
import org.meveo.admin.exception.UnrolledbackBusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
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
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class RatedTransactionService extends PersistenceService<RatedTransaction> {
	
	@Inject
	private UserAccountService userAccountService;

	@Inject
	private InvoiceAgregateService invoiceAgregateService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private WalletOperationService walletOperationService;
	
	@Inject
	private BillingAccountService billingAccountService;
	
	@Inject
	private CatMessagesService catMessagesService;
	
	@Inject
	private FilterService filterService;

	private static final BigDecimal HUNDRED = new BigDecimal("100");

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

	public void createInvoiceAndAgregates(BillingAccount billingAccount, Invoice invoice,
			Filter ratedTransactionFilter,Date lastTransactionDate, User currentUser)
			throws BusinessException {
		createInvoiceAndAgregates(billingAccount, invoice,ratedTransactionFilter, lastTransactionDate, currentUser, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createInvoiceAndAgregates(BillingAccount billingAccount, Invoice invoice,
			Filter ratedTransactionFilter,Date lastTransactionDate, User currentUser, boolean isInvoiceAdjustment)
			throws BusinessException {
		billingAccount = getEntityManager().merge(billingAccount);
		
		boolean entreprise = billingAccount.getProvider().isEntreprise();
		int rounding = billingAccount.getProvider().getRounding()==null?2:billingAccount.getProvider().getRounding();
		BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;
		String languageCode = billingAccount.getTradingLanguage().getLanguage().getLanguageCode();
		List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);				
		boolean isExonerated = billingAccountService.isExonerated(billingAccount);
		List<? extends IEntity> ratedTransactions =null;
		if(ratedTransactionFilter!=null){
		   ratedTransactions = filterService.filteredListAsObjects(ratedTransactionFilter, currentUser);
		}
		for (UserAccount userAccount : userAccounts) {
			WalletInstance wallet = userAccount.getWallet();
			List<Object[]> invoiceSubCats=new ArrayList<>();
			if(ratedTransactionFilter!=null){
				if(ratedTransactions!=null && ratedTransactions.size()>0){
					for(IEntity ent:ratedTransactions){
						RatedTransaction ratedTransaction = (RatedTransaction) ent;
						Object[] record = new Object[5];
						record[0]=ratedTransaction.getInvoiceSubCategory().getId();
						record[1]=ratedTransaction.getAmountWithoutTax();
						record[2]=ratedTransaction.getAmountWithTax();
						record[3]=ratedTransaction.getAmountTax();
						record[4]=ratedTransaction.getQuantity();
						boolean recordValid=true;
						//check status
						recordValid&=isInvoiceAdjustment?ratedTransaction.getStatus()==RatedTransactionStatusEnum.BILLED:ratedTransaction.getStatus()==RatedTransactionStatusEnum.OPEN;
						//wallet
						recordValid&=ratedTransaction.getWallet().getId()==wallet.getId();
						//amount
						if (!billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
							recordValid&=!ratedTransaction.getAmountWithoutTax().equals(BigDecimal.ZERO);
						}
						//usageDate
						recordValid&=ratedTransaction.getUsageDate().before(lastTransactionDate);
						//invoice not set
						recordValid&=ratedTransaction.getInvoice()==null;
						
						if(recordValid){
							ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
							boolean foundRecordForSameId=false;
							for(Object[] existingRecord:invoiceSubCats){
								if(existingRecord[0].equals(record[0])){
									foundRecordForSameId=true;
									existingRecord[1]=((BigDecimal)existingRecord[1]).add((BigDecimal)record[1]);
									existingRecord[2]=((BigDecimal)existingRecord[2]).add((BigDecimal)record[2]);
									existingRecord[3]=((BigDecimal)existingRecord[3]).add((BigDecimal)record[3]);
									existingRecord[4]=((BigDecimal)existingRecord[4]).add((BigDecimal)record[4]);
									break;
								}
							}
							if(!foundRecordForSameId){
								invoiceSubCats.add(record);
							}
						}
					}
				}
			} else {
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
				// Restrictions \
				Predicate pStatus = cb.equal(from.get("status"), RatedTransactionStatusEnum.OPEN);
				if (isInvoiceAdjustment) {
					pStatus = cb.equal(from.get("status"), RatedTransactionStatusEnum.BILLED);
				}
				Predicate pWallet = cb.equal(from.get("wallet"), wallet);
				Predicate pAmoutWithoutTax = null;
				if (!billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
					pAmoutWithoutTax = cb.notEqual(from.get("amountWithoutTax"), BigDecimal.ZERO);
				}
				Predicate pOldTransaction = cb.lessThan(from.get("usageDate"),lastTransactionDate);
				Predicate pdoNotTriggerInvoicing = cb.isFalse(from.get("doNotTriggerInvoicing"));
				
				Predicate pInvoice = cb.isNull(from.get("invoice"));
				if (isInvoiceAdjustment) {
					pInvoice = cb.equal(from.get("invoice"), invoice);
				}
				
				if (!billingAccount.getProvider().isDisplayFreeTransacInInvoice()) {
					cq.where(pStatus, pWallet, pAmoutWithoutTax,pOldTransaction, pdoNotTriggerInvoicing, pInvoice);
				} else {
					cq.where(pStatus, pWallet,pOldTransaction, pdoNotTriggerInvoicing, pInvoice);
				}
				invoiceSubCats = getEntityManager().createQuery(cq).getResultList();				
			}
			
			List<InvoiceAgregate> invoiceAgregateSubcatList = new ArrayList<InvoiceAgregate>();
			
			Map<Long, CategoryInvoiceAgregate> catInvoiceAgregateMap = new HashMap<Long, CategoryInvoiceAgregate>();
			Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

			SubCategoryInvoiceAgregate biggestSubCat = null;
			BigDecimal biggestAmount = new BigDecimal("-100000000");			
			if(invoiceSubCats!=null && !invoiceSubCats.isEmpty()){
				for (Object[] object : invoiceSubCats) {
					log.info("amountWithoutTax=" + object[1] + "amountWithTax =" + object[2] + "amountTax=" + object[3]);
					Long invoiceSubCategoryId = (Long) object[0];
					InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(invoiceSubCategoryId);
					List<Tax> taxes = new ArrayList<Tax>();
					for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
						if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode()) 
					    && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(),billingAccount, invoice)) {
							
							taxes.add(invoicesubcatCountry.getTax());
						}
					}

					String invSubCatDescTranslated = catMessagesService.getMessageDescription(invoiceSubCategory, languageCode);
					SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
					invoiceAgregateSubcat.setAuditable(billingAccount.getAuditable());
					invoiceAgregateSubcat.setProvider(billingAccount.getProvider());
					invoiceAgregateSubcat.setInvoice(invoice);
					invoiceAgregateSubcat.setDescription(invSubCatDescTranslated);
					invoiceAgregateSubcat.setBillingRun(billingAccount.getBillingRun());
					invoiceAgregateSubcat.setWallet(wallet);
					invoiceAgregateSubcat.setAccountingCode(invoiceSubCategory.getAccountingCode());
					fillAgregates(invoiceAgregateSubcat, wallet);
					int itemNumber = invoiceAgregateSubcat.getItemNumber() != null ? invoiceAgregateSubcat.getItemNumber() + 1 : 1;
					invoiceAgregateSubcat.setItemNumber(itemNumber);

					invoiceAgregateSubcat.setAmountWithoutTax((BigDecimal) object[1]);
					invoiceAgregateSubcat.setAmountWithTax((BigDecimal) object[2]);
					invoiceAgregateSubcat.setAmountTax((BigDecimal) object[3]);
					invoiceAgregateSubcat.setQuantity((BigDecimal) object[4]);
					invoiceAgregateSubcat.setProvider(billingAccount.getProvider());
					invoiceAgregateSubcatList.add(invoiceAgregateSubcat);
					// end agregate F

					if (!entreprise) {
						nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add((BigDecimal) object[2]);
					}

					// start agregate T
					if(!isExonerated){
						for (Tax tax:taxes) {
							TaxInvoiceAgregate invoiceAgregateTax = null;
							Long taxId = tax.getId();
	
							if (taxInvoiceAgregateMap.containsKey(taxId)) {
								invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
							} else {
								invoiceAgregateTax = new TaxInvoiceAgregate();
								invoiceAgregateTax.setAuditable(billingAccount.getAuditable());
								invoiceAgregateTax.setProvider(billingAccount.getProvider());
								invoiceAgregateTax.setInvoice(invoice);
								invoiceAgregateTax.setBillingRun(billingAccount.getBillingRun());
								invoiceAgregateTax.setTax(tax);
								invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
								
								taxInvoiceAgregateMap.put(taxId, invoiceAgregateTax);
							}
	
							if (tax.getPercent().compareTo(BigDecimal.ZERO) == 0 ) {
								invoiceAgregateTax.addAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax());
								invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
								invoiceAgregateTax.addAmountWithTax(invoiceAgregateSubcat.getAmountWithoutTax());
								invoiceAgregateTax.setTaxPercent(BigDecimal.ZERO);
							}
	
							fillAgregates(invoiceAgregateTax, wallet);
							if (tax.getPercent().compareTo(BigDecimal.ZERO) != 0 ) {
								invoiceAgregateTax.setTaxPercent(tax.getPercent());
							}
							invoiceAgregateTax.setProvider(billingAccount.getProvider());
	
							if (invoiceAgregateTax.getId() == null) {
								invoiceAgregateService.create(invoiceAgregateTax, currentUser);
							}
							
							invoiceAgregateSubcat.addSubCategoryTax(tax);
						}
					}



					invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCategory);

					// start agregate R
					CategoryInvoiceAgregate invoiceAgregateCat = null;
					Long invoiceCategoryId = invoiceSubCategory.getInvoiceCategory().getId();
					if (catInvoiceAgregateMap.containsKey(invoiceCategoryId)) {
						invoiceAgregateCat = catInvoiceAgregateMap.get(invoiceCategoryId);
					} else {
						String invCatDescTranslated  = catMessagesService.getMessageDescription(invoiceSubCategory.getInvoiceCategory(), languageCode);
						invoiceAgregateCat = new CategoryInvoiceAgregate();
						invoiceAgregateCat.setAuditable(billingAccount.getAuditable());
						invoiceAgregateCat.setProvider(billingAccount.getProvider());
						invoiceAgregateCat.setInvoice(invoice);
						invoiceAgregateCat.setBillingRun(billingAccount.getBillingRun());						
						invoiceAgregateCat.setDescription(invCatDescTranslated);
						catInvoiceAgregateMap.put(invoiceCategoryId, invoiceAgregateCat);
					}

					fillAgregates(invoiceAgregateCat, wallet);
					if (invoiceAgregateCat.getId() == null) {
						invoiceAgregateService.create(invoiceAgregateCat, currentUser);
					}

					invoiceAgregateCat.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
					invoiceAgregateCat.setProvider(billingAccount.getProvider());
					invoiceAgregateSubcat.setCategoryInvoiceAgregate(invoiceAgregateCat);

					// end agregate R

					// round the amount without Tax
					// compute the largest subcategory

					// first we round the amount without tax

					log.debug("subcat " + invoiceAgregateSubcat.getAccountingCode() + " ht="
							+ invoiceAgregateSubcat.getAmountWithoutTax() + " ->"
							+ invoiceAgregateSubcat.getAmountWithoutTax().setScale(rounding, RoundingMode.HALF_UP));
					invoiceAgregateSubcat.setAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax().setScale(rounding,
							RoundingMode.HALF_UP));
					// add it to taxAggregate and CategoryAggregate
					for(Tax tax:invoiceAgregateSubcat.getSubCategoryTaxes()){
						if (tax.getPercent().compareTo(BigDecimal.ZERO) != 0 && !isExonerated) {
							TaxInvoiceAgregate taxInvoiceAgregate = taxInvoiceAgregateMap.get(tax.getId());
							taxInvoiceAgregate.addAmountWithoutTax(invoiceAgregateSubcat.getAmountWithoutTax());
							log.info("  tax " + tax.getPercent() + " ht ->"
									+ taxInvoiceAgregate.getAmountWithoutTax());
						}

					}

					invoiceAgregateSubcat.getCategoryInvoiceAgregate().addAmountWithoutTax(
							invoiceAgregateSubcat.getAmountWithoutTax());
					log.debug("  cat " + invoiceAgregateSubcat.getCategoryInvoiceAgregate().getId() + " ht ->"

							+ invoiceAgregateSubcat.getCategoryInvoiceAgregate().getAmountWithoutTax());
					if (invoiceAgregateSubcat.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
						biggestAmount = invoiceAgregateSubcat.getAmountWithoutTax();
						biggestSubCat = invoiceAgregateSubcat;
					}

					invoiceAgregateService.create(invoiceAgregateSubcat, currentUser);

				}

				// compute the tax
				if(!isExonerated){
					for (Map.Entry<Long, TaxInvoiceAgregate> taxCatMap : taxInvoiceAgregateMap.entrySet()) {
						TaxInvoiceAgregate taxCat = taxCatMap.getValue();
						if (taxCat.getTax().getPercent().compareTo(BigDecimal.ZERO) != 0) {
							// then compute the tax
							taxCat.setAmountTax(taxCat.getAmountWithoutTax().multiply(taxCat.getTaxPercent())
									.divide(new BigDecimal("100")));
							// then round the tax
							taxCat.setAmountTax(taxCat.getAmountTax().setScale(rounding, RoundingMode.HALF_UP));

							// and compute amount with tax
							/*taxCat.setAmountWithTax(taxCat.getAmountWithoutTax().add(taxCat.getAmountTax())
									.setScale(rounding, RoundingMode.HALF_UP));*/
							log.debug("  tax2 ht ->" + taxCat.getAmountWithoutTax());
						} 

					}
				}

				for (Map.Entry<Long, CategoryInvoiceAgregate> cat : catInvoiceAgregateMap.entrySet()) {
					CategoryInvoiceAgregate categoryInvoiceAgregate = cat.getValue();
					invoice.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax().setScale(rounding, RoundingMode.HALF_UP));
				}
				for (Map.Entry<Long, TaxInvoiceAgregate> tax : taxInvoiceAgregateMap.entrySet()) {
					TaxInvoiceAgregate taxInvoiceAgregate = tax.getValue();
					invoice.addAmountTax(taxInvoiceAgregate.getAmountTax().setScale(rounding, RoundingMode.HALF_UP));
				}
				if(invoice.getAmountWithoutTax()!=null){

					invoice.setAmountWithTax(invoice.getAmountWithoutTax().add(invoice.getAmountTax() == null ? BigDecimal.ZERO : invoice.getAmountTax()));
				}
				BigDecimal balance=BigDecimal.ZERO;
				if (!entreprise && biggestSubCat != null && !isExonerated) {
					BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
					log.debug("delta= " + nonEnterprisePriceWithTax + " - " + invoice.getAmountWithTax() + "=" + delta);
					biggestSubCat.setAmountWithoutTax(biggestSubCat.getAmountWithoutTax().add(delta)
							.setScale(rounding, RoundingMode.HALF_UP));
					for(Tax tax:biggestSubCat.getSubCategoryTaxes()){

						TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(tax
								.getId());
						log.debug("  tax3 ht ->" + invoiceAgregateT.getAmountWithoutTax());
						invoiceAgregateT.setAmountWithoutTax(invoiceAgregateT.getAmountWithoutTax().add(delta)
								.setScale(rounding, RoundingMode.HALF_UP));
						log.debug("  tax4 ht ->" + invoiceAgregateT.getAmountWithoutTax());

					}
					CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
					invoiceAgregateR.setAmountWithoutTax(invoiceAgregateR.getAmountWithoutTax().add(delta)
							.setScale(rounding, RoundingMode.HALF_UP));

					invoice.setAmountWithoutTax(invoice.getAmountWithoutTax().add(delta).setScale(rounding, RoundingMode.HALF_UP));
					invoice.setAmountWithTax(nonEnterprisePriceWithTax.setScale(rounding, RoundingMode.HALF_UP));


				}
				createInvoiceDiscountAggregates(currentUser,userAccount, invoice,taxInvoiceAgregateMap);


			}

		}
		Object[] object=invoiceAgregateService.findTotalAmountsForDiscountAggregates(invoice);

		BigDecimal discountAmountWithoutTax=(BigDecimal)object[0];
		BigDecimal discountAmountTax=(BigDecimal)object[1];
		BigDecimal discountAmountWithTax=(BigDecimal)object[2];


		log.info("discountAmountWithoutTax= {},discountAmountTax={},discountAmountWithTax={}" ,object[0],object[1], object[2]);

		invoice.addAmountWithoutTax(discountAmountWithoutTax);
		invoice.addAmountTax(discountAmountTax);
		invoice.addAmountWithTax(discountAmountWithTax);
		BigDecimal netToPay = BigDecimal.ZERO;
		if (entreprise) {
			netToPay = invoice.getAmountWithTax();
		} else {
			BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount()
					.getCustomerAccount().getCode(), invoice.getDueDate(),invoice.getProvider());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = invoice.getAmountWithTax().add(balance);
		}
		invoice.setNetToPay(netToPay);
	}

	private void fillAgregates(InvoiceAgregate invoiceAgregate, WalletInstance wallet) {
		invoiceAgregate.setBillingAccount(wallet.getUserAccount().getBillingAccount());
		invoiceAgregate.setUserAccount(wallet.getUserAccount());
		int itemNumber = invoiceAgregate.getItemNumber() != null ? invoiceAgregate.getItemNumber() + 1 : 1;
		invoiceAgregate.setItemNumber(itemNumber);
	}

	public Boolean isBillingAccountBillable(BillingAccount billingAccount,Date lastTransactionDate) {
		long count = 0;
		TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoinced", Long.class);
		count = q.setParameter("billingAccount", billingAccount)
				.setParameter("lastTransactionDate", lastTransactionDate).getSingleResult();
		log.debug("isBillingAccountBillable code={},lastTransactionDate={}) : {}",billingAccount.getCode(),lastTransactionDate,count);
		return count > 0 ? true : false;
	}
	/**
	 * This method is only for generating Xml invoice {@link org.meveo.service.billing.impl.XMLInvoiceCreator 
	 * #createXMLInvoice(Long, java.io.File, boolean, boolean) createXMLInvoice}
	 * 
	 * <p>If the provider's displayFreeTransacInInvoice of the current invoice is <tt>false</tt>, RatedTransaction 
	 * with amount=0 don't show up in the XML. If displayFreeTransacInInvoice is <tt>true</tt>, RT, which status is OPEN and
	 * not linked to a invoice, will appear in the XML invoice</p>
	 * @param wallet
	 * @param invoice
	 * @param invoiceSubCategory
	 * @return
	 */
	public List<RatedTransaction> getRatedTransactionsForXmlInvoice(WalletInstance wallet, Invoice invoice,
			InvoiceSubCategory invoiceSubCategory) {
		long startDate = System.currentTimeMillis();
		QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c", Arrays.asList("priceplan"),
				invoice.getProvider());
		qb.addCriterionEntity("c.wallet", wallet);
		qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);

		if (!invoice.getProvider().isDisplayFreeTransacInInvoice()) {
			qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
			qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
			qb.addCriterionEntity("c.invoice", invoice);
		}else{
			qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.OPEN);
			qb.addSql(" c.invoice is null ");
		}

		qb.addOrderCriterion("c.usageDate", true);

		@SuppressWarnings("unchecked")
		List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

		log.debug("getRatedTransactions time: " + (System.currentTimeMillis() - startDate));

		return ratedTransactions;

	}

	public List<RatedTransaction> getRatedTransactions(WalletInstance wallet, Invoice invoice,
			InvoiceSubCategory invoiceSubCategory) {
		long startDate = System.currentTimeMillis();
		QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c", Arrays.asList("priceplan"),
				invoice.getProvider());
		qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
		qb.addCriterionEntity("c.wallet", wallet);
		qb.addCriterionEntity("c.invoice", invoice);
		qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);

		qb.addOrderCriterion("c.usageDate", true);

		@SuppressWarnings("unchecked")
		List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

		log.debug("getRatedTransactions time: " + (System.currentTimeMillis() - startDate));

		return ratedTransactions;

	}

	public int reratedByWalletOperationId(Long id) throws UnrolledbackBusinessException {
		int result = 0;
		List<RatedTransaction> ratedTransactions =  (List<RatedTransaction>) getEntityManager()
				.createNamedQuery("RatedTransaction.listByWalletOperationId", RatedTransaction.class)
				.setParameter("walletOperationId", id).getResultList();
		for(RatedTransaction ratedTransaction:ratedTransactions){
			if(ratedTransaction.getBillingRun()!=null && ratedTransaction.getBillingRun().getStatus()!=BillingRunStatusEnum.CANCELED){
				throw new UnrolledbackBusinessException("A rated transaction "+ratedTransaction.getId()+" forbid rerating of wallet operation "+id);
			}
			ratedTransaction.setStatus(RatedTransactionStatusEnum.RERATED);
			result++;
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	public List<RatedTransaction> getNotBilledRatedTransactions(Long walletOperationId) { 
		QueryBuilder qb = new QueryBuilder("from RatedTransaction c");
		qb.addCriterionEntity("c.walletOperationId", walletOperationId);
		qb.addCriterion("c.status", "!=", RatedTransactionStatusEnum.BILLED, false); 
		try {
			return (List<RatedTransaction>) qb.getQuery(getEntityManager())
					.getResultList();
		} catch (NoResultException e) {
			log.warn("error on get not billed rated transactions ",e);
			return null;
		}

	}



	@SuppressWarnings("unchecked")
	public List<RatedTransaction> getRatedTransactionsByBillingRun(BillingRun BillingRun) { 
		QueryBuilder qb = new QueryBuilder("from RatedTransaction c");
		qb.addCriterionEntity("c.billingRun", BillingRun); 
		try {
			return (List<RatedTransaction>) qb.getQuery(getEntityManager())
					.getResultList();
		} catch (NoResultException e) {
			log.warn("failed to get ratedTransactions ny nillingRun",e);
			return null;
		}

	}
	private void createInvoiceDiscountAggregates(User currentUser,UserAccount userAccount,Invoice invoice,Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap){
		try {

			BillingAccount billingAccount=userAccount.getBillingAccount();
			DiscountPlan discountPlan=billingAccount.getDiscountPlan();
			CustomerAccount customerAccount=billingAccount.getCustomerAccount();
			if(discountPlan!=null && discountPlan.isActive()){
				List<DiscountPlanItem> discountPlanItems =discountPlan.getDiscountPlanItems();
				for(DiscountPlanItem discountPlanItem:discountPlanItems){
					if(discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(),customerAccount, billingAccount, invoice)){
						if(discountPlanItem.getInvoiceSubCategory()!=null){
							createDiscountAggregate(currentUser,userAccount,userAccount.getWallet(), invoice, discountPlanItem.getInvoiceSubCategory(),discountPlanItem,taxInvoiceAgregateMap);
						}else if(discountPlanItem.getInvoiceCategory()!=null){
							InvoiceCategory invoiceCat=discountPlanItem.getInvoiceCategory();
							for(InvoiceSubCategory invoiceSubCat:invoiceCat.getInvoiceSubCategories()){
								createDiscountAggregate(currentUser,userAccount,userAccount.getWallet(), invoice, invoiceSubCat,discountPlanItem,taxInvoiceAgregateMap);
							}
						}
					}
				}
			}

		} catch (BusinessException e) {
			log.error("Error when trying to create discount aggregates",e);
		}

	}

	private void createDiscountAggregate(User currentUser,UserAccount userAccount,WalletInstance wallet,Invoice invoice,InvoiceSubCategory invoiceSubCat,DiscountPlanItem discountPlanItem,Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap) throws BusinessException{
		BillingAccount billingAccount=userAccount.getBillingAccount();
		BigDecimal amount=invoiceAgregateService.findTotalAmountByWalletSubCat(wallet, invoiceSubCat, wallet.getProvider(),invoice);
		if (amount!=null && !BigDecimal.ZERO.equals(amount)){
			BigDecimal discountAmountWithoutTax=amount.multiply(discountPlanItem.getPercent().divide(HUNDRED)).negate();
			List<Tax> taxes = new ArrayList<Tax>();
			for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCat
					.getInvoiceSubcategoryCountries()) {
				if (invoicesubcatCountry.getTradingCountry().getCountryCode()
						.equalsIgnoreCase(invoice.getBillingAccount().getTradingCountry().getCountryCode()) 
						&& invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(),billingAccount, invoice)) {
					taxes.add(invoicesubcatCountry.getTax());
				}
			}
			SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
			BigDecimal discountAmountTax=BigDecimal.ZERO;
			//TODO do this in the right place (one time by userAccount)			
			boolean isExonerated = billingAccountService.isExonerated(userAccount.getBillingAccount());			
			if(!isExonerated){
				for (Tax tax:taxes) {
					BigDecimal amountTax=discountAmountWithoutTax.multiply(tax.getPercent().divide(HUNDRED));
					discountAmountTax=discountAmountTax.add(amountTax);
					invoiceAgregateSubcat.addSubCategoryTax(tax);
					TaxInvoiceAgregate 	taxInvoiceAgregate= taxInvoiceAgregateMap.get(tax.getId());
					if(taxInvoiceAgregate!=null){
						taxInvoiceAgregate.addAmountTax(amountTax);
						taxInvoiceAgregate.addAmountWithoutTax(discountAmountWithoutTax);
						invoiceAgregateService.update(taxInvoiceAgregate,currentUser);
					}
				}
			}


			BigDecimal discountAmountWithTax=discountAmountWithoutTax.add(discountAmountTax);

			invoiceAgregateSubcat.setAuditable(billingAccount.getAuditable());
			invoiceAgregateSubcat.setProvider(billingAccount.getProvider());
			invoiceAgregateSubcat.setInvoice(invoice);
			invoiceAgregateSubcat.setBillingRun(billingAccount.getBillingRun());
			invoiceAgregateSubcat.setWallet(userAccount.getWallet());
			invoiceAgregateSubcat.setAccountingCode(invoiceSubCat.getAccountingCode());
			fillAgregates(invoiceAgregateSubcat, userAccount.getWallet());
			invoiceAgregateSubcat.setAmountWithoutTax(discountAmountWithoutTax);
			invoiceAgregateSubcat.setAmountWithTax(discountAmountWithTax);
			invoiceAgregateSubcat.setAmountTax(discountAmountTax);
			invoiceAgregateSubcat.setProvider(billingAccount.getProvider());
			invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCat);

			invoiceAgregateSubcat.setDiscountAggregate(true);
			invoiceAgregateSubcat.setDiscountPercent(discountPlanItem.getPercent());
			invoiceAgregateSubcat.setDiscountPlanCode(discountPlanItem.getDiscountPlan().getCode());
			invoiceAgregateSubcat.setDiscountPlanItemCode(discountPlanItem.getCode());
			invoiceAgregateService.create(invoiceAgregateSubcat,currentUser);

		}}
	


	private boolean matchDiscountPlanItemExpression(String expression,CustomerAccount customerAccount,BillingAccount billingAccount,Invoice invoice) throws BusinessException {
		Boolean result = true;


		if (StringUtils.isBlank(expression) ) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();


		if (expression.indexOf("ca") >= 0) {
			userMap.put("ca",customerAccount);
		}
		if (expression.indexOf("ba") >= 0) {
			userMap.put("ba", billingAccount);
		}
		if (expression.indexOf("iv") >= 0) {
			userMap.put("iv", invoice);

		}
		Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap,
				Boolean.class);
		try {
			result = (Boolean) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression
					+ " do not evaluate to boolean but " + res);
		}
		return result;
	}

	public List<RatedTransaction> getListByInvoiceAndSubCategory(Invoice invoice,InvoiceSubCategory invoiceSubCategory) {
		if (invoice == null || invoiceSubCategory == null) {
			return null;
		}
		return (List<RatedTransaction>)getEntityManager().createNamedQuery("RatedTransaction.getListByInvoiceAndSubCategory",RatedTransaction.class)
				.setParameter("invoice", invoice).setParameter("invoiceSubCategory", invoiceSubCategory).getResultList();
	}


	public void createRatedTransaction(Long walletOperationId,User currentUser )throws Exception{
		WalletOperation walletOperation = walletOperationService.findById(walletOperationId, currentUser.getProvider()) ;

		BigDecimal amountWithTAx = walletOperation.getAmountWithTax();
		BigDecimal amountTax = walletOperation.getAmountTax();
		BigDecimal unitAmountWithTax = walletOperation.getUnitAmountWithTax();
		BigDecimal unitAmountTax = walletOperation.getUnitAmountTax();

		/*if (walletOperation.getChargeInstance().getSubscription().getUserAccount().getBillingAccount()
						.getCustomerAccount().getCustomer().getCustomerCategory().getExoneratedFromTaxes()) {
					amountWithTAx = walletOperation.getAmountWithoutTax();
					amountTax = BigDecimal.ZERO;
					unitAmountWithTax = walletOperation.getUnitAmountWithoutTax();
					unitAmountTax = BigDecimal.ZERO;
				}*/
		RatedTransaction ratedTransaction = new RatedTransaction(walletOperation.getId(),
				walletOperation.getOperationDate(), walletOperation.getUnitAmountWithoutTax(),
				unitAmountWithTax, unitAmountTax, walletOperation.getQuantity(),
				walletOperation.getAmountWithoutTax(), amountWithTAx, amountTax,
				RatedTransactionStatusEnum.OPEN, walletOperation.getProvider(),
				walletOperation.getWallet(), walletOperation.getWallet().getUserAccount()
				.getBillingAccount(), walletOperation.getChargeInstance().getChargeTemplate()
				.getInvoiceSubCategory(), walletOperation.getParameter1(),
				walletOperation.getParameter2(), walletOperation.getParameter3(),
				walletOperation.getRatingUnitDescription(), walletOperation.getPriceplan(),
				walletOperation.getOfferCode(),walletOperation.getEdr());
		ratedTransaction.setDescription(walletOperation.getDescription());
		ratedTransaction.setCode(walletOperation.getCode());
		ratedTransaction.setUnityDescription(walletOperation.getChargeInstance().getChargeTemplate().getInputUnitDescription());
		
		create(ratedTransaction, currentUser);

		walletOperation.setStatus(WalletOperationStatusEnum.TREATED);
		walletOperation.updateAudit(currentUser);
		walletOperationService.updateNoCheck(walletOperation);
	}

	public void createRatedTransaction(Long billingAccountId,User currentUser,Date invoicingDate )throws Exception{
		BillingAccount billingAccount = billingAccountService.findById(billingAccountId, true);
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		List<WalletOperation> walletOps = new ArrayList<WalletOperation>();
		for(UserAccount ua:userAccounts){
			walletOps.addAll(walletOperationService.listToInvoiceByUserAccount(invoicingDate, currentUser.getProvider(), ua));
		}
		for(WalletOperation walletOp:walletOps){
			createRatedTransaction(walletOp.getId(),currentUser);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<RatedTransaction> listByInvoice(Invoice invoice) {
		QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "r", null, invoice.getProvider());
		qb.addCriterionEntity("invoice", invoice);

		try {
			return (List<RatedTransaction>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance , InvoiceSubCategory invoiceSubCategory ) {
		return openRTbySubCat(walletInstance, invoiceSubCategory, null, null);
	}
	@SuppressWarnings("unchecked")
	public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance , InvoiceSubCategory invoiceSubCategory ,Date from, Date to) {
		QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "rt", null,walletInstance.getProvider());
		if(invoiceSubCategory != null){
			qb.addCriterionEntity("rt.invoiceSubCategory", invoiceSubCategory);
		}
		qb.addCriterionEntity("rt.wallet", walletInstance);
		qb.addSql("rt.invoice is null");
		qb.addCriterionEnum("rt.status", RatedTransactionStatusEnum.OPEN);
		if(from != null){
			qb.addCriterion("usageDate", ">=", from, false);	
		}
		if(to != null){
			qb.addCriterion("usageDate", "<=", to, false);	
		}
				
		try {
			return (List<RatedTransaction>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
