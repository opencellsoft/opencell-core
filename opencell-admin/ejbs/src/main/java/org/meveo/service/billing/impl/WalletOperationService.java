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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.util.NumberUtil;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
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
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
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

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;
	
	private DateFormat sdf;
	private String str_tooPerceived = null;

	@PostConstruct
	private void init() {
		ParamBean paramBean = ParamBean.getInstance();
		sdf = new SimpleDateFormat(paramBean.getProperty("walletOperation.dateFormat", "dd/MM/yyyy"));
		str_tooPerceived = resourceBundle.getString("str_tooPerceived");
	}

	public BigDecimal getRatedAmount(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount,
			Date startDate, Date endDate, boolean amountWithTax) {
	
		BigDecimal result = BigDecimal.ZERO;
		LevelEnum level = LevelEnum.PROVIDER;

		if (userAccount != null) {
			level = LevelEnum.USER_ACCOUNT;
		} else if (billingAccount != null) {
			level = LevelEnum.BILLING_ACCOUNT;
		} else if (customerAccount != null) {
			level = LevelEnum.CUSTOMER_ACCOUNT;
		} else if (customer != null) {
			level = LevelEnum.CUSTOMER;
		} else if (seller != null) {
			level = LevelEnum.SELLER;
		}

		try {
			String strQuery = "select SUM(r." + (amountWithTax ? "amountWithTax" : "amountWithoutTax") + ") from " + WalletOperation.class.getSimpleName() + " r "
					+ "WHERE r.operationDate>=:startDate AND r.operationDate<:endDate " + "AND (r.status=:open OR r.status=:treated) ";
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

			Query query = getEntityManager().createQuery(strQuery);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
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
	public BigDecimal getBalanceAmount(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount,
			UserAccount userAccount, Date startDate, Date endDate, boolean amountWithTax, int mode) {

		BigDecimal result = BigDecimal.ZERO;
		LevelEnum level = LevelEnum.PROVIDER;

		if (userAccount != null) {
			level = LevelEnum.USER_ACCOUNT;
		} else if (billingAccount != null) {
			level = LevelEnum.BILLING_ACCOUNT;
		} else if (customerAccount != null) {
			level = LevelEnum.CUSTOMER_ACCOUNT;
		} else if (customer != null) {
			level = LevelEnum.CUSTOMER;
		} else if (seller != null) {
			level = LevelEnum.SELLER;
		}

		try {
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select SUM(r." + (amountWithTax ? "amountWithTax" : "amountWithoutTax") + ") from " + WalletOperation.class.getSimpleName() + " r "
					+ "WHERE 1=1 ");

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
	 * quantity, applicationDate); }
	 */

	public WalletOperation rateOneShotApplication(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantity, Date applicationDate,
			boolean isVirtual, String orderNumberOverride) throws BusinessException {

        if (chargeInstance.getChargeTemplate() == null) {
            throw new IncorrectChargeTemplateException("ChargeTemplate is null for chargeInstance id=" + chargeInstance.getId() + ", code=" + chargeInstance.getCode());
        }

		InvoiceSubCategory invoiceSubCategory = chargeInstance.getChargeTemplate().getInvoiceSubCategory();
		if (invoiceSubCategory == null) {
			throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code=" + chargeInstance.getChargeTemplate().getCode());
		}

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ subscription.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);

		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode() + ".");
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
			if (tax == null) {
				throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}
		}

		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance,
				ApplicationTypeEnum.PUNCTUAL, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency,
				countryId, tax.getPercent(), null, null, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
				chargeInstance.getCriteria3(), orderNumberOverride!=null?(orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER)?null:orderNumberOverride):chargeInstance.getOrderNumber(), null,
				null, null, false, isVirtual);

		return chargeApplication;
	}

	public WalletOperation oneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantity,
			Date applicationDate, boolean isVirtual, String orderNumberOverride) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		if (applicationDate == null) {
			applicationDate = new Date();
		}

		log.debug("WalletOperationService.oneShotWalletOperation subscriptionCode={}, quantity={}, multiplicator={}, applicationDate={}, chargeInstance.id={}, chargeInstance.desc={}", new Object[] {
				subscription.getId(), quantity, chargeInstance.getChargeTemplate().getUnitMultiplicator(), applicationDate, chargeInstance.getId(),chargeInstance.getDescription() });

		WalletOperation walletOperation = rateOneShotApplication(subscription, chargeInstance, inputQuantity, quantity, applicationDate, isVirtual, orderNumberOverride);
		ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

		if (isVirtual){
		    return walletOperation;
		}
		
		chargeWalletOperation(walletOperation);
		
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

			if (nextInvoiceDate==null || applicationDate.after(nextInvoiceDate)) {
				billingAccount.setNextInvoiceDate(applicationDate);
				billingAccountService.update(billingAccount);
			}
		}
		return walletOperation;
	}

	public WalletOperation rateProductApplication(ProductChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantity,
			boolean isVirtual) throws BusinessException {

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
					+ chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, chargeInstance.getChargeDate());

		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode() + ".");
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
			if (tax == null) {
				throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}
		}

		WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance,
				ApplicationTypeEnum.PUNCTUAL, chargeInstance.getChargeDate(), chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency,
				countryId, tax.getPercent(), null, null, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
				chargeInstance.getCriteria3(),chargeInstance.getOrderNumber(), null,
				null, null, false, isVirtual);

		return chargeApplication;
	}
