package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.infinispan.manager.CacheContainer;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.cache.CounterInstanceCache;
import org.meveo.model.cache.CounterPeriodCache;
import org.meveo.model.cache.UsageChargeInstanceCache;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.util.MeveoJpa;
import org.slf4j.Logger;

@Stateless
@LocalBean
public class UsageRatingService {

    @Inject
    @MeveoJpa
    protected EntityManager em;

    @Inject
    protected Logger log;
    
   /* @Resource(lookup="java:jboss/infinispan/container/meveo")
    private CacheContainer meveoContainer;  */
    

    private org.infinispan.Cache<Long, List<UsageChargeInstanceCache>> chargeCache;
    private org.infinispan.Cache<Long, CounterInstanceCache> counterCache;
    
    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;
    
    @EJB
    private CounterInstanceService counterInstanceService;

    @EJB
    private RatingService ratingService;
    
    @EJB
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;
    
    @EJB
	private WalletOperationService walletOperationService;
    
    @PostConstruct
    public void start() {
     /* this.chargeCache = this.meveoContainer.getCache("usageCharge");
      this.counterCache = this.meveoContainer.getCache("counter");
      log.info("loading usage charge cache");
      @SuppressWarnings("unchecked")
	  List<UsageChargeInstance> usageChargeInstances = em.createQuery("From UsageChargeInstance u").getResultList();
      if(usageChargeInstances!=null){
    	  for(UsageChargeInstance usageChargeInstance:usageChargeInstances){
    	      	  UsageChargeInstanceCache.putInCache(usageChargeInstance,chargeCache,counterCache);
    	  }
      }*/
    }
    
    
    /**
     * This method use the price plan to rate an EDR knowing what charge must be used
     * 
     * @param edr
     * @param chargeInstance
     * @param provider
     * @param currencyId
     * @param taxId
     * @return
     */
    //TODO: change TaxId to country
    public WalletOperation rateEDRwithMatchingCharge(EDR edr, UsageChargeInstanceCache chargeCache, UsageChargeInstance chargeInstance, Provider provider){
    	WalletOperation walletOperation = new WalletOperation();
		walletOperation.setSubscriptionDate(null);
		walletOperation.setOperationDate(edr.getEventDate());
		walletOperation.setParameter1(edr.getParameter1());
		walletOperation.setParameter2(edr.getParameter2());
		walletOperation.setParameter3(edr.getParameter3());
		
        walletOperation.setProvider(provider);
        
        //FIXME: copy those info in chargeInstance instead of performing multiple queries
        InvoiceSubCategory invoiceSubCat = chargeInstance
				.getChargeTemplate().getInvoiceSubCategory();
        TradingCountry country=edr.getSubscription().getUserAccount().getBillingAccount()
				.getTradingCountry();
        Long countryId=country.getId();
		InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService
				.findInvoiceSubCategoryCountry(invoiceSubCat.getId(),countryId);
		TradingCurrency currency=edr.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency();
		Tax tax = invoiceSubcategoryCountry.getTax();
        walletOperation.setChargeInstance(chargeInstance); 
        Long sellerId= edr.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().getSeller().getId();
        
        //FIXME: get the wallet from the ServiceUsageChargeTemplate
        walletOperation.setWallet(edr.getSubscription().getUserAccount().getWallet());
        walletOperation.setCode(chargeInstance.getCode());
        walletOperation.setQuantity(edr.getQuantity());
        walletOperation.setTaxPercent(tax.getPercent());
        walletOperation.setStartDate(null);
        walletOperation.setEndDate(null);
        walletOperation.setStatus(WalletOperationStatusEnum.OPEN);
		ratingService.rateBareWalletOperation(walletOperation, null, null, countryId,currency,sellerId, provider);
		return walletOperation;
    }
    
