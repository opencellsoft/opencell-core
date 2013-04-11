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
import org.meveo.model.Auditable;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class ChargeApplicationRatingService {

    @Inject
    @MeveoJpa
    protected EntityManager em;

    @Inject
    protected Logger log;
    
    static HashMap<String,HashMap<String,List<PricePlanMatrix>>> allPricePlan;

	private static final String WILCARD = "*";
    private static final BigDecimal HUNDRED = new BigDecimal("100");

	
	public WalletOperation rateChargeApplication(String code, Subscription subscription,
            ChargeInstance chargeInstance,
            ApplicationTypeEnum applicationType, Date applicationDate, BigDecimal amountWithoutTax,
            BigDecimal amountWithTax, BigDecimal quantity, Long currencyId, Long taxId, BigDecimal taxPercent,
            BigDecimal discountPercent, Date nextApplicationDate,
            InvoiceSubCategory invoiceSubCategory, String criteria1, String criteria2,
            String criteria3, Date startdate, Date endDate,ChargeApplicationModeEnum mode){
		WalletOperation result = new WalletOperation();
		if (subscription != null) {
			result.setSubscriptionDate(subscription.getSubscriptionDate());
        }
		result.setOperationDate(applicationDate);
		result.setParameter1(criteria1);
		result.setParameter2(criteria2);
		result.setParameter3(criteria3);
		
		String providerCode = chargeInstance.getProvider().getCode();
		if(allPricePlan==null){
			loadPricePlan();
		}
		if(!allPricePlan.containsKey(providerCode)){
			throw new RuntimeException("no price plan for provider "+providerCode);
		}
		if(!allPricePlan.get(providerCode).containsKey(code)){
			throw new RuntimeException("no price plan for provider "+providerCode+" and charge code "+code);
		}
		PricePlanMatrix ratePrice=null;
		BigDecimal unitPriceWithoutTax = amountWithoutTax;
        BigDecimal unitPriceWithTax = null;

        boolean overriddenPrice = (unitPriceWithoutTax != null);
        if (overriddenPrice) {
        	unitPriceWithTax = amountWithTax;
        } else {
        	ratePrice=ratePrice(allPricePlan.get(providerCode).get(code),result,taxId,currencyId);
            if (ratePrice == null ||  ratePrice.getAmountWithoutTax()==null) {
            	throw new RuntimeException("invalid price plan for provider "+providerCode+" and charge code "+code);
            } else {
                log.info("found ratePrice:" + ratePrice.getId() + " priceHT=" + ratePrice.getAmountWithoutTax()
                        + " priceTTC=" + ratePrice.getAmountWithTax());
                unitPriceWithoutTax = ratePrice.getAmountWithoutTax();
                unitPriceWithTax = ratePrice.getAmountWithTax();
            }
        }
       
        BigDecimal priceWithoutTax = quantity.multiply(unitPriceWithoutTax);
        BigDecimal priceWithTax = null;
        BigDecimal amountTax = BigDecimal.ZERO;
        if (taxPercent != null) {
            amountTax = priceWithoutTax.multiply(taxPercent.divide(HUNDRED));
        }
        if(unitPriceWithTax==null){
        	priceWithTax = priceWithoutTax.add(amountTax);
        } else {
        	priceWithTax = quantity.multiply(unitPriceWithoutTax);
        }

        Provider provider = chargeInstance.getProvider();

        if (provider.getRounding() != null && provider.getRounding() > 0) {
        	priceWithoutTax = NumberUtils.round(priceWithoutTax, provider.getRounding());
        	priceWithTax = NumberUtils.round(priceWithTax, provider.getRounding());
        }
        
        result.setProvider(provider);
        result.setChargeInstance(chargeInstance);
        
        // FIXME: Too many requests to get the wallet and works only for postpaid
        result.setWallet(subscription.getUserAccount().getWallet());
        result.setCode(code);
        result.setOperationDate(applicationDate);
        result.setQuantity(quantity);
        result.setUnitAmountWithoutTax(unitPriceWithoutTax);
        result.setUnitAmountWithTax(unitPriceWithTax);
        result.setTaxPercent(taxPercent);
        result.setAmountWithoutTax(priceWithoutTax);
        result.setAmountWithTax(priceWithTax);
        result.setAmountTax(amountTax);
        result.setStartDate(startdate);
        result.setEndDate(endDate);
        result.setStatus(WalletOperationStatusEnum.OPEN);
		return result;
		
	}
	
	private PricePlanMatrix ratePrice(List<PricePlanMatrix> listPricePlan,WalletOperation bareOperation, Long taxId, Long currencyId) {
		// FIXME: the price plan properties could be null !
		for (PricePlanMatrix pricePlan : listPricePlan) {
			    boolean taxAreEqual = pricePlan.getTax().getId()==null || taxId==pricePlan.getTax().getId();
			    if(taxAreEqual){
			    	boolean currencyAreEqual = pricePlan.getTradingCurrency().getId()==null || currencyId==pricePlan.getTradingCurrency().getId();
				if(currencyAreEqual){
			    	boolean subscriptionDateInPricePlanPeriod = bareOperation.getSubscriptionDate() == null
				|| ((pricePlan.getStartSubscriptionDate() == null || bareOperation.getSubscriptionDate().after(pricePlan.getStartSubscriptionDate()) || bareOperation.getSubscriptionDate()
						.equals(pricePlan.getStartSubscriptionDate())) && (pricePlan.getEndSubscriptionDate() == null || bareOperation.getSubscriptionDate().before(pricePlan
								.getEndSubscriptionDate())));
				if(subscriptionDateInPricePlanPeriod){
					int subscriptionAge = 0;
					if (bareOperation.getSubscriptionDate()!=null && bareOperation.getOperationDate() != null) {
						//logger.info("subscriptionDate=" + bareOperation.getSubscriptionDate() + "->" + DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(), -1));
						subscriptionAge = DateUtils.monthsBetween(bareOperation.getOperationDate(), DateUtils.addDaysToDate(bareOperation.getSubscriptionDate(), -1));
					}
					log.debug("subscriptionAge=" + subscriptionAge);
					boolean subscriptionMinAgeOK = pricePlan.getMinSubscriptionAgeInMonth() == null || subscriptionAge >= pricePlan.getMinSubscriptionAgeInMonth();
					log.debug("subscriptionMinAgeOK(" + pricePlan.getMinSubscriptionAgeInMonth() + ")=" + subscriptionMinAgeOK);
					if(subscriptionMinAgeOK){
						boolean subscriptionMaxAgeOK = pricePlan.getMaxSubscriptionAgeInMonth() == null || subscriptionAge < pricePlan.getMaxSubscriptionAgeInMonth();
						log.debug("subscriptionMaxAgeOK(" + pricePlan.getMaxSubscriptionAgeInMonth() + ")=" + subscriptionMaxAgeOK);

						if(subscriptionMaxAgeOK){
						  boolean applicationDateInPricePlanPeriod = (pricePlan.getStartRatingDate() == null || bareOperation.getOperationDate().after(pricePlan.getStartRatingDate()) || bareOperation.getOperationDate()
								.equals(pricePlan.getStartRatingDate())) && (pricePlan.getEndRatingDate() == null || bareOperation.getOperationDate().before(pricePlan.getEndRatingDate()));
						  log.debug("applicationDateInPricePlanPeriod(" + pricePlan.getStartRatingDate() + " - " + pricePlan.getEndRatingDate() + ")="
								+ applicationDateInPricePlanPeriod);
						  if(applicationDateInPricePlanPeriod){
							boolean criteria1SameInPricePlan = WILCARD.equals(pricePlan.getCriteria1Value())
									|| (pricePlan.getCriteria1Value()!=null && pricePlan.getCriteria1Value().equals(bareOperation.getParameter1()))
								|| ((pricePlan.getCriteria1Value() == null || "".equals(pricePlan
													.getCriteria1Value())) && bareOperation.getParameter1() == null);
							log.debug("criteria1SameInPricePlan("
									+ pricePlan.getCriteria1Value() + ")="
									+ criteria1SameInPricePlan);
							if (criteria1SameInPricePlan) {
								boolean criteria2SameInPricePlan = WILCARD.equals(pricePlan.getCriteria2Value())
										|| (pricePlan.getCriteria2Value()!=null && pricePlan.getCriteria2Value().equals(bareOperation.getParameter2()))
									|| ((pricePlan.getCriteria2Value() == null || "".equals(pricePlan
														.getCriteria2Value())) && bareOperation.getParameter2() == null);
								log.debug("criteria2SameInPricePlan("
										+ pricePlan.getCriteria2Value() + ")="
										+ criteria2SameInPricePlan);
								if (criteria2SameInPricePlan) {
									boolean criteria3SameInPricePlan = WILCARD.equals(pricePlan.getCriteria3Value())
											|| (pricePlan.getCriteria3Value()!=null && pricePlan.getCriteria3Value().equals(bareOperation.getParameter3()))
									|| ((pricePlan.getCriteria3Value() == null || "".equals(pricePlan
															.getCriteria3Value())) && bareOperation.getParameter3() == null);
									log.debug("criteria3SameInPricePlan("
											+ pricePlan.getCriteria3Value()
											+ ")=" + criteria3SameInPricePlan);
									if (criteria3SameInPricePlan) {
										return pricePlan;
									}
								}
							}
						  }
						}
					}
				}
			    }
			    }
			}
		return null;
	}

	
    @SuppressWarnings("unchecked")
	protected void loadPricePlan() {
    	HashMap<String,HashMap<String,List<PricePlanMatrix>>> result = new HashMap<String,HashMap<String, List<PricePlanMatrix>>>();
        List<PricePlanMatrix> allPricePlans =  (List<PricePlanMatrix>) em.createQuery("from PricePlanMatrix").getResultList();
        if(allPricePlans!=null & allPricePlans.size()>0){
        	for(PricePlanMatrix pricePlan : allPricePlans){
            	if(!result.containsKey(pricePlan.getProvider().getCode())){
            		result.put(pricePlan.getProvider().getCode(), new HashMap<String,List<PricePlanMatrix>>());
            	}
            	HashMap<String,List<PricePlanMatrix>> providerPricePlans = result.get(pricePlan.getProvider().getCode());
            	if(!providerPricePlans.containsKey(pricePlan.getEventCode())){
            		providerPricePlans.put(pricePlan.getEventCode(), new ArrayList<PricePlanMatrix>());
             		log.debug("Added pricePlan for provider="+pricePlan.getProvider().getCode()+" chargeCode="+pricePlan.getEventCode());
            	}
            	providerPricePlans.get(pricePlan.getEventCode()).add(pricePlan);
        	}
        }
        allPricePlan=result;
    }
}
