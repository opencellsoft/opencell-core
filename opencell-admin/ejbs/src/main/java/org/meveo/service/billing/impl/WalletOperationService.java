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

import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import org.meveo.admin.exception.RatingException;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * Service class for WalletOperation entity
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Phung tien lan
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class WalletOperationService extends PersistenceService<WalletOperation> {

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
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private WalletService walletService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private SellerService sellerService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private TaxService taxService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private CurrencyService currencyService;

    public WalletOperation rateOneShotApplication(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits,
            Date applicationDate, boolean isVirtual, String orderNumberOverride) throws BusinessException, RatingException {

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applicationDate);

        WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance, ApplicationTypeEnum.PUNCTUAL, applicationDate,
            chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, quantityInChargeUnits, tax, orderNumberOverride, null, null, null, false,
            isVirtual);

        return chargeApplication;
    }

    public WalletOperation applyOneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits,
            Date applicationDate, boolean isVirtual, String orderNumberOverride) throws BusinessException, RatingException {

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

    public WalletOperation rateProductApplication(ProductChargeInstance chargeInstance, boolean isVirtual) throws BusinessException, RatingException {

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, chargeInstance.getChargeDate());

        WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance, ApplicationTypeEnum.PUNCTUAL, chargeInstance.getChargeDate(),
            chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), chargeInstance.getQuantity(), null, tax, chargeInstance.getOrderNumber(), null, null, null,
            false, isVirtual);

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
            cal = recurringChargeTemplateService.getCalendarFromEl(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl(), chargeInstance.getServiceInstance(),
                chargeInstance.getRecurringChargeTemplate());
        }
        cal.setInitDate(chargeInstance.getSubscriptionDate());

        Date chargeDate = cal.truncateDateTime(chargeInstance.getSubscriptionDate());
        Date nextChargeDate = cal.nextCalendarDate(chargeDate);

        return nextChargeDate;
    }

    /**
     * Sets the charge and next charge date of a RecurringChargeInstance. This method is called when a {@link RecurringChargeTemplate#getFilterExpression()} evaluates to false.
     * 
     * @param chargeInstance RecurringChargeInstance
     * @throws BusinessException business exception
     * @see RecurringChargeInstance
     */
    public void updateChargeDate(RecurringChargeInstance chargeInstance) throws BusinessException {
        Calendar cal = chargeInstance.getRecurringChargeTemplate().getCalendar();
        if (!StringUtils.isBlank(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(chargeInstance.getRecurringChargeTemplate().getCalendarCodeEl(), chargeInstance.getServiceInstance(),
                chargeInstance.getRecurringChargeTemplate());
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
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private List<WalletOperation> applyFirstRecurringChargeInstance(RecurringChargeInstance chargeInstance, Date nextChargeDate, boolean preRateOnly)
            throws BusinessException, RatingException {

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

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applyChargeOnDate);

        boolean isApplyInAdvance = recurringChargeTemplate.getApplyInAdvance() == null ? false : recurringChargeTemplate.getApplyInAdvance();
        if (!StringUtils.isBlank(recurringChargeTemplate.getApplyInAdvanceEl())) {
            isApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getApplyInAdvanceEl(), chargeInstance.getServiceInstance(),
                recurringChargeTemplate);
        }

        Date chargeDateForWO = isApplyInAdvance ? applyChargeOnDate : nextChargeDate;
        ChargeTemplate chargeTemplate = recurringChargeTemplateService.findById(chargeInstance.getChargeTemplate().getId());
        if(chargeTemplate instanceof RecurringChargeTemplate && ((RecurringChargeTemplate)chargeTemplate).isProrataOnPriceChange()){
            BigDecimal inputQuantityHolder = inputQuantity;
            boolean isApplyInAdvanceHolder = isApplyInAdvance;
            Calendar calHolder = cal;
            return chargeApplicationRatingService.getActivePricePlansByChargeCode(recurringChargeTemplate.getCode())
                    .stream()
                    .filter(ppm -> !shouldNotIncludePPM(applyChargeOnDate, nextChargeDate, ppm.getValidityFrom(), ppm.getValidityDate())
                    && (ppm.getValidityFrom() != null && nextChargeDate.after(ppm.getValidityFrom())))
                    .map(pricePlanMatrix -> {
                        Date computedApplyChargeOnDate = pricePlanMatrix.getValidityFrom().after(applyChargeOnDate) ? pricePlanMatrix.getValidityFrom() : applyChargeOnDate;
                        Date computedNextChargeDate = pricePlanMatrix.getValidityDate() != null
                                && pricePlanMatrix.getValidityDate().before(nextChargeDate) ? pricePlanMatrix.getValidityDate() : nextChargeDate;
                        double ratio = computeQuantityRatio(computedApplyChargeOnDate, computedNextChargeDate);
                        BigDecimal computedInputQuantity = inputQuantityHolder.multiply(new BigDecimal(ratio + ""));
                        return getRatedChargeWalletOperations(chargeInstance, computedNextChargeDate, preRateOnly, calHolder, computedApplyChargeOnDate,
                                computedInputQuantity, tax, isApplyInAdvanceHolder, chargeDateForWO);
                    })
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(getRatedChargeWalletOperations(chargeInstance, nextChargeDate, preRateOnly, cal, applyChargeOnDate,
                    inputQuantity, tax, isApplyInAdvance, chargeDateForWO));
        }
    }

    private WalletOperation getRatedChargeWalletOperations(RecurringChargeInstance chargeInstance, Date nextChargeDate, boolean preRateOnly, Calendar cal, Date applyChargeOnDate,
            BigDecimal inputQuantity, Tax tax, boolean isApplyInAdvance, Date chargeDateForWO) {
        WalletOperation walletOperation;
        if (!preRateOnly) {
            walletOperation = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, chargeDateForWO,
                    chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, tax, chargeInstance.getOrderNumber(),
                    applyChargeOnDate, nextChargeDate, null, false, false);
            chargeWalletOperation(walletOperation);
        } else {
            walletOperation =  chargeApplicationRatingService.rateCharge(chargeInstance, ApplicationTypeEnum.PRORATA_SUBSCRIPTION, chargeDateForWO,
                    chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, tax, null, applyChargeOnDate, nextChargeDate, null);
        }
        // For charges that are not applied in advance the charge date corresponds to the end date of charge period and thus new nextChargeDate needs to be calculated
        if (!isApplyInAdvance) {
            chargeInstance.setChargeDate(nextChargeDate);
            chargeInstance.setNextChargeDate(cal.nextCalendarDate(nextChargeDate));
        }
        return walletOperation;
    }

    private boolean shouldNotIncludePPM(Date applyChargeOnDate, Date nextChargeDate, Date from, Date to) {
        return (to != null && (to.before(applyChargeOnDate) || to.getTime() == applyChargeOnDate.getTime()))
                || (from != null && (from.after(nextChargeDate) || from.getTime() == nextChargeDate.getTime()));
    }

    /**
     * Calculate nextChargeDate field of recurring charge instance and apply a first charge if charge template is configured to be applied in advance
     * 
     * @param chargeInstance Recurring charge instance
     * @throws BusinessException Business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void initializeAndApplyFirstRecuringCharge(RecurringChargeInstance chargeInstance) throws BusinessException, RatingException {

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
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void applyReimbursment(RecurringChargeInstance chargeInstance, String orderNumber) throws BusinessException, RatingException {
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

            Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applyChargeOnDate);

            Date chargeDateForWO = isApplyInAdvance ? applyChargeOnDate : nextChargeDate;
            String orderNumberForWO = (orderNumber != null) ? orderNumber : chargeInstance.getOrderNumber();
            WalletOperation chargeApplication = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance, ApplicationTypeEnum.PRORATA_TERMINATION, chargeDateForWO,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, tax, orderNumberForWO, applyChargeOnDate, nextChargeDate,
                ChargeApplicationModeEnum.REIMBURSMENT, false, false);

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
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement, RecurringChargeTemplate recurringChargeTemplate,
            boolean forSchedule) throws BusinessException, RatingException {

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

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applyChargeFromDate);

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

            if(recurringChargeTemplate.isProrataOnPriceChange()){
                walletOperations.addAll(generateWalletOperationsByPricePlan(chargeInstance, reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT,
                        reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, recurringChargeTemplate.getCode(), forSchedule,
                        serviceInstance.getSubscriptionDate(), tax, nextChargeDate, applyChargeOnDate, inputQuantity));
            } else {
                WalletOperation walletOperation = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance,
                    reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT, applyChargeOnDate, chargeInstance.getAmountWithoutTax(),
                    chargeInstance.getAmountWithTax(), inputQuantity, null, tax, chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate,
                    reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, forSchedule, false);

                walletOperation.setSubscriptionDate(serviceInstance.getSubscriptionDate());

                if (forSchedule) {
                    walletOperation.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                }

                List<WalletOperation> operations = chargeWalletOperation(walletOperation);
                walletOperations.addAll(operations);
            }

            // create(chargeApplication);
            chargeInstance.setChargeDate(applyChargeOnDate);
            applyChargeOnDate = nextChargeDate;
        }

        chargeInstance.setNextChargeDate(applyChargeToDate);

        return walletOperations;
    }

    private List<WalletOperation> generateWalletOperationsByPricePlan(RecurringChargeInstance chargeInstance,
            ApplicationTypeEnum applicationTypeEnum,ChargeApplicationModeEnum chargeApplicationModeEnum,
            String recurringChargeTemplateCode, boolean forSchedule, Date subscriptionDate, Tax tax, Date nextChargeDate, Date applyChargeOnDate,
            BigDecimal inputQuantity) {
        return chargeApplicationRatingService.getActivePricePlansByChargeCode(recurringChargeTemplateCode)
                .stream()
                .filter(ppm -> !shouldNotIncludePPM(applyChargeOnDate, nextChargeDate, ppm.getValidityFrom(), ppm.getValidityDate())
                        && (ppm.getValidityFrom() != null && nextChargeDate.after(ppm.getValidityFrom())))
                .map(pricePlanMatrix -> {
                    Date computedApplyChargeOnDate = pricePlanMatrix.getValidityFrom().after(applyChargeOnDate) ? pricePlanMatrix.getValidityFrom() : applyChargeOnDate;
                    Date computedNextChargeDate = pricePlanMatrix.getValidityDate() != null
                            && pricePlanMatrix.getValidityDate().before(nextChargeDate) ? pricePlanMatrix.getValidityDate() : nextChargeDate;
                    double ratio = computeQuantityRatio(computedApplyChargeOnDate, computedNextChargeDate);
                    BigDecimal computedInputQuantityHolder = inputQuantity.multiply(new BigDecimal(ratio + ""));
                    WalletOperation walletOperation = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance,
                            applicationTypeEnum, computedApplyChargeOnDate, chargeInstance.getAmountWithoutTax(),
                            chargeInstance.getAmountWithTax(), computedInputQuantityHolder, null, tax, chargeInstance.getOrderNumber(), computedApplyChargeOnDate, computedNextChargeDate,
                            chargeApplicationModeEnum, forSchedule, false);

                    walletOperation.setSubscriptionDate(subscriptionDate);
                    if (forSchedule) {
                        walletOperation.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                    }
                    return chargeWalletOperation(walletOperation);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private double computeQuantityRatio(Date computedApplyChargeOnDate, Date computedNextChargeDate) {
        int lengthOfMonth = 30;
        double days = DateUtils.daysBetween(computedApplyChargeOnDate, computedNextChargeDate);
        return days > lengthOfMonth ? 1 : days / lengthOfMonth;
    }

    /**
     * Create wallet operations for a recurring charges between given dates for Virtual operation.
     * 
     * @param chargeInstance Recurring charge instance
     * @param fromDate Recurring charge application start
     * @param toDate Recurring charge application end
     * @return Wallet operations
     * @throws BusinessException business exception.
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyReccuringChargeVirtual(RecurringChargeInstance chargeInstance, Date fromDate, Date toDate) throws BusinessException, RatingException {

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

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applyChargeFromDate);

        BigDecimal inputQuantity = chargeInstance.getQuantity();

        Date applyChargeOnDate = applyChargeFromDate;
        while (applyChargeOnDate.getTime() < applyChargeToDate.getTime()) {
            Date nextChargeDate = cal.nextCalendarDate(applyChargeOnDate);

            log.debug("ApplyReccuringChargeVirtual : nextapplicationDate={}, quantity={}", nextChargeDate, inputQuantity);

            WalletOperation walletOperation = chargeApplicationRatingService.rateCharge(chargeInstance, ApplicationTypeEnum.RECURRENT, applyChargeOnDate,
                chargeInstance.getAmountWithoutTax(), chargeInstance.getAmountWithTax(), inputQuantity, null, tax, chargeInstance.getOrderNumber(), applyChargeOnDate,
                nextChargeDate, ChargeApplicationModeEnum.SUBSCRIPTION);

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
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
            RecurringChargeTemplate recurringChargeTemplate) throws BusinessException, RatingException {

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

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applyChargeFromDate);

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
            if(recurringChargeTemplate.isProrataOnPriceChange()){
                walletOperations.addAll(generateWalletOperationsByPricePlan(chargeInstance,reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT,
                        reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, recurringChargeTemplate.getCode(),false,
                        chargeInstance.getSubscriptionDate(),tax, nextChargeDate, applyChargeOnDate, inputQuantity));
            } else {
                WalletOperation walletOperation = chargeApplicationRatingService
                        .rateChargeAndTriggerEDRs(chargeInstance, reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : applicationTypeEnum, nextChargeDate, chargeInstance.getAmountWithoutTax(),
                                chargeInstance.getAmountWithTax(), inputQuantity, null, tax, chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate,
                                reimbursement ? ChargeApplicationModeEnum.REIMBURSMENT : ChargeApplicationModeEnum.SUBSCRIPTION, false, false);

                walletOperation.setSubscriptionDate(chargeInstance.getSubscriptionDate());

                List<WalletOperation> operations = chargeWalletOperation(walletOperation);
                walletOperations.addAll(operations);

                if (!getEntityManager().contains(walletOperation)) {
                    log.error("wtf wallet operation is already detached");
                }
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

        return walletOperations;
    }

    /**
     * Apply missing recurring charges from the last charge date to the end agreement date
     * 
     * @param chargeInstance charge Instance
     * @param recurringChargeTemplate recurringCharge Template
     * @param endAgreementDate end agreement date
     * @throws BusinessException Business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public void applyChargeAgreement(RecurringChargeInstance chargeInstance, RecurringChargeTemplate recurringChargeTemplate, Date endAgreementDate)
            throws BusinessException, RatingException {

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

        Tax tax = invoiceSubCategoryCountryService.determineTax(chargeInstance, applyChargeFromDate);

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

        Date nextChargeDate = null;
        while (applyChargeOnDate.getTime() < endAgreementDate.getTime() && (nextChargeDate = cal.nextCalendarDate(applyChargeOnDate)) != null) {
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

            WalletOperation walletOperation = chargeApplicationRatingService.rateChargeAndTriggerEDRs(chargeInstance, type, applyChargeOnDate, chargeInstance.getAmountWithoutTax(),
                chargeInstance.getAmountWithTax(), inputQuantity, null, tax, chargeInstance.getOrderNumber(), applyChargeOnDate, nextChargeDate,
                ChargeApplicationModeEnum.AGREEMENT, false, false);

            chargeWalletOperation(walletOperation);

            // create(chargeApplication);
            applyChargeOnDate = nextChargeDate;
        }

        chargeInstance.setChargeDate(applyChargeOnDate);
    }

    /**
     * Get a list of wallet operations to rate up to a given date. WalletOperation.invoiceDate< date
     * 
     * @param entityToInvoice Entity to invoice
     * @param invoicingDate Invoicing date
     * @return A list of wallet operations
     */
    public List<WalletOperation> listToRate(IBillableEntity entityToInvoice, Date invoicingDate) {

        if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByBA", WalletOperation.class).setParameter("invoicingDate", invoicingDate)
                .setParameter("billingAccount", entityToInvoice).getResultList();

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateBySubscription", WalletOperation.class).setParameter("invoicingDate", invoicingDate)
                .setParameter("subscription", entityToInvoice).getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByOrderNumber", WalletOperation.class).setParameter("invoicingDate", invoicingDate)
                .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber()).getResultList();
        }

        return new ArrayList<>();
    }

    /**
     * Get a list of wallet operations to be invoiced/converted to rated transactions up to a given date. WalletOperation.invoiceDate< date
     * 
     * @param invoicingDate Invoicing date
     * @param nbToRetrieve Number of items to retrieve for processing
     * @return A list of Wallet operation ids
     */
    public List<Long> listToRate(Date invoicingDate, int nbToRetrieve) {
        return getEntityManager().createNamedQuery("WalletOperation.listToRateIds", Long.class).setParameter("invoicingDate", invoicingDate).setMaxResults(nbToRetrieve)
            .getResultList();
    }

    public WalletOperation findByUserAccountAndCode(String code, UserAccount userAccount) {

        try {
            return getEntityManager().createNamedQuery("WalletOperation.findByUAAndCode", WalletOperation.class).setParameter("userAccount", userAccount).setParameter("code", code)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Charge wallet operation on prepaid wallets
     * 
     * @param chargeInstance Charge instance
     * @param op Wallet operation
     * @return A list of wallet operations containing a single original wallet operation or multiple wallet operations if had to be split among various wallets
     * @throws BusinessException General business exception
     * @throws InsufficientBalanceException Balance is insufficient in the wallet
     */
    private List<WalletOperation> chargeOnPrepaidWallets(ChargeInstance chargeInstance, WalletOperation op) throws BusinessException, InsufficientBalanceException {

        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        List<WalletOperation> result = new ArrayList<>();
        Map<Long, BigDecimal> walletLimits = walletService.getWalletIds(chargeInstance);

        // Handles negative amounts (recharge) - apply recharge to the first wallet
        if (op.getAmountWithTax().compareTo(BigDecimal.ZERO) <= 0) {

            Long walletId = walletLimits.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);
            return result;
        }

        log.debug("chargeWalletOperation chargeInstanceId found with {} wallet ids", walletLimits.size());

        Map<Long, BigDecimal> balances = walletService.getWalletReservedBalances(walletLimits.keySet());

        Map<Long, BigDecimal> woAmounts = new HashMap<>();

        BigDecimal remainingAmountToCharge = op.getAmountWithTax();

        // First iterate over balances that have credit
        for (Long walletId : balances.keySet()) {

            BigDecimal balance = balances.get(walletId);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal negatedBalance = balance.negate();
                // Case when amount to deduct (5) is less than or equal to a negated balance amount -(-10)
                if (remainingAmountToCharge.compareTo(negatedBalance) <= 0) {
                    woAmounts.put(walletId, remainingAmountToCharge);
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Case when amount to deduct (10) is more tan a negated balance amount -(-5)
                } else {
                    woAmounts.put(walletId, negatedBalance);
                    balances.put(walletId, BigDecimal.ZERO);
                    remainingAmountToCharge = remainingAmountToCharge.add(balance);
                }
            }
        }

        // If not all the amount was deducted, then iterate again checking if any of the balances can be reduced pass the Zero up to a rejection limit as defined in a wallet.
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {

            for (Long walletId : balances.keySet()) {

                BigDecimal balance = balances.get(walletId);
                BigDecimal rejectLimit = walletLimits.get(walletId);

                // There is no limit upon which further consumption should be rejected
                if (rejectLimit == null) {
                    if (woAmounts.containsKey(walletId)) {
                        woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                    } else {
                        woAmounts.put(walletId, remainingAmountToCharge);
                    }
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Limit is not exceeded yet
                } else if (rejectLimit.compareTo(balance) > 0) {

                    BigDecimal remainingLimit = rejectLimit.subtract(balance);

                    // Case when amount to deduct (5) is less than or equal to a remaining limit (10)
                    if (remainingAmountToCharge.compareTo(remainingLimit) <= 0) {
                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                        } else {
                            woAmounts.put(walletId, remainingAmountToCharge);
                        }

                        balances.put(walletId, balance.add(remainingAmountToCharge));
                        remainingAmountToCharge = BigDecimal.ZERO;
                        break;

                        // Case when amount to deduct (10) is more tan a remaining limit (5)
                    } else {

                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingLimit));
                        } else {
                            woAmounts.put(walletId, remainingLimit);
                        }

                        balances.put(walletId, rejectLimit);
                        remainingAmountToCharge = remainingAmountToCharge.subtract(remainingLimit);
                    }
                }
            }
        }

        // Not possible to deduct all WO amount, so throw an Insufficient balance error
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {
            throw new InsufficientBalanceException("Insuficient balance when charging " + op.getAmountWithTax() + " for wallet operation " + op.getId());
        }

        // All charge was over one wallet
        if (woAmounts.size() == 1) {
            Long walletId = woAmounts.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);

            // Charge was over multiple wallets
        } else {

            for (Entry<Long, BigDecimal> amountInfo : woAmounts.entrySet()) {
                Long walletId = amountInfo.getKey();
                BigDecimal walletAmount = amountInfo.getValue();

                BigDecimal newOverOldCoeff = walletAmount.divide(op.getAmountWithTax(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                BigDecimal newOpAmountWithTax = walletAmount;
                BigDecimal newOpAmountWithoutTax = op.getAmountWithoutTax().multiply(newOverOldCoeff);

                newOpAmountWithoutTax = round(newOpAmountWithoutTax, rounding, roundingMode);
                newOpAmountWithTax = round(newOpAmountWithTax, rounding, roundingMode);
                BigDecimal newOpAmountTax = newOpAmountWithTax.subtract(newOpAmountWithoutTax);
                BigDecimal newOpQuantity = op.getQuantity().multiply(newOverOldCoeff);

                WalletOperation newOp = op.getUnratedClone();
                newOp.setWallet(getEntityManager().find(WalletInstance.class, walletId));
                newOp.setAmountWithTax(newOpAmountWithTax);
                newOp.setAmountTax(newOpAmountTax);
                newOp.setAmountWithoutTax(newOpAmountWithoutTax);
                newOp.setQuantity(newOpQuantity);
                log.debug("prepaid walletoperation partially fit in walletInstance {}, we charge {} of  ", newOp.getWallet(), newOpAmountTax, op.getAmountWithTax());
                create(newOp);
                result.add(newOp);
                walletCacheContainerProvider.updateBalance(newOp);
            }
        }
        return result;
    }

    public List<WalletOperation> chargeWalletOperation(WalletOperation op) throws BusinessException, InsufficientBalanceException {

        List<WalletOperation> result = new ArrayList<>();
        ChargeInstance chargeInstance = op.getChargeInstance();
        Long chargeInstanceId = chargeInstance.getId();
        // case of scheduled operation (for revenue recognition)
        UserAccount userAccount = chargeInstance.getUserAccount();

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        if (chargeTemplate != null) {
            if (op.getInputUnitDescription() == null) {
                op.setInputUnitDescription(chargeTemplate.getInputUnitDescription());
            }
            if (op.getRatingUnitDescription() == null) {
                op.setRatingUnitDescription(chargeTemplate.getRatingUnitDescription());
            }
            if (op.getInvoiceSubCategory() == null) {
                op.setInvoiceSubCategory(chargeTemplate.getInvoiceSubCategory());
            }
        }

        if (chargeInstanceId == null) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);

            // Balance and reserved balance deals with prepaid wallets. If charge instance does not contain any prepaid wallet, then it is a postpaid charge and dont need to deal
            // with wallet cache at all
        } else if (!chargeInstance.getPrepaid()) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);

            // Prepaid charges only
        } else {
            result = chargeOnPrepaidWallets(chargeInstance, op);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int updateToRerate(List<Long> walletIdList) {
        int walletsOpToRerate = 0;

        // Ignore Rated transactions that were billed already
        // TODO AKK check if RT table can be excluded and join is made directly between WOstatus and RTstatus tables
        List<Long> walletOperationsBilled = (List<Long>) getEntityManager().createNamedQuery("WalletOperation.getWalletOperationsBilled").setParameter("walletIdList", walletIdList)
            .getResultList();
        walletIdList.removeAll(walletOperationsBilled);

        if (!walletIdList.isEmpty()) {

            // set selected wo to rerate and ratedTx.id=null
            walletsOpToRerate = getEntityManager().createNamedQuery("WalletOperation.setStatusToRerate").setParameter("now", new Date())
                .setParameter("notBilledWalletIdList", walletIdList).executeUpdate();

            // cancelled selected rts
            getEntityManager().createNamedQuery("RatedTransaction.cancelByWOIds").setParameter("now", new Date()).setParameter("notBilledWalletIdList", walletIdList)
                .executeUpdate();
        }
        getEntityManager().flush();
        return walletsOpToRerate;
    }

    public List<Long> listToRerate() {
        return (List<Long>) getEntityManager().createNamedQuery("WalletOperation.listToRerate", Long.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder("Select op from WalletOperation op", "op");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("op.chargeInstance.chargeTemplate.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("op.wallet", walletInstance);
        qb.addSql(" op.status = 'OPEN'");
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

    public Long countNonTreatedWOByBA(BillingAccount billingAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByBA").setParameter("billingAccount", billingAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by BA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByUA(UserAccount userAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByUA").setParameter("userAccount", userAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by UA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by CA", e);
            return null;
        }
    }

    /**
     * apply first recurring charge and the counter will be decremented by charge quantity if it's not equal to 0.
     * 
     * @param recurringChargeInstance the recurring charge instance
     * @param nextChargeDate the next charge date
     * @param preRateOnly Pre-rate only
     * @return Created wallet operation
     * @throws BusinessException the business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyFirstRecurringCharge(RecurringChargeInstance recurringChargeInstance, Date nextChargeDate, boolean preRateOnly)
            throws BusinessException, RatingException {
        List<WalletOperation> resultingWalletOperations = null;
        CounterInstance counterInstance = recurringChargeInstance.getCounter();
        if (counterInstance != null) {
            // get the counter period of recurring charge instance
            CounterPeriod counterPeriod = counterInstanceService.getCounterPeriod(counterInstance, recurringChargeInstance.getChargeDate());
            // If the counter is equal to 0, then the charge is not applied (but next activation date is updated).
            if (counterPeriod == null || counterPeriod.getValue() == null || !counterPeriod.getValue().equals(BigDecimal.ZERO)) {
                resultingWalletOperations = applyFirstRecurringChargeInstance(recurringChargeInstance, nextChargeDate, preRateOnly);
                // The counter will be decremented by charge quantity
                if (counterPeriod == null) {
                    counterPeriod = counterInstanceService.getOrCreateCounterPeriod(counterInstance, recurringChargeInstance.getChargeDate(),
                        recurringChargeInstance.getServiceInstance().getSubscriptionDate(), recurringChargeInstance, recurringChargeInstance.getServiceInstance());
                }
                if (counterPeriod != null) {
                    CounterValueChangeInfo counterValueChangeInfo = counterInstanceService.deduceCounterValue(counterPeriod, recurringChargeInstance.getQuantity(), false);
                    counterInstanceService.triggerCounterPeriodEvent(counterValueChangeInfo, counterPeriod);
                }

            } else {
                updateChargeDate(recurringChargeInstance);
            }
        } else {
            resultingWalletOperations = applyFirstRecurringChargeInstance(recurringChargeInstance, nextChargeDate, preRateOnly);
        }
        return resultingWalletOperations;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrWalletsOperationByStatus() {
        try {
            return (List<Object[]>) getEntityManager().createNamedQuery("WalletOperation.countNbrWalletsOperationByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrWalletsOperationByStatus", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrEdrByStatus() {
        try {
            return (List<Object[]>) getEntityManager().createNamedQuery("EDR.countNbrEdrByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrEdrByStatus", e);
            return null;
        }
    }

    public List<AggregatedWalletOperation> listToInvoiceIdsWithGrouping(Date invoicingDate, RatedTransactionsJobAggregationSetting aggregationSettings) {

        WalletOperationAggregatorQueryBuilder woa = new WalletOperationAggregatorQueryBuilder(aggregationSettings);

        String strQuery = woa.getGroupQuery();
        log.debug("aggregated query={}", strQuery);

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("invoicingDate", invoicingDate);

        // get the aggregated data
        @SuppressWarnings("unchecked")
        List<AggregatedWalletOperation> result = (List<AggregatedWalletOperation>) query.getResultList();

        return result;
    }

    public void updateAggregatedWalletOperations(Date invoicingDate) {
        // batch update
        String strQuery = "UPDATE WalletOperation o SET o.status=org.meveo.model.billing.WalletOperationStatusEnum.TREATED "
                + " WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate) AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN";
        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("invoicingDate", invoicingDate);
        int affectedRecords = query.executeUpdate();
        log.debug("updated record wo count={}", affectedRecords);
    }

    public List<WalletOperation> listByRatedTransactionId(Long ratedTransactionId) {
        return getEntityManager().createNamedQuery("WalletOperation.listByRatedTransactionId", WalletOperation.class).setParameter("ratedTransactionId", ratedTransactionId)
            .getResultList();
    }

    /**
     * Return a list of open Wallet operation between two date.
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @param lastId a last id for pagination
     * @param max a max rows
     * @return a list of Wallet Operation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<WalletOperation> getNotOpenedWalletOperationBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, Long lastId, int max) {
        return getEntityManager().createNamedQuery("WalletOperation.listNotOpenedWObetweenTwoDates", WalletOperation.class)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .setParameter("lastId", lastId)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Remove all not open Wallet operation between two date
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @return the number of deleted entities
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {

        return getEntityManager().createNamedQuery("WalletOperation.deleteNotOpenWObetweenTwoDates").setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
    }

    /**
     * Import wallet operations.
     * 
     * @param walletOperations Wallet Operations DTO list
     * @throws BusinessException
     */
    public void importWalletOperation(List<WalletOperationDto> walletOperations) throws BusinessException {

        for (WalletOperationDto dto : walletOperations) {
            Tax tax = null;
            ChargeInstance chargeInstance = null;

            if (dto.getTaxCode() != null) {
                tax = taxService.findByCode(dto.getTaxCode());
            } else if (dto.getTaxPercent() != null) {
                tax = taxService.findTaxByPercent(dto.getTaxPercent());
            }
            if (tax == null) {
                log.warn("No tax matched for wallet operation by code {} nor tax percent {}", dto.getTaxCode(), dto.getTaxPercent());
                continue;
            }

            if (dto.getChargeInstance() != null) {
                chargeInstance = (ChargeInstance) chargeInstanceService.findByCode(dto.getChargeInstance());
            }

            WalletOperation wo = null;
            if (chargeInstance != null) {
                wo = new WalletOperation(chargeInstance, dto.getQuantity(), null, dto.getOperationDate(), dto.getOrderNumber(), dto.getParameter1(), dto.getParameter2(),
                    dto.getParameter3(), dto.getParameterExtra(), tax, dto.getStartDate(), dto.getEndDate());

            } else {
                Seller seller = null;
                WalletInstance wallet = null;
                Currency currency = null;
                OfferTemplate offer = null;

                if (dto.getOfferCode() != null) {
                    offer = offerTemplateService.findByCode(dto.getOfferCode());
                }

                if (dto.getSeller() != null) {
                    seller = sellerService.findByCode(dto.getSeller());
                }
                if (dto.getWalletId() != null) {
                    wallet = walletService.findById(dto.getWalletId());
                }
                if (dto.getCurrency() != null) {
                    currency = currencyService.findByCode(dto.getCurrency());
                }
                wo = new WalletOperation(dto.getCode(), "description", wallet, dto.getOperationDate(), null, dto.getType(), currency, tax, dto.getUnitAmountWithoutTax(),
                    dto.getUnitAmountWithTax(), dto.getUnitAmountTax(), dto.getQuantity(), dto.getAmountWithoutTax(), dto.getAmountWithTax(), dto.getAmountTax(),
                    dto.getParameter1(), dto.getParameter2(), dto.getParameter3(), dto.getParameterExtra(), dto.getStartDate(), dto.getEndDate(), dto.getSubscriptionDate(), offer,
                    seller, null, dto.getRatingUnitDescription(), null, null, null, dto.getStatus());
            }

            create(wo);
        }
    }

    /**
     * Mark wallet operations, that were invoiced by a given billing run, to be rerated
     * 
     * @param billingRun Billing run that invoiced wallet operations
     */
    public void markToRerateByBR(BillingRun billingRun) {

        List<WalletOperation> walletOperations = getEntityManager().createNamedQuery("WalletOperation.listByBRId", WalletOperation.class).setParameter("brId", billingRun.getId())
            .getResultList();

        for (WalletOperation walletOperation : walletOperations) {
            walletOperation.changeStatus(WalletOperationStatusEnum.TO_RERATE);
        }
    }
}