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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.slf4j.Logger;

/**
 * The Class RealtimeChargingService.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
@Stateless
public class RealtimeChargingService {

    /** The log. */
    @Inject
    protected Logger log;

    /** The charge application rating service. */
    @Inject
    private RatingService ratingService;

    /** The wallet operation service. */
    @Inject
    private WalletOperationService walletOperationService;

    /** The recurring charge template service. */
    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;
    
    @Inject
	private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    /**
     * Gets the application price.
     *
     * @param ba Billing account
     * @param chargeTemplate the charge template
     * @param subscriptionDate the subscription date
     * @param offerCode the offer code
     * @param quantity the quantity
     * @param param1 the param 1
     * @param param2 the param 2
     * @param param3 the param 3
     * @param priceWithoutTax the price without tax
     * @return The application price as amounts
     * @throws BusinessException the business exception
     */
    public Amounts getApplicationPrice(Seller seller, BillingAccount ba, OneShotChargeTemplate chargeTemplate, Date subscriptionDate, OfferTemplate offerTemplate, BigDecimal quantity, String param1, String param2,
            String param3) throws BusinessException {

        TradingCurrency currency = ba.getCustomerAccount().getTradingCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException("no currency exists for customerAccount id=" + ba.getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = ba.getTradingCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + ba.getId());
        }

        if (seller == null) {
            seller = ba.getCustomerAccount().getCustomer().getSeller();
        }

        return getApplicationPrice(seller, ba, currency, tradingCountry, chargeTemplate, subscriptionDate, offerTemplate, quantity, param1, param2, param3, false);
    }

    /**
     * Gets the application price.
     *
     * @param seller the seller
     * @param ba the ba
     * @param currency the currency
     * @param buyersCountry Buyer's country
     * @param chargeTemplate the charge template
     * @param subscriptionDate the subscription date
     * @param offerCode the offer code
     * @param inputQuantity the input quantity
     * @param param1 the param 1
     * @param param2 the param 2
     * @param param3 the param 3
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return The application price as amounts
     * @throws BusinessException the business exception
     */
    public Amounts getApplicationPrice(Seller seller, BillingAccount ba, TradingCurrency currency, TradingCountry buyersCountry, OneShotChargeTemplate chargeTemplate, Date subscriptionDate, OfferTemplate offerTemplate,
            BigDecimal inputQuantity, String param1, String param2, String param3, boolean ignoreNoTax) throws BusinessException {

        OneShotChargeInstance ci = new OneShotChargeInstance();
        ci.setCountry(buyersCountry);
        ci.setCurrency(currency);
        ci.setChargeTemplate(chargeTemplate);

        BigDecimal ratingQuantity = chargeTemplateService.evaluateRatingQuantity(chargeTemplate, inputQuantity);
        
        WalletOperation op = new WalletOperation(ci, inputQuantity, ratingQuantity, subscriptionDate, null, param1, param2, param3, null, null, null, null,null, null);
        op.setOfferTemplate(offerTemplate);
        op.setSeller(seller);

        try {
            ratingService.rateBareWalletOperation(op, null, null, buyersCountry.getId(), currency);

        } catch (RatingException e) {
            log.trace("Failed to rate a wallet operation {}: {}", op, e.getRejectionReason());
            throw e; // e.getBusinessException();

        } catch (BusinessException e) {
            log.error("Failed to rate a wallet operation {}: {}", op, e.getMessage(), e);
            throw e;
        }

        return new Amounts(op.getAmountWithoutTax(), op.getAmountWithTax(), op.getAmountTax(), op.getTax());
    }

    /**
     * Gets the first recurring price.
     *
     * @param ba the ba
     * @param chargeTemplate the charge template
     * @param subscriptionDate the subscription date
     * @param quantity the quantity
     * @param param1 the param 1
     * @param param2 the param 2
     * @param param3 the param 3
     * @param priceWithoutTax the price without tax
     * @return the first recurring price
     * @throws BusinessException the business exception
     */
    public BigDecimal getFirstRecurringPrice(BillingAccount ba, RecurringChargeTemplate chargeTemplate, Date subscriptionDate, BigDecimal quantity, String param1, String param2, String param3, boolean priceWithoutTax)
            throws BusinessException {

        RecurringChargeInstance chargeInstance = new RecurringChargeInstance(null, null, quantity, subscriptionDate, null, ba.getCustomerAccount().getCustomer().getSeller(), ba.getTradingCountry(),
            ba.getCustomerAccount().getTradingCurrency(), chargeTemplate);

        Date nextApplicationDate = walletOperationService.getRecurringPeriodEndDate(chargeInstance, chargeInstance.getSubscriptionDate());
        List<WalletOperation> ops;
        try {
            ops = recurringChargeInstanceService.applyRecurringCharge(chargeInstance, nextApplicationDate, true, true);

        } catch (RatingException e) {
            log.trace("Failed to rate a recurring charge {}: {}", chargeInstance, e.getRejectionReason());
            throw e; // e.getBusinessException();

        } catch (BusinessException e) {
            log.error("Failed to rate a recurring charge {}: {}", chargeInstance, e.getMessage(), e);
            throw e;
        }
        return ops.stream().filter(walletOperation -> walletOperation != null).map(walletOperation -> priceWithoutTax ? walletOperation.getAmountWithoutTax() : walletOperation.getAmountWithTax()).reduce(BigDecimal.ZERO,
            BigDecimal::add);

    }

    /**
     * Gets the activation service price.
     *
     * @param ba the ba
     * @param serviceTemplate the service template
     * @param subscriptionDate the subscription date
     * @param offerCode the offer code
     * @param quantity the quantity
     * @param param1 the param 1
     * @param param2 the param 2
     * @param param3 the param 3
     * @param priceWithoutTax the price without tax
     * @return the activation service price
     * @throws BusinessException the business exception
     */
    /*
     * Warning : this method does not handle calendars at service level
     */
    public BigDecimal getActivationServicePrice(Seller seller, BillingAccount ba, ServiceTemplate serviceTemplate, Date subscriptionDate, OfferTemplate offerTemplate, BigDecimal quantity, String param1, String param2,
            String param3, boolean priceWithoutTax) throws BusinessException {

        BigDecimal result = BigDecimal.ZERO;

        if (serviceTemplate.getServiceSubscriptionCharges() != null) {
            for (ServiceChargeTemplate<OneShotChargeTemplate> charge : serviceTemplate.getServiceSubscriptionCharges()) {
                Amounts applicationPrice = getApplicationPrice(seller, ba, charge.getChargeTemplate(), subscriptionDate, offerTemplate, quantity, param1, param2, param3);

                result = result.add(priceWithoutTax ? applicationPrice.getAmountWithoutTax() : applicationPrice.getAmountWithTax());
            }
        }

        if (serviceTemplate.getServiceRecurringCharges() != null) {
            for (ServiceChargeTemplate<RecurringChargeTemplate> charge : serviceTemplate.getServiceRecurringCharges()) {
                boolean isApplyInAdvance = (charge.getChargeTemplate().getApplyInAdvance() == null) ? false : charge.getChargeTemplate().getApplyInAdvance();
                if (!StringUtils.isBlank(charge.getChargeTemplate().getApplyInAdvanceEl())) {
                    isApplyInAdvance = recurringChargeTemplateService.matchExpression(charge.getChargeTemplate().getApplyInAdvanceEl(), null, serviceTemplate, charge.getChargeTemplate(), null);
                }
                if (isApplyInAdvance) {
                    result = result.add(getFirstRecurringPrice(ba, charge.getChargeTemplate(), subscriptionDate, quantity, param1, param2, param3, priceWithoutTax));
                }
            }
        }

        return result;
    }

}
