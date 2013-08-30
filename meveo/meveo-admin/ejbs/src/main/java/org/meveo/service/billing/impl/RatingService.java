package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.commons.utils.DateUtils;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class RatingService {

    @Inject
    @MeveoJpa
    protected EntityManager em;

    @Inject
    protected Logger log;
    
    @Inject
    protected CatMessagesService catMessagesService;

    private static boolean isPricePlanDirty;
    private static HashMap<String, HashMap<String, List<PricePlanMatrix>>> allPricePlan;

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public static void setPricePlanDirty(){
    	isPricePlanDirty=true;
    }
    
    // used to rate a oneshot or recurring charge
    public WalletOperation rateChargeApplication(String code, Subscription subscription, ChargeInstance chargeInstance, ApplicationTypeEnum applicationType, Date applicationDate,
            BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, TradingCurrency tCurrency, Long countryId, BigDecimal taxPercent,
            BigDecimal discountPercent, Date nextApplicationDate, InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2, String criteria3, Date startdate,
            Date endDate, ChargeApplicationModeEnum mode) {
        WalletOperation result = new WalletOperation();
        if (chargeInstance instanceof RecurringChargeInstance) {
            result.setSubscriptionDate(((RecurringChargeInstance)chargeInstance).getServiceInstance()
					.getSubscriptionDate());
        }
        result.setOperationDate(applicationDate);
        result.setParameter1(criteria1);
        result.setParameter2(criteria2);
        result.setParameter3(criteria3);

        Provider provider = chargeInstance.getProvider();
        result.setProvider(provider);
        result.setChargeInstance(chargeInstance);

        result.setWallet(subscription.getUserAccount().getWallet());
        result.setCode(code);
      
    	String languageCode=subscription.getUserAccount().getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
    	CatMessages catMessage= catMessagesService.getCatMessages(chargeInstance.getClass().getSimpleName()+"_"+chargeInstance.getId(),languageCode);
        String chargeInstnceLabel =catMessage != null ?catMessage.getDescription():null;
        result.setDescription(chargeInstnceLabel!=null?chargeInstnceLabel:chargeInstance.getDescription());           
        result.setQuantity(quantity);
        result.setTaxPercent(taxPercent);
        result.setCurrency(tCurrency.getCurrency());
        result.setStartDate(startdate);
        result.setEndDate(endDate);
        result.setStatus(WalletOperationStatusEnum.OPEN);
        result.setSeller(subscription.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().getSeller());
        BigDecimal unitPriceWithoutTax = amountWithoutTax;
        BigDecimal unitPriceWithTax = null;
        if (unitPriceWithoutTax != null) {
            unitPriceWithTax = amountWithTax;
        }

        rateBareWalletOperation(result, unitPriceWithoutTax, unitPriceWithTax, countryId, tCurrency,  provider);
        return result;

    }

    // used to rate or rerate a bareWalletOperation
    public void rateBareWalletOperation(WalletOperation bareWalletOperation, BigDecimal unitPriceWithoutTax, BigDecimal unitPriceWithTax, Long countryId,
            TradingCurrency tcurrency,Provider provider) {

        PricePlanMatrix ratePrice = null;
        String providerCode = provider.getCode();

        if (unitPriceWithoutTax == null) {
            if (allPricePlan == null) {
                loadPricePlan();
            } else if(isPricePlanDirty){
            	reloadPricePlan();
            }
            if (!allPricePlan.containsKey(providerCode)) {
                throw new RuntimeException("no price plan for provider " + providerCode);
            }
            if (!allPricePlan.get(providerCode).containsKey(bareWalletOperation.getCode())) {
                throw new RuntimeException("no price plan for provider " + providerCode + " and charge code " + bareWalletOperation.getCode());
            }
            ratePrice = ratePrice(allPricePlan.get(providerCode).get(bareWalletOperation.getCode()), bareWalletOperation, countryId, tcurrency, bareWalletOperation.getSeller()!=null?bareWalletOperation.getSeller().getId():null);
            if (ratePrice == null || ratePrice.getAmountWithoutTax() == null) {
                throw new RuntimeException("invalid price plan for provider " + providerCode + " and charge code " + bareWalletOperation.getCode());
            } else {
                log.info("found ratePrice:" + ratePrice.getId() + " priceHT=" + ratePrice.getAmountWithoutTax() + " priceTTC=" + ratePrice.getAmountWithTax());
                unitPriceWithoutTax = ratePrice.getAmountWithoutTax();
                unitPriceWithTax = ratePrice.getAmountWithTax();
            }
        }

        BigDecimal priceWithoutTax = bareWalletOperation.getQuantity().multiply(unitPriceWithoutTax);
        BigDecimal priceWithTax = null;
        BigDecimal amountTax = BigDecimal.ZERO;
        if (bareWalletOperation.getTaxPercent() != null) {
            amountTax = priceWithoutTax.multiply(bareWalletOperation.getTaxPercent().divide(HUNDRED));
        }
        if (unitPriceWithTax == null || unitPriceWithTax.intValue()==0) {
            priceWithTax = priceWithoutTax.add(amountTax);
        } else {
            priceWithTax = bareWalletOperation.getQuantity().multiply(unitPriceWithTax);
        }

        if (provider.getRounding() != null && provider.getRounding() > 0) {
            priceWithoutTax = NumberUtils.round(priceWithoutTax, provider.getRounding());
            priceWithTax = NumberUtils.round(priceWithTax, provider.getRounding());
        }

        bareWalletOperation.setUnitAmountWithoutTax(unitPriceWithoutTax);
        bareWalletOperation.setUnitAmountWithTax(unitPriceWithTax);
        bareWalletOperation.setTaxPercent(bareWalletOperation.getTaxPercent());
        bareWalletOperation.setAmountWithoutTax(priceWithoutTax);
        bareWalletOperation.setAmountWithTax(priceWithTax);
        bareWalletOperation.setAmountTax(amountTax);

    }

    private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan, WalletOperation bareOperation, Long countryId, TradingCurrency tcurrency, Long sellerId) {
        // FIXME: the price plan properties could be null !

    	log.info("rate "+bareOperation);
        for (PricePlanMatrix pricePlan : listPricePlan) {
            boolean sellerAreEqual = pricePlan.getSeller() == null || pricePlan.getSeller().getId().equals(sellerId);
            if (!sellerAreEqual) {
            	log.info("The seller of the customer "+sellerId+" is not the same as pricePlan seller "+pricePlan.getSeller().getId()+" ("+pricePlan.getSeller().getCode()+")");
                continue;
            }
            
            boolean countryAreEqual = pricePlan.getTradingCountry() == null || pricePlan.getTradingCountry().getId().equals(countryId);
            if (!countryAreEqual) {
            	log.info("The country of the billing account "+countryId+" is not the same as pricePlan country"+pricePlan.getTradingCountry().getId()+" ("+pricePlan.getTradingCountry().getCountry().getCountryCode()+")");
                continue;
            }
            boolean currencyAreEqual = pricePlan.getTradingCurrency() == null || (tcurrency != null && tcurrency.getId().equals(pricePlan.getTradingCurrency().getId()));
            if (!currencyAreEqual) {
            	log.info("The currency of the customer account "+(tcurrency != null ? tcurrency.getCurrencyCode():"null")+" is not the same as pricePlan currency"+pricePlan.getTradingCurrency().getId()+" ("+pricePlan.getTradingCurrency().getCurrencyCode()+")");
                continue;
            }
            boolean subscriptionDateInPricePlanPeriod = bareOperation.getSubscriptionDate() == null
                    || ((pricePlan.getStartSubscriptionDate() == null || bareOperation.getSubscriptionDate().after(pricePlan.getStartSubscriptionDate()) || bareOperation
                        .getSubscriptionDate().equals(pricePlan.getStartSubscriptionDate())) && (pricePlan.getEndSubscriptionDate() == null || bareOperation.getSubscriptionDate()
                        .before(pricePlan.getEndSubscriptionDate())));
            if (!subscriptionDateInPricePlanPeriod) {
            	log.info("The subscription date "+bareOperation.getSubscriptionDate()+"is not in the priceplan subscription range");
                continue;
            }

            int subscriptionAge = 0;
            if (bareOperation.getSubscriptionDate() != null && bareOperation.getOperationDate() != null) {
                // logger.info("subscriptionDate=" + bareOperation.getSubscriptionDate() + "->" + DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(), -1));
                subscriptionAge = DateUtils.monthsBetween(bareOperation.getOperationDate(), DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(), -1));
            }
            //log.info("subscriptionAge=" + subscriptionAge);
            boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
            //log.info("subscriptionMinAgeOK(" + pricePlan.getMinSubscriptionAgeInMonth() + ")=" + subscriptionMinAgeOK);
            if (!subscriptionMinAgeOK) {
            	log.info("The subscription age "+subscriptionAge+"is less than the priceplan subscription age min :"+pricePlan.getMinSubscriptionAgeInMonth());
                continue;
            }
            boolean subscriptionMaxAgeOK = pricePlan.getMaxSubscriptionAgeInMonth() == null  || pricePlan.getMaxSubscriptionAgeInMonth() == 0  || subscriptionAge < pricePlan.getMaxSubscriptionAgeInMonth();
            log.info("subscriptionMaxAgeOK(" + pricePlan.getMaxSubscriptionAgeInMonth() + ")=" + subscriptionMaxAgeOK);
            if (!subscriptionMaxAgeOK) {
            	log.info("The subscription age "+subscriptionAge+"is greater than the priceplan subscription age max :"+pricePlan.getMaxSubscriptionAgeInMonth());
                continue;
            }

            boolean applicationDateInPricePlanPeriod = (pricePlan.getStartRatingDate() == null || bareOperation.getOperationDate().after(pricePlan.getStartRatingDate()) || bareOperation
                .getOperationDate().equals(pricePlan.getStartRatingDate()))
                    && (pricePlan.getEndRatingDate() == null || bareOperation.getOperationDate().before(pricePlan.getEndRatingDate()));
            log.error("applicationDateInPricePlanPeriod(" + pricePlan.getStartRatingDate() + " - " + pricePlan.getEndRatingDate() + ")=" + applicationDateInPricePlanPeriod);
            if (!applicationDateInPricePlanPeriod) {
            	log.info("The application date "+bareOperation.getOperationDate()+"is not in the priceplan application range");
                continue;
            }
        	boolean criteria1SameInPricePlan = pricePlan.getCriteria1Value() == null || pricePlan.getCriteria1Value().equals(bareOperation.getParameter1());
            //log.info("criteria1SameInPricePlan(" + pricePlan.getCriteria1Value() + ")=" + criteria1SameInPricePlan);
            if (!criteria1SameInPricePlan) {
            	log.info("The operation param1 "+bareOperation.getParameter1()+" is not compatible with price plan criteria 1: "+pricePlan.getCriteria1Value());
                continue;
            }
        	boolean criteria2SameInPricePlan = pricePlan.getCriteria2Value()==null || pricePlan.getCriteria2Value().equals(bareOperation.getParameter2());
            //log.info("criteria2SameInPricePlan(" + pricePlan.getCriteria2Value() + ")=" + criteria2SameInPricePlan);
            if (!criteria2SameInPricePlan) {
            	log.info("The operation param2 "+bareOperation.getParameter2()+" is not compatible with price plan criteria 2: "+pricePlan.getCriteria2Value());
                continue;
            }
        	boolean criteria3SameInPricePlan = pricePlan.getCriteria3Value()==null || pricePlan.getCriteria3Value().equals(bareOperation.getParameter3());
            //log.info("criteria3SameInPricePlan(" + pricePlan.getCriteria3Value() + ")=" + criteria3SameInPricePlan);
            if (criteria3SameInPricePlan) {
            	log.info("criteria3SameInPricePlan");
                return pricePlan;
            }
        	log.info("The operation param3 "+bareOperation.getParameter3()+" is not compatible with price plan criteria 3: "+pricePlan.getCriteria3Value());
        }
        return null;
    }
    

    //synchronized to avoid different threads to reload the priceplan concurrently
    protected synchronized void reloadPricePlan() {
    		if(isPricePlanDirty){
    			log.info("Reload priceplan");
        		loadPricePlan();
        		isPricePlanDirty=false;
        	}
    }

    // FIXME : call this method when priceplan is edited (or more precisely add a button to reload the priceplan)
    @SuppressWarnings("unchecked")
    protected void loadPricePlan() {
        HashMap<String, HashMap<String, List<PricePlanMatrix>>> result = new HashMap<String, HashMap<String, List<PricePlanMatrix>>>();
        List<PricePlanMatrix> allPricePlans = (List<PricePlanMatrix>) em.createQuery("from PricePlanMatrix where disabled=false order by priority ASC").getResultList();
        if (allPricePlans != null & allPricePlans.size() > 0) {
            for (PricePlanMatrix pricePlan : allPricePlans) {
                if (!result.containsKey(pricePlan.getProvider().getCode())) {
                    result.put(pricePlan.getProvider().getCode(), new HashMap<String, List<PricePlanMatrix>>());
                }
                HashMap<String, List<PricePlanMatrix>> providerPricePlans = result.get(pricePlan.getProvider().getCode());
                if (!providerPricePlans.containsKey(pricePlan.getEventCode())) {
                	if(pricePlan.getCriteria1Value()!=null && pricePlan.getCriteria1Value().length()==0){
                		pricePlan.setCriteria1Value(null);
                	}
                	if(pricePlan.getCriteria2Value()!=null && pricePlan.getCriteria2Value().length()==0){
                		pricePlan.setCriteria2Value(null);
                	}
                	if(pricePlan.getCriteria3Value()!=null && pricePlan.getCriteria3Value().length()==0){
                		pricePlan.setCriteria3Value(null);
                	}
                    providerPricePlans.put(pricePlan.getEventCode(), new ArrayList<PricePlanMatrix>());
                    log.error("Added pricePlan for provider=" + pricePlan.getProvider().getCode() + " priceplan=" + pricePlan);
                }
                providerPricePlans.get(pricePlan.getEventCode()).add(pricePlan);
            }
        }
        allPricePlan = result;
    }
}
