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
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.util.NumberUtil;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
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
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LevelEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.slf4j.Logger;

@Stateless
public class WalletOperationService extends BusinessService<WalletOperation> {

	@Inject
	private Logger log;

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

	@Inject
	private WalletCacheContainerProvider walletCacheContainerProvider;

	private DateFormat sdf;
	private String str_tooPerceived = null;

	@PostConstruct
	private void init() {
		ParamBean paramBean = ParamBean.getInstance();
		sdf = new SimpleDateFormat(paramBean.getProperty("walletOperation.dateFormat", "dd/MM/yyyy"));
		str_tooPerceived = resourceBundle.getString("str_tooPerceived");
	}

	public BigDecimal getRatedAmount(Provider provider, Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount,
			Date startDate, Date endDate, boolean amountWithTax) {
		return getRatedAmount(getEntityManager(), provider, seller, customer, customerAccount, billingAccount, userAccount, startDate, endDate, amountWithTax);
	}

	public BigDecimal getRatedAmount(EntityManager em, Provider provider, Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount,
			UserAccount userAccount, Date startDate, Date endDate, boolean amountWithTax) {

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
			String strQuery = "select SUM(r." + (amountWithTax ? "amountWithTax" : "amountWithoutTax") + ") from " + WalletOperation.class.getSimpleName() + " r "
					+ "WHERE r.operationDate>=:startDate AND r.operationDate<:endDate " + "AND (r.status=:open OR r.status=:treated) " + "AND r.provider=:provider ";
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
			log.error("failed to get Rated Amount",e);
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
	public BigDecimal getBalanceAmount(Provider provider, Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount,
			UserAccount userAccount, Date startDate, Date endDate, boolean amountWithTax, int mode) {

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
			strQuery.append("select SUM(r." + (amountWithTax ? "amountWithTax" : "amountWithoutTax") + ") from " + WalletOperation.class.getSimpleName() + " r "
					+ "WHERE r.provider=:provider ");

			if (startDate != null) {
				strQuery.append("AND r.operationDate>=:startDate ");
			}
			if (endDate != null) {
				strQuery.append("AND r.operationDate<:endDate ");
			}
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
			if (startDate != null) {
				query.setParameter("startDate", startDate);
			}
			if (endDate != null) {
				query.setParameter("endDate", endDate);
			}
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
			log.error("failed to get balance amount ",e);
		}

		if (result == null)
			result = BigDecimal.ZERO;
		return result;
	}

	/*
	 * public WalletOperation rateOneShotApplication(Subscription subscription,
	 * OneShotChargeInstance chargeInstance, Integer quantity, Date
	 * applicationDate) throws BusinessException { return
	 * rateOneShotApplication(getEntityManager(), subscription, chargeInstance,
	 * quantity, applicationDate, getCurrentUser()); }
	 */

