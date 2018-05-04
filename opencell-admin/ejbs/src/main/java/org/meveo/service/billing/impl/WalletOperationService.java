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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.NumberUtils;
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
import org.meveo.model.billing.ServiceInstance;
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
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.slf4j.Logger;

/**
 * Service class for WalletOperation entity
 * 
 * @author Wassim Drira
 * @author Phung tien lan
 * @author anasseh
 * @lastModifiedVersion 5.0.2
 */
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
    private RatingService chargeApplicationRatingService;

    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private WalletService walletService;

    public BigDecimal getRatedAmount(Seller seller, Customer customer, CustomerAccount customerAccount, BillingAccount billingAccount, UserAccount userAccount, Date startDate,
            Date endDate, boolean amountWithTax) {

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
            log.error("failed to get Rated Amount", e);
        }

        if (result == null)
            result = BigDecimal.ZERO;
        return result;
    }

    /*
     * public WalletOperation rateOneShotApplication(Subscription subscription, OneShotChargeInstance chargeInstance, Integer quantity, Date applicationDate) throws
     * BusinessException { return rateOneShotApplication(getEntityManager(), subscription, chargeInstance, quantity, applicationDate); }
     */

    public WalletOperation rateOneShotApplication(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits,
            Date applicationDate, boolean isVirtual, String orderNumberOverride) throws BusinessException {

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
            throw new IncorrectChargeTemplateException(
                "No currency exists for customerAccount id=" + subscription.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            applicationDate);

        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
                    + tradingCountry.getCountryCode() + ".");
        }

        Tax tax = null;
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }
        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance, ApplicationTypeEnum.PUNCTUAL, applicationDate,
            chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantityInChargeUnits, currency, tradingCountry.getId(), tax.getPercent(), null,
            null, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
            orderNumberOverride != null ? (orderNumberOverride.equals(ChargeInstance.NO_ORDER_NUMBER) ? null : orderNumberOverride) : chargeInstance.getOrderNumber(), null, null,
            null, false, isVirtual);

        return chargeApplication;
    }

    public WalletOperation oneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits,
            Date applicationDate, boolean isVirtual, String orderNumberOverride) throws BusinessException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        if (applicationDate == null) {
            applicationDate = new Date();
        }

        log.debug(
            "WalletOperationService.oneShotWalletOperation subscriptionCode={}, quantity={}, multiplicator={}, applicationDate={}, chargeInstance.id={}, chargeInstance.desc={}",
            new Object[] { subscription.getId(), quantityInChargeUnits, chargeInstance.getChargeTemplate().getUnitMultiplicator(), applicationDate, chargeInstance.getId(),
                    chargeInstance.getDescription() });

        WalletOperation walletOperation = rateOneShotApplication(subscription, chargeInstance, inputQuantity, quantityInChargeUnits, applicationDate, isVirtual,
            orderNumberOverride);
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

        if (isVirtual) {
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

            if (nextInvoiceDate == null || applicationDate.after(nextInvoiceDate)) {
                billingAccount.setNextInvoiceDate(applicationDate);
                billingAccountService.update(billingAccount);
            }
        }
        return walletOperation;
    }

    public WalletOperation rateProductApplication(ProductChargeInstance chargeInstance, boolean isVirtual) throws BusinessException {

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
            throw new IncorrectChargeTemplateException(
                "No currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            chargeInstance.getChargeDate());

        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
                    + tradingCountry.getCountryCode() + ".");
        }
        Tax tax = null;
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance, ApplicationTypeEnum.PUNCTUAL, chargeInstance.getChargeDate(),
            chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), chargeInstance.getQuantity(), null, currency, tradingCountry.getId(), tax.getPercent(), null,
            null, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), null, null,
            null, false, isVirtual);

        return chargeApplication;
    }
    //
    // /**
    // * Create wallet operation for a product charge for Virtual operation
    // *
    // * @param chargeTemplate Charge template to apply
    // * @param userAccount User account to apply to
    // * @param offerCode Offer code
    // * @param inputQuantity Quantity as received
    // * @param quantity Quantity as calculated
    // * @param applicationDate Effective date
    // * @param amountWithoutTax Amount without tax to override
    // * @param amountWithTax Amount with tax to override
    // * @param criteria1 Criteria 1
    // * @param criteria2 Criteria 2
    // * @param criteria3 Criteria 3
    //
    // * @return Wallet operation
    // * @throws BusinessException General business exception
    // */
    // public WalletOperation rateProductApplicationVirtual(ProductChargeTemplate chargeTemplate, UserAccount userAccount, String offerCode, BigDecimal inputQuantity,
    // BigDecimal quantity, Date applicationDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, String criteria1, String criteria2, String criteria3)
    // throws BusinessException {
    //
    // InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
    // if (invoiceSubCategory == null) {
    // throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code=" + chargeTemplate.getCode());
    // }
    //
    // TradingCurrency currency = userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency();
    // if (currency == null) {
    // throw new IncorrectChargeTemplateException("No currency exists for customerAccount id=" + userAccount.getBillingAccount().getCustomerAccount().getId());
    // }
    //
    // TradingCountry country = userAccount.getBillingAccount().getTradingCountry();
    // if (country == null) {
    // throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + userAccount.getBillingAccount().getId());
    // }
    //
    // Long countryId = country.getId();
    // InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), countryId);
    //
    // if (invoiceSubcategoryCountry == null) {
    // throw new IncorrectChargeTemplateException("No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country="
    // + country.getCountryCode() + ".");
    // }
    //
    // Tax tax = invoiceSubcategoryCountry.getTax();
    // if (tax == null) {
    // throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
    // }
    //
    // WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplicationVirtual(chargeTemplate, userAccount, offerCode, null, ApplicationTypeEnum.PUNCTUAL,
    // applicationDate, null, null, inputQuantity, quantity, currency, countryId, tax.getPercent(), null, null, invoiceSubCategory, criteria1, criteria2, criteria3, null,
    // null, null, false);
    //
    // return chargeApplication;
    // }

    // Be careful to use this method only for the first application of a recurring charge
    public Date initChargeDateAndGetNextChargeDate(RecurringChargeInstance chargeInstance) throws BusinessException {

        Calendar cal = chargeInstance.getRecurringChargeTemplate().getCalendar();       
        if (!StringUtils.isBlank(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl(), chargeInstance.getServiceInstance(), chargeInstance.getRecurringChargeTemplate());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        Date chargeDate = cal.truncateDateTime(chargeInstance.getSubscriptionDate());
        chargeInstance.setChargeDate(chargeDate);
        Date nextChargeDate = cal.nextCalendarDate(chargeDate);

        return nextChargeDate;
    }

    /**
     * Sets the charge and next charge date of a RecurringChargeInstance. This method is called when a {@link RecurringChargeTemplate#getFilterExpression()} evaluates to false.
     * 
     * @param chargeInstance RecurringChargeInstance
     * @throws BusinessException 
     * @see RecurringChargeInstance
     */
    public void updateChargeDate(RecurringChargeInstance chargeInstance) throws BusinessException {
        Calendar cal = chargeInstance.getRecurringChargeTemplate().getCalendar();
        if (!StringUtils.isBlank(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl(), chargeInstance.getServiceInstance(), chargeInstance.getRecurringChargeTemplate());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        Date chargeDate = cal.truncateDateTime(chargeInstance.getNextChargeDate());
        Date nextChargeDate = cal.nextCalendarDate(chargeInstance.getNextChargeDate());

        chargeInstance.setChargeDate(chargeDate);
        chargeInstance.setNextChargeDate(nextChargeDate);
    }

    /**
     * Apply the first recurring charge. Quantity might be prorated based on charge configuration. Will update charge instance with a new charge and next charge dates. <br>
     * For applied in advance charges, will create a WalletOperation with wo.operationDate = chargeInstance.chargeDate, wo.startDate = chargeInstance.chargeDate and
     * wo.endDate=chargeInstance.nextChargeDate.<br>
     * For charges not applied in advance, will create a WalletOperation with wo.operationDate = chargeInstance.nextChargeDate, wo.startDate = chargeInstance.chargeDate and
     * wo.endDate=chargeInstance.nextChargeDate. It will also update chargeInstance.chargeDate = chargeInstance.nextChargeDate and chargeInstance.nextChargeDate =
     * nextCalendarDate(chargeInstance.nextChargeDate)
     * 
     * @param chargeInstance Recurring charge to apply
     * @param nextChargeDate Next charge date
     * @param preRateOnly Pre-rate only
     * @return Created wallet operation
     * @throws BusinessException Business exception
     */
    public WalletOperation applyFirstRecurringCharge(RecurringChargeInstance chargeInstance, Date nextChargeDate, boolean preRateOnly) throws BusinessException {

        WalletOperation result = null;

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

        Date subscriptionDate = chargeInstance.getSubscriptionDate(); // AKK Need to be truncated?? cal.truncateDateTime(chargeInstance.getSubscriptionDate());

        Calendar cal = recurringChargeTemplate.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), recurringChargeTemplate);
        }
        cal.setInitDate(subscriptionDate);

        Date applyChargeOnDate = chargeInstance.getChargeDate(); // Charge date is already truncated based on calendar, so no need to truncate here again
        Date previousChargeDate = cal.previousCalendarDate(applyChargeOnDate);
        chargeInstance.setNextChargeDate(nextChargeDate);

        log.debug("Applying the first recuring charge: id: {} for {} - {}, subscriptionDate={}, previousChargeDate={}", chargeInstance.getId(), applyChargeOnDate, nextChargeDate,
            subscriptionDate, previousChargeDate);

        BigDecimal inputQuantity = chargeInstance.getQuantity();

        // Adjust quantity for a partial period
        boolean isSubscriptionProrata = recurringChargeTemplate.getSubscriptionProrata() == null ? false : recurringChargeTemplate.getSubscriptionProrata();
        if (!StringUtils.isBlank(recurringChargeTemplate.getSubscriptionProrataEl())) {
            isSubscriptionProrata = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getSubscriptionProrataEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }

        if (isSubscriptionProrata) {

            double prorataRatio = 1.0;
            double part1 = DateUtils.daysBetween(applyChargeOnDate, nextChargeDate);
            double part2 = DateUtils.daysBetween(previousChargeDate, nextChargeDate);
            if (part2 > 0) {
                prorataRatio = part1 / part2;
            } else {
                log.error("Error in calendar dates charge id={} : chargeDate={}, nextChargeDate={}, previousChargeDate={}", chargeInstance.getId(), applyChargeOnDate,
                    nextChargeDate, previousChargeDate);
            }

            inputQuantity = inputQuantity.multiply(new BigDecimal(prorataRatio + ""));
            log.debug("Recuring charge id={} will be rated with prorata {}/{}={} -> quantity={}", chargeInstance.getId(), part1, part2, prorataRatio, inputQuantity);
        }

        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
        }

        TradingCurrency currency = chargeInstance.getCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException(
                "no currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            applyChargeOnDate);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException(
                "no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
        }

        Tax tax = null;
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("No tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        boolean isApplyInAdvance = recurringChargeTemplate.getApplyInAdvance() == null ? false : recurringChargeTemplate.getApplyInAdvance();
        if (!StringUtils.isBlank(recurringChargeTemplate.getApplyInAdvanceEl())) {
            isApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getApplyInAdvanceEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }

        Date chargeDateForWO = isApplyInAdvance ? applyChargeOnDate : nextChargeDate;

        if (!preRateOnly) {
            result = chargeApplicationRatingService.rateChargeApplication(chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, chargeDateForWO,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), tax.getPercent(), null,
                nextChargeDate, recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
                chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate, null, false, false);
        } else {
            String languageCode = chargeInstance.getUserAccount().getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
            result = chargeApplicationRatingService.prerateChargeApplication(chargeInstance.getChargeTemplate(), subscriptionDate,
                chargeInstance.getServiceInstance().getSubscription().getOffer(), chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, chargeDateForWO,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), languageCode, tax.getPercent(),
                null, nextChargeDate, recurringChargeTemplate.getInvoiceSubCategory(), chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(),
                chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate, null, chargeInstance.getUserAccount());
        }

        // For charges that are not applied in advance the charge date corresponds to the end date of charge period and thus new nextChargeDate needs to be calculated
        if (!isApplyInAdvance) {
            chargeInstance.setChargeDate(nextChargeDate);
            chargeInstance.setNextChargeDate(cal.nextCalendarDate(nextChargeDate));
        }

        return result;
    }

    /**
     * Calculate nextChargeDate field of recurring charge instance and apply a first charge if charge template is configured to be applied in advance
     * 
     * @param chargeInstance Recurring charge instance
     * @throws BusinessException Business exception
     */
    public void initializeAndApplyFirstRecuringCharge(RecurringChargeInstance chargeInstance) throws BusinessException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        Date nextChargeDate = initChargeDateAndGetNextChargeDate(chargeInstance);

        log.debug("Initializing recurring charge nextChargeDate to {} for chargeInstance id {} chargeCode {}, quantity {}, subscriptionDate {}", nextChargeDate,
            chargeInstance.getId(), chargeInstance.getCode(), chargeInstance.getQuantity(), chargeInstance.getSubscriptionDate());

        if (!isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
            log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
            chargeInstance.setNextChargeDate(nextChargeDate);
            return;
        }

        chargeInstance.setNextChargeDate(nextChargeDate);

        boolean useApplyInAdvance = recurringChargeTemplate.getApplyInAdvance() == null ? false : recurringChargeTemplate.getApplyInAdvance();
        if (!StringUtils.isBlank(recurringChargeTemplate.getApplyInAdvanceEl())) {
            useApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getApplyInAdvanceEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }

        if (useApplyInAdvance) {
            applyFirstRecurringCharge(chargeInstance, nextChargeDate, false);
        }
    }

    public boolean isChargeMatch(ChargeInstance chargeInstance, String filterExpression) throws BusinessException {
        if (StringUtils.isBlank(filterExpression)) {
            return true;
        }

        return ValueExpressionWrapper.evaluateToBooleanOneVariable(filterExpression, "ci", chargeInstance);
    }

    /**
     * Reimburse already applied recurring charges
     * 
     * @param chargeInstance Recurring charge instance
     * @throws BusinessException Business exception
     */
    public void applyReimbursment(RecurringChargeInstance chargeInstance) throws BusinessException {
        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        Date applyChargeOnDate = chargeInstance.getTerminationDate();
        // applyChargeOnDate = DateUtils.addDaysToDate(applyChargeOnDate, 1);

        boolean isApplyInAdvance = chargeInstance.getRecurringChargeTemplate().getApplyInAdvance() == null ? false
                : chargeInstance.getRecurringChargeTemplate().getApplyInAdvance();
        if (StringUtils.isBlank(chargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl())) {
            isApplyInAdvance = recurringChargeTemplateService.matchExpression(chargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl(),
                chargeInstance.getServiceInstance(), chargeInstance.getRecurringChargeTemplate());
        }

        log.debug("Will apply reimbursment for charge {}, chargeCode {}, quantity {}, termination date {}, charge was applied untill {}", chargeInstance.getId(),
            chargeInstance.getCode(), chargeInstance.getQuantity(), chargeInstance.getTerminationDate(), chargeInstance.getNextChargeDate());

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

        Calendar cal = chargeInstance.getRecurringChargeTemplate().getCalendar();
        if (!StringUtils.isBlank(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl(), chargeInstance.getServiceInstance(),
                chargeInstance.getRecurringChargeTemplate());
        }
        if (cal == null) {
            throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code=" + recurringChargeTemplate.getCode());
        }

        cal.setInitDate(chargeInstance.getSubscriptionDate());

        BigDecimal inputQuantity = chargeInstance.getQuantity();

        Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);
        Date previousChargeDate = cal.previousCalendarDate(applyChargeOnDate);

        if (!isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
            log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
            chargeInstance.setNextChargeDate(nextChargeDate);
            return;
        }

        // Take care of the first charge period that termination date falls into
        boolean isTerminationProrata = recurringChargeTemplate.getTerminationProrata() == null ? false : recurringChargeTemplate.getTerminationProrata();
        if (!StringUtils.isBlank(recurringChargeTemplate.getTerminationProrataEl())) {
            isTerminationProrata = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getTerminationProrataEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }
        if (isTerminationProrata) {

            log.debug("Applying the first prorated recuring charge reimbursement : id: {} for {} - {}, subscriptionDate={}, previousChargeDate={}", chargeInstance.getId(),
                applyChargeOnDate, nextChargeDate, chargeInstance.getSubscriptionDate(), previousChargeDate);

            double prorataRatio = 1.0;
            double part1 = DateUtils.daysBetween(applyChargeOnDate, nextChargeDate);
            double part2 = DateUtils.daysBetween(previousChargeDate, nextChargeDate);

            if (part2 > 0) {
                prorataRatio = (-1) * part1 / part2;
            } else {
                log.error("Error in calendar dates charge id={} : chargeDate={}, nextChargeDate={}, previousChargeDate={}", chargeInstance.getId(), applyChargeOnDate,
                    nextChargeDate, previousChargeDate);
            }

            inputQuantity = inputQuantity.multiply(new BigDecimal(prorataRatio + ""));
            log.debug("Recuring charge id={} will be rated with prorata {}/{}={} -> quantity={}", chargeInstance.getId(), part1, part2, prorataRatio, inputQuantity);

            InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
            if (invoiceSubCategory == null) {
                throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
            }

            TradingCurrency currency = chargeInstance.getCurrency();
            if (currency == null) {
                throw new IncorrectChargeTemplateException(
                    "no currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
            }

            TradingCountry tradingCountry = chargeInstance.getCountry();
            if (tradingCountry == null) {
                throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
            }

            InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
                applyChargeOnDate);
            if (invoiceSubcategoryCountry == null) {
                throw new IncorrectChargeTemplateException(
                    "no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
            }

            Tax tax = null;
            if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
                tax = invoiceSubcategoryCountry.getTax();
            } else {
                tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                    chargeInstance.getUserAccount().getBillingAccount(), null);
            }
            if (tax == null) {
                throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
            }
            Date chargeDateForWO = isApplyInAdvance ? applyChargeOnDate : nextChargeDate;
            WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance, ApplicationTypeEnum.PRORATA_TERMINATION, chargeDateForWO,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), tax.getPercent(), null,
                nextChargeDate, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(),
                applyChargeOnDate, nextChargeDate, ChargeApplicationModeEnum.REIMBURSMENT, false, false);

            chargeWalletOperation(chargeApplication);
            // create(chargeApplication);
        }

        // Reimburse other charges that were applied already and are passed the charge period that termination date falls into (Note: in those methods, the next period passed the
        // termination date is taken as a starting point)
        if (isApplyInAdvance) {
            Date ciNextChargeDate = chargeInstance.getNextChargeDate();
            log.debug("Will apply recurring charge {} reimbursement for termination date {} for remaining period {} - {}", chargeInstance.getId(), applyChargeOnDate,
                nextChargeDate, ciNextChargeDate);

            if (ciNextChargeDate != null && ciNextChargeDate.getTime() > nextChargeDate.getTime()) {
                applyReccuringCharge(chargeInstance, true, recurringChargeTemplate, false);
            }
        } else {
            Date ciNextChargeDate = chargeInstance.getChargeDate();
            log.debug("Will apply recurring charge {} reimbursement not applied in advance for termination date {} for remaining period {} - {}", chargeInstance.getId(),
                applyChargeOnDate, nextChargeDate, ciNextChargeDate);

            if (ciNextChargeDate != null && ciNextChargeDate.getTime() > nextChargeDate.getTime()) {
                applyNotAppliedinAdvanceReccuringCharge(chargeInstance, true, recurringChargeTemplate);
            }
        }
    }

    /**
     * Apply the recurring charge starting its chargeInstance.nextChargeDate.<br>
     * For non-reimbursement it will charge only the next calendar period cycle<br>
     * For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged. Thus might span multiple calendar periods.<br>
     * 
     * Will create a WalletOperation with wo.operationDate = chargeInstance.chargeDate, wo.startDate = chargeInstance.chargeDate and wo.endDate=chargeInstance.nextChargeDate
     * 
     * @param chargeInstance charge instance
     * @param reimbursement true/false
     * @param recurringChargeTemplate recurring charge template
     * @param forSchedule true/false
     * @return List of created wallet operations
     * @throws BusinessException business exception.
     */
    public List<WalletOperation> applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement, RecurringChargeTemplate recurringChargeTemplate,
            boolean forSchedule) throws BusinessException {

        long startDate = System.currentTimeMillis();

        ServiceInstance serviceInstance = chargeInstance.getServiceInstance();

        Calendar cal = recurringChargeTemplate.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), recurringChargeTemplate);
        }
        cal.setInitDate(serviceInstance.getSubscriptionDate());

        Date applyChargeFromDate = null;
        Date applyChargeToDate = null;

        // For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged. Thus might span multiple calendar periods.
        if (reimbursement) {
            applyChargeFromDate = cal.nextCalendarDate(chargeInstance.getTerminationDate());
            applyChargeToDate = chargeInstance.getNextChargeDate();

            // For non-reimbursement it will cover only one calendar period cycle
        } else {
            applyChargeFromDate = chargeInstance.getNextChargeDate();
            applyChargeToDate = cal.nextCalendarDate(applyChargeFromDate);
        }

        if (applyChargeFromDate == null) {
            throw new IncorrectChargeInstanceException("nextChargeDate is null.");
        }

        log.debug("Will apply {} recuring charges for charge {} for period {} - {}", reimbursement ? "reimbursement" : "", chargeInstance.getId(), applyChargeFromDate,
            applyChargeToDate);

        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
        }

        TradingCurrency currency = chargeInstance.getCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException(
                "No currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            applyChargeFromDate);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException(
                "No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
        }

        Tax tax = null;
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        List<WalletOperation> walletOperations = new ArrayList<>();

        Date applyChargeOnDate = applyChargeFromDate;

        // In case of regular operation loop will happen only once as nextApplicationDate is same as nextCalendarDate(applicationDate).
        // But in case of termination/reimbursement charges it can happen multiple times if multiple calendar periods fall between the termination date and last charge
        // (chargeInstance.getNextChargeDate())
        while (applyChargeToDate != null && applyChargeOnDate.getTime() < applyChargeToDate.getTime()) {
            Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);

            BigDecimal inputQuantity = chargeInstance.getQuantity();
            if (reimbursement) {
                inputQuantity = inputQuantity.negate();
            }

            log.debug("Applying recurring charge {} for {} - {}, quantity {}", chargeInstance.getId(), applyChargeOnDate, nextChargeDate, inputQuantity);

            WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance,
                reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT, applyChargeOnDate, chargeInstance.getAmountWithoutTax(),
                chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), tax.getPercent(), null, nextChargeDate, invoiceSubCategory,
                chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate,
                reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, forSchedule, false);

            walletOperation.setSubscriptionDate(serviceInstance.getSubscriptionDate());

            if (forSchedule) {
                walletOperation.setStatus(WalletOperationStatusEnum.SCHEDULED);
            }

            List<WalletOperation> operations = chargeWalletOperation(walletOperation);
            walletOperations.addAll(operations);

            // create(chargeApplication);
            chargeInstance.setChargeDate(applyChargeOnDate);
            applyChargeOnDate = nextChargeDate;
        }

        chargeInstance.setNextChargeDate(applyChargeToDate);

        log.debug("Before return applyReccuringCharge:" + (System.currentTimeMillis() - startDate));
        return walletOperations;
    }

    /**
     * Create wallet operations for a recurring charges between given dates for Virtual operation.
     * 
     * @param chargeInstance Recurring charge instance
     * @param fromDate Recurring charge application start
     * @param toDate Recurring charge application end
     * @return Wallet operations
     * @throws BusinessException business exception.
     */
    public List<WalletOperation> applyReccuringChargeVirtual(RecurringChargeInstance chargeInstance, Date fromDate, Date toDate) throws BusinessException {

        List<WalletOperation> walletOperations = new ArrayList<>();

        if (chargeInstance == null) {
            return walletOperations;
        }

        Date applyChargeFromDate = fromDate;
        Calendar cal = chargeInstance.getRecurringChargeTemplate().getCalendar();
        if (!StringUtils.isBlank(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl(), chargeInstance.getServiceInstance(),
                chargeInstance.getRecurringChargeTemplate());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());
        if (cal.getInitDate() == null) {
            ServiceInstance serviceInstance = chargeInstance.getServiceInstance();
            if (serviceInstance != null) {
                cal.setInitDate(serviceInstance.getSubscriptionDate());
            }
        }

        Date applyChargeToDate = cal.nextCalendarDate(toDate == null ? fromDate : toDate);

        InvoiceSubCategory invoiceSubCategory = chargeInstance.getRecurringChargeTemplate().getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code=" + chargeInstance.getRecurringChargeTemplate().getCode());
        }

        TradingCurrency currency = chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException(
                "No currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getUserAccount().getBillingAccount().getTradingCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            applyChargeFromDate);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException(
                "No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
        }

        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("no tax exists for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        BigDecimal inputQuantity = chargeInstance.getQuantity();

        Date applyChargeOnDate = applyChargeFromDate;
        while (applyChargeOnDate.getTime() < applyChargeToDate.getTime()) {
            Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);

            log.debug("ApplyReccuringChargeVirtual : nextapplicationDate={}, quantity={}", nextChargeDate, inputQuantity);

            WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance, ApplicationTypeEnum.RECURRENT, applyChargeOnDate,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), tax.getPercent(), null,
                nextChargeDate, invoiceSubCategory, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(),
                applyChargeOnDate, nextChargeDate, ChargeApplicationModeEnum.SUBSCRIPTION, false, true);

            walletOperations.add(walletOperation);

            applyChargeOnDate = nextChargeDate;
        }

        return walletOperations;
    }

    /**
     * Apply charge that is applied at the end of calendar period.<br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.nextChargeDate, wo.startDate = chargeInstance.chargeDate and
     * wo.endDate=chargeInstance.nextChargeDate.<br>
     * 
     * @param chargeInstance Recurring charge instance to apply
     * @param reimbursement Is it a reimbursemet
     * @param recurringChargeTemplate Recurring charge template
     * @return List of created wallet operations
     * @throws BusinessException Business exception
     */
    public List<WalletOperation> applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
            RecurringChargeTemplate recurringChargeTemplate) throws BusinessException {

        long startDate = System.currentTimeMillis();

        Calendar cal = recurringChargeTemplate.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), recurringChargeTemplate);
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        // For non-reimbursement it will cover only one calendar period cycle
        Date applyChargeFromDate = chargeInstance.getChargeDate(); // Charge date is already truncated based on calendar, so no need to truncate here again
        Date applyChargeToDate = chargeInstance.getNextChargeDate();

        // For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged. Thus might span multiple calendar periods.
        if (reimbursement) {
            applyChargeFromDate = cal.nextCalendarDate(chargeInstance.getTerminationDate());
            applyChargeToDate = chargeInstance.getChargeDate();
        }

        if (applyChargeFromDate == null) {
            throw new IncorrectChargeInstanceException("ChargeDate is null.");
        }

        log.debug("Will apply {} recuring charges not applied in advance for charge {} for period {} - {}", reimbursement ? "reimbursement" : "", chargeInstance.getId(),
            applyChargeFromDate, applyChargeToDate);

        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("InvoiceSubCategory is null for chargeTemplate code=" + recurringChargeTemplate.getCode());
        }

        TradingCurrency currency = chargeInstance.getCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException(
                "No currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("No country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            applyChargeFromDate);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException(
                "No invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
        }

        Tax tax = null;
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("Tax is null for invoiceSubCategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }
        List<WalletOperation> walletOperations = new ArrayList<>();

        Date applyChargeOnDate = applyChargeFromDate;

        boolean isSubscriptionProrata = recurringChargeTemplate.getSubscriptionProrata() == null ? false : recurringChargeTemplate.getSubscriptionProrata();
        if (!StringUtils.isBlank(recurringChargeTemplate.getSubscriptionProrataEl())) {
            isSubscriptionProrata = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getSubscriptionProrataEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }

        while (applyChargeOnDate.getTime() < applyChargeToDate.getTime()) {

            Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);

            BigDecimal inputQuantity = chargeInstance.getQuantity();

            ApplicationTypeEnum applicationTypeEnum = ApplicationTypeEnum.RECURRENT;

            // Apply prorated the first charge only
            if (isSubscriptionProrata && chargeInstance.getWalletOperations().isEmpty()) {

                Date previousChargeDate = cal.previousCalendarDate(applyChargeFromDate);

                applicationTypeEnum = ApplicationTypeEnum.PRORATA_SUBSCRIPTION;
                double prorataRatio = 1.0;
                double part1 = DateUtils.daysBetween(applyChargeOnDate, nextChargeDate);
                double part2 = DateUtils.daysBetween(previousChargeDate, nextChargeDate);

                if (part2 > 0) {
                    prorataRatio = part1 / part2;
                } else {
                    log.error("Error in calendar dates charge id={} : chargeDate={}, nextChargeDate={}, previousChargeDate={}", chargeInstance.getId(), applyChargeOnDate,
                        nextChargeDate, previousChargeDate);
                }
                inputQuantity = inputQuantity.multiply(new BigDecimal(prorataRatio + ""));
                log.debug("Recuring charge id={} will be rated with prorata {}/{}={} -> quantity={}", chargeInstance.getId(), part1, part2, prorataRatio, inputQuantity);
            }

            if (reimbursement) {
                inputQuantity = inputQuantity.negate();
            }

            log.debug("Applying not applied in advance recurring charge {} for {}-{}, quantity {}", chargeInstance.getId(), applyChargeOnDate, nextChargeDate, inputQuantity);

            log.debug("Before walletOperation:" + (System.currentTimeMillis() - startDate));

            WalletOperation walletOperation = chargeApplicationRatingService.rateChargeApplication(chargeInstance,
                reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : applicationTypeEnum, nextChargeDate, chargeInstance.getAmountWithoutTax(),
                chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), tax.getPercent(), null, nextChargeDate, invoiceSubCategory,
                chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate,
                reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, false, false);

            log.debug("After walletOperation:" + (System.currentTimeMillis() - startDate));

            walletOperation.setSubscriptionDate(chargeInstance.getSubscriptionDate());

            List<WalletOperation> operations = chargeWalletOperation(walletOperation);
            walletOperations.addAll(operations);

            log.debug("After chargeWalletOperation:" + (System.currentTimeMillis() - startDate));

            // create(walletOperation);
            // em.flush();
            // em.refresh(chargeInstance);
            chargeInstance.getWalletOperations().addAll(operations);
            if (!getEntityManager().contains(walletOperation)) {
                log.error("wtf wallet operation is already detached");
            }
            if (!getEntityManager().contains(chargeInstance)) {
                log.error("wow chargeInstance is detached");
                getEntityManager().merge(chargeInstance);
            }
            applyChargeOnDate = nextChargeDate;
        }

        chargeInstance.setChargeDate(applyChargeOnDate);
        Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);
        chargeInstance.setNextChargeDate(nextChargeDate);

        log.debug("Before exit:" + (System.currentTimeMillis() - startDate));

        return walletOperations;
    }

    /**
     * Apply missing recuring charges from the last charge date to the end agreement date
     * 
     * @param chargeInstance charge Instance
     * @param recurringChargeTemplate recurringCharge Template
     * @param endAgreementDate end agreement date
     * @throws BusinessException Business exception
     */
    public void applyChargeAgreement(RecurringChargeInstance chargeInstance, RecurringChargeTemplate recurringChargeTemplate, Date endAgreementDate) throws BusinessException {

        // we apply the charge at its nextChargeDate if applied in advance, else at chargeDate
        Date applyChargeFromDate = chargeInstance.getNextChargeDate();
        RecurringChargeTemplate recChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        boolean isApplyInAdvance = recChargeTemplate.getApplyInAdvance() == null ? false : recChargeTemplate.getApplyInAdvance();
        if (!StringUtils.isBlank(recChargeTemplate.getApplyInAdvanceEl())) {
            isApplyInAdvance = recurringChargeTemplateService.matchExpression(recChargeTemplate.getApplyInAdvanceEl(), chargeInstance.getServiceInstance(), recChargeTemplate);
        }
        if (!isApplyInAdvance) {
            applyChargeFromDate = chargeInstance.getChargeDate();
        }

        if (applyChargeFromDate == null) {
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
            throw new IncorrectChargeTemplateException(
                "no currency exists for customerAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = chargeInstance.getCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + chargeInstance.getUserAccount().getBillingAccount().getId());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountry(invoiceSubCategory, tradingCountry,
            applyChargeFromDate);
        if (invoiceSubcategoryCountry == null) {
            throw new IncorrectChargeTemplateException(
                "no invoiceSubcategoryCountry exists for invoiceSubCategory code=" + invoiceSubCategory.getCode() + " and trading country=" + tradingCountry.getCountryCode());
        }

        Tax tax = null;
        if (StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL())) {
            tax = invoiceSubcategoryCountry.getTax();
        } else {
            tax = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), chargeInstance.getUserAccount(),
                chargeInstance.getUserAccount().getBillingAccount(), null);
        }

        if (tax == null) {
            throw new IncorrectChargeTemplateException("tax is null for invoiceSubcategoryCountry id=" + invoiceSubcategoryCountry.getId());
        }

        Calendar cal = recurringChargeTemplate.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), recurringChargeTemplate);
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());
        log.debug("Will apply recurring charge {} for supplement charge agreement for {} - {}", chargeInstance.getId(), applyChargeFromDate, endAgreementDate);

        boolean isTerminationProrata = recurringChargeTemplate.getTerminationProrata() == null ? false : recurringChargeTemplate.getTerminationProrata();
        if (!StringUtils.isBlank(recurringChargeTemplate.getTerminationProrataEl())) {
            isTerminationProrata = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getTerminationProrataEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }

        Date applyChargeOnDate = applyChargeFromDate;
        while (applyChargeOnDate.getTime() < endAgreementDate.getTime()) {

            Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);

            Double prorataRatio = null;
            ApplicationTypeEnum type = ApplicationTypeEnum.RECURRENT;
            BigDecimal inputQuantity = chargeInstance.getQuantity();

            if (nextChargeDate.getTime() > endAgreementDate.getTime() && applyChargeOnDate.getTime() < endAgreementDate.getTime()) {
                Date endAgreementDateModified = DateUtils.addDaysToDate(endAgreementDate, 1);

                double part1 = endAgreementDateModified.getTime() - applyChargeOnDate.getTime();
                double part2 = nextChargeDate.getTime() - applyChargeOnDate.getTime();
                if (part2 > 0) {
                    prorataRatio = part1 / part2;
                }

                nextChargeDate = endAgreementDate;

                if (isTerminationProrata) {
                    type = ApplicationTypeEnum.PRORATA_TERMINATION;
                    inputQuantity = inputQuantity.multiply(new BigDecimal(prorataRatio + ""));
                }
            }

            log.debug("Applying recurring charge {} for supplement charge agreemeent for {}-{}, quantity {}", chargeInstance.getId(), applyChargeOnDate, nextChargeDate,
                inputQuantity);

            if (!isChargeMatch(chargeInstance, recChargeTemplate.getFilterExpression())) {
                log.debug("IPIEL: not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
                chargeInstance.setChargeDate(applyChargeOnDate);
                applyChargeOnDate = nextChargeDate;
                continue;
            }

            WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeApplication(chargeInstance, type, applyChargeOnDate, chargeInstance.getAmountWithoutTax(),
                chargeInstance.getAmountWithTax(), inputQuantity, null, currency, tradingCountry.getId(), tax.getPercent(), null, nextChargeDate, invoiceSubCategory,
                chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3(), chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate,
                ChargeApplicationModeEnum.AGREEMENT, false, false);

            chargeWalletOperation(chargeApplication);

            // create(chargeApplication);
            applyChargeOnDate = nextChargeDate;
        }

        chargeInstance.setChargeDate(applyChargeOnDate);
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> findByStatus(WalletOperationStatusEnum status) {

        List<WalletOperation> walletOperations = null;
        try {
            log.debug("start of find {} by status (status={})) ..", "WalletOperation", status);
            QueryBuilder qb = new QueryBuilder(WalletOperation.class, "c");
            qb.addCriterion("c.status", "=", status, true);

            walletOperations = qb.getQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by status (status={}). Result size found={}.",
                new Object[] { "WalletOperation", status, walletOperations != null ? walletOperations.size() : 0 });

        } catch (Exception e) {
            log.error("findByStatus error={} ", e);
        }
        return walletOperations;
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> listToInvoice(Date invoicingDate) {
        List<WalletOperation> walletOperations = null;
        try {
            walletOperations = getEntityManager().createNamedQuery("WalletOperation.listToInvoice").setParameter("invoicingDate", invoicingDate).getResultList();
        } catch (Exception e) {
            log.error("listToInvoice error ", e);
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
            log.error("listToInvoiceByUserAccount error ", e);
        }
        return walletOperations;
    }

    @SuppressWarnings("unchecked")
    public List<Long> listToInvoiceIds(Date invoicingDate) {
        List<Long> ids = null;
        try {
            ids = getEntityManager().createNamedQuery("WalletOperation.listToInvoiceIds").setParameter("invoicingDate", invoicingDate).getResultList();
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
            log.warn("failed to get walletOperation list by ChargeInstance", e);
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
            log.warn("failed to find walletOperation by user account and code", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> findByUserAccountAndWalletCode(String walletCode, UserAccount userAccount, Boolean orderAscending) {

        QueryBuilder qb = new QueryBuilder(WalletOperation.class, "w", Arrays.asList("chargeInstance"));

        qb.addCriterionEntity("w.wallet.userAccount", userAccount);
        qb.addCriterion("w.wallet.code", "=", walletCode, true);
        if (orderAscending != null) {
            qb.addOrderCriterionAsIs("w.operationDate", orderAscending);
        }

        try {
            return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to find by user account and wallet", e);
            return null;
        }
    }

    // charging
    private List<WalletOperation> chargeOnWalletIds(List<Long> walletIds, WalletOperation op) throws BusinessException {
        List<WalletOperation> result = new ArrayList<>();
        BigDecimal remainingAmountToCharge = op.getAmountWithTax();
        BigDecimal totalBalance = walletService.getWalletReservedBalance(walletIds);
        log.debug("chargeOnWalletIds remainingAmountToCharge={}, totalBalance={}", remainingAmountToCharge, totalBalance);
        if (remainingAmountToCharge.compareTo(totalBalance) > 0) {
            throw new InsufficientBalanceException();
        }
        for (Long walletId : walletIds) {
            BigDecimal balance = walletService.getWalletReservedBalance(walletId);
            log.debug("chargeOnWalletIds walletId={}, reserved balance={}", walletId, balance);
            if (balance.compareTo(BigDecimal.ZERO) > 0 || remainingAmountToCharge.compareTo(BigDecimal.ZERO) < 0) {
                if (balance.compareTo(op.getAmountWithTax()) >= 0) {
                    op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
                    log.debug("prepaid walletoperation fit in walletInstance {}", op.getWallet());
                    create(op);
                    result.add(op);
                    walletCacheContainerProvider.updateBalance(op);
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
                    walletCacheContainerProvider.updateBalance(newOp);

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
        Long chargeInstanceId = chargeInstance.getId();
        log.debug("chargeWalletOperation on chargeInstanceId: {}", chargeInstanceId);
        // case of scheduled operation (for revenue recognition)
        UserAccount userAccount = chargeInstance.getUserAccount();

        if (chargeInstanceId == null) {
            op.setWallet(userAccount.getWallet());
            log.debug("chargeWalletOperation is create schedule on wallet {}", op.getWallet());
            result.add(op);
            create(op);
            log.debug("After create:" + (System.currentTimeMillis() - startDate));

            // Balance and reserved balance deals with prepaid wallets. If charge instance does not contain any prepaid wallet, then it is a postpaid charge and dont need to deal
            // with wallet cache at all
        } else if (!chargeInstance.getPrepaid()) {
            op.setWallet(userAccount.getWallet());
            log.debug("chargeWalletOperation is postpaid, set wallet to {}", op.getWallet().getId());
            result.add(op);
            create(op);

            // Prepaid usage charges only
        } else if (chargeInstance instanceof UsageChargeInstance) {
            List<Long> walletIds = walletService.getWalletIds((UsageChargeInstance) chargeInstance);
            log.debug("chargeWalletOperation chargeInstanceId found in usageCache with {} wallet ids", walletIds.size());
            result = chargeOnWalletIds(walletIds, op);
            log.debug("After chargeOnWalletIds:" + (System.currentTimeMillis() - startDate));

            // The usage charge is taken care of in IF before, as it is cached
            // Prepaid charges only
        } else if (chargeInstance instanceof RecurringChargeInstance || chargeInstance instanceof OneShotChargeInstance) {
            List<Long> walletIds = new ArrayList<>();
            List<WalletInstance> walletInstances = chargeInstance.getWalletInstances();
            for (WalletInstance wallet : walletInstances) {
                walletIds.add(wallet.getId());
            }
            result = chargeOnWalletIds(walletIds, op);

        } else {
            log.error("chargeWalletOperation wallet not found for chargeInstance {} ", chargeInstanceId);
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
        return (List<Long>) getEntityManager().createQuery("SELECT o.id FROM WalletOperation o " + "WHERE o.status=org.meveo.model.billing.WalletOperationStatusEnum.TO_RERATE ")
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

    public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory) {
        return openWalletOperationsBySubCat(walletInstance, invoiceSubCategory, null, null);
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder(WalletOperation.class, "op", null);
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("op.chargeInstance.chargeTemplate.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("op.wallet", walletInstance);
        qb.addCriterionEnum("op.status", WalletOperationStatusEnum.OPEN);
        if (from != null) {
            qb.addCriterion("operationDate", ">=", from, false);
        }
        if (to != null) {
            qb.addCriterion("operationDate", "<=", to, false);
        }

        try {
            return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> openWalletOperationsByCharge(WalletInstance walletInstance) {

        try {
            // todo ejbQL and make namedQuery
            List<Object[]> resultList = getEntityManager()
                .createNativeQuery("select op.description ,sum(op.quantity) QT, sum(op.amount_without_tax) MT ,op.input_unit_description from "
                        + "billing_wallet_operation op , cat_charge_template ct, billing_charge_instance ci " + "where op.wallet_id = " + walletInstance.getId()
                        + " and  op.status = 'OPEN'  and op.charge_instance_id = ci.id and ci.charge_template_id = ct.id and ct.id in (select id from cat_usage_charge_template) "
                        + "group by op.description, op.input_unit_description")
                .getResultList();

            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }

}
