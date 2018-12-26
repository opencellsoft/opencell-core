package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
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

    /** The invoice sub category country service. */
    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    /** The charge application rating service. */
    @Inject
    private RatingService chargeApplicationRatingService;

    /** The wallet operation service. */
    @Inject
    private WalletOperationService walletOperationService;

    /** The invoice sub category service. */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    /** The recurring charge template service. */
    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    /**
     * Gets the application price.
     *
     * @param ba the ba
     * @param chargeTemplate the charge template
     * @param subscriptionDate the subscription date
     * @param offerCode the offer code
     * @param quantity the quantity
     * @param param1 the param 1
     * @param param2 the param 2
     * @param param3 the param 3
     * @param priceWithoutTax the price without tax
     * @return the application price
     * @throws BusinessException the business exception
     */    
    public BigDecimal getApplicationPrice(Seller seller, BillingAccount ba, OneShotChargeTemplate chargeTemplate, Date subscriptionDate, String offerCode, BigDecimal quantity, String param1,
            String param2, String param3, boolean priceWithoutTax) throws BusinessException {

        TradingCurrency currency = ba.getCustomerAccount().getTradingCurrency();
        if (currency == null) {
            throw new IncorrectChargeTemplateException("no currency exists for customerAccount id=" + ba.getCustomerAccount().getId());
        }

        TradingCountry tradingCountry = ba.getTradingCountry();
        if (tradingCountry == null) {
            throw new IncorrectChargeTemplateException("no country exists for billingAccount id=" + ba.getId());
        }

        if (seller == null){
            seller = ba.getCustomerAccount().getCustomer().getSeller();
        }

        return getApplicationPrice(seller, ba, currency, tradingCountry, chargeTemplate, subscriptionDate, offerCode, quantity, param1, param2, param3, priceWithoutTax, false);
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
     * @param priceWithoutTax the price without tax
     * @param ignoreNoTax Should exception be thrown if no tax was matched
     * @return the application price
     * @throws BusinessException the business exception
     */
    public BigDecimal getApplicationPrice(Seller seller, BillingAccount ba, TradingCurrency currency, TradingCountry buyersCountry,
            OneShotChargeTemplate chargeTemplate, Date subscriptionDate, String offerCode, BigDecimal inputQuantity, String param1, String param2, String param3,
            boolean priceWithoutTax, boolean ignoreNoTax) throws BusinessException {

        InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();

        Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, ba, subscriptionDate, ignoreNoTax);

        WalletOperation op = new WalletOperation();

        op.setOperationDate(subscriptionDate);
        op.setParameter1(param1);
        op.setParameter2(param2);
        op.setParameter3(param3);

        OneShotChargeInstance ci = new OneShotChargeInstance();
        ci.setCountry(buyersCountry);
        ci.setCurrency(currency);
        op.setChargeInstance(ci);
        // we do not need charging of this operation so we set its wallet to null
        op.setWallet(null);
        op.setCode(chargeTemplate.getCode());

        op.setDescription("");
        op.setInputQuantity(inputQuantity);
        op.setQuantity(NumberUtils.getInChargeUnit(inputQuantity, chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(), chargeTemplate.getRoundingMode()));
        op.setTax(tax);
        op.setTaxPercent(tax == null ? BigDecimal.ZERO : tax.getPercent());
        op.setCurrency(currency.getCurrency());
        op.setStartDate(null);
        op.setEndDate(null);
        op.setOfferCode(offerCode);
        op.setStatus(WalletOperationStatusEnum.OPEN);
        op.setSeller(seller);

        chargeApplicationRatingService.rateBareWalletOperation(op, null, null, buyersCountry.getId(), currency);

        return priceWithoutTax ? op.getAmountWithoutTax() : op.getAmountWithTax();
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
    public BigDecimal getFirstRecurringPrice(BillingAccount ba, RecurringChargeTemplate chargeTemplate, Date subscriptionDate, BigDecimal quantity, String param1, String param2,
            String param3, boolean priceWithoutTax) throws BusinessException {

        RecurringChargeInstance chargeInstance = new RecurringChargeInstance(null, null, quantity, subscriptionDate, null, ba.getCustomerAccount().getCustomer().getSeller(),
            ba.getTradingCountry(), ba.getCustomerAccount().getTradingCurrency(), chargeTemplate);

        Date nextApplicationDate = walletOperationService.initChargeDateAndGetNextChargeDate(chargeInstance);
        WalletOperation op = walletOperationService.applyFirstRecurringCharge(chargeInstance, nextApplicationDate, true);

        BigDecimal firstRecurringPrice = BigDecimal.ZERO;
        if(op != null) {
            firstRecurringPrice = priceWithoutTax ? op.getAmountWithoutTax() : op.getAmountWithTax();
        }
        return firstRecurringPrice;
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
    public BigDecimal getActivationServicePrice(Seller seller, BillingAccount ba, ServiceTemplate serviceTemplate, Date subscriptionDate, String offerCode, BigDecimal quantity, String param1,
            String param2, String param3, boolean priceWithoutTax) throws BusinessException {

        BigDecimal result = BigDecimal.ZERO;

        if (serviceTemplate.getServiceSubscriptionCharges() != null) {
            for (ServiceChargeTemplate<OneShotChargeTemplate> charge : serviceTemplate.getServiceSubscriptionCharges()) {
                result = result.add(getApplicationPrice(seller, ba, charge.getChargeTemplate(), subscriptionDate, offerCode, quantity, param1, param2, param3, priceWithoutTax));
            }
        }

        if (serviceTemplate.getServiceRecurringCharges() != null) {
            for (ServiceChargeTemplate<RecurringChargeTemplate> charge : serviceTemplate.getServiceRecurringCharges()) {
                boolean isApplyInAdvance = (charge.getChargeTemplate().getApplyInAdvance() == null) ? false : charge.getChargeTemplate().getApplyInAdvance();
                if (!StringUtils.isBlank(charge.getChargeTemplate().getApplyInAdvanceEl())) {
                    isApplyInAdvance = recurringChargeTemplateService.matchExpression(charge.getChargeTemplate().getApplyInAdvanceEl(), null, serviceTemplate,
                        charge.getChargeTemplate());
                }
                if (isApplyInAdvance) {
                    result = result.add(getFirstRecurringPrice(ba, charge.getChargeTemplate(), subscriptionDate, quantity, param1, param2, param3, priceWithoutTax));
                }
            }
        }

        return result;
    }

}
