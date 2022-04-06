package org.meveo.service.script;


import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.script.Script;
import org.meveo.service.tax.TaxMappingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;

public class RatingScript extends Script {


    public static final String REC_CDR_UUID   = "CDR_UUID";
    public static final String REC_PAIRING_ID = "PAIRING_ID";

    private TaxMappingService taxMappingService = (TaxMappingService) getServiceInterface(TaxMappingService.class.getSimpleName());
    private RatingService ratingService = (RatingService) getServiceInterface(RatingService.class.getSimpleName());
    private AccessService accessService = (AccessService) getServiceInterface(AccessService.class.getSimpleName());
    private Provider appProvider;
    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        log.info("Execute executeContext:{}", executeContext);
         appProvider = (Provider) executeContext.get(Script.CONTEXT_APP_PROVIDER);
        WalletOperation wo = (WalletOperation) executeContext.get(Script.CONTEXT_ENTITY);
        if (wo == null) {
            throw new BusinessException("Wallet operation is null!");
        }
        log.info("walletOperation {}", wo);
        OfferTemplate offer = wo.getOfferTemplate();
        log.info("offer {}", offer);
        recalculateWoRated(wo,appProvider);
    }

    private void recalculateWoRated(WalletOperation wo,Provider appProvider) {

        ChargeInstance chargeInstance = wo.getChargeInstance();
        Long buyerCountryId = chargeInstance.getCountry()!=null?chargeInstance.getCountry().getId() : null;
        TradingCurrency buyerCurrency = chargeInstance.getCurrency();
        List<PricePlanMatrix> pricePlans = ratingService.getActivePricePlansByChargeCode(chargeInstance.getCode());
        TaxMappingService.TaxInfo taxInfo = taxMappingService.determineTax(chargeInstance, wo.getOperationDate(),null);
        // setting tax info
        wo.setTaxClass(taxInfo.taxClass);
        wo.setTax(taxInfo.tax);
        wo.setTaxPercent(taxInfo.tax.getPercent());
        Optional<PricePlanMatrix> pricePlan = pricePlans.stream().filter((pp)-> checkPricePlanConditions(pp,wo,buyerCountryId,buyerCurrency)).findFirst();
        calculateAmount(pricePlan.get(),appProvider,wo,buyerCountryId,buyerCurrency);

        log.info("WalletOperation", wo);
    }

    /**
     * the lambda expression used to execute the extract process
     * @param appProvider
     * @param wo
     * @return Consumer : the lambda expression used to execute the extract process
     */
        private void calculateAmount (PricePlanMatrix pricePlan, Provider appProvider,WalletOperation wo,Long buyerCountryId,TradingCurrency buyerCurrency){

            Integer rounding = appProvider.getRounding();
            RoundingModeEnum roundingMode = appProvider.getRoundingMode();
                BigDecimal unitPrice = appProvider.isEntreprise()?pricePlan.getAmountWithoutTax():pricePlan.getAmountWithTax();
                BigDecimal amount = wo.getQuantity().multiply(unitPrice);
                BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(unitPrice, unitPrice, wo.getTaxPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amount, amount, wo.getTaxPercent(), appProvider.isEntreprise(), rounding, roundingMode.getRoundingMode());
                wo.setUnitAmountWithoutTax(unitAmounts[0]);
                wo.setUnitAmountWithTax(unitAmounts[1]);
                wo.setUnitAmountTax(unitAmounts[2]);
                wo.setAmountWithoutTax(amounts[0]);
                wo.setAmountWithTax(amounts[1]);
                wo.setAmountTax(amounts[2]);
    }

    private boolean checkPricePlanConditions(PricePlanMatrix pricePlan,WalletOperation bareOperation,Long buyerCountryId,TradingCurrency buyerCurrency){

        if(!checkPricePlanSellerAndCountry(pricePlan,bareOperation,buyerCountryId,buyerCurrency)) {
            return false;
        }
        if(!checkPricePlanDateAndAge(pricePlan,bareOperation)){
            return false;
        }

        if(!checkPricePlanCriterias(pricePlan,bareOperation)){
            return false;
        }

            return true;
    }

    /**
     * Check Seller and country pricePlan conditions
     * @param pricePlan
     * @param bareOperation
     * @param buyerCountryId
     * @param buyerCurrency
     * @return
     */
    private boolean checkPricePlanSellerAndCountry(PricePlanMatrix pricePlan,WalletOperation bareOperation,Long buyerCountryId,TradingCurrency buyerCurrency){
        Seller seller = pricePlan.getSeller();
        boolean sellerAreEqual = seller == null || seller.getId().equals(bareOperation.getSeller().getId());
        if (!sellerAreEqual) {
            log.trace("The seller of the customer {} is not the same as pricePlan seller {}", bareOperation.getSeller().getId(), seller.getId());
            return false;
        }

        TradingCountry tradingCountry = pricePlan.getTradingCountry();
        boolean countryAreEqual = tradingCountry == null || tradingCountry.getId().equals(buyerCountryId);
        if (!countryAreEqual) {
            log.trace("The countryId={} of the billing account is not the same as pricePlan with countryId={}", buyerCountryId, tradingCountry.getId());
            return false;
        }

        TradingCurrency tradingCurrency = pricePlan.getTradingCurrency();
        boolean currencyAreEqual = tradingCurrency == null || (buyerCurrency != null && buyerCurrency.getId().equals(tradingCurrency.getId()));
        if (!currencyAreEqual) {
            log.trace("The currency of the customer account {} is not the same as pricePlan currency {}", (buyerCurrency != null ? buyerCurrency.getCurrencyCode() : "null"), tradingCurrency.getId());
            return false;
        }
        return true;
    }
    /**
     * checking PricePlan Criterias
     * @param pricePlan
     * @param bareOperation
     * @return
     */
    private boolean checkPricePlanCriterias(PricePlanMatrix pricePlan,WalletOperation bareOperation){
        String criteria1Value = pricePlan.getCriteria1Value();
        boolean criteria1SameInPricePlan = criteria1Value == null || criteria1Value.equals(bareOperation.getParameter1());
        if (!criteria1SameInPricePlan) {
            log.trace("The operation param1 {} is not compatible with price plan criteria 1: {}", bareOperation.getParameter1(), criteria1Value);
            return false;
        }
        String criteria2Value = pricePlan.getCriteria2Value();
        String parameter2 = bareOperation.getParameter2();
        boolean criteria2SameInPricePlan = criteria2Value == null || criteria2Value.equals(parameter2);
        if (!criteria2SameInPricePlan) {
            log.trace("The operation param2 {} is not compatible with price plan criteria 2: {}", parameter2, criteria2Value);
            return false;
        }
        String criteria3Value = pricePlan.getCriteria3Value();
        boolean criteria3SameInPricePlan = criteria3Value == null || criteria3Value.equals(bareOperation.getParameter3());
        if (!criteria3SameInPricePlan) {
            log.trace("The operation param3 {} is not compatible with price plan criteria 3: {}", bareOperation.getParameter3(), criteria3Value);
            return false;
        }
        return true;
    }

    /**
     * Checking priclePlan date and age
     * @param pricePlan
     * @param bareOperation
     * @return
     */
    private boolean checkPricePlanDateAndAge(PricePlanMatrix pricePlan,WalletOperation bareOperation){
        Date subscriptionDate = bareOperation.getSubscriptionDate();
        Date startSubscriptionDate = pricePlan.getStartSubscriptionDate();
        Date endSubscriptionDate = pricePlan.getEndSubscriptionDate();
        boolean subscriptionDateInPricePlanPeriod = subscriptionDate == null || ((startSubscriptionDate == null || subscriptionDate.after(startSubscriptionDate) || subscriptionDate.equals(startSubscriptionDate))
                && (endSubscriptionDate == null || subscriptionDate.before(endSubscriptionDate)));
        if (!subscriptionDateInPricePlanPeriod) {
            log.trace("The subscription date {} is not in the priceplan subscription range {} - {}", subscriptionDate, startSubscriptionDate, endSubscriptionDate);
            return false;
        }
        int subscriptionAge = 0;
        Date operationDate = bareOperation.getOperationDate();
        if (subscriptionDate != null && operationDate != null) {
            subscriptionAge = DateUtils.monthsBetween(operationDate, DateUtils.addDaysToDate(subscriptionDate, -1));
        }

        boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
        if (!subscriptionMinAgeOK) {
            log.trace("The subscription age={} is less than the priceplan subscription age min={}", subscriptionAge, pricePlan.getMinSubscriptionAgeInMonth());
            return false;
        }
        Long maxSubscriptionAgeInMonth = pricePlan.getMaxSubscriptionAgeInMonth();
        boolean subscriptionMaxAgeOK = maxSubscriptionAgeInMonth == null || maxSubscriptionAgeInMonth == 0 || subscriptionAge < maxSubscriptionAgeInMonth;
        if (!subscriptionMaxAgeOK) {
            log.trace("The subscription age {} is greater than the priceplan subscription age max {}", subscriptionAge, maxSubscriptionAgeInMonth);
            return false;
        }
        Date startRatingDate = pricePlan.getStartRatingDate();
        Date endRatingDate = pricePlan.getEndRatingDate();
        boolean applicationDateInPricePlanPeriod = (startRatingDate == null || operationDate.after(startRatingDate) || operationDate.equals(startRatingDate))
                && (endRatingDate == null || operationDate.before(endRatingDate));
        if (!applicationDateInPricePlanPeriod) {
            log.trace("The application date {} is not in the priceplan application range {} - {}", operationDate, startRatingDate, endRatingDate);
            return false;
        }
        return true;
    }

}