    /**
     * This method first look if there is a counter and a 
     * @param edr
     * @param charge
     * @return
     */
    boolean fitInCounters(EDR edr,UsageChargeInstanceCache charge){
    	boolean result=false;
    	CounterInstanceCache counterInstanceCache= counterCache.get(charge.getCounter().getKey());
		CounterPeriodCache periodCache = null;
		BigDecimal countedValue = BigDecimal.ZERO;
		if(counterInstanceCache.getCounterPeriods()!=null){
			for(CounterPeriodCache itemPeriodCache:counterInstanceCache.getCounterPeriods()){
				if((itemPeriodCache.getStartDate().before(edr.getEventDate()) || itemPeriodCache.getStartDate().equals(edr.getEventDate())) 
						&& itemPeriodCache.getEndDate().after(edr.getEventDate())){
					periodCache=itemPeriodCache;
					break;
				}
			}
		}
		if(periodCache==null){
			CounterInstance counterInstance=counterInstanceService.findById(counterInstanceCache.getKey());
			CounterPeriod counterPeriod=counterInstanceService.createPeriod(counterInstance,edr.getEventDate());
			periodCache = CounterPeriodCache.getInstance(counterPeriod,counterInstance.getCounterTemplate());
		}
		countedValue = edr.getQuantity().multiply(charge.getUnityMultiplicator());
		if(charge.getUnityNbDecimal()>0){
				countedValue=countedValue.setScale(charge.getUnityNbDecimal(), RoundingMode.HALF_UP);
		}
		if(periodCache.getValue().compareTo(countedValue)<0){
    		result=true;
			periodCache.setValue(periodCache.getValue().subtract(countedValue));
				//TODO: check this is propagated on all server nodes
				//TODO :set lastupdate of counterInstanceCache so it get persisted in BDD
		} 
    	return result;
    }
    
    /**
     * this method evaluate the EDR against the charge and its counter
     * it returns true if the charge has been rated (either because it has no counter or because the counter can be decremented with the EDR content)
     * @param edr
     * @param charge
     * @return
     */
    public boolean rateEDRonChargeAndCounters(EDR edr,UsageChargeInstanceCache charge){
    	boolean result=false;
    	boolean continueRating=true;
    	if(charge.getCounter()!=null){
    		// if the charge is associated to a counter and we can decrement it then
    		// we rate the charge if not we simply try the next charge
    		continueRating=fitInCounters(edr, charge);
    	}
		if(continueRating){
			Provider provider=charge.getProvider();
			UsageChargeInstance chargeInstance =usageChargeInstanceService.findById(charge.getChargeInstanceId());
			WalletOperation walletOperation = rateEDRwithMatchingCharge(edr, charge,chargeInstance, provider);
			walletOperationService.create(walletOperation, null,provider);
			result=true;			
		}
		return result;
    }
    
    /**
     * Rate an EDR using counters if they apply 
     * @param edr
     */
    //TODO: this is only for postpaid wallets, for prepaid we dont need to check counters
    public void ratePostpaidUsage(EDR edr){
    	if(edr.getSubscription()==null){
    		edr.setStatus(EDRStatusEnum.REJECTED);
    		edr.setRejectReason("subscription null");
    	} else {
    		boolean edrIsRated = false;
    		if(chargeCache.containsKey(edr.getSubscription().getId())){
    			//TODO:order charges by priority and id
        		List<UsageChargeInstanceCache> charges = chargeCache.get(edr.getSubscription().getId());
    			for(UsageChargeInstanceCache charge: charges){
    				if(charge.getFilter1()==null || charge.getFilter1().equals(edr.getParameter1())) {
    					if(charge.getFilter2()==null || charge.getFilter1().equals(edr.getParameter1())) {
    						if(charge.getFilter3()==null || charge.getFilter1().equals(edr.getParameter1())) {
    							if(charge.getFilter4()==null || charge.getFilter1().equals(edr.getParameter1())) {
    								if(charge.getFilterExpression()!=null) {
    									//TODO: implement EL expression 
    									//javax.el.ELContext elContext = javax.faces.context.FacesContext.getCurrentInstance().getELContext();
    									//javax.el.ExpressionFactory expressionFactory = 
        		    				}	
    								//we found matching charge, if we rate it we exit the look
    								edrIsRated=rateEDRonChargeAndCounters(edr,charge);
    								if(edrIsRated){
    					        		edr.setStatus(EDRStatusEnum.RATED);
    									break;
    								}
    		    				}	
    	    				}	
        				}		
    				}
    			}
    			if(!edrIsRated){
            		edr.setStatus(EDRStatusEnum.REJECTED);
            		edr.setRejectReason("no matching charge");   
    			}
    		} else {
        		edr.setStatus(EDRStatusEnum.REJECTED);
        		edr.setRejectReason("subscription has no usage charge");    			
    		}
    	}
    }    
}