	public WalletOperation rateOneShotApplication(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantity, Date applicationDate,
			User creator) throws BusinessException {

		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
		if (chargeTemplate == null) {
			throw new IncorrectChargeTemplateException("ChargeTemplate is null for chargeInstance id=" + chargeInstance.getId() + ", code=" + chargeInstance.getCode());
		}

		InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code=" + chargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ subscription.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId,
				creator.getProvider());

		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode() + ".");
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
		}

		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeTemplate.getCode(), subscription, chargeInstance,
				ApplicationTypeEnum.PUNCTUAL, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency,
				countryId, tax.getPercent(), null, null, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), null,
				null, null);

		return chargeApplication;
	}

	public WalletOperation oneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantity,
			Date applicationDate, User creator) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		if (applicationDate == null) {
			applicationDate = new Date();
		}

		log.debug("WalletOperationService.oneShotWalletOperation subscriptionCode={}, quantity={}, multiplicator={}, applicationDate={}, chargeInstance.getId={}", new Object[] {
				subscription.getId(), quantity, chargeInstance.getChargeTemplate().getUnitMultiplicator(), applicationDate, chargeInstance.getId() });

		WalletOperation walletOperation = rateOneShotApplication(subscription, chargeInstance, inputQuantity, quantity, applicationDate, creator);
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

		chargeWalletOperation(walletOperation, creator, chargeInstance.getProvider());
		OneShotChargeTemplate oneShotChargeTemplate = null;

		if (chargeTemplate instanceof OneShotChargeTemplate) {
			oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
		} else {
			oneShotChargeTemplate = oneShotChargeTemplateService.findById(chargeTemplate.getId());
		}

		Boolean immediateInvoicing = (oneShotChargeTemplate != null && oneShotChargeTemplate.getImmediateInvoicing() != null) ? oneShotChargeTemplate.getImmediateInvoicing()
				: false;

		if (immediateInvoicing != null && immediateInvoicing) {
			BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
			int delay = billingAccount.getBillingCycle().getInvoiceDateDelay();
			Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(), -delay);
			nextInvoiceDate = DateUtils.setTimeToZero(nextInvoiceDate);
			applicationDate = DateUtils.setTimeToZero(applicationDate);

			if (applicationDate.after(nextInvoiceDate)) {
				billingAccount.setNextInvoiceDate(applicationDate);
				billingAccountService.update(billingAccount, creator);
			}
		}
		return walletOperation;
	}


	public Date getNextApplicationDate(RecurringChargeInstance chargeInstance) {
		Date applicationDate = chargeInstance.getSubscriptionDate();
		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		Calendar cal = recurringChargeTemplate.getCalendar();
		if (cal.truncDateTime()) {
			applicationDate = DateUtils.setTimeToZero(chargeInstance.getSubscriptionDate());
		}
		chargeInstance.setChargeDate(applicationDate);
		cal.setInitDate(chargeInstance.getSubscriptionDate());
		Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			nextapplicationDate = DateUtils.setTimeToZero(nextapplicationDate);
		}
		return nextapplicationDate;
	}

	public WalletOperation prerateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance, Date nextapplicationDate) throws BusinessException {
		return rateSubscription(subscriptionDate, chargeInstance, nextapplicationDate);
	}

	public WalletOperation rateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance, Date nextapplicationDate) throws BusinessException {
		WalletOperation result = null;
		Date applicationDate = chargeInstance.getChargeDate();

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(subscriptionDate);
		Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			previousapplicationDate = DateUtils.setTimeToZero(previousapplicationDate);
		}
		log.debug("rateSubscription subscriptionDate={} applicationDate={}, nextapplicationDate={},previousapplicationDate={}",subscriptionDate, applicationDate, nextapplicationDate, previousapplicationDate);

		BigDecimal quantity = chargeInstance.getServiceInstance() == null ? null : chargeInstance.getServiceInstance().getQuantity();
		BigDecimal inputQuantity = quantity;
		quantity = NumberUtil.getInChargeUnit(quantity, chargeInstance.getChargeTemplate().getUnitMultiplicator(), chargeInstance.getChargeTemplate().getUnitNbDecimal());
		
		if (Boolean.TRUE.equals(recurringChargeTemplate.getSubscriptionProrata())) {
			Date periodStart = applicationDate;
			double prorataRatio = 1.0;
			double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
			double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);
			if (part2 > 0) {
				prorataRatio = part1 / part2;
			} else {
				log.error("Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}", nextapplicationDate, previousapplicationDate);
			}

			quantity = quantity.multiply(new BigDecimal(prorataRatio).setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
			log.debug("rateSubscription part1={}, part2={}, prorataRation={} -> quantity={}", new Object[] { part1, part2, prorataRatio, quantity });
		}

		String param2 = " " + sdf.format(applicationDate) + " au " + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
		log.debug("param2={}", param2);

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId,
				getCurrentProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
		}

		if (!recurringChargeTemplate.getApplyInAdvance()) {
			applicationDate = nextapplicationDate;
		}
		if (subscriptionDate == null) {
			result = chargeApplicationRatingService.rateChargeApplication(chargeInstance.getCode(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
					ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency,
					countryId, tax.getPercent(), null, nextapplicationDate, recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(),
					chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1), null);
		} else {
			result = chargeApplicationRatingService.prerateChargeApplication(chargeInstance.getCode(), subscriptionDate, chargeInstance.getServiceInstance().getSubscription()
					.getOffer().getCode(), chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate, chargeInstance.getAmountWithoutTax(),
					chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate,
					recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), applicationDate,
					DateUtils.addDaysToDate(nextapplicationDate, -1), null);
		}
		return result;
	}

	public void chargeSubscription(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("ChargeApplicationService.chargeSubscription subscriptionCode={}, chargeCode={}, quantity={}, applicationDate={},chargeInstance.getId={}",
				new Object[] { chargeInstance.getServiceInstance().getSubscription().getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(),
						chargeInstance.getSubscriptionDate(), chargeInstance.getId() });

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		Date nextapplicationDate = getNextApplicationDate(chargeInstance);

		if (recurringChargeTemplate.getApplyInAdvance() != null && recurringChargeTemplate.getApplyInAdvance()) {
			WalletOperation chargeApplication = rateSubscription(chargeInstance.getSubscriptionDate(), chargeInstance, nextapplicationDate);
			// create(chargeApplication, creator, chargeInstance.getProvider());
			chargeWalletOperation(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setNextChargeDate(nextapplicationDate);
		} else {
			chargeInstance.setNextChargeDate(nextapplicationDate);
		}

	}

	public void applyReimbursment(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {
		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("applyReimbursment subscriptionCode={},chargeCode={},quantity={}," + "applicationDate={},chargeInstance.getId={},NextChargeDate={}", chargeInstance
				.getServiceInstance().getSubscription().getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(),
				chargeInstance.getSubscriptionDate(), chargeInstance.getId(), chargeInstance.getNextChargeDate());

		Date applicationDate = chargeInstance.getTerminationDate();
		applicationDate = DateUtils.addDaysToDate(applicationDate, 1);

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		if (recurringChargeTemplate.getCalendar() == null) {
			throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code=" + recurringChargeTemplate.getCode());
		}
		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
		if (cal.truncDateTime()) {
			applicationDate = DateUtils.setTimeToZero(applicationDate);
		}

		BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity();
		BigDecimal inputQuantity = quantity;
		quantity = NumberUtil.getInChargeUnit(quantity, chargeInstance.getChargeTemplate().getUnitMultiplicator(), chargeInstance.getChargeTemplate().getUnitNbDecimal());

		Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			nextapplicationDate = DateUtils.setTimeToZero(nextapplicationDate);
		}
		Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			previousapplicationDate = DateUtils.setTimeToZero(previousapplicationDate);
		}
		log.debug("applicationDate={}, nextapplicationDate={},previousapplicationDate={}", applicationDate, nextapplicationDate, previousapplicationDate);

		Date periodStart = applicationDate;
		if (recurringChargeTemplate.getTerminationProrata()) {

			double prorataRatio = 1.0;
			double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
			double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);

			if (part2 > 0) {
				prorataRatio = (-1) * part1 / part2;
			} else {
				log.error("Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}", nextapplicationDate, previousapplicationDate);
			}

			// FIXME i18n
			String param2 = " " + str_tooPerceived + " " + sdf.format(periodStart) + " / " + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			quantity = quantity.multiply(new BigDecimal(prorataRatio + "").setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
			log.debug("part1={}, part2={}, prorataRatio={}, param2={} -> quantity={}", part1, part2, prorataRatio, param2, quantity);

			InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
			if (invoiceSubCategory == null) {
				throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
			}

			TradingCurrency currency = chargeInstance.getCurrency();
			if (currency == null) {
				throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
						+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getId());
			}

			TradingCountry country = chargeInstance.getCountry();
			if (country == null) {
				throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
						+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
			}
			Long countryId = country.getId();

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId,
					creator.getProvider());
			if (invoiceSubcategoryCountry == null) {
				throw new IncorrectChargeTemplateException("no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
						+ " and trading country=" + country.getCountryCode());
			}

			Tax tax = invoiceSubcategoryCountry.getTax();
			if (tax == null) {
				throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance.getCode(), chargeInstance.getServiceInstance()
					.getSubscription(), chargeInstance, ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance
					.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory, chargeInstance
					.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), periodStart, DateUtils.addDaysToDate(nextapplicationDate, -1),
					ChargeApplicationModeEnum.REIMBURSMENT);

			chargeWalletOperation(chargeApplication, creator, chargeInstance.getProvider());
			// create(chargeApplication, creator, chargeInstance.getProvider());
		}

		if (recurringChargeTemplate.getApplyInAdvance()) {
			Date nextChargeDate = chargeInstance.getNextChargeDate();
			log.debug("reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}", applicationDate, nextapplicationDate, nextChargeDate);

			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyReccuringCharge(chargeInstance, true, recurringChargeTemplate, creator);
			}
		} else {
			Date nextChargeDate = chargeInstance.getChargeDate();
			log.debug("reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}", applicationDate, nextapplicationDate, nextChargeDate);

			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyNotAppliedinAdvanceReccuringCharge(chargeInstance, true, recurringChargeTemplate, creator);
			}
		}
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
	public void applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement, RecurringChargeTemplate recurringChargeTemplate, User creator)
			throws BusinessException {

		Date applicationDate = chargeInstance.getNextChargeDate();

		if (reimbursement) {
			Calendar cal = recurringChargeTemplate.getCalendar();
			cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
			applicationDate = cal.nextCalendarDate(chargeInstance.getTerminationDate());
		}

		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("nextChargeDate is null.");
		}

		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
		Date nextApplicationDate = reimbursement ? chargeInstance.getNextChargeDate() : cal.nextCalendarDate(applicationDate);

		log.debug("reimbursement={}, applicationDate={}", reimbursement, applicationDate);

		InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId,
				creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
		}

		log.debug("next step for {}, applicationDate={}, nextApplicationDate={}, nextApplicationDate={}", chargeInstance.getId(), applicationDate, nextApplicationDate,
				nextApplicationDate);
		while (applicationDate.getTime() < nextApplicationDate.getTime()) {
			Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
			log.debug("next step for {}, applicationDate={}, nextApplicationDate={}, nextApplicationDate={}", chargeInstance.getId(), applicationDate, nextapplicationDate,
					nextApplicationDate);

			String param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate) + (reimbursement ? " / " : " au ")
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity();
			if (reimbursement) {
				quantity = quantity.negate();
			}
			BigDecimal inputQuantity = quantity;
			quantity = NumberUtil.getInChargeUnit(quantity, recurringChargeTemplate.getUnitMultiplicator(), recurringChargeTemplate.getUnitNbDecimal());
			
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={} -> quantity={}", nextapplicationDate, param2, quantity);

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance.getCode(), chargeInstance.getServiceInstance()
					.getSubscription(), chargeInstance, reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT, applicationDate, chargeInstance
					.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate,
					invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), applicationDate, DateUtils.addDaysToDate(
							nextapplicationDate, -1), reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION);
			chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

			chargeWalletOperation(chargeApplication, creator, chargeInstance.getProvider());
			// create(chargeApplication, creator, chargeInstance.getProvider());
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}

		chargeInstance.setNextChargeDate(nextApplicationDate);
	}

	public void applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
			RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

		Date applicationDate = chargeInstance.getChargeDate();
		Calendar cal = recurringChargeTemplate.getCalendar();
		if (chargeInstance.getServiceInstance() != null) {
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
			throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId,
				creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("Tax is null for invoiceSubCategoryCountry id=" + invoiceSubcategoryCountry.getId());
		}

		while (applicationDate.getTime() < nextChargeDate.getTime()) {
			Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
			log.debug("ApplyNotAppliedinAdvanceReccuringCharge next step for {}, applicationDate={}, nextApplicationDate={},nextApplicationDate={}", chargeInstance.getId(),
					applicationDate, nextapplicationDate, nextChargeDate);

			Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
			previousapplicationDate = DateUtils.setTimeToZero(previousapplicationDate);
			log.debug("ApplyNotAppliedinAdvanceReccuringCharge applicationDate={}, nextapplicationDate={},previousapplicationDate={}", applicationDate, nextapplicationDate,
					previousapplicationDate);

			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity();
			BigDecimal inputQuantity = quantity;
			quantity = NumberUtil.getInChargeUnit(quantity, recurringChargeTemplate.getUnitMultiplicator(), recurringChargeTemplate.getUnitNbDecimal());
			
			ApplicationTypeEnum applicationTypeEnum = ApplicationTypeEnum.RECURRENT;
			Date periodStart = applicationDate;
			// n'appliquer le prorata que dans le cas de la 1ere application de
			// charges echues
			log.debug("ApplyNotAppliedinAdvanceReccuringCharge chargeInstance.getWalletOperations().size()={}", chargeInstance.getWalletOperations().size());
			if (chargeInstance.getWalletOperations().size() == 0 && recurringChargeTemplate.getSubscriptionProrata()) {
				applicationTypeEnum = ApplicationTypeEnum.PRORATA_SUBSCRIPTION;
				double prorataRatio = 1.0;
				double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
				double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);

				if (part2 > 0) {
					prorataRatio = part1 / part2;
				} else {
					log.error("ApplyNotAppliedinAdvanceReccuringCharge Error in calendar dates : nextapplicationDate={}, previousapplicationDate={}", nextapplicationDate,
							previousapplicationDate);
				}
				quantity = quantity.multiply(new BigDecimal(prorataRatio + "").setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
				log.debug("part1={}, part2={}, prorataRatio={} -> quantity", part1, part2, prorataRatio, quantity);
			}

			String param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate) + (reimbursement ? " / " : " au ")
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			log.debug("param2={}", param2);

			log.debug("ApplyNotAppliedinAdvanceReccuringCharge : nextapplicationDate={}, param2={}", nextapplicationDate, param2);

			if (reimbursement) {
				quantity = quantity.negate();
			}

			WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance.getCode(), chargeInstance.getServiceInstance().getSubscription(),
					chargeInstance, reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : applicationTypeEnum, applicationDate, chargeInstance.getAmountWithoutTax(),
					chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory,
					chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1),
					reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION);
			walletOperation.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

			List<WalletOperation> oprations = chargeWalletOperation(walletOperation, creator, chargeInstance.getProvider());
			// create(walletOperation, creator, chargeInstance.getProvider());
			// em.flush();
			// em.refresh(chargeInstance);
			chargeInstance.setChargeDate(applicationDate);
			chargeInstance.getWalletOperations().addAll(oprations);
			if (!getEntityManager().contains(walletOperation)) {
				log.error("wtf wallet operation is already detached");
			}
			if (!getEntityManager().contains(chargeInstance)) {
				log.error("wow chargeInstance is detached");
				getEntityManager().merge(chargeInstance);
			}
			applicationDate = nextapplicationDate;
		}

		Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
		chargeInstance.setNextChargeDate(nextapplicationDate);
		chargeInstance.setChargeDate(applicationDate);
	}

	public void applyChargeAgreement(RecurringChargeInstance chargeInstance, RecurringChargeTemplate recurringChargeTemplate, User creator)
			throws BusinessException {

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
			throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
					+ chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getSubscription().getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId,
				creator.getProvider());
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			throw new IncorrectChargeTemplateException("tax is null for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
		}
		
		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
		while (applicationDate.getTime() < endAgreementDate.getTime()) {
			Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
			log.debug("agreement next step for {}, applicationDate={}, nextApplicationDate={}", recurringChargeTemplate.getCode(), applicationDate, nextapplicationDate);
			Double prorataRatio = null;
			ApplicationTypeEnum type = ApplicationTypeEnum.RECURRENT;
			Date endDate = DateUtils.addDaysToDate(nextapplicationDate, -1);
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity();
			BigDecimal inputQuantity = quantity;
			quantity = NumberUtil.getInChargeUnit(quantity, recurringChargeTemplate.getUnitMultiplicator(), recurringChargeTemplate.getUnitNbDecimal());
			
			if (nextapplicationDate.getTime() > endAgreementDate.getTime() && applicationDate.getTime() < endAgreementDate.getTime()) {
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
					quantity = quantity.multiply(new BigDecimal(prorataRatio + "").setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
				}
			}
			String param2 = sdf.format(applicationDate) + " au " + sdf.format(endDate);
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={}", nextapplicationDate, param2);

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance.getCode(), chargeInstance.getServiceInstance()
					.getSubscription(), chargeInstance, type, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity,
					currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
					chargeInstance.getCriteria3(), applicationDate, endDate, ChargeApplicationModeEnum.AGREEMENT);

			chargeWalletOperation(chargeApplication, creator, chargeInstance.getProvider());
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
			log.debug("end of find {} by status (status={}). Result size found={}.", new Object[] { "WalletOperation", status,
					walletOperations != null ? walletOperations.size() : 0 });

		} catch (Exception e) {
			log.error("findByStatus error={} ", e);
		}
		return walletOperations;
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> listToInvoice(Date invoicingDate, Provider provider) {
		List<WalletOperation> walletOperations = null;
		try {
			walletOperations = getEntityManager().createNamedQuery("WalletOperation.listToInvoice").setParameter("invoicingDate", invoicingDate).setParameter("provider", provider)
					.getResultList();
		} catch (Exception e) {
			log.error("listToInvoice error ",e);
		}
		return walletOperations;
	}
	
	@SuppressWarnings("unchecked")
	public List<WalletOperation> listToInvoiceByUserAccount(Date invoicingDate, Provider provider,UserAccount userAccount) {
		List<WalletOperation> walletOperations = null;
		try {
			walletOperations = getEntityManager().createNamedQuery("WalletOperation.listToInvoiceByUA").setParameter("invoicingDate", invoicingDate).setParameter("provider", provider)
					.setParameter("userAccount", userAccount).getResultList();
		} catch (Exception e) {
			log.error("listToInvoiceByUserAccount error ",e);
		}
		return walletOperations;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Long> listToInvoiceIds(Date invoicingDate, Provider provider) {
		List<Long> ids = null;
		try {
			ids = getEntityManager().createNamedQuery("WalletOperation.listToInvoiceIds").setParameter("invoicingDate", invoicingDate).setParameter("provider", provider)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("listToInvoice error={} ", e.getMessage());
		}
		return ids;
	}
	

	@SuppressWarnings("unchecked")
	public List<WalletOperation> listByChargeInstance(ChargeInstance chargeInstance) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "c");
		qb.addCriterionEntity("chargeInstance", chargeInstance);

		try {
			return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("failed to get walletOperation list by ChargeInstance",e);
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
			log.warn("failed to find walletOperation by user account and code",e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findByUserAccountAndWalletCode(String walletCode, UserAccount userAccount, Provider provider, Boolean orderAscending) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w");
		qb.addCriterionEntity("wallet.userAccount", userAccount);
		qb.addCriterionEntity("provider", provider);
		qb.addCriterion("wallet.code", "=", walletCode, true);
		if (orderAscending != null) {
			qb.addOrderCriterion("operationDate", orderAscending);
		}

		try {
			return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("failed to find by user account and wallet",e);
			return null;
		}
	}

	// charging
	private List<WalletOperation> chargeOnWalletIds(List<Long> walletIds, WalletOperation op, User creator, Provider provider) throws BusinessException {
		List<WalletOperation> result = new ArrayList<>();
		BigDecimal remainingAmountToCharge = op.getAmountWithTax();
		BigDecimal totalBalance = walletCacheContainerProvider.getReservedBalance(walletIds);
		log.debug("chargeOnWalletIds remainingAmountToCharge={}, totalBalance={}", remainingAmountToCharge, totalBalance);
		if (remainingAmountToCharge.compareTo(totalBalance) > 0 && walletCacheContainerProvider.isReservedBalanceCached(walletIds.get(walletIds.size() - 1))) {
			throw new BusinessException("INSUFFICIENT_BALANCE");
		}
		for (Long walletId : walletIds) {
			BigDecimal balance = walletCacheContainerProvider.getReservedBalance(walletId);
			log.debug("chargeOnWalletIds walletId={}, balance={}", walletId, balance);
			if (balance.compareTo(BigDecimal.ZERO) > 0 || remainingAmountToCharge.compareTo(BigDecimal.ZERO) < 0) {
				if (balance.compareTo(op.getAmountWithTax()) >= 0) {
					op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
					log.debug("prepaid walletoperation fit in walletInstance {}", op.getWallet());
					create(op, creator, provider);
					result.add(op);
					walletCacheContainerProvider.updateBalanceCache(op);
					break;
				} else {
					BigDecimal newOverOldCoeff = balance.divide(op.getAmountWithTax(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
					remainingAmountToCharge = remainingAmountToCharge.subtract(balance);
					BigDecimal newOpAmountWithTax = balance;
					BigDecimal newOpAmountWithoutTax = op.getAmountWithoutTax().multiply(newOverOldCoeff);
					if (provider.getRounding() != null && provider.getRounding() > 0) {
						newOpAmountWithoutTax = NumberUtils.round(newOpAmountWithoutTax, provider.getRounding());
						newOpAmountWithTax = NumberUtils.round(newOpAmountWithTax, provider.getRounding());
					}
					BigDecimal newOpAmountTax = newOpAmountWithTax.subtract(newOpAmountWithoutTax);
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
					log.debug("prepaid walletoperation partially fit in walletInstance {}, we charge {} and remains ", newOp.getWallet(), newOpAmountTax, opAmountTax);
					create(newOp, creator, provider);
					result.add(newOp);
					walletCacheContainerProvider.updateBalanceCache(newOp);

					op.setAmountWithTax(opAmountWithTax);
					op.setAmountTax(opAmountTax);
					op.setAmountWithoutTax(opAmountWithoutTax);
					op.setQuantity(opQuantity);
				}
			}
		}
		return result;
	}

	public List<WalletOperation> chargeWalletOperation(WalletOperation op, User creator, Provider provider) throws BusinessException {
		List<WalletOperation> result = new ArrayList<>();
		log.debug("chargeWalletOperation on chargeInstanceId:{}", op.getChargeInstance().getId());
		if (walletCacheContainerProvider.isWalletIdsCached(op.getChargeInstance().getId())) {
			List<Long> walletIds = walletCacheContainerProvider.getWallets(op.getChargeInstance().getId());
			log.debug("chargeWalletOperation chargeInstanceId found in usageCache with {} wallet ids", walletIds.size());
			result = chargeOnWalletIds(walletIds, op, creator, provider);

		} else if (op.getChargeInstance().getPrepaid() && (op.getChargeInstance() instanceof RecurringChargeInstance || op.getChargeInstance() instanceof OneShotChargeInstance)) {
			List<Long> walletIds = new ArrayList<>();
			for (WalletInstance wallet : op.getChargeInstance().getWalletInstances()) {
				walletIds.add(wallet.getId());
			}
			log.debug("chargeWalletOperation is recurring or oneshot, and associated to {} wallet ids", walletIds.size());
			result = chargeOnWalletIds(walletIds, op, creator, provider);

		} else if (!op.getChargeInstance().getPrepaid()) {
			op.setWallet(op.getChargeInstance().getSubscription().getUserAccount().getWallet());
			log.debug("chargeWalletOperation is postpaid, set wallet to {}", op.getWallet());
			result.add(op);
			create(op, creator, provider);
			walletCacheContainerProvider.updateBalanceCache(op);
		} else {
			log.error("chargeWalletOperation wallet not found for chargeInstance {} ", op.getChargeInstance().getId());
			throw new BusinessException("WALLET_NOT_FOUND");
		}
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int updateToRerate(List<Long> walletIdList) {
		int walletsOpToRerate = 0;
		@SuppressWarnings("unchecked")
		List<Long> ratedTransactionsBilled = (List<Long>) getEntityManager().createNamedQuery("RatedTransaction.getRatedTransactionsBilled")
				.setParameter("walletIdList", walletIdList).getResultList();
		walletIdList.removeAll(ratedTransactionsBilled);
		if (walletIdList.size() > 0 && !walletIdList.isEmpty()) {
			walletsOpToRerate = getEntityManager().createNamedQuery("WalletOperation.setStatusToRerate").setParameter("notBilledWalletIdList", walletIdList).executeUpdate();
			getEntityManager().createNamedQuery("RatedTransaction.setStatusToCanceled").setParameter("notBilledWalletIdList", walletIdList).executeUpdate();
		}
		getEntityManager().flush();
		return walletsOpToRerate;
	}

	@SuppressWarnings("unchecked")
	public List<Long> listToRerate(Provider provider) {
		return (List<Long>) getEntityManager()
				.createQuery("SELECT o.id FROM WalletOperation o " + "WHERE o.status=org.meveo.model.billing.WalletOperationStatusEnum.TO_RERATE" + " AND o.provider=:provider")
				.setParameter("provider", provider).getResultList();
	}

	public Long getNbrWalletOperationByStatus(WalletOperationStatusEnum status, Provider provider) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w");
		qb.addCriterionEnum("w.status", status);
		qb.addCriterionEntity("provider", provider);
		log.debug("totalCount: queryString={}", qb);
		return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult());
	}

	public Long getNbrEdrByStatus(EDRStatusEnum status, Provider provider) {
		QueryBuilder qb = new QueryBuilder(EDR.class, "e");
		qb.addCriterionEnum("e.status", status);
		qb.addCriterionEntity("provider", provider);
		log.debug("totalCount: queryString={}", qb);
		return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult());
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findWalletOperation(WalletOperationStatusEnum status, WalletTemplate walletTemplate, WalletInstance walletInstance, UserAccount userAccount,
			List<String> fetchFields, Provider provider, int maxResult) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w", fetchFields, provider);

			if (status != null) {
				qb.addCriterionEnum("w.status", status);
			}
			if (walletTemplate != null) {
				qb.addCriterionEntity("w.wallet.walletTemplate", walletTemplate);
			} else {
				qb.addCriterionEntity("w.wallet", walletInstance);
			}
			if (userAccount != null) {
				qb.addCriterionEntity("w.wallet.userAccount", userAccount);
			}

			return (List<WalletOperation>) qb.getQuery(getEntityManager()).setMaxResults(maxResult).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
