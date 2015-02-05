/**
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.qualifier.Created;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;

@Stateless
public class WalletOperationService extends BusinessService<WalletOperation> {

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;

	@Inject
	private transient ResourceBundle resourceBundle;

	@Inject
	private RatingService chargeApplicationRatingService;

	private DateFormat sdf;
	private String str_tooPerceived = null;

	@Resource(name = "java:jboss/infinispan/container/meveo")
	private CacheContainer meveoContainer;

	// cache of the prepaid walletInstance balances
	private static BasicCache<Long, BigDecimal> balanceCache;

	// cache of the prepaid walletInstance reserved balances
	private static BasicCache<Long, BigDecimal> reservedBalanceCache;

	// cache the association between usage chargeInstance and wallets (if it is
	// not the only principal one)
	private static BasicCache<Long, List<Long>> usageChargeInstanceWallet;

	@PostConstruct
	private void init() {
		ParamBean paramBean = ParamBean.getInstance();
		sdf = new SimpleDateFormat(paramBean.getProperty("walletOperation.dateFormat", "dd/MM/yyyy"));
		str_tooPerceived = resourceBundle.getString("str_tooPerceived");
		if (balanceCache == null) {
			balanceCache = meveoContainer.getCache("meveo-balance");
		}
		if (reservedBalanceCache == null) {
			reservedBalanceCache = meveoContainer.getCache("meveo-reservedBalance");
		}
		if (usageChargeInstanceWallet == null) {
			usageChargeInstanceWallet = meveoContainer.getCache("meveo-usageChargeInstanceWallet");
		}
		if (balanceCache.size() == 0) {
			buildCacheFromDB();
		}
	}

	public void buildCacheFromDB() {
		// for each recrring usage charInstance of active subscription we create
		// association
		List<UsageChargeInstance> charges = (List<UsageChargeInstance>) getEntityManager().createNamedQuery(
				"UsageChargeInstance.listPrepaidActive", UsageChargeInstance.class).getResultList();
		for (UsageChargeInstance charge : charges) {
			updateCache(charge);
		}
		List<Long> walletIds = (List<Long>) getEntityManager().createNamedQuery(
				"WalletInstance.listPrepaidActiiveWalletIds", Long.class).getResultList();
		for (Long walletId : walletIds) {
			if (!balanceCache.containsKey(walletId)) {
				fillBalanceCaches(walletId);
			}
		}
	}

	public void fillBalanceCaches(Long walletId) {
		BigDecimal balance = (BigDecimal) getEntityManager()
				.createNamedQuery("WalletOperation.getBalance", BigDecimal.class).setParameter("walletId", walletId)
				.getSingleResult();
		if (balance == null) {
			balance = BigDecimal.ZERO;
		}
		balanceCache.put(walletId, balance);
		BigDecimal reservedBalance = (BigDecimal) getEntityManager()
				.createNamedQuery("WalletOperation.getReservedBalance", BigDecimal.class)
				.setParameter("walletId", walletId).getSingleResult();
		if (reservedBalance == null) {
			reservedBalance = BigDecimal.ZERO;
		}
		reservedBalanceCache.put(walletId, reservedBalance);
		log.debug("fillBalanceCaches walletId:{} balance:{} reservedBalance:{}", walletId, balance, reservedBalance);
	}

	public void updateBalanceCache(@Observes @Created WalletInstance wallet) {
		if (wallet.getWalletTemplate()!=null && wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
			fillBalanceCaches(wallet.getId());
		}
	}

	public void updateBalanceCache(@Observes @Created WalletOperation op) {
		// FIXME: handle reservation
		if (reservedBalanceCache.containsKey(op.getChargeInstance().getId())) {
			BigDecimal oldValue = reservedBalanceCache.get(op.getChargeInstance().getId());
			BigDecimal newValue = oldValue.subtract(op.getAmountWithTax());
			log.debug("update reservedBalance Cache {}->{}", oldValue, newValue);
			reservedBalanceCache.put(op.getChargeInstance().getId(), newValue);
			oldValue = balanceCache.get(op.getChargeInstance().getId());
			newValue = oldValue.subtract(op.getAmountWithTax());
			log.debug("update balance Cache {}->{}", oldValue, newValue);
			balanceCache.put(op.getChargeInstance().getId(), newValue);
			// FIXME: handle low balance notifications
		}
	}

	public void updateCache(@Observes @Created UsageChargeInstance charge) {
		// TODO:: make sure ordering is correct
		List<WalletInstance> wallets = charge.getWalletInstances();
		List<Long> walletIds = new ArrayList<>();
		log.debug("updateCache usageChargeInstanceWallet. wallets:{}", wallets == null ? "null" : wallets.size());
		for (WalletInstance wallet : wallets) {
			if (!walletIds.contains(wallet.getId())) {
				walletIds.add(wallet.getId());
				log.debug("updateCache walletId:{}", wallet.getId());
				if (!balanceCache.containsKey(wallet.getId())) {
					fillBalanceCaches(wallet.getId());
				}
			}
		}
		usageChargeInstanceWallet.put(charge.getId(), walletIds);
	}

	public BigDecimal getRatedAmount(Provider provider, Seller seller, Customer customer,
			CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
			Date endDate, boolean amountWithTax) {
		return getRatedAmount(getEntityManager(), provider, seller, customer, customerAccount, billingAccount,
				userAccount, startDate, endDate, amountWithTax);
	}

	public BigDecimal getRatedAmount(EntityManager em, Provider provider, Seller seller, Customer customer,
			CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
			Date endDate, boolean amountWithTax) {

		BigDecimal result = BigDecimal.ZERO;
		LevelEnum level = LevelEnum.PROVIDER;

		if (userAccount != null) {
			level = LevelEnum.USER_ACCOUNT;
			provider = userAccount.getProvider();
		} else if (billingAccount != null) {
			level = LevelEnum.BILLING_ACCOUNT;
			provider = billingAccount.getProvider();
		} else if (customerAccount != null) {
			level = LevelEnum.CUSTOMER_ACCOUNT;
			provider = customerAccount.getProvider();
		} else if (customer != null) {
			level = LevelEnum.CUSTOMER;
			provider = customer.getProvider();
		} else if (seller != null) {
			level = LevelEnum.SELLER;
			provider = seller.getProvider();
		}

		try {
			String strQuery = "select SUM(r." + (amountWithTax ? "amountWithTax" : "amountWithoutTax") + ") from "
					+ WalletOperation.class.getSimpleName() + " r "
					+ "WHERE r.operationDate>=:startDate AND r.operationDate<:endDate "
					+ "AND (r.status=:open OR r.status=:treated) " + "AND r.provider=:provider ";
			switch (level) {
			case BILLING_ACCOUNT:
				strQuery += "AND r.wallet.userAccount.billingAccount=:billingAccount ";
				break;
			case CUSTOMER:
				strQuery += "AND r.wallet.userAccount.billingAccount.customerAccount.customer=:customer ";
				break;
			case CUSTOMER_ACCOUNT:
				strQuery += "AND r.wallet.userAccount.billingAccount.customerAccount=:customerAccount ";
				break;
			case PROVIDER:
				break;
			case SELLER:
				strQuery += "AND r.wallet.userAccount.billingAccount.customerAccount.customer.seller=:seller ";
				break;
			case USER_ACCOUNT:
				strQuery += "AND r.wallet.userAccount=:userAccount ";
				break;
			default:
				break;
			}

			Query query = em.createQuery(strQuery);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			query.setParameter("provider", provider);
			query.setParameter("open", WalletOperationStatusEnum.OPEN);
			query.setParameter("treated", WalletOperationStatusEnum.TREATED);

			switch (level) {
			case BILLING_ACCOUNT:
				query.setParameter("billingAccount", billingAccount);
				break;
			case CUSTOMER:
				query.setParameter("customer", customer);
				break;
			case CUSTOMER_ACCOUNT:
				query.setParameter("customerAccount", customerAccount);
				break;
			case PROVIDER:
				break;
			case SELLER:
				query.setParameter("seller", seller);
				break;
			case USER_ACCOUNT:
				query.setParameter("userAccount", userAccount);
				break;
			default:
				break;
			}

			result = (BigDecimal) query.getSingleResult();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		if (result == null)
			result = BigDecimal.ZERO;
		return result;
	}

	/**
	 * Get wallet operation balance.
	 * 
	 * @param em
	 * @param provider
	 * @param seller
	 * @param customer
	 * @param customerAccount
	 * @param billingAccount
	 * @param userAccount
	 * @param startDate
	 * @param endDate
	 * @param amountWithTax
	 * @param mode
	 *            : 1 - current (OPEN or RESERVED), 2 - reserved (RESERVED), 3 -
	 *            open (OPEN)
	 * @return
	 */
	public BigDecimal getBalanceAmount(Provider provider, Seller seller, Customer customer,
			CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
			Date endDate, boolean amountWithTax, int mode) {

		BigDecimal result = BigDecimal.ZERO;
		LevelEnum level = LevelEnum.PROVIDER;

		if (userAccount != null) {
			level = LevelEnum.USER_ACCOUNT;
			provider = userAccount.getProvider();
		} else if (billingAccount != null) {
			level = LevelEnum.BILLING_ACCOUNT;
			provider = billingAccount.getProvider();
		} else if (customerAccount != null) {
			level = LevelEnum.CUSTOMER_ACCOUNT;
			provider = customerAccount.getProvider();
		} else if (customer != null) {
			level = LevelEnum.CUSTOMER;
			provider = customer.getProvider();
		} else if (seller != null) {
			level = LevelEnum.SELLER;
			provider = seller.getProvider();
		}

		try {
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select SUM(r." + (amountWithTax ? "amountWithTax" : "amountWithoutTax") + ") from "
					+ WalletOperation.class.getSimpleName() + " r "
					+ "WHERE r.operationDate>=:startDate AND r.operationDate<:endDate ");

			if (mode == 1) {
				strQuery.append("AND (r.status=:open OR r.status=:reserved) ");
			} else if (mode == 2) {
				strQuery.append("AND (r.status=:reserved) ");
			} else if (mode == 3) {
				strQuery.append("AND (r.status=:open) ");
			}

			// + "AND (r.status=:open OR r.status=:treated) "
			strQuery.append("AND r.provider=:provider ");
			switch (level) {
			case BILLING_ACCOUNT:
				strQuery.append("AND r.wallet.userAccount.billingAccount=:billingAccount ");
				break;
			case CUSTOMER:
				strQuery.append("AND r.wallet.userAccount.billingAccount.customerAccount.customer=:customer ");
				break;
			case CUSTOMER_ACCOUNT:
				strQuery.append("AND r.wallet.userAccount.billingAccount.customerAccount=:customerAccount ");
				break;
			case PROVIDER:
				break;
			case SELLER:
				strQuery.append("AND r.wallet.userAccount.billingAccount.customerAccount.customer.seller=:seller ");
				break;
			case USER_ACCOUNT:
				strQuery.append("AND r.wallet.userAccount=:userAccount ");
				break;
			default:
				break;
			}

			Query query = getEntityManager().createQuery(strQuery.toString());

			if (mode == 1) {
				query.setParameter("open", WalletOperationStatusEnum.OPEN);
				query.setParameter("reserved", WalletOperationStatusEnum.RESERVED);
			} else if (mode == 2) {
				query.setParameter("reserved", WalletOperationStatusEnum.RESERVED);
			} else if (mode == 3) {
				query.setParameter("open", WalletOperationStatusEnum.OPEN);
			}

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			query.setParameter("provider", provider);

			switch (level) {
			case BILLING_ACCOUNT:
				query.setParameter("billingAccount", billingAccount);
				break;
			case CUSTOMER:
				query.setParameter("customer", customer);
				break;
			case CUSTOMER_ACCOUNT:
				query.setParameter("customerAccount", customerAccount);
				break;
			case PROVIDER:
				break;
			case SELLER:
				query.setParameter("seller", seller);
				break;
			case USER_ACCOUNT:
				query.setParameter("userAccount", userAccount);
				break;
			default:
				break;
			}

			result = (BigDecimal) query.getSingleResult();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		if (result == null)
			result = BigDecimal.ZERO;
		return result;
	}

	public void usageWalletOperation(Subscription subscription, Date usageDate, BigDecimal quantity, String param1,
			String param2, String param3) {

	}

	/*
	 * public WalletOperation rateOneShotApplication(Subscription subscription,
	 * OneShotChargeInstance chargeInstance, Integer quantity, Date
	 * applicationDate) throws BusinessException { return
	 * rateOneShotApplication(getEntityManager(), subscription, chargeInstance,
	 * quantity, applicationDate, getCurrentUser()); }
	 */

	public WalletOperation rateOneShotApplication(EntityManager em, Subscription subscription,
			OneShotChargeInstance chargeInstance, Integer quantity, Date applicationDate, User creator)
			throws BusinessException {

		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
		if (chargeTemplate == null) {
			throw new IncorrectChargeTemplateException("ChargeTemplate is null for chargeInstance id="
					+ chargeInstance.getId() + ", code=" + chargeInstance.getCode());
		}

		InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code="
					+ chargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ subscription.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, creator.getProvider());

		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
							+ " and trading country=" + country.getCountryCode() + ".");
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id="
					+ invoiceSubcategoryCountry.getId());
		}

		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(em, chargeTemplate
				.getCode(), subscription, chargeInstance, ApplicationTypeEnum.PUNCTUAL, applicationDate, chargeInstance
				.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), quantity == null ? null : new BigDecimal(
				quantity), currency, countryId, tax.getPercent(), null, null, invoiceSubCategory, chargeInstance
				.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), null, null, null);

		return chargeApplication;
	}

	public void oneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance,
			Integer quantity, Date applicationDate, User creator) throws BusinessException {
		oneShotWalletOperation(getEntityManager(), subscription, chargeInstance, quantity, applicationDate, creator);
	}

	public void oneShotWalletOperation(EntityManager em, Subscription subscription,
			OneShotChargeInstance chargeInstance, Integer quantity, Date applicationDate, User creator)
			throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		if (applicationDate == null) {
			applicationDate = new Date();
		}

		log.debug(
				"WalletOperationService.oneShotWalletOperation subscriptionCode={}, quantity={}, applicationDate={}, chargeInstance.getId={}",
				new Object[] { subscription.getId(), quantity, applicationDate, chargeInstance.getId() });

		WalletOperation chargeApplication = rateOneShotApplication(em, subscription, chargeInstance, quantity,
				applicationDate, creator);
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

		chargeWalletOpertation(chargeApplication, creator, chargeInstance.getProvider());
		// create(chargeApplication, creator, chargeInstance.getProvider());
		OneShotChargeTemplate oneShotChargeTemplate = null;

		if (chargeTemplate instanceof OneShotChargeTemplate) {
			oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
		} else {
			oneShotChargeTemplate = oneShotChargeTemplateService.findById(em, chargeTemplate.getId());
		}

		Boolean immediateInvoicing = (oneShotChargeTemplate != null && oneShotChargeTemplate.getImmediateInvoicing() != null) ? oneShotChargeTemplate
				.getImmediateInvoicing() : false;

		if (immediateInvoicing != null && immediateInvoicing) {
			BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
			int delay = billingAccount.getBillingCycle().getInvoiceDateDelay();
			Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(), -delay);
			nextInvoiceDate = DateUtils.parseDateWithPattern(nextInvoiceDate, "dd/MM/yyyy");
			applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

			if (applicationDate.after(nextInvoiceDate)) {
				billingAccount.setNextInvoiceDate(applicationDate);
				billingAccountService.setProvider(creator.getProvider());
				billingAccountService.update(billingAccount, creator);
			}
		}
	}

	public void recurringWalletOperation(Subscription subscription, RecurringChargeInstance chargeInstance,
			Integer quantity, Date applicationDate, User creator) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("ChargeApplicationService.recurringChargeApplication subscriptionCode={},quantity={},"
				+ "applicationDate={},chargeInstance.getId={}", subscription.getId(), quantity, applicationDate,
				chargeInstance.getId());
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
		if (chargeTemplate == null) {
			throw new IncorrectChargeTemplateException("chargeTemplate is null for chargeInstance id="
					+ chargeInstance.getId() + ", code=" + chargeInstance.getCode());
		}
		InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
					+ chargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ subscription.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}
		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
							+ " and trading country=" + country.getCountryCode());
		}
		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id="
					+ invoiceSubcategoryCountry.getId());
		}

		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeTemplate
				.getCode(), subscription, chargeInstance, ApplicationTypeEnum.PUNCTUAL, applicationDate, chargeInstance
				.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), quantity == null ? null : new BigDecimal(
				quantity), currency, countryId, tax.getPercent(), null, null, invoiceSubCategory, chargeInstance
				.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), null, null, null);

		chargeWalletOpertation(chargeApplication, creator, chargeInstance.getProvider());
		// create(chargeApplication, creator, chargeInstance.getProvider());
	}

	public Date getNextApplicationDate(RecurringChargeInstance chargeInstance) {
		Date applicationDate = chargeInstance.getSubscriptionDate();
		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		Calendar cal = recurringChargeTemplate.getCalendar();
		if (cal.truncDateTime()) {
			applicationDate = DateUtils.parseDateWithPattern(chargeInstance.getSubscriptionDate(), "dd/MM/yyyy");
		}
		chargeInstance.setChargeDate(applicationDate);
		Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
		}
		return nextapplicationDate;
	}

	public WalletOperation prerateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance,
			Date nextapplicationDate) throws BusinessException {
		return rateSubscription(subscriptionDate, chargeInstance, nextapplicationDate);
	}

	public WalletOperation rateSubscription(RecurringChargeInstance chargeInstance, Date nextapplicationDate)
			throws BusinessException {
		return rateSubscription(getEntityManager(), chargeInstance, nextapplicationDate);
	}

	public WalletOperation rateSubscription(EntityManager em, RecurringChargeInstance chargeInstance,
			Date nextapplicationDate) throws BusinessException {
		return rateSubscription(em, null, chargeInstance, nextapplicationDate);
	}

	public WalletOperation rateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance,
			Date nextapplicationDate) throws BusinessException {
		return rateSubscription(getEntityManager(), subscriptionDate, chargeInstance, nextapplicationDate);
	}

	public WalletOperation rateSubscription(EntityManager em, Date subscriptionDate,
			RecurringChargeInstance chargeInstance, Date nextapplicationDate) throws BusinessException {
		WalletOperation result = null;
		Date applicationDate = chargeInstance.getChargeDate();

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(subscriptionDate);
		Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
		}
		log.debug("rateSubscription applicationDate={}, nextapplicationDate={},previousapplicationDate={}",
				applicationDate, nextapplicationDate, previousapplicationDate);

		BigDecimal quantity = (chargeInstance.getServiceInstance() == null || chargeInstance.getServiceInstance()
				.getQuantity() == null) ? BigDecimal.ONE : new BigDecimal(chargeInstance.getServiceInstance()
				.getQuantity());
		if (Boolean.TRUE.equals(recurringChargeTemplate.getSubscriptionProrata())) {
			Date periodStart = applicationDate;
			double prorataRatio = 1.0;
			double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
			double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);
			if (part2 > 0) {
				prorataRatio = part1 / part2;
			} else {
				log.error("Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}",
						nextapplicationDate, previousapplicationDate);
			}

			quantity = quantity.multiply(new BigDecimal(prorataRatio).setScale(BaseEntity.NB_DECIMALS,
					RoundingMode.HALF_UP));
			log.debug("rateSubscription part1={}, part2={}, prorataRation={} -> quantity={}", new Object[] { part1,
					part2, prorataRatio, quantity });
		}

		String param2 = " " + sdf.format(applicationDate) + " au "
				+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
		log.debug("param2={}", param2);

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
					+ recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
							.getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(em, invoiceSubCategory.getId(), countryId, getCurrentProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
							+ " and trading country=" + country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id="
					+ invoiceSubcategoryCountry.getId());
		}

		if (!recurringChargeTemplate.getApplyInAdvance()) {
			applicationDate = nextapplicationDate;
		}
		if (subscriptionDate == null) {
			result = chargeApplicationRatingService.rateChargeApplication(em, chargeInstance.getCode(), chargeInstance
					.getServiceInstance().getSubscription(), chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION,
					applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), quantity,
					currency, countryId, tax.getPercent(), null, nextapplicationDate, recurringChargeTemplate
							.getInvoiceSubCategory(), chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
					chargeInstance.getCriteria3(), applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1),
					null);
		} else {
			result = chargeApplicationRatingService.prerateChargeApplication(em, chargeInstance.getCode(),
					subscriptionDate, chargeInstance.getServiceInstance().getSubscription().getOffer().getCode(),
					chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate,
					chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), quantity, currency,
					countryId, tax.getPercent(), null, nextapplicationDate,
					recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(),
					chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), applicationDate,
					DateUtils.addDaysToDate(nextapplicationDate, -1), null);
		}
		return result;
	}

	public void chargeSubscription(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {
		chargeSubscription(getEntityManager(), chargeInstance, creator);
	}

	public void chargeSubscription(EntityManager em, RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug(
				"ChargeApplicationService.chargeSubscription subscriptionCode={},chargeCode={},quantity={},"
						+ "applicationDate={},chargeInstance.getId={}",
				new Object[] { chargeInstance.getServiceInstance().getSubscription().getCode(),
						chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(),
						chargeInstance.getSubscriptionDate(), chargeInstance.getId() });

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		Date nextapplicationDate = getNextApplicationDate(chargeInstance);

		if (recurringChargeTemplate.getApplyInAdvance() != null && recurringChargeTemplate.getApplyInAdvance()) {
			WalletOperation chargeApplication = rateSubscription(em, chargeInstance, nextapplicationDate);
			create(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setNextChargeDate(nextapplicationDate);
		} else {
			chargeInstance.setNextChargeDate(nextapplicationDate);
		}

	}

	public void applyReimbursment(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {
		applyReimbursment(getEntityManager(), chargeInstance, creator);
	}

	public void applyReimbursment(EntityManager em, RecurringChargeInstance chargeInstance, User creator)
			throws BusinessException {
		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("applyReimbursment subscriptionCode={},chargeCode={},quantity={},"
				+ "applicationDate={},chargeInstance.getId={},NextChargeDate={}", chargeInstance.getServiceInstance()
				.getSubscription().getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance()
				.getQuantity(), chargeInstance.getSubscriptionDate(), chargeInstance.getId(), chargeInstance
				.getNextChargeDate());

		Date applicationDate = chargeInstance.getTerminationDate();
		applicationDate = DateUtils.addDaysToDate(applicationDate, 1);

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		if (recurringChargeTemplate.getCalendar() == null) {
			throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code="
					+ recurringChargeTemplate.getCode());
		}
		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
		if (cal.truncDateTime()) {
			applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");
		}

		BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
				: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());

		Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
		}
		Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
		}
		log.debug("applicationDate={}, nextapplicationDate={},previousapplicationDate={}", applicationDate,
				nextapplicationDate, previousapplicationDate);

		Date periodStart = applicationDate;
		if (recurringChargeTemplate.getTerminationProrata()) {

			double prorataRatio = 1.0;
			double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
			double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);

			if (part2 > 0) {
				prorataRatio = (-1) * part1 / part2;
			} else {
				log.error("Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}",
						nextapplicationDate, previousapplicationDate);
			}

			// FIXME i18n
			String param2 = " " + str_tooPerceived + " " + sdf.format(periodStart) + " / "
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			quantity = quantity.multiply(new BigDecimal(prorataRatio + "").setScale(BaseEntity.NB_DECIMALS,
					RoundingMode.HALF_UP));
			log.debug("part1={}, part2={}, prorataRatio={}, param2={} -> quantity={}", part1, part2, prorataRatio,
					param2, quantity);

			InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
			if (invoiceSubCategory == null) {
				throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
						+ recurringChargeTemplate.getCode());
			}

			TradingCurrency currency = chargeInstance.getCurrency();
			if (currency == null) {
				throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
						+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
								.getId());
			}

			TradingCountry country = chargeInstance.getCountry();
			if (country == null) {
				throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
						+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
			}
			Long countryId = country.getId();

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
					.findInvoiceSubCategoryCountry(em, invoiceSubCategory.getId(), countryId, creator.getProvider());
			if (invoiceSubcategoryCountry == null) {
				throw new IncorrectChargeTemplateException(
						"no invoiceSubcategoryCountry exists for invoiceSubCategory code="
								+ invoiceSubCategory.getCode() + " and trading country=" + country.getCountryCode());
			}

			Tax tax = invoiceSubcategoryCountry.getTax();
			if (tax == null) {
				throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id="
						+ invoiceSubcategoryCountry.getId());
			}

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(em,
					chargeInstance.getCode(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
					ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate, chargeInstance.getAmountWithoutTax(),
					chargeInstance.getAmountWithTax(), quantity, currency, countryId, tax.getPercent(), null,
					nextapplicationDate, invoiceSubCategory, chargeInstance.getCriteria1(),
					chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), periodStart,
					DateUtils.addDaysToDate(nextapplicationDate, -1), ChargeApplicationModeEnum.REIMBURSMENT);

			chargeWalletOpertation(chargeApplication, creator, chargeInstance.getProvider());
			// create(chargeApplication, creator, chargeInstance.getProvider());
		}

		if (recurringChargeTemplate.getApplyInAdvance()) {
			Date nextChargeDate = chargeInstance.getNextChargeDate();
			log.debug("reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}",
					applicationDate, nextapplicationDate, nextChargeDate);

			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyReccuringCharge(em, chargeInstance, true, recurringChargeTemplate, creator);
			}
		} else {
			Date nextChargeDate = chargeInstance.getChargeDate();
			log.debug("reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}",
					applicationDate, nextapplicationDate, nextChargeDate);

			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyNotAppliedinAdvanceReccuringCharge(em, chargeInstance, true, recurringChargeTemplate, creator);
			}
		}
	}

	public void applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {
		applyReccuringCharge(getEntityManager(), chargeInstance, reimbursement, recurringChargeTemplate, creator);
	}

	/**
	 * Apply the charge at its nextChargeDate.
	 * 
	 * @param em
	 * @param chargeInstance
	 * @param reimbursement
	 * @param recurringChargeTemplate
	 * @param creator
	 * @throws BusinessException
	 */
	public void applyReccuringCharge(EntityManager em, RecurringChargeInstance chargeInstance, boolean reimbursement,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

		Date applicationDate = chargeInstance.getNextChargeDate();

		if (reimbursement) {
			applicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
					chargeInstance.getTerminationDate());
		}

		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("nextChargeDate is null.");
		}

		Date nextApplicationDate = reimbursement ? chargeInstance.getNextChargeDate() : recurringChargeTemplate
				.getCalendar().nextCalendarDate(applicationDate);

		log.debug("reimbursement={}, applicationDate={}", reimbursement, applicationDate);

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
					+ recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
							.getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
							+ " and trading country=" + country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id="
					+ invoiceSubcategoryCountry.getId());
		}

		while (applicationDate.getTime() < nextApplicationDate.getTime()) {
			Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
			log.debug("next step for {}, applicationDate={}, nextApplicationDate={},nextApplicationDate={}",
					chargeInstance.getId(), applicationDate, nextapplicationDate, nextApplicationDate);

			String param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate)
					+ (reimbursement ? " / " : " au ") + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			if (reimbursement) {
				quantity = quantity.negate();
			}
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={} -> quantity={}", nextapplicationDate,
					param2, quantity);

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance
					.getCode(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
					reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT,
					applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), quantity,
					currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory,
					chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
					applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1),
					reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION);
			chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

			chargeWalletOpertation(chargeApplication, creator, chargeInstance.getProvider());
			// create(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}

		chargeInstance.setNextChargeDate(nextApplicationDate);
	}

	public void applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {
		applyNotAppliedinAdvanceReccuringCharge(getEntityManager(), chargeInstance, reimbursement,
				recurringChargeTemplate, creator);
	}

	public void applyNotAppliedinAdvanceReccuringCharge(EntityManager em, RecurringChargeInstance chargeInstance,
			boolean reimbursement, RecurringChargeTemplate recurringChargeTemplate, User creator)
			throws BusinessException {

		Date applicationDate = chargeInstance.getChargeDate();
		Calendar cal =recurringChargeTemplate.getCalendar();
		if(chargeInstance.getServiceInstance()!=null){
			cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
		}

		if (reimbursement) {
			applicationDate = cal.nextCalendarDate(chargeInstance.getTerminationDate());
		}

		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("ChargeDate is null.");
		}

		Date nextChargeDate = reimbursement ? chargeInstance.getChargeDate() : chargeInstance.getNextChargeDate();

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code="
					+ recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
							.getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
							+ " and trading country=" + country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("Tax is null for invoiceSubCategoryCountry id="
					+ invoiceSubcategoryCountry.getId());
		}

		while (applicationDate.getTime() < nextChargeDate.getTime()) {
			Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
			log.debug(
					"ApplyNotAppliedinAdvanceReccuringCharge next step for {}, applicationDate={}, nextApplicationDate={},nextApplicationDate={}",
					chargeInstance.getId(), applicationDate, nextapplicationDate, nextChargeDate);

			Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
			previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
			log.debug(
					"ApplyNotAppliedinAdvanceReccuringCharge applicationDate={}, nextapplicationDate={},previousapplicationDate={}",
					applicationDate, nextapplicationDate, previousapplicationDate);

			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			ApplicationTypeEnum applicationTypeEnum = ApplicationTypeEnum.RECURRENT;
			Date periodStart = applicationDate;
			// n'appliquer le prorata que dans le cas de la 1ere application de
			// charges echues
			log.debug("ApplyNotAppliedinAdvanceReccuringCharge chargeInstance.getWalletOperations().size()={}",
					chargeInstance.getWalletOperations().size());
			if (chargeInstance.getWalletOperations().size() == 0 && recurringChargeTemplate.getSubscriptionProrata()) {
				applicationTypeEnum = ApplicationTypeEnum.PRORATA_SUBSCRIPTION;
				double prorataRatio = 1.0;
				double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
				double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);

				if (part2 > 0) {
					prorataRatio = part1 / part2;
				} else {
					log.error(
							"ApplyNotAppliedinAdvanceReccuringCharge Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}",
							nextapplicationDate, previousapplicationDate);
				}
				quantity = quantity.multiply(new BigDecimal(prorataRatio + "").setScale(BaseEntity.NB_DECIMALS,
						RoundingMode.HALF_UP));
				log.debug("part1={}, part2={}, prorataRatio={} -> quantity", part1, part2, prorataRatio, quantity);
			}

			String param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate)
					+ (reimbursement ? " / " : " au ") + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			log.debug("param2={}", param2);

			log.debug("ApplyNotAppliedinAdvanceReccuringCharge : nextapplicationDate={}, param2={}",
					nextapplicationDate, param2);

			if (reimbursement) {
				quantity = quantity.negate();
			}

			WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance
					.getCode(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
					reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : applicationTypeEnum, applicationDate,
					chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), quantity, currency,
					countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory, chargeInstance
							.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
					applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1),
					reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION);
			walletOperation.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

			List<WalletOperation> oprations = chargeWalletOpertation(walletOperation, creator,
					chargeInstance.getProvider());
			// create(walletOperation, creator, chargeInstance.getProvider());
			// em.flush();
			// em.refresh(chargeInstance);
			chargeInstance.setChargeDate(applicationDate);
			chargeInstance.getWalletOperations().addAll(oprations);
			if (!em.contains(walletOperation)) {
				log.error("wtf wallet operation is already detached");
			}
			if (!em.contains(chargeInstance)) {
				log.error("wow chargeInstance is detached");
				em.merge(chargeInstance);
			}
			applicationDate = nextapplicationDate;
		}

		Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
		chargeInstance.setNextChargeDate(nextapplicationDate);
		chargeInstance.setChargeDate(applicationDate);
	}

	public void applyChargeAgreement(RecurringChargeInstance chargeInstance,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {
		applyChargeAgreement(getEntityManager(), chargeInstance, recurringChargeTemplate, creator);
	}

	public void applyChargeAgreement(EntityManager em, RecurringChargeInstance chargeInstance,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

		// we apply the charge at its nextChargeDate
		Date applicationDate = chargeInstance.getNextChargeDate();
		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("nextChargeDate is null.");
		}

		Date endAgreementDate = chargeInstance.getServiceInstance().getEndAgrementDate();

		if (endAgreementDate == null) {
			return;
		}

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
					+ recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount()
							.getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(em, invoiceSubCategory.getId(), countryId, creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException(
					"no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
							+ " and trading country=" + country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("tax is null for invoiceSubcategoryCountry id="
					+ invoiceSubcategoryCountry.getId());
		}
		while (applicationDate.getTime() < endAgreementDate.getTime()) {
			Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
			log.debug("agreement next step for {}, applicationDate={}, nextApplicationDate={}",
					recurringChargeTemplate.getCode(), applicationDate, nextapplicationDate);
			Double prorataRatio = null;
			ApplicationTypeEnum type = ApplicationTypeEnum.RECURRENT;
			Date endDate = DateUtils.addDaysToDate(nextapplicationDate, -1);
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity() == null ? BigDecimal.ONE
					: new BigDecimal(chargeInstance.getServiceInstance().getQuantity());
			if (nextapplicationDate.getTime() > endAgreementDate.getTime()
					&& applicationDate.getTime() < endAgreementDate.getTime()) {
				Date endAgreementDateModified = DateUtils.addDaysToDate(endAgreementDate, 1);

				double part1 = endAgreementDateModified.getTime() - applicationDate.getTime();
				double part2 = nextapplicationDate.getTime() - applicationDate.getTime();
				if (part2 > 0) {
					prorataRatio = part1 / part2;
				}

				nextapplicationDate = endAgreementDate;
				endDate = nextapplicationDate;
				if (recurringChargeTemplate.getTerminationProrata()) {
					type = ApplicationTypeEnum.PRORATA_TERMINATION;
					quantity = quantity.multiply(new BigDecimal(prorataRatio + "").setScale(BaseEntity.NB_DECIMALS,
							RoundingMode.HALF_UP));
				}
			}
			String param2 = sdf.format(applicationDate) + " au " + sdf.format(endDate);
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={}", nextapplicationDate, param2);

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(
					chargeInstance.getCode(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
					type, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(),
					quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory,
					chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
					applicationDate, endDate, ChargeApplicationModeEnum.AGREEMENT);

			chargeWalletOpertation(chargeApplication, creator, chargeInstance.getProvider());
			// create(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}
	}

	public List<WalletOperation> findByStatus(WalletOperationStatusEnum status, Provider provider) {
		return findByStatus(getEntityManager(), status, provider);
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findByStatus(EntityManager em, WalletOperationStatusEnum status, Provider provider) {
		List<WalletOperation> walletOperations = null;
		try {
			log.debug("start of find {} by status (status={})) ..", "WalletOperation", status);
			QueryBuilder qb = new QueryBuilder(WalletOperation.class, "c");
			qb.addCriterion("c.status", "=", status, true);
			qb.addCriterionEntity("c.provider", provider);

			walletOperations = qb.getQuery(em).getResultList();
			log.debug("end of find {} by status (status={}). Result size found={}.", new Object[] { "WalletOperation",
					status, walletOperations != null ? walletOperations.size() : 0 });

		} catch (Exception e) {
			log.error("findByStatus error={} ", e.getMessage());
		}
		return walletOperations;
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> listByChargeInstance(ChargeInstance chargeInstance) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "c");
		qb.addCriterionEntity("chargeInstance", chargeInstance);

		try {
			return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	public WalletOperation findByUserAccountAndCode(String code, UserAccount userAccount, Provider provider) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w");
		qb.addCriterionEntity("wallet.userAccount", userAccount);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("code", "=", code, true);

		try {
			return (WalletOperation) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findByUserAccountAndWalletCode(String walletCode, UserAccount userAccount, Provider provider) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w");
		qb.addCriterionEntity("wallet.userAccount", userAccount);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("wallet.code", "=", walletCode, true);
		try {
			return (List<WalletOperation>)  qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}
	// charging
	public List<WalletOperation> chargeOnWalletIds(List<Long> walletIds, WalletOperation op, User creator,
			Provider provider) throws BusinessException {
		List<WalletOperation> result = new ArrayList<>();
		BigDecimal remainingAmountToCharge = op.getAmountWithTax();
		BigDecimal totalBalance = reservedBalanceCache.get(walletIds.get(0));
		if (walletIds.size() > 1) {
			for (int i = 1; i < walletIds.size(); i++) {
				totalBalance = totalBalance.add(reservedBalanceCache.get(walletIds.get(i)));
			}
		}
		log.debug("chargeOnWalletIds remainingAmountToCharge={}, totalBalance={}", remainingAmountToCharge,
				totalBalance);
		if (remainingAmountToCharge.compareTo(totalBalance) > 0
				&& reservedBalanceCache.containsKey(walletIds.get(walletIds.size() - 1))) {
			throw new BusinessException("INSUFFICIENT_BALANCE");
		}
		for (Long walletId : walletIds) {
			BigDecimal balance = reservedBalanceCache.get(walletId);
			log.debug("chargeOnWalletIds walletId={}, balance={}", walletId, balance);
			if (balance.compareTo(BigDecimal.ZERO) > 0) {
				if (balance.compareTo(op.getAmountWithTax()) >= 0) {
					op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
					create(op, creator, provider);
					result.add(op);
					break;
				} else {
					BigDecimal newOverOldCoeff = balance.divide(op.getAmountWithTax());
					remainingAmountToCharge = remainingAmountToCharge.subtract(balance);
					BigDecimal newOpAmountWithTax = balance;
					BigDecimal newOpAmountTax = op.getAmountTax().multiply(newOverOldCoeff);
					BigDecimal newOpAmountWithoutTax = newOpAmountWithTax.subtract(newOpAmountTax);
					BigDecimal newOpQuantity = op.getQuantity().multiply(newOverOldCoeff);

					BigDecimal opAmountWithTax = remainingAmountToCharge;
					BigDecimal opAmountTax = op.getAmountTax().subtract(newOpAmountTax);
					BigDecimal opAmountWithoutTax = opAmountWithTax.subtract(opAmountTax);
					BigDecimal opQuantity = op.getQuantity().subtract(newOpQuantity);

					WalletOperation newOp = op.getUnratedClone();
					newOp.setWallet(getEntityManager().find(WalletInstance.class, walletId));
					newOp.setAmountWithTax(newOpAmountWithTax);
					newOp.setAmountTax(newOpAmountTax);
					newOp.setAmountWithoutTax(newOpAmountWithoutTax);
					newOp.setQuantity(newOpQuantity);
					create(newOp, creator, provider);
					result.add(newOp);

					op.setAmountWithTax(opAmountWithTax);
					op.setAmountTax(opAmountTax);
					op.setAmountWithoutTax(opAmountWithoutTax);
					op.setQuantity(opQuantity);
				}
			}
		}
		return result;
	}

	public List<WalletOperation> chargeWalletOpertation(WalletOperation op, User creator, Provider provider)
			throws BusinessException {
		List<WalletOperation> result = new ArrayList<>();
		if (op.getWallet() == null) {
			log.debug("we dont charge operations not associated to wallet");
			return result;
		}
		log.debug("chargeWalletOpertation on chargeInstanceId:{}", op.getChargeInstance().getId());
		if (usageChargeInstanceWallet.containsKey(op.getChargeInstance().getId())) {
			List<Long> walletIds = usageChargeInstanceWallet.get(op.getChargeInstance().getId());
			log.debug("chargeWalletOpertation chargeInstanceId found in usageCache with {} wallet ids",
					walletIds.size());
			result = chargeOnWalletIds(walletIds, op, creator, provider);
		} else if (op.getChargeInstance().isPrepaid()
				&& (op.getChargeInstance() instanceof RecurringChargeInstance || op.getChargeInstance() instanceof OneShotChargeInstance)) {
			List<Long> walletIds = new ArrayList<>();
			for (WalletInstance wallet : op.getChargeInstance().getWalletInstances()) {
				walletIds.add(wallet.getId());
			}
			log.debug("chargeWalletOpertation is recurring or oneshot, and associated to {} wallet ids",
					walletIds.size());
			result = chargeOnWalletIds(walletIds, op, creator, provider);
		} else if (!op.getChargeInstance().isPrepaid()) {
			log.debug("chargeWalletOpertation is postpaid");
			result.add(op);
			create(op, creator, provider);
		} else {
			log.error("chargeWalletOpertation wallet not found for chargeInstance {} ", op.getChargeInstance().getId());
			throw new BusinessException("WALLET_NOT_FOUND");
		}
		return result;
	}
}