//
//    /**
//     * Create wallet operation for a product charge for Virtual operation
//     * 
//     * @param chargeTemplate Charge template to apply
//     * @param userAccount User account to apply to
//     * @param offerCode Offer code
//     * @param inputQuantity Quantity as received
//     * @param quantity Quantity as calculated
//     * @param applicationDate Effective date
//     * @param amountWithoutTax Amount without tax to override
//     * @param amountWithTax Amount with tax to override
//     * @param criteria1 Criteria 1
//     * @param criteria2 Criteria 2
//     * @param criteria3 Criteria 3
//
//     * @return Wallet operation
//     * @throws BusinessException
//     */
//    public WalletOperation rateProductApplicationVirtual(ProductChargeTemplate chargeTemplate, UserAccount userAccount, String offerCode, BigDecimal inputQuantity,
//            BigDecimal quantity, Date applicationDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, String criteria1, String criteria2, String criteria3)
//            throws BusinessException {
//
//        InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
//        if (invoiceSubCategory == null) {
//            throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code=" + chargeTemplate.getCode());
//        }
//
//        TradingCurrency currency = userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency();
//        if (currency == null) {
//            throw new IncorrectChargeTemplateException("No currency exists for customerAccount id=" + userAccount.getBillingAccount().getCustomerAccount().getId());
//        }
//
//        TradingCountry country = userAccount.getBillingAccount().getTradingCountry();
//        if (country == null) {
//            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + userAccount.getBillingAccount().getId());
//        }
//
//        Long countryId = country.getId();
//        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
//
//        if (invoiceSubcategoryCountry == null) {
//            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
//                    + country.getCountryCode() + ".");
//        }
//
//        Tax tax = invoiceSubcategoryCountry.getTax();
//        if (tax == null) {
//            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
//        }
//
//        WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplicationVirtual(chargeTemplate, userAccount, offerCode, null, ApplicationTypeEnum.PUNCTUAL,
//            applicationDate, null, null, inputQuantity, quantity, currency, countryId, tax.getPercent(), null, null, invoiceSubCategory, criteria1, criteria2, criteria3, null,
//            null, null, false);
//
//        return chargeApplication;
//    }

	//Be careful to use this method only for the first application of a recurring charge
	public Date getNextApplicationDate(RecurringChargeInstance chargeInstance) {
		Date applicationDate = chargeInstance.getSubscriptionDate();
		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		Calendar cal = recurringChargeTemplate.getCalendar();
		if (cal.truncDateTime()) {
			applicationDate = DateUtils.setTimeToZero(chargeInstance.getSubscriptionDate());
		}
		chargeInstance.setChargeDate(applicationDate);
		cal.setInitDate(applicationDate);
		Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			nextapplicationDate = DateUtils.setTimeToZero(nextapplicationDate);
		}
		return nextapplicationDate;
	}

	public WalletOperation prerateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance, Date nextapplicationDate) throws BusinessException {
		return rateSubscription(subscriptionDate, chargeInstance, nextapplicationDate, true);
	}

	public WalletOperation rateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance, Date nextapplicationDate) throws BusinessException {
		return rateSubscription(subscriptionDate, chargeInstance, nextapplicationDate, false);
	}
	
	public WalletOperation rateSubscription(Date subscriptionDate, RecurringChargeInstance chargeInstance, Date nextapplicationDate, boolean preRateOnly) throws BusinessException {
				WalletOperation result = null;
		Date applicationDate = chargeInstance.getChargeDate();

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

		Calendar cal = recurringChargeTemplate.getCalendar();
		Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			previousapplicationDate = DateUtils.setTimeToZero(previousapplicationDate);
			subscriptionDate = DateUtils.setTimeToZero(subscriptionDate);
		}
		cal.setInitDate(subscriptionDate);
		log.debug("rateSubscription subscriptionDate={} applicationDate={}, nextapplicationDate={},previousapplicationDate={}",subscriptionDate, applicationDate, nextapplicationDate, previousapplicationDate);

		BigDecimal quantity = chargeInstance.getServiceInstance() == null ? null : chargeInstance.getServiceInstance().getQuantity();
		BigDecimal inputQuantity = quantity;
		quantity = NumberUtil.getInChargeUnit(quantity, chargeInstance.getChargeTemplate().getUnitMultiplicator(), chargeInstance.getChargeTemplate().getUnitNbDecimal(),chargeInstance.getChargeTemplate().getRoundingMode());
		
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
            
			quantity = NumberUtil.getInChargeUnit(quantity, new BigDecimal(prorataRatio), chargeInstance.getChargeTemplate().getUnitNbDecimal(),chargeInstance.getChargeTemplate().getRoundingMode());			
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
					+ chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
		}

		Long countryId = country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
			if (tax == null) {
				throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}
		}

		if (!recurringChargeTemplate.getApplyInAdvance()) {
			applicationDate = nextapplicationDate;
		}
		if (!preRateOnly) {
			result = chargeApplicationRatingService.rateChargeApplication(chargeInstance,
					ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency,
					countryId, tax.getPercent(), null, nextapplicationDate, recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(),
					chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(),
					applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1), null, false, false);
		} else {
		    String languageCode = chargeInstance.getUserAccount().getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
			result = chargeApplicationRatingService.prerateChargeApplication(chargeInstance.getChargeTemplate(), subscriptionDate, chargeInstance.getServiceInstance().getSubscription()
					.getOffer().getCode(), chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate, chargeInstance.getAmountWithoutTax(),
					chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, languageCode, tax.getPercent(), null, nextapplicationDate,
					recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
					chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), applicationDate,
					DateUtils.addDaysToDate(nextapplicationDate, -1), null,chargeInstance.getUserAccount());
		}
		return result;
	}

	public void chargeSubscription(RecurringChargeInstance chargeInstance) throws BusinessException {

		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("ChargeApplicationService.chargeSubscription subscriptionCode={}, chargeCode={}, quantity={}, applicationDate={},chargeInstance.getId={}",
				new Object[] { chargeInstance.getServiceInstance().getSubscription().getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(),
						chargeInstance.getSubscriptionDate(), chargeInstance.getId() });

		RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
		Date nextapplicationDate = getNextApplicationDate(chargeInstance);
		
		if (!isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
			log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
			chargeInstance.setNextChargeDate(nextapplicationDate);
			return;
		}

		if (recurringChargeTemplate.getApplyInAdvance() != null && recurringChargeTemplate.getApplyInAdvance()) {
			WalletOperation chargeApplication = rateSubscription(chargeInstance.getSubscriptionDate(), chargeInstance, nextapplicationDate);
			// create(chargeApplication);
			chargeWalletOperation(chargeApplication);
			chargeInstance.setNextChargeDate(nextapplicationDate);
		} else {
			chargeInstance.setNextChargeDate(nextapplicationDate);
		}

	}
	
	public boolean isChargeMatch(RecurringChargeInstance activeRecurringChargeInstance, String filterExpression) throws BusinessException {
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("ci", activeRecurringChargeInstance);
		if (StringUtils.isBlank(filterExpression)) {
			return true;
		}
		
		return (Boolean) ValueExpressionWrapper.evaluateExpression(filterExpression, userMap, Boolean.class);
	}

	public void applyReimbursment(RecurringChargeInstance chargeInstance) throws BusinessException {
		if (chargeInstance == null) {
			throw new IncorrectChargeInstanceException("charge instance is null");
		}

		log.debug("applyReimbursment subscriptionCode={},chargeCode={},quantity={}," + "applicationDate={},chargeInstance.getId={},NextChargeDate={}", chargeInstance
				.getServiceInstance().getSubscription().getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(),
				chargeInstance.getSubscriptionDate(), chargeInstance.getId(), chargeInstance.getNextChargeDate());

		Date applicationDate = chargeInstance.getTerminationDate();
		//applicationDate = DateUtils.addDaysToDate(applicationDate, 1);

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
		quantity = NumberUtil.getInChargeUnit(quantity, chargeInstance.getChargeTemplate().getUnitMultiplicator(), chargeInstance.getChargeTemplate().getUnitNbDecimal(),chargeInstance.getChargeTemplate().getRoundingMode());

		Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			nextapplicationDate = DateUtils.setTimeToZero(nextapplicationDate);
		}
		Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
		if (cal.truncDateTime()) {
			previousapplicationDate = DateUtils.setTimeToZero(previousapplicationDate);
		}
		log.debug("applicationDate={}, nextapplicationDate={},previousapplicationDate={}", applicationDate, nextapplicationDate, previousapplicationDate);
		
		if (!isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
			log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
			chargeInstance.setNextChargeDate(nextapplicationDate);
			return;
		}

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
			quantity = NumberUtil.getInChargeUnit(quantity, new BigDecimal(prorataRatio + ""), chargeInstance.getChargeTemplate().getUnitNbDecimal(),chargeInstance.getChargeTemplate().getRoundingMode());
			log.debug("part1={}, part2={}, prorataRatio={}, param2={} -> quantity={}", part1, part2, prorataRatio, param2, quantity);

			InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
			if (invoiceSubCategory == null) {
				throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
			}

			TradingCurrency currency = chargeInstance.getCurrency();
			if (currency == null) {
				throw new IncorrectChargeTemplateException("no currency exists for customerAccount id="
						+ chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
			}

			TradingCountry country = chargeInstance.getCountry();
			if (country == null) {
				throw new IncorrectChargeTemplateException("no country exists for billingAccount id="
						+ chargeInstance.getUserAccount().getBillingAccount().getId());
			}
			Long countryId = country.getId();

			InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);
			if (invoiceSubcategoryCountry == null) {
				throw new IncorrectChargeTemplateException("no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode()
						+ " and trading country=" + country.getCountryCode());
			}

			Tax tax = invoiceSubcategoryCountry.getTax();
			if (tax == null) {
				tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
				if (tax == null) {
					throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
				}
			}

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(
					 chargeInstance, ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance
					.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory, chargeInstance
					.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), periodStart, DateUtils.addDaysToDate(nextapplicationDate, -1),
					ChargeApplicationModeEnum.REIMBURSMENT, false, false);

			chargeWalletOperation(chargeApplication);
			// create(chargeApplication);
		}

		if (recurringChargeTemplate.getApplyInAdvance() != null && recurringChargeTemplate.getApplyInAdvance()) {
			Date nextChargeDate = chargeInstance.getNextChargeDate();
			log.debug("reimbursment-applyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}", applicationDate, nextapplicationDate, nextChargeDate);

			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyReccuringCharge(chargeInstance, true, recurringChargeTemplate, false);
			}
		} else {
			Date nextChargeDate = chargeInstance.getChargeDate();
			log.debug("reimbursment-NotApplyInAdvance applicationDate={}, nextapplicationDate={},nextChargeDate={}", applicationDate, nextapplicationDate, nextChargeDate);

			if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
				applyNotAppliedinAdvanceReccuringCharge(chargeInstance, true, recurringChargeTemplate);
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
	 * @param isVirtual is it a virtual operation
	 * @param creator
	 * @return 
	 * @throws BusinessException
	 */
	public List<WalletOperation> applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement, RecurringChargeTemplate recurringChargeTemplate,boolean forSchedule)
			throws BusinessException {
		long startDate = System.currentTimeMillis();
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
		
		log.info("After invoiceSubCategory applyReccuringCharge:" + (System.currentTimeMillis() - startDate));

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}
		
		log.info("After invoiceSubcategoryCountry applyReccuringCharge:" + (System.currentTimeMillis() - startDate));

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
			if (tax == null) {
				throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}
		}
		
		log.info("After tax applyReccuringCharge:" + (System.currentTimeMillis() - startDate));

		log.debug("next step for {}, applicationDate={}, nextApplicationDate={}, nextApplicationDate={}", chargeInstance.getId(), applicationDate, nextApplicationDate,
				nextApplicationDate);
		
		List<WalletOperation> walletOperations = new ArrayList<>();
		
		while (nextApplicationDate != null && applicationDate.getTime() < nextApplicationDate.getTime()) {
			log.info("Inside nextApplicationDate applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
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
			quantity = NumberUtil.getInChargeUnit(quantity, recurringChargeTemplate.getUnitMultiplicator(), recurringChargeTemplate.getUnitNbDecimal(),recurringChargeTemplate.getRoundingMode());
			
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={} -> quantity={}", nextapplicationDate, param2, quantity);
			log.info("Before walletOperation applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
			WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance,
					reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT, applicationDate, chargeInstance
					.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate,
					invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(),
					applicationDate, DateUtils.addDaysToDate(
							nextapplicationDate, -1), reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, forSchedule, false);
			log.info("After walletOperation applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
			walletOperations.add(walletOperation);
			
			walletOperation.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

			if(forSchedule){
				walletOperation.setStatus(WalletOperationStatusEnum.SCHEDULED);
			}
			
			log.info("Before chargeWalletOperation applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
			chargeWalletOperation(walletOperation);
			log.info("Before chargeWalletOperation applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
          
			// create(chargeApplication);
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}

		chargeInstance.setNextChargeDate(nextApplicationDate);
		
		log.info("Before return applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
		return walletOperations;
	}

    /**
     * Create wallet operations for a recurring charges between given dates for Virtual operation
     * 
     * @param chargeInstance Recurring charge instance
     * @param inputQuantity Quantity as received
     * @param quantity Quantity as calculated
     * @param fromDate Recurring charge application start
     * @param toDate Recurring charge application end
     * @return Wallet operations
     * @throws BusinessException
     */
    public List<WalletOperation> applyReccuringChargeVirtual(RecurringChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantity, Date fromDate, Date toDate) throws BusinessException {

        List<WalletOperation> walletOperations = new ArrayList<>();
        Date applicationDate = fromDate;

        Calendar cal = chargeInstance.getRecurringChargeTemplate().getCalendar();
        cal.setInitDate(chargeInstance.getSubscriptionDate());
        Date endApplicationDate = cal.nextCalendarDate(toDate == null ? fromDate : toDate);

        InvoiceSubCategory invoiceSubCategory = chargeInstance.getRecurringChargeTemplate().getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + chargeInstance.getRecurringChargeTemplate().getCode());
        }

        TradingCurrency currency = chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
                    + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry country = chargeInstance.getUserAccount().getBillingAccount().getTradingCountry();
        if (country == null) {
            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }
        Long countryId = country.getId();

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
                    + country.getCountryCode());
        }

        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        while (applicationDate.getTime() < endApplicationDate.getTime()) {
            Date nextapplicationDate = cal.nextCalendarDate(applicationDate);

            String param2 = " " + sdf.format(applicationDate) + " au " + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

            log.debug("ApplyReccuringChargeVirtual : nextapplicationDate={}, param2={} -> quantity={}", nextapplicationDate, param2, quantity);

            WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance, ApplicationTypeEnum.RECURRENT, applicationDate,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate,
                invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), applicationDate,
                DateUtils.addDaysToDate(nextapplicationDate, -1), ChargeApplicationModeEnum.SUBSCRIPTION, false, true);

            walletOperations.add(walletOperation);

            applicationDate = nextapplicationDate;
        }

        return walletOperations;
    }

	public void applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
			RecurringChargeTemplate recurringChargeTemplate) throws BusinessException {
		
		long startDate = System.currentTimeMillis();

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
		
		log.info("After invoiceSubCategory:" + (System.currentTimeMillis() - startDate));

		TradingCurrency currency = chargeInstance.getCurrency();
		if (currency == null) {
			throw new IncorrectChargeTemplateException("No currency exists for customerAccount id="
					+ chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}
		
		log.info("After invoiceSubcategoryCountry:" + (System.currentTimeMillis() - startDate));

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
			if (tax == null) {
				throw new IncorrectChargeTemplateException("Tax is null for invoiceSubCategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}
		}
		
		log.info("After tax:" + (System.currentTimeMillis() - startDate));
		while (applicationDate.getTime() < nextChargeDate.getTime()) {
			log.info("Inside applicationDate:" + (System.currentTimeMillis() - startDate));
			Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
			log.debug("ApplyNotAppliedinAdvanceReccuringCharge next step for {}, applicationDate={}, nextApplicationDate={},nextApplicationDate={}", chargeInstance.getId(),
					applicationDate, nextapplicationDate, nextChargeDate);

			Date previousapplicationDate = cal.previousCalendarDate(applicationDate);
			previousapplicationDate = DateUtils.setTimeToZero(previousapplicationDate);
			log.debug("ApplyNotAppliedinAdvanceReccuringCharge applicationDate={}, nextapplicationDate={},previousapplicationDate={}", applicationDate, nextapplicationDate,
					previousapplicationDate);

			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity();
			BigDecimal inputQuantity = quantity;
			quantity = NumberUtil.getInChargeUnit(quantity, recurringChargeTemplate.getUnitMultiplicator(), recurringChargeTemplate.getUnitNbDecimal(),recurringChargeTemplate.getRoundingMode());
			
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
				quantity = NumberUtil.getInChargeUnit(quantity, new BigDecimal(prorataRatio + ""), recurringChargeTemplate.getUnitNbDecimal(),recurringChargeTemplate.getRoundingMode());				
				log.debug("part1={}, part2={}, prorataRatio={} -> quantity", part1, part2, prorataRatio, quantity);
			}

			String param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate) + (reimbursement ? " / " : " au ")
					+ sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

			log.debug("param2={}", param2);

			log.debug("ApplyNotAppliedinAdvanceReccuringCharge : nextapplicationDate={}, param2={}", nextapplicationDate, param2);

			if (reimbursement) {
				quantity = quantity.negate();
			}
			
			log.info("Before walletOperation:" + (System.currentTimeMillis() - startDate));

			WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication( 
					chargeInstance, reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : applicationTypeEnum, nextapplicationDate, chargeInstance.getAmountWithoutTax(),
					chargeInstance.getAmountWithTax(), inputQuantity, quantity, currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory,
					chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(),
					applicationDate, DateUtils.addDaysToDate(nextapplicationDate, -1),
					reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, false, false);
			
			log.info("After walletOperation:" + (System.currentTimeMillis() - startDate));
			
			walletOperation.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

			List<WalletOperation> oprations = chargeWalletOperation(walletOperation);
			
			log.info("After chargeWalletOperation:" + (System.currentTimeMillis() - startDate));
			
			// create(walletOperation);
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
		
		log.info("Before exit:" + (System.currentTimeMillis() - startDate));
	}

	public void applyChargeAgreement(RecurringChargeInstance chargeInstance, RecurringChargeTemplate recurringChargeTemplate,Date endAgreementDate)
			throws BusinessException {

		// we apply the charge at its nextChargeDate if applied in advance, else at chargeDate
		Date applicationDate = chargeInstance.getNextChargeDate();
		if (chargeInstance.getRecurringChargeTemplate().getApplyInAdvance() != null
				&& !chargeInstance.getRecurringChargeTemplate().getApplyInAdvance()) {
			applicationDate = chargeInstance.getChargeDate();
		}
		if (applicationDate == null) {
			throw new IncorrectChargeInstanceException("nextChargeDate is null.");
		}

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
					+ chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
		}

		TradingCountry country = chargeInstance.getCountry();
		if (country == null) {
			throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
		}
		Long countryId = country.getId();

		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId, applicationDate);
		if (invoiceSubcategoryCountry == null) {
			throw new IncorrectChargeTemplateException("no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
					+ country.getCountryCode());
		}

		Tax tax = invoiceSubcategoryCountry.getTax();
		if (tax == null) {
			tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),chargeInstance.getUserAccount().getBillingAccount(), null);
			if (tax == null) {
				throw new IncorrectChargeTemplateException("tax is null for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
			}
		}
		
		Calendar cal = recurringChargeTemplate.getCalendar();
		cal.setInitDate(chargeInstance.getServiceInstance().getSubscriptionDate());
		log.debug("applicationDate={}, endAgreementDate={}",applicationDate,endAgreementDate);
		while (applicationDate.getTime() < endAgreementDate.getTime()) {
			Date nextapplicationDate = cal.nextCalendarDate(applicationDate);
			log.debug("agreement next step for {}, applicationDate={}, nextApplicationDate={}", recurringChargeTemplate.getCode(), applicationDate, nextapplicationDate);
			Double prorataRatio = null;
			ApplicationTypeEnum type = ApplicationTypeEnum.RECURRENT;
			Date endDate = DateUtils.addDaysToDate(nextapplicationDate, -1);
			BigDecimal quantity = chargeInstance.getServiceInstance().getQuantity();
			BigDecimal inputQuantity = quantity;
			quantity = NumberUtil.getInChargeUnit(quantity, recurringChargeTemplate.getUnitMultiplicator(), recurringChargeTemplate.getUnitNbDecimal(),recurringChargeTemplate.getRoundingMode());
			
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
					quantity = NumberUtil.getInChargeUnit(quantity, new BigDecimal(prorataRatio + ""), recurringChargeTemplate.getUnitNbDecimal(),recurringChargeTemplate.getRoundingMode());
				}
			}
			String param2 = sdf.format(applicationDate) + " - " + sdf.format(endDate);
			log.debug("applyReccuringCharge : nextapplicationDate={}, param2={}", nextapplicationDate, param2);
			
			if (!isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
				log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
				chargeInstance.setChargeDate(applicationDate);
				applicationDate = nextapplicationDate;
				continue;
			}

			WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication( 
					 chargeInstance, type, applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantity,
					currency, countryId, tax.getPercent(), null, nextapplicationDate, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
					chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), applicationDate, endDate, ChargeApplicationModeEnum.AGREEMENT, false, false);

			chargeWalletOperation(chargeApplication);
			// create(chargeApplication);
			chargeInstance.setChargeDate(applicationDate);
			applicationDate = nextapplicationDate;
		}
	}

	public List<WalletOperation> findByStatus(WalletOperationStatusEnum status) {
		return findByStatus(getEntityManager(), status);
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findByStatus(EntityManager em, WalletOperationStatusEnum status) {
		List<WalletOperation> walletOperations = null;
		try {
			log.debug("start of find {} by status (status={})) ..", "WalletOperation", status);
			QueryBuilder qb = new QueryBuilder(WalletOperation.class, "c");
			qb.addCriterion("c.status", "=", status, true);

			walletOperations = qb.getQuery(em).getResultList();
			log.debug("end of find {} by status (status={}). Result size found={}.", new Object[] { "WalletOperation", status,
					walletOperations != null ? walletOperations.size() : 0 });

		} catch (Exception e) {
			log.error("findByStatus error={} ", e);
		}
		return walletOperations;
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> listToInvoice(Date invoicingDate) {
		List<WalletOperation> walletOperations = null;
		try {
			walletOperations = getEntityManager().createNamedQuery("WalletOperation.listToInvoice").setParameter("invoicingDate", invoicingDate)
					.getResultList();
		} catch (Exception e) {
			log.error("listToInvoice error ",e);
		}
		return walletOperations;
	}
	
	@SuppressWarnings("unchecked")
	public List<WalletOperation> listToInvoiceByUserAccount(Date invoicingDate, UserAccount userAccount) {
		List<WalletOperation> walletOperations = null;
		try {
			walletOperations = getEntityManager().createNamedQuery("WalletOperation.listToInvoiceByUA").setParameter("invoicingDate", invoicingDate)
					.setParameter("userAccount", userAccount).getResultList();
		} catch (Exception e) {
			log.error("listToInvoiceByUserAccount error ",e);
		}
		return walletOperations;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Long> listToInvoiceIds(Date invoicingDate) {
		List<Long> ids = null;
		try {
			ids = getEntityManager().createNamedQuery("WalletOperation.listToInvoiceIds").setParameter("invoicingDate", invoicingDate)
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

	public WalletOperation findByUserAccountAndCode(String code, UserAccount userAccount) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w");
		qb.addCriterionEntity("wallet.userAccount", userAccount);
		
		qb.addCriterion("code", "=", code, true);

		try {
			return (WalletOperation) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("failed to find walletOperation by user account and code",e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findByUserAccountAndWalletCode(String walletCode, UserAccount userAccount, Boolean orderAscending) {
		
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w", Arrays.asList("chargeInstance"));
		
		qb.addCriterionEntity("w.wallet.userAccount", userAccount);
		qb.addCriterion("w.wallet.code", "=", walletCode, true);
		if (orderAscending != null) {
			qb.addOrderCriterion("w.operationDate", orderAscending);
		}

		try {
			return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("failed to find by user account and wallet",e);
			return null;
		}
	}

	// charging
	private List<WalletOperation> chargeOnWalletIds(List<Long> walletIds, WalletOperation op) throws BusinessException {
		List<WalletOperation> result = new ArrayList<>();
		BigDecimal remainingAmountToCharge = op.getAmountWithTax();
		BigDecimal totalBalance = walletCacheContainerProvider.getReservedBalance(walletIds);
		log.debug("chargeOnWalletIds remainingAmountToCharge={}, totalBalance={}", remainingAmountToCharge, totalBalance);
		if (remainingAmountToCharge.compareTo(totalBalance) > 0 && walletCacheContainerProvider.isReservedBalanceCached(walletIds.get(walletIds.size() - 1))) {
			throw new InsufficientBalanceException();
		}
		for (Long walletId : walletIds) {
			BigDecimal balance = walletCacheContainerProvider.getReservedBalance(walletId);
			log.debug("chargeOnWalletIds walletId={}, balance={}", walletId, balance);
			if (balance.compareTo(BigDecimal.ZERO) > 0 || remainingAmountToCharge.compareTo(BigDecimal.ZERO) < 0) {
				if (balance.compareTo(op.getAmountWithTax()) >= 0) {
					op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
					log.debug("prepaid walletoperation fit in walletInstance {}", op.getWallet());
					create(op);
					result.add(op);
					walletCacheContainerProvider.updateBalanceCache(op);
					break;
				} else {
					BigDecimal newOverOldCoeff = balance.divide(op.getAmountWithTax(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
					remainingAmountToCharge = remainingAmountToCharge.subtract(balance);
					BigDecimal newOpAmountWithTax = balance;
					BigDecimal newOpAmountWithoutTax = op.getAmountWithoutTax().multiply(newOverOldCoeff);
					if (appProvider.getRounding() != null && appProvider.getRounding() > 0) {
						newOpAmountWithoutTax = NumberUtils.round(newOpAmountWithoutTax, appProvider.getRounding());
						newOpAmountWithTax = NumberUtils.round(newOpAmountWithTax, appProvider.getRounding());
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
					create(newOp);
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

	public List<WalletOperation> chargeWalletOperation(WalletOperation op) throws BusinessException {
		long startDate = System.currentTimeMillis();
		
		List<WalletOperation> result = new ArrayList<>();
		ChargeInstance chargeInstance = op.getChargeInstance();
		log.info("After chargeInstance:" + (System.currentTimeMillis() - startDate));
		Long id = chargeInstance.getId();
		log.debug("chargeWalletOperation on chargeInstanceId:{}", id);
		//case of scheduled operation (for revenue recognition)
		UserAccount userAccount = chargeInstance.getUserAccount();
		log.info("After userAccount:" + (System.currentTimeMillis() - startDate));
		if (id == null) {
			log.info("Inside null:" + (System.currentTimeMillis() - startDate));
			op.setWallet(userAccount.getWallet());
			log.debug("chargeWalletOperation is create schedule on wallet {}", op.getWallet());
			result.add(op);
			create(op);
			log.info("After create:" + (System.currentTimeMillis() - startDate));
		} else if (walletCacheContainerProvider.isWalletIdsCached(id)) {
			log.info("Inside isWalletIdsCached:" + (System.currentTimeMillis() - startDate));
			List<Long> walletIds = walletCacheContainerProvider.getWallets(id);
			log.info("After walletIds:" + (System.currentTimeMillis() - startDate));
			log.debug("chargeWalletOperation chargeInstanceId found in usageCache with {} wallet ids", walletIds.size());
			result = chargeOnWalletIds(walletIds, op);
			log.info("After chargeOnWalletIds:" + (System.currentTimeMillis() - startDate));

		} else if (chargeInstance.getPrepaid() && (chargeInstance instanceof RecurringChargeInstance || chargeInstance instanceof OneShotChargeInstance)) {
			log.info("Inside getPrepaid:" + (System.currentTimeMillis() - startDate));
			List<Long> walletIds = new ArrayList<>();
			List<WalletInstance> walletInstances = chargeInstance.getWalletInstances();
			for (WalletInstance wallet : walletInstances) {
				walletIds.add(wallet.getId());
			}
			log.debug("chargeWalletOperation is recurring or oneshot, and associated to {} wallet ids", walletIds.size());
			result = chargeOnWalletIds(walletIds, op);
			log.info("After chargeOnWalletIds:" + (System.currentTimeMillis() - startDate));
		} else if (!chargeInstance.getPrepaid()) {
			log.info("Inside not getPrepaid:" + (System.currentTimeMillis() - startDate));
			op.setWallet(userAccount.getWallet());
			log.debug("chargeWalletOperation is postpaid, set wallet to {}", op.getWallet());
			result.add(op);
			create(op);
			walletCacheContainerProvider.updateBalanceCache(op);
			
			log.info("After updateBalanceCache:" + (System.currentTimeMillis() - startDate));
		} else {
			log.error("chargeWalletOperation wallet not found for chargeInstance {} ", id);
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
	public List<Long> listToRerate() {
		return (List<Long>) getEntityManager()
				.createQuery("SELECT o.id FROM WalletOperation o " + "WHERE o.status=org.meveo.model.billing.WalletOperationStatusEnum.TO_RERATE ")
				.getResultList();
	}

	public Long getNbrWalletOperationByStatus(WalletOperationStatusEnum status) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w");
		qb.addCriterionEnum("w.status", status);
		
		log.debug("totalCount: queryString={}", qb);
		return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult());
	}

	public Long getNbrEdrByStatus(EDRStatusEnum status) {
		QueryBuilder qb = new QueryBuilder(EDR.class, "e");
		qb.addCriterionEnum("e.status", status);
		
		log.debug("totalCount: queryString={}", qb);
		return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult());
	}

	@SuppressWarnings("unchecked")
	public List<WalletOperation> findWalletOperation(WalletOperationStatusEnum status, WalletTemplate walletTemplate, WalletInstance walletInstance, UserAccount userAccount,
			List<String> fetchFields, int maxResult) {
		try {
			QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w", fetchFields);

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
	
	public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance , InvoiceSubCategory invoiceSubCategory ) {
		return openWalletOperationsBySubCat(walletInstance, invoiceSubCategory, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance , InvoiceSubCategory invoiceSubCategory ,Date from, Date to) {
		QueryBuilder qb = new QueryBuilder(WalletOperation.class, "op", null);
		if(invoiceSubCategory != null){
			qb.addCriterionEntity("op.chargeInstance.chargeTemplate.invoiceSubCategory", invoiceSubCategory);
		}
		qb.addCriterionEntity("op.wallet", walletInstance);
		qb.addCriterionEnum("op.status", WalletOperationStatusEnum.OPEN);
		if(from != null){
			qb.addCriterion("operationDate", ">=", from, false);	
		}
		if(to != null){
			qb.addCriterion("operationDate", "<=", to, false);	
		}
				
		try {
			return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> openWalletOperationsByCharge(WalletInstance walletInstance ) {
		
		try {
			//todo ejbQL and make namedQuery
			List<Object[]> resultList = getEntityManager().createNativeQuery("select op.description ,sum(op.quantity) QT, sum(op.amount_without_tax) MT ,op.input_unit_description from "+
					 "billing_wallet_operation op , cat_charge_template ct, billing_charge_instance ci "+
					 "where op.wallet_id = "+walletInstance.getId()+" and  op.status = 'OPEN'  and op.charge_instance_id = ci.id and ci.charge_template_id = ct.id and ct.id in (select id from cat_usage_charge_template) "+
					 "group by op.description, op.input_unit_description").getResultList();
			
			
			return resultList;			
		} catch (NoResultException e) {
			return null;
		}
	}

}
